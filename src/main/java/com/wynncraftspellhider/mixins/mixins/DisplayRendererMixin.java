package com.wynncraftspellhider.mixins.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.wynncraftspellhider.mixins.accessors.DisplayEntityRenderStateAccessor;
import com.wynncraftspellhider.mixins.wrappers.AlphaSubmitNodeCollector;
import com.wynncraftspellhider.models.Models;
import com.wynncraftspellhider.models.spells.SpellGroup;
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
            method = "submit*",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/DisplayRenderer;submitInner(Lnet/minecraft/client/renderer/entity/state/DisplayEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;IF)V"
            )
    )
    private void wrapSubmitInner(DisplayRenderer<?, ?, ?> renderer, DisplayEntityRenderState state, PoseStack poseStack, SubmitNodeCollector collector, int lightCoords, float partialTick, Operation<Void> original) {
        TexturepackModel model = Models.texturepackModel;
        SpellGroup group = null;

        if (model != null && state instanceof DisplayEntityRenderStateAccessor acc) {
            int modelId = acc.wynncraftspellhider_getModelId();
            if (modelId != -1) {
                group = model.getGroupForModelId(modelId);
            }
        }

        if (group != null) {
            poseStack.translate(group.offsetX, group.offsetY, group.offsetZ);
            poseStack.scale(group.scaleX, group.scaleY, group.scaleZ);
            if (group.transparency > 0.0f) {
                collector = new AlphaSubmitNodeCollector(collector, 1.0f - group.transparency);
            }
        }

        original.call(renderer, state, poseStack, collector, lightCoords, partialTick);
    }
}