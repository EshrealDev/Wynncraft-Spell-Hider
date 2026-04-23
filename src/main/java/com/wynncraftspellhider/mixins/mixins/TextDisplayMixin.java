package com.wynncraftspellhider.mixins.mixins;

import com.wynncraftspellhider.mixins.extensions.TextDisplayExtension;
import com.wynncraftspellhider.models.spells.SpellGroup;
import net.minecraft.world.entity.Display;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;


@Mixin(Display.TextDisplay.class)
public class TextDisplayMixin implements TextDisplayExtension {
    @Unique private SpellGroup wynncraftspellhider_spellGroup = null;
    @Unique private boolean wynncraftspellhider_hasChecked = false;

    @Override public SpellGroup wynncraftspellhider_getSpellGroup() { return wynncraftspellhider_spellGroup; }
    @Override public void wynncraftspellhider_setSpellGroup(SpellGroup group) { wynncraftspellhider_spellGroup = group; }

    @Override public boolean wynncraftspellhider_hasChecked() { return wynncraftspellhider_hasChecked; }
    @Override public void wynncraftspellhider_setHasChecked(boolean hasChecked) { this.wynncraftspellhider_hasChecked = hasChecked; }
}