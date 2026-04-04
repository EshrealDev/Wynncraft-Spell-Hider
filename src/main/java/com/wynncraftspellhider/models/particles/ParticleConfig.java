package com.wynncraftspellhider.models.particles;

import net.minecraft.core.particles.ParticleType;

/**
 * Represents a single configurable particle entry in the ParticleRegistry.
 * Analogous to SpellConfig but flat — no groups, no transforms.
 */
public class ParticleConfig {

    public final String name;
    public final ParticleType<?> particleType;
    public final String description; // null = no info button shown
    public boolean hidden;

    public ParticleConfig(String name, ParticleType<?> particleType) {
        this.name = name;
        this.particleType = particleType;
        this.description = null;
        this.hidden = false;
    }

    public ParticleConfig(String name, ParticleType<?> particleType, String description) {
        this.name = name;
        this.particleType = particleType;
        this.description = description;
        this.hidden = false;
    }
}