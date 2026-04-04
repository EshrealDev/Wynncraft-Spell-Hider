package com.wynncraftspellhider.mixins.mixins;

import com.wynncraftspellhider.mixins.accessors.DisplayEntityRenderStateAccessor;
import net.minecraft.client.renderer.entity.DisplayRenderer;
import net.minecraft.client.renderer.entity.state.ItemDisplayEntityRenderState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Display;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(DisplayRenderer.ItemDisplayRenderer.class)
public class ItemDisplayRendererMixin {

    @Inject(method = "extractRenderState*", at = @At("TAIL"))
    private void onExtractRenderState(
            Display.ItemDisplay itemDisplay,
            ItemDisplayEntityRenderState itemDisplayEntityRenderState,
            float f,
            CallbackInfo ci
    ) {
        ValueOutput view = TagValueOutput.createWithContext(
                ProblemReporter.DISCARDING,
                Objects.requireNonNull(net.minecraft.client.Minecraft.getInstance().getConnection()).registryAccess()
        );
        itemDisplay.saveWithoutId(view);
        CompoundTag nbt = ((TagValueOutput) view).buildResult();

        if (!nbt.contains("item")) return;

        CompoundTag components = nbt.getCompoundOrEmpty("item").getCompoundOrEmpty("components");
        if (!components.contains("minecraft:custom_model_data")) return;

        ListTag floats = components.getCompoundOrEmpty("minecraft:custom_model_data").getListOrEmpty("floats");
        if (floats.isEmpty()) return;

        float modelIdFloat = floats.getFloatOr(0, Float.MAX_VALUE);
        if (modelIdFloat == Float.MAX_VALUE) return;

        ((DisplayEntityRenderStateAccessor) itemDisplayEntityRenderState).wynncraftspellhider_setModelId((int) modelIdFloat);
    }
}