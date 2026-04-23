package com.wynncraftspellhider.mixins.mixins;

import com.wynncraftspellhider.mixins.extensions.ItemEntityExtension;
import com.wynncraftspellhider.models.spells.SpellGroup;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ItemEntity.class)
public class ItemEntityMixin implements ItemEntityExtension {

    @Unique
    private SpellGroup wynncraftspellhider_spellGroup;

    @Override
    public SpellGroup wynncraftspellhider_getSpellGroup() {
        return wynncraftspellhider_spellGroup;
    }

    @Override
    public void wynncraftspellhider_setSpellGroup(SpellGroup group) {
        this.wynncraftspellhider_spellGroup = group;
    }
}