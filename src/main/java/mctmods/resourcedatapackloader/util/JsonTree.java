package mctmods.resourcedatapackloader.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;

public final class JsonTree {
    private JsonTree() {}

    public static JsonArray map(JsonArray held, UnaryOperator<JsonElement> itemFn) {
        JsonArray out = new JsonArray();
        for (JsonElement item : held) { out.add(itemFn.apply(item)); }
        return out;
    }

    public static JsonObject map(JsonObject held, BiFunction<String, JsonElement, JsonElement> entryFn) {
        JsonObject out = new JsonObject();
        for (Map.Entry<String, JsonElement> entry : held.entrySet()) {
            JsonElement value = entryFn.apply(entry.getKey(), entry.getValue());
            if (value != null) { out.add(entry.getKey(), value); }
        }
        return out;
    }
}
