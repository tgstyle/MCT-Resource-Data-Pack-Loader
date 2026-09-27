package mctmods.resourcedatapackloader.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Map;
import java.util.Set;

public final class LootUp {
    private static final String TYPE = "type";
    private static final String CONDITION = "condition";
    private static final String CONDITIONS = "conditions";
    private static final String FUNCTION = "function";
    private static final String FUNCTIONS = "functions";
    private static final String MODIFIER = "modifier";
    private static final String GIVEN = "given_item_modifiers";
    private static final String MINECRAFT = "minecraft:";
    private static final String DESTINATION = "destination";
    private static final Set<String> ENTITY_KEYS = Set.of("bystander", "cause", "child", "entity", "lightning", "parent", "partner", "player", "projectile", "source", "villager", "zombie");
    private static final Map<String, String> TRIGGER_KEYS = Map.of("block", "blocks", "loot_table", "loot_tables", "recipe", "recipes", "recipe_id", "recipes");

    private LootUp() {}

    public static JsonElement loot(JsonElement element) {
        if (element.isJsonArray()) {
            JsonArray out = new JsonArray();
            for (JsonElement item : element.getAsJsonArray()) { out.add(loot(item)); }
            return out;
        }
        if (!element.isJsonObject()) { return element.deepCopy(); }
        JsonObject in = element.getAsJsonObject();
        boolean condition = named(in, CONDITION);
        boolean function = !condition && named(in, FUNCTION);
        JsonObject out = new JsonObject();
        for (Map.Entry<String, JsonElement> entry : in.entrySet()) {
            String key = entry.getKey();
            JsonElement value = entry.getValue();
            if (condition && CONDITION.equals(key) || function && FUNCTION.equals(key)) { out.add(TYPE, value.deepCopy()); }
            else if (CONDITIONS.equals(key) && listOf(value, CONDITION)) { put(out, CONDITION, allOf(loot(value).getAsJsonArray())); }
            else if (FUNCTIONS.equals(key) && listOf(value, FUNCTION) && !(function && "sequence".equals(id(in, FUNCTION)))) { put(out, MODIFIER, modifier(loot(value).getAsJsonArray())); }
            else if (GIVEN.equals(key) && value.isJsonArray()) { put(out, "given_item_modifier", modifier(loot(value).getAsJsonArray())); }
            else { out.add(key, loot(value)); }
        }
        if (condition) { return condition(out); }
        return function ? function(out) : value(out);
    }

    private static JsonObject value(JsonObject out) {
        if (out.size() == 2 && named(out, "id") && out.has("expected") && !out.get("id").getAsString().startsWith("#")) { out.addProperty("id", "#" + out.get("id").getAsString()); }
        if ("sum".equals(id(out, TYPE))) {
            out.addProperty(TYPE, MINECRAFT + "add");
            rename(out, "summands", "inputs");
        }
        return out;
    }

    public static JsonObject advancement(JsonObject json) {
        JsonObject copy = json.deepCopy();
        JsonElement criteria = copy.get("criteria");
        if (criteria != null && criteria.isJsonObject()) {
            for (Map.Entry<String, JsonElement> criterion : criteria.getAsJsonObject().entrySet()) {
                if (!criterion.getValue().isJsonObject()) { continue; }
                JsonObject held = criterion.getValue().getAsJsonObject();
                if (held.has(CONDITIONS) && held.get(CONDITIONS).isJsonObject()) { held.add(CONDITIONS, trigger(held.getAsJsonObject(CONDITIONS))); }
            }
        }
        return loot(copy).getAsJsonObject();
    }

    private static JsonObject trigger(JsonObject conditions) {
        JsonObject out = new JsonObject();
        for (Map.Entry<String, JsonElement> entry : conditions.entrySet()) {
            String key = entry.getKey();
            JsonElement value = entry.getValue();
            if (TRIGGER_KEYS.containsKey(key)) { out.add(TRIGGER_KEYS.get(key), value); }
            else if ("victims".equals(key) && value.isJsonArray()) {
                JsonArray victims = new JsonArray();
                for (JsonElement victim : value.getAsJsonArray()) { victims.add(predicate(victim, true)); }
                out.add(key, victims);
            }
            else if (ENTITY_KEYS.contains(key) || "location".equals(key)) { put(out, key, predicate(value, ENTITY_KEYS.contains(key))); }
            else { out.add(key, value); }
        }
        return out;
    }

    private static JsonElement predicate(JsonElement value, boolean entity) {
        if (value.isJsonArray()) { return allOf(value.getAsJsonArray()); }
        if (!entity || !value.isJsonObject() || value.getAsJsonObject().has(CONDITION)) { return value; }
        JsonObject out = new JsonObject();
        out.addProperty(CONDITION, MINECRAFT + "entity_properties");
        out.addProperty("entity", "this");
        out.add("predicate", value);
        return out;
    }

    private static JsonElement condition(JsonObject out) {
        switch (id(out, TYPE)) {
            case "block_state_property" -> {
                out.addProperty(TYPE, MINECRAFT + "match_block");
                rename(out, "block", "blocks");
                rename(out, "properties", "state");
            }
            case "reference" -> { return out.get("name"); }
            case "value_check" -> {
                out.addProperty(TYPE, MINECRAFT + "int_value_check");
                rename(out, "range", "test");
            }
            default -> { }
        }
        return out;
    }

    private static JsonElement function(JsonObject out) {
        if ("exploration_map".equals(id(out, TYPE))) {
            String destination = named(out, DESTINATION) ? out.get(DESTINATION).getAsString() : MINECRAFT + "on_treasure_maps";
            out.addProperty(DESTINATION, destination.startsWith("#") ? destination : "#" + destination);
        }
        if (!"reference".equals(id(out, TYPE))) { return out; }
        JsonElement name = out.get("name");
        if (!out.has(CONDITION)) { return name; }
        JsonObject sequence = new JsonObject();
        sequence.addProperty(TYPE, MINECRAFT + "sequence");
        sequence.add(CONDITION, out.get(CONDITION));
        JsonArray functions = new JsonArray();
        functions.add(name);
        sequence.add(FUNCTIONS, functions);
        return sequence;
    }

    private static JsonElement allOf(JsonArray terms) {
        if (terms.size() < 2) { return terms.isEmpty() ? null : terms.get(0); }
        JsonObject out = new JsonObject();
        out.addProperty(TYPE, MINECRAFT + "all_of");
        out.add("terms", terms);
        return out;
    }

    private static JsonElement modifier(JsonArray functions) { return functions.size() < 2 ? functions.isEmpty() ? null : functions.get(0) : functions; }

    private static void put(JsonObject out, String key, JsonElement value) {
        if (value != null) { out.add(key, value); }
    }

    private static void rename(JsonObject out, String from, String to) {
        JsonElement held = out.remove(from);
        if (held != null) { out.add(to, held); }
    }

    private static boolean listOf(JsonElement value, String key) {
        if (!value.isJsonArray()) { return false; }
        for (JsonElement item : value.getAsJsonArray()) {
            if (!item.isJsonObject() || !named(item.getAsJsonObject(), key)) { return false; }
        }
        return true;
    }

    private static boolean named(JsonObject object, String key) { return object.has(key) && object.get(key).isJsonPrimitive() && object.get(key).getAsJsonPrimitive().isString(); }

    private static String id(JsonObject object, String key) {
        String held = named(object, key) ? object.get(key).getAsString() : "";
        return held.startsWith(MINECRAFT) ? held.substring(MINECRAFT.length()) : held;
    }
}
