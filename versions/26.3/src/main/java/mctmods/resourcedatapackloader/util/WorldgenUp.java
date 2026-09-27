package mctmods.resourcedatapackloader.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public final class WorldgenUp {
    private static final String TYPE = "type";
    private static final String CONFIG = "config";
    private static final String NAME = "Name";
    private static final String PROPERTIES = "Properties";
    private static final String SURFACE_RULE = "surface_rule";
    private static final String MATERIAL_RULE = "material_rule";
    private static final String SEQUENCE = "sequence";
    private static final String ROUTER = "noise_router";
    private static final String SPAWN_TARGET = "spawn_target";
    private static final String ORE_VEIN = "minecraft:ore_vein";
    private static final List<String> SETTINGS_STATES = List.of("default_block", "default_fluid");
    private static final List<String> VEIN_STATES = List.of("ore_block", "raw_ore_block", "filler_block");
    private static final Map<String, String> PROVIDERS = Map.of(
            "minecraft:simple_state_provider", "minecraft:simple",
            "minecraft:weighted_state_provider", "minecraft:weighted",
            "minecraft:noise_provider", "minecraft:noise",
            "minecraft:dual_noise_provider", "minecraft:dual_noise",
            "minecraft:rotated_block_provider", "minecraft:rotated",
            "minecraft:noise_threshold_provider", "minecraft:noise_threshold",
            "minecraft:randomized_int_state_provider", "minecraft:randomized_int",
            "minecraft:rule_based_state_provider", "minecraft:rule_based");
    private static final Map<String, String> SPAWN_AXES = Map.of(
            "temperature", "temperature",
            "humidity", "vegetation",
            "continentalness", "continents",
            "erosion", "erosion",
            "weirdness", "ridges");
    private static final List<String> AQUIFER_KEYS = List.of("barrier", "fluid_level_floodedness", "fluid_level_spread", "lava");
    private static final List<String> VEIN_KEYS = List.of("vein_toggle", "vein_ridged", "vein_gap");
    private static final String PRELIMINARY = "preliminary_surface_level";
    private static final String INPUT = "input";
    private static final String CACHE = "minecraft:cache";
    private static final String FIRST_OCTAVE = "firstOctave";
    private static final String AMPLITUDES = "amplitudes";
    private static final Set<String> CACHES = Set.of("minecraft:flat_cache", "minecraft:cache_2d", "minecraft:cache_once", "minecraft:cache_all_in_cell");
    private static final Set<String> SHIFTS = Set.of("minecraft:shift_a", "minecraft:shift_b", "minecraft:shift");
    private static final Set<String> CARRIED = Set.of("minecraft:constant", "minecraft:noise", "minecraft:abs", "minecraft:square", "minecraft:cube", "minecraft:half_negative", "minecraft:quarter_negative", "minecraft:squeeze", "minecraft:add", "minecraft:mul", "minecraft:min", "minecraft:max", "minecraft:spline", "minecraft:clamp", "minecraft:range_choice", "minecraft:interval_select", "minecraft:blend_density", "minecraft:blend_alpha", "minecraft:blend_offset", "minecraft:beardifier", "minecraft:find_top_surface", "minecraft:old_blended_noise", "minecraft:interpolated");
    private WorldgenUp() {}

    public static JsonObject portedNoiseSettings(JsonObject settings, Consumer<String> note) {
        JsonObject router = settings.has(ROUTER) && settings.get(ROUTER).isJsonObject() ? settings.getAsJsonObject(ROUTER) : new JsonObject();
        boolean aquifers = settings.has("aquifers_enabled") && !off(settings, "aquifers_enabled");
        if (settings.has("ore_veins_enabled") && !off(settings, "ore_veins_enabled")) { note.accept("makes ore veins through its noise router, which 26.3 does not have, so its ore veins are dropped"); }
        spawnLosses(settings, note);
        JsonObject out = noiseSettings(settings);
        JsonObject noise = out.has("noise") && out.get("noise").isJsonObject() ? out.getAsJsonObject("noise") : new JsonObject();
        int cellXz = noise.has("size_horizontal") ? noise.get("size_horizontal").getAsInt() * 4 : 4;
        int cellY = noise.has("size_vertical") ? noise.get("size_vertical").getAsInt() * 4 : 8;
        JsonObject ported = out.has(ROUTER) && out.get(ROUTER).isJsonObject() ? out.getAsJsonObject(ROUTER) : new JsonObject();
        JsonElement surface = router.has(PRELIMINARY) ? density(router.get(PRELIMINARY), cellXz, cellY, note) : new JsonPrimitive(0.0);
        JsonObject aquifer = new JsonObject();
        for (String key : AQUIFER_KEYS) {
            JsonElement held = ported.remove(key);
            if (held != null) { aquifer.add(key, density(held, cellXz, cellY, note)); }
        }
        for (String key : VEIN_KEYS) { ported.remove(key); }
        JsonObject routed = new JsonObject();
        for (Map.Entry<String, JsonElement> entry : ported.entrySet()) { routed.add(entry.getKey(), density(entry.getValue(), cellXz, cellY, note)); }
        routed.add("chunk_surface_level", surface.isJsonPrimitive() && surface.getAsJsonPrimitive().isNumber() ? surface : interpolated(surface));
        out.add(ROUTER, routed);
        if (aquifers) {
            aquifer.add("exclusion", exclusion(routed.get("erosion"), routed.get("depth")));
            aquifer.add("surface_level", surface.deepCopy());
            out.add("aquifers", aquifer);
        }
        return out;
    }

    public static JsonObject ported(JsonObject json, Consumer<String> note) { return placed(configured(json), note).getAsJsonObject(); }

    public static JsonObject carver(JsonObject carver, Consumer<String> note) {
        String type = type(carver);
        boolean nether = "minecraft:nether_cave".equals(type);
        JsonObject out = carver.deepCopy();
        out.remove("debug_settings");
        JsonElement replaceable = out.remove("replaceable");
        JsonElement lava = out.remove("lava_level");
        String tag = nether ? "#minecraft:nether_carver_replaceables" : "#minecraft:overworld_carver_replaceables";
        if (replaceable != null && !(replaceable.isJsonPrimitive() && tag.equals(replaceable.getAsString()))) { note.accept("names the blocks its carver may cut, which 26.3 leaves to the noise settings, so that list is dropped"); }
        if (lava != null && !lava.equals(JsonParser.parseString(nether ? "{\"above_bottom\":10}" : "{\"above_bottom\":8}"))) { note.accept("sets its carver lava level, which 26.3 leaves to the noise settings, so it is dropped"); }
        JsonElement scale = out.remove("yScale");
        if ("minecraft:canyon".equals(type)) {
            if (scale != null && out.has("shape") && out.get("shape").isJsonObject()) { out.getAsJsonObject("shape").add("y_scale", scale); }
            return out;
        }
        if (!nether && !"minecraft:cave".equals(type)) { return out; }
        out.addProperty(TYPE, "minecraft:cave");
        out.add("count", JsonParser.parseString("{\"type\":\"minecraft:very_biased_to_bottom\",\"min_inclusive\":0,\"max_inclusive\":" + (nether ? 9 : 14) + "}"));
        out.add("thickness", JsonParser.parseString(nether ? "{\"type\":\"minecraft:trapezoid\",\"min\":0.0,\"max\":6.0,\"plateau\":2.0}" : "{\"type\":\"minecraft:trapezoid\",\"min\":0.0,\"max\":3.0,\"plateau\":1.0}"));
        if (nether) { out.addProperty("start_vertical_radius_multiplier", 5.0); }
        else { out.addProperty("weird_thickness_bias", true); }
        if (scale != null) { out.add("room_vertical_radius_multiplier", scale); }
        return out;
    }

    public static JsonObject noise(JsonObject noise, Consumer<String> note) {
        if (!noise.has(FIRST_OCTAVE) || !noise.has(AMPLITUDES) || !noise.get(AMPLITUDES).isJsonArray()) { return noise; }
        List<Double> modifiers = new ArrayList<>();
        for (JsonElement amplitude : noise.getAsJsonArray(AMPLITUDES)) { modifiers.add(amplitude.getAsDouble()); }
        if (modifiers.isEmpty()) { return noise; }
        if (modifiers.stream().anyMatch(modifier -> modifier < 0.0)) {
            note.accept("gives a noise octave a negative amplitude, which 26.3 cannot, so its size is used");
            modifiers.replaceAll(Math::abs);
        }
        JsonObject out = new JsonObject();
        out.addProperty("base_amplitude", NoiseParity.baseAmplitude(modifiers));
        out.add("base_octave", noise.get(FIRST_OCTAVE));
        if (modifiers.size() > 1) { out.addProperty("octave_count", modifiers.size()); }
        if (modifiers.stream().anyMatch(modifier -> modifier != 1.0)) {
            JsonArray list = new JsonArray();
            modifiers.forEach(list::add);
            out.add("amplitude_modifiers", list);
        }
        return out;
    }

    public static JsonObject biome(JsonObject biome, Consumer<String> note) {
        JsonObject out = ported(biome, note);
        JsonObject attributes = out.has("attributes") && out.get("attributes").isJsonObject() ? out.getAsJsonObject("attributes") : new JsonObject();
        JsonElement spawners = out.remove("spawners");
        JsonElement costs = out.remove("spawn_costs");
        if (spawners != null && spawners.isJsonObject()) {
            JsonObject categories = new JsonObject();
            for (Map.Entry<String, JsonElement> entry : spawners.getAsJsonObject().entrySet()) {
                if (entry.getValue().isJsonArray() && !entry.getValue().getAsJsonArray().isEmpty()) { categories.add(entry.getKey(), entry.getValue()); }
            }
            JsonObject spawns = new JsonObject();
            spawns.add("spawn_costs", costs == null ? new JsonObject() : costs);
            spawns.add("spawns_by_category", categories);
            JsonObject overlay = new JsonObject();
            overlay.add("argument", spawns);
            overlay.addProperty("modifier", "overlay");
            attributes.add("minecraft:gameplay/natural_mob_spawns", overlay);
        }
        JsonElement probability = out.remove("creature_spawn_probability");
        if (probability != null) { attributes.add("minecraft:gameplay/creature_world_gen_spawn_probability", probability); }
        if (!attributes.isEmpty()) { out.add("attributes", attributes); }
        return out;
    }

    private static JsonElement placed(JsonElement element, Consumer<String> note) {
        if (element.isJsonArray()) {
            JsonArray out = new JsonArray();
            for (JsonElement item : element.getAsJsonArray()) { out.add(placed(item, note)); }
            return out;
        }
        if (!element.isJsonObject()) { return element; }
        JsonObject in = element.getAsJsonObject();
        boolean offset = "minecraft:random_offset".equals(type(in));
        boolean spawn = in.has("weight") && in.has("minCount");
        JsonObject out = new JsonObject();
        for (Map.Entry<String, JsonElement> entry : in.entrySet()) {
            String key = entry.getKey();
            if (offset && "xz_spread".equals(key)) {
                out.add("x", entry.getValue());
                out.add("z", entry.getValue());
            }
            else if (offset && "y_spread".equals(key)) { out.add("y", entry.getValue()); }
            else if (spawn && "minCount".equals(key)) { out.add("count", count(entry.getValue(), in.get("maxCount"))); }
            else if (!spawn || !"maxCount".equals(key)) { out.add(key, placed(entry.getValue(), note)); }
        }
        if (offset) { out.addProperty(TYPE, "minecraft:offset"); }
        return noise(out, note);
    }

    private static JsonElement count(JsonElement min, JsonElement max) {
        if (max == null || min.equals(max)) { return min; }
        JsonObject out = new JsonObject();
        out.addProperty(TYPE, "minecraft:uniform");
        out.add("min_inclusive", min);
        out.add("max_inclusive", max);
        return out;
    }

    public static JsonElement density(JsonElement element, int cellXz, int cellY, Consumer<String> note) {
        if (element.isJsonArray()) {
            JsonArray out = new JsonArray();
            for (JsonElement item : element.getAsJsonArray()) { out.add(density(item, cellXz, cellY, note)); }
            return out;
        }
        if (!element.isJsonObject()) { return element.deepCopy(); }
        JsonObject in = element.getAsJsonObject();
        String type = type(in);
        JsonObject out = new JsonObject();
        for (Map.Entry<String, JsonElement> entry : in.entrySet()) { out.add(type.isEmpty() ? entry.getKey() : densityKey(entry.getKey(), type), density(entry.getValue(), cellXz, cellY, note)); }
        if (type.isEmpty()) { return noise(out, note); }
        if (CARRIED.contains(type) && !"minecraft:interpolated".equals(type)) { return out; }
        if (CACHES.contains(type)) {
            JsonElement input = out.get(INPUT);
            if (input != null && input.isJsonObject() && CACHE.equals(type(input.getAsJsonObject()))) { return input; }
            out.addProperty(TYPE, CACHE);
            return out;
        }
        switch (type) {
            case "minecraft:interpolated" -> {
                if (cellXz <= 0) { note.accept("interpolates outside noise settings, and 26.3 sizes interpolation cells in the function itself, so the overworld's 4 by 8 is used"); }
                out.addProperty("cell_size_xz", cellXz > 0 ? cellXz : 4);
                out.addProperty("cell_size_y", cellY > 0 ? cellY : 8);
            }
            case "minecraft:y_clamped_gradient" -> {
                out.addProperty(TYPE, "minecraft:gradient");
                out.addProperty("axis", "y");
            }
            case "minecraft:shifted_noise" -> {
                out.addProperty(TYPE, "minecraft:noise");
                JsonElement shift = out.remove("shift_y");
                if (shift != null && !(shift.isJsonPrimitive() && shift.getAsJsonPrimitive().isNumber() && shift.getAsDouble() == 0.0)) { note.accept("shifts a noise along y, which 26.3 noise cannot, so that shift is dropped"); }
            }
            case "minecraft:invert" -> out.addProperty(TYPE, "minecraft:reciprocal");
            case "minecraft:end_islands" -> { return NoiseParity.endIslands(); }
            default -> {
                if (!SHIFTS.contains(type)) { note.accept("uses the density function " + type + ", which 26.3 does not have, so it is kept as written and will not load"); }
            }
        }
        return out;
    }

    private static String densityKey(String key, String type) {
        return switch (key) {
            case "argument1" -> "left";
            case "argument2" -> "right";
            case "argument" -> SHIFTS.contains(type) ? "noise" : INPUT;
            case "from_y" -> "minecraft:y_clamped_gradient".equals(type) ? "from_coordinate" : key;
            case "to_y" -> "minecraft:y_clamped_gradient".equals(type) ? "to_coordinate" : key;
            default -> key;
        };
    }

    private static JsonObject interpolated(JsonElement input) {
        JsonObject out = new JsonObject();
        out.addProperty(TYPE, "minecraft:interpolated");
        out.add(INPUT, input.deepCopy());
        out.addProperty("cell_size_xz", 16);
        out.addProperty("cell_size_y", 1);
        return out;
    }

    private static JsonObject exclusion(JsonElement erosion, JsonElement depth) {
        JsonObject low = binary("minecraft:sub", new JsonPrimitive(-0.225), erosion == null ? new JsonPrimitive(0.0) : erosion.deepCopy());
        JsonObject deep = binary("minecraft:max", binary("minecraft:sub", depth == null ? new JsonPrimitive(0.0) : depth.deepCopy(), new JsonPrimitive(0.9)), new JsonPrimitive(0.0));
        return binary("minecraft:min", low, deep);
    }

    private static JsonObject binary(String type, JsonElement left, JsonElement right) {
        JsonObject out = new JsonObject();
        out.addProperty(TYPE, type);
        out.add("left", left);
        out.add("right", right);
        return out;
    }

    private static void spawnLosses(JsonObject settings, Consumer<String> note) {
        if (!settings.has(SPAWN_TARGET) || !settings.get(SPAWN_TARGET).isJsonArray()) { return; }
        JsonObject router = settings.has(ROUTER) && settings.get(ROUTER).isJsonObject() ? settings.getAsJsonObject(ROUTER) : new JsonObject();
        boolean placed = false;
        boolean unnamed = false;
        for (JsonElement point : settings.getAsJsonArray(SPAWN_TARGET)) {
            if (!point.isJsonObject()) { continue; }
            for (Map.Entry<String, JsonElement> entry : point.getAsJsonObject().entrySet()) {
                String axis = SPAWN_AXES.get(entry.getKey());
                if (axis == null) { placed |= nonZero(entry.getValue()); }
                else { unnamed |= !(router.has(axis) && router.get(axis).isJsonPrimitive()); }
            }
        }
        if (placed) { note.accept("sets a spawn target depth or offset, which 26.3 spawn targets cannot hold, so they are dropped"); }
        if (unnamed) { note.accept("aims its spawn target at a noise router axis written inline, which a 26.3 spawn target cannot name, so that axis is dropped"); }
    }

    private static boolean nonZero(JsonElement value) {
        if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isNumber()) { return value.getAsDouble() != 0.0; }
        if (!value.isJsonArray()) { return true; }
        for (JsonElement item : value.getAsJsonArray()) {
            if (nonZero(item)) { return true; }
        }
        return false;
    }

    public static JsonObject noiseSettings(JsonObject settings) {
        JsonObject out = settings.deepCopy();
        boolean veinless = off(out, "ore_veins_enabled");
        if (off(out, "aquifers_enabled")) { out.remove("aquifers"); }
        out.remove("aquifers_enabled");
        out.remove("ore_veins_enabled");
        JsonElement surface = out.remove(SURFACE_RULE);
        if (surface != null) { out.add(MATERIAL_RULE, materialRule(surface)); }
        if (veinless && out.has(MATERIAL_RULE)) { out.add(MATERIAL_RULE, withoutVeins(out.get(MATERIAL_RULE))); }
        for (String key : SETTINGS_STATES) {
            if (out.has(key)) { out.add(key, blockState(out.get(key))); }
        }
        JsonObject router = out.has(ROUTER) && out.get(ROUTER).isJsonObject() ? out.getAsJsonObject(ROUTER) : null;
        if (router == null) { return out; }
        router.remove("preliminary_surface_level");
        if (out.has(SPAWN_TARGET) && out.get(SPAWN_TARGET).isJsonArray()) {
            JsonArray targets = out.getAsJsonArray(SPAWN_TARGET);
            for (int index = 0; index < targets.size(); index++) { targets.set(index, spawnTarget(targets.get(index), router)); }
        }
        return out;
    }

    public static JsonElement materialRule(JsonElement surfaceRule) {
        JsonElement out = surfaceRule.deepCopy();
        rule(out);
        return out;
    }

    public static JsonElement blockState(JsonElement state) {
        if (!state.isJsonObject()) { return state.deepCopy(); }
        JsonObject object = state.getAsJsonObject();
        if (!isOldState(object)) { return object.deepCopy(); }
        JsonObject out = new JsonObject();
        out.add("id", object.get(NAME).deepCopy());
        if (object.has(PROPERTIES)) { out.add("properties", object.get(PROPERTIES).deepCopy()); }
        return out;
    }

    public static JsonObject configured(JsonObject configured) {
        JsonElement out = configured.deepCopy();
        states(out);
        return out.getAsJsonObject();
    }

    private static void rule(JsonElement element) {
        if (!element.isJsonObject()) { return; }
        JsonObject rule = element.getAsJsonObject();
        switch (type(rule)) {
            case "minecraft:sequence" -> {
                if (rule.has(SEQUENCE) && rule.get(SEQUENCE).isJsonArray()) {
                    for (JsonElement step : rule.getAsJsonArray(SEQUENCE)) { rule(step); }
                }
            }
            case "minecraft:condition" -> {
                if (rule.has("if_true")) { condition(rule.get("if_true")); }
                if (rule.has("then_run")) { rule(rule.get("then_run")); }
            }
            case "minecraft:block" -> {
                if (rule.has("result_state")) { rule.add("result_state", blockState(rule.get("result_state"))); }
            }
            case ORE_VEIN -> {
                for (String key : VEIN_STATES) {
                    if (rule.has(key)) { rule.add(key, blockState(rule.get(key))); }
                }
            }
            default -> {}
        }
    }

    private static void condition(JsonElement element) {
        if (!element.isJsonObject()) { return; }
        JsonObject condition = element.getAsJsonObject();
        if ("minecraft:not".equals(type(condition)) && condition.has("invert")) { condition(condition.get("invert")); }
    }

    private static JsonElement withoutVeins(JsonElement element) {
        if (!element.isJsonObject()) { return element; }
        JsonObject rule = element.getAsJsonObject();
        switch (type(rule)) {
            case "minecraft:sequence" -> {
                if (rule.has(SEQUENCE) && rule.get(SEQUENCE).isJsonArray()) {
                    JsonArray kept = new JsonArray();
                    for (JsonElement step : rule.getAsJsonArray(SEQUENCE)) {
                        if (step.isJsonObject() && ORE_VEIN.equals(type(step.getAsJsonObject()))) { continue; }
                        kept.add(withoutVeins(step));
                    }
                    rule.add(SEQUENCE, kept);
                }
            }
            case "minecraft:condition" -> {
                if (rule.has("then_run")) { rule.add("then_run", withoutVeins(rule.get("then_run"))); }
            }
            default -> {}
        }
        return rule;
    }

    private static JsonElement spawnTarget(JsonElement element, JsonObject router) {
        if (!element.isJsonObject()) { return element; }
        JsonObject point = element.getAsJsonObject();
        if (SPAWN_AXES.keySet().stream().noneMatch(point::has)) { return point; }
        JsonObject out = new JsonObject();
        for (Map.Entry<String, JsonElement> entry : point.entrySet()) {
            String axis = SPAWN_AXES.get(entry.getKey());
            if (axis == null) { continue; }
            JsonElement function = router.get(axis);
            if (function != null && function.isJsonPrimitive() && function.getAsJsonPrimitive().isString()) { out.add(function.getAsString(), entry.getValue().deepCopy()); }
        }
        return out;
    }

    private static void states(JsonElement element) {
        if (element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();
            for (int index = 0; index < array.size(); index++) {
                JsonElement item = array.get(index);
                if (item.isJsonObject() && isOldState(item.getAsJsonObject())) { array.set(index, blockState(item)); }
                else { states(item); }
            }
            return;
        }
        if (!element.isJsonObject()) { return; }
        JsonObject object = element.getAsJsonObject();
        if (object.has(CONFIG) && object.get(CONFIG).isJsonObject() && object.has(TYPE) && object.size() == 2) {
            JsonObject config = object.remove(CONFIG).getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : config.entrySet()) {
                if (!object.has(entry.getKey())) { object.add(entry.getKey(), entry.getValue()); }
            }
        }
        String renamed = PROVIDERS.get(type(object));
        if (renamed != null) { object.addProperty(TYPE, renamed); }
        for (String key : List.copyOf(object.keySet())) {
            JsonElement value = object.get(key);
            if (value.isJsonObject() && isOldState(value.getAsJsonObject())) { object.add(key, blockState(value)); }
            else { states(value); }
        }
    }

    private static boolean isOldState(JsonObject object) {
        if (!object.has(NAME) || !object.get(NAME).isJsonPrimitive()) { return false; }
        for (String key : object.keySet()) {
            if (!NAME.equals(key) && !PROPERTIES.equals(key)) { return false; }
        }
        return true;
    }

    private static boolean off(JsonObject settings, String key) { return settings.has(key) && settings.get(key).isJsonPrimitive() && !settings.get(key).getAsBoolean(); }

    private static String type(JsonObject object) { return object.has(TYPE) && object.get(TYPE).isJsonPrimitive() ? object.get(TYPE).getAsString() : ""; }
}
