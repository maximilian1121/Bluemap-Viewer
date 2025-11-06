package ca.maximilian.bluemap_viewer;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class NewServerScreen extends Screen {
    private EditBox inputBox;
    private StringWidget label;

    protected NewServerScreen() {
        super(Component.literal("New Server"));
    }

    @Override
    protected void init() {
        super.init();

        BluemapViewer.closeBrowser();

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        label = new StringWidget(Component.translatable("bluemap_viewer.ui.string.enterurl"), font);
        label.setPosition(centerX - 100, centerY - 20);
        this.addRenderableWidget(label);

        inputBox = new EditBox(font, centerX - 100, centerY - 10, 200, 20, Component.translatable("bluemap_viewer.ui.string.enterurl"));
        inputBox.setMaxLength(Integer.MAX_VALUE);
        this.addRenderableWidget(inputBox);
    }

    @Override
    public void onClose() {
        BluemapViewer.closeBrowser();
        super.onClose();
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, delta);
        inputBox.render(guiGraphics, mouseX, mouseY, delta);
        label.render(guiGraphics, mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            super.onClose();
        }
        if (keyCode == GLFW.GLFW_KEY_ENTER) {
            List<String> urls = new ArrayList<>(BluemapViewerConfig.serverUrls);
            urls.add(minecraft.getCurrentServer().ip + "$" + inputBox.getValue());
            BluemapViewerConfig.serverUrls = new ArrayList<>(urls);
            BluemapViewerConfig.write(BluemapViewer.MOD_ID);
            if (BluemapViewer.BROWSER != null) {
                BluemapViewer.BROWSER.loadURL(inputBox.getValue());
                BluemapViewer.URL = inputBox.getValue();
            }
            minecraft.setScreen(new BrowserScreen());
        }
        if (inputBox.keyPressed(keyCode, scanCode, modifiers) || inputBox.canConsumeInput()) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}