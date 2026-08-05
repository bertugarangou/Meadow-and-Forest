package net.carqui.meadowandforest.block;

import com.mojang.serialization.MapCodec;
import net.carqui.meadowandforest.recipe.DryingRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * A low tray that dries up to 4 items in a 2x2 grid, purely through
 * right-click interaction - no GUI. Right-click an empty quadrant with a
 * compatible item to start drying it; right-click an occupied quadrant with
 * an empty hand to withdraw it (cancelling progress if unfinished).
 */
public class MAFDryingTrayBlock extends BaseEntityBlock {

    private static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);

    public static final MapCodec<MAFDryingTrayBlock> CODEC = simpleCodec(MAFDryingTrayBlock::new);

    public MAFDryingTrayBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected @NonNull MapCodec<MAFDryingTrayBlock> codec() {
        return CODEC;
    }

    @Override
    protected @NonNull VoxelShape getShape(@NonNull BlockState state, @NonNull BlockGetter level, @NonNull BlockPos pos, @NonNull CollisionContext context) {
        return SHAPE;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NonNull BlockPos pos, @NonNull BlockState state) {
        return new MAFDryingTrayBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(@NonNull Level level, @NonNull BlockState state, @NonNull BlockEntityType<T> type) {
        if (level.isClientSide()) return null;
        return createTickerHelper(type, MAFBlockEntities.DRYING_TRAY.get(), MAFDryingTrayBlockEntity::tick);
    }

    @Override
    protected @NonNull InteractionResult useItemOn(@NonNull ItemStack stack, @NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos, @NonNull Player player, @NonNull InteractionHand hand, @NonNull BlockHitResult hitResult) {
        if (!(level.getBlockEntity(pos) instanceof MAFDryingTrayBlockEntity tray)) {
            return InteractionResult.PASS;
        }

        int slotIndex = resolveSlot(pos, hitResult);
        MAFDryingTrayBlockEntity.DryingSlot slot = tray.getSlot(slotIndex);

        if (!slot.isEmpty()) {
            // Occupied slot: only an empty hand withdraws it (cancelling progress if unfinished).
            if (!stack.isEmpty()) return InteractionResult.PASS;

            if (level instanceof ServerLevel) {
                ItemStack returned = tray.withdraw(slotIndex);
                if (!returned.isEmpty() && !player.getInventory().add(returned)) {
                    player.drop(returned, false);
                }
            }
            return InteractionResult.SUCCESS_SERVER;
        }

        // Empty slot: need a held item. Recipe matching requires a full RecipeManager,
        // which is only available server-side (Level#recipeAccess() only exposes the
        // narrower client-safe RecipeAccess) - so the client defers entirely to the server.
        if (stack.isEmpty()) {
            return InteractionResult.PASS;
        }
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.SUCCESS_SERVER;
        }
        if (!hasDryingRecipe(serverLevel, stack)) {
            return InteractionResult.PASS;
        }

        tray.insert(slotIndex, stack.copyWithCount(1));
        stack.shrink(1);
        return InteractionResult.SUCCESS_SERVER;
    }

    private static boolean hasDryingRecipe(ServerLevel level, ItemStack stack) {
        return level.recipeAccess()
                .getRecipeFor(DryingRecipe.TYPE.get(), new SingleRecipeInput(stack), level)
                .isPresent();
    }

    /**
     * Maps the exact hit location on the tray's top face to one of the 4
     * quadrants (0 = -X-Z, 1 = +X-Z, 2 = -X+Z, 3 = +X+Z).
     */
    private static int resolveSlot(BlockPos pos, BlockHitResult hitResult) {
        double localX = hitResult.getLocation().x - pos.getX();
        double localZ = hitResult.getLocation().z - pos.getZ();
        int col = localX >= 0.5 ? 1 : 0;
        int row = localZ >= 0.5 ? 1 : 0;
        return row * 2 + col;
    }
}
