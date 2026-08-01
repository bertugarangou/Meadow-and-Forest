package net.carqui.meadowandforest.datagen;

import net.carqui.meadowandforest.MAF;
import net.carqui.meadowandforest.item.MAFItems;
import net.carqui.meadowandforest.loot.SeedSwapLootModifier;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;

import java.util.concurrent.CompletableFuture;

public class MAFGlobalLootModifierProvider extends GlobalLootModifierProvider {

    // Vanilla's block loot table for the short grass plant.
    private static final ResourceKey<LootTable> SHORT_GRASS_LOOT_TABLE = ResourceKey.create(
            Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath("minecraft", "blocks/short_grass"));

    // Of the times grass would drop wheat seeds (vanilla: 12.5%), this fraction gets swapped to tomato seeds instead.
    private static final float TOMATO_SEED_SWAP_CHANCE = 0.25F;

    public MAFGlobalLootModifierProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, MAF.MOD_ID);
    }

    @Override
    protected void start() {
        this.add(
                "tomato_seeds_from_grass",
                new SeedSwapLootModifier(
                        new LootItemCondition[] {
                                LootTableIdCondition.builder(SHORT_GRASS_LOOT_TABLE.identifier()).build(),
                                LootItemRandomChanceCondition.randomChance(TOMATO_SEED_SWAP_CHANCE).build()
                        },
                        1000,
                        Items.WHEAT_SEEDS,
                        MAFItems.TOMATO_SEEDS.get()
                )
        );
    }
}
