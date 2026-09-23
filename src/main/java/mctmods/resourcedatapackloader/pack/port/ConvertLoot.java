package mctmods.resourcedatapackloader.pack.port;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

final class ConvertLoot {
    private static final String KILLED_BY_PLAYER = "minecraft:killed_by_player";
    private static final String ENTITY_PROPERTIES = "minecraft:entity_properties";
    private static final String SET_ATTRIBUTES = "minecraft:set_attributes";
    private static final Map<String, String> OPERATIONS = Map.of("addition", "add_value", "multiply_base", "add_multiplied_base", "multiply_total", "add_multiplied_total");
    private static final Pattern ID_CHARACTERS = Pattern.compile("[^a-z0-9_./-]");

    private ConvertLoot() {}

    public static String loot(JsonObject json, String namespace, Ported pack) {
        if (json.has("target") && json.get("target").isJsonPrimitive()) { lootTable(json, "target", pack); }
        lootWalk(json, namespace, pack);
        return Ported.GSON.toJson(json);
    }

    private static void lootWalk(JsonElement element, String namespace, Ported pack) {
        if (element.isJsonArray()) {
            for (JsonElement inner : element.getAsJsonArray()) { lootWalk(inner, namespace, pack); }
            return;
        }
        if (!element.isJsonObject()) { return; }
        JsonObject held = element.getAsJsonObject();
        for (String key : new String[] {"type", "function", "condition"}) {
            if (held.has(key) && held.get(key).isJsonPrimitive() && held.get(key).getAsString().indexOf(':') < 0) {
                held.addProperty(key, "minecraft:" + held.get(key).getAsString());
                pack.rewrote();
            }
        }
        String condition = held.has("condition") && held.get("condition").isJsonPrimitive() ? held.get("condition").getAsString() : "";
        if (KILLED_BY_PLAYER.equals(condition) && held.has("inverse")) { inverse(held, pack); }
        if (ENTITY_PROPERTIES.equals(condition) && held.has("properties") && held.get("properties").isJsonObject()) { entityProperties(held, pack); }
        if (held.has("function") && SET_ATTRIBUTES.equals(held.get("function").getAsString()) && held.has("modifiers") && held.get("modifiers").isJsonArray()) {
            for (JsonElement modifier : held.getAsJsonArray("modifiers")) {
                if (modifier.isJsonObject()) { attributeModifier(modifier.getAsJsonObject(), namespace, pack); }
            }
        }
        if (held.has("name") && held.has("type") && "minecraft:loot_table".equals(held.get("type").getAsString())) {
            lootTable(held, "name", pack);
            held.add("value", held.remove("name"));
        }
        if (held.has("name") && held.has("type") && "minecraft:item".equals(held.get("type").getAsString())) {
            int data = 0;
            if (held.has("functions") && held.get("functions").isJsonArray()) {
                JsonArray functions = held.getAsJsonArray("functions");
                for (int i = functions.size() - 1; i >= 0; i--) {
                    JsonElement function = functions.get(i);
                    if (!function.isJsonObject()) { continue; }
                    JsonObject held2 = function.getAsJsonObject();
                    String name = held2.has("function") ? held2.get("function").getAsString() : "";
                    if ("minecraft:set_data".equals(name) || "set_data".equals(name)) {
                        if (held2.has("data") && held2.get("data").isJsonPrimitive() && held2.get("data").getAsJsonPrimitive().isNumber()) { data = held2.get("data").getAsInt(); }
                        functions.remove(i);
                        pack.rewrote();
                    }
                }
            }
            held.addProperty("name", Convert.itemName(held.get("name").getAsString() + (data > 0 ? ":" + data : ""), pack));
        }
        for (Map.Entry<String, JsonElement> entry : new ArrayList<>(held.entrySet())) {
            if (!"name".equals(entry.getKey())) { lootWalk(entry.getValue(), namespace, pack); }
        }
    }

    static void lootTable(JsonObject held, String key, Ported pack) {
        String named = held.get(key).getAsString();
        String mapped = Ids.lootTable(named);
        if (mapped.equals(named)) { return; }
        held.addProperty(key, mapped);
        pack.rewrote();
    }

    private static void inverse(JsonObject held, Ported pack) {
        boolean inverse = held.remove("inverse").getAsBoolean();
        pack.rewrote();
        if (!inverse) { return; }
        JsonObject term = new JsonObject();
        term.addProperty("condition", KILLED_BY_PLAYER);
        held.addProperty("condition", "minecraft:inverted");
        held.add("term", term);
    }

    private static void entityProperties(JsonObject held, Ported pack) {
        JsonObject properties = held.remove("properties").getAsJsonObject();
        JsonObject flags = new JsonObject();
        for (Map.Entry<String, JsonElement> property : properties.entrySet()) {
            if ("on_fire".equals(property.getKey())) { flags.addProperty("is_on_fire", property.getValue().getAsBoolean()); }
            else { pack.note("An entity_properties condition asks for the 1.12.2 property '" + property.getKey() + "', which has no twin here and is left out"); }
        }
        JsonObject predicate = held.has("predicate") && held.get("predicate").isJsonObject() ? held.getAsJsonObject("predicate") : new JsonObject();
        if (!flags.isEmpty()) { predicate.add("flags", flags); }
        held.add("predicate", predicate);
        pack.rewrote();
    }

    private static void attributeModifier(JsonObject modifier, String namespace, Ported pack) {
        if (!modifier.has("attribute") || !modifier.get("attribute").isJsonPrimitive()) { return; }
        String attribute = modifier.get("attribute").getAsString();
        modifier.addProperty("attribute", Ids.attribute(attribute));
        if (modifier.has("operation") && modifier.get("operation").isJsonPrimitive()) {
            String operation = modifier.get("operation").getAsString();
            modifier.addProperty("operation", OPERATIONS.getOrDefault(operation, operation));
        }
        if (modifier.has("name")) {
            String name = modifier.remove("name").getAsString();
            modifier.addProperty("id", namespace + ":" + ID_CHARACTERS.matcher(name.toLowerCase(Locale.ROOT)).replaceAll("_"));
        }
        pack.rewrote();
    }
}
