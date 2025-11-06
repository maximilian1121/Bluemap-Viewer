package ca.maximilian.bluemap_viewer;

import com.cinemamod.mcef.MCEFBrowser;
import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.networking.NetworkManager;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import eu.midnightdust.lib.config.MidnightConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

import static ca.maximilian.bluemap_viewer.BluemapViewerConst.GET_SERVER_URL;

@Environment(EnvType.CLIENT)
public final class BluemapViewer {
    public static final String MOD_ID = BluemapViewerConst.MOD_ID;
    public static MCEFBrowser BROWSER;
    public static String URL;
    public static KeyMapping BROWSER_KEY = new KeyMapping(
            "key.bluemap_viewer.open_browser",
            InputConstants.Type.KEYSYM,
            InputConstants.KEY_M,
            "key.categories.bluemap_viewer"
    );

    public static void closeBrowser() {
        if (!BluemapViewerConfig.keepBrowserInMemory && BROWSER != null) {
            BROWSER.close();
            BROWSER = null;
        }
    }

    public static void killBrowser() {
        if (BROWSER != null) {
            BROWSER.close();
            BROWSER = null;
        }
    }

    public static void init() {
        MidnightConfig.init(MOD_ID, BluemapViewerConfig.class);
        KeyMappingRegistry.register(BROWSER_KEY);

        ClientPlayerEvent.CLIENT_PLAYER_QUIT.register((LocalPlayer player) -> {
            killBrowser();
        });

        ClientPlayerEvent.CLIENT_PLAYER_JOIN.register((LocalPlayer player) -> {
            killBrowser();
        });

        ClientTickEvent.CLIENT_POST.register(minecraft -> {
            while (BROWSER_KEY.consumeClick()) {
                if (minecraft.level != null && minecraft.level.isClientSide && !minecraft.isSingleplayer()) {
                    minecraft.setScreen(new BrowserScreen());
                } else {
                    SystemToast.addOrUpdate(Minecraft.getInstance().getToasts(), SystemToast.SystemToastIds.PERIODIC_NOTIFICATION,
                            Component.translatable("bluemap_viewer.errors.multiplayer.title"), Component.translatable("bluemap_viewer.errors.multiplayer.desc"));
                }
            }
        });

        NetworkManager.registerReceiver(NetworkManager.Side.S2C, GET_SERVER_URL, (buf, context) -> {
            String url = buf.readUtf(32767);
            Minecraft minecraft = Minecraft.getInstance();
            String currentIp = minecraft.getCurrentServer().ip;

            List<String> serverUrls = new ArrayList<>(BluemapViewerConfig.serverUrls);
            boolean found = false;

            for (String checkingUrl : serverUrls) {
                if (checkingUrl.split("$").equals(currentIp)) {
                    found = true;
                }
            }

            if (!found) {
                serverUrls.add(currentIp + "$" + url);
                BluemapViewerConfig.serverUrls = new ArrayList<>(serverUrls);
                BluemapViewerConfig.write(BluemapViewer.MOD_ID);
            }
        });
    }
}
