package net.carqui.meadowandforest;

import net.carqui.meadowandforest.datagen.*;
import net.carqui.meadowandforest.item.MAFItems;
import net.carqui.meadowandforest.loot.SeedSwapLootModifier;
import net.carqui.meadowandforest.worldgen.MAFBiomeModifiers;
import net.carqui.meadowandforest.worldgen.MAFConfiguredFeatures;
import net.carqui.meadowandforest.worldgen.MAFPlacedFeatures;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Collections;
import java.util.List;

@EventBusSubscriber(modid = MAF.MOD_ID)
public class MAFDataGen {

    // Vanilla's block loot table for the short grass plant.
    private static final ResourceKey<LootTable> SHORT_GRASS_LOOT_TABLE = ResourceKey.create(
            Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath("minecraft", "blocks/short_grass"));

    @SubscribeEvent
    public static void gatherData(GatherDataEvent.Client event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();

        // Basil worldgen: configured feature, placed features, and biome modifiers.
        event.createDatapackRegistryObjects(
                new RegistrySetBuilder()
                        .add(Registries.CONFIGURED_FEATURE, MAFConfiguredFeatures::bootstrap)
                        .add(Registries.PLACED_FEATURE, MAFPlacedFeatures::bootstrap)
                        .add(net.neoforged.neoforge.registries.NeoForgeRegistries.Keys.BIOME_MODIFIERS, MAFBiomeModifiers::bootstrap)
        );

        var lookupProvider = event.getLookupProvider();
        generator.addProvider(true, new MAFBlockTagsProvider(packOutput, lookupProvider));

        generator.addProvider(true, new LootTableProvider(packOutput, Collections.emptySet(),
                List.of(new LootTableProvider.SubProviderEntry(MAFBlockLootTableProvider::new, LootContextParamSets.BLOCK)), lookupProvider));

        generator.addProvider(true, new MAFModelProvider(packOutput));
        generator.addProvider(true, new MAFLanguageProvider(packOutput, "en_us"));

        generator.addProvider(true, new MAFRecipes.Runner(packOutput, lookupProvider));

        // When grass drops wheat seeds, 12% of the time swap it for a tomato seed instead (88% wheat / 12% tomato).
        generator.addProvider(true, new GlobalLootModifierProvider(packOutput, lookupProvider, MAF.MOD_ID) {
            @Override
            protected void start() {
                this.add(
                        "tomato_seeds_from_grass",
                        new SeedSwapLootModifier(
                                new LootItemCondition[] {
                                        LootTableIdCondition.builder(SHORT_GRASS_LOOT_TABLE.identifier()).build(),
                                        LootItemRandomChanceCondition.randomChance(0.12F).build()
                                },
                                1000,
                                Items.WHEAT_SEEDS,
                                MAFItems.TOMATO_SEEDS.get()
                        )
                );
            }
        });
    }
}
