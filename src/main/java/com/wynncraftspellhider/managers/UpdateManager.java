package com.wynncraftspellhider.managers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.wynncraftspellhider.WynncraftSpellHider;
import com.wynncraftspellhider.models.spell.SpellModelDeserializer;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class UpdateManager {

    private static final String REGISTRY_FILENAME = "spell_registry.json";
    private static final String TEXTURE_HASHES_FILENAME = "texture_hashes.json";

    public static void checkModVersionAsync(Consumer<String> onUpdateAvailable, Runnable onUpToDate) {
        CompletableFuture.runAsync(() -> {
            String current = WynncraftSpellHider.getCurrentVersion();
            if (current == null) return;

            JsonObject json = NetworkManager.fetchJson("update");
            if (json == null) return;

            String latest = json.get("currentVersion").getAsString();
            WynncraftSpellHider.info("Update check — current: " + current + ", latest: " + latest);

            if (!current.equals(latest)) {
                onUpdateAvailable.accept(latest);
            } else {
                onUpToDate.run();
            }
        });
    }

    public static CompletableFuture<Boolean> checkSpellRegistryAsync(Path configDir) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return tryDownloadSpellRegistry(configDir);
            } catch (Exception e) {
                WynncraftSpellHider.error("Failed to download " + REGISTRY_FILENAME + " from GitHub.");
                return false;
            }
        });
    }

    private static boolean tryDownloadSpellRegistry(Path configDir) throws Exception {
        JsonObject remote = NetworkManager.fetchJson("spell_registry");
        if (remote == null || !remote.has("version")) return false;

        int remoteVersion = remote.get("version").getAsInt();
        if (remoteVersion <= getLocalSpellRegistryVersion(configDir)) return false;

        SpellModelDeserializer.deserialize(remote); // validate before writing

        Files.createDirectories(configDir);
        String json = new Gson().toJson(remote);
        Path tmp = configDir.resolve(REGISTRY_FILENAME + ".tmp");
        Files.writeString(tmp, json, StandardCharsets.UTF_8);
        Files.move(
                tmp,
                configDir.resolve(REGISTRY_FILENAME),
                StandardCopyOption.REPLACE_EXISTING,
                StandardCopyOption.ATOMIC_MOVE);

        WynncraftSpellHider.info("Spell registry updated to version " + remoteVersion + ".");
        return true;
    }

    private static int getLocalSpellRegistryVersion(Path configDir) {
        Path registryPath = configDir.resolve(REGISTRY_FILENAME);
        if (Files.exists(registryPath)) {
            try {
                String json = Files.readString(registryPath, StandardCharsets.UTF_8);
                JsonObject root = new Gson().fromJson(json, JsonObject.class);
                if (root.has("version")) return root.get("version").getAsInt();
            } catch (Exception ignored) {
            }
        }

        try (InputStream is = WynncraftSpellHider.getModResourceAsStream(REGISTRY_FILENAME)) {
            if (is != null) {
                String json = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                JsonObject root = new Gson().fromJson(json, JsonObject.class);
                if (root.has("version")) return root.get("version").getAsInt();
            }
        } catch (Exception ignored) {
        }

        return -1;
    }

    public static CompletableFuture<Boolean> checkTextureHashesAsync(Path configDir) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return tryDownloadTextureHashes(configDir);
            } catch (Exception e) {
                WynncraftSpellHider.error("Failed to download " + TEXTURE_HASHES_FILENAME + " from GitHub.");
                return false;
            }
        });
    }

    private static boolean tryDownloadTextureHashes(Path configDir) throws Exception {
        JsonObject remote = NetworkManager.fetchJson("texture_hashes");
        if (remote == null || !remote.has("version")) return false;

        int remoteVersion = remote.get("version").getAsInt();
        if (remoteVersion <= getLocalTextureHashesVersion(configDir)) return false;

        Files.createDirectories(configDir);
        String json = new Gson().toJson(remote);
        Path tmp = configDir.resolve(TEXTURE_HASHES_FILENAME + ".tmp");
        Files.writeString(tmp, json, StandardCharsets.UTF_8);
        Files.move(
                tmp,
                configDir.resolve(TEXTURE_HASHES_FILENAME),
                StandardCopyOption.REPLACE_EXISTING,
                StandardCopyOption.ATOMIC_MOVE);

        WynncraftSpellHider.info("Texture hashes updated to version " + remoteVersion + ".");
        return true;
    }

    private static int getLocalTextureHashesVersion(Path configDir) {
        Path hashesPath = configDir.resolve(TEXTURE_HASHES_FILENAME);
        if (Files.exists(hashesPath)) {
            try {
                String json = Files.readString(hashesPath, StandardCharsets.UTF_8);
                JsonObject root = new Gson().fromJson(json, JsonObject.class);
                if (root.has("version")) return root.get("version").getAsInt();
            } catch (Exception ignored) {
            }
        }

        try (InputStream is = WynncraftSpellHider.getModResourceAsStream(TEXTURE_HASHES_FILENAME)) {
            if (is != null) {
                String json = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                JsonObject root = new Gson().fromJson(json, JsonObject.class);
                if (root.has("version")) return root.get("version").getAsInt();
            }
        } catch (Exception ignored) {
        }

        return -1;
    }
}
