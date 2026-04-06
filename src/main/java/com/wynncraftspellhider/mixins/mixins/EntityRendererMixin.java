package com.wynncraftspellhider.mixins.mixins;

import com.wynncraftspellhider.models.Models;
import com.wynncraftspellhider.models.spells.SpellGroup;
import com.wynncraftspellhider.models.texturepack.TexturepackModel;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.Display.ItemDisplay;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;



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
        if (!(entity instanceof ItemDisplay display)) return;

        ItemStack stack = display.getItemStack();
        if (stack.isEmpty()) return;

        var customModelData = stack.get(DataComponents.CUSTOM_MODEL_DATA);
        if (customModelData == null) return;

        List<Float> floats = customModelData.floats();
        if (floats.isEmpty()) return;

        float modelIdFloat = floats.get(0);
        SpellGroup group = texturepackModel.getGroupForModelId((int) modelIdFloat);

        if (group != null && (group.hidden || group.transparency >= 1.0f)) {
            cir.setReturnValue(false);
        }
    }
}