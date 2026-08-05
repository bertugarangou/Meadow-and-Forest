package net.carqui.meadowandforest.worldgen;

import net.carqui.meadowandforest.MAF;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.*;

import java.util.List;

/**
 * Placed features describing WHERE / HOW OFTEN Basil patches generate.
 *
 * Since Minecraft 26.1 removed the random_patch feature type, a "patch" is now
 * built directly out of placement modifiers: CountPlacement repeats the single
 * SIMPLE_BLOCK feature 1-4 times, and RandomOffsetPlacement scatters each of
 * those repeats within a small radius - together reproducing the old
 * tries/xz_spread/y_spread behavior of random_patch.
 */
public final class MAFPlacedFeatures {

    public static final ResourceKey<PlacedFeature> BASIL_PATCH_COMMON = ResourceKey.create(
            Registries.PLACED_FEATURE, Identifier.fromNamespaceAndPath(MAF.MOD_ID, "basil_patch_common"));

    public static final ResourceKey<PlacedFeature> BASIL_PATCH_RARE = ResourceKey.create(
            Registries.PLACED_FEATURE, Identifier.fromNamespaceAndPath(MAF.MOD_ID, "basil_patch_rare"));

    // Higher = rarer.
    // Common: Plains / Sunflower Plains / Meadow.
    // Rare: Sparse Jungle.
    private static final int COMMON_RARITY = 25; // ~4% of chunks
    private static final int RARE_RARITY = 30;   // ~3.3% of chunks

    public static void bootstrap(BootstrapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);
        var basilPatch = configuredFeatures.getOrThrow(MAFConfiguredFeatures.BASIL_PATCH);

        context.register(BASIL_PATCH_COMMON, new PlacedFeature(basilPatch, placementModifiers(COMMON_RARITY)));
        context.register(BASIL_PATCH_RARE, new PlacedFeature(basilPatch, placementModifiers(RARE_RARITY)));
    }

    // 1-4 plants scattered in a small radius, snapped to the surface heightmap,
    // only directly on Grass Block, and only where it's air.
    private static List<PlacementModifier> placementModifiers(int rarity) {
        return List.of(
                RarityFilter.onAverageOnceEvery(rarity),
                CountPlacement.of(UniformInt.of(1, 4)),
                RandomOffsetPlacement.of(UniformInt.of(-2, 2), ConstantInt.of(0)),
                HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG),
                BlockPredicateFilter.forPredicate(
                        BlockPredicate.matchesBlocks(BlockPos.ZERO.below(), Blocks.GRASS_BLOCK)),
                BlockPredicateFilter.forPredicate(BlockPredicate.ONLY_IN_AIR_PREDICATE),
                BiomeFilter.biome()
        );
    }

    private MAFPlacedFeatures() {
    }
}
