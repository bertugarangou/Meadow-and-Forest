package net.carqui.meadowandforest.block;

import net.carqui.meadowandforest.item.MAFItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.NonNull;


/**
 * Wild Basil plant.
 * - Two growth stages: age 0 (freshly planted) and age 1 (mature / max age).
 * - Naturally generated basil always spawns already at age 1 (see MAFConfiguredFeatures).
 * - Player-planted basil starts at age 0 and slowly grows to age 1 via random ticks.
 * - Breaking the block (at any age) drops the "basil plant" item, i.e., its own
 *   BlockItem (registered automatically by MAFBlocks.registerBlock), so it can be
 *   re-planted like a flower. See MAFBlockLootTableProvider for the drop table.
 * - Right-clicking a mature (age 1) plant with Shears harvests 1-2 Basil Leaves
 *   without destroying the plant; the plant resets to age 0 and must regrow.
 *   If the plant is directly adjacent (N/S/E/W) to a Tomato Crop block, the
 *   harvest always yields 2 leaves instead of the normal 50/50 chance for 1 or 2.
 */
public class MAFBasilPlantBlock extends BushBlock implements BonemealableBlock {

    public static final int MAX_AGE = 4;
    // Vanilla 0-1 integer property (the same one Bamboo uses for its small/large stalk state).
    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, MAX_AGE);

    private static final VoxelShape[] SHAPES_BY_AGE = new VoxelShape[]{
            Block.box(6.0D, 0.0D, 6.0D, 10.0D, 6.5D, 10.0D),     // age 0: w=2, h=4
            Block.box(5.0D, 0.0D, 5.0D, 11.0D, 9.5D, 10.5D),   // age 1: w=5, h=8
            Block.box(3.5D, 0.0D, 3.5D, 12.5D, 12.0D, 12.5D),  // age 2: w=8, h=12
            Block.box(2.5D, 0.0D, 2.5D, 13.5D, 15.0D, 13.5D),  // age 3: w=10, h=15
            Block.box(1.5D, 0.0D, 1.5D, 14.5D, 16.0D, 14.5D)   // age 4: w=12, h=16
    };

    // Chance denominator for growth on a random tick (1 in N random ticks -> grows one stage).
    private static final int GROWTH_CHANCE_DENOMINATOR = 8;

    /**
     * Creates the block with the given properties and sets its default state to age 0.
     */
    public MAFBasilPlantBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(AGE, 0));
    }

    /**
     * Bone meal succeeds as long as the plant hasn't reached max age yet.
     */
    public boolean isBonemealSuccess(@NonNull Level level, @NonNull RandomSource random, @NonNull BlockPos pos, BlockState state) {
        return state.getValue(AGE) < MAX_AGE;
    }

    /**
     * Bone meal only targets this plant while it hasn't reached max age yet.
     */
    @Override
    public boolean isValidBonemealTarget(@NonNull LevelReader level, @NonNull BlockPos pos, BlockState state) {
        return state.getValue(AGE) < MAX_AGE;
    }


    /**
     * Advances the plant by one growth stage when bone meal is used on it.
     */
    @Override
    public void performBonemeal(@NonNull ServerLevel level, @NonNull RandomSource random, @NonNull BlockPos pos, BlockState state) {
        int currentAge = state.getValue(AGE);
        if (currentAge < MAX_AGE) {
            level.setBlock(pos, state.setValue(AGE, Math.min(currentAge + 1 + random.nextInt(3), MAX_AGE)), Block.UPDATE_CLIENTS);
        }
    }

    /**
     * Declares the age property as part of this block's state.
     */
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    /**
     * Returns the collision/selection shape, larger for the mature stage than the young one.
     */
    @Override
    protected @NonNull VoxelShape getShape(BlockState state, @NonNull BlockGetter level, @NonNull BlockPos pos, @NonNull CollisionContext context) {
        return SHAPES_BY_AGE[state.getValue(AGE)];
    }

    // Can be planted on grass/dirt-type blocks, farmland, moss, etc. - the same set of
    // blocks vanilla bushes/short grass/flowers can be planted on. (Minecraft 26.1 split
    // #dirt into narrower tags and moved Grass Block out of it - #supports_vegetation is
    // the new purpose-built tag covering everything flowers/bushes could survive on before.)
    /**
     * Determines which blocks this plant can be placed/survive on.
     */
    @Override
    protected boolean mayPlaceOn(BlockState state, @NonNull BlockGetter level, @NonNull BlockPos pos) {
        return state.is(BlockTags.SUPPORTS_VEGETATION);
    }

    /**
     * The plant only needs random ticks while it hasn't reached max age yet.
     */
    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return state.getValue(AGE) < MAX_AGE;
    }

    /**
     * On each random tick, has a chance to grow the plant by one age stage.
     */
    @Override
    protected void randomTick(BlockState state, @NonNull ServerLevel level, @NonNull BlockPos pos, @NonNull RandomSource random) {
        if (state.getValue(AGE) < MAX_AGE && random.nextInt(GROWTH_CHANCE_DENOMINATOR) == 0) {
            level.setBlock(pos, state.setValue(AGE, state.getValue(AGE) + 1), Block.UPDATE_CLIENTS);
        }
    }

    // Right-clicking a mature plant with Shears harvests leaves without destroying it.
    /**
     * Handles shearing a mature plant: drops 1-2 leaves (always 3 next to a Tomato Crop),
     * damages the shears, and resets the plant to age 0 instead of breaking it.
     */
    @Override
    protected @NonNull InteractionResult useItemOn(ItemStack stack, @NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos,
                                                   @NonNull Player player, @NonNull InteractionHand hand, @NonNull BlockHitResult hitResult) {
        if (stack.is(Items.SHEARS) && state.getValue(AGE) >= MAX_AGE) {
            if (!level.isClientSide()) {
                boolean nextToTomato = isAdjacentToTomatoCrop(level, pos);

                int leafCount;
                if (nextToTomato) {
                    // Always 2 leaves when grown next to a tomato crop.
                    leafCount = 3;
                } else {
                    // 50% chance for 1 leaf, 50% chance for 2 leaves.
                    leafCount = level.getRandom().nextBoolean() ? 1 : 2;
                }

                Block.popResource(level, pos, new ItemStack(MAFItems.BASIL_LEAVES.get(), leafCount));
                level.playSound(null, pos, SoundEvents.SHEEP_SHEAR, SoundSource.BLOCKS, 1.0F, 1.0F);

                // Damage the shears by 1 durability point (works whichever hand held them).
                EquipmentSlot slot = hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
                stack.hurtAndBreak(1, player, slot);

                // Harvesting does not destroy the plant - it resets to age 0 and regrows.
                level.setBlock(pos, state.setValue(AGE, 2), Block.UPDATE_CLIENTS);
            }
            return InteractionResult.SUCCESS;
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    /**
     * Checks the four horizontal neighbors of this position for a Tomato Crop block.
     */
    private boolean isAdjacentToTomatoCrop(Level level, BlockPos pos) {
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            if (level.getBlockState(pos.relative(direction)).is(MAFBlocks.TOMATO_CROP.get())) {
                return true;
            }
        }
        return false;
    }
}
