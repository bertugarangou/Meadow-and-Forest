package net.carqui.meadowandforest;

import net.carqui.meadowandforest.block.MAFBlockEntities;
import net.carqui.meadowandforest.block.MAFDryingTrayRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
/**
 * Client-only mod entry point. Registers the config screen and runs client setup logging.
 * This class is never loaded on a dedicated server.
 */
@Mod(value = MAF.MOD_ID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = MAF.MOD_ID, value = Dist.CLIENT)
public class MAFClient {
    /**
     * Registers the mod's config screen factory so it shows up in the mods menu.
     */
    public MAFClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    /**
     * Runs during client setup. Currently just logs that setup has started.
     */
    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        // Some client setup code
        MAF.LOGGER.info("Beginning of client setup for Meadow and Forest");
    }

    /**
     * Registers the Drying Tray's block entity renderer.
     */
    @SubscribeEvent
    static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(MAFBlockEntities.DRYING_TRAY.get(), MAFDryingTrayRenderer::new);
    }
}
