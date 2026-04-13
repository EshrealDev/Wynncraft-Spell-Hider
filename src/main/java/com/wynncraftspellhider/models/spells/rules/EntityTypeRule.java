package com.wynncraftspellhider.models.spells.rules;

public final class EntityTypeRule implements MatchRule {

    public final String entityType;

    public EntityTypeRule(String entityType) {
        this.entityType = entityType;
    }

    public boolean matches(String entityType) {
        return this.entityType.equals(entityType);
    }
}