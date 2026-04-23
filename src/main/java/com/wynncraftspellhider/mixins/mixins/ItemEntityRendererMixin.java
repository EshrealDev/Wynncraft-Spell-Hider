package com.wynncraftspellhider.mixins.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import com.wynncraftspellhider.mixins.extensions.ItemEntityExtension;
import com.wynncraftspellhider.mixins.extensions.ItemEntityRenderStateExtension;
import com.wynncraftspellhider.models.spells.SpellGroup;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.state.ItemEntityRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntityRenderer.class)
public abstract class ItemEntityRendererMixin {

    @Inject(
            method = "extractRenderState*",
            at = @At("TAIL")
    )
    private void onExtractRenderState(
            ItemEntity itemEntity,
            ItemEntityRenderState itemEntityRenderState,
            float f,
            CallbackInfo ci
    ) {
        SpellGroup group = ((ItemEntityExtension) itemEntity).wynncraftspellhider_getSpellGroup();
        ((ItemEntityRenderStateExtension) itemEntityRenderState).wynncraftspellhider_setSpellGroup(group);
    }

    @Inject(
            method = "submit(Lnet/minecraft/client/renderer/entity/state/ItemEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V", shift = At.Shift.AFTER)
    )
    private void applyTransforms(ItemEntityRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera, CallbackInfo ci) {
        SpellGroup group = state instanceof ItemEntityRenderStateExtension ext ? ext.wynncraftspellhider_getSpellGroup() : null;

        if (group != null) {
            poseStack.translate(group.offsetX, group.offsetY, group.offsetZ);
            poseStack.scale(group.scaleX, group.scaleY, group.scaleZ);
        }
    }
}