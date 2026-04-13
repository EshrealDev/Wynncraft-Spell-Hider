package com.wynncraftspellhider.mixins.mixins;

import com.wynncraftspellhider.mixins.extensions.ArmorStandExtension;
import com.wynncraftspellhider.models.spells.SpellGroup;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ArmorStand.class)
public class ArmorStandMixin implements ArmorStandExtension {
    @Unique private SpellGroup wynncraftspellhider_spellGroup = null;

    @Override public SpellGroup wynncraftspellhider_getSpellGroup() { return wynncraftspellhider_spellGroup; }
    @Override public void wynncraftspellhider_setSpellGroup(SpellGroup group) { wynncraftspellhider_spellGroup = group; }
}