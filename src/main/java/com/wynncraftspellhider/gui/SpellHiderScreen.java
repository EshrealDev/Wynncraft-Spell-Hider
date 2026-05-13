package com.wynncraftspellhider.gui;

import com.wynncraftspellhider.models.spells.SpellConfig;
import com.wynncraftspellhider.models.spells.SpellGroup;
import com.wynncraftspellhider.models.spells.SpellRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpellHiderScreen extends BaseHiderScreen {

    // --- Session state ---
    private SpellRegistry.WynnClass activeTab;
    private SpellList spellList;
    private @Nullable SpellConfig selectedSpell;
    private @Nullable SpellGroup selectedGroup;
    private @Nullable SpellGroup hoveredGroup;
    private boolean lockScale = GuiState.detailLockScale;
    private @Nullable DetailScrollList detailScrollList;
    private final Map<SpellGroup, Button> groupInfoButtons = new HashMap<>();

    private static final int LIST_WIDTH_FRAC = 3;
    private static final int PANEL_PAD = 10;

    // =========================================================
    //  Constructor
    // =========================================================

    public SpellHiderScreen() {
        super(Component.literal("Spell Hider"));
        activeTab     = GuiState.lastActiveTab;
        selectedSpell = GuiState.lastSelectedSpell;
        selectedGroup = GuiState.lastSelectedGroup;
    }

    @Override
    public void onClose() {
        GuiState.lastActiveTab     = activeTab;
        GuiState.lastSelectedSpell = selectedSpell;
        GuiState.lastSelectedGroup = selectedGroup;
        saveListScroll();
        saveDetailScroll();
        super.onClose();
    }

    // --- Layout helpers ---

    private int listRight()   { return 8 + width / LIST_WIDTH_FRAC + PANEL_PAD; }
    private int panelWidth()  { return width - listRight() - PANEL_PAD; }

    // --- Selection helpers ---

    private void clearSelection() {
        selectedSpell = null;
        selectedGroup = null;
        GuiState.lastSelectedSpell = null;
        GuiState.lastSelectedGroup = null;
    }

    private void selectSpell(@Nullable SpellConfig spell) {
        saveDetailScroll();
        if (selectedSpell == spell) {
            clearSelection();
        } else {
            selectedSpell = spell;
            selectedGroup = (spell != null && !spell.groups.isEmpty()) ? spell.groups.getFirst() : null;
            GuiState.lastSelectedSpell = selectedSpell;
            GuiState.lastSelectedGroup = selectedGroup;
        }
        rebuildDetailWidgets();
    }

    private void selectGroup(SpellGroup group) {
        saveDetailScroll();
        selectedGroup = group;
        GuiState.lastSelectedGroup = group;
        rebuildDetailWidgets();
    }

    private void saveListScroll() {
        if (spellList != null) GuiState.spellListScrollAmounts.put(activeTab, spellList.scrollAmount());
    }

    private void saveDetailScroll() {
        if (detailScrollList != null && selectedGroup != null) {
            GuiState.detailScrollAmounts.put(selectedGroup, detailScrollList.scrollAmount());
        }
    }

    // =========================================================
    //  BaseHiderScreen hooks
    // =========================================================

    @Override
    protected void buildPageContent() {
        int listW = width / LIST_WIDTH_FRAC;
        spellList = new SpellList(minecraft, listW, height - LIST_TOP - LIST_BOTTOM_OFFSET, LIST_TOP, 26);
        spellList.setX(8);
        addWidget(spellList);
        spellList.setScrollAmount(GuiState.spellListScrollAmounts.getOrDefault(activeTab, 0.0));

        if (selectedSpell != null && !SpellRegistry.getSpells(activeTab).contains(selectedSpell)) {
            clearSelection();
        }
        rebuildDetailWidgets();
    }

    @Override
    protected void renderPageContent(GuiGraphics g, int mouseX, int mouseY, float pt) {
        int lr = listRight(), pw = panelWidth();
        int top = LIST_TOP, bot = height - LIST_BOTTOM_OFFSET;

        if (selectedSpell != null) {
            g.fill(lr, top, lr + pw, bot, 0x88000000);
            g.drawString(font, selectedSpell.name, lr + 8, top + 8, 0xFFFFFFFF, false);
            g.drawString(font, "Groups: (click to edit)", lr + 8, top + 20, 0xFFAAAAAA, false);
            renderGroupRows(g, mouseX, mouseY, pt, lr + 8, lr + pw - 8, top + 32);
            if (detailScrollList != null) detailScrollList.render(g, mouseX, mouseY, pt);
        } else {
            g.drawCenteredString(font, Component.literal("Select a spell to customize"),
                    lr + pw / 2, top + (bot - top) / 2, 0xFF666666);
        }

        spellList.render(g, mouseX, mouseY, pt);
    }

    private void renderGroupRows(GuiGraphics g, int mx, int my, float pt, int left, int right, int y) {
        hoveredGroup = null;
        for (SpellGroup group : selectedSpell.groups) {
            boolean selected = group == selectedGroup;
            boolean hovered  = mx >= left && mx <= right && my >= y - 1 && my <= y + 9;
            if (hovered) hoveredGroup = group;

            int color = selected ? 0xFFFFFF00
                    : hovered  ? 0xFFCCCCCC
                    : group.hidden ? 0xFF555555
                    : 0xFFAAAAAA;

            String text = (selected ? "> " : "  ") + "- " + group.name
                    + (group.hidden ? " [hidden]" : "");
            g.drawString(font, text, left, y, color, false);

            Button infoBtn = groupInfoButtons.get(group);
            if (infoBtn != null) {
                infoBtn.setX(left + font.width(text) + 2);
                infoBtn.setY(y - 2);
                infoBtn.render(g, mx, my, pt);
            }
            y += 12;
        }
    }

    @Override
    protected void onClassTabClicked(SpellRegistry.WynnClass wynnClass) {
        saveListScroll();
        saveDetailScroll();
        activeTab = wynnClass;
        GuiState.lastActiveTab = wynnClass;
        clearSelection();
        rebuildWidgets();
    }

    @Override protected boolean hasHideShowButtons() { return true; }

    @Override
    protected void hideAll() {
        SpellRegistry.getSpells(activeTab).forEach(s -> s.groups.forEach(g -> g.hidden = true));
        rebuildWidgets();
    }

    @Override
    protected void showAll() {
        SpellRegistry.getSpells(activeTab).forEach(s -> s.groups.forEach(g -> g.hidden = false));
        rebuildWidgets();
    }

    @Override protected SpellRegistry.WynnClass getActiveTab() { return activeTab; }
    @Override protected int getActivePageIndex() { return 0; }
    @Override protected void saveScrollState() { saveListScroll(); saveDetailScroll(); }

    // =========================================================
    //  Detail panel
    // =========================================================

    private void rebuildDetailWidgets() {
        if (detailScrollList != null) removeWidget(detailScrollList);
        detailScrollList = null;
        groupInfoButtons.clear();

        if (selectedSpell == null || selectedGroup == null) return;

        // Create [?] buttons for groups that have descriptions
        for (SpellGroup group : selectedSpell.groups) {
            if (group.description != null) {
                groupInfoButtons.put(
                        group,
                        Button.builder(Component.literal("[?]"),
                                        b -> openInfoModal(group.name, group.description))
                                .size(22, 12).build()
                );
            }
        }

        int lr = listRight(), pw = panelWidth();
        int groupH = selectedSpell.groups.size() * 12;
        int sTop = LIST_TOP + 32 + groupH + 4;
        int sH   = height - LIST_BOTTOM_OFFSET - sTop;
        if (sH < 20) return;

        detailScrollList = new DetailScrollList(minecraft, pw, sH, sTop, lr, selectedGroup, pw - 16);

        // Restore scroll position for this group if we have one
        double savedScroll = GuiState.detailScrollAmounts.getOrDefault(selectedGroup, 0.0);
        detailScrollList.setScrollAmount(savedScroll);

        addWidget(detailScrollList);
    }

    // =========================================================
    //  Input
    // =========================================================

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean bl) {
        if (isModalOpen()) return super.mouseClicked(event, bl);

        if (selectedSpell != null) {
            for (Button btn : groupInfoButtons.values()) {
                if (btn.isMouseOver(event.x(), event.y())) {
                    btn.mouseClicked(event, bl);
                    return true;
                }
            }
            if (hoveredGroup != null) {
                selectGroup(hoveredGroup);
                return true;
            }
        }

        if (detailScrollList != null && detailScrollList.mouseClicked(event, bl)) return true;
        return super.mouseClicked(event, bl);
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double h, double v) {
        if (isModalOpen()) return super.mouseScrolled(mx, my, h, v);
        if (mx >= listRight() && detailScrollList != null) return detailScrollList.mouseScrolled(mx, my, h, v);
        return super.mouseScrolled(mx, my, h, v);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double mx, double my) {
        if (detailScrollList != null && detailScrollList.mouseDragged(event, mx, my)) return true;
        return super.mouseDragged(event, mx, my);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (detailScrollList != null) detailScrollList.mouseReleased(event);
        return super.mouseReleased(event);
    }

    // =========================================================
    //  DetailScrollList
    // =========================================================

    private class DetailScrollList extends ObjectSelectionList<DetailScrollList.Entry> {

        private final Entry entry;

        DetailScrollList(Minecraft mc, int w, int h, int y, int x, SpellGroup group, int innerW) {
            super(mc, w, h, y, 200);
            setX(x);
            entry = new Entry(group, innerW);
            addEntry(entry);
        }

        @Override public int getRowWidth()                          { return width - 6; }
        @Override protected int scrollBarX()                        { return getX() + width - 6; }
        @Override protected double scrollRate()                     { return 10.0; }
        @Override protected boolean entriesCanBeSelected()          { return false; }

        @Override
        public boolean mouseClicked(MouseButtonEvent event, boolean bl) {
            if (event.x() >= scrollBarX() && event.x() <= scrollBarX() + 6) return super.mouseClicked(event, bl);
            Entry e = getEntryAtPosition(event.x(), event.y());
            if (e != null && e.mouseClicked(event, bl)) return true;
            return super.mouseClicked(event, bl);
        }

        @Override
        public boolean mouseDragged(MouseButtonEvent event, double mx, double my) {
            return entry.mouseDragged(event, mx, my) || super.mouseDragged(event, mx, my);
        }

        @Override
        public boolean mouseReleased(MouseButtonEvent event) {
            return entry.mouseReleased(event) || super.mouseReleased(event);
        }

        // =========================================================
        //  Entry
        // =========================================================

        class Entry extends ObjectSelectionList.Entry<Entry> {

            private final SpellGroup group;
            private final List<AbstractWidget> widgets = new ArrayList<>();
            private final Button hideBtn, resetBtn, lockBtn;
            private final Slider scaleX, scaleY, scaleZ, offX, offY, offZ, transparency;
            private @Nullable Slider dragged;

            private static final int PAD = 8, LOCK_W = 26;
            private static final int LBL_GAP = 14, STEP = 25, SEC_GAP = 10;
            private final int totalHeight;

            Entry(SpellGroup group, int innerW) {
                this.group = group;
                int sliderW = innerW - LOCK_W - 4 - PAD;

                // --- Buttons ---
                hideBtn = Button.builder(hideLabel(), btn -> {
                    group.hidden = !group.hidden;
                    btn.setMessage(hideLabel());
                }).size(innerW - 6, 20).build();
                widgets.add(hideBtn);

                resetBtn = Button.builder(
                        Component.literal("Reset"),
                        btn -> resetGroup()
                ).size(innerW - 6, 20).build();
                widgets.add(resetBtn);

                // --- Scale sliders ---
                Slider[] sx = {null}, sy = {null}, sz = {null};

                sx[0] = new Slider(0, 0, sliderW, "Scale X", group.scaleX, val -> {
                    group.scaleX = val;
                    if (lockScale) { group.scaleY = val; group.scaleZ = val; sy[0].forceValue(val); sz[0].forceValue(val); }
                });
                sy[0] = new Slider(0, 0, sliderW, "Scale Y", group.scaleY, val -> {
                    group.scaleY = val;
                    if (lockScale) { group.scaleX = val; group.scaleZ = val; sx[0].forceValue(val); sz[0].forceValue(val); }
                });
                sz[0] = new Slider(0, 0, sliderW, "Scale Z", group.scaleZ, val -> {
                    group.scaleZ = val;
                    if (lockScale) { group.scaleX = val; group.scaleY = val; sx[0].forceValue(val); sy[0].forceValue(val); }
                });
                scaleX = sx[0]; scaleY = sy[0]; scaleZ = sz[0];
                widgets.add(scaleX); widgets.add(scaleY); widgets.add(scaleZ);

                lockBtn = Button.builder(Component.literal(lockScale ? "🔒" : "🔓"), btn -> {
                    lockScale = !lockScale;
                    GuiState.detailLockScale = lockScale;
                    btn.setMessage(Component.literal(lockScale ? "🔒" : "🔓"));
                    if (lockScale) {
                        group.scaleY = group.scaleX; group.scaleZ = group.scaleX;
                        scaleY.forceValue(group.scaleX); scaleZ.forceValue(group.scaleX);
                    }
                }).size(LOCK_W, 50).build();
                widgets.add(lockBtn);

                // -- Offset sliders ---
                offX = new Slider(0, 0, sliderW, "Offset X", group.offsetX, -3f, 3f, val -> group.offsetX = val);
                offY = new Slider(0, 0, sliderW, "Offset Y", group.offsetY, -3f, 3f, val -> group.offsetY = val);
                offZ = new Slider(0, 0, sliderW, "Offset Z", group.offsetZ, -3f, 3f, val -> group.offsetZ = val);
                widgets.add(offX); widgets.add(offY); widgets.add(offZ);

                // --- Transparency ---
                transparency = new Slider(0, 0, sliderW, "Transparency", group.transparency, 0f, 1f, val -> group.transparency = val);
                widgets.add(transparency);

                // Compute total height (must match renderContent layout)
                int sY0 = 44 + LBL_GAP + 4;
                int oY0 = sY0 + STEP * 3 + LBL_GAP + SEC_GAP;
                int tY0 = oY0 + STEP * 3 + LBL_GAP + SEC_GAP;
                totalHeight = tY0 + 20 + PAD;
            }

            private Component hideLabel() {
                return Component.literal(group.hidden ? "Show \"" + group.name + "\"" : "Hide \"" + group.name + "\"");
            }

            private void resetGroup() {
                saveDetailScroll();
                group.hidden      = SpellGroup.Defaults.HIDDEN;
                group.scaleX      = SpellGroup.Defaults.SCALE_X;
                group.scaleY      = SpellGroup.Defaults.SCALE_Y;
                group.scaleZ      = SpellGroup.Defaults.SCALE_Z;
                group.offsetX     = SpellGroup.Defaults.OFFSET_X;
                group.offsetY     = SpellGroup.Defaults.OFFSET_Y;
                group.offsetZ     = SpellGroup.Defaults.OFFSET_Z;
                group.transparency = SpellGroup.Defaults.TRANSPARENCY;
                rebuildDetailWidgets();
            }

            @Override public int getHeight() { return totalHeight; }

            @Override
            public void renderContent(GuiGraphics g, int mx, int my, boolean hov, float pt) {
                int x = getX() + PAD, y = getY();

                // Buttons
                hideBtn.setX(x);  hideBtn.setY(y);
                resetBtn.setX(x); resetBtn.setY(y + 24);

                // Scale section
                int sY = y + 44 + LBL_GAP + 4;
                g.drawString(font, "Scale:", x, sY - LBL_GAP, 0xFFAAAAAA, false);
                scaleX.setX(x); scaleX.setY(sY);
                scaleY.setX(x); scaleY.setY(sY + STEP);
                scaleZ.setX(x); scaleZ.setY(sY + STEP * 2);
                lockBtn.setX(x + scaleX.getWidth() + 4); lockBtn.setY(sY + 12);

                // Offset section
                int oY = sY + STEP * 3 + LBL_GAP + SEC_GAP;
                g.drawString(font, "Offset:", x, oY - LBL_GAP, 0xFFAAAAAA, false);
                offX.setX(x); offX.setY(oY);
                offY.setX(x); offY.setY(oY + STEP);
                offZ.setX(x); offZ.setY(oY + STEP * 2);

                // Transparency section
                int tY = oY + STEP * 3 + LBL_GAP + SEC_GAP;
                g.drawString(font, "Transparency:", x, tY - LBL_GAP, 0xFFAAAAAA, false);
                transparency.setX(x); transparency.setY(tY);

                for (AbstractWidget w : widgets) w.render(g, mx, my, pt);
            }

            @Override
            public boolean mouseClicked(MouseButtonEvent event, boolean bl) {
                double mx = event.x(), my = event.y();
                for (AbstractWidget w : widgets) {
                    if (w.isMouseOver(mx, my)) {
                        w.mouseClicked(event, bl);
                        if (w instanceof Slider s) dragged = s;
                        return true;
                    }
                }
                return false;
            }

            @Override
            public boolean mouseDragged(MouseButtonEvent event, double mx, double my) {
                if (dragged != null) { dragged.mouseDragged(event, mx, my); return true; }
                return false;
            }

            @Override
            public boolean mouseReleased(MouseButtonEvent event) {
                if (dragged != null) { dragged.onRelease(event); dragged = null; return true; }
                return false;
            }

            @Override public Component getNarration() { return Component.literal("Controls for " + group.name); }
        }
    }

    // =========================================================
    //  Spell List
    // =========================================================

    private class SpellList extends ObjectSelectionList<SpellList.SpellEntry> {

        SpellList(Minecraft mc, int w, int h, int y, int ih) {
            super(mc, w, h, y, ih);
            rebuildEntries();
        }

        void rebuildEntries() {
            clearEntries();
            for (SpellConfig s : SpellRegistry.getSpells(activeTab)) addEntry(new SpellEntry(s));
        }

        @Override public int getRowWidth()                         { return width - 6; }
        @Override protected int scrollBarX()                       { return getX() + width - 6; }
        @Override protected boolean entriesCanBeSelected()         { return false; }

        class SpellEntry extends ObjectSelectionList.Entry<SpellEntry> {

            private final SpellConfig spell;
            private final Button toggleBtn, editBtn;
            private final @Nullable Button infoBtn;

            SpellEntry(SpellConfig spell) {
                this.spell = spell;

                toggleBtn = Button.builder(Component.literal(allHidden() ? "Show" : "Hide"), btn -> {
                    boolean hide = !allHidden();
                    for (SpellGroup g : spell.groups) g.hidden = hide;
                    btn.setMessage(Component.literal(allHidden() ? "Show" : "Hide"));
                }).size(40, 16).build();

                editBtn = Button.builder(Component.literal("Edit"),
                        btn -> selectSpell(spell)).size(30, 16).build();

                infoBtn = spell.description != null
                        ? Button.builder(Component.literal("?"),
                                btn -> openInfoModal(spell.name, spell.description))
                        .size(16, 16).build()
                        : null;
            }

            private boolean allHidden() { return spell.groups.stream().allMatch(g -> g.hidden); }

            @Override
            public void renderContent(GuiGraphics g, int mx, int my, boolean hov, float pt) {
                int l = getX(), t = getY(), w = getWidth(), h = getHeight();
                g.drawString(font, spell.name, l + 4, t + (h - 8) / 2,
                        allHidden() ? 0xFF555555 : 0xFFFFFFFF, false);

                int c = l + w - 14;
                editBtn.setX(c - 30);   editBtn.setY(t + (h - 16) / 2); editBtn.render(g, mx, my, pt);   c -= 34;
                toggleBtn.setX(c - 40); toggleBtn.setY(t + (h - 16) / 2); toggleBtn.render(g, mx, my, pt); c -= 44;
                if (infoBtn != null) {
                    infoBtn.setX(c - 16); infoBtn.setY(t + (h - 16) / 2); infoBtn.render(g, mx, my, pt);
                }
            }

            @Override
            public boolean mouseClicked(MouseButtonEvent event, boolean bl) {
                double mx = event.x(), my = event.y();
                if (infoBtn != null && infoBtn.isMouseOver(mx, my))   { infoBtn.mouseClicked(event, bl);   return true; }
                if (toggleBtn.isMouseOver(mx, my))                     { toggleBtn.mouseClicked(event, bl); return true; }
                if (editBtn.isMouseOver(mx, my))                       { editBtn.mouseClicked(event, bl);   return true; }
                return false;
            }

            @Override public Component getNarration() { return Component.literal(spell.name); }
        }
    }
}