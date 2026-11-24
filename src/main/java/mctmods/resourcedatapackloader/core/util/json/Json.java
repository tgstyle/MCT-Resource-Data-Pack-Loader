package mctmods.resourcedatapackloader.core.util.json;

import mctmods.resourcedatapackloader.common.pack.loading.ContentInherits;
import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Enums;
import mctmods.resourcedatapackloader.core.util.IdNames;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.ResourceLocation;

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

    public static void bad(String owner, String path, Object value, String expected) {
        ContentLog.LOGGER.error("{} gives {} as {}, which is not {}, ignoring it", owner, path, value, expected);
    }

    private static <T> T tolerant(JsonObject object, String key, T fallback, Supplier<T> read) {
        if (!object.has(key)) { return fallback; }
        try { return read.get(); }
        catch (RuntimeException bad) {
            ContentLog.LOGGER.error(BAD_VALUE, where(), key, object.get(key), fallback);
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

    @Nullable public static JsonElement first(JsonElement value) {
        if (!value.isJsonArray()) { return value; }
        JsonArray array = value.getAsJsonArray();
        return array.size() > 0 ? array.get(0) : null;
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
        return stringList(array, key, false, true);
    }

    private static boolean unusable(JsonElement element, String member) {
        if (element.isJsonPrimitive()) { return false; }
        ContentLog.LOGGER.error(BAD_ENTRY, where(), member, element);
        return true;
    }

    public static Iterable<JsonObject> objectsOrSkip(JsonArray array, String skipped, Object owner) {
        return () -> {
            Iterator<JsonElement> source = array.iterator();
            return new Iterator<JsonObject>() {
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

    public static JsonObject object(JsonObject parent, String key) { return parent.has(key) && parent.get(key).isJsonObject() ? parent.getAsJsonObject(key) : new JsonObject(); }

    public static JsonArray array(JsonObject parent, String key) { return JsonUtils.isJsonArray(parent, key) ? parent.getAsJsonArray(key) : new JsonArray(); }

    @Nullable public static JsonObject optionalObject(JsonObject parent, String key) { return parent.has(key) && parent.get(key).isJsonObject() ? parent.getAsJsonObject(key) : null; }

    public static int atLeast(JsonObject json, String key, int fallback, int min) { return Math.max(min, getInt(json, key, fallback)); }

    public static float atLeast(JsonObject json, String key, float fallback, float min) { return Math.max(min, getFloat(json, key, fallback)); }

    public static int clamped(JsonObject json, String key, int fallback, int min, int max) { return MathHelper.clamp(getInt(json, key, fallback), min, max); }

    public static float clamped(JsonObject json, String key, float fallback, float min, float max) { return MathHelper.clamp(getFloat(json, key, fallback), min, max); }

    @Nullable public static <T> T optional(JsonObject json, String key, Object owner, String expected, Function<JsonElement, T> reader) {
        if (!json.has(key)) { return null; }
        try {
            T value = reader.apply(json.get(key));
            if (value != null) { return value; }
        }
        catch (RuntimeException ignored) {}
        ContentLog.LOGGER.error("{} gives {} as something other than {}, ignoring it", owner, key, expected);
        return null;
    }

    public static float chance(JsonObject json, String key, float fallback) { return clamped(json, key, fallback, 0.0F, 1.0F); }

    @Nullable public static ResourceLocation location(JsonObject object, String key) {
        String named = getString(object, key, "").trim();
        return named.isEmpty() ? null : new ResourceLocation(named);
    }

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

    @Nullable public static JsonObject object(ResourceLocation key, String contents, String kind) {
        JsonObject json = GSON.fromJson(contents, JsonObject.class);
        if (json == null) { ContentLog.LOGGER.error(EMPTY_FILE, label(kind), key); }
        return json;
    }

    @Nullable public static JsonObject strictObject(String contents) { return JsonUtils.gsonDeserialize(GSON, contents, JsonObject.class); }

    public static JsonElement parse(String text) { return new JsonParser().parse(text); }

    @Nullable public static JsonObject parseObject(String text) {
        try { return parse(text).getAsJsonObject(); }
        catch (RuntimeException notJson) { return null; }
    }

    private static String label(String kind) { return Character.toUpperCase(kind.charAt(0)) + kind.substring(1); }

    public static boolean isString(JsonElement value) { return value.isJsonPrimitive() && value.getAsJsonPrimitive().isString(); }

    public static String text(JsonObject object, String key) { return JsonUtils.isJsonPrimitive(object, key) ? object.get(key).getAsString() : ""; }

    public static void rewriteText(JsonObject object, String key, UnaryOperator<String> mapper) {
        if (!JsonUtils.isJsonPrimitive(object, key)) { return; }
        String mapped = mapper.apply(object.get(key).getAsString());
        if (mapped != null && !mapped.isEmpty()) { object.addProperty(key, mapped); }
    }

    public static String id(JsonObject object, String key) { return IdNames.namespaced(text(object, key)); }

    public static String text(JsonObject object, String key, String fallback) { return JsonUtils.isJsonPrimitive(object, key) ? object.get(key).getAsString().trim() : fallback; }

    @Nullable public static String rawOrNull(JsonObject object, String key) { return JsonUtils.isJsonPrimitive(object, key) ? object.get(key).getAsString() : null; }

    public static String trimmed(JsonObject object, String key, String fallback) { return getString(object, key, fallback).trim(); }

    public static String getLowerString(JsonObject object, String key, String fallback) { return trimmed(object, key, fallback).toLowerCase(Locale.ROOT); }

    public static int number(JsonObject object, String key, int fallback) {
        try { return JsonUtils.isJsonPrimitive(object, key) ? object.get(key).getAsInt() : fallback; }
        catch (NumberFormatException notNumber) { return fallback; }
    }

    public static boolean isBoolean(JsonElement element) { return element.isJsonPrimitive() && element.getAsJsonPrimitive().isBoolean(); }

    public static boolean flag(JsonObject object, String key) { return JsonUtils.isJsonPrimitive(object, key) && object.get(key).getAsBoolean(); }

    public static boolean flag(String owner, String path, JsonElement value, boolean fallback) {
        if (isBoolean(value)) { return value.getAsBoolean(); }
        bad(owner, path, value, "true or false");
        return fallback;
    }

    @Nullable public static String text(JsonElement value) { return isString(value) ? value.getAsString() : null; }

    @Nullable public static Float number(String owner, String path, JsonElement value, boolean whole) {
        if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isNumber()) {
            float number = value.getAsFloat();
            if (number >= 0.0F && number < Float.POSITIVE_INFINITY && (!whole || number == Math.floor(number))) { return number; }
        }
        bad(owner, path, value, (whole ? "a whole number" : "a number") + " of 0 or more");
        return null;
    }

    @Nullable public static Float within(String owner, String path, JsonElement value, boolean whole, float low, float high) {
        Float number = number(owner, path, value, whole);
        if (number == null) { return null; }
        if (number < low || number > high) {
            ContentLog.LOGGER.error("{} gives {} as {}, which is outside {} to {}, ignoring it", owner, path, number, low, high);
            return null;
        }
        return number;
    }

    @Nullable public static JsonObject object(String owner, String path, JsonElement element, String expected) {
        if (element.isJsonObject()) { return element.getAsJsonObject(); }
        ContentLog.LOGGER.error("{} gives {} that is not {}, ignoring it", owner, path, expected);
        return null;
    }

    public static void each(String owner, String path, JsonElement element, String expected, Consumer<String> resolver) {
        if (!element.isJsonArray()) {
            ContentLog.LOGGER.error("{} gives {} that is not a list of {}s, ignoring it", owner, path, expected);
            return;
        }
        String article = "aeiou".indexOf(expected.charAt(0)) < 0 ? "a" : "an";
        for (JsonElement entry : element.getAsJsonArray()) {
            String name = text(entry);
            if (name == null) { ContentLog.LOGGER.error("{} gives {} an entry {} that is not {} {}, ignoring it", owner, path, entry, article, expected); }
            else { resolver.accept(name); }
        }
    }

    private static List<String> stringList(JsonArray array, String key, boolean trim, boolean keepEmpty) {
        List<String> values = new ArrayList<>(array.size());
        for (JsonElement entry : array) {
            if (unusable(entry, key)) { continue; }
            String value = trim ? entry.getAsString().trim() : entry.getAsString();
            if (keepEmpty || !value.isEmpty()) { values.add(value); }
        }
        return values;
    }

    public static List<String> strings(JsonObject json, String member) {
        if (!json.has(member)) { return Collections.emptyList(); }
        return Collections.unmodifiableList(stringList(getJsonArray(json, member, new JsonArray()), member, false, false));
    }

    public static List<String> names(JsonObject json, String field) {
        return stringList(array(json, field), field, true, false);
    }

    public static List<String> unknownKeys(JsonObject json, Collection<String> known) {
        List<String> unknown = new ArrayList<>();
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            if (!known.contains(entry.getKey())) { unknown.add(entry.getKey()); }
        }
        return unknown;
    }

    public static void unknown(String owner, JsonObject json, Collection<String> known) {
        for (String key : unknownKeys(json, known)) { ContentLog.LOGGER.warn("{} sets '{}', which is not a key anything reads, so it does nothing. Check the spelling", owner, key); }
    }

    public static List<String> present(JsonObject json, Collection<String> keys) {
        List<String> found = new ArrayList<>();
        for (String key : keys) {
            if (json.has(key)) { found.add(key); }
        }
        return found;
    }

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

    public static void putOrRemove(JsonObject json, String key, JsonArray values) {
        if (values.size() > 0) { json.add(key, values); }
        else { json.remove(key); }
    }

    @Nullable public static Integer intOrNull(JsonObject object, String key) { return object.has(key) ? getInt(object, key) : null; }

    @Nullable public static Float floatOrNull(JsonObject object, String key) { return object.has(key) ? getFloat(object, key) : null; }

    @Nullable public static String stringOrNull(JsonObject object, String key) { return object.has(key) ? getString(object, key) : null; }

    @Nullable public static Boolean flagOrNull(JsonObject object, String key) { return object.has(key) ? getBoolean(object, key) : null; }

    @Nullable public static Integer intOrNull(JsonObject object, String key, int fallback) { return object.has(key) ? getInt(object, key, fallback) : null; }

    @Nullable public static String stringOrNull(JsonObject object, String key, String fallback) { return object.has(key) ? getString(object, key, fallback) : null; }

    @Nullable public static Boolean flagOrNull(JsonObject object, String key, boolean fallback) { return object.has(key) ? getBoolean(object, key, fallback) : null; }

    public static void moveKey(JsonObject json, String from, String to) {
        if (json.has(from) && !json.has(to)) { json.add(to, json.remove(from)); }
    }

    @Nullable public static JsonObject objectOrNull(JsonObject parent, String member) { return parent.has(member) && parent.get(member).isJsonObject() ? parent.getAsJsonObject(member) : null; }

    @Nullable public static JsonObject objectOrFlag(JsonObject json, String member) {
        JsonElement value = json.get(member);
        if (value == null) { return null; }
        if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isBoolean()) { return value.getAsBoolean() ? new JsonObject() : null; }
        return getJsonObject(json, member, new JsonObject());
    }

    public static float ranged(JsonObject json, String member, float fallback, float low, float high, Object owner) {
        if (!json.has(member)) { return fallback; }
        float value = getFloat(json, member, fallback);
        if (value >= low && value <= high) { return value; }
        ContentLog.LOGGER.error("{} sets {} to {}, which is outside {} to {}, so the default is used instead", owner, member, value, low, high);
        return fallback;
    }

    public static double ranged(JsonObject json, String member, double fallback, double low, double high, Object owner) {
        if (!json.has(member)) { return fallback; }
        JsonElement element = json.get(member);
        if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isNumber()) {
            ContentLog.LOGGER.error("{} sets {} to {}, which is not a number, so {} is used instead", owner, member, element, fallback);
            return fallback;
        }
        double value = element.getAsDouble();
        if (value >= low && value <= high) { return value; }
        ContentLog.LOGGER.error("{} sets {} to {}, which is outside {} to {}, so {} is used instead", owner, member, value, low, high, fallback);
        return fallback;
    }

    @Nullable public static Integer whole(JsonElement held, int least, int most) {
        if (!held.isJsonPrimitive() || !held.getAsJsonPrimitive().isNumber()) { return null; }
        double value = held.getAsDouble();
        return value == Math.rint(value) && value >= least && value <= most ? (int) value : null;
    }

    public static String readText(Path file) throws IOException { return new String(Files.readAllBytes(file), StandardCharsets.UTF_8); }

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
        String matched = Enums.byName(options, option -> option, asked);
        if (matched != null) { return matched; }
        ContentLog.LOGGER.error("{} sets {} to '{}', which is not one of {}, so {} is used instead", owner, member, asked, options, fallback == null ? "the default" : "'" + fallback + "'");
        return fallback;
    }

    public static boolean choiceIs(JsonObject json, String member, String fallback, Object owner, Collection<String> options, String value) {
        return value.equals(choice(json, member, fallback, owner, options));
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

    public static void unknownKeys(JsonObject json, String path, Collection<String> read) {
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            if (!read.contains(entry.getKey())) { ContentLog.LOGGER.warn("{}: unknown key {} under {}, ignoring it", where(), entry.getKey(), path); }
        }
    }

    @Nullable public static <T> T byName(Map<String, T> defs, @Nullable String name) { return name == null || name.isEmpty() ? null : defs.get(name.toLowerCase(Locale.ROOT)); }

    public static void eachFile(String type, String kind, BiConsumer<ResourceLocation, String> reader) { PackManager.get().forEach(type, PackManager.JSON, (namespace, path, contents) -> read(new ResourceLocation(namespace, path), contents, kind, reader)); }

    public static <T> void loadDefs(Map<String, T> defs, String type, String label, BiFunction<ResourceLocation, String, T> parser) {
        eachFile(type, label, (key, contents) -> {
            T def = parser.apply(key, contents);
            if (def != null) { defs.put(key.toString(), def); }
        });
        if (!defs.isEmpty()) { ContentLog.LOGGER.debug("Loaded {} {}(s): {}", defs.size(), label, defs.keySet()); }
    }

    public static void eachInheritedFile(String type, String kind, Set<String> identity, BiConsumer<ResourceLocation, String> reader) {
        for (Map.Entry<ResourceLocation, String> held : ContentInherits.collectFiles(type, identity).entrySet()) { read(held.getKey(), held.getValue(), kind, reader); }
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

    public static void eachObject(String type, String kind, BiConsumer<ResourceLocation, JsonObject> reader) { eachObject(type, kind, kind, reader); }

    public static void eachObject(String type, String kind, String emptyKind, BiConsumer<ResourceLocation, JsonObject> reader) {
        String label = label(emptyKind);
        eachFile(type, kind, (key, contents) -> {
            JsonObject json = GSON.fromJson(contents, JsonObject.class);
            if (json == null) {
                ContentLog.LOGGER.error(EMPTY_FILE, label, key);
                return;
            }
            reader.accept(key, json);
        });
    }
}
