package com.wynncraftspellhider.gui;

import com.wynncraftspellhider.models.spells.SpellConfig;
import com.wynncraftspellhider.models.spells.SpellGroup;
import com.wynncraftspellhider.models.spells.SpellRegistry;
import net.minecraft.client.gui.screens.Screen;

import java.util.function.Supplier;

/**
 * Shared GUI state across all WynncraftSpellHider screens.
 * All screens read from and write to this class so that navigation,
 * scroll positions, and selections persist when switching between screens.
 */
public class GuiState {

    // --- Last open screen (restored when keybind is pressed) ---
    public static Supplier<Screen> lastScreenFactory = SpellHiderScreen::new;

    // --- Spell screen state ---
    public static SpellRegistry.WynnClass lastActiveTab = SpellRegistry.WynnClass.WARRIOR;
    public static SpellConfig lastSelectedSpell = null;
    public static SpellGroup lastSelectedGroup = null;
    public static java.util.Map<SpellRegistry.WynnClass, Double> spellListScrollAmounts = new java.util.HashMap<>();

    // --- Particle screen state ---
    public static double particleListScrollAmount = 0;

    // --- Config screen state ---
    public static double configListScrollAmount = 0;
}