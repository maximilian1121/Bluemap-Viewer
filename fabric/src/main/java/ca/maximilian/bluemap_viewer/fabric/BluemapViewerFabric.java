package ca.maximilian.bluemap_viewer.fabric;

import ca.maximilian.bluemap_viewer.BluemapViewer;
import ca.maximilian.bluemap_viewer.BluemapViewerServer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public final class BluemapViewerFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        boolean isServer = FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER;
        if (isServer) {
            BluemapViewerServer.init();
        } else {
            BluemapViewer.init();
        }
    }
}
