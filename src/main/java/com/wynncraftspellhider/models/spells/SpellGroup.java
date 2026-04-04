package com.wynncraftspellhider.models.spells;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SpellGroup {
    public final String name;
    public final List<MatchRule> rules;
    public final String description; // null = no info button
    public boolean hidden;
    public float scaleX = 1.0f, scaleY = 1.0f, scaleZ = 1.0f;
    public float offsetX = 0.0f, offsetY = 0.0f, offsetZ = 0.0f;
    public float alpha = 0.0f;

    public SpellGroup(String name, List<MatchRule> rules) {
        this.name = name;
        this.rules = rules;
        this.description = null;
        this.hidden = false;
    }

    public SpellGroup(String name, MatchRule rule) {
        this(name, List.of(rule));
    }

    public SpellGroup(String name, String description, List<MatchRule> rules) {
        this.name = name;
        this.rules = rules;
        this.description = description;
        this.hidden = false;
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