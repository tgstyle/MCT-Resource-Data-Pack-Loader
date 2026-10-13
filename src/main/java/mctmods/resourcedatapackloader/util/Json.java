package mctmods.resourcedatapackloader.util;

import mctmods.resourcedatapackloader.pack.PackManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
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

    public static int getInt(JsonObject object, String key, int fallback) { return tolerant(object, key, fallback, () -> GsonHelper.getAsInt(object, key, fallback)); }

    public static float getFloat(JsonObject object, String key, float fallback) { return tolerant(object, key, fallback, () -> GsonHelper.getAsFloat(object, key, fallback)); }

    public static double getDouble(JsonObject object, String key, double fallback) { return tolerant(object, key, fallback, () -> GsonHelper.getAsDouble(object, key, fallback)); }

    public static long getLong(JsonObject object, String key, long fallback) { return tolerant(object, key, fallback, () -> GsonHelper.getAsLong(object, key, fallback)); }

    public static String getString(JsonObject object, String key, String fallback) { return tolerant(object, key, fallback, () -> GsonHelper.getAsString(object, key, fallback)); }

    public static boolean getBoolean(JsonObject object, String key, boolean fallback) { return tolerant(object, key, fallback, () -> GsonHelper.getAsBoolean(object, key, fallback)); }

    public static JsonObject getJsonObject(JsonObject object, String key, JsonObject fallback) { return tolerant(object, key, fallback, () -> GsonHelper.getAsJsonObject(object, key, fallback)); }

    public static JsonArray getJsonArray(JsonObject object, String key, JsonArray fallback) { return tolerant(object, key, fallback, () -> GsonHelper.getAsJsonArray(object, key, fallback)); }

    public static int getInt(JsonElement element, String key) { return GsonHelper.convertToInt(element, key); }

    public static int getInt(JsonObject object, String key) { return GsonHelper.getAsInt(object, key); }

    public static float getFloat(JsonElement element, String key) { return GsonHelper.convertToFloat(element, key); }

    public static float getFloat(JsonObject object, String key) { return GsonHelper.getAsFloat(object, key); }

    public static String getString(JsonElement element, String key) { return GsonHelper.convertToString(element, key); }

    public static String getString(JsonObject object, String key) { return GsonHelper.getAsString(object, key); }

    public static boolean getBoolean(JsonElement element, String key) { return GsonHelper.convertToBoolean(element, key); }

    public static boolean getBoolean(JsonObject object, String key) { return GsonHelper.getAsBoolean(object, key); }

    public static JsonObject getJsonObject(JsonElement element, String key) { return GsonHelper.convertToJsonObject(element, key); }

    public static JsonObject getJsonObject(JsonObject object, String key) { return GsonHelper.getAsJsonObject(object, key); }

    public static JsonArray getJsonArray(JsonElement element, String key) { return GsonHelper.convertToJsonArray(element, key); }

    public static JsonArray getJsonArray(JsonObject object, String key) { return GsonHelper.getAsJsonArray(object, key); }

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

    public static JsonObject object(JsonObject parent, String key) { return GsonHelper.isObjectNode(parent, key) ? parent.getAsJsonObject(key) : new JsonObject(); }

    public static JsonObject obj(Object... pairs) {
        JsonObject json = new JsonObject();
        for (int i = 0; i + 1 < pairs.length; i += 2) { json.add(String.valueOf(pairs[i]), element(pairs[i + 1])); }
        return json;
    }

    public static JsonArray arr(Object... values) {
        JsonArray array = new JsonArray();
        for (Object value : values) { array.add(element(value)); }
        return array;
    }

    private static JsonElement element(Object value) {
        if (value instanceof JsonElement held) { return held; }
        if (value instanceof Boolean held) { return new JsonPrimitive(held); }
        if (value instanceof Number held) { return new JsonPrimitive(held); }
        if (value instanceof List<?> held) { return arr(held.toArray()); }
        return new JsonPrimitive(String.valueOf(value));
    }

    @Nullable public static JsonObject object(ResourceLocation key, String contents, String kind) {
        JsonObject json = GSON.fromJson(contents, JsonObject.class);
        if (json == null) { ContentLog.LOGGER.error("{} {} is empty, ignoring it", label(kind), key); }
        return json;
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

    private static String label(String kind) { return Character.toUpperCase(kind.charAt(0)) + kind.substring(1); }

    public static String text(JsonObject object, String key) { return GsonHelper.isValidPrimitive(object, key) ? object.get(key).getAsString() : ""; }

    public static String text(JsonObject object, String key, String fallback) { return GsonHelper.isValidPrimitive(object, key) ? object.get(key).getAsString().trim() : fallback; }

    public static int number(JsonObject object, String key, int fallback) {
        try { return GsonHelper.isValidPrimitive(object, key) ? object.get(key).getAsInt() : fallback; }
        catch (NumberFormatException notNumber) { return fallback; }
    }

    public static boolean flag(JsonObject object, String key) { return GsonHelper.isValidPrimitive(object, key) && object.get(key).getAsBoolean(); }

    public static float ranged(JsonObject json, String member, float fallback, float low, float high, Object owner) {
        if (!json.has(member)) { return fallback; }
        float value = getFloat(json, member, fallback);
        if (value >= low && value <= high) { return value; }
        ContentLog.LOGGER.error("{} sets {} to {}, which is outside {} to {}, so the default is used instead", owner, member, value, low, high);
        return fallback;
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

    public static List<String> strings(JsonObject json, String member) {
        List<String> out = new ArrayList<>();
        if (!json.has(member)) { return out; }
        JsonElement held = json.get(member);
        if (held.isJsonPrimitive()) {
            if (!held.getAsString().isEmpty()) { out.add(held.getAsString()); }
            return out;
        }
        if (!held.isJsonArray()) {
            unusable(held, member);
            return out;
        }
        for (JsonElement element : held.getAsJsonArray()) {
            if (element.isJsonPrimitive()) { out.add(element.getAsString()); }
            else if (element.isJsonObject() && GsonHelper.isValidPrimitive(element.getAsJsonObject(), "block")) { out.add(element.getAsJsonObject().get("block").getAsString()); }
            else { unusable(element, member); }
        }
        out.removeIf(String::isEmpty);
        return out;
    }

    public static List<String> names(JsonObject json, String field) {
        List<String> found = new ArrayList<>();
        for (String entry : strings(json, field)) {
            String named = entry.trim();
            if (!named.isEmpty()) { found.add(named); }
        }
        return found;
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

    public static void eachFile(String folder, String kind, BiConsumer<ResourceLocation, String> reader) {
        PackManager.get().forEach(folder, PackManager.JSON, (namespace, path, contents) -> {
            ResourceLocation key = ResourceLocation.fromNamespaceAndPath(namespace, path);
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

    public static void eachObject(String folder, String kind, BiConsumer<ResourceLocation, JsonObject> reader) {
        eachFile(folder, kind, (key, contents) -> {
            JsonObject json = object(key, contents, kind);
            if (json != null) { reader.accept(key, json); }
        });
    }
}
