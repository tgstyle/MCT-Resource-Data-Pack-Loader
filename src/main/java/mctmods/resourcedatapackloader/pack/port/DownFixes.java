package mctmods.resourcedatapackloader.pack.port;

import static mctmods.resourcedatapackloader.util.IdNames.MINECRAFT;
import static mctmods.resourcedatapackloader.util.IdNames.namespaced;

import mctmods.resourcedatapackloader.util.Json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

final class DownFixes {
    private static final String SHOWN = "show_in_tooltip";
    private static final String LUCK = "minecraft:luck";
    private static final String ATTRIBUTE_MODIFIERS = "minecraft:attribute_modifiers";
    private static final String CAN_BREAK = "minecraft:can_break";
    private static final String CAN_PLACE_ON = "minecraft:can_place_on";
    private static final String DYED_COLOR = "minecraft:dyed_color";
    private static final String ENCHANTMENTS = "minecraft:enchantments";
    private static final String JUKEBOX_PLAYABLE = "minecraft:jukebox_playable";
    private static final String STORED_ENCHANTMENTS = "minecraft:stored_enchantments";
    private static final String TRIM = "minecraft:trim";
    private static final String UNBREAKABLE = "minecraft:unbreakable";
    private static final Map<String, String> NAMES = Map.of("minecraft:iron_chain", "minecraft:chain");
    private static final List<String> PREFIXED = List.of("generic.armor", "generic.armor_toughness", "generic.attack_damage", "generic.attack_knockback", "generic.attack_speed", "player.block_break_speed",
            "player.block_interaction_range", "generic.burning_time", "generic.explosion_knockback_resistance", "player.entity_interaction_range", "generic.fall_damage_multiplier", "generic.flying_speed",
            "generic.follow_range", "generic.gravity", "generic.jump_strength", "generic.knockback_resistance", "generic.luck", "generic.max_absorption", "generic.max_health", "player.mining_efficiency",
            "generic.movement_efficiency", "generic.movement_speed", "generic.oxygen_bonus", "generic.safe_fall_distance", "generic.scale", "player.sneaking_speed", "zombie.spawn_reinforcements",
            "generic.step_height", "player.submerged_mining_speed", "player.sweeping_damage_ratio", "generic.water_movement_efficiency");
    private static final Map<String, String> ATTRIBUTES = attributes();
    private static final Map<String, String> RECIPES = Map.ofEntries(Map.entry("chiseled_stone_bricks_from_stone_stonecutting", "chiseled_stone_bricks_stone_from_stonecutting"),
            Map.entry("end_stone_brick_slab_from_end_stone_bricks_stonecutting", "end_stone_brick_slab_from_end_stone_brick_stonecutting"),
            Map.entry("end_stone_brick_stairs_from_end_stone_bricks_stonecutting", "end_stone_brick_stairs_from_end_stone_brick_stonecutting"),
            Map.entry("end_stone_brick_wall_from_end_stone_bricks_stonecutting", "end_stone_brick_wall_from_end_stone_brick_stonecutting"),
            Map.entry("mossy_stone_brick_slab_from_mossy_stone_bricks_stonecutting", "mossy_stone_brick_slab_from_mossy_stone_brick_stonecutting"),
            Map.entry("mossy_stone_brick_stairs_from_mossy_stone_bricks_stonecutting", "mossy_stone_brick_stairs_from_mossy_stone_brick_stonecutting"),
            Map.entry("mossy_stone_brick_wall_from_mossy_stone_bricks_stonecutting", "mossy_stone_brick_wall_from_mossy_stone_brick_stonecutting"),
            Map.entry("prismarine_brick_slab_from_prismarine_bricks_stonecutting", "prismarine_brick_slab_from_prismarine_stonecutting"),
            Map.entry("prismarine_brick_stairs_from_prismarine_bricks_stonecutting", "prismarine_brick_stairs_from_prismarine_stonecutting"),
            Map.entry("quartz_slab_from_quartz_block_stonecutting", "quartz_slab_from_stonecutting"), Map.entry("stone_brick_wall_from_stone_stonecutting", "stone_brick_walls_from_stone_stonecutting"));
    private static final Set<String> TOOLTIP_SHOWN = Set.of(TRIM, UNBREAKABLE, DYED_COLOR, ATTRIBUTE_MODIFIERS, ENCHANTMENTS,
            STORED_ENCHANTMENTS, JUKEBOX_PLAYABLE, CAN_PLACE_ON, CAN_BREAK);
    private static final Map<String, String> WRAPPED = Map.of(DYED_COLOR, "rgb", ATTRIBUTE_MODIFIERS, "modifiers", ENCHANTMENTS, "levels",
            STORED_ENCHANTMENTS, "levels", JUKEBOX_PLAYABLE, "song", CAN_PLACE_ON, "predicates", CAN_BREAK, "predicates");
    private static final Map<String, String> CLICKS = Map.of("open_url", "url", "run_command", "command", "suggest_command", "command", "change_page", "page", "open_file", "path", "copy_to_clipboard", "value");

    private DownFixes() {}

    private static Map<String, String> attributes() {
        Map<String, String> out = new HashMap<>();
        for (String prefixed : PREFIXED) { out.put(MINECRAFT + prefixed.substring(prefixed.indexOf('.') + 1), MINECRAFT + prefixed); }
        return out;
    }

    static boolean unknownComponent(String component) { return unregistered(BuiltInRegistries.DATA_COMPONENT_TYPE, component); }

    static boolean unknownRecipe(String type) { return unregistered(BuiltInRegistries.RECIPE_SERIALIZER, type); }

    private static boolean unregistered(Registry<?> registry, String text) {
        ResourceLocation id = ResourceLocation.tryParse(text);
        return id != null && "minecraft".equals(id.getNamespace()) && !registry.containsKey(id);
    }

    static String name(String id) { return NAMES.getOrDefault(namespaced(id), id); }

    static String attribute(String id) { return ATTRIBUTES.getOrDefault(namespaced(id), id); }

    static String recipe(String id) {
        String named = namespaced(id);
        String old = named.startsWith(MINECRAFT) ? RECIPES.get(named.substring(MINECRAFT.length())) : null;
        return old == null ? id : MINECRAFT + old;
    }

    static JsonElement names(JsonElement element) { return names(element, false); }

    private static JsonElement names(JsonElement element, boolean modifier) {
        if (element.isJsonArray()) {
            JsonArray out = new JsonArray();
            element.getAsJsonArray().forEach(inner -> out.add(names(inner, false)));
            return out;
        }
        if (element.isJsonObject()) {
            JsonObject held = element.getAsJsonObject();
            boolean modifies = held.has("operation") && held.has("amount");
            JsonObject out = new JsonObject();
            held.entrySet().forEach(entry -> out.add(entry.getKey(), names(entry.getValue(), modifies && ("attribute".equals(entry.getKey()) || "type".equals(entry.getKey())))));
            return out;
        }
        if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isString()) { return element; }
        String text = element.getAsString();
        if (text.indexOf(':') <= 0 || text.startsWith("#") || text.indexOf(' ') >= 0) { return element; }
        String renamed = name(text);
        if (renamed.equals(text) && (modifier || !LUCK.equals(namespaced(text)))) { renamed = attribute(text); }
        return renamed.equals(text) ? element : new JsonPrimitive(renamed);
    }

    static JsonElement text(JsonElement text) {
        if (text.isJsonArray()) {
            JsonArray out = new JsonArray();
            text.getAsJsonArray().forEach(one -> out.add(text(one)));
            return out;
        }
        if (!text.isJsonObject()) { return text; }
        JsonObject out = new JsonObject();
        for (Map.Entry<String, JsonElement> entry : text.getAsJsonObject().entrySet()) {
            switch (entry.getKey()) {
                case "click_event" -> {
                    JsonObject click = click(entry.getValue());
                    if (click != null) { out.add("clickEvent", click); }
                }
                case "hover_event" -> {
                    JsonObject hover = hover(entry.getValue());
                    if (hover != null) { out.add("hoverEvent", hover); }
                }
                case "extra", "with", "separator" -> out.add(entry.getKey(), text(entry.getValue()));
                default -> out.add(entry.getKey(), entry.getValue());
            }
        }
        return out;
    }

    private static JsonObject click(JsonElement held) {
        if (!held.isJsonObject()) { return null; }
        JsonObject event = held.getAsJsonObject();
        String action = Json.text(event, "action");
        String field = CLICKS.get(action);
        if (field == null || !event.has(field)) { return null; }
        JsonObject out = new JsonObject();
        out.addProperty("action", action);
        out.addProperty("value", event.get(field).getAsString());
        return out;
    }

    private static JsonObject hover(JsonElement held) {
        if (!held.isJsonObject()) { return null; }
        JsonObject event = held.getAsJsonObject();
        String action = Json.text(event, "action");
        JsonObject out = new JsonObject();
        out.addProperty("action", action);
        switch (action) {
            case "show_text" -> {
                if (!event.has("value")) { return null; }
                out.add("contents", text(event.get("value")));
            }
            case "show_item" -> {
                JsonObject item = new JsonObject();
                if (event.has("id")) { item.add("id", event.get("id")); }
                if (event.has("count")) { item.add("count", event.get("count")); }
                if (GsonHelper.isObjectNode(event, CrossJson.COMPONENTS)) { item.add(CrossJson.COMPONENTS, event.get(CrossJson.COMPONENTS)); }
                out.add("contents", item);
            }
            case "show_entity" -> {
                JsonObject entity = new JsonObject();
                if (event.has("id")) { entity.add("type", event.get("id")); }
                if (event.has("uuid")) { entity.add("id", event.get("uuid")); }
                if (event.has("name")) { entity.add("name", text(event.get("name"))); }
                out.add("contents", entity);
            }
            default -> { return null; }
        }
        return out;
    }

    static void stack(JsonObject stack, String file, Consumer<String> note) { if (GsonHelper.isObjectNode(stack, CrossJson.COMPONENTS)) { components(stack.getAsJsonObject(CrossJson.COMPONENTS), file, note); } }

    static void components(JsonObject components, String file, Consumer<String> note) {
        List<String> lost = new ArrayList<>();
        Set<String> hidden = new HashSet<>();
        JsonElement display = components.remove(MINECRAFT + "tooltip_display");
        if (display != null && display.isJsonObject()) {
            JsonObject shown = display.getAsJsonObject();
            if (shown.has("hide_tooltip") && shown.get("hide_tooltip").getAsBoolean()) { components.add(MINECRAFT + "hide_tooltip", new JsonObject()); }
            if (GsonHelper.isArrayNode(shown, "hidden_components")) { shown.getAsJsonArray("hidden_components").forEach(one -> hidden.add(namespaced(one.getAsString()))); }
        }
        food(components, lost);
        for (String key : new ArrayList<>(components.keySet())) {
            String named = namespaced(key);
            JsonElement value = components.remove(key);
            JsonElement out = component(named, value);
            if (out == null || unknownComponent(named)) {
                lost.add(named);
                continue;
            }
            if (hidden.contains(named) && TOOLTIP_SHOWN.contains(named) && out.isJsonObject()) { out.getAsJsonObject().addProperty(SHOWN, false); }
            components.add(key, out);
        }
        hidden.removeAll(TOOLTIP_SHOWN);
        if (!hidden.isEmpty()) { components.add(MINECRAFT + "hide_additional_tooltip", new JsonObject()); }
        if (!lost.isEmpty()) { note.accept("'" + file + "' sets the item component(s) " + String.join(", ", lost) + ", which " + Port.Line.running().title() + " does not have in that form, so they are left out"); }
    }

    private static JsonElement component(String named, JsonElement value) {
        switch (named) {
            case "minecraft:custom_name", "minecraft:item_name" -> { return new JsonPrimitive(text(value).toString()); }
            case "minecraft:lore" -> {
                if (!value.isJsonArray()) { return value; }
                JsonArray lines = new JsonArray();
                value.getAsJsonArray().forEach(line -> lines.add(new JsonPrimitive(text(line).toString())));
                return lines;
            }
            case "minecraft:custom_model_data" -> {
                if (value.isJsonPrimitive()) { return value; }
                JsonObject data = value.getAsJsonObject();
                JsonArray floats = GsonHelper.isArrayNode(data, "floats") ? data.getAsJsonArray("floats") : null;
                return floats == null || floats.isEmpty() ? null : new JsonPrimitive((int) Math.floor(floats.get(0).getAsDouble()));
            }
            case "minecraft:damage_resistant" -> {
                boolean fire = value.isJsonObject() && "#minecraft:is_fire".equals(Json.text(value.getAsJsonObject(), "types"));
                return fire ? new JsonObject() : null;
            }
            case "minecraft:lock" -> { return lock(value); }
            case TRIM, UNBREAKABLE -> { return value; }
            default -> { return WRAPPED.containsKey(named) ? wrapped(named, value) : value; }
        }
    }

    private static JsonElement wrapped(String named, JsonElement value) {
        String field = WRAPPED.get(named);
        if (value.isJsonObject() && (value.getAsJsonObject().has(field) || named.endsWith("can_break") || named.endsWith("can_place_on"))) { return value; }
        JsonElement inner = value;
        if ("modifiers".equals(field) && value.isJsonArray()) { inner = names(value); }
        if ("rgb".equals(field) && value.isJsonPrimitive() && value.getAsJsonPrimitive().isString()) { inner = new JsonPrimitive(color(value, 0)); }
        JsonObject out = new JsonObject();
        out.add(field, inner);
        return out;
    }

    private static JsonElement lock(JsonElement value) {
        if (value.isJsonPrimitive()) { return value; }
        JsonObject predicate = value.getAsJsonObject();
        JsonObject held = GsonHelper.isObjectNode(predicate, CrossJson.COMPONENTS) ? predicate.getAsJsonObject(CrossJson.COMPONENTS) : new JsonObject();
        JsonElement name = held.get(MINECRAFT + "custom_name");
        if (name == null) { return null; }
        if (name.isJsonPrimitive()) { return name; }
        return name.isJsonObject() && name.getAsJsonObject().has("text") ? name.getAsJsonObject().get("text") : null;
    }

    private static void food(JsonObject components, List<String> lost) {
        JsonElement consumable = components.remove(MINECRAFT + "consumable");
        JsonElement remainder = components.remove(MINECRAFT + "use_remainder");
        JsonElement food = components.get(MINECRAFT + "food");
        if (food == null || !food.isJsonObject()) {
            if (consumable != null) { lost.add(MINECRAFT + "consumable"); }
            if (remainder != null) { lost.add(MINECRAFT + "use_remainder"); }
            return;
        }
        JsonObject eaten = food.getAsJsonObject();
        if (remainder != null) { eaten.add("using_converts_to", remainder); }
        if (consumable == null || !consumable.isJsonObject()) { return; }
        JsonObject used = consumable.getAsJsonObject();
        if (used.has("consume_seconds")) { eaten.add("eat_seconds", used.get("consume_seconds")); }
        if (!GsonHelper.isArrayNode(used, "on_consume_effects")) { return; }
        JsonArray effects = new JsonArray();
        for (JsonElement effect : used.getAsJsonArray("on_consume_effects")) {
            JsonObject applied = effect.isJsonObject() ? effect.getAsJsonObject() : new JsonObject();
            if (!"minecraft:apply_effects".equals(namespaced(Json.text(applied, "type"))) || !applied.has("effects")) {
                lost.add(MINECRAFT + "consumable/" + Json.text(applied, "type"));
                continue;
            }
            for (JsonElement instance : applied.getAsJsonArray("effects")) {
                JsonObject one = new JsonObject();
                one.add("effect", instance);
                one.add("probability", applied.has("probability") ? applied.get("probability") : new JsonPrimitive(1.0F));
                effects.add(one);
            }
        }
        if (!effects.isEmpty()) { eaten.add("effects", effects); }
    }

    static int color(JsonElement held, int fallback) {
        if (held == null || !held.isJsonPrimitive()) { return fallback; }
        if (held.getAsJsonPrimitive().isNumber()) { return held.getAsInt(); }
        String text = held.getAsString().replace("#", "");
        try { return (int) (Long.parseLong(text, 16) & 0xFFFFFF); }
        catch (NumberFormatException unreadable) { return fallback; }
    }
}
