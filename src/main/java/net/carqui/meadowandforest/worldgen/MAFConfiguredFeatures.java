package net.carqui.meadowandforest.worldgen;

import net.carqui.meadowandforest.MAF;
import net.carqui.meadowandforest.block.MAFBasilPlantBlock;
import net.carqui.meadowandforest.block.MAFBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

/**
 * Configured feature describing WHAT gets placed for wild Basil: a single
 * mature Basil plant block. The "small patch of 1-4" shape is no longer
 * expressed here - Minecraft 26.1 removed the flower/flower_no_bonemeal/
 * random_patch feature types entirely. Patches are now built by repeating
 * a plain SIMPLE_BLOCK feature via count + random_offset placement modifiers
 * (see MAFPlacedFeatures).
 */
public final class MAFConfiguredFeatures {

    public static final ResourceKey<ConfiguredFeature<?, ?>> BASIL_PATCH = ResourceKey.create(
            Registries.CONFIGURED_FEATURE, Identifier.fromNamespaceAndPath(MAF.MOD_ID, "basil_patch"));

    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        // Wild basil always spawns fully grown (age = MAX_AGE).
        BlockStateProvider basilMatureState = BlockStateProvider.simple(
                MAFBlocks.BASIL_PLANT.get().defaultBlockState()
                        .setValue(MAFBasilPlantBlock.AGE, MAFBasilPlantBlock.MAX_AGE)
        );

        context.register(
                BASIL_PATCH,
                new ConfiguredFeature<>(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(basilMatureState))
        );
    }

    private MAFConfiguredFeatures() {
    }
}
