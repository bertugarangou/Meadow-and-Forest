package net.carqui.meadowandforest.block;

import net.carqui.meadowandforest.MAF;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

/**
 * Central block entity type registry for the mod.
 */
public class MAFBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MAF.MOD_ID);

    public static final Supplier<BlockEntityType<MAFDryingTrayBlockEntity>> DRYING_TRAY =
            BLOCK_ENTITY_TYPES.register("drying_tray",
                    () -> new BlockEntityType<>(MAFDryingTrayBlockEntity::new, false, MAFBlocks.DRYING_TRAY.get()));

    /**
     * Registers the block entity type deferred register to the mod event bus. Call once from the main mod class.
     */
    public static void register(IEventBus eventBus) {
        BLOCK_ENTITY_TYPES.register(eventBus);
    }
}
