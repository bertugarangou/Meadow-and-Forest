package net.carqui.meadowandforest.item;

import net.carqui.meadowandforest.MAF;
import net.carqui.meadowandforest.block.MAFBlocks;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MAFItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MAF.MOD_ID);


    public static final DeferredItem<Item> TOMATO_SEEDS = ITEMS.registerItem("tomato_seeds",
            properties -> new BlockItem(MAFBlocks.TOMATO_CROP.get(), properties.useBlockDescriptionPrefix()));

    public static final DeferredItem<Item> TOMATO = ITEMS.registerSimpleItem("tomato");
    public static final DeferredItem<Item> VINE_TOMATO = ITEMS.registerSimpleItem("vine_tomato");
    public static final DeferredItem<Item> CANE = ITEMS.registerSimpleItem("cane");
    public static final DeferredItem<Item> TOMATO_SOUP = ITEMS.registerSimpleItem("tomato_soup");
    public static final DeferredItem<Item> HALF_BREAD = ITEMS.registerSimpleItem("half_a_bread");
    public static final DeferredItem<Item> WHOLE_BREAD = ITEMS.registerSimpleItem("whole_bread");
    public static final DeferredItem<Item> SLICE_OF_BREAD = ITEMS.registerSimpleItem("slice_of_bread");
    public static final DeferredItem<Item> PA_AMB_TOMATA = ITEMS.registerSimpleItem("pa_amb_tomata");
    public static final DeferredItem<Item> XAVA_ABERRATION = ITEMS.registerSimpleItem("xava_aberration");
    public static final DeferredItem<Item> BREAD_DOUGH = ITEMS.registerSimpleItem("bread_dough");


    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
