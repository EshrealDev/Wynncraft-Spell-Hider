package com.wynncraftspellhider.mixins.mixins;

import com.wynncraftspellhider.mixins.extensions.DisplayRenderStateExtension;
import com.wynncraftspellhider.mixins.extensions.ItemDisplayExtension;
import com.wynncraftspellhider.models.spells.SpellGroup;
import net.minecraft.client.renderer.entity.DisplayRenderer;
import net.minecraft.client.renderer.entity.state.ItemDisplayEntityRenderState;
import net.minecraft.world.entity.Display;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DisplayRenderer.ItemDisplayRenderer.class)
public class ItemDisplayRendererMixin {

    @Inject(method = "extractRenderState*", at = @At("TAIL"))
    private void onExtractRenderState(
            Display.ItemDisplay itemDisplay,
            ItemDisplayEntityRenderState itemDisplayEntityRenderState,
            float f,
            CallbackInfo ci
    ) {
        SpellGroup group = ((ItemDisplayExtension) itemDisplay).wynncraftspellhider_getSpellGroup();
        ((DisplayRenderStateExtension) itemDisplayEntityRenderState).wynncraftspellhider_setSpellGroup(group);
    }
}