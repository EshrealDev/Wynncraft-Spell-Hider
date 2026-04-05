package com.wynncraftspellhider.models.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.wynncraftspellhider.WynncraftSpellHider;
import com.wynncraftspellhider.models.Models;
import com.wynncraftspellhider.models.particles.ParticleRegistry;
import com.wynncraftspellhider.gui.GuiState;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.wynncraftspellhider.models.spells.SpellConfig;
import com.wynncraftspellhider.models.spells.SpellGroup;
import com.wynncraftspellhider.models.spells.SpellRegistry;
import com.wynncraftspellhider.models.config.ProfileRegistry;
import net.minecraft.network.chat.Component;

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
                                        group.transparency = value;
                                    }
                                }
                                ProfileRegistry.saveActiveProfile();
                                ctx.getSource().sendFeedback(Component.literal(
                                        "Set global transparency to " + String.format("%.2f", value)));
                                return 1;
                            }))
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