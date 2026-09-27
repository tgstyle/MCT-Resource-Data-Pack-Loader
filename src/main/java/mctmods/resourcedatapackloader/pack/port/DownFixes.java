package mctmods.resourcedatapackloader.pack.port;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

final class DownFixes {
    static final String MINECRAFT = "minecraft:";
    private static final String COMPONENTS = "components";
    private static final String SHOWN = "show_in_tooltip";
    private static final String LUCK = "minecraft:luck";
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
    private static final Set<String> TOOLTIP_SHOWN = Set.of("minecraft:trim", "minecraft:unbreakable", "minecraft:dyed_color", "minecraft:attribute_modifiers", "minecraft:enchantments",
            "minecraft:stored_enchantments", "minecraft:jukebox_playable", "minecraft:can_place_on", "minecraft:can_break");
    private static final Map<String, String> WRAPPED = Map.of("minecraft:dyed_color", "rgb", "minecraft:attribute_modifiers", "modifiers", "minecraft:enchantments", "levels",
            "minecraft:stored_enchantments", "levels", "minecraft:jukebox_playable", "song", "minecraft:can_place_on", "predicates", "minecraft:can_break", "predicates");
    private static final Map<String, String> CLICKS = Map.of("open_url", "url", "run_command", "command", "suggest_command", "command", "change_page", "page", "open_file", "path", "copy_to_clipboard", "value");

    private DownFixes() {}

    private static Map<String, String> attributes() {
        Map<String, String> out = new HashMap<>();
        for (String prefixed : PREFIXED) { out.put(MINECRAFT + prefixed.substring(prefixed.indexOf('.') + 1), MINECRAFT + prefixed); }
        return out;
    }

    static boolean knownRecipe(String type) {
        ResourceLocation id = ResourceLocation.tryParse(type);
        return id == null || !"minecraft".equals(id.getNamespace()) || ForgeRegistries.RECIPE_SERIALIZERS.containsKey(id);
    }

    static String namespaced(String id) { return id.indexOf(':') < 0 ? MINECRAFT + id : id; }

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
        String action = string(event, "action");
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
        String action = string(event, "action");
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
                if (event.has(COMPONENTS) && event.get(COMPONENTS).isJsonObject()) { item.add(COMPONENTS, event.get(COMPONENTS)); }
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

    static String string(JsonObject json, String key) { return json.has(key) && json.get(key).isJsonPrimitive() ? json.get(key).getAsString() : ""; }

    static void stack(JsonObject stack, String file, Consumer<String> note) {
        if (stack.has(COMPONENTS) && stack.get(COMPONENTS).isJsonObject()) { components(stack.getAsJsonObject(COMPONENTS), file, note); }
    }

    static void components(JsonObject components, String file, Consumer<String> note) {
        List<String> lost = new ArrayList<>();
        Set<String> hidden = new HashSet<>();
        JsonElement display = components.remove(MINECRAFT + "tooltip_display");
        if (display != null && display.isJsonObject()) {
            JsonObject shown = display.getAsJsonObject();
            if (shown.has("hide_tooltip") && shown.get("hide_tooltip").getAsBoolean()) { components.add(MINECRAFT + "hide_tooltip", new JsonObject()); }
            if (shown.has("hidden_components") && shown.get("hidden_components").isJsonArray()) { shown.getAsJsonArray("hidden_components").forEach(one -> hidden.add(namespaced(one.getAsString()))); }
        }
        food(components, lost);
        for (String key : new ArrayList<>(components.keySet())) {
            String named = namespaced(key);
            JsonElement value = components.remove(key);
            JsonElement out = component(named, value);
            if (out == null) {
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
                JsonArray floats = data.has("floats") && data.get("floats").isJsonArray() ? data.getAsJsonArray("floats") : null;
                return floats == null || floats.isEmpty() ? null : new JsonPrimitive((int) Math.floor(floats.get(0).getAsDouble()));
            }
            case "minecraft:damage_resistant" -> {
                boolean fire = value.isJsonObject() && "#minecraft:is_fire".equals(string(value.getAsJsonObject(), "types"));
                return fire ? new JsonObject() : null;
            }
            case "minecraft:lock" -> { return lock(value); }
            case "minecraft:trim", "minecraft:unbreakable" -> { return value; }
            default -> { return WRAPPED.containsKey(named) ? wrapped(named, value) : value; }
        }
    }

    private static JsonElement wrapped(String named, JsonElement value) {
        String field = WRAPPED.get(named);
        if (value.isJsonObject() && (value.getAsJsonObject().has(field) || named.endsWith("can_break") || named.endsWith("can_place_on"))) { return value; }
        JsonElement inner = value;
        if ("modifiers".equals(field) && value.isJsonArray()) { inner = names(value); }
        if ("rgb".equals(field) && value.isJsonPrimitive() && value.getAsJsonPrimitive().isString()) { inner = new JsonPrimitive(Integer.parseInt(value.getAsString().replace("#", ""), 16) & 0xFFFFFF); }
        JsonObject out = new JsonObject();
        out.add(field, inner);
        return out;
    }

    private static JsonElement lock(JsonElement value) {
        if (value.isJsonPrimitive()) { return value; }
        JsonObject predicate = value.getAsJsonObject();
        JsonObject held = predicate.has(COMPONENTS) && predicate.get(COMPONENTS).isJsonObject() ? predicate.getAsJsonObject(COMPONENTS) : new JsonObject();
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
        if (!used.has("on_consume_effects") || !used.get("on_consume_effects").isJsonArray()) { return; }
        JsonArray effects = new JsonArray();
        for (JsonElement effect : used.getAsJsonArray("on_consume_effects")) {
            JsonObject applied = effect.isJsonObject() ? effect.getAsJsonObject() : new JsonObject();
            if (!"minecraft:apply_effects".equals(namespaced(string(applied, "type"))) || !applied.has("effects")) {
                lost.add(MINECRAFT + "consumable/" + string(applied, "type"));
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
