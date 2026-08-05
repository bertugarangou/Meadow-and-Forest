package net.carqui.meadowandforest.datagen;

import net.carqui.meadowandforest.block.MAFBlocks;
import net.carqui.meadowandforest.block.MAFGlassJarBlock;
import net.carqui.meadowandforest.block.MAFTomatoCropBlock;
import net.carqui.meadowandforest.item.MAFItems;
import net.minecraft.advancements.predicates.StatePropertiesPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.AlternativesEntry;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyBlockState;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import org.jspecify.annotations.NonNull;

import java.util.Set;
public class MAFBlockLootTableProvider extends BlockLootSubProvider {
    public MAFBlockLootTableProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        add(MAFBlocks.TOMATO_CROP.get(), createTomatoCropDrops());
        MAFBlocks.GLASS_JARS.values().forEach(block -> add(block.get(), createGlassJarDrops(block.get())));
        // Basil always drops itself (its own BlockItem) regardless of age, like a flower.
        dropSelf(MAFBlocks.BASIL_PLANT.get());
    }

    private LootTable.Builder createGlassJarDrops(Block jar) {
        LootPool.Builder pool = LootPool.lootPool().setRolls(ConstantValue.exactly(1));

        for (int count = 1; count <= 4; count++) {
            LootItemBlockStatePropertyCondition.Builder hasCount =
                    LootItemBlockStatePropertyCondition.hasBlockStateProperties(jar)
                            .setProperties(StatePropertiesPredicate.Builder.properties()
                                    .hasProperty(CandleBlock.CANDLES, count));

            pool.add(LootItem.lootTableItem(jar.asItem())
                    .when(hasCount)
                    .apply(CopyBlockState.copyState(jar).copy(MAFGlassJarBlock.FIREFLY_INSIDE))
                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(count))));
        }

        return LootTable.lootTable().withPool(pool);
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
    protected @NonNull Iterable<Block> getKnownBlocks() {
        return (MAFBlocks.BLOCKS.getEntries().stream().map(Holder::value)::iterator);
    }
}
