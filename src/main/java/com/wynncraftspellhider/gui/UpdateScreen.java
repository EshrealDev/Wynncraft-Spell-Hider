package com.wynncraftspellhider.gui;

import com.wynncraftspellhider.WynncraftSpellHider;
import com.wynncraftspellhider.managers.NetworkManager;
import com.wynncraftspellhider.models.config.ProfileRegistry;
import java.net.URI;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class UpdateScreen extends Screen {
    private final Screen parent;
    private final String latestVersion;

    public UpdateScreen(Screen parent, String latestVersion) {
        super(Component.literal("Update Available"));
        this.parent = parent;
        this.latestVersion = latestVersion;
    }

    @Override
    protected void init() {
        super.init();

        // Download button
        this.addRenderableWidget(Button.builder(Component.literal("Download Update"), btn -> {
                    try {
                        net.minecraft.util.Util.getPlatform().openUri(new URI(NetworkManager.getUrl("download")));
                    } catch (Exception e) {
                        // silently fail if browser can't be opened
                    }
                })
                .pos(this.width / 2 - 155, this.height / 2 + 25)
                .size(150, 20)
                .build());

        // Dismiss button
        this.addRenderableWidget(Button.builder(Component.literal("Dismiss"), btn -> this.minecraft.setScreen(parent))
                .pos(this.width / 2 + 5, this.height / 2 + 25)
                .size(150, 20)
                .build());

        // Do Not Show Again button — saves the latest version to meta.json so
        // the screen won't reappear until an even newer version is released.
        this.addRenderableWidget(Button.builder(Component.literal("Do Not Show Again"), btn -> {
                    ProfileRegistry.dismissUpdate(latestVersion);
                    this.minecraft.setScreen(parent);
                })
                .pos(this.width / 2 - 75, this.height / 2 + 50)
                .size(150, 20)
                .build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        super.render(graphics, mouseX, mouseY, delta);

        graphics.drawCenteredString(
                this.font,
                "Wynncraft Spell Hider — Update Available!",
                this.width / 2,
                this.height / 2 - 50,
                0xFFFFFFFF);

        graphics.drawCenteredString(
                this.font,
                "Current version: " + orUnknown(WynncraftSpellHider.getCurrentVersion()),
                this.width / 2,
                this.height / 2 - 35,
                0xFFAAAAAA);

        graphics.drawCenteredString(
                this.font,
                "Latest version:  " + orUnknown(latestVersion),
                this.width / 2,
                this.height / 2 - 20,
                0xFFAAAAAA);

        graphics.drawCenteredString(
                this.font, "A new version is available on GitHub.", this.width / 2, this.height / 2 - 5, 0xFFAAAAAA);

        graphics.drawCenteredString(
                this.font,
                "Please contact Eshreal on discord if there is a problem.",
                this.width / 2,
                this.height / 2 + 10,
                0xFFAAAAAA);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        this.renderTransparentBackground(guiGraphics);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(parent);
    }

    private static String orUnknown(String s) {
        return s != null ? s : "unknown";
    }
}
