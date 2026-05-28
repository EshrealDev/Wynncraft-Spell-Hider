package com.wynncraftspellhider.models.spell;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.wynncraftspellhider.WynncraftSpellHider;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class SpellModelLoader {

    private static final String REGISTRY_FILENAME = "spell_registry.json";

    public static Map<SpellModel.WynnClass, List<SpellConfig>> load(Path configDir) {
        Map<SpellModel.WynnClass, List<SpellConfig>> fromDisk = loadFromDisk(configDir);
        if (fromDisk != null) {
            WynncraftSpellHider.info("Loaded spell registry from disk.");
            return fromDisk;
        }

        WynncraftSpellHider.info("Loaded spell registry from bundled jar assets.");
        return loadFromAssets();
    }

    private static Map<SpellModel.WynnClass, List<SpellConfig>> loadFromDisk(Path configDir) {
        Path registryPath = configDir.resolve(REGISTRY_FILENAME);

        if (Files.exists(registryPath)) {
            try {
                String json = Files.readString(registryPath, StandardCharsets.UTF_8);
                JsonObject root = new Gson().fromJson(json, JsonObject.class);
                return SpellModelDeserializer.deserialize(root);
            } catch (Exception e) {
                throw new RuntimeException("Failed to load registry from disk", e);
            }
        }

        return null;
    }

    private static Map<SpellModel.WynnClass, List<SpellConfig>> loadFromAssets() {
        try {
            JsonObject root = getBundledRegistryJson();
            return SpellModelDeserializer.deserialize(root);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load bundled spell registry", e);
        }
    }

    private static JsonObject getBundledRegistryJson() throws Exception {
        try (InputStream is = WynncraftSpellHider.getModResourceAsStream(REGISTRY_FILENAME)) {
            if (is == null) {
                throw new IllegalStateException(
                        "Bundled " + REGISTRY_FILENAME + " not found in assets/wynncraftspellhider");
            }
            String json = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            return new Gson().fromJson(json, JsonObject.class);
        }
    }
}
