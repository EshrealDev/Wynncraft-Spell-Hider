package com.wynncraftspellhider.mixins.mixins;

import com.wynncraftspellhider.mixins.extensions.ItemDisplayExtension;
import com.wynncraftspellhider.models.spells.SpellGroup;
import net.minecraft.world.entity.Display;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Display.ItemDisplay.class)
public class ItemDisplayMixin implements ItemDisplayExtension {
    @Unique private SpellGroup wynncraftspellhider_spellGroup = null;

    @Override public SpellGroup wynncraftspellhider_getSpellGroup() { return wynncraftspellhider_spellGroup; }
    @Override public void wynncraftspellhider_setSpellGroup(SpellGroup group) { wynncraftspellhider_spellGroup = group; }
}