package net.carqui.meadowandforest.datagen;

import net.carqui.meadowandforest.MAF;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.BlockTagsProvider;

import java.util.concurrent.CompletableFuture;

public class MAFBlockTagsProvider extends BlockTagsProvider {

    public MAFBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, MAF.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {

    }
}
