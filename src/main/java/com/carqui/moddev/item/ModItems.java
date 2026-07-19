package com.carqui.moddev.item;

import com.carqui.moddev.ModDev;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    //afegim els items
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ModDev.MOD_ID);


    public static final DeferredItem<Item> TOMATO_SEEDS = ITEMS.registerSimpleItem("tomato_seeds");
    public static final DeferredItem<Item> TOMATO = ITEMS.registerSimpleItem("tomato");





    //registrem els items
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
