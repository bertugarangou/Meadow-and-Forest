package net.carqui.meadowandforest.block;


import net.carqui.meadowandforest.MAF;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

import static net.carqui.meadowandforest.item.MAFItems.ITEMS;

/**
 * Central block registry for the mod. Registers all blocks and, via {@link #registerBlock},
 * automatically registers a matching BlockItem for each one.
 */
public class MAFBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MAF.MOD_ID);
    public static final Map<String, DeferredBlock<MAFGlassJarBlock>> GLASS_JARS = registerGlassJars();
    public static final DeferredBlock<MAFGlassJarBlock> GLASS_JAR = GLASS_JARS.get("glass_jar");

    /**
     * Registers the block deferred register to the mod event bus. Call once from the main mod class.
     */
    public static void Register(IEventBus eventBus){
        BLOCKS.register(eventBus);
    }

    /**
     * Registers a BlockItem for the given block under the same registry name.
     */
    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block){
        ITEMS.registerItem(name,properties -> new BlockItem(block.get(), properties.useBlockDescriptionPrefix()));
    }
    /**
     * Registers a block and its matching BlockItem in one call. Use this instead of
     * registering directly on BLOCKS whenever the block should be obtainable as an item.
     */
    public static <T extends Block> DeferredBlock<T> registerBlock(String name, Function<BlockBehaviour.Properties, T> function){
        DeferredBlock<T> toReturn = BLOCKS.registerBlock(name, function);
        registerBlockItem(name, toReturn);
        return (toReturn);
    }

    /**
     * Registers every glass jar color variant defined in {@link MAFGlassJarVariants}.
     */
    private static Map<String, DeferredBlock<MAFGlassJarBlock>> registerGlassJars() {
        LinkedHashMap<String, DeferredBlock<MAFGlassJarBlock>> glassJars = new LinkedHashMap<>();
        for (MAFGlassJarVariants.Variant variant : MAFGlassJarVariants.ALL) {
            glassJars.put(variant.registryName(), registerGlassJar(variant.registryName()));
        }
        return Collections.unmodifiableMap(glassJars);
    }

    /**
     * Registers a single glass jar block variant with its shared block properties.
     */
    private static DeferredBlock<MAFGlassJarBlock> registerGlassJar(String name) {
        return registerBlock(name, properties -> new MAFGlassJarBlock(properties
                .mapColor(MapColor.NONE)
                .lightLevel(MAFGlassJarBlock.LIGHT_EMISSION)
                .noOcclusion()
                .strength(0.2F)
                .sound(SoundType.GLASS)
                .pushReaction(PushReaction.DESTROY)));
    }

    public static final DeferredBlock<MAFTomatoCropBlock> TOMATO_CROP = BLOCKS.registerBlock("tomato_crop",
            properties -> new MAFTomatoCropBlock(properties
                    .mapColor(MapColor.PLANT)
                    .noCollision()
                    .randomTicks()
                    .instabreak()
                    .sound(SoundType.CROP)
                    .pushReaction(PushReaction.DESTROY)));

    // Wild basil - generates naturally on grass (see worldgen package), can also be
    // planted like a flower. Uses registerBlock so it gets its own BlockItem ("basil_plant")
    // registered automatically, matching the "drops basil plant, replant like a flower" behavior.
    public static final DeferredBlock<MAFBasilPlantBlock> BASIL_PLANT = registerBlock("basil_plant",
            properties -> new MAFBasilPlantBlock(properties
                    .mapColor(MapColor.PLANT)
                    .noCollision()
                    .randomTicks()
                    .instabreak()
                    .sound(SoundType.GRASS)
                    .pushReaction(PushReaction.DESTROY)));

    // Drying Tray - dries fruits/veggies/herbs/mushrooms/meat over time via right-click interaction.
    public static final DeferredBlock<MAFDryingTrayBlock> DRYING_TRAY = registerBlock("drying_tray",
            properties -> new MAFDryingTrayBlock(properties
                    .mapColor(MapColor.WOOD)
                    .strength(3F)
                    .sound(SoundType.WOOD)
                    .noOcclusion()));
}
