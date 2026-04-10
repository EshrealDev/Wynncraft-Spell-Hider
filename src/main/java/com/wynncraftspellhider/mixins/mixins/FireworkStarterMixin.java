package com.wynncraftspellhider.mixins.mixins;

import com.wynncraftspellhider.models.particles.ParticleRegistry;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.particle.FireworkParticles;
import net.minecraft.core.particles.ParticleTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FireworkParticles.Starter.class)
public class FireworkStarterMixin {

    @Inject(
            method = "createParticle(DDDDDDLit/unimi/dsi/fastutil/ints/IntList;Lit/unimi/dsi/fastutil/ints/IntList;ZZ)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void blockFireworkParticles(
            double x, double y, double z,
            double vx, double vy, double vz,
            IntList colors, IntList fadeColors,
            boolean trail, boolean twinkle,
            CallbackInfo ci
    ) {
        if (ParticleRegistry.isHidden(ParticleTypes.FIREWORK)) {
            ci.cancel();
        }
    }
}