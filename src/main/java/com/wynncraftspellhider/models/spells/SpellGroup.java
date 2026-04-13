package com.wynncraftspellhider.models.spells;

import com.wynncraftspellhider.models.spells.rules.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SpellGroup {

    // =========================================================
    //  Defaults
    // =========================================================
    public static class Defaults {
        public static final boolean HIDDEN       = false;
        public static final float   SCALE_X      = 1.0f;
        public static final float   SCALE_Y      = 1.0f;
        public static final float   SCALE_Z      = 1.0f;
        public static final float   OFFSET_X     = 0.0f;
        public static final float   OFFSET_Y     = 0.0f;
        public static final float   OFFSET_Z     = 0.0f;
        public static final float   TRANSPARENCY = 0.0f;
    }

    public final String name;
    public final List<MatchRule> rules;
    public final String description; // null = no info button

    public boolean hidden;
    public float scaleX       = Defaults.SCALE_X;
    public float scaleY       = Defaults.SCALE_Y;
    public float scaleZ       = Defaults.SCALE_Z;
    public float offsetX      = Defaults.OFFSET_X;
    public float offsetY      = Defaults.OFFSET_Y;
    public float offsetZ      = Defaults.OFFSET_Z;
    public float transparency = Defaults.TRANSPARENCY;

    public SpellGroup(String name, List<MatchRule> rules) {
        this.name        = name;
        this.rules       = rules;
        this.description = null;
        this.hidden      = Defaults.HIDDEN;
    }

    public SpellGroup(String name, MatchRule rule) {
        this(name, List.of(rule));
    }

    public SpellGroup(String name, String description, List<MatchRule> rules) {
        this.name        = name;
        this.rules       = rules;
        this.description = description;
        this.hidden      = Defaults.HIDDEN;
    }

    public SpellGroup(String name, String description, MatchRule rule) {
        this(name, description, List.of(rule));
    }

    // =========================================================
    //  Texture matching
    // =========================================================

    public boolean matchesTexture(String textureName) {
        return rules.stream()
                .filter(r -> r instanceof TextureRule)
                .map(r -> (TextureRule) r)
                .anyMatch(r -> r.textureNames.contains(textureName));
    }

    public boolean matches(String textureName, String fingerprint) {
        // Fingerprint-specific rules take priority
        for (MatchRule rule : rules) {
            if (rule instanceof TextureRule tr && tr.hasFingerprints()) {
                if (tr.matches(textureName, fingerprint)) return true;
            }
        }
        for (MatchRule rule : rules) {
            if (rule instanceof TextureRule tr && !tr.hasFingerprints()) {
                if (tr.matches(textureName, fingerprint)) return true;
            }
        }
        return false;
    }

    public Set<String> getAllTextureNames() {
        return rules.stream()
                .filter(r -> r instanceof TextureRule)
                .flatMap(r -> ((TextureRule) r).textureNames.stream())
                .collect(Collectors.toSet());
    }

    public Set<String> getAllFingerprints() {
        return rules.stream()
                .filter(r -> r instanceof TextureRule tr && tr.hasFingerprints())
                .flatMap(r -> ((TextureRule) r).fingerprints.stream())
                .collect(Collectors.toSet());
    }

    // =========================================================
    //  Entity type matching
    // =========================================================

    public boolean isEntityTypeMatch(String entityType) {
        return rules.stream()
                .filter(r -> r instanceof EntityTypeRule)
                .map(r -> (EntityTypeRule) r)
                .anyMatch(r -> r.matches(entityType));
    }

    // =========================================================
    //  Text display matching
    // =========================================================

    public boolean isTextDisplayMatch(String plainText, boolean isLocalPlayer) {
        return rules.stream()
                .filter(r -> r instanceof TextDisplayRule)
                .map(r -> (TextDisplayRule) r)
                .anyMatch(r -> r.matches(plainText, isLocalPlayer));
    }

    // =========================================================
    //  Armor stand matching
    // =========================================================

    public boolean isArmorStandMatch(String itemId, int damage) {
        return rules.stream()
                .filter(r -> r instanceof ArmorStandRule)
                .map(r -> (ArmorStandRule) r)
                .anyMatch(r -> r.matches(itemId, damage));
    }
}