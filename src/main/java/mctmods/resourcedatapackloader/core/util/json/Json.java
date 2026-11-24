package mctmods.resourcedatapackloader.core.util.json;

import mctmods.resourcedatapackloader.common.pack.loading.ContentInherits;
import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.IdNames;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;

public final class Json {
    public static final Gson GSON = new GsonBuilder().create();
    public static final Gson PRETTY = new GsonBuilder().setPrettyPrinting().create();
    public static final String RUNS = "runs";
    private static final String NO_CONTEXT = "pack content";
    private static final String EMPTY_FILE = "{} {} is empty, ignoring it";
    private static final String BAD_VALUE = "{}: key '{}' has bad value {}, using {}";
    private static final String BAD_ENTRY = "{}: key '{}' has bad value {}, using no entry";
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
            ContentLog.LOGGER.error(BAD_VALUE, where(), key, object.get(key), fallback);
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

    @Nullable public static JsonElement first(JsonElement value) {
        if (!value.isJsonArray()) { return value; }
        JsonArray array = value.getAsJsonArray();
        return array.isEmpty() ? null : array.get(0);
    }

    private static <T> T element(JsonElement element, String key, T fallback, Supplier<T> read) {
        try { return read.get(); }
        catch (RuntimeException bad) {
            ContentLog.LOGGER.error(BAD_VALUE, where(), key, element, fallback);
            return fallback;
        }
    }

    public static int asInt(JsonElement element, String key, int fallback) { return element(element, key, fallback, element::getAsInt); }

    @Nullable public static int[] intPair(JsonElement element, String key, int sentinel) {
        if (!element.isJsonArray() || element.getAsJsonArray().size() != 2) { return null; }
        JsonArray pair = element.getAsJsonArray();
        return new int[] { asInt(pair.get(0), key, sentinel), asInt(pair.get(1), key, sentinel) };
    }

    public static int[] pair(Object owner, JsonObject json, String key, @Nullable int[] fallback, int low, int high) {
        if (!json.has(key)) { return fallback; }
        int[] both = intPair(json.get(key), key, low - 1);
        if (both != null && both[0] >= low && both[1] <= high && both[0] <= both[1]) { return both; }
        ContentLog.LOGGER.error("{} sets '{}' to something other than [min, max] with {} <= min <= max <= {}, so {} instead", owner, key, low, high, fallback == null ? "it is ignored" : Arrays.toString(fallback) + " is used");
        return fallback;
    }

    public static float asFloat(JsonElement element, String key, float fallback) { return element(element, key, fallback, element::getAsFloat); }

    public static double asDouble(JsonElement element, String key, double fallback) { return element(element, key, fallback, element::getAsDouble); }

    public static String asString(JsonElement element, String key, String fallback) { return element(element, key, fallback, element::getAsString); }

    public static List<Integer> ints(JsonArray array, String key) {
        List<Integer> values = new ArrayList<>(array.size());
        for (JsonElement entry : array) {
            if (unusable(entry, key)) { continue; }
            try { values.add(entry.getAsInt()); }
            catch (RuntimeException bad) { ContentLog.LOGGER.error(BAD_ENTRY, where(), key, entry); }
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
        ContentLog.LOGGER.error(BAD_ENTRY, where(), member, element);
        return true;
    }

    public static Iterable<JsonObject> objectsOrSkip(JsonArray array, String skipped, Object owner) {
        return () -> {
            Iterator<JsonElement> source = array.iterator();
            return new Iterator<>() {
                private JsonObject ahead;

                @Override
                public boolean hasNext() {
                    while (ahead == null && source.hasNext()) {
                        JsonElement entry = source.next();
                        if (entry.isJsonObject()) { ahead = entry.getAsJsonObject(); }
                        else { ContentLog.LOGGER.error(skipped, owner); }
                    }
                    return ahead != null;
                }

                @Override
                public JsonObject next() {
                    if (!hasNext()) { throw new NoSuchElementException(); }
                    JsonObject held = ahead;
                    ahead = null;
                    return held;
                }
            };
        };
    }

    public static JsonObject object(JsonObject parent, String key) { return GsonHelper.isObjectNode(parent, key) ? parent.getAsJsonObject(key) : new JsonObject(); }

    @Nullable public static JsonObject optionalObject(JsonObject parent, String key) { return GsonHelper.isObjectNode(parent, key) ? parent.getAsJsonObject(key) : null; }

    @Nullable public static JsonObject objectOrNull(JsonObject parent, String member) { return optionalObject(parent, member); }

    public static int atLeast(JsonObject json, String key, int fallback, int min) { return Math.max(min, getInt(json, key, fallback)); }

    public static float atLeast(JsonObject json, String key, float fallback, float min) { return Math.max(min, getFloat(json, key, fallback)); }

    public static int clamped(JsonObject json, String key, int fallback, int min, int max) { return Mth.clamp(getInt(json, key, fallback), min, max); }

    public static float clamped(JsonObject json, String key, float fallback, float min, float max) { return Mth.clamp(getFloat(json, key, fallback), min, max); }

    public interface VariantReader<V> { V read(String name, int meta, JsonObject json); }

    public static <V> Map<Integer, V> variants(JsonObject variants, ResourceLocation key, String kind, int maxMeta, String rangeAdvice, Collection<String> variantKeys, VariantReader<V> read) {
        Map<Integer, V> byMeta = new LinkedHashMap<>();
        Map<Integer, String> names = new LinkedHashMap<>();
        for (Map.Entry<String, JsonElement> entry : variants.entrySet()) {
            String name = entry.getKey();
            if (!entry.getValue().isJsonObject()) {
                ContentLog.LOGGER.error("{} variant '{}' in {} is not an object, skipping it", kind, name, key);
                continue;
            }
            JsonObject variant = entry.getValue().getAsJsonObject();
            unknownKeys(variant, "variants", variantKeys);
            int meta = getInt(variant, "meta", -1);
            if (meta < 0 || meta > maxMeta) {
                ContentLog.LOGGER.error("{} variant '{}' in {} has meta {}, which is outside 0-{}, skipping it{}", kind, name, key, meta, maxMeta, rangeAdvice.isEmpty() ? "" : ". " + rangeAdvice);
                continue;
            }
            if (names.containsKey(meta)) {
                ContentLog.LOGGER.error("{} variants '{}' and '{}' in {} both claim meta {}, skipping '{}'", kind, names.get(meta), name, key, meta, name);
                continue;
            }
            names.put(meta, name);
            byMeta.put(meta, read.read(name, meta, variant));
        }
        return byMeta;
    }

    public static boolean isString(JsonElement value) { return value.isJsonPrimitive() && value.getAsJsonPrimitive().isString(); }

    public static boolean isBoolean(JsonElement element) { return element.isJsonPrimitive() && element.getAsJsonPrimitive().isBoolean(); }

    public static String id(JsonObject object, String key) { return IdNames.namespaced(text(object, key)); }

    public static String getLowerString(JsonObject object, String key, String fallback) { return getString(object, key, fallback).trim().toLowerCase(Locale.ROOT); }

    public static <T> void eachCapped(Iterable<T> items, int cap, Predicate<T> kind, Predicate<T> keep, Consumer<T> wrongKind, Consumer<T> overCap) {
        int kept = 0;
        for (T item : items) {
            if (!kind.test(item)) { wrongKind.accept(item); }
            else if (kept >= cap) { overCap.accept(item); }
            else if (keep.test(item)) { kept++; }
        }
    }

    public static List<Integer> dimensions(JsonObject json) {
        if (!json.has("dimensions")) { return Collections.emptyList(); }
        return Collections.unmodifiableList(ints(getJsonArray(json, "dimensions"), "dimensions"));
    }

    @Nullable public static Integer intOrNull(JsonObject object, String key) { return object.has(key) ? getInt(object, key) : null; }

    @Nullable public static String stringOrNull(JsonObject object, String key) { return object.has(key) ? getString(object, key) : null; }

    @Nullable public static Boolean flagOrNull(JsonObject object, String key) { return object.has(key) ? getBoolean(object, key) : null; }

    @Nullable public static Integer intOrNull(JsonObject object, String key, int fallback) { return object.has(key) ? getInt(object, key, fallback) : null; }

    @Nullable public static String stringOrNull(JsonObject object, String key, String fallback) { return object.has(key) ? getString(object, key, fallback) : null; }

    @Nullable public static Boolean flagOrNull(JsonObject object, String key, boolean fallback) { return object.has(key) ? getBoolean(object, key, fallback) : null; }

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
        if (json == null) { ContentLog.LOGGER.error(EMPTY_FILE, label(kind), key); }
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

    @Nullable public static Integer whole(JsonElement held, int least, int most) {
        if (!held.isJsonPrimitive() || !held.getAsJsonPrimitive().isNumber()) { return null; }
        double value = held.getAsDouble();
        return value == Math.rint(value) && value >= least && value <= most ? (int) value : null;
    }

    public static String required(JsonObject json, String member, ResourceLocation key, String message) {
        String value = getString(json, member, "");
        if (value.isEmpty()) { ContentLog.LOGGER.error(message, key); }
        return value;
    }

    public static boolean missing(JsonObject json, String member, ResourceLocation key, String message) {
        if (json.has(member)) { return false; }
        ContentLog.LOGGER.error(message, key);
        return true;
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
        return Collections.unmodifiableMap(primitives(getJsonObject(json, member, new JsonObject()), skipped -> { }));
    }

    public static Map<String, String> primitives(JsonObject object, Consumer<String> skip) {
        Map<String, String> values = new LinkedHashMap<>();
        for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
            if (!entry.getValue().isJsonPrimitive()) {
                skip.accept(entry.getKey());
                continue;
            }
            values.put(entry.getKey(), entry.getValue().getAsString());
        }
        return values;
    }

    @Nullable public static <T> T byName(Map<String, T> defs, @Nullable String name) { return name == null || name.isEmpty() ? null : defs.get(name.toLowerCase(Locale.ROOT)); }

    public static void unknownKeys(JsonObject json, String path, Collection<String> read) {
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            if (!read.contains(entry.getKey())) { ContentLog.LOGGER.warn("{}: unknown key {} under {}, ignoring it", where(), entry.getKey(), path); }
        }
    }

    public static void eachFile(String folder, String kind, BiConsumer<ResourceLocation, String> reader) { PackManager.get().forEach(folder, PackManager.JSON, (namespace, path, contents) -> read(ResourceLocation.fromNamespaceAndPath(namespace, path), contents, kind, reader)); }

    public static void eachInheritedFile(String folder, String kind, Set<String> identity, BiConsumer<ResourceLocation, String> reader) {
        for (Map.Entry<ResourceLocation, String> held : ContentInherits.collectFiles(folder, identity).entrySet()) { read(held.getKey(), held.getValue(), kind, reader); }
    }

    private static void read(ResourceLocation key, String contents, String kind, BiConsumer<ResourceLocation, String> reader) {
        try {
            within(kind, key, () -> {
                reader.accept(key, contents);
                return null;
            });
        }
        catch (JsonParseException ex) { ContentLog.LOGGER.error("Parsing error in {} {}, ignoring it: {}", kind, key, ex.getMessage()); }
        catch (RuntimeException ex) { ContentLog.LOGGER.error("Parsing error in " + kind + " {}, ignoring it", key, ex); }
    }

    public static void eachObject(String folder, String kind, BiConsumer<ResourceLocation, JsonObject> reader) {
        eachFile(folder, kind, (key, contents) -> {
            JsonObject json = object(key, contents, kind);
            if (json != null) { reader.accept(key, json); }
        });
    }
}
