package com.wynncraftspellhider.gui;

import com.wynncraftspellhider.models.updatechecker.UpdateChecker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.components.Button;
import java.net.URI;

public class UpdateScreen extends Screen {
    private final Screen parent;

    public UpdateScreen(Screen parent) {
        super(Component.literal("Update Available"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        this.addRenderableWidget(Button.builder(
                        Component.literal("Download Update"),
                        btn -> {
                            try {
                                net.minecraft.util.Util.getPlatform().openUri(
                                        new java.net.URI(UpdateChecker.DOWNLOAD_URL)
                                );
                            } catch (Exception e) {
                                // silently fail if browser can't be opened
                            }
                        })
                .pos(this.width / 2 - 155, this.height / 2 + 25)
                .size(150, 20)
                .build()
        );

        // Dismiss button
        this.addRenderableWidget(Button.builder(
                        Component.literal("Dismiss"),
                        btn -> this.minecraft.setScreen(parent))
                .pos(this.width / 2 + 5, this.height / 2 + 25)
                .size(150, 20)
                .build()
        );
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        super.render(graphics, mouseX, mouseY, delta);

        graphics.drawCenteredString(
                this.font,
                "Wynncraft Spell Hider — Update Available!",
                this.width / 2,
                this.height / 2 - 45,
                0xFFFFFFFF
        );

        graphics.drawCenteredString(
                this.font,
                "Current version: " + UpdateChecker.currentVersion(),
                this.width / 2,
                this.height / 2 - 30,
                0xFFAAAAAA
        );

        graphics.drawCenteredString(
                this.font,
                "A new version is available on GitHub.",
                this.width / 2,
                this.height / 2 - 15,
                0xFFAAAAAA
        );

        graphics.drawCenteredString(
                this.font,
                "Please contact Eshreal on discord if there is a problem.",
                this.width / 2,
                this.height / 2,
                0xFFAAAAAA
        );
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
}