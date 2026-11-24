package mctmods.resourcedatapackloader.common.pack.io.port.down;

import mctmods.resourcedatapackloader.common.pack.io.port.Port;
import mctmods.resourcedatapackloader.common.pack.io.port.cross.CrossJson;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.util.GsonHelper;

final class DownRecipes {
    private static final String TYPE = "type";
    private static final String CUSTOM_TYPE = "neoforge:ingredient_type";
    private static final List<String> SINGLE = List.of("ingredient", "base", "addition", "template");
    private static final List<String> NESTED = List.of("children", "base", "subtracted");

    private DownRecipes() {}

    static void recipe(JsonObject json, Consumer<String> note) {
        if (!json.has(TYPE)) { return; }
        String type = Json.id(json, TYPE);
        if (DownFixes.unknownRecipe(type)) {
            dropped(json);
            note.accept("is a " + type + " recipe, a type " + Port.Line.running().title() + " does not have, so it is left out");
            return;
        }
        if ("minecraft:smithing_trim".equals(type)) { json.remove("pattern"); }
        for (String key : SINGLE) {
            if (json.has(key)) { json.add(key, ingredient(json.get(key), note)); }
        }
        if (GsonHelper.isArrayNode(json, "ingredients")) {
            JsonArray out = new JsonArray();
            for (JsonElement one : json.getAsJsonArray("ingredients")) { out.add(ingredient(one, note)); }
            json.add("ingredients", out);
        }
        if (GsonHelper.isObjectNode(json, "key")) {
            for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject("key").entrySet()) { entry.setValue(ingredient(entry.getValue(), note)); }
        }
        if (GsonHelper.isObjectNode(json, "result")) { DownFixes.stack(json.getAsJsonObject("result"), note); }
        DownFixes.rename(json);
    }

    private static void dropped(JsonObject json) {
        JsonObject never = new JsonObject();
        never.addProperty(TYPE, "neoforge:false");
        JsonArray conditions = new JsonArray();
        conditions.add(never);
        JsonObject out = new JsonObject();
        out.add("neoforge:conditions", conditions);
        out.add(TYPE, json.get(TYPE));
        CrossJson.replace(json, out);
    }

    static JsonElement ingredient(JsonElement held, Consumer<String> note) {
        if (held.isJsonArray()) {
            JsonArray out = new JsonArray();
            for (JsonElement one : held.getAsJsonArray()) { out.add(ingredient(one, note)); }
            return out;
        }
        if (held.isJsonPrimitive()) {
            String named = held.getAsString();
            JsonObject out = new JsonObject();
            if (named.startsWith("#")) { out.addProperty("tag", named.substring(1)); }
            else { out.addProperty("item", named); }
            return out;
        }
        if (!held.isJsonObject() || !held.getAsJsonObject().has(CUSTOM_TYPE)) { return held; }
        JsonObject object = held.getAsJsonObject();
        JsonObject out = new JsonObject();
        out.add(TYPE, object.get(CUSTOM_TYPE));
        for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
            String key = entry.getKey();
            JsonElement value = entry.getValue();
            if (CUSTOM_TYPE.equals(key)) { continue; }
            if (NESTED.contains(key)) { out.add(key, ingredient(value, note)); }
            else if (CrossJson.COMPONENTS.equals(key) && value.isJsonObject()) {
                JsonObject components = value.getAsJsonObject().deepCopy();
                DownFixes.components(components, note);
                out.add(key, components);
            }
            else { out.add(key, value); }
        }
        return out;
    }
}
