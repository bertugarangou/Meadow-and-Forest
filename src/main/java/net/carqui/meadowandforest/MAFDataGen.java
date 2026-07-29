package net.carqui.meadowandforest;

import net.carqui.meadowandforest.datagen.*;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Collections;
import java.util.List;

@EventBusSubscriber(modid = MAF.MOD_ID)
public class MAFDataGen {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent.Client event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();

        var lookupProvider = event.getLookupProvider();
        generator.addProvider(true, new MAFBlockTagsProvider(packOutput, lookupProvider));

        generator.addProvider(true, new LootTableProvider(packOutput, Collections.emptySet(),
                List.of(new LootTableProvider.SubProviderEntry(MAFBlockLootTableProvider::new, LootContextParamSets.BLOCK)), lookupProvider));

        generator.addProvider(true, new MAFModelProvider(packOutput));
        generator.addProvider(true, new MAFLanguageProvider(packOutput, "en_us"));
        generator.addProvider(true, new MAFLanguageProvider(packOutput, "ca_es"));
        generator.addProvider(true, new MAFLanguageProvider(packOutput, "es_es"));

        generator.addProvider(true, new MAFRecipes.Runner(packOutput, lookupProvider));
    }
}
