package com.wynncraftspellhider.mixins.mixins;


import com.wynncraftspellhider.models.particles.ParticleRegistry;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.core.Direction;

@Mixin(ClientLevel.class)
public class  ClientLevelMixin {

    @Inject(
            method = "addDestroyBlockEffect",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onAddDestroyBlockEffect(BlockPos blockPos, BlockState blockState, CallbackInfo ci) {
        if (ParticleRegistry.isHidden(ParticleTypes.BLOCK)) {
            ci.cancel();
        }
    }

    @Inject(
            method = "addBreakingBlockEffect",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onAddBreakingBlockEffect(BlockPos blockPos, Direction direction, CallbackInfo ci) {
        if (ParticleRegistry.isHidden(ParticleTypes.BLOCK)) {
            ci.cancel();
        }
    }
}