package net.carqui.meadowandforest;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import net.carqui.meadowandforest.block.MAFBlocks;
import net.carqui.meadowandforest.inventarytab.MAFInventoryTab;
import net.carqui.meadowandforest.item.MAFItems;
import net.carqui.meadowandforest.loot.SeedSwapLootModifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.slf4j.Logger;

import java.util.function.Supplier;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(MAF.MOD_ID)
public class MAF {
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "meadowandforest";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    // Global loot modifiers (e.g. grass sometimes dropping tomato seeds instead of wheat seeds)
    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> LOOT_MODIFIER_SERIALIZERS =
            DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, MOD_ID);
    public static final Supplier<MapCodec<SeedSwapLootModifier>> SEED_SWAP_LOOT_MODIFIER =
            LOOT_MODIFIER_SERIALIZERS.register("seed_swap", () -> SeedSwapLootModifier.CODEC);

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public MAF(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        //carreguem les classes
        MAFInventoryTab.register(modEventBus);
        MAFItems.register(modEventBus);
        MAFBlocks.Register(modEventBus);
        LOOT_MODIFIER_SERIALIZERS.register(modEventBus);

        NeoForge.EVENT_BUS.register(this);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        //modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
    }

    /*
    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if(event.getTabKey() == CreativeModeTabs.NATURAL_BLOCKS) {
            event.accept(MAFItems.TOMATO_SEEDS);
        }
        if(event.getTabKey() == CreativeModeTabs.FOOD_AND_DRINKS) {
            event.accept(MAFItems.TOMATO);
        }
    }*/

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }
}
