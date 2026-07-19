package dev.firecontroller.oshaa.api;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Schedules energy draws without ticking every individual consumer.
 *
 * <p>The scheduler uses two 32-slot timing wheels. The first wheel has
 * one-tick resolution, while the second groups ticks into 32-tick windows.
 * Together they provide constant-time scheduling for delays below 1,024
 * ticks. Longer delays wait in an overflow heap until they enter that
 * window.</p>
 */
public final class OAEnergyScheduler {

    private static final int WHEEL_BITS = 5;
    private static final int WHEEL_SIZE = 1 << WHEEL_BITS;
    private static final int WHEEL_MASK = WHEEL_SIZE - 1;
    private static final long WHEEL_HORIZON = 1L << (WHEEL_BITS * 2);

    private static final Map<ServerLevel, OAEnergyScheduler> LEVEL_SCHEDULERS = new IdentityHashMap<>();
    private static boolean eventHandlersRegistered;

    private final ArrayDeque<ScheduledDraw>[] nearWheel = createWheel();
    private final ArrayDeque<ScheduledDraw>[] farWheel = createWheel();
    private final PriorityQueue<ScheduledDraw> overflow = new PriorityQueue<>(Comparator.comparingLong(ScheduledDraw::getDueTick));
    private final Long2ObjectOpenHashMap<ScheduledDraw> scheduledDraws = new Long2ObjectOpenHashMap<>();

    private OAEnergyScheduler() {
    }

    /**
     * Registers the scheduler with NeoForge's game event bus.
     */
    public static void registerEventHandlers() {
        if (eventHandlersRegistered) return;
        eventHandlersRegistered = true;
        NeoForge.EVENT_BUS.addListener(OAEnergyScheduler::onLevelTick);
        NeoForge.EVENT_BUS.addListener(OAEnergyScheduler::onLevelUnload);
    }

    /**
     * Schedules a consumer if it does not already have a pending draw.
     */
    public static void ensureScheduled(ServerLevel level, BlockPos pos, int delay) {
        validateDelay(delay);
        long now = level.getGameTime();
        LEVEL_SCHEDULERS
                .computeIfAbsent(level, ignored -> new OAEnergyScheduler())
                .ensureScheduled(pos.asLong(), getDueTick(now, delay), now);
    }

    /**
     * Replaces any pending draw with a fresh delay.
     */
    public static void restart(ServerLevel level, BlockPos pos, int delay) {
        validateDelay(delay);
        long now = level.getGameTime();
        LEVEL_SCHEDULERS
                .computeIfAbsent(level, ignored -> new OAEnergyScheduler())
                .restart(pos.asLong(), getDueTick(now, delay), now);
    }

    /**
     * Cancels the pending draw for a consumer, if one exists.
     */
    public static void cancel(ServerLevel level, BlockPos pos) {
        OAEnergyScheduler scheduler = LEVEL_SCHEDULERS.get(level);
        if (scheduler != null) {
            scheduler.scheduledDraws.remove(pos.asLong());
            if (scheduler.scheduledDraws.isEmpty()) {
                LEVEL_SCHEDULERS.remove(level, scheduler);
            }
        }
    }

    private static void onLevelTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        OAEnergyScheduler scheduler = LEVEL_SCHEDULERS.get(level);
        if (scheduler != null) {
            scheduler.tick(level);
            if (scheduler.scheduledDraws.isEmpty()) {
                LEVEL_SCHEDULERS.remove(level, scheduler);
            }
        }
    }

    private static void onLevelUnload(LevelEvent.Unload event) {
        if (event.getLevel() instanceof ServerLevel level) {
            LEVEL_SCHEDULERS.remove(level);
        }
    }

    private void ensureScheduled(long packedPos, long dueTick, long now) {
        if (this.scheduledDraws.get(packedPos) == null) {
            schedule(packedPos, dueTick, now);
        }
    }

    private void restart(long packedPos, long dueTick, long now) {
        schedule(packedPos, dueTick, now);
    }

    private void schedule(long packedPos, long dueTick, long now) {
        ScheduledDraw draw = new ScheduledDraw(BlockPos.of(packedPos), dueTick);
        this.scheduledDraws.put(packedPos, draw);
        enqueue(draw, now);
    }

    private void tick(ServerLevel level) {
        long now = level.getGameTime();
        promoteOverflow(now);

        if ((now & WHEEL_MASK) == 0L) {
            cascadeFarWheel(now);
        }

        dispatchDueDraws(level, now);
    }

    private void promoteOverflow(long now) {
        while (!this.overflow.isEmpty()) {
            ScheduledDraw draw = this.overflow.peek();
            if (!isCurrent(draw)) {
                this.overflow.poll();
                continue;
            }
            if (draw.getDueTick() - now >= WHEEL_HORIZON) return;
            this.overflow.poll();
            enqueue(draw, now);
        }
    }

    private void cascadeFarWheel(long now) {
        int index = (int) (now >>> WHEEL_BITS) & WHEEL_MASK;
        ArrayDeque<ScheduledDraw> bucket = this.farWheel[index];
        ScheduledDraw draw;
        while ((draw = bucket.pollFirst()) != null) {
            if (isCurrent(draw)) {
                enqueue(draw, now);
            }
        }
    }

    private void dispatchDueDraws(ServerLevel level, long now) {
        int index = (int) now & WHEEL_MASK;
        ArrayDeque<ScheduledDraw> bucket = this.nearWheel[index];
        ScheduledDraw draw;
        while ((draw = bucket.pollFirst()) != null) {
            if (!isCurrent(draw)) continue;
            if (draw.getDueTick() > now) {
                enqueue(draw, now);
                continue;
            }

            if (!level.isLoaded(draw.getPos())) {
                remove(draw);
                continue;
            }
            BlockEntity blockEntity = level.getBlockEntity(draw.getPos());
            if (blockEntity instanceof ScheduledEnergyDraw scheduledEnergyDraw) {
                int nextDelay = scheduledEnergyDraw.handleScheduledEnergyDraw();
                if (!isCurrent(draw)) continue;
                if (nextDelay > 0) {
                    draw.setDueTick(getDueTick(now, nextDelay));
                    enqueue(draw, now);
                } else {
                    remove(draw);
                }
            } else {
                remove(draw);
            }
        }
    }

    private void enqueue(ScheduledDraw draw, long now) {
        long remaining = draw.getDueTick() - now;
        if (remaining <= 0L) {
            this.nearWheel[(int) now & WHEEL_MASK].addLast(draw);
        } else if (remaining < WHEEL_SIZE) {
            this.nearWheel[(int) draw.getDueTick() & WHEEL_MASK].addLast(draw);
        } else if (remaining < WHEEL_HORIZON) {
            this.farWheel[(int) (draw.getDueTick() >>> WHEEL_BITS) & WHEEL_MASK].addLast(draw);
        } else {
            this.overflow.add(draw);
        }
    }

    private boolean isCurrent(ScheduledDraw draw) {
        return this.scheduledDraws.get(draw.getPackedPos()) == draw;
    }

    private void remove(ScheduledDraw draw) {
        if (isCurrent(draw)) {
            this.scheduledDraws.remove(draw.getPackedPos());
        }
    }

    private static long getDueTick(long now, int delay) {
        return Long.MAX_VALUE - now < delay ? Long.MAX_VALUE : now + delay;
    }

    private static void validateDelay(int delay) {
        if (delay <= 0) {
            throw new IllegalArgumentException("Energy draw delay must be positive.");
        }
    }

    @SuppressWarnings("unchecked")
    private static ArrayDeque<ScheduledDraw>[] createWheel() {
        ArrayDeque<ScheduledDraw>[] wheel = (ArrayDeque<ScheduledDraw>[]) new ArrayDeque<?>[WHEEL_SIZE];
        for (int index = 0; index < wheel.length; index++) {
            wheel[index] = new ArrayDeque<>();
        }
        return wheel;
    }

    private static final class ScheduledDraw {
        private final BlockPos pos;
        private final long packedPos;
        private long dueTick;

        private ScheduledDraw(BlockPos pos, long dueTick) {
            this.pos = pos;
            this.packedPos = pos.asLong();
            this.dueTick = dueTick;
        }

        private BlockPos getPos() {
            return this.pos;
        }

        private long getPackedPos() {
            return this.packedPos;
        }

        private long getDueTick() {
            return this.dueTick;
        }

        private void setDueTick(long dueTick) {
            this.dueTick = dueTick;
        }
    }

    /**
     * Implemented by block entities which can receive scheduled energy draws.
     */
    public interface ScheduledEnergyDraw {
        /**
         * Performs a due draw and returns the delay before the next draw.
         * A non-positive result stops scheduling.
         */
        int handleScheduledEnergyDraw();
    }

}
