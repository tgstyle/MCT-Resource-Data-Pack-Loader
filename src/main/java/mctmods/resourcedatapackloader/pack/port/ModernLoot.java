package mctmods.resourcedatapackloader.pack.port;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

final class ModernLoot {
    private static final String COMPONENTS = "components";
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
        if (json.has("rewards") && json.get("rewards").isJsonObject() && json.getAsJsonObject("rewards").has("recipes") && json.getAsJsonObject("rewards").get("recipes").isJsonArray()) {
            JsonArray recipes = new JsonArray();
            for (JsonElement recipe : json.getAsJsonObject("rewards").getAsJsonArray("recipes")) { recipes.add(recipe.isJsonPrimitive() ? ModernFixes.recipe(recipe.getAsString()) : recipe.getAsString()); }
            json.getAsJsonObject("rewards").add("recipes", recipes);
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
            if (json.has(key) && json.get(key).isJsonPrimitive() && TARGETS.containsKey(json.get(key).getAsString())) { json.addProperty(key, TARGETS.get(json.get(key).getAsString())); }
        }
        String condition = json.has("condition") && json.get("condition").isJsonPrimitive() ? json.get("condition").getAsString() : "";
        if (("minecraft:time_check".equals(condition) || "time_check".equals(condition)) && !json.has("clock")) { json.addProperty("clock", ModernFixes.MINECRAFT + "overworld"); }
        if (json.has("recipe") && json.get("recipe").isJsonPrimitive()) { json.addProperty("recipe", ModernFixes.recipe(json.get("recipe").getAsString())); }
        String function = json.has("function") && json.get("function").isJsonPrimitive() ? json.get("function").getAsString() : "";
        if (!function.isEmpty()) { function(json, function, stacks); }
        else if (stacks && json.has(COMPONENTS) && json.get(COMPONENTS).isJsonObject()) { json.add(COMPONENTS, ModernFixes.components(ModernFixes.firstItem(json.get("items")), json.getAsJsonObject(COMPONENTS))); }
        if (json.has(TYPE_SPECIFIC) && json.get(TYPE_SPECIFIC).isJsonObject()) { variant(json, file, note); }
        json.entrySet().forEach(entry -> walk(entry.getValue(), file, stacks, note));
    }

    private static void function(JsonObject json, String function, boolean stacks) {
        switch (function.startsWith(ModernFixes.MINECRAFT) ? function.substring(ModernFixes.MINECRAFT.length()) : function) {
            case "set_components" -> {
                if (stacks && json.has(COMPONENTS) && json.get(COMPONENTS).isJsonObject()) { json.add(COMPONENTS, ModernFixes.components("minecraft:stone", json.getAsJsonObject(COMPONENTS))); }
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
        String type = specific.has("type") && specific.get("type").isJsonPrimitive() ? specific.get("type").getAsString() : "";
        String bare = type.startsWith(ModernFixes.MINECRAFT) ? type.substring(ModernFixes.MINECRAFT.length()) : type;
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
        JsonObject components = json.has(COMPONENTS) && json.get(COMPONENTS).isJsonObject() ? json.getAsJsonObject(COMPONENTS) : new JsonObject();
        components.add(ModernFixes.MINECRAFT + component, variant);
        json.add(COMPONENTS, components);
    }
}
