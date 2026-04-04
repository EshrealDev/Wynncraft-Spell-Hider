package com.wynncraftspellhider.gui;

import com.wynncraftspellhider.models.particles.ParticleConfig;
import com.wynncraftspellhider.models.particles.ParticleRegistry;
import com.wynncraftspellhider.models.spells.SpellRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class ParticleHiderScreen extends BaseHiderScreen {

    private ParticleList particleList;

    public ParticleHiderScreen() {
        super(Component.literal("Particle Hider"));
    }

    @Override
    public void onClose() {
        if (particleList != null) GuiState.particleListScrollAmount = particleList.scrollAmount();
        super.onClose();
    }

    // =========================================================
    //  BaseHiderScreen hooks
    // =========================================================

    @Override
    protected void buildPageContent() {
        int listWidth = width - 16;
        particleList = new ParticleList(minecraft, listWidth, height - LIST_TOP - LIST_BOTTOM_OFFSET, LIST_TOP, 26);
        particleList.setX(8);
        addWidget(particleList);
        particleList.setScrollAmount(GuiState.particleListScrollAmount);
    }

    @Override
    protected void renderPageContent(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        particleList.render(guiGraphics, mouseX, mouseY, partialTick);

        if (ParticleRegistry.getParticles().isEmpty()) {
            guiGraphics.drawCenteredString(font,
                    Component.literal("No particles registered. Run dumpAllParticlesToClipboard() to get started."),
                    width / 2, height / 2, 0xFF666666);
        }
    }

    @Override
    protected void onClassTabClicked(SpellRegistry.WynnClass wynnClass) {
        if (particleList != null) GuiState.particleListScrollAmount = particleList.scrollAmount();
        GuiState.lastActiveTab = wynnClass;
        navigateTo(SpellHiderScreen::new);  // was: minecraft.setScreen(new SpellHiderScreen())
    }

    @Override protected boolean hasHideShowButtons() { return true; }

    @Override
    protected void hideAll() {
        for (ParticleConfig config : ParticleRegistry.getParticles()) config.hidden = true;
        if (particleList != null) particleList.rebuildEntries();
    }

    @Override
    protected void showAll() {
        for (ParticleConfig config : ParticleRegistry.getParticles()) config.hidden = false;
        if (particleList != null) particleList.rebuildEntries();
    }

    @Override protected SpellRegistry.WynnClass getActiveTab() { return null; }
    @Override protected int getActivePageIndex() { return 1; }

    @Override
    protected void saveScrollState() {
        if (particleList != null) GuiState.particleListScrollAmount = particleList.scrollAmount();
    }

    // =========================================================
    //  Particle list
    // =========================================================

    private class ParticleList extends ObjectSelectionList<ParticleList.ParticleEntry> {

        public ParticleList(Minecraft minecraft, int width, int height, int y, int itemHeight) {
            super(minecraft, width, height, y, itemHeight);
            rebuildEntries();
        }

        public void rebuildEntries() {
            clearEntries();
            for (ParticleConfig config : ParticleRegistry.getParticles()) addEntry(new ParticleEntry(config));
        }

        @Override public int getRowWidth() { return width - 6; }
        @Override protected int scrollBarX() { return getX() + width - 6; }

        public class ParticleEntry extends ObjectSelectionList.Entry<ParticleEntry> {
            private final ParticleConfig config;
            private final Button toggleButton;
            @Nullable private final Button infoButton;

            public ParticleEntry(ParticleConfig config) {
                this.config = config;

                this.toggleButton = Button.builder(
                                Component.literal(config.hidden ? "Show" : "Hide"),
                                btn -> {
                                    config.hidden = !config.hidden;
                                    btn.setMessage(Component.literal(config.hidden ? "Show" : "Hide"));
                                })
                        .size(40, 16).build();

                this.infoButton = config.description != null
                        ? Button.builder(Component.literal("?"),
                                btn -> openInfoModal(config.name, config.description))
                        .size(16, 16).build()
                        : null;
            }

            @Override
            public void renderContent(GuiGraphics guiGraphics, int mouseX, int mouseY,
                                      boolean hovered, float partialTick) {
                int left        = getX();
                int top         = getY();
                int entryWidth  = getWidth();
                int entryHeight = getHeight();
                int nameColor   = config.hidden ? 0xFF555555 : 0xFFFFFFFF;

                guiGraphics.drawString(ParticleHiderScreen.this.font, config.name,
                        left + 4, top + (entryHeight - 8) / 2, nameColor, false);

                int rightCursor = left + entryWidth - 14;

                toggleButton.setX(rightCursor - 40);
                toggleButton.setY(top + (entryHeight - 16) / 2);
                toggleButton.render(guiGraphics, mouseX, mouseY, partialTick);
                rightCursor -= 44;

                if (infoButton != null) {
                    infoButton.setX(rightCursor - 16);
                    infoButton.setY(top + (entryHeight - 16) / 2);
                    infoButton.render(guiGraphics, mouseX, mouseY, partialTick);
                }
            }

            @Override
            public boolean mouseClicked(MouseButtonEvent event, boolean bl) {
                double mx = event.x(), my = event.y();
                if (infoButton != null && infoButton.isMouseOver(mx, my)) { infoButton.mouseClicked(event, bl); return true; }
                if (toggleButton.isMouseOver(mx, my))                     { toggleButton.mouseClicked(event, bl); return true; }
                return super.mouseClicked(event, bl);
            }

            @Override
            public Component getNarration() { return Component.literal(config.name); }
        }
    }
}