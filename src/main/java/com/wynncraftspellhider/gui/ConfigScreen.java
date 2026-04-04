package com.wynncraftspellhider.gui;

import com.wynncraftspellhider.models.config.ProfileConfig;
import com.wynncraftspellhider.models.config.ProfileRegistry;
import com.wynncraftspellhider.models.spells.SpellRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class ConfigScreen extends BaseHiderScreen {

    private ProfileList profileList;

    // Layout
    private static final int LIST_WIDTH     = 340;
    private static final int ADD_BAR_HEIGHT = 24;

    // Add-profile bar (always visible at the bottom, not modal-based)
    @Nullable private EditBox addBox = null;

    public ConfigScreen() {
        super(Component.literal("Config"));
    }

    // In onClose — save active profile before leaving
    @Override
    public void onClose() {
        if (profileList != null) GuiState.configListScrollAmount = profileList.scrollAmount();
        ProfileRegistry.saveActiveProfile();
        super.onClose();
    }

    // =========================================================
    //  BaseHiderScreen hooks
    // =========================================================

    @Override
    protected void buildPageContent() {
        int listX   = (width - LIST_WIDTH) / 2;
        int listTop = LIST_TOP + 18; // room for "Active profile:" label
        int listH   = height - listTop - LIST_BOTTOM_OFFSET - ADD_BAR_HEIGHT - 6;

        profileList = new ProfileList(minecraft, LIST_WIDTH, listH, listTop, 28);
        profileList.setX(listX);
        addWidget(profileList);
        profileList.setScrollAmount(GuiState.configListScrollAmount);

        // --- Add-profile bar pinned to the bottom ---
        int barY = height - LIST_BOTTOM_OFFSET - ADD_BAR_HEIGHT - 4;

        addBox = new EditBox(font, listX, barY, LIST_WIDTH - 80, ADD_BAR_HEIGHT,
                Component.literal("Profile name..."));
        addBox.setMaxLength(40);
        addBox.setValue("");
        addRenderableWidget(addBox);

        addRenderableWidget(Button.builder(Component.literal("Add"),
                        btn -> commitAddProfile())
                .pos(listX + LIST_WIDTH - 76, barY).size(36, ADD_BAR_HEIGHT).build());

        addRenderableWidget(Button.builder(Component.literal("✕"),
                        btn -> { if (addBox != null) addBox.setValue(""); })
                .pos(listX + LIST_WIDTH - 36, barY).size(36, ADD_BAR_HEIGHT).build());
    }

    private void commitAddProfile() {
        if (addBox == null) return;
        String name = addBox.getValue().trim();
        if (name.isEmpty()) return;
        ProfileRegistry.create(name);
        addBox.setValue("");
        if (profileList != null) profileList.rebuildEntries();
    }

    @Override
    protected void renderPageContent(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        int listX = (width - LIST_WIDTH) / 2;

        // Active profile label above the list
        ProfileConfig active = ProfileRegistry.getActiveProfile();
        String activeLabel = active != null
                ? "Active profile: " + active.name
                : "Active profile: none";
        guiGraphics.drawString(font, activeLabel, listX, LIST_TOP + 6, 0xFFAAAAAA, false);

        profileList.render(guiGraphics, mouseX, mouseY, partialTick);

        if (ProfileRegistry.getProfiles().isEmpty()) {
            guiGraphics.drawCenteredString(font,
                    Component.literal("No profiles yet. Type a name below and press Add."),
                    width / 2, height / 2, 0xFF666666);
        }
    }

    @Override
    protected void onClassTabClicked(SpellRegistry.WynnClass wynnClass) {
        if (profileList != null) GuiState.configListScrollAmount = profileList.scrollAmount();
        GuiState.lastActiveTab = wynnClass;
        navigateTo(SpellHiderScreen::new);  // was: minecraft.setScreen(new SpellHiderScreen())
    }

    @Override protected boolean hasHideShowButtons() { return false; }
    @Override protected SpellRegistry.WynnClass getActiveTab() { return null; }
    @Override protected int getActivePageIndex() { return 2; }

    @Override
    protected void saveScrollState() {
        if (profileList != null) GuiState.configListScrollAmount = profileList.scrollAmount();
    }

    // =========================================================
    //  Rename modal
    // =========================================================

    private void openRenameModal(ProfileConfig profile) {
        openCustomModal("Rename Profile");

        int cx = modalContentX();
        int cw = modalContentWidth();
        int cy = modalContentY() + 10; // a little padding below the divider

        // Label
        // (drawn in renderPageContent won't work here since modal rendering is in base;
        //  we instead use a second label rendered by adding a no-op widget — simpler:
        //  just position the EditBox with a visible prompt and rely on its hint text)

        EditBox nameBox = new EditBox(font, cx, cy, cw, 20,
                Component.literal("New name..."));
        nameBox.setMaxLength(40);
        nameBox.setValue(profile.name);
        nameBox.setFocused(true);
        // Register through addModalWidget so it gets removed when the modal closes
        addModalWidget(nameBox);
        setFocused(nameBox);

        int btnY   = modalActionY();
        int btnW   = (cw - 4) / 2;

        Button confirmBtn = addModalWidget(Button.builder(Component.literal("Rename"),
                        btn -> {
                            String newName = nameBox.getValue().trim();
                            if (!newName.isEmpty()) ProfileRegistry.rename(profile, newName);
                            closeModal();
                            if (profileList != null) profileList.rebuildEntries();
                        })
                .pos(cx, btnY).size(btnW, 20).build());

        addModalWidget(Button.builder(Component.literal("Cancel"),
                        btn -> closeModal())
                .pos(cx + btnW + 4, btnY).size(btnW, 20).build());
    }

    // =========================================================
    //  Delete confirmation modal
    // =========================================================

    private void openDeleteModal(ProfileConfig profile) {
        openCustomModal("Delete Profile");

        int cx  = modalContentX();
        int cw  = modalContentWidth();
        int cy  = modalContentY() + 10;
        int btnY = modalActionY();
        int btnW = (cw - 4) / 2;

        // We render the confirmation text ourselves by adding a custom label widget.
        // Since we can't draw arbitrary text from inside the modal render without
        // refactoring heavily, we place the text in a dummy widget that just draws.
        String confirmText = "Delete \"" + profile.name + "\"?";
        // Use a non-interactive button styled as a label (disabled, no border interaction)
        Button labelWidget = Button.builder(Component.literal(confirmText), btn -> {})
                .pos(cx, cy).size(cw, 20).build();
        labelWidget.active = false;
        addModalWidget(labelWidget);

        addModalWidget(Button.builder(Component.literal("Delete"),
                        btn -> {
                            ProfileRegistry.delete(profile);
                            closeModal();
                            if (profileList != null) profileList.rebuildEntries();
                        })
                .pos(cx, btnY).size(btnW, 20).build());

        addModalWidget(Button.builder(Component.literal("Cancel"),
                        btn -> closeModal())
                .pos(cx + btnW + 4, btnY).size(btnW, 20).build());
    }

    // =========================================================
    //  Input — Enter in the add box
    // =========================================================

    @Override
    public boolean keyPressed(KeyEvent keyEvent) {
        // If the add-box is focused and the modal is not open, handle Enter
        if (!isModalOpen() && addBox != null && addBox.isFocused()) {
            if (keyEvent.key() == org.lwjgl.glfw.GLFW.GLFW_KEY_ENTER
                    || keyEvent.key() == org.lwjgl.glfw.GLFW.GLFW_KEY_KP_ENTER) {
                commitAddProfile();
                return true;
            }
        }
        // Delegate to base (handles modal Enter/Escape and routes to focused widget)
        return super.keyPressed(keyEvent);
    }

    // =========================================================
    //  Profile list
    // =========================================================

    private class ProfileList extends ObjectSelectionList<ProfileList.ProfileEntry> {

        public ProfileList(Minecraft minecraft, int width, int height, int y, int itemHeight) {
            super(minecraft, width, height, y, itemHeight);
            rebuildEntries();
        }

        public void rebuildEntries() {
            clearEntries();
            for (ProfileConfig profile : ProfileRegistry.getProfiles()) addEntry(new ProfileEntry(profile));
        }

        @Override public int getRowWidth() { return width - 6; }
        @Override protected int scrollBarX() { return getX() + width - 6; }

        public class ProfileEntry extends ObjectSelectionList.Entry<ProfileEntry> {
            private final ProfileConfig profile;
            private final Button applyButton;
            private final Button renameButton;
            private final Button deleteButton;
            private final Button defaultButton;

            public ProfileEntry(ProfileConfig profile) {
                this.profile = profile;

                this.applyButton = Button.builder(Component.literal("Apply"),
                                btn -> {
                                    ProfileRegistry.apply(profile);
                                    profileList.rebuildEntries();
                                })
                        .size(42, 16).build();

                this.renameButton = Button.builder(Component.literal("Rename"),
                                btn -> openRenameModal(profile))
                        .size(50, 16).build();

                this.deleteButton = Button.builder(Component.literal("✕"),
                                btn -> openDeleteModal(profile))
                        .size(18, 16).build();

                this.defaultButton = Button.builder(Component.literal(""),   // label set dynamically in render
                                btn -> {
                                    ProfileRegistry.setAsDefault(profile);
                                    profileList.rebuildEntries();
                                })
                        .size(18, 16).build();
            }

            private boolean isActive() {
                return ProfileRegistry.getActiveProfile() == profile;
            }

            private boolean isDefault() {
                return ProfileRegistry.getDefaultProfile() == profile;
            }

            @Override
            public void renderContent(GuiGraphics guiGraphics, int mouseX, int mouseY,
                                      boolean hovered, float partialTick) {
                int left        = getX();
                int top         = getY();
                int entryWidth  = getWidth();
                int entryHeight = getHeight();

                if (isActive()) {
                    guiGraphics.fill(left, top, left + entryWidth - 6, top + entryHeight, 0x44FFFF00);
                }

                int nameColor = isActive() ? 0xFFFFFF00 : 0xFFFFFFFF;
                guiGraphics.drawString(ConfigScreen.this.font, profile.name,
                        left + 4, top + (entryHeight - 8) / 2, nameColor, false);

                // Buttons — right-aligned
                int rightCursor = left + entryWidth - 14;

                deleteButton.setX(rightCursor - 18);
                deleteButton.setY(top + (entryHeight - 16) / 2);
                deleteButton.render(guiGraphics, mouseX, mouseY, partialTick);
                rightCursor -= 22;

                renameButton.setX(rightCursor - 50);
                renameButton.setY(top + (entryHeight - 16) / 2);
                renameButton.render(guiGraphics, mouseX, mouseY, partialTick);
                rightCursor -= 54;

                applyButton.setX(rightCursor - 42);
                applyButton.setY(top + (entryHeight - 16) / 2);
                applyButton.render(guiGraphics, mouseX, mouseY, partialTick);
                rightCursor -= 46;

                // Star — filled if this is the default profile
                defaultButton.setMessage(Component.literal(isDefault() ? "★" : "☆"));
                defaultButton.setX(rightCursor - 18);
                defaultButton.setY(top + (entryHeight - 16) / 2);
                defaultButton.render(guiGraphics, mouseX, mouseY, partialTick);
            }

            @Override
            public boolean mouseClicked(MouseButtonEvent event, boolean bl) {
                double mx = event.x(), my = event.y();
                if (deleteButton.isMouseOver(mx, my))  { deleteButton.mouseClicked(event, bl);  return true; }
                if (renameButton.isMouseOver(mx, my))  { renameButton.mouseClicked(event, bl);  return true; }
                if (applyButton.isMouseOver(mx, my))   { applyButton.mouseClicked(event, bl);   return true; }
                if (defaultButton.isMouseOver(mx, my)) { defaultButton.mouseClicked(event, bl); return true; }
                return super.mouseClicked(event, bl);
            }

            @Override
            public Component getNarration() { return Component.literal(profile.name); }
        }
    }
}