package com.wynncraftspellhider.mixins.extensions;

import com.wynncraftspellhider.models.spell.SpellGroup;

public interface AbstractArrowExtension {
    SpellGroup wynncraftspellhider_getSpellGroup();

    void wynncraftspellhider_setSpellGroup(SpellGroup group);
}
