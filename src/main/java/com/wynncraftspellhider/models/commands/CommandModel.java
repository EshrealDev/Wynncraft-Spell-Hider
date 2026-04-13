package com.wynncraftspellhider.models.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.wynncraftspellhider.WynncraftSpellHider;
import com.wynncraftspellhider.models.Models;
import com.wynncraftspellhider.models.particles.ParticleRegistry;
import com.wynncraftspellhider.gui.GuiState;
import com.wynncraftspellhider.models.spells.rules.TextureRule;
import com.wynncraftspellhider.models.updatechecker.UpdateChecker;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.wynncraftspellhider.models.spells.SpellConfig;
import com.wynncraftspellhider.models.spells.SpellGroup;
import com.wynncraftspellhider.models.spells.SpellRegistry;
import com.wynncraftspellhider.models.config.ProfileRegistry;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;

import java.net.URI;

public class CommandModel {

    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            LiteralArgumentBuilder<FabricClientCommandSource> root =
                    ClientCommandManager.literal("wynncraftspellhider");

            // /wynncraftspellhider menu
            root.then(ClientCommandManager.literal("menu")
                    .executes(ctx -> {
                        Minecraft.getInstance().schedule(() ->
                                Minecraft.getInstance().setScreen(GuiState.lastScreenFactory.get())
                        );
                        return 1;
                    })
            );

            root.then(ClientCommandManager.literal("globalTransparency")
                    .then(ClientCommandManager.argument("value", FloatArgumentType.floatArg(0.0f, 1.0f))
                            .executes(ctx -> {
                                float value = FloatArgumentType.getFloat(ctx, "value");
                                for (SpellConfig spell : SpellRegistry.getAllSpells()) {
                                    for (SpellGroup group : spell.groups) {
                                        boolean onlyTextureRules = group.rules.stream().allMatch(r -> r instanceof TextureRule);

                                        if (onlyTextureRules) {
                                            group.transparency = value;
                                        }
                                    }
                                }
                                ProfileRegistry.saveActiveProfile();
                                ctx.getSource().sendFeedback(Component.literal(
                                        "Set global transparency to " + String.format("%.2f", value)));
                                return 1;
                            }))
            );

            // /wynncraftspellhider version
            root.then(ClientCommandManager.literal("version")
                    .executes(ctx -> {
                        ctx.getSource().sendFeedback(Component.literal(
                                "WynncraftSpellHider version: " + UpdateChecker.currentVersion()));
                        return 1;
                    })
            );

            // /wynncraftspellhider update
            root.then(ClientCommandManager.literal("update")
                    .executes(ctx -> {
                        ctx.getSource().sendFeedback(Component.literal(
                                "Checking for updates... (current version: " + UpdateChecker.currentVersion() + ")"));
                        UpdateChecker.checkAsync(
                                () -> Minecraft.getInstance().execute(() ->
                                        ctx.getSource().sendFeedback(
                                                Component.literal("A new update is available! Click ")
                                                        .append(Component.literal("here")
                                                                .withStyle(style -> style
                                                                        .withClickEvent(new ClickEvent.OpenUrl(URI.create(UpdateChecker.DOWNLOAD_URL)))
                                                                        .withUnderlined(true)
                                                                        .withColor(ChatFormatting.YELLOW)))
                                                        .append(Component.literal(" to download it.")))),
                                () -> Minecraft.getInstance().execute(() ->
                                        ctx.getSource().sendFeedback(Component.literal(
                                                "You are up to date!")))
                        );
                        return 1;
                    })
            );

            // dev subcommands — only registered if devMode is on
            if (WynncraftSpellHider.devMode) {
                LiteralArgumentBuilder<FabricClientCommandSource> dev =
                        ClientCommandManager.literal("dev");

                dev.then(ClientCommandManager.literal("encodetextures")
                        .executes(ctx -> {
                            Models.texturepackModel.encodeTextureCacheToJson();
                            return 1;
                        })
                );

                dev.then(ClientCommandManager.literal("dumpparticles")
                        .executes(ctx -> {
                            ParticleRegistry.dumpAllParticlesToClipboard();
                            return 1;
                        })
                );

                root.then(dev);
            }

            dispatcher.register(root);
        });
    }
}