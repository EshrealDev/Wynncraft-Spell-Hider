package com.wynncraftspellhider.models.updatechecker;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wynncraftspellhider.WynncraftSpellHider;
import net.fabricmc.loader.api.FabricLoader;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.concurrent.CompletableFuture;

public class UpdateChecker {
    private static final String UPDATE_URL = "https://raw.githubusercontent.com/EshrealDev/Static-Storage/refs/heads/main/update.json";
    public static final String DOWNLOAD_URL = "https://github.com/EshrealDev/Wynncraft-Spell-Hider/releases";

    public static String currentVersion() {
        return FabricLoader.getInstance().getModContainer("wynncraftspellhider").get().getMetadata().getVersion().getFriendlyString();
    }

    public static void checkAsync(Runnable onUpdateAvailable) {
        CompletableFuture.runAsync(() -> {
            try {
                HttpURLConnection connection = (HttpURLConnection) URI.create(UPDATE_URL).toURL().openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                if (connection.getResponseCode() != 200) {
                    WynncraftSpellHider.warn("Update check failed: HTTP " + connection.getResponseCode());
                    return;
                }

                JsonObject json = JsonParser.parseReader(new InputStreamReader(connection.getInputStream())).getAsJsonObject();

                String latest = json.get("currentVersion").getAsString();
                String current = currentVersion();

                WynncraftSpellHider.info("Update check — current: " + current + " latest: " + latest);

                if (!current.equals(latest)) {
                    onUpdateAvailable.run();
                }

            } catch (Exception e) {
                WynncraftSpellHider.warn("Update check failed: " + e.getMessage());
            }
        });
    }
}