package mctmods.resourcedatapackloader.util;

import mctmods.resourcedatapackloader.pack.PackManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
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
import javax.annotation.Nullable;

public final class Json {
    public static final Gson GSON = new GsonBuilder().create();
    public static final Gson PRETTY = new GsonBuilder().setPrettyPrinting().create();

    private Json() {}

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
        float value = GsonHelper.getAsFloat(json, member, fallback);
        if (value >= low && value <= high) { return value; }
        ContentLog.LOGGER.error("{} sets {} to {}, which is outside {} to {}, so the default is used instead", owner, member, value, low, high);
        return fallback;
    }

    public static String choice(JsonObject json, String member, String fallback, Object owner, Collection<String> options) {
        if (!json.has(member)) { return fallback; }
        String asked = GsonHelper.getAsString(json, member).trim();
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
        if (!held.isJsonArray()) { throw new JsonSyntaxException("Expected " + member + " to be a JsonArray, was " + GsonHelper.getType(held)); }
        for (JsonElement element : held.getAsJsonArray()) {
            if (element.isJsonPrimitive()) { out.add(element.getAsString()); }
            else if (element.isJsonObject() && GsonHelper.isValidPrimitive(element.getAsJsonObject(), "block")) { out.add(element.getAsJsonObject().get("block").getAsString()); }
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
