package net.carqui.meadowandforest.block;


import net.carqui.meadowandforest.MAF;
import net.carqui.meadowandforest.item.MAFItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;

public class MAFBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MAF.MOD_ID);

    public static void Register(IEventBus eventBus){
        BLOCKS.register(eventBus);
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block){
        MAFItems.ITEMS.registerItem(name,properties -> new BlockItem(block.get(), properties.useBlockDescriptionPrefix()));
    }
    public static <T extends Block> DeferredBlock<T> registerBlock(String name, Function<BlockBehaviour.Properties, T> function){
        DeferredBlock<T> toReturn = BLOCKS.registerBlock(name, function);
        registerBlockItem(name, toReturn);
        return (toReturn);
    }


}
