package net.carqui.meadowandforest.datagen;

import net.carqui.meadowandforest.block.MAFBlocks;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;

import java.util.Set;

public class MAFBlockLootTableProvider extends BlockLootSubProvider {
    public MAFBlockLootTableProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        dropSelf(MAFBlocks.BLOCK_NAME.get());

    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return (MAFBlocks.BLOCKS.getEntries().stream().map(Holder::value)::iterator);
    }
}

