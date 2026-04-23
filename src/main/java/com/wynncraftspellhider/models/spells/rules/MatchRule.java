package com.wynncraftspellhider.models.spells.rules;

import net.minecraft.world.item.Item;

import java.util.Set;

public sealed interface MatchRule permits TextureRule, EntityTypeRule, TextDisplayRule, ArmorStandRule, ItemEntityRule {

    // --- texture rules ---
    static TextureRule ofTexture(String textureName) {
        return new TextureRule(Set.of(textureName), null);
    }

    static TextureRule ofTexture(Set<String> textureNames) {
        return new TextureRule(textureNames, null);
    }

    static TextureRule ofTexture(String textureName, Set<String> fingerprints) {
        return new TextureRule(Set.of(textureName), fingerprints);
    }

    static TextureRule ofTexture(Set<String> textureNames, String fingerprint) {
        return new TextureRule(textureNames, Set.of(fingerprint));
    }

    static TextureRule ofTexture(Set<String> textureNames, Set<String> fingerprints) {
        return new TextureRule(textureNames, fingerprints);
    }

    // --- entityType rule ---
    static EntityTypeRule ofEntityType(String entityType) {
        return new EntityTypeRule(entityType);
    }

    // --- textDisplay rules ---
    static TextDisplayRule ofTextDisplay(String regex) {
        return new TextDisplayRule(regex);
    }

    static TextDisplayRule ofTextDisplay(String regex, TextDisplayRule.OwnerFilter ownerFilter) {
        return new TextDisplayRule(regex, ownerFilter);
    }
    // --- armorStand rule ---
    static ArmorStandRule ofArmorStand(ArmorStandRule.ArmorStandModel... models) {
        return new ArmorStandRule(Set.of(models));
    }

    // --- itemEntity rule ---
    static ItemEntityRule ofItemEntity(Item item) {
        return new ItemEntityRule(item);
    }
}