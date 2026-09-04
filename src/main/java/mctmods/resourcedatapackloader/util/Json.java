package mctmods.resourcedatapackloader.util;

import mctmods.resourcedatapackloader.pack.PackManager;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

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

    public static void eachFile(String folder, String kind, BiConsumer<ResourceLocation, String> reader) {
        PackManager.get().forEach(folder, PackManager.JSON, (namespace, path, contents) -> {
            ResourceLocation key = ResourceLocation.fromNamespaceAndPath(namespace, path);
            try { reader.accept(key, contents); }
            catch (IllegalArgumentException | JsonParseException ex) { ContentLog.LOGGER.error("Parsing error in " + kind + " {}, ignoring it", key, ex); }
        });
    }
}
