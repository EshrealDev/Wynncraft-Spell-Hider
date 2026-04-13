package com.wynncraftspellhider.mixins.mixins;

import com.wynncraftspellhider.mixins.extensions.DisplayRenderStateExtension;
import com.wynncraftspellhider.mixins.extensions.TextDisplayExtension;
import com.wynncraftspellhider.models.spells.SpellGroup;
import net.minecraft.client.renderer.entity.DisplayRenderer;
import net.minecraft.client.renderer.entity.state.TextDisplayEntityRenderState;
import net.minecraft.world.entity.Display;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DisplayRenderer.TextDisplayRenderer.class)
public class TextDisplayRendererMixin {

    @Inject(method = "extractRenderState*", at = @At("TAIL"))
    private void onExtractRenderState(
            Display.TextDisplay textDisplay,
            TextDisplayEntityRenderState textDisplayEntityRenderState,
            float f,
            CallbackInfo ci
    ) {
        SpellGroup group = ((TextDisplayExtension) textDisplay).wynncraftspellhider_getSpellGroup();
        ((DisplayRenderStateExtension) textDisplayEntityRenderState).wynncraftspellhider_setSpellGroup(group);
    }
}