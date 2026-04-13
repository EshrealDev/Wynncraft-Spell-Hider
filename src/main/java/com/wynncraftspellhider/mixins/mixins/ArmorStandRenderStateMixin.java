package com.wynncraftspellhider.mixins.mixins;

import com.wynncraftspellhider.mixins.extensions.ArmorStandRenderStateExtension;
import com.wynncraftspellhider.models.spells.SpellGroup;
import net.minecraft.client.renderer.entity.state.ArmorStandRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ArmorStandRenderState.class)
public class ArmorStandRenderStateMixin implements ArmorStandRenderStateExtension {
    @Unique private SpellGroup wynncraftspellhider_spellGroup = null;

    @Override public SpellGroup wynncraftspellhider_getSpellGroup() { return wynncraftspellhider_spellGroup; }
    @Override public void wynncraftspellhider_setSpellGroup(SpellGroup group) { wynncraftspellhider_spellGroup = group; }
}