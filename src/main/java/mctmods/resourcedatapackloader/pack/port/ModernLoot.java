package mctmods.resourcedatapackloader.pack.port;

import static mctmods.resourcedatapackloader.util.IdNames.MINECRAFT;
import static mctmods.resourcedatapackloader.util.IdNames.bare;
import static mctmods.resourcedatapackloader.util.IdNames.namespaced;

import mctmods.resourcedatapackloader.util.Json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.util.GsonHelper;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

final class ModernLoot {
    private static final String TYPE_SPECIFIC = "type_specific";
    private static final Map<String, String> TARGETS = Map.of("killer", "attacker", "direct_killer", "direct_attacker", "killer_player", "attacking_player");
    private static final Set<String> TARGET_KEYS = Set.of("entity", "source");
    private static final Set<String> VARIANTS = Set.of("axolotl", "fox", "mooshroom", "rabbit", "horse", "llama", "villager", "parrot", "painting", "cat", "frog", "wolf");
    private static final List<String> TEXTS = List.of("title", "description");

    private ModernLoot() {}

    static void loot(JsonObject json, String file, boolean stacks, Consumer<String> note) {
        walk(json, file, stacks, note);
        ModernFixes.replace(json, ModernFixes.names(json).getAsJsonObject());
    }

    static void advancement(JsonObject json, String file, boolean stacks, Consumer<String> note) {
        JsonObject display = json.has("display") && json.get("display").isJsonObject() ? json.getAsJsonObject("display") : null;
        if (display != null) {
            if (stacks && display.has("icon") && display.get("icon").isJsonObject()) { display.add("icon", ModernFixes.stack(display.getAsJsonObject("icon"))); }
            for (String key : TEXTS) {
                if (display.has(key)) { display.add(key, ModernFixes.text(display.get(key))); }
            }
        }
        JsonObject rewards = json.has("rewards") && json.get("rewards").isJsonObject() ? json.getAsJsonObject("rewards") : null;
        if (rewards != null && rewards.has("recipes") && rewards.get("recipes").isJsonArray()) {
            JsonArray recipes = new JsonArray();
            for (JsonElement recipe : rewards.getAsJsonArray("recipes")) { recipes.add(recipe.isJsonPrimitive() ? new JsonPrimitive(ModernFixes.recipe(recipe.getAsString())) : recipe); }
            rewards.add("recipes", recipes);
        }
        loot(json, file, stacks, note);
    }

    private static void walk(JsonElement element, String file, boolean stacks, Consumer<String> note) {
        if (element.isJsonArray()) {
            element.getAsJsonArray().forEach(inner -> walk(inner, file, stacks, note));
            return;
        }
        if (!element.isJsonObject()) { return; }
        JsonObject json = element.getAsJsonObject();
        for (String key : TARGET_KEYS) {
            if (GsonHelper.isValidPrimitive(json, key) && TARGETS.containsKey(json.get(key).getAsString())) { json.addProperty(key, TARGETS.get(json.get(key).getAsString())); }
        }
        String condition = namespaced(Json.text(json, "condition"));
        if ("minecraft:time_check".equals(condition) && !json.has("clock")) { json.addProperty("clock", MINECRAFT + "overworld"); }
        if (GsonHelper.isValidPrimitive(json, "recipe")) { json.addProperty("recipe", ModernFixes.recipe(json.get("recipe").getAsString())); }
        String function = Json.text(json, "function");
        if (!function.isEmpty()) { function(json, function, stacks); }
        else if (stacks && json.has(CrossJson.COMPONENTS) && json.get(CrossJson.COMPONENTS).isJsonObject()) { json.add(CrossJson.COMPONENTS, ModernFixes.components(ModernFixes.firstItem(json.get("items")), json.getAsJsonObject(CrossJson.COMPONENTS))); }
        if (json.has(TYPE_SPECIFIC) && json.get(TYPE_SPECIFIC).isJsonObject()) { variant(json, file, note); }
        json.entrySet().forEach(entry -> walk(entry.getValue(), file, stacks, note));
    }

    private static void function(JsonObject json, String function, boolean stacks) {
        switch (bare(function)) {
            case "set_components" -> {
                if (stacks && json.has(CrossJson.COMPONENTS) && json.get(CrossJson.COMPONENTS).isJsonObject()) { json.add(CrossJson.COMPONENTS, ModernFixes.components("minecraft:stone", json.getAsJsonObject(CrossJson.COMPONENTS))); }
            }
            case "set_custom_model_data" -> {
                if (!json.has("value")) { return; }
                JsonArray values = new JsonArray();
                values.add(json.remove("value"));
                JsonObject floats = new JsonObject();
                floats.add("values", values);
                floats.addProperty("mode", "replace_all");
                json.add("floats", floats);
            }
            case "set_name" -> {
                if (json.has("name")) { json.add("name", ModernFixes.text(json.get("name"))); }
            }
            case "set_lore" -> {
                if (!json.has("lore") || !json.get("lore").isJsonArray()) { return; }
                JsonArray lore = new JsonArray();
                json.getAsJsonArray("lore").forEach(line -> lore.add(ModernFixes.text(line)));
                json.add("lore", lore);
            }
            default -> { }
        }
    }

    private static void variant(JsonObject json, String file, Consumer<String> note) {
        JsonObject specific = json.getAsJsonObject(TYPE_SPECIFIC);
        String bare = bare(Json.text(specific, "type"));
        String component = VARIANTS.contains(bare) ? bare + "/variant" : "tropical_fish".equals(bare) ? "tropical_fish/pattern" : null;
        if ("boat".equals(bare)) {
            json.remove(TYPE_SPECIFIC);
            note.accept("'" + file + "' tests a boat's wood type, which is its entity type now (for example minecraft:oak_boat), so that test is left out; test the entity type by hand");
            return;
        }
        if (component == null || !specific.has("variant")) { return; }
        JsonElement variant = specific.get("variant");
        if (!variant.isJsonPrimitive() || variant.getAsString().startsWith("#")) {
            note.accept("'" + file + "' tests the " + bare + " variant against a list or tag, which a component test cannot hold, so the test is left as written; name one variant by hand");
            return;
        }
        json.remove(TYPE_SPECIFIC);
        JsonObject components = json.has(CrossJson.COMPONENTS) && json.get(CrossJson.COMPONENTS).isJsonObject() ? json.getAsJsonObject(CrossJson.COMPONENTS) : new JsonObject();
        components.add(MINECRAFT + component, variant);
        json.add(CrossJson.COMPONENTS, components);
    }
}
