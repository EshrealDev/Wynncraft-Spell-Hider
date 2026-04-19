package com.wynncraftspellhider.mixins.mixins;


import com.wynncraftspellhider.mixins.extensions.AbstractArrowExtension;
import com.wynncraftspellhider.models.spells.SpellGroup;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(AbstractArrow.class)
public class AbstractArrowMixin  implements AbstractArrowExtension {
    @Unique private SpellGroup wynncraftspellhider_spellGroup = null;

    @Override public SpellGroup wynncraftspellhider_getSpellGroup() { return wynncraftspellhider_spellGroup; }
    @Override public void wynncraftspellhider_setSpellGroup(SpellGroup group) { wynncraftspellhider_spellGroup = group; }
}
