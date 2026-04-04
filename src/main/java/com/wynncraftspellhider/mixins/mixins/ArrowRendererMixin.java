package com.wynncraftspellhider.mixins.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import com.wynncraftspellhider.models.Models;
import com.wynncraftspellhider.models.spells.SpellGroup;
import com.wynncraftspellhider.models.spells.SpellRegistry;
import com.wynncraftspellhider.models.texturepack.TexturepackModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.state.ArrowRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArrowRenderer.class)
public class ArrowRendererMixin {

    @Inject(
            method = "submit*",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V",
                    shift = At.Shift.AFTER
            )
    )
    private <S extends ArrowRenderState> void onAfterPushPose(
            S arrowRenderState,
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            CameraRenderState cameraRenderState,
            CallbackInfo ci
    ) {
        TexturepackModel texturepackModel = Models.texturepackModel;
        if (texturepackModel == null) return;

        SpellGroup group = SpellRegistry.getGroupForEntityType("arrow");
        if (group == null) return;

        poseStack.translate(group.offsetX, group.offsetY, group.offsetZ);
        poseStack.scale(group.scaleX, group.scaleY, group.scaleZ);
    }
}