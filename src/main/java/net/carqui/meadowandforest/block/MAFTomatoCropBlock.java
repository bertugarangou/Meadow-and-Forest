package net.carqui.meadowandforest.block;

import net.carqui.meadowandforest.item.MAFItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.CommonHooks;
import org.jspecify.annotations.NonNull;

public class MAFTomatoCropBlock extends CropBlock {

    public static final BooleanProperty TRELLISED = BooleanProperty.create("trellised");
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    private static final int REGROWTH_AGE = 4;
    private static final VoxelShape TRELLIS_TOP_SHAPE = Block.column(16.0, 0.0, 16.0);

    public MAFTomatoCropBlock(Properties properties) {
        super(properties);
        registerDefaultState(this.defaultBlockState().setValue(TRELLISED, false).setValue(HALF, DoubleBlockHalf.LOWER));
    }

    @Override
    protected @NonNull ItemLike getBaseSeedId() {
        return MAFItems.TOMATO_SEEDS.get();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NonNull Builder<Block, @NonNull BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(TRELLISED, HALF);
    }

    @Override
    protected @NonNull VoxelShape getShape(BlockState state, @NonNull BlockGetter level, @NonNull BlockPos pos, @NonNull CollisionContext context) {
        if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
            return TRELLIS_TOP_SHAPE;
        }
        return super.getShape(state, level, pos, context);
    }

    @Override
    protected boolean canSurvive(BlockState state, @NonNull LevelReader level, @NonNull BlockPos pos) {
        if (state.getValue(HALF) != DoubleBlockHalf.UPPER) {
            return super.canSurvive(state, level, pos);
        }
        BlockState belowState = level.getBlockState(pos.below());
        if (state.getBlock() != this) return super.canSurvive(state, level, pos);
        return belowState.is(this) && belowState.getValue(HALF) == DoubleBlockHalf.LOWER;
    }

    @Override
    protected @NonNull BlockState updateShape(BlockState state, @NonNull LevelReader level, @NonNull ScheduledTickAccess ticks, @NonNull BlockPos pos, Direction directionToNeighbour, @NonNull BlockPos neighbourPos, @NonNull BlockState neighbourState, @NonNull RandomSource random) {
        DoubleBlockHalf half = state.getValue(HALF);
        if (directionToNeighbour.getAxis() != Direction.Axis.Y
                || half == DoubleBlockHalf.LOWER != (directionToNeighbour == Direction.UP)
                || neighbourState.is(this) && neighbourState.getValue(HALF) != half) {
            return half == DoubleBlockHalf.LOWER && directionToNeighbour == Direction.DOWN && !state.canSurvive(level, pos)
                    ? Blocks.AIR.defaultBlockState()
                    : super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
        } else {
            return Blocks.AIR.defaultBlockState();
        }
    }

    @Override
    protected @NonNull InteractionResult useItemOn(@NonNull ItemStack stack, @NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos, @NonNull Player player, @NonNull InteractionHand hand, @NonNull BlockHitResult hitResult) {
        if (state.getValue(HALF) == DoubleBlockHalf.LOWER && !state.getValue(TRELLISED) && stack.is(MAFItems.CANE.get()) && getAge(state) <= 1) {
            BlockPos abovePos = pos.above();
            if (pos.getY() >= level.getMaxY() || !level.getBlockState(abovePos).canBeReplaced()) {
                return InteractionResult.PASS;
            }
            if (!level.isClientSide()) {
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                level.setBlock(pos, state.setValue(TRELLISED, true), UPDATE_CLIENTS);
                level.setBlock(abovePos, state.setValue(TRELLISED, true).setValue(HALF, DoubleBlockHalf.UPPER), UPDATE_CLIENTS);
            }
            return InteractionResult.SUCCESS;
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    protected @NonNull InteractionResult useWithoutItem(BlockState state, @NonNull Level level, @NonNull BlockPos pos, @NonNull Player player, @NonNull BlockHitResult hitResult) {
        if (state.getValue(HALF) == DoubleBlockHalf.LOWER && state.getValue(TRELLISED) && isMaxAge(state)) {
            if (!level.isClientSide()) {
                Block.popResource(level, pos, new ItemStack(MAFItems.VINE_TOMATO.get(), 2 + level.getRandom().nextInt(3)));
                level.setBlock(pos, state.setValue(getAgeProperty(), REGROWTH_AGE), UPDATE_CLIENTS);
            }
            return InteractionResult.SUCCESS;
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    protected void randomTick(BlockState state, @NonNull ServerLevel level, @NonNull BlockPos pos, @NonNull RandomSource random) {
        if (state.getValue(HALF) != DoubleBlockHalf.LOWER) return;
        if (!level.isAreaLoaded(pos, 1)) return;
        if (level.getRawBrightness(pos, 0) >= 9) {
            int age = getAge(state);
            if (age < getMaxAge()) {
                float growthSpeed = getGrowthSpeed(state, level, pos);
                if (CommonHooks.canCropGrow(level, pos, state, random.nextInt((int) (25.0F / growthSpeed) + 1) == 0)) {
                    level.setBlock(pos, state.setValue(getAgeProperty(), age + 1), UPDATE_CLIENTS);
                    CommonHooks.fireCropGrowPost(level, pos, state);
                }
            }
        }
    }

    @Override
    public boolean isValidBonemealTarget(@NonNull LevelReader level, @NonNull BlockPos pos, BlockState state) {
        if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
            BlockPos belowPos = pos.below();
            BlockState belowState = level.getBlockState(belowPos);
            return belowState.is(this) && super.isValidBonemealTarget(level, belowPos, belowState);
        }
        return super.isValidBonemealTarget(level, pos, state);
    }

    @Override
    public void performBonemeal(@NonNull ServerLevel level, @NonNull RandomSource random, @NonNull BlockPos pos, BlockState state) {
        if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
            BlockPos belowPos = pos.below();
            BlockState belowState = level.getBlockState(belowPos);
            if (belowState.is(this)) {
                super.performBonemeal(level, random, belowPos, belowState);
            }
            return;
        }
        super.performBonemeal(level, random, pos, state);
    }

    @Override
    public void growCrops(@NonNull Level level, @NonNull BlockPos pos, BlockState state) {
        if (state.getValue(HALF) != DoubleBlockHalf.LOWER) return;
        int age = Math.min(getMaxAge(), getAge(state) + getBonemealAgeIncrease(level));
        level.setBlock(pos, state.setValue(getAgeProperty(), age), UPDATE_CLIENTS);
    }
}
