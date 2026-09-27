package mctmods.resourcedatapackloader.pack.port;

import mctmods.resourcedatapackloader.util.GameData;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.resources.Identifier;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

final class ModernRecipes {
    private static final String TYPE = "type";
    private static final String CUSTOM_TYPE = "neoforge:ingredient_type";
    private static final String NEOFORGE = "neoforge:";
    private static final String COMPOUND = "neoforge:compound";
    private static final String CHILDREN = "children";
    private static final String COMPONENTS = "components";
    private static final String TEMPLATE_SUFFIX = "_armor_trim_smithing_template";
    private static final List<String> SINGLE = List.of("ingredient", "base", "addition", "template");
    private static final List<String> NESTED = List.of("base", "subtracted");
    private static final Set<String> GONE = Set.of("minecraft:crafting_special_armordye", "minecraft:crafting_special_mapcloning", "minecraft:crafting_special_tippedarrow", "minecraft:crafting_special_shulkerboxcoloring", "minecraft:crafting_special_suspiciousstew");

    private static final Map<String, String> SPECIAL = Map.of("minecraft:crafting_special_bookcloning", "book_cloning", "minecraft:crafting_special_firework_star", "firework_star", "minecraft:crafting_special_firework_star_fade", "firework_star_fade",
            "minecraft:crafting_special_firework_rocket", "firework_rocket", "minecraft:crafting_special_mapextending", "map_extending", "minecraft:crafting_special_repairitem", "repair_item", "minecraft:crafting_special_shielddecoration", "shield_decoration",
            "minecraft:crafting_decorated_pot", "decorated_pot");

    private ModernRecipes() {}

    static void recipe(JsonObject json, String file, boolean stacks, Consumer<String> note) {
        String named = json.has(TYPE) && json.get(TYPE).isJsonPrimitive() ? json.get(TYPE).getAsString() : "";
        String type = named.isEmpty() || named.indexOf(':') >= 0 ? named : ModernFixes.MINECRAFT + named;
        if (GONE.contains(type) || "minecraft:crafting_special_bannerduplicate".equals(type)) { note.accept("'" + file + "' is a " + type + " recipe, a type this version replaced with per-item recipes, so it no longer loads; remove or rewrite it by hand"); }
        if (SPECIAL.containsKey(type)) { special(json, type, file, note); }
        for (String key : SINGLE) {
            if (json.has(key)) { json.add(key, ingredient(json.get(key), file, stacks, note)); }
        }
        if (json.has("ingredients") && json.get("ingredients").isJsonArray()) {
            JsonArray out = new JsonArray();
            for (JsonElement one : json.getAsJsonArray("ingredients")) { out.add(ingredient(one, file, stacks, note)); }
            json.add("ingredients", out);
        }
        if (json.has("key") && json.get("key").isJsonObject()) {
            for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject("key").entrySet()) { entry.setValue(ingredient(entry.getValue(), file, stacks, note)); }
        }
        if (stacks && json.has("result") && json.get("result").isJsonObject()) { json.add("result", ModernFixes.stack(json.getAsJsonObject("result"))); }
        if ("minecraft:smithing_trim".equals(type) && !json.has("pattern")) { pattern(json, file, note); }
        ModernFixes.replace(json, ModernFixes.names(json).getAsJsonObject());
    }

    private static void special(JsonObject json, String type, String file, Consumer<String> note) {
        JsonObject vanilla = GameData.json(Identifier.fromNamespaceAndPath("minecraft", "recipe/" + SPECIAL.get(type) + ".json"));
        if (vanilla == null || !vanilla.has(TYPE) || !type.equals(vanilla.get(TYPE).getAsString())) {
            note.accept("'" + file + "' is a " + type + " recipe, which now names its ingredients, and the game's own could not be read; it is served as written");
            return;
        }
        for (Map.Entry<String, JsonElement> entry : vanilla.entrySet()) {
            if (!json.has(entry.getKey())) { json.add(entry.getKey(), entry.getValue().deepCopy()); }
        }
    }

    private static void pattern(JsonObject json, String file, Consumer<String> note) {
        JsonElement template = json.get("template");
        String named = template != null && template.isJsonPrimitive() ? template.getAsString() : "";
        if (!named.startsWith("#") && named.endsWith(TEMPLATE_SUFFIX)) {
            json.addProperty("pattern", named.substring(0, named.length() - TEMPLATE_SUFFIX.length()));
            return;
        }
        note.accept("'" + file + "' is a smithing trim recipe, which now names its trim pattern, and its template does not show which; add \"pattern\" by hand");
    }

    static JsonElement ingredient(JsonElement held, String file, boolean stacks, Consumer<String> note) {
        if (held.isJsonArray()) { return alternatives(held.getAsJsonArray(), file, stacks, note); }
        if (!held.isJsonObject()) { return held; }
        JsonObject object = held.getAsJsonObject();
        String type = object.has(TYPE) && object.get(TYPE).isJsonPrimitive() ? object.get(TYPE).getAsString() : "";
        if (type.startsWith(NEOFORGE)) { return custom(object, type, file, stacks, note); }
        if (object.has("item") && object.get("item").isJsonPrimitive()) { return new JsonPrimitive(object.get("item").getAsString()); }
        if (object.has("tag") && object.get("tag").isJsonPrimitive()) { return new JsonPrimitive("#" + object.get("tag").getAsString()); }
        return held;
    }

    private static JsonElement alternatives(JsonArray given, String file, boolean stacks, Consumer<String> note) {
        JsonArray items = new JsonArray();
        JsonArray children = new JsonArray();
        for (JsonElement one : given) {
            JsonElement converted = ingredient(one, file, stacks, note);
            children.add(converted);
            if (converted.isJsonPrimitive() && !converted.getAsString().startsWith("#")) { items.add(converted); }
        }
        if (items.size() == children.size()) { return items.size() == 1 ? items.get(0) : items; }
        if (children.size() == 1) { return children.get(0); }
        JsonObject compound = new JsonObject();
        compound.addProperty(CUSTOM_TYPE, COMPOUND);
        compound.add(CHILDREN, children);
        return compound;
    }

    private static JsonElement custom(JsonObject object, String type, String file, boolean stacks, Consumer<String> note) {
        JsonObject out = new JsonObject();
        out.addProperty(CUSTOM_TYPE, type);
        for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
            String key = entry.getKey();
            JsonElement value = entry.getValue();
            if (TYPE.equals(key)) { continue; }
            if (CHILDREN.equals(key) && value.isJsonArray()) {
                JsonArray children = new JsonArray();
                for (JsonElement child : value.getAsJsonArray()) { children.add(ingredient(child, file, stacks, note)); }
                out.add(key, children);
            }
            else if (NESTED.contains(key)) { out.add(key, ingredient(value, file, stacks, note)); }
            else if (COMPONENTS.equals(key) && value.isJsonObject() && stacks) { out.add(key, ModernFixes.components(ModernFixes.firstItem(object.get("items")), value.getAsJsonObject())); }
            else { out.add(key, value); }
        }
        return out;
    }
}
