package com.wynncraftspellhider.mixins.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.wynncraftspellhider.WynncraftSpellHider;
import com.wynncraftspellhider.mixins.accessors.DisplayEntityRenderStateAccessor;
import com.wynncraftspellhider.mixins.wrappers.AlphaSubmitNodeCollector;
import com.wynncraftspellhider.models.Models;
import com.wynncraftspellhider.models.spells.SpellGroup;
import com.wynncraftspellhider.models.spells.SpellRegistry;
import com.wynncraftspellhider.models.texturepack.TexturepackModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.DisplayRenderer;
import net.minecraft.client.renderer.entity.state.DisplayEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

@Mixin(DisplayRenderer.class)
public abstract class DisplayRendererMixin {

    @WrapOperation(
            method = "submit",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/DisplayRenderer;submitInner(Lnet/minecraft/client/renderer/entity/state/DisplayEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;IF)V"
            )
    )
    private void wrapSubmitInner(DisplayRenderer<?, ?, ?> renderer,
                                 DisplayEntityRenderState state,
                                 PoseStack poseStack,
                                 SubmitNodeCollector collector,
                                 int lightCoords,
                                 float partialTick,
                                 Operation<Void> original) {
        // Look up the spell group
        SpellGroup group = null;
        int modelId = -1;
        if (state instanceof DisplayEntityRenderStateAccessor acc && acc.wynncraftspellhider_getModelId() != -1) {
            TexturepackModel model = Models.texturepackModel;
            if (model != null) {
                modelId = acc.wynncraftspellhider_getModelId();
                String texture = model.modelIdToKnownTexture.get(modelId);
                if (texture != null) {
                    String fingerprint = model.modelIdToFingerprint.get(modelId);
                    group = SpellRegistry.getGroupForModel(texture, fingerprint);
                }
            }
        }

        // Apply transformations and alpha wrapper if a group was found
        if (group != null) {
            poseStack.translate(group.offsetX, group.offsetY, group.offsetZ);
            poseStack.scale(group.scaleX, group.scaleY, group.scaleZ);
            if (group.transparency > 0.0f) {
                //WynncraftSpellHider.info("setting: " + group.name + " to: " + group.transparency);
                collector = new AlphaSubmitNodeCollector(collector, 1.0f - group.transparency);
            }
        }

        // Call the original submitInner method
        original.call(renderer, state, poseStack, collector, lightCoords, partialTick);
    }
}