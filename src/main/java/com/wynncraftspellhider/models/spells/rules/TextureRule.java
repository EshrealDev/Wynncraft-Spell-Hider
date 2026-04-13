package com.wynncraftspellhider.models.spells.rules;

import java.util.Set;

public final class TextureRule implements MatchRule {

    public final Set<String> textureNames;
    public final Set<String> fingerprints; // null = match any fingerprint

    public TextureRule(Set<String> textureNames, Set<String> fingerprints) {
        this.textureNames = textureNames;
        this.fingerprints = fingerprints;
    }

    public boolean hasFingerprints() {
        return fingerprints != null;
    }

    public boolean matches(String textureName, String fingerprint) {
        if (!textureNames.contains(textureName)) return false;
        if (fingerprints == null) return true;
        return fingerprints.contains(fingerprint);
    }
}
