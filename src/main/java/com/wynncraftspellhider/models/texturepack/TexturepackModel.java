package com.wynncraftspellhider.models.texturepack;

import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.platform.NativeImage;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.GsonBuilder;
import com.google.gson.Gson;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.concurrent.CompletableFuture;

import com.wynncraftspellhider.WynncraftSpellHider;
import com.wynncraftspellhider.models.config.ConfigModel;
import com.wynncraftspellhider.models.spells.SpellGroup;
import com.wynncraftspellhider.models.spells.SpellRegistry;
import com.wynncraftspellhider.models.spells.SpellConfig;
import net.minecraft.util.Util;

public class TexturepackModel {
    public Map<Integer, String> modelIdToKnownTexture = new HashMap<>();
    public Map<Integer, String> modelIdToFingerprint = new HashMap<>();
    public Map<String, List<Integer>> knownTextureToModelIds = new HashMap<>();

    public Map<Integer, String> modelIdToJson = new HashMap<>();
    public Map<Integer, List<Identifier>> modelIdToTextureIds = new HashMap<>();

    private static final String HASH_JSON_FILENAME = "texture_hashes.json";
    private static final String JAR_HASH_ASSET = "/assets/wynncraftspellhider/" + HASH_JSON_FILENAME;

    public boolean isLoaded() {
        return !modelIdToKnownTexture.isEmpty();
    }

    public boolean isEntityTypeBlocked(String entityType) {
        for (SpellConfig spell : SpellRegistry.getAllSpells()) {
            for (SpellGroup group : spell.groups) {
                if (group.isEntityTypeMatch(entityType) && group.hidden) return true;
            }
        }
        return false;
    }

    public boolean isEntityBlocked(int customModelData) {
        String textureName = modelIdToKnownTexture.get(customModelData);
        if (textureName == null) return false;

        SpellGroup group = SpellRegistry.getGroupForModel(textureName, modelIdToFingerprint.get(customModelData));
        return group != null && group.hidden;
    }

    public static String sha256(String input) {
        try {
            byte[] fullHash = MessageDigest.getInstance("SHA-256").digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(fullHash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm missing", e);
        }
    }

    public static String sha256Pixels(int[] pixels) {
        return sha256(Arrays.toString(pixels));
    }

    public void listResourcesAsync() {
        CompletableFuture.runAsync(this::listResources, Util.ioPool())
                .exceptionally(e -> {
                    WynncraftSpellHider.error("Async texture load failed: " + e.getMessage());
                    return null;
                });
    }

    public void listResources() {
        modelIdToKnownTexture.clear();
        modelIdToFingerprint.clear();
        knownTextureToModelIds.clear();
        modelIdToJson.clear();
        modelIdToTextureIds.clear();

        ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
        Map<String, String> texturePathToKnownName = mapObfuscatedTextures(resourceManager);
        Identifier baseItemOverride = Identifier.fromNamespaceAndPath("minecraft", "models/item/oak_boat.json");
        Optional<Resource> overrideRes = resourceManager.getResource(baseItemOverride);

        if (overrideRes.isEmpty()) {
            WynncraftSpellHider.error("Could not find oak_boat.json override file!");
            return;
        }

        try (InputStream stream = overrideRes.get().open();
             InputStreamReader reader = new InputStreamReader(stream)) {

            JsonObject baseJson = JsonParser.parseReader(reader).getAsJsonObject();
            if (!baseJson.has("overrides")) return;

            JsonArray overrides = baseJson.getAsJsonArray("overrides");
            WynncraftSpellHider.info("Processing " + overrides.size() + " overrides from oak_boat.json");

            for (int i = 0; i < overrides.size(); i++) {
                JsonObject override = overrides.get(i).getAsJsonObject();
                int cmd = override.getAsJsonObject("predicate").get("custom_model_data").getAsInt();
                String modelPath = override.get("model").getAsString();

                processModelFile(resourceManager, cmd, modelPath, texturePathToKnownName);
            }
        } catch (Exception e) {
            WynncraftSpellHider.error("Failed to parse base item overrides");
            e.printStackTrace();
        }

        if (WynncraftSpellHider.devMode) {
            WynncraftSpellHider.info("=== Final knownTexture -> modelIDs map ===");
            for (Map.Entry<String, List<Integer>> entry : knownTextureToModelIds.entrySet()) {
                WynncraftSpellHider.info(entry.getKey() + " -> " + entry.getValue());
            }
        }
        // Check for known textures that never matched
        Map<String, String> knownHashes = loadKnownHashes();
        for (String name : knownHashes.keySet()) {
            if (!knownTextureToModelIds.containsKey(name)) {
                WynncraftSpellHider.error("Known texture never matched any resource pack texture: " + name);
            }
        }

        // Check for texture names registered in spells that were never matched
        Set<String> matchedTextures = knownTextureToModelIds.keySet();
        for (SpellConfig spell : SpellRegistry.getAllSpells()) {
            for (SpellGroup group : spell.groups) {
                for (String registeredTexture : group.getAllTextureNames()) {
                    if (!matchedTextures.contains(registeredTexture)) {
                        WynncraftSpellHider.error("Registered texture name in Registry never matched any resource pack texture: " + registeredTexture + " (Spell: " + spell.name + " -> Group: " + group.name + ")");
                    }
                }
            }
        }

        // Check for registered fingerprints that never matched
        for (SpellConfig spell : SpellRegistry.getAllSpells()) {
            for (SpellGroup group : spell.groups) {
                if (group.getAllFingerprints() == null) continue;
                for (String fp : group.getAllFingerprints()) {
                    boolean found = modelIdToFingerprint.values().stream().anyMatch(v -> v.equals(fp));
                    if (!found) {
                        WynncraftSpellHider.error("Registered fingerprint never matched any model: " + fp + " (group: " + group.name + " textures: " + group.getAllTextureNames() + ")");
                    }
                }
            }
        }
    }

    // --- Hash loading ---

    private Map<String, String> loadKnownHashes() {
        // Try config folder first
        File configJson = new File(ConfigModel.configFolder, HASH_JSON_FILENAME);
        if (configJson.exists()) {
            try (InputStreamReader reader = new InputStreamReader(new FileInputStream(configJson), StandardCharsets.UTF_8)) {
                JsonObject obj = JsonParser.parseReader(reader).getAsJsonObject();
                Map<String, String> result = new HashMap<>();
                for (String key : obj.keySet()) {
                    result.put(key, obj.get(key).getAsString());
                }
                WynncraftSpellHider.info("Loaded texture hashes from config folder (" + result.size() + " entries)");
                return result;
            } catch (Exception e) {
                WynncraftSpellHider.error("Failed to load texture hashes from config folder, falling back to JAR: " + e.getMessage());
            }
        }

        // Fallback: bundled JAR asset
        try (InputStream is = TexturepackModel.class.getResourceAsStream(JAR_HASH_ASSET)) {
            if (is == null) {
                WynncraftSpellHider.error("No texture_hashes.json found in JAR either!");
                return new HashMap<>();
            }
            JsonObject obj = JsonParser.parseReader(new InputStreamReader(is, StandardCharsets.UTF_8)).getAsJsonObject();
            Map<String, String> result = new HashMap<>();
            for (String key : obj.keySet()) {
                result.put(key, obj.get(key).getAsString());
            }
            WynncraftSpellHider.info("Loaded texture hashes from JAR (" + result.size() + " entries)");
            return result;
        } catch (Exception e) {
            WynncraftSpellHider.error("Failed to load texture hashes from JAR: " + e.getMessage());
            return new HashMap<>();
        }
    }

    // --- Texture mapping ---

    private Map<String, String> mapObfuscatedTextures(ResourceManager rm) {
        Map<String, String> pathToKnown = new HashMap<>();
        Map<String, String> knownHashes = loadKnownHashes();

        if (knownHashes.isEmpty()) {
            WynncraftSpellHider.error("No known texture hashes loaded — texture matching will not work!");
            return pathToKnown;
        }

        // Build reverse map: hash -> known name
        Map<String, String> hashToKnownName = new HashMap<>();
        for (Map.Entry<String, String> entry : knownHashes.entrySet()) {
            hashToKnownName.put(entry.getValue(), entry.getKey());
        }

        Map<Identifier, Resource> resources = rm.listResources("textures/item", id -> id.getPath().endsWith(".png"));
        WynncraftSpellHider.info("textures count: " + resources.size());

        for (Map.Entry<Identifier, Resource> entry : resources.entrySet()) {
            try (InputStream stream = entry.getValue().open();
                 NativeImage img = NativeImage.read(stream)) {

                String hash = sha256Pixels(getPixels(img));
                if (hashToKnownName.containsKey(hash)) {
                    String knownName = hashToKnownName.get(hash);
                    String logicalPath = entry.getKey().getPath()
                            .replaceFirst("^textures/", "")
                            .replaceFirst("\\.png$", "");
                    pathToKnown.put(logicalPath, knownName);
                    if (WynncraftSpellHider.devMode) WynncraftSpellHider.info("HASH MATCH: " + entry.getKey() + " -> " + knownName);
                }
            } catch (IOException ignored) {}
        }

        return pathToKnown;
    }

    /**
     * DEV ONLY — reads textureCacheFolder PNGs, computes pixel hashes, and writes
     * texture_hashes.json to the config folder. Run this locally to regenerate the
     * hash file whenever Wynncraft updates their textures. Never call this in production.
     */
    public void encodeTextureCacheToJson() {
        File textureCacheFolder = new File(ConfigModel.configFolder, "textureCache");
        if (!textureCacheFolder.exists()) {
            WynncraftSpellHider.error("textureCache folder not found at: " + textureCacheFolder.getAbsolutePath());
            return;
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonObject output = new JsonObject();

        for (File file : collectPngFiles(textureCacheFolder)) {
            String name = file.getName().replace(".png", "");
            try (InputStream is = new FileInputStream(file);
                 NativeImage img = NativeImage.read(is)) {
                String hash = sha256Pixels(getPixels(img));
                output.addProperty(name, hash);
                WynncraftSpellHider.info("Encoded: " + name + " -> " + hash);
            } catch (IOException e) {
                WynncraftSpellHider.error("Failed to encode texture: " + file.getName());
            }
        }

        File outFile = new File(ConfigModel.configFolder, HASH_JSON_FILENAME);
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(outFile), StandardCharsets.UTF_8)) {
            gson.toJson(output, writer);
            WynncraftSpellHider.info("Written texture_hashes.json to: " + outFile.getAbsolutePath());
        } catch (IOException e) {
            WynncraftSpellHider.error("Failed to write texture_hashes.json: " + e.getMessage());
        }
    }

    private static List<File> collectPngFiles(File folder) {
        List<File> result = new ArrayList<>();
        if (folder == null || !folder.exists()) return result;

        File[] entries = folder.listFiles();
        if (entries == null) return result;

        for (File entry : entries) {
            if (entry.isDirectory()) {
                result.addAll(collectPngFiles(entry));
            } else if (entry.getName().endsWith(".png")) {
                result.add(entry);
            }
        }
        return result;
    }

    // --- Shared helpers ---

    private void processModelFile(ResourceManager rm, int cmd, String modelPath, Map<String, String> textureMapping) {
        String path = modelPath.contains(":") ? modelPath.split(":")[1] : modelPath;
        Identifier modelId = Identifier.fromNamespaceAndPath("minecraft", "models/" + path + ".json");

        rm.getResource(modelId).ifPresent(res -> {
            try (InputStream stream = res.open();
                 InputStreamReader reader = new InputStreamReader(stream)) {

                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                modelIdToJson.put(cmd, json.toString());

                String fingerprint = "none";
                if (!json.has("elements")) return;

                JsonArray elements = json.getAsJsonArray("elements");
                fingerprint = sha256(elements.toString());
                modelIdToFingerprint.put(cmd, fingerprint);

                Set<String> referencedSlots = new LinkedHashSet<>();
                for (int i = 0; i < elements.size(); i++) {
                    JsonObject element = elements.get(i).getAsJsonObject();
                    if (!element.has("faces")) continue;
                    JsonObject faces = element.getAsJsonObject("faces");
                    for (String faceKey : faces.keySet()) {
                        JsonObject face = faces.getAsJsonObject(faceKey);
                        if (!face.has("texture")) continue;
                        String ref = face.get("texture").getAsString();
                        if (ref.startsWith("#")) {
                            referencedSlots.add(ref.substring(1));
                        }
                    }
                }

                if (!json.has("textures")) return;
                JsonObject textures = json.getAsJsonObject("textures");

                Set<String> matchedKnownNames = new LinkedHashSet<>();
                for (String slot : referencedSlots) {
                    if (!textures.has(slot)) continue;
                    String texPath = textures.get(slot).getAsString();
                    String cleanTexPath = texPath.contains(":") ? texPath.split(":")[1] : texPath;

                    String ns = texPath.contains(":") ? texPath.split(":")[0] : "minecraft";
                    Identifier texIdentifier = Identifier.fromNamespaceAndPath(ns, "textures/" + cleanTexPath + ".png");
                    modelIdToTextureIds.computeIfAbsent(cmd, k -> new ArrayList<>()).add(texIdentifier);

                    if (textureMapping.containsKey(cleanTexPath)) {
                        matchedKnownNames.add(textureMapping.get(cleanTexPath));
                    }
                }

                if (matchedKnownNames.isEmpty()) return;

                if (matchedKnownNames.size() > 1) {
                    WynncraftSpellHider.error("WARNING: model ID " + cmd + " has elements referencing multiple known textures: " + matchedKnownNames + " - using first");
                }

                String knownName = matchedKnownNames.iterator().next();
                modelIdToKnownTexture.put(cmd, knownName);
                knownTextureToModelIds.computeIfAbsent(knownName, k -> new ArrayList<>()).add(cmd);
                if (WynncraftSpellHider.devMode) WynncraftSpellHider.info("MAPPED: " + knownName + " -> model ID " + cmd + " fingerprint: " + fingerprint + " json: " + json);

            } catch (Exception e) {
                WynncraftSpellHider.error("Failed to parse model JSON: " + modelId);
            }
        });
    }

    private int[] getPixels(NativeImage img) {
        int width = img.getWidth();
        int height = img.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                pixels[y * width + x] = img.getPixel(x, y);
            }
        }
        return pixels;
    }
}