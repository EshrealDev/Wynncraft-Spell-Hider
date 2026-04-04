package com.wynncraftspellhider.mixins.mixins;

import com.wynncraftspellhider.models.Models;
import com.wynncraftspellhider.models.texturepack.TexturepackModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Display.ItemDisplay;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.Objects;



@Mixin(EntityRenderer.class)
public class EntityRendererMixin {

    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    private <T extends Entity> void onShouldRender(
            T entity,
            Frustum frustum,
            double x,
            double y,
            double z,
            CallbackInfoReturnable<Boolean> cir
    ) {
        TexturepackModel texturepackModel = Models.texturepackModel;
        if (texturepackModel == null) return;

        // --- Arrow handling ---
        if (entity instanceof AbstractArrow) {
            if (texturepackModel.isEntityTypeBlocked("arrow")) {
                cir.setReturnValue(false);
            }
            return;
        }

        // --- ItemDisplay handling ---
        if (!(entity instanceof ItemDisplay)) return;

        ValueOutput view = TagValueOutput.createWithContext(
                ProblemReporter.DISCARDING,
                Objects.requireNonNull(Minecraft.getInstance().getConnection()).registryAccess()
        );
        entity.saveWithoutId(view);
        CompoundTag nbt = ((TagValueOutput) view).buildResult();

        if (!nbt.contains("item")) return;
        CompoundTag components = nbt.getCompoundOrEmpty("item").getCompoundOrEmpty("components");

        if (!components.contains("minecraft:custom_model_data")) return;
        ListTag floats = components.getCompoundOrEmpty("minecraft:custom_model_data").getListOrEmpty("floats");
        if (floats.isEmpty()) return;

        float modelIdFloat = floats.getFloatOr(0, Float.MAX_VALUE);
        if (modelIdFloat == Float.MAX_VALUE) return;

        if (texturepackModel.isEntityBlocked((int) modelIdFloat)) {
            cir.setReturnValue(false);
        }
    }
}