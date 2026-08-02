package net.carqui.meadowandforest.datagen;

import net.carqui.meadowandforest.block.MAFBlocks;
import net.carqui.meadowandforest.block.MAFTomatoCropBlock;
import net.carqui.meadowandforest.item.MAFItems;
import net.minecraft.advancements.predicates.StatePropertiesPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.AlternativesEntry;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.Set;

public class MAFBlockLootTableProvider extends BlockLootSubProvider {
    public MAFBlockLootTableProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        add(MAFBlocks.TOMATO_CROP.get(), createTomatoCropDrops());
    }

    private LootTable.Builder createTomatoCropDrops() {
        CropBlock crop = MAFBlocks.TOMATO_CROP.get();
        int maxAge = crop.getMaxAge();

        LootItemBlockStatePropertyCondition.Builder isMature =
                LootItemBlockStatePropertyCondition.hasBlockStateProperties(crop)
                        .setProperties(StatePropertiesPredicate.Builder.properties()
                                .hasProperty(CropBlock.AGE, maxAge));

        // Only the lower half drops anything - the trellis top is purely cosmetic.
        LootItemBlockStatePropertyCondition.Builder isLowerHalf =
                LootItemBlockStatePropertyCondition.hasBlockStateProperties(crop)
                        .setProperties(StatePropertiesPredicate.Builder.properties()
                                .hasProperty(MAFTomatoCropBlock.HALF, DoubleBlockHalf.LOWER));

        LootItemBlockStatePropertyCondition.Builder isTrellised =
                LootItemBlockStatePropertyCondition.hasBlockStateProperties(crop)
                        .setProperties(StatePropertiesPredicate.Builder.properties()
                                .hasProperty(MAFTomatoCropBlock.TRELLISED, true));

        // Primary drop: 2 vine tomatoes if mature and trellised, 2 tomatoes if mature and not, nothing if immature.
        LootPool.Builder primaryPool = LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .when(isLowerHalf)
                .add(AlternativesEntry.alternatives(
                        LootItem.lootTableItem(MAFItems.VINE_TOMATO.get())
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(2)))
                                .when(isMature)
                                .when(isTrellised),
                        LootItem.lootTableItem(MAFItems.TOMATO.get())
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(2)))
                                .when(isMature)
                ));

        return LootTable.lootTable()
                .withPool(primaryPool);
    }


    @Override
    protected Iterable<Block> getKnownBlocks() {
        return (MAFBlocks.BLOCKS.getEntries().stream().map(Holder::value)::iterator);
    }
}

