package com.wynncraftspellhider.models.config;

import java.io.File;
import net.fabricmc.loader.api.FabricLoader;

public class ConfigModel {
    public static File configFolder;

    public ConfigModel() {
        configFolder = new File(FabricLoader.getInstance().getConfigDir().toFile(), "wynncraftspellhider");
        if (!configFolder.exists()) {
            configFolder.mkdirs();
        }
    }
}
