package net.carqui.meadowandforest.worldgen;

import net.carqui.meadowandforest.MAF;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers.AddFeaturesBiomeModifier;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/**
 * Injects the Basil placed features into the target biomes.
 */
public final class MAFBiomeModifiers {

    public static final ResourceKey<BiomeModifier> ADD_BASIL_COMMON = ResourceKey.create(
            NeoForgeRegistries.Keys.BIOME_MODIFIERS, Identifier.fromNamespaceAndPath(MAF.MOD_ID, "add_basil_common"));

    public static final ResourceKey<BiomeModifier> ADD_BASIL_SPARSE_JUNGLE = ResourceKey.create(
            NeoForgeRegistries.Keys.BIOME_MODIFIERS, Identifier.fromNamespaceAndPath(MAF.MOD_ID, "add_basil_sparse_jungle"));

    public static void bootstrap(BootstrapContext<BiomeModifier> context) {
        HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
        HolderGetter<PlacedFeature> placedFeatures = context.lookup(Registries.PLACED_FEATURE);

        context.register(
                ADD_BASIL_COMMON,
                new AddFeaturesBiomeModifier(
                        HolderSet.direct(
                                biomes.getOrThrow(Biomes.PLAINS),
                                biomes.getOrThrow(Biomes.SUNFLOWER_PLAINS),
                                biomes.getOrThrow(Biomes.MEADOW)
                        ),
                        HolderSet.direct(placedFeatures.getOrThrow(MAFPlacedFeatures.BASIL_PATCH_COMMON)),
                        GenerationStep.Decoration.VEGETAL_DECORATION
                )
        );

        context.register(
                ADD_BASIL_SPARSE_JUNGLE,
                new AddFeaturesBiomeModifier(
                        HolderSet.direct(biomes.getOrThrow(Biomes.SPARSE_JUNGLE)),
                        HolderSet.direct(placedFeatures.getOrThrow(MAFPlacedFeatures.BASIL_PATCH_RARE)),
                        GenerationStep.Decoration.VEGETAL_DECORATION
                )
        );
    }

    private MAFBiomeModifiers() {
    }
}
