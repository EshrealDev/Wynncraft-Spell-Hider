package com.wynncraftspellhider.models.spells.rules;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class ItemEntityRule implements MatchRule {

    public final Item item;

    public ItemEntityRule(Item item) {
        this.item = item;
    }

    public boolean matches(ItemStack stack) {
        return stack.is(item);
    }
}