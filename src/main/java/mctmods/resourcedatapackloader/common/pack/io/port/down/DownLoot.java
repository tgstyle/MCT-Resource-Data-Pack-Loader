package mctmods.resourcedatapackloader.common.pack.io.port.down;

import static mctmods.resourcedatapackloader.core.util.IdNames.MINECRAFT;
import static mctmods.resourcedatapackloader.core.util.IdNames.namespaced;
import mctmods.resourcedatapackloader.core.util.IdNames;
import mctmods.resourcedatapackloader.core.util.json.Json;
import mctmods.resourcedatapackloader.core.util.json.JsonTree;
import mctmods.resourcedatapackloader.core.util.text.NumberDown;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.util.JsonUtils;

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

    static void loot(JsonObject json, Consumer<String> note) {
        walk(json, note);
        DownFixes.rename(json);
    }

    static void advancement(JsonObject json, Consumer<String> note) {
        JsonObject display = Json.object(json, "display");
        JsonObject icon = Json.objectOrNull(display, "icon");
        if (icon != null) { DownFixes.stack(icon, note); }
        for (String key : TEXTS) {
            if (display.has(key)) { display.add(key, DownFixes.text(display.get(key))); }
        }
        JsonObject rewards = Json.object(json, "rewards");
        if (JsonUtils.isJsonArray(rewards, "recipes")) {
            JsonArray recipes = new JsonArray();
            for (JsonElement recipe : rewards.getAsJsonArray("recipes")) { recipes.add(recipe.isJsonPrimitive() ? new JsonPrimitive(DownFixes.recipe(recipe.getAsString())) : recipe); }
            rewards.add("recipes", recipes);
        }
        loot(json, note);
    }

    private static void walk(JsonElement element, Consumer<String> note) {
        if (element.isJsonArray()) {
            for (JsonElement inner : element.getAsJsonArray()) { walk(inner, note); }
            return;
        }
        if (!element.isJsonObject()) { return; }
        JsonObject json = element.getAsJsonObject();
        for (String key : TARGET_KEYS) {
            if (JsonUtils.isJsonPrimitive(json, key) && TARGETS.containsKey(json.get(key).getAsString())) { json.addProperty(key, TARGETS.get(json.get(key).getAsString())); }
        }
        String condition = Json.id(json, "condition");
        if ("minecraft:time_check".equals(condition)) { json.remove("clock"); }
        if (JsonUtils.isJsonPrimitive(json, "recipe")) { json.addProperty("recipe", DownFixes.recipe(json.get("recipe").getAsString())); }
        String function = Json.text(json, "function");
        if (!function.isEmpty()) { function(json, namespaced(function), note); }
        else if (Json.objectOrNull(json, COMPONENTS) != null) {
            variant(json);
            DownFixes.components(json.getAsJsonObject(COMPONENTS), note);
            if (json.getAsJsonObject(COMPONENTS).size() == 0) { json.remove(COMPONENTS); }
        }
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) { walk(entry.getValue(), note); }
    }

    private static void function(JsonObject json, String function, Consumer<String> note) {
        switch (function) {
            case "minecraft:set_components":
                DownFixes.stack(json, note);
                break;
            case "minecraft:set_custom_model_data":
                modelData(json, note);
                break;
            case "minecraft:set_name":
                if (json.has("name")) { json.add("name", DownFixes.text(json.get("name"))); }
                break;
            case "minecraft:set_lore": {
                if (!JsonUtils.isJsonArray(json, "lore")) { return; }
                JsonArray lore = new JsonArray();
                for (JsonElement line : json.getAsJsonArray("lore")) { lore.add(DownFixes.text(line)); }
                json.add("lore", lore);
                break;
            }
            default: break;
        }
    }

    private static void modelData(JsonObject json, Consumer<String> note) {
        if (json.has(NumberDown.VALUE)) { return; }
        JsonElement floats = json.remove("floats");
        List<String> lost = new ArrayList<>();
        for (String other : MODEL_LISTS) {
            if (json.remove(other) != null) { lost.add(other); }
        }
        JsonArray values = floats != null && floats.isJsonObject() && JsonUtils.isJsonArray(floats.getAsJsonObject(), "values") ? floats.getAsJsonObject().getAsJsonArray("values") : null;
        if (values != null && values.size() > 0) { json.add(NumberDown.VALUE, values.get(0)); }
        else { json.addProperty(NumberDown.VALUE, 0); }
        if (values != null && values.size() > 1) { lost.add("floats after the first"); }
        if (!lost.isEmpty()) { note.accept("sets custom model data " + String.join(", ", lost) + ", which " + DownFixes.TARGET + " cannot hold (one whole number only), so they are left out"); }
    }

    private static void variant(JsonObject json) {
        JsonObject components = json.getAsJsonObject(COMPONENTS);
        for (String key : JsonTree.keys(components)) {
            String named = namespaced(key);
            String bare = IdNames.bare(named);
            if (bare.equals(named)) { continue; }
            String owner = bare.endsWith(VARIANT) ? bare.substring(0, bare.length() - VARIANT.length()) : "";
            String entity = VARIANTS.contains(owner) ? owner : "tropical_fish/pattern".equals(bare) ? "tropical_fish" : null;
            if (entity == null || json.has(TYPE_SPECIFIC)) { continue; }
            JsonObject specific = new JsonObject();
            specific.addProperty("type", MINECRAFT + entity);
            specific.add("variant", components.remove(key));
            json.add(TYPE_SPECIFIC, specific);
        }
    }
}
