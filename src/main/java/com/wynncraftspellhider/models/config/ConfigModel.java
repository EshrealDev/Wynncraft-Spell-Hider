package com.wynncraftspellhider.models.config;

import net.fabricmc.loader.api.FabricLoader;
import java.io.File;

public class ConfigModel {
    public static File configFolder;

    public ConfigModel() {
        configFolder = new File(FabricLoader.getInstance().getConfigDir().toFile(), "wynncraftspellhider");
        if (!configFolder.exists()) {
            configFolder.mkdirs();
        }
    }
}