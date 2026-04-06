package com.wynncraftspellhider.mixins.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.wynncraftspellhider.models.Models;
import com.wynncraftspellhider.models.spells.SpellGroup;
import com.wynncraftspellhider.models.texturepack.TexturepackModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.state.ArrowRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.util.ARGB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArrowRenderer.class)
public abstract class ArrowRendererMixin {


    @Inject(
            method = "submit(Lnet/minecraft/client/renderer/entity/state/ArrowRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V", shift = At.Shift.AFTER)
    )
    private void applyTransforms(ArrowRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera, CallbackInfo ci) {
        TexturepackModel texturepackModel = Models.texturepackModel;
        if (texturepackModel == null) return;

        SpellGroup group = texturepackModel.getGroupForEntityType("arrow");
        if (group == null) return;

        poseStack.translate(group.offsetX, group.offsetY, group.offsetZ);
        poseStack.scale(group.scaleX, group.scaleY, group.scaleZ);
    }

    /*
    //this does not work for transparency.
    @WrapOperation(
            method = "submit(Lnet/minecraft/client/renderer/entity/state/ArrowRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/rendertype/RenderType;IIILnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V")
    )
    private void wrapInternalModel(SubmitNodeCollector instance, net.minecraft.client.model.Model<?> model, Object state, PoseStack stack, net.minecraft.client.renderer.rendertype.RenderType rt, int light, int overlay, int color, net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay crumbling, Operation<Void> original) {
        TexturepackModel texturepackModel = Models.texturepackModel;
        if (model == null) return;

        SpellGroup group = texturepackModel.getGroupForEntityType("arrow");
        if (group == null) return;

        if (group.transparency > 0.0f) {
            instance = new com.wynncraftspellhider.mixins.wrappers.AlphaSubmitNodeCollector(instance, 1.0f - group.transparency);
        }

        original.call(instance, model, state, stack, rt, light, overlay, color, crumbling);
    }

     */
}