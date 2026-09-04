package mctmods.resourcedatapackloader.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class Json {
    private Json() {}

    public static List<String> strings(JsonObject json, String member) {
        if (!json.has(member)) { return Collections.emptyList(); }
        JsonArray array = GsonHelper.getAsJsonArray(json, member);
        List<String> values = new ArrayList<>(array.size());
        for (JsonElement element : array) {
            String value = element.getAsString();
            if (!value.isEmpty()) { values.add(value); }
        }
        return Collections.unmodifiableList(values);
    }

    public static float bounded(JsonObject json, String member, float low, float high, Object owner) {
        if (!json.has(member)) { return Float.NaN; }
        float value = GsonHelper.getAsFloat(json, member, Float.NaN);
        if (value >= low && value <= high) { return value; }
        ContentLog.LOGGER.error("{} sets {} to {}, which is outside {} to {}, so the world setting is used instead", owner, member, value, low, high);
        return Float.NaN;
    }

    public static Map<String, String> map(JsonObject json, String member) {
        if (!json.has(member)) { return Collections.emptyMap(); }
        JsonObject object = GsonHelper.getAsJsonObject(json, member);
        Map<String, String> values = new LinkedHashMap<>();
        for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
            if (!entry.getValue().isJsonPrimitive()) { continue; }
            values.put(entry.getKey(), entry.getValue().getAsString());
        }
        return Collections.unmodifiableMap(values);
    }
}
