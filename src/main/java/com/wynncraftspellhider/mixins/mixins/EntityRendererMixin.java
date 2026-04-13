package com.wynncraftspellhider.mixins.mixins;

import com.wynncraftspellhider.mixins.extensions.ArmorStandExtension;
import com.wynncraftspellhider.mixins.extensions.ItemDisplayExtension;
import com.wynncraftspellhider.mixins.extensions.TextDisplayExtension;
import com.wynncraftspellhider.models.Models;
import com.wynncraftspellhider.models.spells.SpellGroup;
import com.wynncraftspellhider.models.texturepack.TexturepackModel;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.client.renderer.entity.ArmorStandRenderer;

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

        // --- Arrow handling ---
        if (entity instanceof AbstractArrow) {
            TexturepackModel texturepackModel = Models.texturepackModel;
            if (texturepackModel != null && texturepackModel.isEntityTypeBlocked("arrow")) {
                cir.setReturnValue(false);
            }
            return;
        }

        // --- ItemDisplay handling ---
        if (entity instanceof Display.ItemDisplay itemDisplay) {
            SpellGroup group = ((ItemDisplayExtension) itemDisplay).wynncraftspellhider_getSpellGroup();

            if (group != null && (group.hidden || group.transparency >= 1.0f)) {
                cir.setReturnValue(false);
            }
            return;
        }

        // --- TextDisplay handling ---
        if (entity instanceof Display.TextDisplay textDisplay) {
            SpellGroup group = ((TextDisplayExtension) textDisplay).wynncraftspellhider_getSpellGroup();

            if (group != null && (group.hidden || group.transparency >= 1.0f)) {
                cir.setReturnValue(false);
            }
            return;
        }

        // --- ArmorStand handling ---
        if (entity instanceof ArmorStand armorStand) {
            SpellGroup group = ((ArmorStandExtension) armorStand).wynncraftspellhider_getSpellGroup();

            if (group != null && (group.hidden || group.transparency >= 1.0f)) {
                cir.setReturnValue(false);
            }
            return;
        }
    }
}