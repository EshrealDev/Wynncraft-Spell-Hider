package com.wynncraftspellhider.mixins.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.wynncraftspellhider.mixins.extensions.ArmorStandExtension;
import com.wynncraftspellhider.mixins.extensions.ArmorStandRenderStateExtension;
import com.wynncraftspellhider.mixins.wrappers.AlphaSubmitNodeCollector;
import com.wynncraftspellhider.models.spells.SpellGroup;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.ArmorStandRenderer;
import net.minecraft.client.renderer.entity.state.ArmorStandRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArmorStandRenderer.class)
public class ArmorStandRendererMixin {
    @Inject(method = "extractRenderState*", at = @At("TAIL"))
    private void onExtractRenderState(
            ArmorStand armorStand,
            ArmorStandRenderState armorStandRenderState,
            float f,
            CallbackInfo ci
    ) {
        SpellGroup group = ((ArmorStandExtension) armorStand).wynncraftspellhider_getSpellGroup();
        ((ArmorStandRenderStateExtension) armorStandRenderState).wynncraftspellhider_setSpellGroup(group);
    }

    @WrapOperation(
            method = "submit(Lnet/minecraft/client/renderer/entity/state/ArmorStandRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;submit(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V"
            )
    )
    private void wrapSubmit(
            ArmorStandRenderer renderer,
            LivingEntityRenderState state,
            PoseStack poseStack,
            SubmitNodeCollector collector,
            CameraRenderState cameraRenderState,
            Operation<Void> original
    ) {
        SpellGroup group = state instanceof ArmorStandRenderStateExtension ext ? ext.wynncraftspellhider_getSpellGroup() : null;

        if (group != null) {
            poseStack.translate(group.offsetX, group.offsetY, group.offsetZ);
            poseStack.scale(group.scaleX, group.scaleY, group.scaleZ);
            //this does not work
            /*
            if (group.transparency > 0.0f) {
                collector = new AlphaSubmitNodeCollector(collector, 1.0f - group.transparency);
            }
            */
        }

        original.call(renderer, state, poseStack, collector, cameraRenderState);
    }
}