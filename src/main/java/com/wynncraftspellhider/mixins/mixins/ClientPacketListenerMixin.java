package com.wynncraftspellhider.mixins.mixins;

import com.wynncraftspellhider.WynncraftSpellHider;
import com.wynncraftspellhider.mixins.extensions.AbstractArrowExtension;
import com.wynncraftspellhider.mixins.extensions.ArmorStandExtension;
import com.wynncraftspellhider.mixins.extensions.ItemDisplayExtension;
import com.wynncraftspellhider.mixins.extensions.TextDisplayExtension;
import com.wynncraftspellhider.models.Models;
import com.wynncraftspellhider.models.spells.SpellGroup;
import com.wynncraftspellhider.models.spells.SpellRegistry;
import com.wynncraftspellhider.models.texturepack.TexturepackModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.regex.Pattern;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {

    @Inject(
            method = "handleSetEntityData",
            at = @At("TAIL")
    )
    private void onSetEntityData(ClientboundSetEntityDataPacket packet, CallbackInfo ci) {
        var level = Minecraft.getInstance().level;
        if (level == null) return;

        Entity entity = level.getEntity(packet.id());
        if (entity == null) return;

        // --- ItemDisplay ---
        if (entity instanceof Display.ItemDisplay itemDisplay) {
            SpellGroup group = resolveItemDisplayGroup(itemDisplay);

            //Some models use a vehicle for shadows. Now we hide that too.
            if (group == null && itemDisplay.getItemStack().isEmpty() && !itemDisplay.getPassengers().isEmpty()) {
                for (Entity passenger : itemDisplay.getPassengers()) {
                    if (passenger instanceof Display.ItemDisplay passengerDisplay) {
                        SpellGroup passengerGroup = ((ItemDisplayExtension) passengerDisplay).wynncraftspellhider_getSpellGroup();
                        if (passengerGroup != null) {
                            group = passengerGroup;
                            break;
                        }
                    }
                }
            }

            ((ItemDisplayExtension) itemDisplay).wynncraftspellhider_setSpellGroup(group);

            return;
        }

        // --- TextDisplay ---
        if (entity instanceof Display.TextDisplay textDisplay) {
            var ext = (TextDisplayExtension) textDisplay;
            SpellGroup current = ext.wynncraftspellhider_getSpellGroup();

            //quick fix so that arming... and % damage spell groups work properly
            if (current == null || current.name.equals("Arming name tag (all)")) {
                SpellGroup group = resolveTextDisplayGroup(textDisplay);
                ext.wynncraftspellhider_setSpellGroup(group);
            }
            return;
        }
    }

    
    @Inject(
            method = "handleSetEquipment",
            at = @At("TAIL")
    )
    private void onSetEquipment(ClientboundSetEquipmentPacket packet, CallbackInfo ci) {
        var level = Minecraft.getInstance().level;
        if (level == null) return;

        Entity entity = level.getEntity(packet.getEntity());
        if (!(entity instanceof ArmorStand armorStand)) return;

        var ext = (ArmorStandExtension) armorStand;
        if (ext.wynncraftspellhider_getSpellGroup() != null) return;

        SpellGroup group = resolveArmorStandGroup(armorStand);
        ext.wynncraftspellhider_setSpellGroup(group);
    }


    @Inject(
            method = "handleAddEntity",
            at = @At("TAIL")
    )
    private void onAddEntity(ClientboundAddEntityPacket packet, CallbackInfo ci) {
        var level = Minecraft.getInstance().level;
        if (level == null) return;

        Entity entity = level.getEntity(packet.getId());
        if (entity == null) return;

        if (entity instanceof AbstractArrow arrow) {
            ((AbstractArrowExtension) arrow).wynncraftspellhider_setSpellGroup(SpellRegistry.ARROW_ENTITY_GROUP);
        }
    }

    @Unique
    private SpellGroup resolveItemDisplayGroup(Display.ItemDisplay itemDisplay) {
        TexturepackModel texturepackModel = Models.texturepackModel;
        if (texturepackModel == null) return null;

        ItemStack stack = itemDisplay.getItemStack();
        if (!stack.is(Items.OAK_BOAT)) return null;

        var customModelData = stack.get(DataComponents.CUSTOM_MODEL_DATA);
        if (customModelData == null || customModelData.floats().isEmpty()) return null;

        int modelId = customModelData.floats().get(0).intValue();

        return texturepackModel.getGroupForModelId(modelId);
    }

    @Unique
    private static final Pattern STRIP_CODES = Pattern.compile("§[0-9a-fk-orA-FK-OR]");

    @Unique
    private SpellGroup resolveTextDisplayGroup(Display.TextDisplay textDisplay) {
        Component text = textDisplay.getText();
        String rawText = text.getString();
        if (rawText.isEmpty()) return null;

        var player = Minecraft.getInstance().player;
        if (player == null) return null;

        String plainText = STRIP_CODES.matcher(rawText).replaceAll("");

        String localPlayerName = player.getName().getString();
        boolean isLocalPlayer = plainText.startsWith(localPlayerName + "'s ");

        return SpellRegistry.getGroupForTextDisplay(plainText, isLocalPlayer);
    }

    @Unique
    private SpellGroup resolveArmorStandGroup(ArmorStand armorStand) {
        ItemStack head = armorStand.getItemBySlot(EquipmentSlot.HEAD);
        if (head.isEmpty()) return null;

        String itemId = BuiltInRegistries.ITEM.getKey(head.getItem()).getPath(); // e.g. "diamond_sword"
        int damage = head.getDamageValue();

        return SpellRegistry.getGroupForArmorStand(itemId, damage);
    }
}