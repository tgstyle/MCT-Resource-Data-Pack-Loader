package mctmods.resourcedatapackloader.common.pack.io.port.down;

import mctmods.resourcedatapackloader.core.util.IdNames;
import mctmods.resourcedatapackloader.core.util.json.Json;
import mctmods.resourcedatapackloader.core.util.json.JsonTree;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.util.JsonUtils;

final class DownRecipes {
    private static final String TYPE = "type";
    private static final String CUSTOM_TYPE = "neoforge:ingredient_type";
    private static final String COMPONENTS = "components";
    private static final List<String> SINGLE = Arrays.asList("ingredient", "base", "addition", "template");
    private static final List<String> NESTED = Arrays.asList("children", "base", "subtracted");

    private DownRecipes() {}

    static void recipe(JsonObject json, Consumer<String> note) {
        if (!json.has(TYPE)) { return; }
        if ("minecraft:smithing_trim".equals(Json.id(json, TYPE))) { json.remove("pattern"); }
        for (String key : SINGLE) {
            if (json.has(key)) { json.add(key, ingredient(json.get(key), note)); }
        }
        if (JsonUtils.isJsonArray(json, "ingredients")) {
            JsonArray out = new JsonArray();
            for (JsonElement one : json.getAsJsonArray("ingredients")) { out.add(ingredient(one, note)); }
            json.add("ingredients", out);
        }
        JsonObject keys = Json.objectOrNull(json, "key");
        if (keys != null) {
            for (Map.Entry<String, JsonElement> entry : keys.entrySet()) { entry.setValue(ingredient(entry.getValue(), note)); }
        }
        JsonObject result = Json.objectOrNull(json, "result");
        if (result != null) { DownFixes.stack(result, note); }
        DownFixes.rename(json);
    }

    private static JsonElement ingredient(JsonElement held, Consumer<String> note) {
        if (held.isJsonArray()) {
            JsonArray out = new JsonArray();
            for (JsonElement one : held.getAsJsonArray()) { out.add(ingredient(one, note)); }
            return out;
        }
        if (held.isJsonPrimitive()) {
            String named = held.getAsString();
            JsonObject out = new JsonObject();
            if (IdNames.isTag(named)) { out.addProperty("tag", IdNames.tagPath(named)); }
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
            else if (COMPONENTS.equals(key) && value.isJsonObject()) {
                JsonObject components = JsonTree.copy(value.getAsJsonObject());
                DownFixes.components(components, note);
                out.add(key, components);
            }
            else { out.add(key, value); }
        }
        return out;
    }
}
