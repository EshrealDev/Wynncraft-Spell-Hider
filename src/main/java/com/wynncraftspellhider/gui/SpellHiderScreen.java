package com.wynncraftspellhider.gui;

import com.wynncraftspellhider.models.spells.SpellConfig;
import com.wynncraftspellhider.models.spells.SpellGroup;
import com.wynncraftspellhider.models.spells.SpellRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class SpellHiderScreen extends BaseHiderScreen {

    // --- Session state ---
    private SpellRegistry.WynnClass activeTab;
    private SpellList spellList;
    private SpellConfig selectedSpell;
    private SpellGroup selectedGroup;
    private SpellGroup hoveredGroup = null;

    // --- Scale sliders ---
    private ScaleSlider scaleSliderX, scaleSliderY, scaleSliderZ;
    private Button scaleLockButton;
    private boolean lockScale = true;

    // --- Offset sliders ---
    private ScaleSlider offsetSliderX, offsetSliderY, offsetSliderZ;

    // --- Transparency slider ---
    private ScaleSlider alphaSlider;

    // --- Detail buttons ---
    private Button groupHideButton;
    private Button groupResetButton;

    private static final int LIST_WIDTH_FRACTION  = 3;
    private static final int DETAIL_PANEL_PADDING = 10;

    // =========================================================
    //  Constructor
    // =========================================================

    public SpellHiderScreen() {
        super(Component.literal("Spell Hider"));
        this.activeTab     = GuiState.lastActiveTab;
        this.selectedSpell = GuiState.lastSelectedSpell;
        this.selectedGroup = GuiState.lastSelectedGroup;
    }

    @Override
    public void onClose() {
        GuiState.lastActiveTab     = activeTab;
        GuiState.lastSelectedSpell = selectedSpell;
        GuiState.lastSelectedGroup = selectedGroup;
        if (spellList != null) GuiState.spellListScrollAmounts.put(activeTab, spellList.scrollAmount());
        super.onClose();
    }

    // =========================================================
    //  BaseHiderScreen hooks
    // =========================================================

    @Override
    protected void buildPageContent() {
        int listWidth = width / LIST_WIDTH_FRACTION;
        spellList = new SpellList(minecraft, listWidth, height - LIST_TOP - LIST_BOTTOM_OFFSET, LIST_TOP, 26);
        spellList.setX(8);
        addWidget(spellList);
        spellList.setScrollAmount(GuiState.spellListScrollAmounts.getOrDefault(activeTab, 0.0));

        if (selectedSpell != null && !SpellRegistry.getSpells(activeTab).contains(selectedSpell)) {
            selectedSpell = null;
            selectedGroup = null;
        }

        rebuildDetailWidgets();
    }

    @Override
    protected void renderPageContent(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        int listRight  = 8 + width / LIST_WIDTH_FRACTION + DETAIL_PANEL_PADDING;
        int panelTop   = LIST_TOP;
        int panelWidth = width - listRight - DETAIL_PANEL_PADDING;

        if (selectedSpell != null) {
            guiGraphics.fill(listRight, panelTop, listRight + panelWidth, height - LIST_BOTTOM_OFFSET, 0x88000000);
        }

        if (selectedSpell != null) {
            guiGraphics.drawString(font, selectedSpell.name, listRight + 8, panelTop + 8, 0xFFFFFFFF, false);
            guiGraphics.drawString(font, "Groups: (click to edit)", listRight + 8, panelTop + 20, 0xFFAAAAAA, false);

            int gy = panelTop + 32;
            hoveredGroup = null;
            for (SpellGroup group : selectedSpell.groups) {
                boolean isSelected = selectedGroup == group;
                int rowLeft  = listRight + 8;
                int rowRight = listRight + panelWidth - 8;
                boolean isHovered = mouseX >= rowLeft && mouseX <= rowRight
                        && mouseY >= gy - 1 && mouseY <= gy + 9;
                if (isHovered) hoveredGroup = group;

                int color;
                if (isSelected)     color = 0xFFFFFF00;
                else if (isHovered) color = 0xFFCCCCCC;
                else if (group.hidden) color = 0xFF555555;
                else                color = 0xFFAAAAAA;

                String prefix = isSelected ? "> " : "  ";
                String suffix = group.description != null ? " [?]" : "";
                guiGraphics.drawString(font, prefix + "- " + group.name
                        + (group.hidden ? " [hidden]" : "") + suffix, rowLeft, gy, color, false);
                gy += 12;
            }

            if (selectedGroup != null) {
                int baseY        = LIST_TOP + 32 + (selectedSpell.groups.size() * 12) + 4;
                int scaleStartY  = baseY + 58;
                int offsetStartY = scaleStartY + 95;
                int alphaStartY  = offsetStartY + 95;
                guiGraphics.drawString(font, "Scale:",  listRight + 8, scaleStartY  - 10, 0xFFAAAAAA, false);
                guiGraphics.drawString(font, "Offset:", listRight + 8, offsetStartY - 10, 0xFFAAAAAA, false);
                guiGraphics.drawString(font, "Transparency:", listRight + 8, alphaStartY  - 10, 0xFFAAAAAA, false);
            }
        } else {
            int centerX = listRight + panelWidth / 2;
            int centerY = panelTop + (height - LIST_BOTTOM_OFFSET - panelTop) / 2;
            guiGraphics.drawCenteredString(font, Component.literal("Select a spell to customize"),
                    centerX, centerY, 0xFF666666);
        }

        spellList.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    protected void onClassTabClicked(SpellRegistry.WynnClass wynnClass) {
        if (spellList != null) GuiState.spellListScrollAmounts.put(activeTab, spellList.scrollAmount());

        activeTab = wynnClass;
        GuiState.lastActiveTab = wynnClass;
        selectedSpell = null;
        selectedGroup = null;
        GuiState.lastSelectedSpell = null;
        GuiState.lastSelectedGroup = null;
        rebuildWidgets();
    }

    @Override protected boolean hasHideShowButtons() { return true; }

    @Override
    protected void hideAll() {
        for (SpellConfig spell : SpellRegistry.getSpells(activeTab))
            for (SpellGroup group : spell.groups) group.hidden = true;
        rebuildWidgets();
    }

    @Override
    protected void showAll() {
        for (SpellConfig spell : SpellRegistry.getSpells(activeTab))
            for (SpellGroup group : spell.groups) group.hidden = false;
        rebuildWidgets();
    }

    @Override
    protected SpellRegistry.WynnClass getActiveTab() { return activeTab; }

    @Override protected int getActivePageIndex() { return 0; }

    @Override
    protected void saveScrollState() {
        if (spellList != null) GuiState.spellListScrollAmounts.put(activeTab, spellList.scrollAmount());
    }

    // =========================================================
    //  Detail panel
    // =========================================================

    private void rebuildDetailWidgets() {
        removeIfNotNull(scaleSliderX);
        removeIfNotNull(scaleSliderY);
        removeIfNotNull(scaleSliderZ);
        removeIfNotNull(scaleLockButton);
        removeIfNotNull(offsetSliderX);
        removeIfNotNull(offsetSliderY);
        removeIfNotNull(offsetSliderZ);
        removeIfNotNull(alphaSlider);
        removeIfNotNull(groupHideButton);
        removeIfNotNull(groupResetButton);
        scaleSliderX = scaleSliderY = scaleSliderZ = null;
        offsetSliderX = offsetSliderY = offsetSliderZ = null;
        alphaSlider = null;
        scaleLockButton = null;
        groupHideButton = null;
        groupResetButton = null;

        if (selectedSpell == null || selectedGroup == null) return;

        int listRight    = 8 + width / LIST_WIDTH_FRACTION + DETAIL_PANEL_PADDING;
        int panelWidth   = width - listRight - DETAIL_PANEL_PADDING;
        int lockBtnWidth = 26;
        int sliderWidth  = panelWidth - 16 - lockBtnWidth - 4;
        int sliderXPos   = listRight + 8;
        int lockBtnX     = sliderXPos + sliderWidth + 4;
        int baseY        = LIST_TOP + 32 + (selectedSpell.groups.size() * 12) + 4;

        groupHideButton = Button.builder(
                        Component.literal(selectedGroup.hidden
                                ? "Show \"" + selectedGroup.name + "\""
                                : "Hide \"" + selectedGroup.name + "\""),
                        btn -> {
                            selectedGroup.hidden = !selectedGroup.hidden;
                            btn.setMessage(Component.literal(selectedGroup.hidden
                                    ? "Show \"" + selectedGroup.name + "\""
                                    : "Hide \"" + selectedGroup.name + "\""));
                        })
                .pos(sliderXPos, baseY)
                .size(sliderWidth + lockBtnWidth + 4, 20)
                .build();

        groupResetButton = Button.builder(
                        Component.literal("Reset"),
                        btn -> {
                            selectedGroup.hidden  = false;
                            selectedGroup.scaleX  = 1f;
                            selectedGroup.scaleY  = 1f;
                            selectedGroup.scaleZ  = 1f;
                            selectedGroup.offsetX = 0f;
                            selectedGroup.offsetY = 0f;
                            selectedGroup.offsetZ = 0f;
                            selectedGroup.alpha   = 1f;
                            if (groupHideButton != null)
                                groupHideButton.setMessage(Component.literal("Hide \"" + selectedGroup.name + "\""));
                            rebuildDetailWidgets();
                        })
                .pos(sliderXPos, baseY + 24)
                .size(sliderWidth + lockBtnWidth + 4, 20)
                .build();

        int scaleStartY  = baseY + 70;
        int offsetStartY = scaleStartY + 95;

        scaleSliderX = new ScaleSlider(sliderXPos, scaleStartY, sliderWidth, "Scale X",
                selectedGroup.scaleX, val -> {
            selectedGroup.scaleX = val;
            if (lockScale) {
                selectedGroup.scaleY = val; selectedGroup.scaleZ = val;
                if (scaleSliderY != null) scaleSliderY.forceValue(val);
                if (scaleSliderZ != null) scaleSliderZ.forceValue(val);
            }
        });
        scaleSliderY = new ScaleSlider(sliderXPos, scaleStartY + 25, sliderWidth, "Scale Y",
                selectedGroup.scaleY, val -> {
            selectedGroup.scaleY = val;
            if (lockScale) {
                selectedGroup.scaleX = val; selectedGroup.scaleZ = val;
                if (scaleSliderX != null) scaleSliderX.forceValue(val);
                if (scaleSliderZ != null) scaleSliderZ.forceValue(val);
            }
        });
        scaleSliderZ = new ScaleSlider(sliderXPos, scaleStartY + 50, sliderWidth, "Scale Z",
                selectedGroup.scaleZ, val -> {
            selectedGroup.scaleZ = val;
            if (lockScale) {
                selectedGroup.scaleX = val; selectedGroup.scaleY = val;
                if (scaleSliderX != null) scaleSliderX.forceValue(val);
                if (scaleSliderY != null) scaleSliderY.forceValue(val);
            }
        });

        scaleLockButton = Button.builder(
                        Component.literal(lockScale ? "🔒" : "🔓"),
                        btn -> {
                            lockScale = !lockScale;
                            btn.setMessage(Component.literal(lockScale ? "🔒" : "🔓"));
                            if (lockScale) {
                                selectedGroup.scaleY = selectedGroup.scaleX;
                                selectedGroup.scaleZ = selectedGroup.scaleX;
                                if (scaleSliderY != null) scaleSliderY.forceValue(selectedGroup.scaleX);
                                if (scaleSliderZ != null) scaleSliderZ.forceValue(selectedGroup.scaleX);
                            }
                        })
                .pos(lockBtnX, scaleStartY + 12).size(lockBtnWidth, 50).build();

        offsetSliderX = new ScaleSlider(sliderXPos, offsetStartY,      sliderWidth, "Offset X",
                selectedGroup.offsetX, -3f, 3f, val -> selectedGroup.offsetX = val);
        offsetSliderY = new ScaleSlider(sliderXPos, offsetStartY + 25, sliderWidth, "Offset Y",
                selectedGroup.offsetY, -3f, 3f, val -> selectedGroup.offsetY = val);
        offsetSliderZ = new ScaleSlider(sliderXPos, offsetStartY + 50, sliderWidth, "Offset Z",
                selectedGroup.offsetZ, -3f, 3f, val -> selectedGroup.offsetZ = val);

        int alphaStartY = offsetStartY + 95;
        alphaSlider = new ScaleSlider(sliderXPos, alphaStartY, sliderWidth, "Transparency",
                selectedGroup.alpha, 0.0f, 1.0f, val -> selectedGroup.alpha = val);

        addRenderableWidget(groupHideButton);
        addRenderableWidget(groupResetButton);
        addRenderableWidget(scaleSliderX);
        addRenderableWidget(scaleSliderY);
        addRenderableWidget(scaleSliderZ);
        addRenderableWidget(scaleLockButton);
        addRenderableWidget(offsetSliderX);
        addRenderableWidget(offsetSliderY);
        addRenderableWidget(offsetSliderZ);
        addRenderableWidget(alphaSlider);
    }

    // =========================================================
    //  Input — group row clicks (beyond modal routing in base)
    // =========================================================

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean bl) {
        // Let base handle modal routing first
        if (isModalOpen()) return super.mouseClicked(event, bl);

        if (selectedSpell != null && hoveredGroup != null) {
            // Check for [?] click
            if (hoveredGroup.description != null) {
                int listRight = 8 + width / LIST_WIDTH_FRACTION + DETAIL_PANEL_PADDING;
                int gy = LIST_TOP + 32;
                for (SpellGroup group : selectedSpell.groups) {
                    if (group == hoveredGroup) {
                        String label = "> - " + group.name + (group.hidden ? " [hidden]" : "") + " [?]";
                        int labelWidth = font.width(label);
                        int qStart = listRight + 8 + font.width("> - " + group.name + (group.hidden ? " [hidden]" : "") + " ");
                        int qEnd   = listRight + 8 + labelWidth;
                        if (event.x() >= qStart && event.x() <= qEnd
                                && event.y() >= gy - 1 && event.y() <= gy + 9) {
                            openInfoModal(group.name, group.description);
                            return true;
                        }
                        break;
                    }
                    gy += 12;
                }
            }
            selectedGroup = hoveredGroup;
            GuiState.lastSelectedGroup = selectedGroup;
            rebuildDetailWidgets();
            return true;
        }
        return super.mouseClicked(event, bl);
    }

    // =========================================================
    //  Spell list
    // =========================================================

    private class SpellList extends ObjectSelectionList<SpellList.SpellEntry> {

        public SpellList(Minecraft minecraft, int width, int height, int y, int itemHeight) {
            super(minecraft, width, height, y, itemHeight);
            rebuildEntries();
        }

        public void rebuildEntries() {
            clearEntries();
            for (SpellConfig spell : SpellRegistry.getSpells(activeTab)) addEntry(new SpellEntry(spell));
        }

        @Override public int getRowWidth() { return width - 6; }
        @Override protected int scrollBarX() { return getX() + width - 6; }

        public class SpellEntry extends ObjectSelectionList.Entry<SpellEntry> {
            private final SpellConfig spell;
            private final Button toggleButton;
            private final Button editButton;
            @Nullable private final Button infoButton;

            public SpellEntry(SpellConfig spell) {
                this.spell = spell;

                this.toggleButton = Button.builder(
                                Component.literal(allHidden() ? "Show" : "Hide"),
                                btn -> {
                                    boolean allHidden = allHidden();
                                    for (SpellGroup group : spell.groups) group.hidden = !allHidden;
                                    btn.setMessage(Component.literal(allHidden() ? "Show" : "Hide"));
                                })
                        .size(40, 16).build();

                this.editButton = Button.builder(
                                Component.literal("Edit"),
                                btn -> {
                                    if (selectedSpell == spell) {
                                        selectedSpell = null;
                                        selectedGroup = null;
                                    } else {
                                        selectedSpell = spell;
                                        selectedGroup = spell.groups.isEmpty() ? null : spell.groups.get(0);
                                    }
                                    GuiState.lastSelectedSpell = selectedSpell;
                                    GuiState.lastSelectedGroup = selectedGroup;
                                    rebuildDetailWidgets();
                                })
                        .size(30, 16).build();

                this.infoButton = spell.description != null
                        ? Button.builder(Component.literal("?"),
                                btn -> openInfoModal(spell.name, spell.description))
                        .size(16, 16).build()
                        : null;
            }

            private boolean allHidden() {
                return spell.groups.stream().allMatch(g -> g.hidden);
            }

            @Override
            public void renderContent(GuiGraphics guiGraphics, int mouseX, int mouseY,
                                      boolean hovered, float partialTick) {
                int left        = getX();
                int top         = getY();
                int entryWidth  = getWidth();
                int entryHeight = getHeight();
                int nameColor   = allHidden() ? 0xFF555555 : 0xFFFFFFFF;

                guiGraphics.drawString(SpellHiderScreen.this.font, spell.name,
                        left + 4, top + (entryHeight - 8) / 2, nameColor, false);

                int rightCursor = left + entryWidth - 14;

                editButton.setX(rightCursor - 30);
                editButton.setY(top + (entryHeight - 16) / 2);
                editButton.render(guiGraphics, mouseX, mouseY, partialTick);
                rightCursor -= 34;

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
                if (editButton.isMouseOver(mx, my))                       { editButton.mouseClicked(event, bl); return true; }
                return super.mouseClicked(event, bl);
            }

            @Override
            public Component getNarration() { return Component.literal(spell.name); }
        }
    }
}