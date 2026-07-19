package dev.firecontroller.oshaa.block.entity;

import dev.firecontroller.oshaa.api.OAEnergyDraw;
import dev.firecontroller.oshaa.api.OAEnergyScheduler;
import dev.firecontroller.oshaa.api.OAEnergyProfile;
import dev.firecontroller.oshaa.api.OAEnergyStorage;
import dev.firecontroller.oshaa.api.OAIEnergyConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

public abstract class EnergyConsumerBlockEntityBase extends BlockEntity implements MenuProvider, Container, OAEnergyScheduler.ScheduledEnergyDraw {

    protected static final String TAG_ENERGY = "Energy";
    protected static final String TAG_CONSUMER_INVENTORY = "ConsumerInventory";
    protected static final String TAG_OPERATING = "Operating";
    protected final ItemStackHandler consumerInventory;
    protected OAEnergyStorage energyStorage;
    private boolean operating;

    private OAEnergyProfile energyProfile;
    private OAEnergyDraw energyOperatingDraw;
    private OAEnergyDraw energyStandbyDraw;

    /**
     * Constructs a new {@link EnergyConsumerBlockEntityBase}.
     */
    public EnergyConsumerBlockEntityBase(BlockEntityType<?> type, BlockPos pos, BlockState blockState, int numSlots, int capacity, int maxReceive, int maxExtract, int maxConsume, int energy) {
        super(type, pos, blockState);

        this.consumerInventory = new ItemStackHandler(numSlots) {
            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return EnergyConsumerBlockEntityBase.this.isConsumerItemValid(slot, stack);
            }

            @Override
            protected void onContentsChanged(int slot) {
                EnergyConsumerBlockEntityBase.this.onConsumerInventoryChanged(slot);
            }

            @Override
            public int getSlotLimit(int slot) {
                return 1;
            }
        };
        this.energyStorage = new OAEnergyStorage(capacity, maxReceive, maxExtract, maxConsume, energy, () -> onEnergyChanged());
        this.operating = false;

        recalculateEnergy();
    }

    @Override
    public int handleScheduledEnergyDraw() {
        OAEnergyDraw draw = getEnergyDraw();
        if (draw.draw() <= 0 || draw.interval() <= 0) return 0;
        this.energyStorage.consumeEnergy(draw.draw(), false);
        if (this.energyStorage.getEnergyStored() <= 0) return 0;
        OAEnergyDraw nextDraw = getEnergyDraw();
        return nextDraw.draw() > 0 && nextDraw.interval() > 0 ? nextDraw.interval() : 0;
    }

    protected void ensureEnergyDrawScheduled() {
        if (!(this.level instanceof ServerLevel serverLevel)) return;
        OAEnergyDraw draw = getEnergyDraw();
        if (this.energyStorage.getEnergyStored() <= 0 || draw.interval() <= 0 || draw.draw() <= 0) {
            OAEnergyScheduler.cancel(serverLevel, this.worldPosition);
            return;
        }
        OAEnergyScheduler.ensureScheduled(serverLevel, this.worldPosition, draw.interval());
    }

    protected void restartEnergyDrawSchedule() {
        if (!(this.level instanceof ServerLevel serverLevel)) return;
        OAEnergyDraw draw = getEnergyDraw();
        if (this.energyStorage.getEnergyStored() <= 0 || draw.interval() <= 0 || draw.draw() <= 0) {
            OAEnergyScheduler.cancel(serverLevel, this.worldPosition);
            return;
        }
        OAEnergyScheduler.restart(serverLevel, this.worldPosition, draw.interval());
    }

    protected abstract boolean isConsumerItemValid(int slot, @NotNull ItemStack stack);

    protected void onConsumerInventoryChanged(int slot) {
        recalculateEnergy();
    }

    protected void onEnergyChanged() {
        setChanged();
        ensureEnergyDrawScheduled();
    }

    protected void recalculateEnergy() {
        recalculateEnergyProfile();
        recalculateEnergyDraw();
        setChanged();
        restartEnergyDrawSchedule();
    }

    private void recalculateEnergyProfile() {
        double totalContinuous = 0.0;
        double totalStandby = 0.0;
        int totalStartup = 0;
        for (int slot = 0; slot < this.consumerInventory.getSlots(); slot++) {
            ItemStack stack = this.consumerInventory.getStackInSlot(slot);
            if (stack.isEmpty()) continue;
            if (stack.getItem() instanceof OAIEnergyConsumer energyConsumer) {
                OAEnergyProfile energyConsumerProfile = energyConsumer.getEnergyProfile();
                totalContinuous += energyConsumerProfile.continuous() * this.getConsumptionModifier();
                totalStandby += energyConsumerProfile.standby() * this.getConsumptionModifier();
                totalStartup += (int) Math.round(energyConsumerProfile.startup() * this.getConsumptionModifier());
            }
        }
        this.energyProfile = new OAEnergyProfile(totalContinuous, totalStandby, totalStartup);
    }

    private void recalculateEnergyDraw() {
        this.energyOperatingDraw = calculateEnergyDraw(this.energyProfile.continuous());
        this.energyStandbyDraw = calculateEnergyDraw(this.energyProfile.standby());
    }

    public int getRequiredStorageForTime(int seconds) {
        if (seconds < 0) throw new IllegalArgumentException("Seconds cannot be negative.");
        OAEnergyDraw draw = this.energyOperatingDraw;
        if (draw.draw() == 0 || draw.interval() == 0) {
            return 0;
        }
        long ticks = seconds * 20L;
        long required = Math.ceilDiv(ticks * draw.draw(), draw.interval());
        return Math.toIntExact(required);
    }

    public IEnergyStorage getEnergyStorage() {
        return this.energyStorage;
    }

    /**
     * Sets the entity as 'operating,' and returns true if successful.
     * If the entity has a startup cost, it will be drawn. If not
     * enough energy is available to start, this method will return false.
     */
    public boolean setOperating(boolean operating) {
        if (this.operating == operating) return true;
        int startup = operating ? this.energyProfile.startup() : 0;
        if (startup > 0) {
            int amount = this.energyStorage.consumeEnergy(startup, true);
            if (amount < startup) return false;
            this.energyStorage.consumeEnergy(startup, false);
        }
        this.operating = operating;
        setChanged();
        restartEnergyDrawSchedule();
        return true;
    }

    public boolean isOperating() {
        return this.operating;
    }

    public OAEnergyProfile getEnergyProfile() {
        return this.energyProfile;
    }

    public OAEnergyDraw getEnergyOperatingDraw() {
        return this.energyOperatingDraw;
    }

    public OAEnergyDraw getEnergyStandbyDraw() {
        return this.energyStandbyDraw;
    }

    public OAEnergyDraw getEnergyDraw() {
        return this.operating ? this.energyOperatingDraw : this.energyStandbyDraw;
    }

    public double getConsumptionModifier() {
        return 1.0;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        recalculateEnergy();
    }

    @Override
    public void setRemoved() {
        if (this.level instanceof ServerLevel serverLevel) {
            OAEnergyScheduler.cancel(serverLevel, this.worldPosition);
        }
        super.setRemoved();
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt(TAG_ENERGY, this.energyStorage.getEnergyStored());
        tag.put(TAG_CONSUMER_INVENTORY, this.consumerInventory.serializeNBT(registries));
        tag.putBoolean(TAG_OPERATING, this.operating);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains(TAG_OPERATING)) {
            this.operating = tag.getBoolean(TAG_OPERATING);
        }
        this.consumerInventory.deserializeNBT(registries, tag.getCompound(TAG_CONSUMER_INVENTORY));
        this.energyStorage.setEnergyStored(tag.getInt(TAG_ENERGY));
    }

    @Override
    public int getContainerSize() {
        return consumerInventory.getSlots();
    }

    @Override
    public boolean isEmpty() {
        for (int i = 0; i < this.consumerInventory.getSlots(); i++) {
            if (!this.consumerInventory.getStackInSlot(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public @NotNull ItemStack getItem(int slot) {
        return this.consumerInventory.getStackInSlot(slot);
    }

    @Override
    public @NotNull ItemStack removeItem(int slot, int amount) {
        return this.consumerInventory.extractItem(slot, amount, false);
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int slot) {
        ItemStack stack = this.consumerInventory.getStackInSlot(slot);
        this.consumerInventory.setStackInSlot(slot, ItemStack.EMPTY);
        return stack;
    }

    @Override
    public void setItem(int slot, @NotNull ItemStack stack) {
        this.consumerInventory.setStackInSlot(slot, stack);
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        for (int i = 0; i < this.consumerInventory.getSlots(); i++) {
            this.consumerInventory.setStackInSlot(i, ItemStack.EMPTY);
        }
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return this.consumerInventory.isItemValid(slot, stack);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    public static OAEnergyDraw calculateEnergyDraw(double fePerTick) {
        if (!Double.isFinite(fePerTick) || fePerTick < 0.0) throw new IllegalArgumentException("Energy draw must be finite and non-negative.");
        if (fePerTick == 0.0) return new OAEnergyDraw(0, 0);
        BigDecimal decimal = BigDecimal.valueOf(fePerTick).setScale(4, RoundingMode.HALF_UP).stripTrailingZeros();
        BigInteger numerator = decimal.unscaledValue();
        int scale = decimal.scale();
        BigInteger denominator;
        if (scale >= 0) {
            denominator = BigInteger.TEN.pow(scale);
        } else {
            numerator = numerator.multiply(BigInteger.TEN.pow(-scale));
            denominator = BigInteger.ONE;
        }
        BigInteger gcd = numerator.gcd(denominator);
        numerator = numerator.divide(gcd);
        denominator = denominator.divide(gcd);
        return new OAEnergyDraw(denominator.intValueExact(), numerator.intValueExact());
    }

}
