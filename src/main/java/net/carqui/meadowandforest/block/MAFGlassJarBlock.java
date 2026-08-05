package net.carqui.meadowandforest.block;

import net.carqui.meadowandforest.MAF;
import net.carqui.meadowandforest.item.MAFDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.function.ToIntFunction;

/**
 * A candle-like jar block that can hold a firefly, catching it from a Firefly Bush
 * or releasing it back into Short Grass. Reuses candle count/lit/waterlogged states.
 */
public class MAFGlassJarBlock extends CandleBlock {
    public static final BooleanProperty FIREFLY_INSIDE = BooleanProperty.create("firefly_inside");

    private static final VoxelShape SHAPE_1 = Block.box(5.0D, 0.0D, 4.5D, 11.0D, 10.5D, 11.0D);
    private static final VoxelShape SHAPE_2 = Block.box(1.75D, 0.0D, 1.75D, 15.0D, 11.0D, 15.0D);
    private static final VoxelShape SHAPE_3 = Block.box(2.0D, 0.0D, 1.75D, 15.0D, 11.0D, 15.0D);
    private static final VoxelShape SHAPE_4 = Block.box(1.75D, 0.0D, 1.75D, 15.25D, 11.0D, 15.0D);

    private static final int[] LIGHT_LEVELS = new int[]{3, 6, 9, 12};

    public static final ToIntFunction<BlockState> LIGHT_EMISSION = state -> {
        if (!state.getValue(FIREFLY_INSIDE)) return 0;
        return LIGHT_LEVELS[state.getValue(CANDLES) - 1];
    };

    /**
     * Creates the block with the given properties and sets its default state
     * to one unlit, dry candle slot with no firefly inside.
     */
    public MAFGlassJarBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(CANDLES, 1)
                .setValue(LIT, false)
                .setValue(WATERLOGGED, false)
                .setValue(FIREFLY_INSIDE, false));
    }

    /**
     * Adds the firefly-inside flag to this block's state, on top of the candle properties.
     */
    @Override
    protected void createBlockStateDefinition(StateDefinition.@NonNull Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FIREFLY_INSIDE);
    }

    /**
     * Glass jars can never be lit, unlike regular candles.
     */
    @Override
    protected boolean canBeLit(@NonNull BlockState state) {
        return false;
    }

    /**
     * Returns the jar's collision/selection shape, which changes with the candle count.
     */
    @Override
    public @NotNull VoxelShape getShape(@NonNull BlockState state, @NonNull BlockGetter level, @NonNull BlockPos pos, @NonNull CollisionContext context) {
        return switch (state.getValue(CANDLES)) {
            case 1 -> SHAPE_1;
            case 2 -> SHAPE_2;
            case 3 -> SHAPE_3;
            default -> SHAPE_4;
        };
    }

    /**
     * Disables the default candle right-click behavior; firefly capture/release is
     * handled separately by {@link InteractionHandler}.
     */
    @Override
    protected @NonNull InteractionResult useItemOn(@NonNull ItemStack stack, @NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos, @NonNull Player player, @NonNull InteractionHand hand, @NonNull BlockHitResult hitResult) {
        return InteractionResult.PASS;
    }

    /**
     * Handles right-clicking Firefly Bush or Short Grass while holding a glass jar,
     * moving the firefly between the jar item and the world block.
     */
    @EventBusSubscriber(modid = MAF.MOD_ID)
    public static class InteractionHandler {
        /**
         * Catches a firefly from a Firefly Bush into an empty jar, or releases a
         * firefly from a jar into Short Grass. Ignored for any other block or item.
         */
        @SubscribeEvent
        public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
            boolean clientSide = event.getLevel().isClientSide();
            var targetState = event.getLevel().getBlockState(event.getPos());
            ItemStack stack = event.getItemStack();
            if (!isGlassJar(stack)) {
                return;
            }

            BlockItemStateProperties jarState = stack.getOrDefault(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY);
            boolean fireflyInside = Boolean.TRUE.equals(jarState.get(FIREFLY_INSIDE));

            if (targetState.is(net.minecraft.world.level.block.Blocks.FIREFLY_BUSH)) {
                if (fireflyInside) {
                    event.setCancellationResult(InteractionResult.FAIL);
                    event.setCanceled(true);
                    return;
                }

                if (!clientSide) {
                    event.getLevel().setBlockAndUpdate(event.getPos(), net.minecraft.world.level.block.Blocks.SHORT_GRASS.defaultBlockState());
                }
                stack.set(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY.with(FIREFLY_INSIDE, true));
                stack.set(MAFDataComponents.FIREFLY_INSIDE_MARKER.get(), true);
                event.setCancellationResult(InteractionResult.SUCCESS);
                event.setCanceled(true);
                return;
            }

            if (targetState.is(net.minecraft.world.level.block.Blocks.TALL_GRASS)) {
                event.setCancellationResult(InteractionResult.FAIL);
                event.setCanceled(true);
                return;
            }

            if (!targetState.is(net.minecraft.world.level.block.Blocks.SHORT_GRASS)) {
                return;
            }

            if (!fireflyInside) {
                event.setCancellationResult(InteractionResult.FAIL);
                event.setCanceled(true);
                return;
            }

            if (!clientSide) {
                event.getLevel().setBlockAndUpdate(event.getPos(), net.minecraft.world.level.block.Blocks.FIREFLY_BUSH.defaultBlockState());
            }
            stack.set(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY.with(FIREFLY_INSIDE, false));
            stack.remove(MAFDataComponents.FIREFLY_INSIDE_MARKER.get());
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
        }

        /**
         * Checks whether the given item stack is any glass jar color variant.
         */
        private static boolean isGlassJar(ItemStack stack) {
            for (MAFGlassJarVariants.Variant variant : MAFGlassJarVariants.ALL) {
                if (stack.is(MAFBlocks.GLASS_JARS.get(variant.registryName()).get().asItem())) {
                    return true;
                }
            }
            return false;
        }
    }
}