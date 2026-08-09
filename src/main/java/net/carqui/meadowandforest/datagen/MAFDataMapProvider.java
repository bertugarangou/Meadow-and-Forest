package net.carqui.meadowandforest.datagen;

import net.carqui.meadowandforest.item.MAFItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.datamaps.builtin.FurnaceFuel;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public class MAFDataMapProvider extends DataMapProvider {

    public MAFDataMapProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @Override
    protected void gather(HolderLookup.@NonNull Provider provider) {
        builder(NeoForgeDataMaps.FURNACE_FUELS).add(MAFItems.BASIL_LEAVES_DRIED.getId(), new FurnaceFuel(110), false);

    }
}
