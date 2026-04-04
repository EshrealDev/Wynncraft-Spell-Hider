package com.wynncraftspellhider.models.config;

import com.google.gson.*;
import com.wynncraftspellhider.WynncraftSpellHider;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ProfileRegistry {

    private static final List<ProfileConfig> profiles = new ArrayList<>();
    private static ProfileConfig activeProfile = null;
    private static ProfileConfig defaultProfile = null;

    // Resolved once in loadFromDisk(), used for all file operations
    private static File profilesFolder;
    private static File metaFile;

    // -------------------------------------------------------------------------
    //  Profile list access
    // -------------------------------------------------------------------------

    public static List<ProfileConfig> getProfiles()          { return profiles; }
    public static ProfileConfig getActiveProfile()           { return activeProfile; }
    public static ProfileConfig getDefaultProfile()          { return defaultProfile; }

    // -------------------------------------------------------------------------
    //  Startup
    // -------------------------------------------------------------------------

    /**
     * Call once from onInitializeClient, after Models.loadModels().
     * Loads all profiles from disk, applies the default if one is set,
     * and creates a first-launch "Default" profile if none exist.
     */
    public static void loadFromDisk(File configFolder) {
        profilesFolder = new File(configFolder, "profiles");
        metaFile       = new File(configFolder, "meta.json");

        profilesFolder.mkdirs();

        // Load all profile json files
        File[] files = profilesFolder.listFiles(f -> f.getName().endsWith(".json"));
        if (files != null) {
            for (File f : files) {
                ProfileConfig profile = ProfileConfig.load(f);
                if (profile != null) profiles.add(profile);
            }
        }

        // First launch — no profiles found
        if (profiles.isEmpty()) {
            ProfileConfig def = create("Default");
            setAsDefault(def);
            WynncraftSpellHider.info("First launch: created default profile.");
            return;
        }

        // Read meta.json
        String defaultName = loadMeta();
        if (defaultName != null) {
            for (ProfileConfig p : profiles) {
                if (p.name.equals(defaultName)) {
                    defaultProfile = p;
                    break;
                }
            }
        }

        // Apply default on startup
        if (defaultProfile != null) {
            defaultProfile.apply();
            activeProfile = defaultProfile;
            WynncraftSpellHider.info("Applied default profile: " + defaultProfile.name);
        }
    }

    // -------------------------------------------------------------------------
    //  CRUD
    // -------------------------------------------------------------------------

    public static ProfileConfig create(String name) {
        ProfileConfig profile = new ProfileConfig(name);
        profile.capture();
        profiles.add(profile);
        saveProfileToDisk(profile);
        return profile;
    }

    public static void apply(ProfileConfig profile) {
        // Save whatever the current active profile was before switching away
        if (activeProfile != null && activeProfile != profile) {
            activeProfile.capture();
            saveProfileToDisk(activeProfile);
        }
        profile.apply();
        activeProfile = profile;
    }

    public static void saveActiveProfile() {
        if (activeProfile == null) return;
        activeProfile.capture();
        saveProfileToDisk(activeProfile);
    }

    public static boolean rename(ProfileConfig profile, String newName) {
        newName = newName.trim();
        if (newName.isEmpty()) return false;
        for (ProfileConfig p : profiles) {
            if (p != profile && p.name.equalsIgnoreCase(newName)) return false;
        }

        // Delete old file
        File oldFile = profileFile(profile.name);
        if (oldFile.exists()) oldFile.delete();

        // Rename and save under new file
        profile.name = newName;
        saveProfileToDisk(profile);

        // If this was the default, update meta too
        if (defaultProfile == profile) saveMeta();

        return true;
    }

    public static void delete(ProfileConfig profile) {
        profiles.remove(profile);

        File file = profileFile(profile.name);
        if (file.exists()) file.delete();

        if (activeProfile  == profile) activeProfile  = null;
        if (defaultProfile == profile) {
            defaultProfile = null;
            saveMeta();
        }
    }

    public static void setAsDefault(ProfileConfig profile) {
        defaultProfile = profile;
        saveMeta();
    }

    // -------------------------------------------------------------------------
    //  Disk helpers
    // -------------------------------------------------------------------------

    public static void saveProfileToDisk(ProfileConfig profile) {
        if (profilesFolder == null) return;
        profile.save(profileFile(profile.name));
    }

    private static File profileFile(String profileName) {
        return new File(profilesFolder, toFileName(profileName) + ".json");
    }

    /**
     * Strips characters that are illegal in filenames on Windows/Linux/macOS.
     * The display name is always kept as-is; only the filename is sanitized.
     */
    public static String toFileName(String name) {
        return name.replaceAll("[<>:\"/\\\\|?*]", "_");
    }

    private static void saveMeta() {
        if (metaFile == null) return;
        JsonObject root = new JsonObject();
        if (defaultProfile != null) root.addProperty("default", defaultProfile.name);

        try (Writer writer = new OutputStreamWriter(new FileOutputStream(metaFile), StandardCharsets.UTF_8)) {
            new GsonBuilder().setPrettyPrinting().create().toJson(root, writer);
        } catch (IOException e) {
            WynncraftSpellHider.error("Failed to save meta.json: " + e.getMessage());
        }
    }

    /**
     * Returns the default profile name from meta.json, or null if absent/malformed.
     */
    private static String loadMeta() {
        if (!metaFile.exists()) return null;
        try (Reader reader = new InputStreamReader(new FileInputStream(metaFile), StandardCharsets.UTF_8)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            JsonElement def = root.get("default");
            return (def != null && !def.isJsonNull()) ? def.getAsString() : null;
        } catch (Exception e) {
            WynncraftSpellHider.error("Failed to read meta.json: " + e.getMessage());
            return null;
        }
    }
}