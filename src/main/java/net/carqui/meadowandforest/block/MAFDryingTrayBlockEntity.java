package net.carqui.meadowandforest.block;

import net.carqui.meadowandforest.recipe.DryingRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Holds the state for a Drying Tray: up to 4 independently-drying item slots.
 * Ticks server-side, syncing to the client only when a slot actually changes
 * (insert, withdraw, or completion) - never every tick.
 */
public class MAFDryingTrayBlockEntity extends BlockEntity {

    public static final int SLOT_COUNT = 4;
    // Fixed drying duration for every recipe: 2 in-game days.
    public static final double DRYING_TIME_TICKS = 48000.0;
    // How often (in ticks) the environmental modifier is recomputed and cached.
    private static final int ENVIRONMENT_CHECK_INTERVAL = 40;

    /**
     * One drying slot: the item being dried (or already-finished result), how
     * much progress it has accumulated, and whether it has finished drying.
     */
    public record DryingSlot(ItemStack stack, double progress, boolean done) {
        public static final DryingSlot EMPTY = new DryingSlot(ItemStack.EMPTY, 0.0, false);

        public boolean isEmpty() {
            return this.stack.isEmpty();
        }
    }

    private final DryingSlot[] slots = {DryingSlot.EMPTY, DryingSlot.EMPTY, DryingSlot.EMPTY, DryingSlot.EMPTY};

    // Cached, only recomputed every ENVIRONMENT_CHECK_INTERVAL ticks.
    private double cachedEnvironmentMultiplier = 1.0;

    public MAFDryingTrayBlockEntity(BlockPos pos, BlockState state) {
        super(MAFBlockEntities.DRYING_TRAY.get(), pos, state);
    }

    public DryingSlot getSlot(int index) {
        return this.slots[index];
    }

    public boolean isEmpty() {
        for (DryingSlot slot : this.slots) {
            if (!slot.isEmpty()) return false;
        }
        return true;
    }

    /**
     * Places a single item into the given empty slot and starts its timer.
     * Returns false if the slot was already occupied.
     */
    public boolean insert(int index, ItemStack singleItem) {
        if (!this.slots[index].isEmpty()) return false;
        this.slots[index] = new DryingSlot(singleItem, 0.0, false);
        this.setChanged();
        this.syncToClients();
        return true;
    }

    /**
     * Removes and returns whatever is in the given slot (in-progress item or
     * finished result), clearing its progress. Returns ItemStack.EMPTY if the
     * slot was already empty.
     */
    public ItemStack withdraw(int index) {
        DryingSlot slot = this.slots[index];
        if (slot.isEmpty()) return ItemStack.EMPTY;
        this.slots[index] = DryingSlot.EMPTY;
        this.setChanged();
        this.syncToClients();
        return slot.stack();
    }

    private void syncToClients() {
        if (this.level != null && !this.level.isClientSide()) {
            this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    /**
     * Server-side ticker. Early-returns immediately if the tray is empty, or
     * if the (cached) environmental modifier is currently zero.
     */
    public static void tick(Level level, BlockPos pos, BlockState state, MAFDryingTrayBlockEntity be) {
        if (!(level instanceof ServerLevel serverLevel) || be.isEmpty()) return;

        if (level.getGameTime() % ENVIRONMENT_CHECK_INTERVAL == 0) {
            be.cachedEnvironmentMultiplier = computeEnvironmentMultiplier(level, pos);
        }

        double multiplier = be.cachedEnvironmentMultiplier;
        if (multiplier <= 0.0) return;

        boolean changed = false;
        for (int i = 0; i < SLOT_COUNT; i++) {
            DryingSlot slot = be.slots[i];
            if (slot.isEmpty() || slot.done()) continue;

            double newProgress = slot.progress() + multiplier;
            if (newProgress >= DRYING_TIME_TICKS) {
                ItemStack result = resolveResult(serverLevel, slot.stack());
                be.slots[i] = new DryingSlot(result, DRYING_TIME_TICKS, true);
            } else {
                be.slots[i] = new DryingSlot(slot.stack(), newProgress, false);
            }
            changed = true;
        }

        if (changed) {
            be.setChanged();
            be.syncToClients();
        }
    }

    /**
     * Resolves the drying result for the given (still-original) input stack.
     * If no recipe matches anymore (e.g. removed by a datapack reload while
     * drying), the original item is kept unconverted rather than lost.
     */
    private static ItemStack resolveResult(ServerLevel level, ItemStack input) {
        SingleRecipeInput recipeInput = new SingleRecipeInput(input);
        return level.recipeAccess()
                .getRecipeFor(DryingRecipe.TYPE.get(), recipeInput, level)
                .map(holder -> holder.value().assemble(recipeInput))
                .orElse(input);
    }

    /**
     * Multiplicative environmental modifier for this tick: 0 pauses drying
     * entirely (raining, or nighttime in the Overworld); otherwise depends on
     * dimension (Nether dries faster, the End slower).
     */
    private static double computeEnvironmentMultiplier(Level level, BlockPos pos) {
        if (level.isRainingAt(pos)) return 0.0;

        boolean overworld = level.dimension() == Level.OVERWORLD;
        if (overworld && !level.isBrightOutside()) return 0.0;

        if (level.dimension() == Level.NETHER) return 1.5;
        if (level.dimension() == Level.END) return 0.25;
        return 1.0;
    }

    @Override
    protected void saveAdditional(@NonNull ValueOutput output) {
        super.saveAdditional(output);
        for (int i = 0; i < SLOT_COUNT; i++) {
            DryingSlot slot = this.slots[i];
            if (slot.isEmpty()) continue;
            ValueOutput slotOut = output.child("slot_" + i);
            slotOut.store("item", ItemStack.OPTIONAL_CODEC, slot.stack());
            slotOut.putDouble("progress", slot.progress());
            slotOut.putBoolean("done", slot.done());
        }
    }

    @Override
    protected void loadAdditional(@NonNull ValueInput input) {
        super.loadAdditional(input);
        for (int i = 0; i < SLOT_COUNT; i++) {
            ValueInput slotIn = input.childOrEmpty("slot_" + i);
            ItemStack stack = slotIn.read("item", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
            if (stack.isEmpty()) {
                this.slots[i] = DryingSlot.EMPTY;
                continue;
            }
            double progress = slotIn.getDoubleOr("progress", 0.0);
            boolean done = slotIn.getBooleanOr("done", false);
            this.slots[i] = new DryingSlot(stack, progress, done);
        }
    }

    // --- Fast sync on block update (insert/withdraw/complete), on top of the
    // --- default chunk-load sync that saveAdditional/loadAdditional already provide.

    @Override
    public @NonNull CompoundTag getUpdateTag(HolderLookup.@NonNull Provider registries) {
        return this.saveWithoutMetadata(registries);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(@NonNull Connection connection, @NonNull ValueInput input) {
        super.onDataPacket(connection, input);
    }
}
