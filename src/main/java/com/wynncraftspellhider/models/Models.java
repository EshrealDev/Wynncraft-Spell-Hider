package com.wynncraftspellhider.models;

import com.wynncraftspellhider.models.config.ConfigModel;
import com.wynncraftspellhider.models.texturepack.TexturepackModel;

public class Models {
    public static TexturepackModel texturepackModel;
    public static ConfigModel configModel;

    public static void loadModels() {
        texturepackModel = new TexturepackModel();
        configModel = new ConfigModel();
    }
}
