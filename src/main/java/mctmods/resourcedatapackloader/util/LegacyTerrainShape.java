package mctmods.resourcedatapackloader.util;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;

public record LegacyTerrainShape(Map<String, Double> values, OptionalInt riverSize) {
    public static final String NOISE_FUNCTION = "legacy_noise";
    public static final String FACTOR_FUNCTION = "legacy_factor";
    public static final String OFFSET_FUNCTION = "legacy_offset";
    public static final String COORDINATE_SCALE = "coordinateScale";
    public static final String HEIGHT_SCALE = "heightScale";
    public static final String UPPER_LIMIT_SCALE = "upperLimitScale";
    public static final String LOWER_LIMIT_SCALE = "lowerLimitScale";
    public static final String DEPTH_SCALE_X = "depthNoiseScaleX";
    public static final String DEPTH_SCALE_Z = "depthNoiseScaleZ";
    public static final String DEPTH_EXPONENT = "depthNoiseScaleExponent";
    public static final String MAIN_SCALE_X = "mainNoiseScaleX";
    public static final String MAIN_SCALE_Y = "mainNoiseScaleY";
    public static final String MAIN_SCALE_Z = "mainNoiseScaleZ";
    public static final String BASE_SIZE = "baseSize";
    public static final String STRETCH_Y = "stretchY";
    public static final String DEPTH_WEIGHT = "biomeDepthWeight";
    public static final String DEPTH_OFFSET = "biomeDepthOffset";
    public static final String SCALE_WEIGHT = "biomeScaleWeight";
    public static final String SCALE_OFFSET = "biomeScaleOffset";
    public static final String RIVER_SIZE = "riverSize";
    public static final LegacyTerrainShape NONE = new LegacyTerrainShape(Map.of(), OptionalInt.empty());
    private static final int DEFAULT_RIVER = 4;
    private static final double BIOME_WEIGHT_SUM = 17.0D;
    private static final double DEFAULT_BIOME_HEIGHT = 64.25D;
    public static final double OFFSET_BLOCKS = 128.0D;
    private static final double SURFACE_PER_SIZE = 7.5D;
    private static final double DEPTH_DIVISOR = 8000.0D;
    private static final double DEPTH_REACH = 0.8D;
    public static final int DEPTH_OCTAVES = 16;
    public static final double DEPTH_SUM = 65535.0D;
    private static final Map<String, Double> DEFAULTS = defaults();
    private static final Set<String> DIVISORS = Set.of(UPPER_LIMIT_SCALE, LOWER_LIMIT_SCALE, MAIN_SCALE_X, MAIN_SCALE_Y, MAIN_SCALE_Z);
    private static final Set<String> NOISE_KEYS = Set.of(COORDINATE_SCALE, HEIGHT_SCALE, UPPER_LIMIT_SCALE, LOWER_LIMIT_SCALE, MAIN_SCALE_X, MAIN_SCALE_Y, MAIN_SCALE_Z);
    private static final Set<String> FACTOR_KEYS = Set.of(STRETCH_Y, SCALE_WEIGHT, SCALE_OFFSET);
    private static final Set<String> WARNED = ConcurrentHashMap.newKeySet();

    private static Map<String, Double> defaults() {
        Map<String, Double> out = new LinkedHashMap<>();
        out.put(COORDINATE_SCALE, 684.412D);
        out.put(HEIGHT_SCALE, 684.412D);
        out.put(UPPER_LIMIT_SCALE, 512.0D);
        out.put(LOWER_LIMIT_SCALE, 512.0D);
        out.put(DEPTH_SCALE_X, 200.0D);
        out.put(DEPTH_SCALE_Z, 200.0D);
        out.put(DEPTH_EXPONENT, 0.5D);
        out.put(MAIN_SCALE_X, 80.0D);
        out.put(MAIN_SCALE_Y, 160.0D);
        out.put(MAIN_SCALE_Z, 80.0D);
        out.put(BASE_SIZE, 8.5D);
        out.put(STRETCH_Y, 12.0D);
        out.put(DEPTH_WEIGHT, 1.0D);
        out.put(DEPTH_OFFSET, 0.0D);
        out.put(SCALE_WEIGHT, 1.0D);
        out.put(SCALE_OFFSET, 0.0D);
        return Map.copyOf(out);
    }

    public static boolean reads(String key) { return DEFAULTS.containsKey(key) || RIVER_SIZE.equals(key); }

    public static LegacyTerrainShape of(@Nullable JsonObject options) {
        if (options == null) { return NONE; }
        Map<String, Double> values = new LinkedHashMap<>();
        for (String key : DEFAULTS.keySet()) { number(options, key, DIVISORS.contains(key)).ifPresent(value -> values.put(key, value)); }
        OptionalDouble river = number(options, RIVER_SIZE, true);
        return new LegacyTerrainShape(Map.copyOf(values), river.isPresent() ? OptionalInt.of((int) Math.round(river.getAsDouble())) : OptionalInt.empty());
    }

    private static OptionalDouble number(JsonObject options, String key, boolean divisor) {
        if (!options.has(key)) { return OptionalDouble.empty(); }
        JsonElement held = options.get(key);
        double value;
        try { value = held.isJsonPrimitive() ? held.getAsDouble() : Double.NaN; }
        catch (NumberFormatException notNumber) { value = Double.NaN; }
        if (!Double.isFinite(value)) {
            ContentLog.LOGGER.errorOnce(WARNED, key + "=" + held, "generatorOptions sets '{}' to {}, which is not a number, so it is left out", key, held);
            return OptionalDouble.empty();
        }
        if (divisor && value <= 0.0D) {
            ContentLog.LOGGER.errorOnce(WARNED, key + "=" + held, "generatorOptions sets '{}' to {}, which must be above 0, so it is left out", key, held);
            return OptionalDouble.empty();
        }
        return OptionalDouble.of(value);
    }

    public boolean any() { return !values.isEmpty(); }

    public LegacyTerrainShape terrain() { return new LegacyTerrainShape(values, OptionalInt.empty()); }

    public int riverOctaves() { return riverSize.isPresent() ? riverSize.getAsInt() - DEFAULT_RIVER : 0; }

    public double get(String key) { return values.getOrDefault(key, DEFAULTS.get(key)); }

    public boolean shapesNoise() { return changes(NOISE_KEYS); }

    public boolean shapesFactor() { return changes(FACTOR_KEYS); }

    private boolean changes(Set<String> keys) {
        for (String key : keys) {
            if (get(key) != DEFAULTS.get(key)) { return true; }
        }
        return false;
    }

    public double offsetScale() { return 2.0D * get(BASE_SIZE) * get(DEPTH_WEIGHT) / BIOME_WEIGHT_SUM; }

    public double offsetShift() {
        double size = get(BASE_SIZE);
        return (SURFACE_PER_SIZE * size + 2.0D * size * get(DEPTH_OFFSET) + 2.0D * size * get(DEPTH_WEIGHT) * DEFAULT_BIOME_HEIGHT / BIOME_WEIGHT_SUM - OFFSET_BLOCKS) / OFFSET_BLOCKS;
    }

    public static double depthOffset(double raw, double baseSize) {
        double depth = raw / DEPTH_DIVISOR;
        if (depth < 0.0D) { depth = -depth * 0.3D; }
        depth = depth * 3.0D - 2.0D;
        if (depth < 0.0D) { depth = Math.max(depth / 2.0D, -1.0D) / 1.4D / 2.0D; }
        else { depth = Math.min(depth, 1.0D) / 8.0D; }
        return depth * baseSize * DEPTH_REACH / OFFSET_BLOCKS;
    }

    public static double lowestDepth(double baseSize) { return Math.min(depthOffset(0.0D, baseSize), depthOffset(DEPTH_DIVISOR, baseSize)); }

    public static double highestDepth(double baseSize) { return Math.max(depthOffset(0.0D, baseSize), depthOffset(DEPTH_DIVISOR, baseSize)); }

    public JsonObject noise(JsonElement base) {
        JsonObject out = typed(NOISE_FUNCTION);
        out.add("noise", base);
        out.addProperty("coordinate_scale", get(COORDINATE_SCALE));
        out.addProperty("height_scale", get(HEIGHT_SCALE));
        out.addProperty("main_scale_x", get(MAIN_SCALE_X));
        out.addProperty("main_scale_y", get(MAIN_SCALE_Y));
        out.addProperty("main_scale_z", get(MAIN_SCALE_Z));
        out.addProperty("lower_limit_scale", get(LOWER_LIMIT_SCALE));
        out.addProperty("upper_limit_scale", get(UPPER_LIMIT_SCALE));
        return out;
    }

    public JsonObject factor(JsonElement factor) {
        JsonObject out = typed(FACTOR_FUNCTION);
        out.add("factor", factor);
        out.addProperty("stretch_y", get(STRETCH_Y));
        out.addProperty("scale_weight", get(SCALE_WEIGHT));
        out.addProperty("scale_offset", get(SCALE_OFFSET));
        return out;
    }

    public JsonObject offset(JsonElement offset, String seed) {
        JsonObject out = typed(OFFSET_FUNCTION);
        out.add("offset", offset);
        out.addProperty("scale", offsetScale());
        out.addProperty("shift", offsetShift());
        out.add("noise", new JsonPrimitive(seed));
        out.addProperty("depth_scale_x", get(DEPTH_SCALE_X));
        out.addProperty("depth_scale_z", get(DEPTH_SCALE_Z));
        out.addProperty("base_size", get(BASE_SIZE));
        return out;
    }

    private static JsonObject typed(String function) {
        JsonObject out = new JsonObject();
        out.addProperty("type", ResourceDataPackLoader.id(function).toString());
        return out;
    }
}
