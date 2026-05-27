package com.wynncraftspellhider.models.spell;

import com.google.gson.*;
import com.wynncraftspellhider.WynncraftSpellHider;
import com.wynncraftspellhider.models.spell.rules.*;
import java.util.*;
import java.util.stream.IntStream;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

public class SpellModelDeserializer {

    public static final int SUPPORTED_SCHEMA_VERSION = 1;

    public static Map<SpellModel.WynnClass, List<SpellConfig>> deserialize(JsonObject root) {
        int schemaVersion = root.get("schemaVersion").getAsInt();
        if (schemaVersion != SUPPORTED_SCHEMA_VERSION) {
            throw new IllegalArgumentException("Unsupported spell registry schema version: " + schemaVersion
                    + " (supported: " + SUPPORTED_SCHEMA_VERSION + ")");
        }

        Map<SpellModel.WynnClass, List<SpellConfig>> result = new EnumMap<>(SpellModel.WynnClass.class);
        JsonObject classes = root.getAsJsonObject("classes");

        for (SpellModel.WynnClass wynnClass : SpellModel.WynnClass.values()) {
            String key = wynnClass.name(); // "WARRIOR", "MAGE", etc.
            if (classes.has(key)) {
                result.put(wynnClass, parseSpellConfigs(classes.getAsJsonArray(key)));
            } else {
                result.put(wynnClass, new ArrayList<>());
            }
        }

        return result;
    }

    private static List<SpellConfig> parseSpellConfigs(JsonArray array) {
        List<SpellConfig> configs = new ArrayList<>();
        for (JsonElement element : array) {
            try {
                configs.add(parseSpellConfig(element.getAsJsonObject()));
            } catch (Exception e) {
                WynncraftSpellHider.error("Failed to parse SpellConfig: " + e.getMessage());
            }
        }
        return configs;
    }

    private static SpellConfig parseSpellConfig(JsonObject obj) {
        String name = obj.get("name").getAsString();
        String description = obj.has("description") ? obj.get("description").getAsString() : null;

        List<SpellGroup> groups = new ArrayList<>();
        for (JsonElement element : obj.getAsJsonArray("groups")) {
            try {
                groups.add(parseSpellGroup(element.getAsJsonObject()));
            } catch (Exception e) {
                WynncraftSpellHider.error("Failed to parse SpellGroup: " + e.getMessage());
            }
        }

        return description != null ? new SpellConfig(name, description, groups) : new SpellConfig(name, groups);
    }

    private static SpellGroup parseSpellGroup(JsonObject obj) {
        String name = obj.get("name").getAsString();
        String description = obj.has("description") ? obj.get("description").getAsString() : null;

        List<MatchRule> rules = new ArrayList<>();
        for (JsonElement element : obj.getAsJsonArray("rules")) {
            try {
                MatchRule rule = parseMatchRule(element.getAsJsonObject());
                if (rule != null) rules.add(rule);
            } catch (Exception e) {
                WynncraftSpellHider.error("Failed to parse MatchRule: " + e.getMessage());
            }
        }

        return description != null ? new SpellGroup(name, description, rules) : new SpellGroup(name, rules);
    }

    private static MatchRule parseMatchRule(JsonObject obj) {
        String type = obj.get("type").getAsString();
        return switch (type) {
            case "texture" -> parseTextureRule(obj);
            case "entity_type" -> parseEntityTypeRule(obj);
            case "text_display" -> parseTextDisplayRule(obj);
            case "armor_stand" -> parseArmorStandRule(obj);
            case "item_entity" -> parseItemEntityRule(obj);
            default -> null; // Unknown type — skip gracefully.
        };
    }

    private static TextureRule parseTextureRule(JsonObject obj) {
        Set<String> names = parseNames(obj.getAsJsonArray("names"));

        Set<String> fingerprints = null;
        if (obj.has("fingerprints")) {
            fingerprints = new HashSet<>();
            for (JsonElement fp : obj.getAsJsonArray("fingerprints")) {
                fingerprints.add(fp.getAsString());
            }
            fingerprints = Collections.unmodifiableSet(fingerprints);
        }

        return new TextureRule(names, fingerprints);
    }

    /**
     * Parses the "names" array, which can contain:
     *   - Plain strings:  "meteorbeam"
     *   - Range objects:  { "prefix": "bash", "from": 1, "to": 7 }
     *
     * Both are expanded into the same flat Set<String>, exactly matching
     * the behaviour of range() + ofTexture() in the old static registry.
     */
    private static Set<String> parseNames(JsonArray namesArray) {
        Set<String> names = new HashSet<>();
        for (JsonElement element : namesArray) {
            if (element.isJsonPrimitive()) {
                names.add(element.getAsString());
            } else if (element.isJsonObject()) {
                JsonObject rangeObj = element.getAsJsonObject();
                String prefix = rangeObj.get("prefix").getAsString();
                int from = rangeObj.get("from").getAsInt();
                int to = rangeObj.get("to").getAsInt();
                IntStream.rangeClosed(from, to)
                        .mapToObj(i -> prefix + " (" + i + ")")
                        .forEach(names::add);
            }
            // Anything else is silently ignored — forward compatibility.
        }
        return Collections.unmodifiableSet(names);
    }

    private static EntityTypeRule parseEntityTypeRule(JsonObject obj) {
        return new EntityTypeRule(obj.get("entityType").getAsString());
    }

    private static TextDisplayRule parseTextDisplayRule(JsonObject obj) {
        String regex = obj.get("regex").getAsString();

        if (obj.has("ownerFilter")) {
            String raw = obj.get("ownerFilter").getAsString();
            try {
                TextDisplayRule.OwnerFilter filter = TextDisplayRule.OwnerFilter.valueOf(raw);
                return new TextDisplayRule(regex, filter);
            } catch (IllegalArgumentException e) {
                // Unknown ownerFilter value — fall through to default constructor.
            }
        }

        return new TextDisplayRule(regex);
    }

    private static ArmorStandRule parseArmorStandRule(JsonObject obj) {
        Set<ArmorStandRule.ArmorStandModel> models = new HashSet<>();
        for (JsonElement element : obj.getAsJsonArray("models")) {
            String raw = element.getAsString();
            try {
                models.add(ArmorStandRule.ArmorStandModel.valueOf(raw));
            } catch (IllegalArgumentException e) {
                WynncraftSpellHider.error("Unknown model name in parseArmorStandRule: " + e.getMessage());
            }
        }
        return new ArmorStandRule(models);
    }

    private static ItemEntityRule parseItemEntityRule(JsonObject obj) {
        String itemId = obj.get("item").getAsString();
        Item item = BuiltInRegistries.ITEM
                .get(Identifier.parse(itemId))
                .orElseThrow(() -> new IllegalArgumentException("Unknown item: " + itemId))
                .value();
        return new ItemEntityRule(item);
    }
}
