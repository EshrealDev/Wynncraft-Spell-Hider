package com.wynncraftspellhider;

import com.wynncraftspellhider.gui.GuiState;
import com.wynncraftspellhider.gui.UpdateScreen;
import com.wynncraftspellhider.managers.UpdateManager;
import com.wynncraftspellhider.models.Models;
import com.wynncraftspellhider.models.command.CommandModel;
import com.wynncraftspellhider.models.config.ProfileRegistry;
import com.wynncraftspellhider.models.spell.SpellModel;
import com.wynncraftspellhider.models.texturepack.EntityDebugHelper;
import java.io.InputStream;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.fabric.api.resource.v1.reloader.SimpleResourceReloader;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WynncraftSpellHider implements ClientModInitializer {
    public static final String MOD_ID = "wynncraftspellhider";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final KeyMapping.Category KEYBIND_CATEGORY =
            KeyMapping.Category.register(Identifier.fromNamespaceAndPath(MOD_ID, "wynncraft-spell-hider"));

    public static final boolean devMode = FabricLoader.getInstance().isDevelopmentEnvironment();
    private static boolean updateChecked = false;

    public static KeyMapping menuKey;
    public static KeyMapping debugKey;
    public static KeyMapping clipboardKey;

    public static void info(String msg) {
        LOGGER.info(msg);
    }

    public static void warn(String msg) {
        LOGGER.warn(msg);
    }

    public static void error(String msg) {
        LOGGER.error(msg);
    }

    public static InputStream getModResourceAsStream(String resourceName) {
        return WynncraftSpellHider.class.getClassLoader().getResourceAsStream("assets/" + MOD_ID + "/" + resourceName);
    }

    public static String getCurrentVersion() {
        return FabricLoader.getInstance()
                .getModContainer("wynncraftspellhider")
                .get()
                .getMetadata()
                .getVersion()
                .getFriendlyString();
    }

    @Override
    public void onInitializeClient() {
        Models.loadModels();
        SpellModel.init(Models.configModel.configFolder.toPath());
        registerUpdateChecker();
        ProfileRegistry.loadFromDisk(Models.configModel.configFolder);
        registerKeybinds();
        if (devMode) devModeRegisters();
        registerAutoReload();
        CommandModel.register();
    }

    private void registerAutoReload() {
        ResourceLoader.get(PackType.CLIENT_RESOURCES)
                .registerReloader(
                        Identifier.fromNamespaceAndPath(MOD_ID, "texturepack_scan"),
                        new SimpleResourceReloader<Void>() {
                            @Override
                            protected Void prepare(SharedState store) {
                                return null;
                            }

                            @Override
                            protected void apply(Void prepared, SharedState store) {
                                if (Models.texturepackModel != null) {
                                    Models.texturepackModel.listResourcesAsync(false);
                                }
                            }
                        });
    }

    private void registerKeybinds() {
        menuKey = KeyBindingHelper.registerKeyBinding(new KeyMapping("Menu Key", GLFW.GLFW_KEY_N, KEYBIND_CATEGORY));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (menuKey.consumeClick()) {
                client.setScreen(GuiState.lastScreenFactory.get());
            }
        });
    }

    private void registerUpdateChecker() {
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (!updateChecked && screen instanceof TitleScreen) {
                updateChecked = true;
                UpdateManager.checkModVersionAsync(
                        latestVersion -> {
                            if (!ProfileRegistry.isUpdateDismissed(latestVersion)) {
                                client.execute(() -> client.setScreen(new UpdateScreen(screen, latestVersion)));
                            }
                        },
                        () -> {});
            }
        });
    }

    private void devModeRegisters() {
        debugKey =
                KeyBindingHelper.registerKeyBinding(new KeyMapping("Debug Key", GLFW.GLFW_KEY_COMMA, KEYBIND_CATEGORY));
        clipboardKey = KeyBindingHelper.registerKeyBinding(
                new KeyMapping("Clipboard Key", GLFW.GLFW_KEY_UP, KEYBIND_CATEGORY));

        HudElementRegistry.addLast(
                Identifier.fromNamespaceAndPath(MOD_ID, "entity_debug_helper_tick"), (drawContext, tickCounter) -> {
                    EntityDebugHelper.frameUpdate();
                });
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            EntityDebugHelper.tickUpdate();

            if (debugKey.consumeClick()) {
                EntityDebugHelper.startRecording();
            }
            if (clipboardKey.consumeClick()) {
                EntityDebugHelper.copyEntitiesToClipboard();
            }
        });
    }
}
