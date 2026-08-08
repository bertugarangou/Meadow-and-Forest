package net.carqui.meadowandforest.inventarytab;

import net.carqui.meadowandforest.MAF;
import net.carqui.meadowandforest.block.MAFBlocks;
import net.carqui.meadowandforest.block.MAFGlassJarVariants;
import net.carqui.meadowandforest.item.MAFItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

/**
 * Registers the mod's custom creative inventory tab and defines which items
 * and blocks appear inside it.
 */
public class MAFInventoryTab {
    public static final DeferredRegister<CreativeModeTab> INVENTORY_TAB = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MAF.MOD_ID);

    /**
     * Registers the creative tab deferred register to the mod event bus. Call once from the main mod class.
     */
    public static void register(IEventBus eventBus) {
        INVENTORY_TAB.register(eventBus);
    }

    public static final Supplier<CreativeModeTab> MAF_ITEM_TAB = INVENTORY_TAB.register("meadowandforest_items_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(MAFItems.TOMATO.get()))

                    .title(Component.translatable("creativetab.meadowandforest.tab_items"))
                    .displayItems((itemDisplayParameters, output) -> {

                        output.accept(MAFItems.CANE);
                        output.accept(MAFItems.HALF_BREAD);
                        output.accept(MAFItems.PA_AMB_TOMATA);
                        output.accept(MAFItems.SLICE_OF_BREAD);
                        output.accept(MAFItems.TOMATO);
                        output.accept(MAFItems.TOMATO_SEEDS);
                        output.accept(MAFItems.TOMATO_SOUP);
                        output.accept(MAFItems.VINE_TOMATO);
                        output.accept(MAFItems.WHOLE_BREAD);
                        output.accept(MAFItems.XAVA_ABERRATION);
                        output.accept(MAFItems.BAGUETTE_BREAD_BREADDOUGH);
                        output.accept(MAFItems.BIG_BREAD_BREADDOUGH);
                        output.accept(MAFItems.SMALL_BREAD_BREADDOUGH);
                        output.accept(MAFItems.BREADDOUGH);
                        MAFGlassJarVariants.ALL.forEach(variant -> output.accept(MAFBlocks.GLASS_JARS.get(variant.registryName())));
                        output.accept(MAFItems.BASIL_LEAVES);
                        output.accept(MAFItems.BASIL_LEAVES_DRIED);
                        output.accept(MAFBlocks.DRYING_TRAY);
                        output.accept(MAFBlocks.BASIL_PLANT);


                    }).build());
}
