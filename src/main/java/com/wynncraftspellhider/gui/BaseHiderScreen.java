package com.wynncraftspellhider.gui;

import com.wynncraftspellhider.models.Models;
import com.wynncraftspellhider.models.config.ProfileRegistry;
import com.wynncraftspellhider.models.spells.SpellRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Shared base for all WynncraftSpellHider screens.
 *
 * Nav bar layout:
 *   Row 1 (y=8,  h=20): [class tabs...] [Particles] [Config]   [Rebuild Cache]
 *   Row 2 (y=34, h=18): [Hide All] [Show All]   (only on screens that opt in)
 *
 * Modal supports two modes:
 *   - Info mode   (openInfoModal):    scrollable text, single Close button
 *   - Custom mode (openCustomModal):  arbitrary registered widgets inside the modal bounds;
 *                                     callers add their own buttons/inputs via addModalWidget()
 */
public abstract class BaseHiderScreen extends Screen {

    // =========================================================
    //  Layout constants — same values as the original screens
    // =========================================================

    protected static final int TAB_Y              = 8;
    protected static final int TAB_HEIGHT         = 20;
    protected static final int CONTROLS_Y         = 34;
    protected static final int CONTROLS_HEIGHT    = 18;
    protected static final int LIST_TOP           = 58;
    protected static final int LIST_BOTTOM_OFFSET = 30;

    protected static final int MODAL_WIDTH        = 320;
    protected static final int MODAL_HEIGHT       = 200;
    protected static final int MODAL_PADDING      = 12;
    protected static final int MODAL_TITLE_HEIGHT = 20;

    private static final int MODAL_CLOSE_HEIGHT  = 24;
    private static final int MODAL_SCROLL_HEIGHT =
            MODAL_HEIGHT - MODAL_TITLE_HEIGHT - MODAL_CLOSE_HEIGHT - MODAL_PADDING * 3;

    // Page-nav button widths
    private static final int PAGE_W_PARTICLES = 60;
    private static final int PAGE_W_CONFIG    = 50;

    // Left edges computed in buildNavBar() for underline rendering
    private int pageNavParticlesX;
    private int pageNavConfigX;

    // =========================================================
    //  Modal state
    // =========================================================

    @Nullable private String modalTitle = null;

    // Info-modal widgets
    @Nullable private ModalScrollList modalScrollList = null;
    @Nullable private Button modalCloseButton = null;

    // Custom-modal widgets — added by subclass via addModalWidget(), removed on closeModal()
    private final List<GuiEventListener> modalCustomWidgets = new ArrayList<>();

    // =========================================================
    //  Constructor
    // =========================================================

    protected BaseHiderScreen(Component title) {
        super(title);
    }

    // =========================================================
    //  Init
    // =========================================================

    @Override
    protected void init() {
        buildNavBar();
        buildPageContent();
    }

    private void buildNavBar() {
        int tabCount = SpellRegistry.WynnClass.values().length;
        int tabWidth = (width / 2) / tabCount;

        // --- Row 1 left: class tabs ---
        for (int i = 0; i < tabCount; i++) {
            SpellRegistry.WynnClass wynnClass = SpellRegistry.WynnClass.values()[i];
            int finalI = i;
            addRenderableWidget(Button.builder(
                            Component.literal(capitalize(wynnClass.name())),
                            btn -> onClassTabClicked(wynnClass))
                    .pos(8 + finalI * (tabWidth + 2), TAB_Y)
                    .size(tabWidth, TAB_HEIGHT)
                    .build());
        }

        // --- Row 1 middle: Particles · Config page-nav ---
        int x = 8 + tabCount * (tabWidth + 2) + 4;

        pageNavParticlesX = x;
        addRenderableWidget(Button.builder(Component.literal("Particles"),
                        btn -> navigateTo(ParticleHiderScreen::new))
                .pos(x, TAB_Y).size(PAGE_W_PARTICLES, TAB_HEIGHT).build());
        x += PAGE_W_PARTICLES + 4;

        pageNavConfigX = x;
        addRenderableWidget(Button.builder(Component.literal("Config"),
                        btn -> navigateTo(ConfigScreen::new))
                .pos(x, TAB_Y).size(PAGE_W_CONFIG, TAB_HEIGHT).build());

        // --- Row 1 right: Rebuild Cache ---
        addRenderableWidget(Button.builder(Component.literal("Rebuild Cache"),
                        btn -> { if (Models.texturepackModel != null) Models.texturepackModel.listResourcesAsync(); })
                .pos(width - 108, TAB_Y).size(100, TAB_HEIGHT).build());

        // --- Row 2: Hide All / Show All (left-aligned, only when screen opts in) ---
        if (hasHideShowButtons()) {
            addRenderableWidget(Button.builder(Component.literal("Hide All"), btn -> hideAll())
                    .pos(8, CONTROLS_Y).size(60, CONTROLS_HEIGHT).build());
            addRenderableWidget(Button.builder(Component.literal("Show All"), btn -> showAll())
                    .pos(72, CONTROLS_Y).size(60, CONTROLS_HEIGHT).build());
        }
    }

    protected void navigateTo(java.util.function.Supplier<Screen> factory) {
        saveScrollState();
        ProfileRegistry.saveActiveProfile();
        GuiState.lastScreenFactory = factory;
        minecraft.setScreen(factory.get());
    }

    // =========================================================
    //  Subclass hooks
    // =========================================================

    protected abstract void buildPageContent();
    protected abstract void renderPageContent(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick);
    protected abstract void onClassTabClicked(SpellRegistry.WynnClass wynnClass);

    protected boolean hasHideShowButtons() { return false; }
    protected void hideAll() {}
    protected void showAll() {}

    @Nullable
    protected SpellRegistry.WynnClass getActiveTab() { return null; }

    protected abstract int getActivePageIndex();

    protected void saveScrollState() {}

// =========================================================
//  Render — page widgets first, then modal on top
// =========================================================

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderPageContent(guiGraphics, mouseX, mouseY, partialTick);

        // Render all non-modal widgets via super (buttons, lists, sliders, etc.)
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        // Render modal background and widgets on top
        if (isModalOpen()) {
            renderModalBackground(guiGraphics);
            renderModalWidgets(guiGraphics, mouseX, mouseY, partialTick);
        }

        renderNavBarDecorations(guiGraphics);
    }

    private void renderModalBackground(GuiGraphics guiGraphics) {
        int modalX = (width  - MODAL_WIDTH)  / 2;
        int modalY = (height - MODAL_HEIGHT) / 2;

        guiGraphics.fill(0, 0, width, height, 0xAA000000);
        guiGraphics.fill(modalX, modalY, modalX + MODAL_WIDTH, modalY + MODAL_HEIGHT, 0xFF222222);
        guiGraphics.renderOutline(modalX, modalY, MODAL_WIDTH, MODAL_HEIGHT, 0xFFAAAAAA);

        guiGraphics.drawCenteredString(font, Component.literal(modalTitle),
                modalX + MODAL_WIDTH / 2, modalY + MODAL_PADDING, 0xFFFFFFFF);

        guiGraphics.fill(modalX + MODAL_PADDING,
                modalY + MODAL_TITLE_HEIGHT + MODAL_PADDING - 2,
                modalX + MODAL_WIDTH - MODAL_PADDING,
                modalY + MODAL_TITLE_HEIGHT + MODAL_PADDING - 1,
                0xFF555555);
    }

    private void renderModalWidgets(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (modalScrollList != null) modalScrollList.render(guiGraphics, mouseX, mouseY, partialTick);
        if (modalCloseButton != null) modalCloseButton.render(guiGraphics, mouseX, mouseY, partialTick);
        for (GuiEventListener widget : modalCustomWidgets) {
            if (widget instanceof net.minecraft.client.gui.components.Renderable r) {
                r.render(guiGraphics, mouseX, mouseY, partialTick);
            }
        }
    }

    private void renderNavBarDecorations(GuiGraphics guiGraphics) {
        SpellRegistry.WynnClass activeTab = getActiveTab();
        if (activeTab != null) {
            int tabCount = SpellRegistry.WynnClass.values().length;
            int tabWidth = (width / 2) / tabCount;
            int x = 8 + activeTab.ordinal() * (tabWidth + 2);
            guiGraphics.fill(x, TAB_Y + TAB_HEIGHT - 2, x + tabWidth, TAB_Y + TAB_HEIGHT, 0xFFFFFF00);
        }

        int pi = getActivePageIndex();
        if (pi == 1) {
            guiGraphics.fill(pageNavParticlesX, TAB_Y + TAB_HEIGHT - 2,
                    pageNavParticlesX + PAGE_W_PARTICLES, TAB_Y + TAB_HEIGHT, 0xFFFFFF00);
        } else if (pi == 2) {
            guiGraphics.fill(pageNavConfigX, TAB_Y + TAB_HEIGHT - 2,
                    pageNavConfigX + PAGE_W_CONFIG, TAB_Y + TAB_HEIGHT, 0xFFFFFF00);
        }
    }

    // =========================================================
//  children() — only expose modal widgets when modal is open
// =========================================================

    @Override
    public List<? extends GuiEventListener> children() {
        if (!isModalOpen()) return super.children();

        // Return only the modal widgets so ContainerEventHandler routes
        // clicks/focus/keyboard exclusively to them
        List<GuiEventListener> modalOnly = new ArrayList<>();
        if (modalScrollList != null)  modalOnly.add(modalScrollList);
        if (modalCloseButton != null) modalOnly.add(modalCloseButton);
        modalOnly.addAll(modalCustomWidgets);
        return modalOnly;
    }

    // =========================================================
    //  Modal — shared chrome
    // =========================================================

    protected boolean isModalOpen() {
        return modalTitle != null;
    }

    /** Returns the X of the modal's inner content area (left edge + padding). */
    protected int modalContentX() { return (width - MODAL_WIDTH) / 2 + MODAL_PADDING; }

    /** Returns the Y just below the modal title divider — where custom content starts. */
    protected int modalContentY() { return (height - MODAL_HEIGHT) / 2 + MODAL_TITLE_HEIGHT + MODAL_PADDING; }

    /** Returns the usable inner width (modal width minus both paddings). */
    protected int modalContentWidth() { return MODAL_WIDTH - MODAL_PADDING * 2; }

    /** Returns the Y of the bottom action row inside the modal. */
    protected int modalActionY() { return (height - MODAL_HEIGHT) / 2 + MODAL_HEIGHT - MODAL_PADDING - 20; }

    // =========================================================
    //  Modal — info mode (scrollable text + Close)
    // =========================================================

    protected void openInfoModal(String title, String description) {
        closeModal();
        modalTitle = title;

        int modalX     = (width - MODAL_WIDTH) / 2;
        int scrollY    = modalContentY();
        int innerWidth = MODAL_WIDTH - MODAL_PADDING * 2 - 12;

        List<FormattedCharSequence> wrappedLines = new ArrayList<>();
        for (String paragraph : description.split("\n")) {
            if (paragraph.isEmpty()) {
                wrappedLines.add(FormattedCharSequence.EMPTY);
            } else {
                wrappedLines.addAll(font.split(Component.literal(paragraph), innerWidth));
            }
        }

        modalScrollList = new ModalScrollList(minecraft, MODAL_WIDTH - MODAL_PADDING * 2,
                MODAL_SCROLL_HEIGHT, scrollY, wrappedLines);
        modalScrollList.setX(modalX + MODAL_PADDING);
        addRenderableWidget(modalScrollList);
        modalScrollList.setScrollAmount(0);

        modalCloseButton = Button.builder(Component.literal("Close"), btn -> closeModal())
                .pos(modalContentX(), modalActionY())
                .size(modalContentWidth(), 20)
                .build();
        addRenderableWidget(modalCloseButton);
    }

    // =========================================================
    //  Modal — custom mode (caller registers their own widgets)
    // =========================================================

    /**
     * Opens the modal chrome (background, title, divider) without adding any content widgets.
     * Call addModalWidget() after this to add buttons, edit boxes, etc.
     */
    protected void openCustomModal(String title) {
        closeModal();
        modalTitle = title;
    }

    /**
     * Registers a widget that lives inside the modal.
     * It will be removed automatically when closeModal() is called.
     * Use addRenderableWidget() for things that need to render and receive input,
     * but route through this so they get cleaned up properly.
     */
    protected <T extends GuiEventListener & net.minecraft.client.gui.components.Renderable & net.minecraft.client.gui.narration.NarratableEntry> T addModalWidget(T widget) {
        modalCustomWidgets.add(widget);
        return addRenderableWidget(widget);
    }

    protected void closeModal() {
        modalTitle = null;

        // Remove info-modal widgets
        removeIfNotNull(modalScrollList);
        removeIfNotNull(modalCloseButton);
        modalScrollList = null;
        modalCloseButton = null;

        // Remove custom-modal widgets
        for (GuiEventListener w : modalCustomWidgets) {
            removeWidget(w);
        }
        modalCustomWidgets.clear();
    }

    // =========================================================
    //  Input — route everything to modal when open
    // =========================================================

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean bl) {
        if (isModalOpen()) {
            double mx = event.x(), my = event.y();
            int modalX = (width  - MODAL_WIDTH)  / 2;
            int modalY = (height - MODAL_HEIGHT) / 2;
            if (mx >= modalX && mx <= modalX + MODAL_WIDTH
                    && my >= modalY && my <= modalY + MODAL_HEIGHT) {
                return super.mouseClicked(event, bl); // routes via filtered children()
            }
            return true; // swallow outside clicks
        }
        return super.mouseClicked(event, bl);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (isModalOpen()) {
            if (modalScrollList != null) modalScrollList.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean keyPressed(KeyEvent keyEvent) {
        if (isModalOpen()) {
            if (keyEvent.isEscape()) { closeModal(); return true; }
            return super.keyPressed(keyEvent); // routes via filtered children()
        }
        return super.keyPressed(keyEvent);
    }

    @Override
    public boolean charTyped(CharacterEvent characterEvent) {
        if (isModalOpen()) {
            return super.charTyped(characterEvent);
        }
        return super.charTyped(characterEvent);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        if (isModalOpen()) {
            if (getFocused() != null && isDragging() && event.button() == 0)
                return getFocused().mouseDragged(event, dragX, dragY);
            return true;
        }
        return super.mouseDragged(event, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (isModalOpen()) {
            setDragging(false);
            if (getFocused() != null) return getFocused().mouseReleased(event);
            return true;
        }
        return super.mouseReleased(event);
    }

    @Override
    public void onClose() {
        ProfileRegistry.saveActiveProfile();
        super.onClose();
    }

    @Override
    public boolean isPauseScreen() { return false; }

    // =========================================================
    //  Utilities
    // =========================================================

    protected void removeIfNotNull(GuiEventListener widget) {
        if (widget != null) removeWidget(widget);
    }

    protected String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.charAt(0) + s.substring(1).toLowerCase();
    }

    // =========================================================
    //  Info-modal scroll list
    // =========================================================

    private class ModalScrollList extends ObjectSelectionList<ModalScrollList.LineEntry> {

        public ModalScrollList(Minecraft minecraft, int width, int height, int y,
                               List<FormattedCharSequence> lines) {
            super(minecraft, width, height, y, 10);
            for (FormattedCharSequence line : lines) addEntry(new LineEntry(line));
        }

        @Override public int getRowWidth() { return width; }
        @Override protected int scrollBarX() { return getX() + width - 6; }

        public class LineEntry extends ObjectSelectionList.Entry<LineEntry> {
            private final FormattedCharSequence line;
            public LineEntry(FormattedCharSequence line) { this.line = line; }

            @Override
            public void renderContent(GuiGraphics guiGraphics, int mouseX, int mouseY,
                                      boolean hovered, float partialTick) {
                guiGraphics.drawString(font, line, getX() + 4, getY(), 0xFFCCCCCC, false);
            }

            @Override public boolean mouseClicked(MouseButtonEvent event, boolean bl) { return false; }
            @Override public Component getNarration() { return Component.empty(); }
        }
    }
}