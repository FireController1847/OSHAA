package dev.firecontroller.oshaa.block;

import com.mojang.serialization.MapCodec;
import dev.firecontroller.oshaa.OABlockEntities;
import dev.firecontroller.oshaa.OAUtil;
import dev.firecontroller.oshaa.block.entity.ExitSignBlockEntity;
import dev.firecontroller.oshaa.item.SafetyBinderItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class ExitSignBlock extends FaceAttachedHorizontalDirectionalBlock implements EntityBlock {

    public static final MapCodec<ExitSignBlock> CODEC = simpleCodec(ExitSignBlock::new);

    private static final VoxelShape SHAPE_CEILING_BASE = Shapes.or(
        Block.box(1, 7, 7, 15, 14, 9),
        Block.box(5, 14, 7.5, 11, 15, 8.5),
        Block.box(4, 15, 6, 12, 16, 10)
    );
    private static final VoxelShape SHAPE_WALL_BASE = Shapes.or(
        Block.box(2, 5, 7, 16, 12, 9),
        Block.box(1, 5.5, 7.5, 2, 11.5, 8.5),
        Block.box(0, 4.5, 6, 1, 12.5, 10)
    );
    private static final VoxelShape SHAPE_FLOOR_BASE = Shapes.or(
        Block.box(1, 2, 7, 15, 9, 9),
        Block.box(5, 1, 7.5, 11, 2, 8.5),
        Block.box(4, 0, 6, 12, 1, 10)
    );
    private static final Map<Direction, VoxelShape> SHAPE_CEILING_MAP = OAUtil.createHorizontalShapes(SHAPE_CEILING_BASE, Direction.NORTH);
    private static final Map<Direction, VoxelShape> SHAPE_WALL_MAP = OAUtil.createHorizontalShapes(SHAPE_WALL_BASE, Direction.EAST);
    private static final Map<Direction, VoxelShape> SHAPE_FLOOR_MAP = OAUtil.createHorizontalShapes(SHAPE_FLOOR_BASE, Direction.NORTH);

    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public static final BooleanProperty LEFT_ARROW = BooleanProperty.create("left_arrow");
    public static final BooleanProperty RIGHT_ARROW = BooleanProperty.create("right_arrow");

    /**
     * Constructs a new {@link ExitSignBlock}.
     * @param properties Block properties to be assigned during construction.
     */
    public ExitSignBlock(Properties properties) {
        super(properties
                .noOcclusion()
                .lightLevel(state -> state.getValue(LIT) ? 6 : 0)
        );
        registerDefaultState(
            this.stateDefinition.any()
                .setValue(FACE, AttachFace.FLOOR)
                .setValue(FACING, Direction.NORTH)
                .setValue(LIT, false)
                .setValue(LEFT_ARROW, false)
                .setValue(RIGHT_ARROW, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACE, FACING, LIT, LEFT_ARROW, RIGHT_ARROW);
    }

    @Override
    protected @NotNull MapCodec<? extends FaceAttachedHorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    protected @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return getShape(state);
    }

    @Override
    protected @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return getShape(state);
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(@NotNull ItemStack stack, @NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hitResult) {
        if (stack.getItem() instanceof DyeItem dyeItem) {
            if (!(level.getBlockEntity(pos) instanceof ExitSignBlockEntity exitSignBlockEntity)) {
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }
            if (level.isClientSide) return ItemInteractionResult.SUCCESS;

            int color = dyeItem.getDyeColor().getTextureDiffuseColor() & 0x00FFFFFF;
            if (exitSignBlockEntity.setColor(color)) {
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
            }

            return ItemInteractionResult.SUCCESS;
        } else if (stack.getItem() instanceof SafetyBinderItem safetyBinderItem) {
            if (level.isClientSide) return ItemInteractionResult.SUCCESS;

            BlockState nextState;
            if (player.isShiftKeyDown()) {
                nextState = state.cycle (LIT);
            } else {
                boolean left = state.getValue(LEFT_ARROW);
                boolean right = state.getValue(RIGHT_ARROW);
                int current = (left ? 1 : 0) | (right ? 2 : 0);
                int next = (current + 1) & 3;
                nextState = state
                        .setValue(LEFT_ARROW, (next & 1) != 0)
                        .setValue(RIGHT_ARROW, (next & 2) != 0);
            }

            level.setBlockAndUpdate(pos, nextState);
            return ItemInteractionResult.SUCCESS;
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public @NotNull ItemStack getCloneItemStack(@NotNull BlockState state, @NotNull HitResult target, @NotNull LevelReader level, @NotNull BlockPos pos, @NotNull Player player) {
        ItemStack stack = super.getCloneItemStack(state, target, level, pos, player);
        if (level.getBlockEntity(pos) instanceof ExitSignBlockEntity exitSignBlockEntity) {
            int color = exitSignBlockEntity.getColor();
            if (color != ExitSignBlockEntity.DEFAULT_COLOR) {
                stack.set(DataComponents.DYED_COLOR, new DyedItemColor(color, false));
            }
        }
        return stack;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new ExitSignBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> blockEntityType) {
        if (level.isClientSide) return null;
        if (blockEntityType != OABlockEntities.EXIT_SIGN.get()) return null;
        return (tickerLevel, tickerPos, tickerState, blockEntity) -> ExitSignBlockEntity.tick(tickerLevel, tickerPos, tickerState, (ExitSignBlockEntity) blockEntity);
    }

    private static VoxelShape getShape(BlockState state) {
        Direction facing = state.getValue(FACING);
        return switch (state.getValue(FACE)) {
            case CEILING -> SHAPE_CEILING_MAP.get(facing);
            case WALL -> SHAPE_WALL_MAP.get(facing);
            case FLOOR -> SHAPE_FLOOR_MAP.get(facing);
        };
    }

}
