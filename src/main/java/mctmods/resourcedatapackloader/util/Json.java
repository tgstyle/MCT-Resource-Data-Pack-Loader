package mctmods.resourcedatapackloader.util;

import mctmods.resourcedatapackloader.pack.PackManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
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
import java.util.function.Supplier;
import javax.annotation.Nullable;

public final class Json {
    public static final Gson GSON = new GsonBuilder().create();
    public static final Gson PRETTY = new GsonBuilder().setPrettyPrinting().create();

    private static final String NO_CONTEXT = "pack content";
    private static final ThreadLocal<String> WHERE = new ThreadLocal<>();

    private Json() {}

    public static <T> T within(String kind, Object file, Supplier<T> body) {
        String previous = WHERE.get();
        WHERE.set(kind + " " + file);
        try { return body.get(); }
        finally {
            if (previous == null) { WHERE.remove(); }
            else { WHERE.set(previous); }
        }
    }

    private static String where() {
        String held = WHERE.get();
        return held == null ? NO_CONTEXT : held;
    }

    private static <T> T tolerant(JsonObject object, String key, T fallback, Supplier<T> read) {
        if (!object.has(key)) { return fallback; }
        try { return read.get(); }
        catch (RuntimeException bad) {
            ContentLog.LOGGER.error("{}: key '{}' has bad value {}, using {}", where(), key, object.get(key), fallback);
            return fallback;
        }
    }

    public static int getInt(JsonObject object, String key, int fallback) { return tolerant(object, key, fallback, () -> JsonUtils.getInt(object, key, fallback)); }

    public static float getFloat(JsonObject object, String key, float fallback) { return tolerant(object, key, fallback, () -> JsonUtils.getFloat(object, key, fallback)); }

    public static String getString(JsonObject object, String key, String fallback) { return tolerant(object, key, fallback, () -> JsonUtils.getString(object, key, fallback)); }

    public static boolean getBoolean(JsonObject object, String key, boolean fallback) { return tolerant(object, key, fallback, () -> JsonUtils.getBoolean(object, key, fallback)); }

    public static JsonObject getJsonObject(JsonObject object, String key, JsonObject fallback) { return tolerant(object, key, fallback, () -> JsonUtils.getJsonObject(object, key, fallback)); }

    public static JsonArray getJsonArray(JsonObject object, String key, JsonArray fallback) { return tolerant(object, key, fallback, () -> JsonUtils.getJsonArray(object, key, fallback)); }

    public static int getInt(JsonElement element, String key) { return JsonUtils.getInt(element, key); }

    public static int getInt(JsonObject object, String key) { return JsonUtils.getInt(object, key); }

    public static float getFloat(JsonElement element, String key) { return JsonUtils.getFloat(element, key); }

    public static float getFloat(JsonObject object, String key) { return JsonUtils.getFloat(object, key); }

    public static String getString(JsonElement element, String key) { return JsonUtils.getString(element, key); }

    public static String getString(JsonObject object, String key) { return JsonUtils.getString(object, key); }

    public static boolean getBoolean(JsonElement element, String key) { return JsonUtils.getBoolean(element, key); }

    public static boolean getBoolean(JsonObject object, String key) { return JsonUtils.getBoolean(object, key); }

    public static JsonObject getJsonObject(JsonElement element, String key) { return JsonUtils.getJsonObject(element, key); }

    public static JsonObject getJsonObject(JsonObject object, String key) { return JsonUtils.getJsonObject(object, key); }

    public static JsonArray getJsonArray(JsonElement element, String key) { return JsonUtils.getJsonArray(element, key); }

    public static JsonArray getJsonArray(JsonObject object, String key) { return JsonUtils.getJsonArray(object, key); }

    private static <T> T element(JsonElement element, String key, T fallback, Supplier<T> read) {
        try { return read.get(); }
        catch (RuntimeException bad) {
            ContentLog.LOGGER.error("{}: key '{}' has bad value {}, using {}", where(), key, element, fallback);
            return fallback;
        }
    }

    public static int asInt(JsonElement element, String key, int fallback) { return element(element, key, fallback, element::getAsInt); }

    public static float asFloat(JsonElement element, String key, float fallback) { return element(element, key, fallback, element::getAsFloat); }

    public static double asDouble(JsonElement element, String key, double fallback) { return element(element, key, fallback, element::getAsDouble); }

    public static String asString(JsonElement element, String key, String fallback) { return element(element, key, fallback, element::getAsString); }

    public static List<Integer> ints(JsonArray array, String key) {
        List<Integer> values = new ArrayList<>(array.size());
        for (JsonElement entry : array) {
            if (unusable(entry, key)) { continue; }
            try { values.add(entry.getAsInt()); }
            catch (RuntimeException bad) { ContentLog.LOGGER.error("{}: key '{}' has bad value {}, using no entry", where(), key, entry); }
        }
        return values;
    }

    public static List<String> texts(JsonArray array, String key) {
        List<String> values = new ArrayList<>(array.size());
        for (JsonElement entry : array) {
            if (!unusable(entry, key)) { values.add(entry.getAsString()); }
        }
        return values;
    }

    private static boolean unusable(JsonElement element, String member) {
        if (element.isJsonPrimitive()) { return false; }
        ContentLog.LOGGER.error("{}: key '{}' has bad value {}, using no entry", where(), member, element);
        return true;
    }

    public static JsonObject object(JsonObject parent, String key) { return parent.has(key) && parent.get(key).isJsonObject() ? parent.getAsJsonObject(key) : new JsonObject(); }

    @Nullable public static JsonObject object(ResourceLocation key, String contents, String kind) {
        JsonObject json = GSON.fromJson(contents, JsonObject.class);
        if (json == null) { ContentLog.LOGGER.error("{} {} is empty, ignoring it", label(kind), key); }
        return json;
    }

    private static String label(String kind) { return Character.toUpperCase(kind.charAt(0)) + kind.substring(1); }

    public static String text(JsonObject object, String key) { return JsonUtils.isJsonPrimitive(object, key) ? object.get(key).getAsString() : ""; }

    public static String text(JsonObject object, String key, String fallback) { return JsonUtils.isJsonPrimitive(object, key) ? object.get(key).getAsString().trim() : fallback; }

    public static int number(JsonObject object, String key, int fallback) {
        try { return JsonUtils.isJsonPrimitive(object, key) ? object.get(key).getAsInt() : fallback; }
        catch (NumberFormatException notNumber) { return fallback; }
    }

    public static boolean flag(JsonObject object, String key) { return JsonUtils.isJsonPrimitive(object, key) && object.get(key).getAsBoolean(); }

    public static List<String> strings(JsonObject json, String member) {
        if (!json.has(member)) { return Collections.emptyList(); }
        JsonArray array = getJsonArray(json, member, new JsonArray());
        List<String> values = new ArrayList<>(array.size());
        for (JsonElement element : array) {
            if (unusable(element, member)) { continue; }
            String value = element.getAsString();
            if (!value.isEmpty()) { values.add(value); }
        }
        return Collections.unmodifiableList(values);
    }

    public static List<String> names(JsonObject json, String field) {
        List<String> found = new ArrayList<>();
        if (!JsonUtils.isJsonArray(json, field)) { return found; }
        for (JsonElement entry : json.getAsJsonArray(field)) {
            if (unusable(entry, field)) { continue; }
            String named = entry.getAsString().trim();
            if (!named.isEmpty()) { found.add(named); }
        }
        return found;
    }

    public static float ranged(JsonObject json, String member, float fallback, float low, float high, Object owner) {
        if (!json.has(member)) { return fallback; }
        float value = getFloat(json, member, fallback);
        if (value >= low && value <= high) { return value; }
        ContentLog.LOGGER.error("{} sets {} to {}, which is outside {} to {}, so the default is used instead", owner, member, value, low, high);
        return fallback;
    }

    public static float bounded(JsonObject json, String member, float low, float high, Object owner) {
        if (!json.has(member)) { return Float.NaN; }
        float value = getFloat(json, member, Float.NaN);
        if (Float.isNaN(value)) { return Float.NaN; }
        if (value >= low && value <= high) { return value; }
        ContentLog.LOGGER.error("{} sets {} to {}, which is outside {} to {}, so the world setting is used instead", owner, member, value, low, high);
        return Float.NaN;
    }

    public static String choice(JsonObject json, String member, String fallback, Object owner, Collection<String> options) {
        if (!json.has(member)) { return fallback; }
        String asked = getString(json, member, "").trim();
        if (asked.isEmpty()) { return fallback; }
        for (String option : options) {
            if (option.equalsIgnoreCase(asked)) { return option; }
        }
        ContentLog.LOGGER.error("{} sets {} to '{}', which is not one of {}, so {} is used instead", owner, member, asked, options, fallback == null ? "the default" : "'" + fallback + "'");
        return fallback;
    }

    public static Map<String, String> map(JsonObject json, String member) {
        if (!json.has(member)) { return Collections.emptyMap(); }
        JsonObject object = getJsonObject(json, member, new JsonObject());
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
            try {
                within(kind, key, () -> {
                    reader.accept(key, contents);
                    return null;
                });
            }
            catch (JsonParseException ex) { ContentLog.LOGGER.error("Parsing error in {} {}, ignoring it: {}", kind, key, ex.getMessage()); }
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
