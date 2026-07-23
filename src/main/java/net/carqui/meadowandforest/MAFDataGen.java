package net.carqui.meadowandforest;

import net.carqui.meadowandforest.datagen.MAFLanguageProvider;
import net.carqui.meadowandforest.datagen.MAFModelProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = MAF.MOD_ID)
public class MAFDataGen {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent.Client event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();

        generator.addProvider(true, new MAFModelProvider(packOutput));
        generator.addProvider(true, new MAFLanguageProvider(packOutput, "en_us"));
        generator.addProvider(true, new MAFLanguageProvider(packOutput, "ca_es"));
        generator.addProvider(true, new MAFLanguageProvider(packOutput, "es_es"));
    }
}
