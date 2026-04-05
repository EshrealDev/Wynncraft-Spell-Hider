package com.wynncraftspellhider.mixins.mixins;

import com.wynncraftspellhider.mixins.accessors.DisplayEntityRenderStateAccessor;
import net.minecraft.client.renderer.entity.DisplayRenderer;
import net.minecraft.client.renderer.entity.state.ItemDisplayEntityRenderState;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.Display;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(DisplayRenderer.ItemDisplayRenderer.class)
public class ItemDisplayRendererMixin {

    @Inject(method = "extractRenderState*", at = @At("TAIL"))
    private void onExtractRenderState(
            Display.ItemDisplay itemDisplay,
            ItemDisplayEntityRenderState itemDisplayEntityRenderState,
            float f,
            CallbackInfo ci
    ) {

        ItemStack stack = itemDisplay.getItemStack();

        var customModelData = stack.get(DataComponents.CUSTOM_MODEL_DATA);

        if (customModelData == null) return;

        List<Float> floats = customModelData.floats();
        if (floats.isEmpty()) return;

        float modelIdFloat = floats.get(0);

        ((DisplayEntityRenderStateAccessor) itemDisplayEntityRenderState).wynncraftspellhider_setModelId((int) modelIdFloat);
    }

}