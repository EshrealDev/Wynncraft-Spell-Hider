package com.wynncraftspellhider.mixins.mixins;

import com.wynncraftspellhider.mixins.accessors.DisplayEntityRenderStateAccessor;
import net.minecraft.client.renderer.entity.state.DisplayEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(DisplayEntityRenderState.class)
public class DisplayEntityRenderStateMixin implements DisplayEntityRenderStateAccessor {

    @Unique
    private int wynncraftspellhider_modelId = -1;

    @Override
    public int wynncraftspellhider_getModelId() {
        return wynncraftspellhider_modelId;
    }

    @Override
    public void wynncraftspellhider_setModelId(int id) {
        wynncraftspellhider_modelId = id;
    }
}