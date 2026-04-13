package com.wynncraftspellhider.models.spells.rules;

import java.util.regex.Pattern;

public final class TextDisplayRule implements MatchRule {

    public enum OwnerFilter {
        ALL,            // match regardless of owner
        LOCAL_PLAYER,   // only match if text belongs to local player
        OTHER_PLAYERS   // only match if text does NOT belong to local player
    }

    public final Pattern pattern;
    public final OwnerFilter ownerFilter;

    public TextDisplayRule(String regex, OwnerFilter ownerFilter) {
        this.pattern = Pattern.compile(regex, Pattern.DOTALL);
        this.ownerFilter = ownerFilter;
    }

    public TextDisplayRule(String regex) {
        this(regex, OwnerFilter.ALL);
    }

    public boolean matches(String plainText, boolean isLocalPlayer) {
        if (!pattern.matcher(plainText).find()) return false;
        return switch (ownerFilter) {
            case ALL           -> true;
            case LOCAL_PLAYER  -> isLocalPlayer;
            case OTHER_PLAYERS -> !isLocalPlayer;
        };
    }
}