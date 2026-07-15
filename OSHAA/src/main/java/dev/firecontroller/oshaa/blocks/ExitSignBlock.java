package dev.firecontroller.oshaa.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jetbrains.annotations.NotNull;

public class ExitSignBlock extends FaceAttachedHorizontalDirectionalBlock {

    public static final MapCodec<ExitSignBlock> CODEC = simpleCodec(ExitSignBlock::new);

    /**
     * Constructs a new {@link ExitSignBlock}.
     * @param properties Block properties to be assigned during construction.
     */
    public ExitSignBlock(Properties properties) {
        super(properties.noOcclusion());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACE, FACING);
    }

    @Override
    protected @NotNull MapCodec<? extends FaceAttachedHorizontalDirectionalBlock> codec() {
        return CODEC;
    }

}
