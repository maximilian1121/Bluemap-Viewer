package ca.maximilian.bluemap_viewer;

import com.cinemamod.mcef.MCEF;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class BrowserScreen extends Screen {

    public static Button changeUrlButton;

    public BrowserScreen() {
        super(Component.translatable("key.categories.bluemap_viewer"));
    }

    @Override
    protected void init() {
        super.init();
        // Create browser if it does not exist
        if (BluemapViewer.BROWSER == null) {
            String serverIp = minecraft.getCurrentServer().ip;
            List<String> serverUrls = new ArrayList<>(BluemapViewerConfig.serverUrls);
            boolean found = false;

            for (String checkingUrl : serverUrls) {
                if (checkingUrl == null) {
                    continue;
                }
                String checkingIp = checkingUrl.split("\\$")[0];
                String serverUrl = checkingUrl.substring(checkingUrl.indexOf('$') + 1);

                if (serverIp.equals(checkingIp)) {
                    BluemapViewer.URL = serverUrl;
                    found = true;
                }
            }

            // make user create server identity if not found
            if (!found) {
                minecraft.setScreen(new NewServerScreen());
            }

            // set up browser if found
            BluemapViewer.BROWSER = MCEF.createBrowser(BluemapViewer.URL, false);

            // set up change url button
            changeUrlButton = Button.builder(Component.translatable("bluemap_viewer.ui.string.changeUrl"), button -> {

                List<String> urls = new ArrayList<>(BluemapViewerConfig.serverUrls);
                urls.remove(minecraft.getCurrentServer().ip + "$" + BluemapViewer.URL);
                minecraft.setScreen(null);
                BluemapViewerConfig.serverUrls = new ArrayList<>(urls);
                BluemapViewerConfig.write(BluemapViewer.MOD_ID);

                minecraft.setScreen(new NewServerScreen());
            }).build();
            resizeBrowser();
        }
    }

    private int mouseX(double x) {
        return (int) ((x) * minecraft.getWindow().getGuiScale());
    }

    private int mouseY(double y) {
        return (int) ((y) * minecraft.getWindow().getGuiScale());
    }

    private int scaleX(double x) {
        return (int) ((x) * minecraft.getWindow().getGuiScale());
    }

    private int scaleY(double y) {
        return (int) ((y) * minecraft.getWindow().getGuiScale());
    }

    private void resizeBrowser() {
        changeUrlButton.setPosition(5, this.height - 25);
        if (width > 100 && height > 100 && BluemapViewer.BROWSER != null) {
            BluemapViewer.BROWSER.resize(scaleX(width), scaleY(height));
        }
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        super.resize(minecraft, width, height);
        resizeBrowser();
    }

    @Override
    public void onClose() {
        BluemapViewer.closeBrowser();
        super.onClose();
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics) {
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (BluemapViewer.BROWSER == null || BluemapViewer.BROWSER.getRenderer() == null) {
            return;
        }

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, BluemapViewer.BROWSER.getRenderer().getTextureID());

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

        buffer.vertex(0, height, 0).uv(0.0f, 1.0f).color(255, 255, 255, 255).endVertex();
        buffer.vertex(width, height, 0).uv(1.0f, 1.0f).color(255, 255, 255, 255).endVertex();
        buffer.vertex(width, 0, 0).uv(1.0f, 0.0f).color(255, 255, 255, 255).endVertex();
        buffer.vertex(0, 0, 0).uv(0.0f, 0.0f).color(255, 255, 255, 255).endVertex();

        BufferUploader.drawWithShader(buffer.end());
        RenderSystem.setShaderTexture(0, 0);
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();

        changeUrlButton.render(guiGraphics, mouseX, mouseY, partialTick);

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {

        if (changeUrlButton.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }

        if (BluemapViewer.BROWSER != null) {
            BluemapViewer.BROWSER.sendMousePress(mouseX(mouseX), mouseY(mouseY), button);
            BluemapViewer.BROWSER.setFocus(true);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (BluemapViewer.BROWSER != null) {
            BluemapViewer.BROWSER.sendMouseRelease(mouseX(mouseX), mouseY(mouseY), button);
            BluemapViewer.BROWSER.setFocus(true);
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        if (BluemapViewer.BROWSER != null) {
            BluemapViewer.BROWSER.sendMouseMove(mouseX(mouseX), mouseY(mouseY));
        }
        super.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (BluemapViewer.BROWSER != null) {
            BluemapViewer.BROWSER.sendMouseMove(mouseX(mouseX), mouseY(mouseY));
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (BluemapViewer.BROWSER != null) {
            BluemapViewer.BROWSER.sendMouseWheel(
                    (int) mouseX,
                    (int) mouseY,
                    delta,
                    0
            );
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (BluemapViewer.BROWSER != null) {
            BluemapViewer.BROWSER.sendKeyPress(keyCode, scanCode, modifiers);
            BluemapViewer.BROWSER.setFocus(true);
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (BluemapViewer.BROWSER != null) {
            BluemapViewer.BROWSER.sendKeyRelease(keyCode, scanCode, modifiers);
            BluemapViewer.BROWSER.setFocus(true);
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (codePoint == 0 || BluemapViewer.BROWSER == null) return false;
        BluemapViewer.BROWSER.sendKeyTyped(codePoint, modifiers);
        BluemapViewer.BROWSER.setFocus(true);
        return super.charTyped(codePoint, modifiers);
    }
}
