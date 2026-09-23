package mctmods.resourcedatapackloader.pack.port;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

final class ConvertRecipes {
    private static final int WILDCARD_DATA = 32767;
    private static final String LEGACY_CONDITIONS = "conditions";
    private static final String CONDITIONS = "neoforge:conditions";
    private static final Map<String, String> CONDITION_TYPES = Map.of("minecraft:item_exists", "neoforge:item_exists", "forge:mod_loaded", "neoforge:mod_loaded", "forge:not", "neoforge:not", "forge:or", "neoforge:or", "forge:and", "neoforge:and", "forge:false", "neoforge:false");
    private static final String ORE_DICT = "forge:ore_dict";
    private static final String ITEM_TYPE = "minecraft:item";
    private static final String NBT_TYPE = "minecraft:item_nbt";

    private ConvertRecipes() {}

    public static String recipe(JsonObject json, String namespace, Ported pack) {
        if (json.has("type") && json.get("type").isJsonPrimitive()) {
            String type = json.get("type").getAsString();
            String mapped = switch (type) {
                case "forge:ore_shaped", "crafting_shaped" -> "minecraft:crafting_shaped";
                case "forge:ore_shapeless", "crafting_shapeless" -> "minecraft:crafting_shapeless";
                default -> type;
            };
            if (!mapped.equals(type)) {
                json.addProperty("type", mapped);
                pack.rewrote();
            }
        }
        if (json.has(LEGACY_CONDITIONS) && json.get(LEGACY_CONDITIONS).isJsonArray()) {
            JsonArray conditions = json.remove(LEGACY_CONDITIONS).getAsJsonArray();
            for (JsonElement condition : conditions) { condition(condition, namespace, pack); }
            json.add(CONDITIONS, conditions);
        }
        if (json.has("key") && json.get("key").isJsonObject()) {
            for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject("key").entrySet()) { entry.setValue(ingredient(entry.getValue(), namespace, pack)); }
        }
        if (json.has("ingredients") && json.get("ingredients").isJsonArray()) {
            JsonArray ingredients = json.getAsJsonArray("ingredients");
            for (int i = 0; i < ingredients.size(); i++) { ingredients.set(i, ingredient(ingredients.get(i), namespace, pack)); }
        }
        if (json.has("result") && json.get("result").isJsonObject()) {
            JsonObject result = json.getAsJsonObject("result");
            qualify(result, namespace, pack);
            stack(result, pack);
            if (result.has("item")) { result.add("id", result.remove("item")); }
        }
        return Ported.GSON.toJson(json);
    }

    private static void condition(JsonElement element, String namespace, Ported pack) {
        if (!element.isJsonObject()) { return; }
        JsonObject held = element.getAsJsonObject();
        String type = held.has("type") && held.get("type").isJsonPrimitive() ? held.get("type").getAsString() : "";
        String mapped = CONDITION_TYPES.getOrDefault(type, type);
        if (!mapped.equals(type)) {
            held.addProperty("type", mapped);
            pack.rewrote();
        }
        if (held.has("item") && held.get("item").isJsonPrimitive()) {
            qualify(held, namespace, pack);
            held.addProperty("item", Convert.itemName(held.get("item").getAsString(), pack));
        }
        if (held.has("value")) { condition(held.get("value"), namespace, pack); }
        if (held.has("values") && held.get("values").isJsonArray()) {
            for (JsonElement inner : held.getAsJsonArray("values")) { condition(inner, namespace, pack); }
        }
    }

    private static JsonElement ingredient(JsonElement element, String namespace, Ported pack) {
        if (element.isJsonArray()) {
            JsonArray out = new JsonArray();
            for (JsonElement inner : element.getAsJsonArray()) {
                JsonElement converted = ingredient(inner, namespace, pack);
                if (converted.isJsonArray()) { out.addAll(converted.getAsJsonArray()); }
                else { out.add(converted); }
            }
            return out;
        }
        if (!element.isJsonObject()) { return element; }
        JsonObject held = element.getAsJsonObject();
        String type = held.has("type") && held.get("type").isJsonPrimitive() ? held.get("type").getAsString() : "";
        if (ORE_DICT.equals(type) || ITEM_TYPE.equals(type)) {
            held.remove("type");
            pack.rewrote();
        }
        if (held.has("ore")) {
            held.addProperty("tag", Ids.oreDictTag(held.get("ore").getAsString()));
            held.remove("ore");
            pack.rewrote();
        }
        qualify(held, namespace, pack);
        if (NBT_TYPE.equals(type)) { return nbt(held, pack); }
        JsonArray wildcard = wildcard(held, pack);
        if (wildcard != null) { return wildcard; }
        stack(held, pack);
        return held;
    }

    private static void qualify(JsonObject held, String namespace, Ported pack) {
        if (!held.has("item") || !held.get("item").isJsonPrimitive()) { return; }
        String item = held.get("item").getAsString().trim();
        if (item.startsWith("#")) {
            pack.note("A recipe names the constant '" + item + "', which lived in a 1.12.2 mod's _constants.json and has no twin here; name the item or tag it stood for");
            return;
        }
        if (item.isEmpty() || item.indexOf(':') >= 0) { return; }
        held.addProperty("item", namespace + ":" + item);
        pack.rewrote();
    }

    private static JsonElement nbt(JsonObject held, Ported pack) {
        if (!held.has("item") || !held.get("item").isJsonPrimitive()) { return held; }
        String item = held.get("item").getAsString();
        int data = data(held);
        String nbt = !held.has("nbt") ? "{}" : held.get("nbt").isJsonObject() ? Ported.GSON.toJson(held.get("nbt")) : held.get("nbt").getAsString();
        boolean owned = pack.owns(item);
        JsonObject fixed = Ids.nbtIngredient(owned ? Convert.itemName(item + ":" + data, pack) : item, owned ? 0 : data, nbt);
        if (fixed == null) {
            pack.note("A recipe asks for " + item + " with the nbt " + nbt + ", which could not be carried; it is left as written");
            return held;
        }
        pack.rewrote();
        return fixed;
    }

    @Nullable private static JsonArray wildcard(JsonObject held, Ported pack) {
        if (data(held) != WILDCARD_DATA || !held.has("item") || !held.get("item").isJsonPrimitive()) { return null; }
        List<String> names = Convert.itemNames(held.get("item").getAsString() + Convert.WILDCARD, pack);
        if (names.size() < 2) { return null; }
        JsonArray out = new JsonArray();
        for (String name : names) {
            JsonObject one = new JsonObject();
            one.addProperty("item", name);
            out.add(one);
        }
        return out;
    }

    private static int data(JsonObject held) { return held.has("data") && held.get("data").isJsonPrimitive() && held.get("data").getAsJsonPrimitive().isNumber() ? held.get("data").getAsInt() : 0; }

    static void stack(JsonObject held, Ported pack) {
        if (!held.has("item") || !held.get("item").isJsonPrimitive()) { return; }
        String item = held.get("item").getAsString();
        int data = data(held);
        if (data == WILDCARD_DATA) { data = 0; }
        held.remove("data");
        held.addProperty("item", Convert.itemName(item + (data > 0 ? ":" + data : ""), pack));
    }

    public static Map<String, JsonObject> oreDict(JsonObject json, Ported pack) {
        Map<String, JsonObject> tags = new LinkedHashMap<>();
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            String ore = entry.getKey();
            if (ore.startsWith("_") || !entry.getValue().isJsonArray()) { continue; }
            boolean removing = ore.startsWith("-");
            JsonObject file = tags.computeIfAbsent(Ids.oreDictTag(removing ? ore.substring(1) : ore), tag -> tagFile());
            if (removing && !file.has("remove")) { file.add("remove", new JsonArray()); }
            for (JsonElement item : entry.getValue().getAsJsonArray()) {
                if (!item.isJsonPrimitive()) { continue; }
                String named = item.getAsString().trim();
                if (removing && "*".equals(named)) {
                    file.addProperty("replace", true);
                    file.add("values", new JsonArray());
                    continue;
                }
                for (String name : Convert.itemNames(named, pack)) { file.getAsJsonArray(removing ? "remove" : "values").add(name); }
            }
            pack.rewrote();
        }
        return tags;
    }

    private static JsonObject tagFile() {
        JsonObject file = new JsonObject();
        file.addProperty("replace", false);
        file.add("values", new JsonArray());
        return file;
    }
}
