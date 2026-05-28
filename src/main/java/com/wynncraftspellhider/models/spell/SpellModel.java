package com.wynncraftspellhider.models.spell;

import com.wynncraftspellhider.WynncraftSpellHider;
import com.wynncraftspellhider.managers.UpdateManager.UpdateManager;
import com.wynncraftspellhider.managers.UpdateManager.UpdateResult;
import com.wynncraftspellhider.models.config.ProfileConfig;
import com.wynncraftspellhider.models.config.ProfileRegistry;
import com.wynncraftspellhider.models.spell.rules.*;
import java.nio.file.Path;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class SpellModel {

    public enum WynnClass {
        WARRIOR,
        MAGE,
        ARCHER,
        ASSASSIN,
        SHAMAN,
        GLOBAL
    }

    private static final List<SpellConfig> WARRIOR_SPELLS = new ArrayList<>();
    private static final List<SpellConfig> MAGE_SPELLS = new ArrayList<>();
    private static final List<SpellConfig> ARCHER_SPELLS = new ArrayList<>();
    private static final List<SpellConfig> ASSASSIN_SPELLS = new ArrayList<>();
    private static final List<SpellConfig> SHAMAN_SPELLS = new ArrayList<>();
    private static final List<SpellConfig> GLOBAL_SPELLS = new ArrayList<>();
    private static final List<SpellConfig> ALL_SPELLS = new ArrayList<>();

    private static final List<SpellGroup> TEXT_SPELL_GROUPS_CACHE = new ArrayList<>();
    private static final Map<ArmorStandRule.ArmorStandModel, SpellGroup> ARMOR_STAND_GROUP_CACHE = new HashMap<>();
    private static final Map<String, SpellGroup> ENTITY_TYPE_GROUP_CACHE = new HashMap<>();
    private static final Map<Item, SpellGroup> ITEM_ENTITY_GROUP_CACHE = new HashMap<>();

    private static boolean initialized = false;

    public static void init(Path configDir) {
        if (initialized) throw new IllegalStateException("SpellModel already initialized");
        populate(configDir);
        scheduleUpdateCheck(configDir);
    }

    public static void reinit(Path configDir) {
        if (!initialized) throw new IllegalStateException("SpellModel not yet initialized");
        clearAll();
        populate(configDir);
        // apply profile, because repopulating the spell arrays resets the spell groups.
        ProfileConfig active = ProfileRegistry.getActiveProfile();
        if (active != null) active.apply();
    }

    private static void populate(Path configDir) {
        Map<WynnClass, List<SpellConfig>> loaded = SpellModelLoader.load(configDir);

        WARRIOR_SPELLS.addAll(loaded.getOrDefault(WynnClass.WARRIOR, List.of()));
        MAGE_SPELLS.addAll(loaded.getOrDefault(WynnClass.MAGE, List.of()));
        ARCHER_SPELLS.addAll(loaded.getOrDefault(WynnClass.ARCHER, List.of()));
        ASSASSIN_SPELLS.addAll(loaded.getOrDefault(WynnClass.ASSASSIN, List.of()));
        SHAMAN_SPELLS.addAll(loaded.getOrDefault(WynnClass.SHAMAN, List.of()));
        GLOBAL_SPELLS.addAll(loaded.getOrDefault(WynnClass.GLOBAL, List.of()));

        for (WynnClass wynnClass : WynnClass.values()) ALL_SPELLS.addAll(getSpells(wynnClass));

        buildCaches();
        initialized = true;
    }

    private static void clearAll() {
        WARRIOR_SPELLS.clear();
        MAGE_SPELLS.clear();
        ARCHER_SPELLS.clear();
        ASSASSIN_SPELLS.clear();
        SHAMAN_SPELLS.clear();
        GLOBAL_SPELLS.clear();
        ALL_SPELLS.clear();
        TEXT_SPELL_GROUPS_CACHE.clear();
        ARMOR_STAND_GROUP_CACHE.clear();
        ENTITY_TYPE_GROUP_CACHE.clear();
        ITEM_ENTITY_GROUP_CACHE.clear();
        initialized = false;
    }

    private static void scheduleUpdateCheck(Path configDir) {
        UpdateManager.checkSpellRegistryAsync(configDir).thenAccept(result -> {
            if (result == UpdateResult.UPDATED) {
                WynncraftSpellHider.info("Spell registry updated — reloading.");
                Minecraft.getInstance().execute(() -> reinit(configDir));
            }
        });
    }

    private static void buildCaches() {
        for (SpellConfig spell : ALL_SPELLS) {
            for (SpellGroup group : spell.groups) {
                for (MatchRule rule : group.rules) {
                    if (rule instanceof TextDisplayRule) TEXT_SPELL_GROUPS_CACHE.add(group);
                    if (rule instanceof EntityTypeRule etr) ENTITY_TYPE_GROUP_CACHE.put(etr.entityType, group);
                    if (rule instanceof ItemEntityRule ier) ITEM_ENTITY_GROUP_CACHE.put(ier.item, group);
                    if (rule instanceof ArmorStandRule asr) {
                        for (ArmorStandRule.ArmorStandModel model : asr.models) {
                            ARMOR_STAND_GROUP_CACHE.put(model, group);
                        }
                    }
                }
            }
        }
    }

    public static List<SpellConfig> getSpells(WynnClass wynnClass) {
        return switch (wynnClass) {
            case WARRIOR -> WARRIOR_SPELLS;
            case MAGE -> MAGE_SPELLS;
            case ARCHER -> ARCHER_SPELLS;
            case ASSASSIN -> ASSASSIN_SPELLS;
            case SHAMAN -> SHAMAN_SPELLS;
            case GLOBAL -> GLOBAL_SPELLS;
        };
    }

    public static List<SpellConfig> getAllSpells() {
        return ALL_SPELLS;
    }

    public static SpellGroup getGroupForModel(String textureName, String fingerprint) {
        for (SpellConfig spell : getAllSpells())
            for (SpellGroup group : spell.groups) if (group.matches(textureName, fingerprint)) return group;
        return null;
    }

    public static SpellGroup getGroupForTextDisplay(String plainText, boolean isLocalPlayer) {
        for (SpellGroup group : TEXT_SPELL_GROUPS_CACHE) {
            if (group.isTextDisplayMatch(plainText, isLocalPlayer)) return group;
        }
        return null;
    }

    public static SpellGroup getGroupForArmorStand(String itemId, int damage) {
        ArmorStandRule.ArmorStandModel model = ArmorStandRule.ArmorStandModel.get(itemId, damage);
        if (model == null) return null;
        return ARMOR_STAND_GROUP_CACHE.get(model);
    }

    public static SpellGroup getGroupForEntityType(String entityType) {
        return ENTITY_TYPE_GROUP_CACHE.get(entityType);
    }

    public static SpellGroup getGroupForItemEntity(ItemStack stack) {
        return ITEM_ENTITY_GROUP_CACHE.get(stack.getItem());
    }
}
