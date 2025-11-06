package ca.maximilian.bluemap_viewer.forge;

import ca.maximilian.bluemap_viewer.BluemapViewerServer;
import dev.architectury.platform.forge.EventBuses;
import eu.midnightdust.lib.config.MidnightConfig;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import ca.maximilian.bluemap_viewer.BluemapViewer;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(BluemapViewer.MOD_ID)
public final class BluemapViewerForge {
    public BluemapViewerForge() {
        // Submit our event bus to let Architectury API register our content on the
        // right time.
        EventBuses.registerModEventBus(BluemapViewer.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());

        if (FMLEnvironment.dist == Dist.CLIENT) {
            BluemapViewer.init();
            ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> {
                return new ConfigScreenHandler.ConfigScreenFactory((client, parent) -> {
                    // This lambda returns the config screen instance
                    return MidnightConfig.getScreen(parent, BluemapViewer.MOD_ID);
                });
            });
        } else if (FMLEnvironment.dist == Dist.DEDICATED_SERVER) {
            BluemapViewerServer.init();
        }
    }
}
