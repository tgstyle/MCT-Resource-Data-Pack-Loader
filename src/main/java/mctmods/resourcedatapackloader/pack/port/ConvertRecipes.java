package mctmods.resourcedatapackloader.pack.port;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

final class ConvertRecipes {
    private static final Set<String> TWINNED_RECIPES = Convert.set("minecraft:crafting_shaped", "minecraft:crafting_shapeless", "forge:ore_shaped", "forge:ore_shapeless", "minecraft:smelting");

    private ConvertRecipes() {}

    static boolean noTwinRecipe(String type) { return type.startsWith(Ids.MINECRAFT + ":") && !TWINNED_RECIPES.contains(type); }

    static String recipe(JsonObject json, String from, Ported pack) {
        conditions(json);
        boolean ores = false;
        if (json.has("key") && json.get("key").isJsonObject()) {
            for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject("key").entrySet()) {
                entry.setValue(ingredient(entry.getValue(), from, pack));
                ores |= usesOre(entry.getValue());
            }
        }
        if (json.has("ingredients") && json.get("ingredients").isJsonArray()) {
            JsonArray ingredients = json.getAsJsonArray("ingredients");
            for (int i = 0; i < ingredients.size(); i++) {
                ingredients.set(i, ingredient(ingredients.get(i), from, pack));
                ores |= usesOre(ingredients.get(i));
            }
        }
        if (json.has("result")) { json.add("result", stack(json.get("result"), from, pack)); }
        json.remove("category");
        json.remove("show_notification");
        String type = json.has("type") && json.get("type").isJsonPrimitive() ? Ids.namespaced(json.get("type").getAsString()) : "";
        if (ores && "minecraft:crafting_shaped".equals(type)) { json.addProperty("type", "forge:ore_shaped"); }
        if (ores && "minecraft:crafting_shapeless".equals(type)) { json.addProperty("type", "forge:ore_shapeless"); }
        return Ported.GSON.toJson(json);
    }

    private static void conditions(JsonObject json) {
        JsonElement held = json.has("neoforge:conditions") ? json.remove("neoforge:conditions") : json.has("forge:conditions") ? json.remove("forge:conditions") : json.get("conditions");
        if (held == null) { return; }
        condition(held);
        json.add("conditions", held);
    }

    private static void condition(JsonElement element) {
        if (element.isJsonArray()) {
            for (JsonElement inner : element.getAsJsonArray()) { condition(inner); }
            return;
        }
        if (!element.isJsonObject()) { return; }
        JsonObject held = element.getAsJsonObject();
        String type = Convert.text(held, "type");
        if (type.startsWith("neoforge:")) { type = "forge:" + type.substring("neoforge:".length()); }
        if ("forge:item_exists".equals(type)) { type = "minecraft:item_exists"; }
        if (!type.isEmpty()) { held.addProperty("type", type); }
        if (held.has("value")) { condition(held.get("value")); }
        if (held.has("values")) { condition(held.get("values")); }
    }

    private static boolean usesOre(JsonElement element) {
        if (element.isJsonArray()) {
            for (JsonElement inner : element.getAsJsonArray()) {
                if (usesOre(inner)) { return true; }
            }
            return false;
        }
        return element.isJsonObject() && "forge:ore_dict".equals(Convert.text(element.getAsJsonObject(), "type"));
    }

    private static JsonElement ingredient(JsonElement element, String from, Ported pack) {
        if (element.isJsonArray()) {
            JsonArray out = new JsonArray();
            for (JsonElement inner : element.getAsJsonArray()) { out.add(ingredient(inner, from, pack)); }
            return out;
        }
        if (element.isJsonPrimitive()) {
            String text = element.getAsString();
            JsonObject held = new JsonObject();
            if (text.startsWith("#")) { held.addProperty("tag", text.substring(1)); }
            else { held.addProperty("item", text); }
            return ingredient(held, from, pack);
        }
        if (!element.isJsonObject()) { return element; }
        JsonObject held = element.getAsJsonObject();
        String type = Convert.text(held, "type");
        if (type.endsWith(":compound") && held.has("children")) { return ingredient(held.get("children"), from, pack); }
        if (held.has("tag")) {
            String tag = held.get("tag").getAsString();
            String ore = Ids.oreName(tag);
            if (ore == null) { ore = Ids.camel(Ids.namespaced(tag).replace(':', '_').replace('/', '_'), false); }
            JsonObject out = new JsonObject();
            out.addProperty("type", "forge:ore_dict");
            out.addProperty("ore", ore);
            return out;
        }
        if (!type.isEmpty() && !type.endsWith(":item") && !type.endsWith(":nbt") && !type.endsWith(":components") && !type.endsWith(":partial_nbt")) { pack.note("'" + from + "' uses the ingredient type " + type + ", which has no twin on 1.12.2, so it is served as written"); }
        String named = held.has("item") ? held.get("item").getAsString() : held.has("items") && held.get("items").isJsonPrimitive() ? held.get("items").getAsString() : "";
        if (named.isEmpty()) { return held; }
        Convert.Ref ref = Convert.item(named, pack, "'" + from + "'");
        JsonObject out = new JsonObject();
        out.addProperty("item", ref == null ? named : ref.id);
        out.addProperty("data", ref == null ? 0 : ref.meta);
        if (held.has("nbt")) {
            out.addProperty("type", "minecraft:item_nbt");
            out.add("nbt", held.get("nbt"));
        }
        else if (held.has("components")) { pack.note("'" + from + "' asks for " + named + " with item components, which have no twin on 1.12.2, so any " + named + " is accepted"); }
        return out;
    }

    static JsonElement stack(JsonElement element, String from, Ported pack) {
        JsonObject held;
        if (element.isJsonPrimitive()) {
            held = new JsonObject();
            held.addProperty("item", element.getAsString());
        }
        else if (element.isJsonObject()) { held = element.getAsJsonObject(); }
        else { return element; }
        if (held.has("id") && !held.has("item")) { held.add("item", held.remove("id")); }
        if (!held.has("item")) { return held; }
        String named = held.get("item").getAsString();
        Convert.Ref ref = Convert.item(named, pack, "'" + from + "'");
        if (ref != null) {
            held.addProperty("item", ref.id);
            held.addProperty("data", ref.meta);
        }
        if (held.has("components")) {
            held.remove("components");
            pack.note("'" + from + "' gives " + named + " with item components, which have no twin on 1.12.2, so the plain item is given");
        }
        return held;
    }

    @Nullable static JsonObject smelting(JsonObject json, Ported pack) {
        String from = "a smelting recipe";
        JsonElement ingredient = json.get("ingredient");
        if (ingredient != null && ingredient.isJsonArray() && ingredient.getAsJsonArray().size() > 0) { ingredient = ingredient.getAsJsonArray().get(0); }
        if (ingredient == null || !ingredient.isJsonObject() || !ingredient.getAsJsonObject().has("item")) {
            pack.note("A smelting recipe smelts " + ingredient + ", which is not one item, and 1.12.2's furnace takes one item, so it is left out");
            return null;
        }
        JsonElement result = stack(json.get("result"), from, pack);
        if (!result.isJsonObject() || !result.getAsJsonObject().has("item")) { return null; }
        JsonObject out = new JsonObject();
        out.addProperty("input", Convert.itemString(ingredient.getAsJsonObject().get("item").getAsString(), pack, from));
        JsonObject made = result.getAsJsonObject();
        out.addProperty("output", made.get("item").getAsString() + ":" + (made.has("data") ? made.get("data").getAsInt() : 0));
        out.addProperty("count", made.has("count") ? made.get("count").getAsInt() : 1);
        if (json.has("experience")) { out.add("experience", json.get("experience")); }
        return out;
    }
}
