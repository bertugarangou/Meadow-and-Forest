package net.carqui.meadowandforest.block;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

import java.util.List;

/**
 * Defines every glass jar color variant and the vanilla glass block each one is crafted from.
 */
public final class MAFGlassJarVariants {
    /**
     * One glass jar color variant: its block registry name and its crafting ingredient.
     */
    public record Variant(String registryName, ItemLike ingredient) {}

    public static final List<Variant> ALL = List.of(
            new Variant("glass_jar", Blocks.GLASS),
            new Variant("white_glass_jar", Blocks.STAINED_GLASS.pick(DyeColor.WHITE)),
            new Variant("orange_glass_jar", Blocks.STAINED_GLASS.pick(DyeColor.ORANGE)),
            new Variant("magenta_glass_jar", Blocks.STAINED_GLASS.pick(DyeColor.MAGENTA)),
            new Variant("light_blue_glass_jar", Blocks.STAINED_GLASS.pick(DyeColor.LIGHT_BLUE)),
            new Variant("yellow_glass_jar", Blocks.STAINED_GLASS.pick(DyeColor.YELLOW)),
            new Variant("lime_glass_jar", Blocks.STAINED_GLASS.pick(DyeColor.LIME)),
            new Variant("pink_glass_jar", Blocks.STAINED_GLASS.pick(DyeColor.PINK)),
            new Variant("gray_glass_jar", Blocks.STAINED_GLASS.pick(DyeColor.GRAY)),
            new Variant("light_gray_glass_jar", Blocks.STAINED_GLASS.pick(DyeColor.LIGHT_GRAY)),
            new Variant("cyan_glass_jar", Blocks.STAINED_GLASS.pick(DyeColor.CYAN)),
            new Variant("purple_glass_jar", Blocks.STAINED_GLASS.pick(DyeColor.PURPLE)),
            new Variant("blue_glass_jar", Blocks.STAINED_GLASS.pick(DyeColor.BLUE)),
            new Variant("brown_glass_jar", Blocks.STAINED_GLASS.pick(DyeColor.BROWN)),
            new Variant("green_glass_jar", Blocks.STAINED_GLASS.pick(DyeColor.GREEN)),
            new Variant("red_glass_jar", Blocks.STAINED_GLASS.pick(DyeColor.RED)),
            new Variant("black_glass_jar", Blocks.STAINED_GLASS.pick(DyeColor.BLACK))
    );

    private MAFGlassJarVariants() {
    }
}
