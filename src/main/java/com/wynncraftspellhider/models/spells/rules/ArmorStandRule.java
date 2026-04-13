package com.wynncraftspellhider.models.spells.rules;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

//This entire rule only exists because these models still use the old armor stand method of making models.
//I assume they will update these when the class reskins get released.
//The numbers here should not change across texture pack updates. I checked old texture packs to make sure they are the same.
public final class ArmorStandRule implements MatchRule {

    public enum ArmorStandModel {
        TRAP_0("trap_0", "diamond_sword", 5),
        TRAP_1("trap_1", "diamond_sword", 6),

        MAMBA_HEAD("mamba_head", "diamond_hoe", 83),
        MAMBA_BODY("mamba_body", "diamond_hoe", 84),
        MAMBA_BUSH("mamba_bush", "diamond_hoe", 85),

        CROW_BODY("crow_body", "diamond_hoe", 86),
        CROW_WING_LEFT("crow_wing_left", "diamond_hoe", 87),
        CROW_WING_RIGHT("crow_wing_right", "diamond_hoe", 88);

        public final String modelName;
        public final String itemId;
        public final int damage;

        ArmorStandModel(String modelName, String itemId, int damage) {
            this.modelName = modelName;
            this.itemId = itemId;
            this.damage = damage;
        }

        private static final Map<String, Map<Integer, ArmorStandModel>> LOOKUP = new HashMap<>();

        static {
            for (ArmorStandModel m : values()) {
                LOOKUP.computeIfAbsent(m.itemId, k -> new HashMap<>()).put(m.damage, m);
            }
        }

        public static ArmorStandModel get(String itemId, int damage) {
            Map<Integer, ArmorStandModel> byDamage = LOOKUP.get(itemId);
            return byDamage != null ? byDamage.get(damage) : null;
        }
    }

    public final Set<ArmorStandModel> models;

    public ArmorStandRule(Set<ArmorStandModel> models) {
        this.models = models;
    }

    public boolean matches(String itemId, int damage) {
        ArmorStandModel model = ArmorStandModel.get(itemId, damage);
        return model != null && models.contains(model);
    }
}