package com.wynncraftspellhider.managers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wynncraftspellhider.WynncraftSpellHider;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;

public class NetworkManager {

    private static JsonObject urls = null;

    private static JsonObject getUrls() {
        if (urls == null) {
            try (InputStream stream = WynncraftSpellHider.getModResourceAsStream("urls.json")) {
                if (stream == null) {
                    WynncraftSpellHider.warn("urls.json not found in mod resources.");
                    return null;
                }
                urls = JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject();
            } catch (Exception e) {
                WynncraftSpellHider.warn("Failed to load urls.json: " + e.getMessage());
            }
        }
        return urls;
    }

    public static String getUrl(String key) {
        JsonObject obj = getUrls();
        if (obj == null || !obj.has(key)) return null;
        return obj.get(key).getAsString();
    }

    public static JsonObject fetchJson(String key) {
        String url = getUrl(key);
        if (url == null) {
            WynncraftSpellHider.warn("No URL found for key: " + key);
            return null;
        }

        try {
            HttpURLConnection connection =
                    (HttpURLConnection) URI.create(url).toURL().openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            if (connection.getResponseCode() != 200) {
                WynncraftSpellHider.warn("Request failed for '" + key + "': HTTP " + connection.getResponseCode());
                return null;
            }

            return JsonParser.parseReader(new InputStreamReader(connection.getInputStream()))
                    .getAsJsonObject();

        } catch (Exception e) {
            WynncraftSpellHider.warn("Request failed for '" + key + "': " + e.getMessage());
            return null;
        }
    }
}
