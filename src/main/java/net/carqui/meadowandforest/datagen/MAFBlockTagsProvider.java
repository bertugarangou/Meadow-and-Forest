package net.carqui.meadowandforest.datagen;

import net.carqui.meadowandforest.MAF;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.BlockTagsProvider;

import java.util.concurrent.CompletableFuture;

/**
 * Generates block tag data for the mod. Currently empty; add tags here as needed
 * (e.g. tool-mineability, flammability) for any of the mod's blocks.
 */
public class MAFBlockTagsProvider extends BlockTagsProvider {

    /**
     * Creates the provider bound to this mod's namespace.
     */
    public MAFBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, MAF.MOD_ID);
    }

    /**
     * Adds block tag entries. Currently empty.
     */
    @Override
    protected void addTags(HolderLookup.Provider provider) {

    }
}
