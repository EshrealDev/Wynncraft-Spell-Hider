package com.wynncraftspellhider.mixins.extensions;

import com.wynncraftspellhider.models.spells.SpellGroup;

public interface ItemEntityRenderStateExtension {
    SpellGroup wynncraftspellhider_getSpellGroup();
    void wynncraftspellhider_setSpellGroup(SpellGroup group);
}