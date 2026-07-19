package dev.firecontroller.oshaa.block.entity;

import dev.firecontroller.oshaa.api.OAEnergyDraw;
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
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.BigInteger;

public abstract class EnergyConsumerBlockEntityBase extends BlockEntity implements MenuProvider, Container {

    protected static final String TAG_ENERGY = "Energy";
    protected static final String TAG_CONSUMER_INVENTORY = "ConsumerInventory";

    protected final ItemStackHandler consumerInventory;
    protected final OAEnergyStorage energyStorage;
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

    public void consumeEnergy() {
        if (!(this.level instanceof ServerLevel)) return;
        if (this.operating) {
            if (this.energyOperatingDraw.draw() != 0) this.energyStorage.consumeEnergy(this.energyOperatingDraw.draw(), false);
        } else {
            if (this.energyStandbyDraw.draw() != 0) this.energyStorage.consumeEnergy(this.energyStandbyDraw.draw(), false);
        }
    }

    protected abstract boolean isConsumerItemValid(int slot, @NotNull ItemStack stack);

    protected void onConsumerInventoryChanged(int slot) {
        recalculateEnergy();
        ensureEnergyDrawTick();
    }

    protected void onEnergyChanged() {
        setChanged();
        if (this.level instanceof ServerLevel) {
            ensureEnergyDrawTick();
        }
    }

    protected void recalculateEnergy() {
        recalculateEnergyProfile();
        recalculateEnergyDraw();
        setChanged();
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
                totalContinuous += energyConsumerProfile.continuous();
                totalStandby += energyConsumerProfile.standby();
                totalStartup += energyConsumerProfile.startup();
            }
        }
        this.energyProfile = new OAEnergyProfile(totalContinuous, totalStandby, totalStartup);
    }

    private void recalculateEnergyDraw() {
        this.energyOperatingDraw = calculateEnergyDraw(this.energyProfile.continuous());
        this.energyStandbyDraw = calculateEnergyDraw(this.energyProfile.standby());
    }

    /**
     * Checks if the block has a scheduled tick and,
     * if not, assigns one.
     */
    private void ensureEnergyDrawTick() {
        if (!(this.level instanceof ServerLevel serverLevel)) return;
        if (this.energyStorage.getEnergyStored() <= 0) return;
        BlockState state = this.level.getBlockState(this.worldPosition);
        if (!serverLevel.getBlockTicks().hasScheduledTick(this.worldPosition, state.getBlock())) {
            if (this.operating) {
                if (this.energyOperatingDraw.interval() != 0) serverLevel.scheduleTick(this.worldPosition, state.getBlock(), this.energyOperatingDraw.interval());
            } else {
                if (this.energyStandbyDraw.interval() != 0) serverLevel.scheduleTick(this.worldPosition, state.getBlock(), this.energyStandbyDraw.interval());
            }
        }
    }

    public IItemHandler getConsumerInventory() {
        return this.consumerInventory;
    }

    public IEnergyStorage getEnergyStorage() {
        return this.energyStorage;
    }

    public void setOperating(boolean operating) {
        if (this.operating == operating) return;
        this.operating = operating;
        setChanged();
        ensureEnergyDrawTick();
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

    @Override
    public void onLoad() {
        super.onLoad();
        recalculateEnergy();
        ensureEnergyDrawTick();
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt(TAG_ENERGY, this.energyStorage.getEnergyStored());
        tag.put(TAG_CONSUMER_INVENTORY, this.consumerInventory.serializeNBT(registries));
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.energyStorage.setEnergyStored(tag.getInt(TAG_ENERGY));
        this.consumerInventory.deserializeNBT(registries, tag.getCompound(TAG_CONSUMER_INVENTORY));
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
        BigDecimal decimal = BigDecimal.valueOf(fePerTick).stripTrailingZeros();
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
