package net.carqui.meadowandforest.block;

import net.carqui.meadowandforest.MAF;
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
import org.jspecify.annotations.NonNull;

public class MAFGlassJarBlock extends CandleBlock {
    public static final BooleanProperty FIREFLY_INSIDE = BooleanProperty.create("firefly_inside");

    private static final VoxelShape SHAPE = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 5.0D, 12.0D);

    public MAFGlassJarBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(CANDLES, 1)
                .setValue(LIT, false)
                .setValue(WATERLOGGED, false)
                .setValue(FIREFLY_INSIDE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NonNull Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FIREFLY_INSIDE);
    }

    @Override
    public @NonNull VoxelShape getShape(@NonNull BlockState state, @NonNull BlockGetter level, @NonNull BlockPos pos, @NonNull CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected @NonNull InteractionResult useItemOn(@NonNull ItemStack stack, @NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos, @NonNull Player player, @NonNull InteractionHand hand, @NonNull BlockHitResult hitResult) {
        return InteractionResult.PASS;
    }

    @EventBusSubscriber(modid = MAF.MOD_ID)
    public static class InteractionHandler {
        @SubscribeEvent
        public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
            if (event.getLevel().isClientSide()) {
                return;
            }

            var targetState = event.getLevel().getBlockState(event.getPos());
            if (!targetState.is(net.minecraft.world.level.block.Blocks.FIREFLY_BUSH)) {
                return;
            }

            ItemStack stack = event.getItemStack();
            if (!stack.is(MAFBlocks.GLASS_JAR.get().asItem())) {
                return;
            }

            stack.set(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY.with(FIREFLY_INSIDE, true));
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
        }
    }
}
