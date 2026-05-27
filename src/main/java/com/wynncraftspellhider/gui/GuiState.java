package com.wynncraftspellhider.gui;

import com.wynncraftspellhider.models.spell.SpellConfig;
import com.wynncraftspellhider.models.spell.SpellGroup;
import com.wynncraftspellhider.models.spell.SpellModel;
import java.util.function.Supplier;
import net.minecraft.client.gui.screens.Screen;

/**
 * Shared GUI state across all WynncraftSpellHider screens. All screens read from and write to this
 * class so that navigation, scroll positions, and selections persist when switching between
 * screens.
 */
public class GuiState {

    // --- Last open screen (restored when keybind is pressed) ---
    public static Supplier<Screen> lastScreenFactory = SpellHiderScreen::new;

    // --- Spell screen state ---
    public static SpellModel.WynnClass lastActiveTab = SpellModel.WynnClass.WARRIOR;
    public static SpellConfig lastSelectedSpell = null;
    public static SpellGroup lastSelectedGroup = null;
    public static java.util.Map<SpellModel.WynnClass, Double> spellListScrollAmounts = new java.util.HashMap<>();

    // --- Detail panel state ---
    public static java.util.Map<SpellGroup, Double> detailScrollAmounts = new java.util.HashMap<>();
    public static boolean detailLockScale = true;

    // --- Particle screen state ---
    public static double particleListScrollAmount = 0;

    // --- Config screen state ---
    public static double configListScrollAmount = 0;
}
