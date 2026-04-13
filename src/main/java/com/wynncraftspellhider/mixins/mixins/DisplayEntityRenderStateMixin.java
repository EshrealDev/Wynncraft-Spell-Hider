package com.wynncraftspellhider.mixins.mixins;

import com.wynncraftspellhider.mixins.extensions.DisplayRenderStateExtension;
import com.wynncraftspellhider.models.spells.SpellGroup;
import net.minecraft.client.renderer.entity.state.DisplayEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(DisplayEntityRenderState.class)
public class DisplayEntityRenderStateMixin implements DisplayRenderStateExtension {
    @Unique private SpellGroup wynncraftspellhider_spellGroup = null;

    @Override public SpellGroup wynncraftspellhider_getSpellGroup() { return wynncraftspellhider_spellGroup; }
    @Override public void wynncraftspellhider_setSpellGroup(SpellGroup g) { wynncraftspellhider_spellGroup = g; }
}