package mctmods.resourcedatapackloader.pack.port;

import mctmods.resourcedatapackloader.util.JsonTree;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

final class DownLoot {
    private static final String COMPONENTS = "components";
    private static final String TYPE_SPECIFIC = "type_specific";
    private static final String VARIANT = "/variant";
    private static final Map<String, String> TARGETS = DownFixes.pairs("attacker", "killer", "direct_attacker", "direct_killer", "attacking_player", "killer_player");
    private static final List<String> TARGET_KEYS = Arrays.asList("entity", "source");
    private static final List<String> VARIANTS = Arrays.asList("axolotl", "fox", "mooshroom", "rabbit", "horse", "llama", "villager", "parrot", "painting", "cat", "frog", "wolf");
    private static final List<String> TEXTS = Arrays.asList("title", "description");
    private static final List<String> MODEL_LISTS = Arrays.asList("flags", "strings", "colors");

    private DownLoot() {}

    static void loot(JsonObject json, String file, Consumer<String> note) {
        walk(json, file, note);
        JsonTree.replace(json, DownFixes.names(json).getAsJsonObject());
    }

    static void advancement(JsonObject json, String file, Consumer<String> note) {
        JsonObject display = json.has("display") && json.get("display").isJsonObject() ? json.getAsJsonObject("display") : null;
        if (display != null) {
            if (display.has("icon") && display.get("icon").isJsonObject()) { DownFixes.stack(display.getAsJsonObject("icon"), file, note); }
            for (String key : TEXTS) {
                if (display.has(key)) { display.add(key, DownFixes.text(display.get(key))); }
            }
        }
        JsonObject rewards = json.has("rewards") && json.get("rewards").isJsonObject() ? json.getAsJsonObject("rewards") : null;
        if (rewards != null && rewards.has("recipes") && rewards.get("recipes").isJsonArray()) {
            JsonArray recipes = new JsonArray();
            for (JsonElement recipe : rewards.getAsJsonArray("recipes")) { recipes.add(recipe.isJsonPrimitive() ? new JsonPrimitive(DownFixes.recipe(recipe.getAsString())) : recipe); }
            rewards.add("recipes", recipes);
        }
        loot(json, file, note);
    }

    private static void walk(JsonElement element, String file, Consumer<String> note) {
        if (element.isJsonArray()) {
            for (JsonElement inner : element.getAsJsonArray()) { walk(inner, file, note); }
            return;
        }
        if (!element.isJsonObject()) { return; }
        JsonObject json = element.getAsJsonObject();
        for (String key : TARGET_KEYS) {
            if (json.has(key) && json.get(key).isJsonPrimitive() && TARGETS.containsKey(json.get(key).getAsString())) { json.addProperty(key, TARGETS.get(json.get(key).getAsString())); }
        }
        String condition = DownFixes.namespaced(DownFixes.string(json, "condition"));
        if ("minecraft:time_check".equals(condition)) { json.remove("clock"); }
        if (json.has("recipe") && json.get("recipe").isJsonPrimitive()) { json.addProperty("recipe", DownFixes.recipe(json.get("recipe").getAsString())); }
        String function = DownFixes.string(json, "function");
        if (!function.isEmpty()) { function(json, DownFixes.namespaced(function), file, note); }
        else if (json.has(COMPONENTS) && json.get(COMPONENTS).isJsonObject()) {
            variant(json);
            DownFixes.components(json.getAsJsonObject(COMPONENTS), file, note);
            if (json.getAsJsonObject(COMPONENTS).size() == 0) { json.remove(COMPONENTS); }
        }
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) { walk(entry.getValue(), file, note); }
    }

    private static void function(JsonObject json, String function, String file, Consumer<String> note) {
        switch (function) {
            case "minecraft:set_components":
                if (json.has(COMPONENTS) && json.get(COMPONENTS).isJsonObject()) { DownFixes.components(json.getAsJsonObject(COMPONENTS), file, note); }
                break;
            case "minecraft:set_custom_model_data":
                modelData(json, file, note);
                break;
            case "minecraft:set_name":
                if (json.has("name")) { json.add("name", DownFixes.text(json.get("name"))); }
                break;
            case "minecraft:set_lore": {
                if (!json.has("lore") || !json.get("lore").isJsonArray()) { return; }
                JsonArray lore = new JsonArray();
                for (JsonElement line : json.getAsJsonArray("lore")) { lore.add(DownFixes.text(line)); }
                json.add("lore", lore);
                break;
            }
            default: break;
        }
    }

    private static void modelData(JsonObject json, String file, Consumer<String> note) {
        if (json.has("value")) { return; }
        JsonElement floats = json.remove("floats");
        List<String> lost = new ArrayList<>();
        for (String other : MODEL_LISTS) {
            if (json.remove(other) != null) { lost.add(other); }
        }
        JsonArray values = floats != null && floats.isJsonObject() && floats.getAsJsonObject().has("values") && floats.getAsJsonObject().get("values").isJsonArray() ? floats.getAsJsonObject().getAsJsonArray("values") : null;
        if (values != null && values.size() > 0) { json.add("value", values.get(0)); }
        else { json.addProperty("value", 0); }
        if (values != null && values.size() > 1) { lost.add("floats after the first"); }
        if (!lost.isEmpty()) { note.accept("'" + file + "' sets custom model data " + String.join(", ", lost) + ", which " + DownFixes.TARGET + " cannot hold (one whole number only), so they are left out"); }
    }

    private static void variant(JsonObject json) {
        JsonObject components = json.getAsJsonObject(COMPONENTS);
        for (String key : JsonTree.keys(components)) {
            String named = DownFixes.namespaced(key);
            if (!named.startsWith(DownFixes.MINECRAFT)) { continue; }
            String bare = named.substring(DownFixes.MINECRAFT.length());
            String owner = bare.endsWith(VARIANT) ? bare.substring(0, bare.length() - VARIANT.length()) : "";
            String entity = VARIANTS.contains(owner) ? owner : "tropical_fish/pattern".equals(bare) ? "tropical_fish" : null;
            if (entity == null || json.has(TYPE_SPECIFIC)) { continue; }
            JsonObject specific = new JsonObject();
            specific.addProperty("type", DownFixes.MINECRAFT + entity);
            specific.add("variant", components.remove(key));
            json.add(TYPE_SPECIFIC, specific);
        }
    }
}
