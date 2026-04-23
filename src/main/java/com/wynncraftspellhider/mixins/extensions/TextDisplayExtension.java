package com.wynncraftspellhider.mixins.extensions;

import com.wynncraftspellhider.models.spells.SpellGroup;

public interface TextDisplayExtension {
    void wynncraftspellhider_setSpellGroup(SpellGroup group);
    SpellGroup wynncraftspellhider_getSpellGroup();
    void wynncraftspellhider_setHasChecked(boolean hasChecked);
    boolean wynncraftspellhider_hasChecked();
}