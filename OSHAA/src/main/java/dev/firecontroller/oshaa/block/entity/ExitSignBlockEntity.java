package dev.firecontroller.oshaa.block.entity;

import dev.firecontroller.oshaa.OABlockEntities;
import dev.firecontroller.oshaa.OATags;
import dev.firecontroller.oshaa.block.ExitSignBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public final class ExitSignBlockEntity extends EnergyConsumerBlockEntityBase {

    public static final int DEFAULT_COLOR = DyeColor.RED.getTextureDiffuseColor() & 0x00FFFFFF;

    private static final String TAG_COLOR = "Color";
    private static final int SURVIVAL_TIME = 75; // OSHA 90 minutes maps roughly to 1m 15s in-game time

    private int color;

    /**
     * Constructs a new {@link EnergyConsumerBlockEntityBase}.
     */
    public ExitSignBlockEntity(BlockPos pos, BlockState blockState) {
//        super(OABlockEntities.EXIT_SIGN.get(), pos, blockState, 2, 0, 5, 0, 1, 0);
        // TODO: temp 9 slots
        super(OABlockEntities.EXIT_SIGN.get(), pos, blockState, 9, 0, Integer.MAX_VALUE, 0, Integer.MAX_VALUE, 0);
        this.color = DEFAULT_COLOR;
        setOperating(true);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (level instanceof ServerLevel serverLevel) {
            updateEnergyStorage();
            updateLitState(serverLevel);
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt(TAG_COLOR, color);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        color = tag.contains(TAG_COLOR) ? tag.getInt(TAG_COLOR) & 0x00FFFFFF : DEFAULT_COLOR;
    }

    @Override
    protected void applyImplicitComponents(@NotNull DataComponentInput input) {
        super.applyImplicitComponents(input);
        DyedItemColor dyedColor = input.get(DataComponents.DYED_COLOR);
        color = dyedColor != null ? dyedColor.rgb() & 0x00FFFFFF : DEFAULT_COLOR;
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        if (color != DEFAULT_COLOR) {
            components.set(DataComponents.DYED_COLOR, new DyedItemColor(color, false));
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(@NotNull HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public void onDataPacket(@NotNull Connection net, @NotNull ClientboundBlockEntityDataPacket pkt, @NotNull HolderLookup.Provider lookupProvider) {
        super.onDataPacket(net, pkt, lookupProvider);
        requestModelDataUpdate();
        if (level != null) {
            BlockState state = getBlockState();
            level.sendBlockUpdated(worldPosition, state, state, ExitSignBlock.UPDATE_CLIENTS);
        }
    }

    @Override
    protected boolean isConsumerItemValid(int slot, @NotNull ItemStack stack) {
        return stack.is(OATags.Items.LIGHT_BULB);
    }

    @Override
    protected void onConsumerInventoryChanged(int slot) {
        super.onConsumerInventoryChanged(slot);
        updateEnergyStorage();
    }

    @Override
    protected void onEnergyChanged() {
        super.onEnergyChanged();
        if (!(level instanceof ServerLevel serverLevel)) return;
        updateLitState(serverLevel);
    }

    @Override
    public double getConsumptionModifier() {
        return 0.4; /* 40% brightness, light level 6/15 */
    }

    /**
     * Checks against the current energy level and
     * updates the block's LIT state accordingly.
     * @param serverLevel The server level to perform the update on.
     */
    private void updateLitState(ServerLevel serverLevel) {
        BlockState state = getBlockState();
        boolean shouldBeLit = energyStorage.getEnergyStored() > 0;
        for (int i = 0; i < 2; i++) { // minimum 2 bulbs to be lit
            ItemStack stack = consumerInventory.getStackInSlot(i);
            if (stack.isEmpty()) {
                // Missing a bulb? Can't be lit :(
                shouldBeLit = false;
                break;
            }
        }
        if (state.getValue(ExitSignBlock.LIT) != shouldBeLit) {
            serverLevel.setBlockAndUpdate(worldPosition, state.setValue(ExitSignBlock.LIT, shouldBeLit));
        }
    }

    /**
     * Recalculates the expected energy storage to meet minimum survival time.
     */
    private void updateEnergyStorage() {
        if (!(level instanceof ServerLevel)) return;
        this.energyStorage.setCapacity(getRequiredStorageForTime(SURVIVAL_TIME));
    }

    public int getColor() {
        return color;
    }

    public boolean setColor(int color) {
        int normalized =  color & 0x00FFFFFF;
        if (this.color == normalized) return false;
        this.color = normalized;
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), ExitSignBlock.UPDATE_CLIENTS);
        }
        return true;
    }

    @Override
    public @NotNull Component getDisplayName() {
        // TODO: localize!
        return Component.literal("TESTING: EXIT SIGN INVENTORY");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, @NotNull Inventory playerInventory, @NotNull Player player) {
        // TODO: custom menu!
        return new ChestMenu(
            MenuType.GENERIC_9x1,
            containerId,
            playerInventory,
            this,
            1
        );
    }

}
