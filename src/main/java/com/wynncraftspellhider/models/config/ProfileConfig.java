package com.wynncraftspellhider.models.config;

import com.google.gson.*;
import com.wynncraftspellhider.WynncraftSpellHider;
import com.wynncraftspellhider.models.particles.ParticleConfig;
import com.wynncraftspellhider.models.particles.ParticleRegistry;
import com.wynncraftspellhider.models.spells.SpellConfig;
import com.wynncraftspellhider.models.spells.SpellGroup;
import com.wynncraftspellhider.models.spells.SpellRegistry;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ProfileConfig {

    public String name;

    public final Map<String, SpellGroupSnapshot> spellGroups = new HashMap<>();
    public final Map<String, Boolean> particles = new HashMap<>();

    public ProfileConfig(String name) {
        this.name = name;
    }

    // -------------------------------------------------------------------------
    //  Snapshot types
    // -------------------------------------------------------------------------

    public static class SpellGroupSnapshot {
        public boolean hidden;
        public float scaleX, scaleY, scaleZ;
        public float offsetX, offsetY, offsetZ;
        public float alpha;

        public SpellGroupSnapshot(SpellGroup group) {
            this.hidden  = group.hidden;
            this.scaleX  = group.scaleX;
            this.scaleY  = group.scaleY;
            this.scaleZ  = group.scaleZ;
            this.offsetX = group.offsetX;
            this.offsetY = group.offsetY;
            this.offsetZ = group.offsetZ;
            this.alpha   = group.alpha;
        }

        // For deserialization
        public SpellGroupSnapshot() {}
    }

    // -------------------------------------------------------------------------
    //  Key helpers
    // -------------------------------------------------------------------------

    public static String groupKey(SpellRegistry.WynnClass wynnClass, SpellConfig spell, SpellGroup group) {
        return wynnClass.name() + '\0' + spell.name + '\0' + group.name;
    }

    public static String particleKey(ParticleConfig config) {
        return config.name;
    }

    // -------------------------------------------------------------------------
    //  Capture / apply
    // -------------------------------------------------------------------------

    public void capture() {
        spellGroups.clear();
        for (SpellRegistry.WynnClass wynnClass : SpellRegistry.WynnClass.values()) {
            for (SpellConfig spell : SpellRegistry.getSpells(wynnClass)) {
                for (SpellGroup group : spell.groups) {
                    spellGroups.put(groupKey(wynnClass, spell, group), new SpellGroupSnapshot(group));
                }
            }
        }

        particles.clear();
        for (ParticleConfig config : ParticleRegistry.getParticles()) {
            particles.put(particleKey(config), config.hidden);
        }
    }

    public void apply() {
        for (SpellRegistry.WynnClass wynnClass : SpellRegistry.WynnClass.values()) {
            for (SpellConfig spell : SpellRegistry.getSpells(wynnClass)) {
                for (SpellGroup group : spell.groups) {
                    SpellGroupSnapshot snap = spellGroups.get(groupKey(wynnClass, spell, group));
                    if (snap == null) continue;
                    group.hidden  = snap.hidden;
                    group.scaleX  = snap.scaleX;
                    group.scaleY  = snap.scaleY;
                    group.scaleZ  = snap.scaleZ;
                    group.offsetX = snap.offsetX;
                    group.offsetY = snap.offsetY;
                    group.offsetZ = snap.offsetZ;
                    group.alpha   = snap.alpha;
                }
            }
        }

        for (ParticleConfig config : ParticleRegistry.getParticles()) {
            Boolean hidden = particles.get(particleKey(config));
            if (hidden != null) config.hidden = hidden;
        }
    }

    // -------------------------------------------------------------------------
    //  Serialization
    // -------------------------------------------------------------------------

    public void save(File file) {
        JsonObject root = new JsonObject();
        root.addProperty("name", name);

        JsonObject groupsObj = new JsonObject();
        for (Map.Entry<String, SpellGroupSnapshot> entry : spellGroups.entrySet()) {
            SpellGroupSnapshot snap = entry.getValue();
            JsonObject g = new JsonObject();
            g.addProperty("hidden",  snap.hidden);
            g.addProperty("scaleX",  snap.scaleX);
            g.addProperty("scaleY",  snap.scaleY);
            g.addProperty("scaleZ",  snap.scaleZ);
            g.addProperty("offsetX", snap.offsetX);
            g.addProperty("offsetY", snap.offsetY);
            g.addProperty("offsetZ", snap.offsetZ);
            g.addProperty("alpha",   snap.alpha);
            groupsObj.add(entry.getKey(), g);
        }
        root.add("spellGroups", groupsObj);

        JsonObject particlesObj = new JsonObject();
        for (Map.Entry<String, Boolean> entry : particles.entrySet()) {
            particlesObj.addProperty(entry.getKey(), entry.getValue());
        }
        root.add("particles", particlesObj);

        try {
            file.getParentFile().mkdirs();
            try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
                new GsonBuilder().setPrettyPrinting().create().toJson(root, writer);
            }
        } catch (IOException e) {
            WynncraftSpellHider.error("Failed to save profile '" + name + "': " + e.getMessage());
        }
    }

    /**
     * Loads a ProfileConfig from a file. Returns null if the file is missing or malformed.
     */
    public static ProfileConfig load(File file) {
        if (!file.exists()) return null;
        try (Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();

            String name = root.get("name").getAsString();
            ProfileConfig profile = new ProfileConfig(name);

            JsonObject groupsObj = root.getAsJsonObject("spellGroups");
            if (groupsObj != null) {
                for (Map.Entry<String, JsonElement> entry : groupsObj.entrySet()) {
                    JsonObject g = entry.getValue().getAsJsonObject();
                    SpellGroupSnapshot snap = new SpellGroupSnapshot();
                    snap.hidden  = getOrDefault(g, "hidden",  false);
                    snap.scaleX  = getOrDefault(g, "scaleX",  1.0f);
                    snap.scaleY  = getOrDefault(g, "scaleY",  1.0f);
                    snap.scaleZ  = getOrDefault(g, "scaleZ",  1.0f);
                    snap.offsetX = getOrDefault(g, "offsetX", 0.0f);
                    snap.offsetY = getOrDefault(g, "offsetY", 0.0f);
                    snap.offsetZ = getOrDefault(g, "offsetZ", 0.0f);
                    snap.alpha   = getOrDefault(g, "alpha",   1.0f);
                    profile.spellGroups.put(entry.getKey(), snap);
                }
            }

            JsonObject particlesObj = root.getAsJsonObject("particles");
            if (particlesObj != null) {
                for (Map.Entry<String, JsonElement> entry : particlesObj.entrySet()) {
                    profile.particles.put(entry.getKey(), entry.getValue().getAsBoolean());
                }
            }

            return profile;
        } catch (Exception e) {
            WynncraftSpellHider.error("Failed to load profile from '" + file.getName() + "': " + e.getMessage());
            return null;
        }
    }

    private static float getOrDefault(JsonObject obj, String key, float def) {
        return obj.has(key) ? obj.get(key).getAsFloat() : def;
    }

    private static boolean getOrDefault(JsonObject obj, String key, boolean def) {
        return obj.has(key) ? obj.get(key).getAsBoolean() : def;
    }

}