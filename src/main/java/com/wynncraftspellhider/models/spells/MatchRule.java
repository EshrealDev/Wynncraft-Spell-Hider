package com.wynncraftspellhider.models.spells;

import java.util.Set;

public class MatchRule {
    public final Set<String> textureNames;
    public final Set<String> fingerprints; // null = match any fingerprint
    public final String entityType;        // null = not an entity-type rule

    private MatchRule(Set<String> textureNames, Set<String> fingerprints, String entityType) {
        this.textureNames = textureNames;
        this.fingerprints = fingerprints;
        this.entityType = entityType;
    }

    // Single texture, any fingerprint
    public static MatchRule of(String textureName) {
        return new MatchRule(Set.of(textureName), null, null);
    }

    // Multiple textures, any fingerprint
    public static MatchRule of(Set<String> textureNames) {
        return new MatchRule(textureNames, null, null);
    }

    // Single texture, specific fingerprints
    public static MatchRule of(String textureName, Set<String> fingerprints) {
        return new MatchRule(Set.of(textureName), fingerprints, null);
    }

    // Multiple textures, single fingerprint
    public static MatchRule of(Set<String> textureNames, String fingerprint) {
        return new MatchRule(textureNames, Set.of(fingerprint), null);
    }

    // Multiple textures, multiple fingerprints
    public static MatchRule of(Set<String> textureNames, Set<String> fingerprints) {
        return new MatchRule(textureNames, fingerprints, null);
    }

    // Entity type rule (no texture/fingerprint matching)
    public static MatchRule ofEntityType(String entityType) {
        return new MatchRule(Set.of(), null, entityType);
    }

    public boolean matches(String textureName, String fingerprint) {
        if (entityType != null) return false; // entity-type rules are matched separately
        if (!textureNames.contains(textureName)) return false;
        if (fingerprints == null) return true;
        return fingerprints.contains(fingerprint);
    }

    public boolean matchesEntityType(String type) {
        return type != null && type.equals(this.entityType);
    }

    public boolean hasFingerprints() {
        return fingerprints != null;
    }

    public boolean isEntityTypeRule() {
        return entityType != null;
    }
}