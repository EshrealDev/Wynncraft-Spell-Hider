package com.wynncraftspellhider.models.command;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.wynncraftspellhider.WynncraftSpellHider;
import com.wynncraftspellhider.gui.GuiState;
import com.wynncraftspellhider.managers.NetworkManager;
import com.wynncraftspellhider.managers.UpdateManager.UpdateManager;
import com.wynncraftspellhider.managers.UpdateManager.UpdateResult;
import com.wynncraftspellhider.models.Models;
import com.wynncraftspellhider.models.config.ProfileRegistry;
import com.wynncraftspellhider.models.particle.ParticleRegistry;
import com.wynncraftspellhider.models.spell.SpellConfig;
import com.wynncraftspellhider.models.spell.SpellGroup;
import com.wynncraftspellhider.models.spell.SpellModel;
import com.wynncraftspellhider.models.spell.SpellModelExporter;
import com.wynncraftspellhider.models.spell.rules.TextureRule;
import java.net.URI;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;

public class CommandModel {

    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            LiteralArgumentBuilder<FabricClientCommandSource> root =
                    ClientCommandManager.literal("wynncraftspellhider");

            // /wynncraftspellhider menu
            root.then(ClientCommandManager.literal("menu").executes(ctx -> {
                Minecraft.getInstance()
                        .schedule(() -> Minecraft.getInstance().setScreen(GuiState.lastScreenFactory.get()));
                return 1;
            }));

            root.then(ClientCommandManager.literal("globalTransparency")
                    .then(ClientCommandManager.argument("value", FloatArgumentType.floatArg(0.0f, 1.0f))
                            .executes(ctx -> {
                                float value = FloatArgumentType.getFloat(ctx, "value");
                                for (SpellConfig spell : SpellModel.getAllSpells()) {
                                    for (SpellGroup group : spell.groups) {
                                        boolean onlyTextureRules =
                                                group.rules.stream().allMatch(r -> r instanceof TextureRule);

                                        if (onlyTextureRules) {
                                            group.transparency = value;
                                        }
                                    }
                                }
                                ProfileRegistry.saveActiveProfile();
                                ctx.getSource()
                                        .sendFeedback(Component.literal(
                                                "Set global transparency to " + String.format("%.2f", value)));
                                return 1;
                            })));

            // /wynncraftspellhider version
            root.then(ClientCommandManager.literal("version").executes(ctx -> {
                ctx.getSource()
                        .sendFeedback(Component.literal(
                                "WynncraftSpellHider version: " + WynncraftSpellHider.getCurrentVersion()));
                return 1;
            }));

// /wynncraftspellhider update
            root.then(ClientCommandManager.literal("update").executes(ctx -> {
                FabricClientCommandSource source = ctx.getSource();
                Path configDir = Models.configModel.configFolder.toPath();

                source.sendFeedback(Component.literal("Checking for updates..."));

                UpdateManager.checkModVersionAsync(
                        latestVersion -> Minecraft.getInstance()
                                .execute(() -> source.sendFeedback(Component.literal("Mod update available! Click ")
                                        .append(Component.literal("here")
                                                .withStyle(style -> style.withClickEvent(new ClickEvent.OpenUrl(
                                                                URI.create(Objects.requireNonNull(
                                                                        NetworkManager.getUrl("download")))))
                                                        .withUnderlined(true)
                                                        .withColor(ChatFormatting.YELLOW)))
                                        .append(Component.literal(" to download v" + latestVersion + ".")))),
                        () -> Minecraft.getInstance()
                                .execute(() -> source.sendFeedback(Component.literal("Mod: ")
                                        .append(Component.literal("up to date")
                                                .withStyle(s -> s.withColor(ChatFormatting.GREEN))))),
                        () -> Minecraft.getInstance()
                                .execute(() -> source.sendFeedback(Component.literal("Mod: ")
                                        .append(Component.literal("failed to reach server")
                                                .withStyle(s -> s.withColor(ChatFormatting.RED))))));

                CompletableFuture<UpdateResult> spellFuture = UpdateManager.checkSpellRegistryAsync(configDir);
                CompletableFuture<UpdateResult> texturesFuture = UpdateManager.checkTextureHashesAsync(configDir);

                CompletableFuture.allOf(spellFuture, texturesFuture).thenRun(() -> {
                    UpdateResult spellResult = spellFuture.join();
                    UpdateResult texturesResult = texturesFuture.join();

                    Minecraft.getInstance().execute(() -> {
                        source.sendFeedback(Component.literal("Spell registry: ")
                                .append(Component.literal(switch (spellResult) {
                                            case UPDATED -> "updated";
                                            case UP_TO_DATE -> "up to date";
                                            case FAILED -> "failed to reach server";
                                        })
                                        .withStyle(s -> s.withColor(switch (spellResult) {
                                            case UPDATED -> ChatFormatting.AQUA;
                                            case UP_TO_DATE -> ChatFormatting.GREEN;
                                            case FAILED -> ChatFormatting.RED;
                                        }))));

                        source.sendFeedback(Component.literal("Texture hashes: ")
                                .append(Component.literal(switch (texturesResult) {
                                            case UPDATED -> "updated";
                                            case UP_TO_DATE -> "up to date";
                                            case FAILED -> "failed to reach server";
                                        })
                                        .withStyle(s -> s.withColor(switch (texturesResult) {
                                            case UPDATED -> ChatFormatting.AQUA;
                                            case UP_TO_DATE -> ChatFormatting.GREEN;
                                            case FAILED -> ChatFormatting.RED;
                                        }))));

                        if (spellResult == UpdateResult.UPDATED) {
                            source.sendFeedback(
                                    Component.literal("Spell registry was updated — reloading spell model..."));
                            try {
                                SpellModel.reinit(configDir);
                                source.sendFeedback(Component.literal("Spell model reloaded successfully.")
                                        .withStyle(s -> s.withColor(ChatFormatting.GREEN)));
                            } catch (Exception e) {
                                WynncraftSpellHider.error(
                                        "Failed to reinit SpellModel after update: " + e.getMessage());
                                source.sendFeedback(Component.literal("Spell model reload failed. Check logs.")
                                        .withStyle(s -> s.withColor(ChatFormatting.RED)));
                            }
                        }

                        if (texturesResult == UpdateResult.UPDATED) {
                            source.sendFeedback(
                                    Component.literal("Texture hashes were updated — reloading texture pack model..."));
                            Models.texturepackModel.listResourcesAsync(false);
                        }
                    });
                });

                return 1;
            }));

            // dev subcommands — only registered if devMode is on
            if (WynncraftSpellHider.devMode) {
                LiteralArgumentBuilder<FabricClientCommandSource> dev = ClientCommandManager.literal("dev");

                dev.then(ClientCommandManager.literal("encodetextures").executes(ctx -> {
                    Models.texturepackModel.encodeTextureCacheToJson();
                    return 1;
                }));

                dev.then(ClientCommandManager.literal("dumpparticles").executes(ctx -> {
                    ParticleRegistry.dumpAllParticlesToClipboard();
                    return 1;
                }));

                dev.then(ClientCommandManager.literal("exportSpellRegistry").executes(ctx -> {
                    SpellModelExporter.exportToFile();
                    return 1;
                }));

                dev.then(ClientCommandManager.literal("listresourcesverbose").executes(ctx -> {
                    ctx.getSource().sendFeedback(Component.literal("Re-scanning resources (verbose)..."));
                    Minecraft.getInstance().execute(() -> Models.texturepackModel.listResourcesAsync(true));
                    return 1;
                }));

                root.then(dev);
            }

            dispatcher.register(root);
        });
    }
}
