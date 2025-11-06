package ca.maximilian.bluemap_viewer;

import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.networking.NetworkManager;
import eu.midnightdust.lib.config.MidnightConfig;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static ca.maximilian.bluemap_viewer.BluemapViewerConst.GET_SERVER_URL;

@Environment(EnvType.SERVER)
public class BluemapViewerServer {
    public static void init() {
        MidnightConfig.init(BluemapViewerConst.MOD_ID + "_server", BluemapViewerServerConfig.class);

        PlayerEvent.PLAYER_JOIN.register(player -> {
            MinecraftServer server = player.getServer();

            // Schedule the send 2 seconds later
            Executors.newSingleThreadScheduledExecutor().schedule(() -> {
                server.execute(() -> { // ensure we are on server thread
                    FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
                    String url = (BluemapViewerServerConfig.serverBluemapUrl != null)
                            ? BluemapViewerServerConfig.serverBluemapUrl
                            : "https://bluecolored.de/bluemap";
                    buf.writeUtf(url);
                    NetworkManager.sendToPlayer(player, GET_SERVER_URL, buf);
                });
            }, 2, TimeUnit.SECONDS); // 2 second delay
        });
    }
}
