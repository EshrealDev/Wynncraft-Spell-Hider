package com.wynncraftspellhider.models.texturepack;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Display.ItemDisplay;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;

import com.wynncraftspellhider.models.Models;
import com.wynncraftspellhider.WynncraftSpellHider;
import com.wynncraftspellhider.models.config.ConfigModel;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueOutput;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.Objects;

public class EntityDebugHelper {

    private static final int RECORD_TICKS = 10 * 20;

    // Single source of truth: model ID -> all texture identifiers for that model
    // Uses LinkedHashMap to preserve insertion order for consistent output
    public static Map<Integer, List<Identifier>> lastSeenModelToIds = new LinkedHashMap<>();

    private static int recordingTicksRemaining = 0;

    public static void frameUpdate() {
        if (recordingTicksRemaining <= 0) return;
        scanEntities();
    }

    public static void tickUpdate() {
        if (recordingTicksRemaining <= 0) return;
        recordingTicksRemaining--;

        if (recordingTicksRemaining == 0) {
            saveDebugResults();
            WynncraftSpellHider.info("Recording finished. Results saved.");
        }
    }

    /** Called when the debug key is pressed. Restarts the recording window. */
    public static void startRecording() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            WynncraftSpellHider.info("No level loaded.");
            return;
        }

        TexturepackModel tpm = Models.texturepackModel;
        if (!tpm.isLoaded()) {
            WynncraftSpellHider.info("TexturepackModel not loaded yet — cannot start recording.");
            return;
        }

        lastSeenModelToIds.clear();
        recordingTicksRemaining = RECORD_TICKS;
        WynncraftSpellHider.info("Recording started " + RECORD_TICKS / 20 + "s");
    }

    private static void scanEntities() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        TexturepackModel tpm = Models.texturepackModel;

        for (Entity entity : mc.level.entitiesForRendering()) {
            if (!(entity instanceof ItemDisplay)) continue;

            ValueOutput view = TagValueOutput.createWithContext(
                    ProblemReporter.DISCARDING,
                    Objects.requireNonNull(mc.getConnection()).registryAccess()
            );
            entity.saveWithoutId(view);
            CompoundTag nbt = ((TagValueOutput) view).buildResult();

            if (!nbt.contains("item")) continue;
            CompoundTag item = nbt.getCompoundOrEmpty("item");
            if (!item.contains("components")) continue;
            CompoundTag components = item.getCompoundOrEmpty("components");
            if (!components.contains("minecraft:custom_model_data")) continue;
            CompoundTag customModelData = components.getCompoundOrEmpty("minecraft:custom_model_data");
            if (!customModelData.contains("floats")) continue;
            ListTag floats = customModelData.getListOrEmpty("floats");
            if (floats.isEmpty()) continue;

            float modelIdFloat = floats.getFloatOr(0, Float.MAX_VALUE);
            if (modelIdFloat == Float.MAX_VALUE) continue;
            int modelId = (int) modelIdFloat;

            if (tpm.isEntityBlocked(modelId)) continue;

            // Already recorded this model ID this session — skip
            if (lastSeenModelToIds.containsKey(modelId)) continue;

            List<Identifier> textureIds = tpm.modelIdToTextureIds.get(modelId);
            String jsonStr = tpm.modelIdToJson.get(modelId);

            if (textureIds != null && !textureIds.isEmpty()) {
                lastSeenModelToIds.put(modelId, textureIds);

                String textureName = tpm.modelIdToKnownTexture.get(modelId);
                if (textureName != null) {
                    WynncraftSpellHider.info("id: " + modelId + " known: " + textureName + " json: " + jsonStr);
                } else {
                    String firstName = getFilename(textureIds.get(0));
                    WynncraftSpellHider.info("id: " + modelId + " png: (unknown) " + firstName + " json: " + jsonStr);
                }
            } else if (jsonStr != null) {
                WynncraftSpellHider.info("id: " + modelId + " png: (no textures resolved) json: " + jsonStr);
            } else {
                WynncraftSpellHider.info("id: " + modelId + " png: (no model found)");
            }
        }
    }

    private static void saveDebugResults() {
        if (ConfigModel.configFolder == null) return;

        TexturepackModel tpm = Models.texturepackModel;

        File outputFile = new File(ConfigModel.configFolder, "entity_debug.txt");
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(
                new FileOutputStream(outputFile), StandardCharsets.UTF_8))) {

            writer.println("=== Entity Debug Results ===");
            writer.println();

            // Summary: one line per model, grouped by label
            writer.println("--- Seen textures summary ---");
            for (Map.Entry<Integer, List<Identifier>> entry : lastSeenModelToIds.entrySet()) {
                int modelId = entry.getKey();
                String label = getLabel(tpm, modelId, entry.getValue());
                writer.println("  " + label + " (model ID: " + modelId + ")");
            }
            writer.println();

            // Detail: one block per model ID
            for (Map.Entry<Integer, List<Identifier>> entry : lastSeenModelToIds.entrySet()) {
                int modelId = entry.getKey();
                List<Identifier> textureIds = entry.getValue();
                String label = getLabel(tpm, modelId, textureIds);

                writer.println("Texture: " + label);

                if (textureIds.size() > 1) {
                    List<String> names = new ArrayList<>();
                    for (Identifier id : textureIds) {
                        names.add(getFilename(id));
                    }
                    writer.println("  shared textures: " + names);
                }

                String fingerprint = tpm.modelIdToFingerprint.getOrDefault(modelId, "none");
                writer.println("  model ID: " + modelId + " fingerprint: " + fingerprint);

                String json = tpm.modelIdToJson.get(modelId);
                if (json != null) {
                    writer.println("  json: " + json);
                }
                writer.println();
            }

            // Export all unique texture PNGs
            File texturesDir = new File(ConfigModel.configFolder, "debug_textures");
            if (!texturesDir.exists()) texturesDir.mkdirs();

            ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
            Set<Identifier> exportedIds = new LinkedHashSet<>();
            for (List<Identifier> ids : lastSeenModelToIds.values()) {
                exportedIds.addAll(ids);
            }

            for (Identifier textureId : exportedIds) {
                resourceManager.getResource(textureId).ifPresent(resource -> {
                    String filename = getFilename(textureId);
                    if (!filename.endsWith(".png")) filename += ".png";
                    File outFile = new File(texturesDir, filename);

                    try (InputStream in = resource.open();
                         FileOutputStream out = new FileOutputStream(outFile)) {
                        in.transferTo(out);
                    } catch (IOException e) {
                        WynncraftSpellHider.error("Failed to export texture " + filename + ": " + e.getMessage());
                    }
                });
            }

            WynncraftSpellHider.info("Debug results saved to: " + outputFile.getAbsolutePath());
        } catch (IOException e) {
            WynncraftSpellHider.error("Failed to save debug results: " + e.getMessage());
        }
    }

    /** Returns the display label for a model — known texture name if mapped, otherwise first obfuscated filename. */
    private static String getLabel(TexturepackModel tpm, int modelId, List<Identifier> textureIds) {
        String known = tpm.modelIdToKnownTexture.get(modelId);
        if (known != null) return known;
        if (textureIds != null && !textureIds.isEmpty()) return getFilename(textureIds.get(0));
        return "(unknown)";
    }

    /** Extracts the bare filename (without path, without .png) from an Identifier. */
    private static String getFilename(Identifier id) {
        String path = id.getPath();
        String name = path.substring(path.lastIndexOf('/') + 1);
        if (name.endsWith(".png")) name = name.substring(0, name.length() - 4);
        return name;
    }


    public static void copyEntitiesToClipboard() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            WynncraftSpellHider.info("No level loaded.");
            return;
        }

        TexturepackModel tpm = Models.texturepackModel;
        StringBuilder displaySb = new StringBuilder("=== ITEM DISPLAYS ===\n\n");
        StringBuilder otherSb = new StringBuilder("=== OTHER ENTITIES (PROJECTILES/MOB/ETC) ===\n\n");

        int displayCount = 0;
        int otherCount = 0;

        for (Entity entity : mc.level.entitiesForRendering()) {
            // Extract NBT for any entity type
            ValueOutput view = TagValueOutput.createWithContext(
                    ProblemReporter.DISCARDING,
                    Objects.requireNonNull(mc.getConnection()).registryAccess()
            );
            entity.saveWithoutId(view);
            CompoundTag nbt = ((TagValueOutput) view).buildResult();

            if (entity instanceof ItemDisplay) {
                displayCount++;
                displaySb.append("--- Display #").append(displayCount)
                        .append(" (ID: ").append(entity.getId()).append(") ---\n");
                displaySb.append("Type: ").append(entity.getType().getDescriptionId()).append("\n");
                displaySb.append("Pos: ").append(entity.position()).append("\n");

                // ItemDisplay specific model logic
                if (nbt.contains("item")) {
                    CompoundTag item = nbt.getCompoundOrEmpty("item");
                    if (item.contains("components")) {
                        CompoundTag components = item.getCompoundOrEmpty("components");
                        if (components.contains("minecraft:custom_model_data")) {
                            CompoundTag cmd = components.getCompoundOrEmpty("minecraft:custom_model_data");
                            ListTag floats = cmd.getListOrEmpty("floats");
                            if (!floats.isEmpty()) {
                                int modelId = (int) floats.getFloatOr(0, 0);
                                displaySb.append("  [Model ID: ").append(modelId).append("]\n");
                                displaySb.append("  [Known: ").append(tpm.modelIdToKnownTexture.getOrDefault(modelId, "Unknown")).append("]\n");
                            }
                        }
                    }
                }
                displaySb.append("Full NBT: ").append(nbt).append("\n\n");

            } else {
                // General entities (Arrows, Players, Spells, etc.)
                otherCount++;
                otherSb.append("--- Entity #").append(otherCount)
                        .append(" (ID: ").append(entity.getId()).append(") ---\n");
                otherSb.append("Type: ").append(entity.getType().toString()).append("\n");
                otherSb.append("Pos: ").append(entity.getX()).append(", ").append(entity.getY()).append(", ").append(entity.getZ()).append("\n");
                otherSb.append("Full NBT: ").append(nbt).append("\n\n");
            }
        }

        // Combine them
        StringBuilder finalOutput = new StringBuilder();
        finalOutput.append(displaySb).append("\n\n").append(otherSb);

        if ((displayCount + otherCount) == 0) {
            WynncraftSpellHider.info("No entities found in rendering range.");
            return;
        }

        mc.keyboardHandler.setClipboard(finalOutput.toString());
        WynncraftSpellHider.info("Copied " + displayCount + " displays and " + otherCount + " others to clipboard.");
    }
}