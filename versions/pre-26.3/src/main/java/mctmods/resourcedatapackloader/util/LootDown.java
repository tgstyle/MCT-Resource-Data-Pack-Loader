package mctmods.resourcedatapackloader.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public final class LootDown {
    private static final String TYPE = "type";
    private static final String CONDITION = "condition";
    private static final String CONDITIONS = "conditions";
    private static final String FUNCTION = "function";
    private static final String FUNCTIONS = "functions";
    private static final String MODIFIER = "modifier";
    private static final String TERMS = "terms";
    private static final String MINECRAFT = "minecraft:";
    private static final Set<String> PREDICATE_KEYS = Set.of("bystander", "cause", "child", "entity", "lightning", "parent", "partner", "player", "projectile", "source", "villager", "zombie", "location");
    private static final Set<String> SINGLE_BLOCK = Set.of("enter_block", "bee_nest_destroyed", "slide_down_block");

    private LootDown() {}

    public static JsonElement loot(JsonElement element, Consumer<String> note) {
        if (element.isJsonArray()) {
            JsonArray out = new JsonArray();
            for (JsonElement item : element.getAsJsonArray()) { out.add(loot(item, note)); }
            return out;
        }
        if (!element.isJsonObject()) { return element.deepCopy(); }
        JsonObject out = new JsonObject();
        for (Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) { walked(out, entry.getKey(), entry.getValue(), note); }
        if (out.size() == 2 && out.has("expected") && out.has("id") && out.get("id").isJsonPrimitive() && out.get("id").getAsString().startsWith("#")) { out.addProperty("id", out.get("id").getAsString().substring(1)); }
        if ("add".equals(type(out)) && out.has("inputs")) {
            out.addProperty(TYPE, MINECRAFT + "sum");
            out.add("summands", out.remove("inputs"));
        }
        return out;
    }

    public static JsonElement predicate(JsonElement element, Consumer<String> note) { return element.isJsonArray() ? terms(element, note) : condition(element, note); }

    public static JsonElement modifier(JsonElement element, Consumer<String> note) {
        if (!element.isJsonArray()) { return function(element, note); }
        JsonArray out = new JsonArray();
        for (JsonElement item : element.getAsJsonArray()) { out.add(function(item, note)); }
        return out;
    }

    public static JsonObject advancement(JsonObject json, Consumer<String> note) {
        JsonObject out = json.deepCopy();
        JsonElement criteria = out.get("criteria");
        if (criteria == null || !criteria.isJsonObject()) { return out; }
        for (Map.Entry<String, JsonElement> criterion : criteria.getAsJsonObject().entrySet()) {
            if (!criterion.getValue().isJsonObject()) { continue; }
            JsonObject held = criterion.getValue().getAsJsonObject();
            String trigger = held.has("trigger") ? bare(held.get("trigger").getAsString()) : "";
            if (held.has(CONDITIONS) && held.get(CONDITIONS).isJsonObject()) { held.add(CONDITIONS, trigger(held.getAsJsonObject(CONDITIONS), trigger, note)); }
        }
        return out;
    }

    private static JsonObject trigger(JsonObject conditions, String trigger, Consumer<String> note) {
        JsonObject out = new JsonObject();
        for (Map.Entry<String, JsonElement> entry : conditions.entrySet()) {
            String key = entry.getKey();
            JsonElement value = entry.getValue();
            switch (key) {
                case "blocks" -> out.add(SINGLE_BLOCK.contains(trigger) ? "block" : key, single(value, "the " + trigger + " trigger", note));
                case "loot_tables" -> out.add("loot_table", single(value, "the player_generates_container_loot trigger", note));
                case "recipes" -> out.add("recipe_unlocked".equals(trigger) ? "recipe" : "recipe_id", single(value, "the " + trigger + " trigger", note));
                case "victims" -> {
                    JsonArray victims = new JsonArray();
                    if (value.isJsonArray()) {
                        for (JsonElement victim : value.getAsJsonArray()) { victims.add(conditions(victim, note)); }
                    }
                    out.add(key, victims);
                }
                default -> out.add(key, PREDICATE_KEYS.contains(key) ? conditions(value, note) : loot(value, note));
            }
        }
        return out;
    }

    private static JsonElement single(JsonElement value, String where, Consumer<String> note) {
        if (value.isJsonArray() && value.getAsJsonArray().size() == 1) { return value.getAsJsonArray().get(0).deepCopy(); }
        if (value.isJsonPrimitive() && !value.getAsString().startsWith("#")) { return value.deepCopy(); }
        note.accept("names a set of ids in " + where + ", which 26.2 reads as one id, so only the first is kept");
        return value.isJsonArray() && !value.getAsJsonArray().isEmpty() ? value.getAsJsonArray().get(0).deepCopy() : value.deepCopy();
    }

    private static void walked(JsonObject out, String key, JsonElement value, Consumer<String> note) {
        switch (key) {
            case CONDITION -> out.add(CONDITIONS, conditions(value, note));
            case MODIFIER -> out.add(FUNCTIONS, functions(value, note));
            case "given_item_modifier" -> out.add("given_item_modifiers", functions(value, note));
            case "requirements", "merchant_predicate", "term" -> out.add(key, condition(value, note));
            case TERMS -> out.add(key, terms(value, note));
            case "on_pass", "on_fail" -> out.add(key, modifier(value, note));
            default -> out.add(key, loot(value, note));
        }
    }

    private static JsonArray conditions(JsonElement value, Consumer<String> note) {
        if (value.isJsonObject() && "all_of".equals(type(value.getAsJsonObject())) && value.getAsJsonObject().has(TERMS) && value.getAsJsonObject().get(TERMS).isJsonArray()) { return terms(value.getAsJsonObject().get(TERMS), note); }
        JsonArray out = new JsonArray();
        out.add(condition(value, note));
        return out;
    }

    private static JsonArray terms(JsonElement value, Consumer<String> note) {
        JsonArray out = new JsonArray();
        if (!value.isJsonArray()) {
            out.add(condition(value, note));
            return out;
        }
        for (JsonElement term : value.getAsJsonArray()) { out.add(condition(term, note)); }
        return out;
    }

    private static JsonArray functions(JsonElement value, Consumer<String> note) {
        JsonArray out = new JsonArray();
        if (!value.isJsonArray()) {
            out.add(function(value, note));
            return out;
        }
        for (JsonElement item : value.getAsJsonArray()) { out.add(function(item, note)); }
        return out;
    }

    private static JsonElement condition(JsonElement value, Consumer<String> note) {
        if (value.isJsonPrimitive()) { return reference(CONDITION, value.getAsString(), note); }
        if (!value.isJsonObject()) { return value.deepCopy(); }
        JsonObject in = value.getAsJsonObject();
        String type = type(in);
        JsonObject out = new JsonObject();
        for (Map.Entry<String, JsonElement> entry : in.entrySet()) {
            String key = entry.getKey();
            JsonElement held = entry.getValue();
            if (TYPE.equals(key)) { out.add(CONDITION, held.deepCopy()); }
            else if ("match_block".equals(type) && "blocks".equals(key)) { out.add("block", single(held, "a block condition", note)); }
            else if ("match_block".equals(type) && "state".equals(key)) { out.add("properties", held.deepCopy()); }
            else if ("match_block".equals(type)) { note.accept("tests block " + key + " in a block condition, which 26.2 cannot, so that test is dropped"); }
            else if (type.endsWith("_value_check") && "test".equals(key)) { out.add("range", held.deepCopy()); }
            else { walked(out, key, held, note); }
        }
        switch (type) {
            case "match_block" -> out.addProperty(CONDITION, MINECRAFT + "block_state_property");
            case "int_value_check", "float_value_check" -> out.addProperty(CONDITION, MINECRAFT + "value_check");
            default -> { }
        }
        return out;
    }

    private static JsonElement function(JsonElement value, Consumer<String> note) {
        if (value.isJsonPrimitive()) { return reference(FUNCTION, value.getAsString(), note); }
        if (value.isJsonArray()) {
            JsonObject sequence = new JsonObject();
            sequence.addProperty(FUNCTION, MINECRAFT + "sequence");
            sequence.add(FUNCTIONS, functions(value, note));
            return sequence;
        }
        if (!value.isJsonObject()) { return value.deepCopy(); }
        JsonObject out = new JsonObject();
        for (Map.Entry<String, JsonElement> entry : value.getAsJsonObject().entrySet()) {
            switch (entry.getKey()) {
                case TYPE -> out.add(FUNCTION, entry.getValue().deepCopy());
                case FUNCTIONS -> out.add(FUNCTIONS, functions(entry.getValue(), note));
                case "destination" -> destination(out, entry.getValue(), note);
                default -> walked(out, entry.getKey(), entry.getValue(), note);
            }
        }
        return out;
    }

    private static void destination(JsonObject out, JsonElement value, Consumer<String> note) {
        if (value.isJsonPrimitive() && value.getAsString().startsWith("#")) { out.addProperty("destination", value.getAsString().substring(1)); }
        else { note.accept("sends an explorer map to named structures, and 26.2 takes only a structure tag, so the map leads to treasure structures"); }
    }

    private static JsonObject reference(String kind, String id, Consumer<String> note) {
        if (id.startsWith("#")) { note.accept("names a tag of " + (CONDITION.equals(kind) ? "predicates" : "item modifiers") + ", which 26.2 cannot, so it is read as the one id " + id.substring(1)); }
        JsonObject out = new JsonObject();
        out.addProperty(kind, MINECRAFT + "reference");
        out.addProperty("name", id.startsWith("#") ? id.substring(1) : id);
        return out;
    }

    private static String type(JsonObject object) { return object.has(TYPE) && object.get(TYPE).isJsonPrimitive() ? bare(object.get(TYPE).getAsString()) : ""; }

    private static String bare(String id) { return id.startsWith(MINECRAFT) ? id.substring(MINECRAFT.length()) : id; }
}
