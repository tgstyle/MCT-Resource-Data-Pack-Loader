package mctmods.resourcedatapackloader.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class JsonTree {
    private JsonTree() {}

    public static JsonElement copy(JsonElement held) {
        if (held.isJsonObject()) { return copy(held.getAsJsonObject()); }
        if (!held.isJsonArray()) { return held; }
        JsonArray out = new JsonArray();
        for (JsonElement inner : held.getAsJsonArray()) { out.add(copy(inner)); }
        return out;
    }

    public static JsonObject copy(JsonObject held) {
        JsonObject out = new JsonObject();
        for (Map.Entry<String, JsonElement> entry : held.entrySet()) { out.add(entry.getKey(), copy(entry.getValue())); }
        return out;
    }

    public static void replace(JsonObject held, JsonObject with) {
        for (String key : keys(held)) { held.remove(key); }
        for (Map.Entry<String, JsonElement> entry : with.entrySet()) { held.add(entry.getKey(), entry.getValue()); }
    }

    public static List<String> keys(JsonObject held) {
        List<String> out = new ArrayList<>();
        for (Map.Entry<String, JsonElement> entry : held.entrySet()) { out.add(entry.getKey()); }
        return out;
    }
}
