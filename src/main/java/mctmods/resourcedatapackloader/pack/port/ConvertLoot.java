package mctmods.resourcedatapackloader.pack.port;

import mctmods.resourcedatapackloader.util.Json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;
import java.util.ArrayList;
import java.util.Map;

final class ConvertLoot {
    private static final String KILLED_BY_PLAYER = "minecraft:killed_by_player";
    private static final String ENTITY_PROPERTIES = "minecraft:entity_properties";
    private static final String SET_ATTRIBUTES = "minecraft:set_attributes";

    private ConvertLoot() {}

    static String loot(JsonObject json, Ported pack) {
        if (GsonHelper.isValidPrimitive(json, "target")) { lootTable(json, "target", pack); }
        lootWalk(json, pack);
        return Ported.GSON.toJson(json);
    }

    private static void lootWalk(JsonElement element, Ported pack) {
        if (element.isJsonArray()) {
            for (JsonElement inner : element.getAsJsonArray()) { lootWalk(inner, pack); }
            return;
        }
        if (!element.isJsonObject()) { return; }
        JsonObject held = element.getAsJsonObject();
        for (String key : new String[] {"type", "function", "condition"}) {
            if (GsonHelper.isValidPrimitive(held, key) && held.get(key).getAsString().indexOf(':') < 0) {
                held.addProperty(key, "minecraft:" + held.get(key).getAsString());
                pack.rewrote();
            }
        }
        String condition = Json.text(held, "condition");
        if (KILLED_BY_PLAYER.equals(condition) && held.has("inverse")) { inverse(held, pack); }
        if (ENTITY_PROPERTIES.equals(condition) && held.has("properties") && held.get("properties").isJsonObject()) { entityProperties(held, pack); }
        if (held.has("function") && SET_ATTRIBUTES.equals(held.get("function").getAsString()) && held.has("modifiers") && held.get("modifiers").isJsonArray()) {
            for (JsonElement modifier : held.getAsJsonArray("modifiers")) {
                if (modifier.isJsonObject()) { attributeModifier(modifier.getAsJsonObject(), pack); }
            }
        }
        if (held.has("name") && held.has("type") && "minecraft:loot_table".equals(held.get("type").getAsString())) { lootTable(held, "name", pack); }
        if (held.has("name") && held.has("type") && "minecraft:item".equals(held.get("type").getAsString())) {
            int data = 0;
            boolean chosen = false;
            if (held.has("functions") && held.get("functions").isJsonArray()) {
                JsonArray functions = held.getAsJsonArray("functions");
                for (int i = functions.size() - 1; i >= 0; i--) {
                    JsonElement function = functions.get(i);
                    if (!function.isJsonObject()) { continue; }
                    JsonObject held2 = function.getAsJsonObject();
                    String name = held2.has("function") ? held2.get("function").getAsString() : "";
                    if ("minecraft:set_data".equals(name) || "set_data".equals(name)) {
                        if (!chosen && GsonHelper.isNumberValue(held2, "data")) {
                            data = held2.get("data").getAsInt();
                            chosen = true;
                        }
                        functions.remove(i);
                        pack.rewrote();
                    }
                }
            }
            held.addProperty("name", Convert.itemName(held.get("name").getAsString() + (data > 0 ? ":" + data : ""), pack));
        }
        for (Map.Entry<String, JsonElement> entry : new ArrayList<>(held.entrySet())) {
            if (!"name".equals(entry.getKey())) { lootWalk(entry.getValue(), pack); }
        }
    }

    static void lootTable(JsonObject held, String key, Ported pack) { Convert.renamed(held, key, Ids::lootTable, pack); }

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
        if (flags.size() > 0) { predicate.add("flags", flags); }
        held.add("predicate", predicate);
        pack.rewrote();
    }

    private static void attributeModifier(JsonObject modifier, Ported pack) {
        if (!GsonHelper.isValidPrimitive(modifier, "attribute")) { return; }
        String attribute = modifier.get("attribute").getAsString();
        String mapped = Ids.attribute(attribute);
        if (mapped.equals(attribute)) { return; }
        modifier.addProperty("attribute", mapped);
        pack.rewrote();
    }
}
