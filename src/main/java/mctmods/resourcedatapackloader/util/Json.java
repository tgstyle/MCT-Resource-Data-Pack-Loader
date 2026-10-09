package mctmods.resourcedatapackloader.util;

import mctmods.resourcedatapackloader.pack.PackManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;

public final class Json {
    public static final Gson GSON = new GsonBuilder().create();
    public static final Gson PRETTY = new GsonBuilder().setPrettyPrinting().create();

    private Json() {}

    public static JsonObject object(JsonObject parent, String key) { return parent.has(key) && parent.get(key).isJsonObject() ? parent.getAsJsonObject(key) : new JsonObject(); }

    @Nullable public static JsonObject object(ResourceLocation key, String contents, String kind) {
        JsonObject json = JsonUtils.gsonDeserialize(GSON, contents, JsonObject.class);
        if (json == null) { ContentLog.LOGGER.error("{} {} is empty, ignoring it", label(kind), key); }
        return json;
    }

    private static String label(String kind) { return Character.toUpperCase(kind.charAt(0)) + kind.substring(1); }

    public static String text(JsonObject object, String key) { return object.has(key) && object.get(key).isJsonPrimitive() ? object.get(key).getAsString() : ""; }

    public static String text(JsonObject object, String key, String fallback) { return object.has(key) && object.get(key).isJsonPrimitive() ? object.get(key).getAsString().trim() : fallback; }

    public static int number(JsonObject object, String key, int fallback) {
        try { return object.has(key) && object.get(key).isJsonPrimitive() ? object.get(key).getAsInt() : fallback; }
        catch (NumberFormatException notNumber) { return fallback; }
    }

    public static boolean flag(JsonObject object, String key) { return object.has(key) && object.get(key).isJsonPrimitive() && object.get(key).getAsBoolean(); }

    public static List<String> strings(JsonObject json, String member) {
        if (!json.has(member)) { return Collections.emptyList(); }
        JsonArray array = JsonUtils.getJsonArray(json, member);
        List<String> values = new ArrayList<>(array.size());
        for (JsonElement element : array) {
            String value = element.getAsString();
            if (!value.isEmpty()) { values.add(value); }
        }
        return Collections.unmodifiableList(values);
    }

    public static List<String> names(JsonObject json, String field) {
        List<String> found = new ArrayList<>();
        if (!json.has(field) || !json.get(field).isJsonArray()) { return found; }
        for (JsonElement entry : json.getAsJsonArray(field)) {
            String named = entry.getAsString().trim();
            if (!named.isEmpty()) { found.add(named); }
        }
        return found;
    }

    public static float ranged(JsonObject json, String member, float fallback, float low, float high, Object owner) {
        if (!json.has(member)) { return fallback; }
        float value = JsonUtils.getFloat(json, member, fallback);
        if (value >= low && value <= high) { return value; }
        ContentLog.LOGGER.error("{} sets {} to {}, which is outside {} to {}, so the default is used instead", owner, member, value, low, high);
        return fallback;
    }

    public static float bounded(JsonObject json, String member, float low, float high, Object owner) {
        if (!json.has(member)) { return Float.NaN; }
        float value = JsonUtils.getFloat(json, member, Float.NaN);
        if (value >= low && value <= high) { return value; }
        ContentLog.LOGGER.error("{} sets {} to {}, which is outside {} to {}, so the world setting is used instead", owner, member, value, low, high);
        return Float.NaN;
    }

    public static String choice(JsonObject json, String member, String fallback, Object owner, Collection<String> options) {
        if (!json.has(member)) { return fallback; }
        String asked = JsonUtils.getString(json, member).trim();
        if (asked.isEmpty()) { return fallback; }
        for (String option : options) {
            if (option.equalsIgnoreCase(asked)) { return option; }
        }
        ContentLog.LOGGER.error("{} sets {} to '{}', which is not one of {}, so {} is used instead", owner, member, asked, options, fallback == null ? "the default" : "'" + fallback + "'");
        return fallback;
    }

    public static Map<String, String> map(JsonObject json, String member) {
        if (!json.has(member)) { return Collections.emptyMap(); }
        JsonObject object = JsonUtils.getJsonObject(json, member);
        Map<String, String> values = new LinkedHashMap<>();
        for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
            if (!entry.getValue().isJsonPrimitive()) { continue; }
            values.put(entry.getKey(), entry.getValue().getAsString());
        }
        return Collections.unmodifiableMap(values);
    }

    public static Set<String> paths(JsonObject json) {
        Set<String> found = new LinkedHashSet<>();
        paths(json, "", found);
        return Collections.unmodifiableSet(found);
    }

    private static void paths(JsonObject json, String prefix, Set<String> found) {
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            String path = prefix + entry.getKey();
            found.add(path);
            if (entry.getValue().isJsonObject()) { paths(entry.getValue().getAsJsonObject(), path + ".", found); }
        }
    }

    public static void eachFile(String type, String kind, BiConsumer<ResourceLocation, String> reader) {
        PackManager.get().forEach(type, PackManager.JSON, (namespace, path, contents) -> {
            ResourceLocation key = new ResourceLocation(namespace, path);
            try { reader.accept(key, contents); }
            catch (RuntimeException ex) { ContentLog.LOGGER.error("Parsing error in " + kind + " {}, ignoring it", key, ex); }
        });
    }

    public static void eachObject(String type, String kind, BiConsumer<ResourceLocation, JsonObject> reader) {
        String label = label(kind);
        eachFile(type, kind, (key, contents) -> {
            JsonObject json = GSON.fromJson(contents, JsonObject.class);
            if (json == null) {
                ContentLog.LOGGER.error("{} {} is empty, ignoring it", label, key);
                return;
            }
            reader.accept(key, json);
        });
    }
}
