package com.wynncraftspellhider.models.spells;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SpellGroup {
    // =========================================================
    //  Defaults (single source of truth)
    // =========================================================
    public static class Defaults {
        public static final boolean HIDDEN = false;
        public static final float SCALE_X = 1.0f;
        public static final float SCALE_Y = 1.0f;
        public static final float SCALE_Z = 1.0f;
        public static final float OFFSET_X = 0.0f;
        public static final float OFFSET_Y = 0.0f;
        public static final float OFFSET_Z = 0.0f;
        public static final float TRANSPARENCY = 0.0f;
    }

    public final String name;
    public final List<MatchRule> rules;
    public final String description; // null = no info button
    public boolean hidden;
    public float scaleX = Defaults.SCALE_X, scaleY = Defaults.SCALE_Y, scaleZ = Defaults.SCALE_Z;
    public float offsetX = Defaults.OFFSET_X, offsetY = Defaults.OFFSET_Y, offsetZ = Defaults.OFFSET_Z;
    public float transparency = Defaults.TRANSPARENCY;

    public SpellGroup(String name, List<MatchRule> rules) {
        this.name = name;
        this.rules = rules;
        this.description = null;
        this.hidden = Defaults.HIDDEN;
    }

    public SpellGroup(String name, MatchRule rule) {
        this(name, List.of(rule));
    }

    public SpellGroup(String name, String description, List<MatchRule> rules) {
        this.name = name;
        this.rules = rules;
        this.description = description;
        this.hidden = Defaults.HIDDEN;
    }

    public SpellGroup(String name, String description, MatchRule rule) {
        this(name, description, List.of(rule));
    }

    public boolean matchesTexture(String textureName) {
        return rules.stream()
                .filter(r -> !r.isEntityTypeRule())
                .anyMatch(r -> r.textureNames.contains(textureName));
    }

    public boolean matches(String textureName, String fingerprint) {
        for (MatchRule rule : rules) {
            if (rule.hasFingerprints() && rule.matches(textureName, fingerprint)) return true;
        }
        for (MatchRule rule : rules) {
            if (!rule.hasFingerprints() && rule.matches(textureName, fingerprint)) return true;
        }
        return false;
    }

    public boolean isEntityTypeMatch(String entityType) {
        return rules.stream().anyMatch(r -> r.matchesEntityType(entityType));
    }

    public Set<String> getAllTextureNames() {
        return rules.stream()
                .filter(r -> !r.isEntityTypeRule())
                .flatMap(r -> r.textureNames.stream())
                .collect(Collectors.toSet());
    }

    public Set<String> getAllFingerprints() {
        return rules.stream()
                .filter(MatchRule::hasFingerprints)
                .flatMap(r -> r.fingerprints.stream())
                .collect(Collectors.toSet());
    }
}