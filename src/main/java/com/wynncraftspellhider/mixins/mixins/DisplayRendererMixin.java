package com.wynncraftspellhider.mixins.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import com.wynncraftspellhider.mixins.accessors.DisplayEntityRenderStateAccessor;
import com.wynncraftspellhider.mixins.wrappers.AlphaSubmitNodeCollector;
import com.wynncraftspellhider.models.Models;
import com.wynncraftspellhider.models.spells.SpellGroup;
import com.wynncraftspellhider.models.spells.SpellRegistry;
import com.wynncraftspellhider.models.texturepack.TexturepackModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.DisplayRenderer;
import net.minecraft.client.renderer.entity.state.DisplayEntityRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DisplayRenderer.class)
public class DisplayRendererMixin {

    private static final ThreadLocal<SpellGroup> CURRENT_GROUP = new ThreadLocal<>();

    @Inject(method = "submit*", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/entity/DisplayRenderer;submitInner(Lnet/minecraft/client/renderer/entity/state/DisplayEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;IF)V"
    ))
    private <ST extends DisplayEntityRenderState> void onBeforeSubmitInner(
            ST displayEntityRenderState,
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            CameraRenderState cameraRenderState,
            CallbackInfo ci
    ) {
        CURRENT_GROUP.set(null);
        TexturepackModel texturepackModel = Models.texturepackModel;
        if (texturepackModel == null) return;

        int modelId = ((DisplayEntityRenderStateAccessor) displayEntityRenderState).wynncraftspellhider_getModelId();
        if (modelId == -1) return;

        String textureName = texturepackModel.modelIdToKnownTexture.get(modelId);
        if (textureName == null) return;

        String fingerprint = texturepackModel.modelIdToFingerprint.get(modelId);
        SpellGroup group = SpellRegistry.getGroupForModel(textureName, fingerprint);
        if (group == null) return;

        poseStack.translate(group.offsetX, group.offsetY, group.offsetZ);
        poseStack.scale(group.scaleX, group.scaleY, group.scaleZ);
        CURRENT_GROUP.set(group);
    }

    @ModifyArg(
            method = "submit*",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/DisplayRenderer;submitInner(Lnet/minecraft/client/renderer/entity/state/DisplayEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;IF)V"
            ),
            index = 2
    )
    private SubmitNodeCollector wrapCollectorForAlpha(SubmitNodeCollector submitNodeCollector) {
        SpellGroup group = CURRENT_GROUP.get();
        if (group == null || group.alpha >= 1.0f) return submitNodeCollector;
        return new AlphaSubmitNodeCollector(submitNodeCollector, group.alpha);
    }
}