package dev.firecontroller.oshaa.block.entity;

import dev.firecontroller.oshaa.OABlockEntities;
import dev.firecontroller.oshaa.api.OAEnergyStorage;
import dev.firecontroller.oshaa.block.ExitSignBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExitSignBlockEntity extends BlockEntity {

    public static final int DEFAULT_COLOR = DyeColor.RED.getTextureDiffuseColor() & 0x00FFFFFF;

    private static final String TAG_ENERGY = "Energy";
    private static final String TAG_COLOR = "Color";

    private final OAEnergyStorage energyStorage;

    private byte counter;
    private int color;

    /**
     * Constructs a new {@link ExitSignBlockEntity}.
     * @param pos The position of the block entity.
     * @param blockState The current state of the entity's block.
     */
    public ExitSignBlockEntity(BlockPos pos, BlockState blockState) {
        super(OABlockEntities.EXIT_SIGN.get(), pos, blockState);

        energyStorage = new OAEnergyStorage(12, 5, 0, 1, 0);

        counter = 0;
        color = DEFAULT_COLOR;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt(TAG_ENERGY, energyStorage.getEnergyStored());
        tag.putInt(TAG_COLOR, color);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        energyStorage.setEnergyStored(tag.getInt(TAG_ENERGY));
        color = tag.contains(TAG_COLOR) ? tag.getInt(TAG_COLOR) & 0x00FFFFFF : DEFAULT_COLOR;
    }

    @Override
    protected void applyImplicitComponents(@NotNull DataComponentInput input) {
        super.applyImplicitComponents(input);
        DyedItemColor dyedColor = input.get(DataComponents.DYED_COLOR);
        color = dyedColor != null ? dyedColor.rgb() & 0x00FFFFFF : DEFAULT_COLOR;
    }

    @Override
    protected void collectImplicitComponents(@NotNull DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        if (color != DEFAULT_COLOR) {
            components.set(DataComponents.DYED_COLOR, new DyedItemColor(color, false));
        }
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
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

    public IEnergyStorage getEnergyStorage() {
        return energyStorage;
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

    public static void tick(Level level, BlockPos pos, BlockState state, ExitSignBlockEntity blockEntity) {
        blockEntity.counter++;
        if (blockEntity.counter == 127) {
            blockEntity.counter = 0;
        } else {
            return;
        }
        boolean lit = state.getValue(ExitSignBlock.LIT);
        int consumed = blockEntity.energyStorage.consumeEnergy(1, false);
        if (consumed > 0 && !lit) {
            level.setBlockAndUpdate(pos, state.setValue(ExitSignBlock.LIT, true));
        } else if (consumed == 0 && lit) {
            level.setBlockAndUpdate(pos, state.setValue(ExitSignBlock.LIT, false));
        }
    }

}
