package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.compat.LineCompat;
import mctmods.resourcedatapackloader.pack.GeneratedResources;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.GameData;
import mctmods.resourcedatapackloader.util.Json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.Set;

final class ContentOptionNoise {
    private static final String BIOME_SIZE = "biomeSize";
    private static final String DENSITY = "worldgen/density_function";
    private static final String NOISE = "worldgen/noise";
    private static final int DEFAULT_SIZE = 4;
    private static final int LARGE_SIZE = 6;
    private static final Set<String> CLIMATE_KEYS = Set.of("temperature", "vegetation");
    private static final Set<String> CLIMATE_NOISES = Set.of("temperature", "vegetation", "temperature_large", "vegetation_large");
    private static final List<String> ROOTS = List.of("noise_router", "aquifers");
    private static final List<String> ANCHORS = List.of("anchor", "true_at_and_below", "false_at_and_above");
    private final Identifier owner;
    private final int offset;
    private final int octaves;
    private final Map<String, String> copied = new HashMap<>();
    private final Map<String, String> noises = new HashMap<>();

    private ContentOptionNoise(Identifier owner, int offset, int octaves) {
        this.owner = owner;
        this.offset = offset;
        this.octaves = octaves;
    }

    record Shifted(int offset, Map<String, String> copies) {
        static final Shifted NONE = new Shifted(0, Map.of());

        String route(String vanilla) { return copies.getOrDefault(vanilla, vanilla); }
    }

    static boolean ignores(String key) { return !BIOME_SIZE.equals(key); }

    static boolean shapes() { return biomeSize().isPresent(); }

    private static OptionalInt biomeSize() {
        JsonObject options = ContentTerrain.customizedOptions();
        return options == null ? OptionalInt.empty() : ContentTerrain.optionNumber(options, BIOME_SIZE);
    }

    static Shifted apply(JsonObject settings, Identifier owner, boolean large, int maxY) {
        int offset = ContentTerrain.terrainOffset();
        if (offset != 0 && (offset < 0 || (offset & 15) != 0 || ContentWorldShape.VANILLA_MAX + offset > maxY)) {
            ContentLog.LOGGER.error("terrainOffset is {}, which must be a multiple of 16 and leave the vanilla top {} plus the offset under worldMaxHeight {}, so the terrain is not shifted", offset, ContentWorldShape.VANILLA_MAX, maxY);
            offset = 0;
        }
        OptionalInt size = biomeSize();
        int octaves = size.isPresent() ? size.getAsInt() - (large ? LARGE_SIZE : DEFAULT_SIZE) : 0;
        if (offset == 0 && octaves == 0) { return Shifted.NONE; }
        ContentOptionNoise shifter = new ContentOptionNoise(owner, offset, octaves);
        shifter.run(settings);
        if (offset != 0) { ContentLog.LOGGER.info("terrainOffset {} lifts the whole vanilla terrain window of {} by {} blocks, sea level and surface layers with it", offset, owner, offset); }
        if (octaves != 0) { ContentLog.LOGGER.info("generatorOptions biomeSize {} moves the temperature and vegetation noise {} octave(s) from the world type's own", size.getAsInt(), -octaves); }
        return new Shifted(offset, Map.copyOf(shifter.copied));
    }

    private void run(JsonObject settings) {
        for (String root : ROOTS) {
            JsonObject object = Json.getJsonObject(settings, root, null);
            if (object == null) { continue; }
            for (String key : List.copyOf(object.keySet())) {
                if (offset != 0 || CLIMATE_KEYS.contains(key)) { object.add(key, walk(object.get(key))); }
            }
        }
        if (offset == 0) { return; }
        if (settings.has("sea_level")) { settings.addProperty("sea_level", Json.getInt(settings, "sea_level", 0) + offset); }
        for (String rule : List.of("surface_rule", "material_rule")) {
            if (settings.has(rule)) { anchors(settings.get(rule)); }
        }
    }

    private JsonElement walk(JsonElement element) {
        if (element.isJsonPrimitive()) { return element.getAsJsonPrimitive().isString() ? reference(element.getAsString(), element) : element; }
        if (element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();
            for (int index = 0; index < array.size(); index++) { array.set(index, walk(array.get(index))); }
            return array;
        }
        if (!element.isJsonObject()) { return element; }
        JsonObject object = element.getAsJsonObject();
        for (String key : List.copyOf(object.keySet())) {
            if ("type".equals(key)) { continue; }
            object.add(key, "noise".equals(key) ? noise(object.get(key)) : walk(object.get(key)));
        }
        String type = Json.getString(object, "type", "");
        if (LineCompat.DENSITY_GRADIENT.equals(type) && "y".equals(Json.getString(object, "axis", "y"))) {
            raise(object, LineCompat.GRADIENT_FROM);
            raise(object, LineCompat.GRADIENT_TO);
        }
        else if ("minecraft:find_top_surface".equals(type) && offset != 0) {
            raise(object, "lower_bound");
            if (object.has("upper_bound")) {
                object.add("upper_bound", LineCompat.densityPair("minecraft:add", object.get("upper_bound"), new JsonPrimitive((double) offset)));
            }
        }
        return object;
    }

    private JsonElement reference(String written, JsonElement element) {
        Identifier id = Identifier.tryParse(written);
        if (id == null || !Identifier.DEFAULT_NAMESPACE.equals(id.getNamespace())) { return element; }
        String made = copied.get(written);
        if (made != null) { return new JsonPrimitive(made); }
        Identifier file = WorldgenFiles.id(DENSITY, id);
        JsonObject source = GameData.has(file) ? GameData.json(file) : null;
        if (source == null) { return element; }
        Identifier copy = Identifier.fromNamespaceAndPath(owner.getNamespace(), owner.getPath() + "_density/" + id.getPath());
        copied.put(written, copy.toString());
        JsonElement walked = walk(source);
        GeneratedResources.put(PackType.SERVER_DATA, copy.getNamespace(), WorldgenFiles.file(DENSITY, copy.getPath()), walked.toString());
        return new JsonPrimitive(copy.toString());
    }

    private JsonElement noise(JsonElement element) {
        if (octaves == 0 || !element.isJsonPrimitive() || !element.getAsJsonPrimitive().isString()) { return element; }
        Identifier id = Identifier.tryParse(element.getAsString());
        if (id == null || !Identifier.DEFAULT_NAMESPACE.equals(id.getNamespace()) || !CLIMATE_NOISES.contains(id.getPath())) { return element; }
        String made = noises.get(element.getAsString());
        if (made != null) { return new JsonPrimitive(made); }
        JsonObject source = GameData.json(WorldgenFiles.id(NOISE, id));
        String octave = source == null ? "" : source.has("firstOctave") ? "firstOctave" : "base_octave";
        if (source == null || !source.has(octave)) { return element; }
        source.addProperty(octave, source.get(octave).getAsInt() - octaves);
        Identifier copy = Identifier.fromNamespaceAndPath(owner.getNamespace(), owner.getPath() + "_" + id.getPath());
        GeneratedResources.put(PackType.SERVER_DATA, copy.getNamespace(), WorldgenFiles.file(NOISE, copy.getPath()), source.toString());
        noises.put(element.getAsString(), copy.toString());
        return new JsonPrimitive(copy.toString());
    }

    private void raise(JsonObject object, String key) {
        if (offset != 0 && object.has(key) && object.get(key).isJsonPrimitive() && object.get(key).getAsJsonPrimitive().isNumber()) { object.addProperty(key, object.get(key).getAsInt() + offset); }
    }

    private void anchors(JsonElement element) {
        if (element.isJsonArray()) {
            for (JsonElement child : element.getAsJsonArray()) { anchors(child); }
            return;
        }
        if (!element.isJsonObject()) { return; }
        JsonObject object = element.getAsJsonObject();
        for (String key : List.copyOf(object.keySet())) {
            JsonElement child = object.get(key);
            if (ANCHORS.contains(key) && child.isJsonObject() && child.getAsJsonObject().has("absolute")) { raise(child.getAsJsonObject(), "absolute"); }
            else { anchors(child); }
        }
    }
}
