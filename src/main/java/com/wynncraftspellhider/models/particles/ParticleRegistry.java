package com.wynncraftspellhider.models.particles;

import com.wynncraftspellhider.WynncraftSpellHider;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

import java.util.*;

/**
 * Registry of curated particle configurations.
 *
 * To populate this list:
 *   1. Call dumpAllParticlesToClipboard() once (bind it temporarily to a key).
 *   2. Paste the output into the static block below.
 *   3. Curate / annotate as needed.
 */
public class ParticleRegistry {

    private static final List<ParticleConfig> PARTICLES = new ArrayList<>();
    private static final Map<ParticleType<?>, ParticleConfig> particleTypeToParticleConfig = new IdentityHashMap<>();


    static {
// Auto-generated particle dump — paste into ParticleRegistry static block
        add("Angry Villager", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "angry_villager")));
        add("Ash", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "ash")));
        add("Block", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "block")));
        add("Block Crumble", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "block_crumble")));
        add("Block Marker", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "block_marker")));
        add("Bubble", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "bubble")));
        add("Bubble Column Up", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "bubble_column_up")));
        add("Bubble Pop", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "bubble_pop")));
        add("Campfire Cosy Smoke", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "campfire_cosy_smoke")));
        add("Campfire Signal Smoke", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "campfire_signal_smoke")));
        add("Cherry Leaves", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "cherry_leaves")));
        add("Cloud", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "cloud")));
        add("Composter", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "composter")));
        add("Copper Fire Flame", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "copper_fire_flame")));
        add("Crimson Spore", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "crimson_spore")));
        add("Crit", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "crit")), "Default arrow particle.");
        add("Current Down", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "current_down")));
        add("Damage Indicator", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "damage_indicator")));
        add("Dolphin", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "dolphin")));
        add("Dragon Breath", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "dragon_breath")));
        add("Dripping Dripstone Lava", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "dripping_dripstone_lava")));
        add("Dripping Dripstone Water", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "dripping_dripstone_water")));
        add("Dripping Honey", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "dripping_honey")));
        add("Dripping Lava", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "dripping_lava")));
        add("Dripping Obsidian Tear", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "dripping_obsidian_tear")));
        add("Dripping Water", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "dripping_water")));
        add("Dust", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "dust")), "Effects Pyrokinesis");
        add("Dust Color Transition", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "dust_color_transition")));
        add("Dust Pillar", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "dust_pillar")));
        add("Dust Plume", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "dust_plume")));
        add("Effect", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "effect")));
        add("Egg Crack", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "egg_crack")));
        add("Elder Guardian", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "elder_guardian")));
        add("Electric Spark", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "electric_spark")));
        add("Enchant", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "enchant")));
        add("Enchanted Hit", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "enchanted_hit")));
        add("End Rod", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "end_rod")));
        add("Entity Effect", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "entity_effect")));
        add("Explosion", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "explosion")));
        add("Explosion Emitter", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "explosion_emitter")));
        add("Falling Dripstone Lava", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "falling_dripstone_lava")));
        add("Falling Dripstone Water", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "falling_dripstone_water")));
        add("Falling Dust", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "falling_dust")));
        add("Falling Honey", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "falling_honey")));
        add("Falling Lava", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "falling_lava")));
        add("Falling Nectar", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "falling_nectar")));
        add("Falling Obsidian Tear", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "falling_obsidian_tear")));
        add("Falling Spore Blossom", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "falling_spore_blossom")));
        add("Falling Water", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "falling_water")));
        add("Firefly", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "firefly")));
        add("Firework", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "firework")));
        add("Fishing", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "fishing")));
        add("Flame", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "flame")), "Effects flaming tongue");
        add("Flash", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "flash")));
        add("Glow", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "glow")));
        add("Glow Squid Ink", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "glow_squid_ink")));
        add("Gust", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "gust")));
        add("Gust Emitter Large", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "gust_emitter_large")));
        add("Gust Emitter Small", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "gust_emitter_small")));
        add("Happy Villager", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "happy_villager")));
        add("Heart", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "heart")));
        add("Infested", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "infested")));
        add("Instant Effect", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "instant_effect")), "Balastic Trap connections\nCould also be TCC wing particle");
        add("Item", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "item")));
        add("Item Cobweb", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "item_cobweb")));
        add("Item Slime", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "item_slime")));
        add("Item Snowball", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "item_snowball")));
        add("Landing Honey", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "landing_honey")));
        add("Landing Lava", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "landing_lava")));
        add("Landing Obsidian Tear", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "landing_obsidian_tear")));
        add("Large Smoke", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "large_smoke")));
        add("Lava", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "lava")));
        add("Mycelium", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "mycelium")));
        add("Nautilus", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "nautilus")));
        add("Note", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "note")));
        add("Ominous Spawning", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "ominous_spawning")));
        add("Pale Oak Leaves", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "pale_oak_leaves")));
        add("Poof", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "poof")), "Effects TNA watched beam");
        add("Portal", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "portal")));
        add("Raid Omen", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "raid_omen")));
        add("Rain", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "rain")));
        add("Reverse Portal", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "reverse_portal")));
        add("Scrape", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "scrape")));
        add("Sculk Charge", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "sculk_charge")));
        add("Sculk Charge Pop", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "sculk_charge_pop")));
        add("Sculk Soul", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "sculk_soul")));
        add("Shriek", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "shriek")));
        add("Small Flame", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "small_flame")));
        add("Small Gust", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "small_gust")));
        add("Smoke", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "smoke")));
        add("Sneeze", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "sneeze")));
        add("Snowflake", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "snowflake")));
        add("Sonic Boom", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "sonic_boom")));
        add("Soul", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "soul")));
        add("Soul Fire Flame", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "soul_fire_flame")));
        add("Spit", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "spit")));
        add("Splash", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "splash")));
        add("Spore Blossom Air", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "spore_blossom_air")));
        add("Squid Ink", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "squid_ink")));
        add("Sweep Attack", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "sweep_attack")));
        add("Tinted Leaves", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "tinted_leaves")));
        add("Totem Of Undying", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "totem_of_undying")), "Effects uproot projectile");
        add("Trail", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "trail")));
        add("Trial Omen", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "trial_omen")));
        add("Trial Spawner Detection", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "trial_spawner_detection")));
        add("Trial Spawner Detection Ominous", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "trial_spawner_detection_ominous")));
        add("Underwater", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "underwater")));
        add("Vault Connection", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "vault_connection")));
        add("Vibration", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "vibration")));
        add("Warped Spore", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "warped_spore")));
        add("Wax Off", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "wax_off")));
        add("Wax On", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "wax_on")));
        add("White Ash", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "white_ash")));
        add("White Smoke", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "white_smoke")));
        add("Witch", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", "witch")));
    }

    // =========================================================
    //  Registration helpers
    // =========================================================

    private static void add(String name, ParticleType<?> type) {
        ParticleConfig config = new ParticleConfig(name, type);
        PARTICLES.add(config);
        particleTypeToParticleConfig.put(type, config);
    }

    private static void add(String name, ParticleType<?> type, String description) {
        ParticleConfig config = new ParticleConfig(name, type, description);
        PARTICLES.add(config);
        particleTypeToParticleConfig.put(type, config);
    }

    // =========================================================
    //  Lookup
    // =========================================================

    public static List<ParticleConfig> getParticles() {
        return PARTICLES;
    }

    public static ParticleConfig getConfig(ParticleType<?> type) {
        return particleTypeToParticleConfig.get(type);
    }

    public static boolean isHidden(ParticleType<?> type) {
        ParticleConfig config = getConfig(type);
        return config != null && config.hidden;
    }

    // =========================================================
    //  Dev utility — dump all registered particle types to clipboard
    //
    //  Call this once by temporarily binding it in WynncraftSpellHider,
    //  then paste the output into the static block above and curate.
    // =========================================================

    public static void dumpAllParticlesToClipboard() {
        StringBuilder sb = new StringBuilder();
        sb.append("// Auto-generated particle dump — paste into ParticleRegistry static block\n");

        List<Identifier> keys = new ArrayList<>(
                BuiltInRegistries.PARTICLE_TYPE.keySet()
        );
        keys.sort(Comparator.comparing(Identifier::toString));

        for (Identifier key : keys) {
            ParticleType<?> type = BuiltInRegistries.PARTICLE_TYPE.getValue(key);
            if (type == null) continue;

            String readable = toReadableName(key.getPath());
            sb.append("add(\"")
                    .append(readable)
                    .append("\", BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath(\"")
                    .append(key.getNamespace())
                    .append("\", \"")
                    .append(key.getPath())
                    .append("\")));\n");
        }

        String result = sb.toString();
        Minecraft.getInstance().keyboardHandler.setClipboard(result);
        WynncraftSpellHider.info("Dumped " + keys.size() + " particle types to clipboard.");
    }

    /**
     * Converts a snake_case registry path to a Title Case readable name.
     * e.g. "large_smoke" -> "Large Smoke"
     */
    private static String toReadableName(String path) {
        String[] parts = path.split("_");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (!part.isEmpty()) {
                sb.append(Character.toUpperCase(part.charAt(0)));
                sb.append(part.substring(1));
                sb.append(" ");
            }
        }
        return sb.toString().trim();
    }
}