package mctmods.resourcedatapackloader.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public final class WorldgenDown {
    private static final String TYPE = "type";
    private static final String CONFIG = "config";
    private static final String ID = "id";
    private static final String PROPERTIES = "properties";
    private static final String ROUTER = "noise_router";
    private static final String SEQUENCE = "sequence";
    private static final String SPAWN_TARGET = "spawn_target";
    private static final String ARGUMENT = "argument";
    private static final String MINECRAFT = "minecraft:";
    private static final String ATTRIBUTES = "attributes";
    private static final String COUNT = "count";
    private static final String START = "start_vertical_radius_multiplier";
    private static final String CAVE_COUNT = "{\"type\":\"minecraft:very_biased_to_bottom\",\"max_inclusive\":14,\"min_inclusive\":0}";
    private static final String NETHER_COUNT = "{\"type\":\"minecraft:very_biased_to_bottom\",\"max_inclusive\":9,\"min_inclusive\":0}";
    private static final String CAVE_THICKNESS = "{\"type\":\"minecraft:trapezoid\",\"max\":3.0,\"min\":0.0,\"plateau\":1.0}";
    private static final String NETHER_THICKNESS = "{\"type\":\"minecraft:trapezoid\",\"max\":6.0,\"min\":0.0,\"plateau\":2.0}";
    private static final List<String> CATEGORIES = Arrays.asList("ambient", "axolotls", "creature", "misc", "monster", "underground_water_creature", "water_ambient", "water_creature");
    private static final Set<String> STATE_KEYS = set("result_state", "state", "output_state", "block_state", "default_fluid", "default_block", "pointed_block", "base_block", "valid_base_block", "stem_state", "hat_state", "decor_state", "water_state", "lava_state", "barrier_state", "air_state", "rim", "default_state", "contents", "states", "high_states", "low_states", "inner_placements", "target");
    private static final Set<String> PROVIDER_KEYS = set("to_place", "then", "fallback", "ground_state", "provider", "fluid", "barrier", "source");
    private static final List<String> SETTINGS_STATES = Arrays.asList("default_block", "default_fluid");
    private static final List<String> AQUIFER_KEYS = Arrays.asList("barrier", "fluid_level_floodedness", "fluid_level_spread", "lava");
    private static final List<String> CLIMATE_KEYS = Arrays.asList("temperature", "vegetation", "continents", "erosion", "depth", "ridges");
    private static final List<String> VEIN_KEYS = Arrays.asList("vein_toggle", "vein_ridged", "vein_gap");
    private static final List<String> SHIFTED = Arrays.asList("shift_x", "shift_y", "shift_z");
    private static final List<String> TARGET_AXES = Arrays.asList("temperature", "humidity", "continentalness", "erosion", "weirdness");
    private static final Map<String, String[]> PROVIDERS = providers();
    private static final Map<String, String> SPAWN_AXES = spawnAxes();
    private static final Set<String> BINARY = set("add", "mul", "min", "max", "sub", "div");
    private static final Set<String> UNARY = set("abs", "square", "cube", "half_negative", "quarter_negative", "squeeze", "reciprocal", "negate", "blend_density", "interpolated", "cache");
    private static final Set<String> SHIFTS = set("shift_a", "shift_b", "shift");
    private static final Set<String> CARRIED = set("constant", "noise", "abs", "square", "cube", "half_negative", "quarter_negative", "squeeze", "add", "mul", "min", "max", "spline", "clamp", "range_choice", "interval_select", "blend_density", "blend_alpha", "blend_offset", "beardifier", "find_top_surface", "old_blended_noise", "shift_a", "shift_b", "shift");

    private WorldgenDown() {}

    private static Set<String> set(String... values) { return new HashSet<>(Arrays.asList(values)); }

    private static Map<String, String[]> providers() {
        Map<String, String[]> out = new HashMap<>();
        out.put("minecraft:simple", new String[] {"minecraft:simple_state_provider", "state"});
        out.put("minecraft:weighted", new String[] {"minecraft:weighted_state_provider", "entries"});
        out.put("minecraft:noise", new String[] {"minecraft:noise_provider", "states"});
        out.put("minecraft:dual_noise", new String[] {"minecraft:dual_noise_provider", "variety"});
        out.put("minecraft:rotated", new String[] {"minecraft:rotated_block_provider", "state"});
        out.put("minecraft:noise_threshold", new String[] {"minecraft:noise_threshold_provider", "default_state"});
        out.put("minecraft:randomized_int", new String[] {"minecraft:randomized_int_state_provider", "property"});
        out.put("minecraft:rule_based", new String[] {"minecraft:rule_based_state_provider", "rules"});
        return out;
    }

    private static Map<String, String> spawnAxes() {
        Map<String, String> out = new LinkedHashMap<>();
        out.put("temperature", "temperature");
        out.put("vegetation", "humidity");
        out.put("continents", "continentalness");
        out.put("erosion", "erosion");
        out.put("ridges", "weirdness");
        return out;
    }

    public static JsonObject configured(JsonObject configured, Consumer<String> note) { return unflattened(ported(configured, note)); }

    public static JsonObject carver(JsonObject carver, Consumer<String> note) {
        String type = type(carver);
        JsonObject out = JsonTree.copy(carver);
        boolean nether = out.has(START) && !out.get(START).equals(new JsonPrimitive(1.0));
        if ("minecraft:canyon".equals(type)) {
            JsonElement scale = out.has("shape") && out.get("shape").isJsonObject() ? out.getAsJsonObject("shape").remove("y_scale") : null;
            if (scale != null) { out.add("yScale", scale); }
        }
        else if ("minecraft:cave".equals(type)) {
            JsonElement count = out.remove(COUNT);
            JsonElement thickness = out.remove("thickness");
            JsonElement weird = out.remove("weird_thickness_bias");
            JsonElement start = out.remove(START);
            boolean biased = weird != null && weird.getAsBoolean();
            boolean vanilla = nether ? parsed(NETHER_COUNT).equals(count) && parsed(NETHER_THICKNESS).equals(thickness) && !biased && new JsonPrimitive(5.0).equals(start) : parsed(CAVE_COUNT).equals(count) && parsed(CAVE_THICKNESS).equals(thickness) && biased;
            if (!vanilla) { note.accept("sets its own cave count, thickness or start height, which 26.2 cannot, so the vanilla " + (nether ? "nether" : "overworld") + " cave shape is used"); }
            if (nether) { out.addProperty(TYPE, MINECRAFT + "nether_cave"); }
            JsonElement room = out.remove("room_vertical_radius_multiplier");
            if (room != null) { out.add("yScale", room); }
        }
        else { return out; }
        out.add("lava_level", parsed(nether ? "{\"above_bottom\":10}" : "{\"above_bottom\":8}"));
        out.addProperty("replaceable", nether ? "#minecraft:nether_carver_replaceables" : "#minecraft:overworld_carver_replaceables");
        return out;
    }

    private static JsonElement parsed(String json) { return new JsonParser().parse(json); }

    public static JsonObject ported(JsonObject json, Consumer<String> note) { return placed(states(json, note), note).getAsJsonObject(); }

    public static JsonObject biome(JsonObject biome, Consumer<String> note) {
        JsonObject out = ported(biome, note);
        JsonObject attributes = out.has(ATTRIBUTES) && out.get(ATTRIBUTES).isJsonObject() ? out.getAsJsonObject(ATTRIBUTES) : new JsonObject();
        JsonElement spawns = attributes.remove("minecraft:gameplay/natural_mob_spawns");
        JsonObject settings = spawns != null && spawns.isJsonObject() && spawns.getAsJsonObject().has("argument") ? object(spawns.getAsJsonObject(), "argument") : spawns != null && spawns.isJsonObject() ? spawns.getAsJsonObject() : new JsonObject();
        JsonObject categories = object(settings, "spawns_by_category");
        JsonObject spawners = new JsonObject();
        for (String category : CATEGORIES) { spawners.add(category, categories.has(category) ? categories.get(category) : new JsonArray()); }
        out.add("spawners", spawners);
        out.add("spawn_costs", object(settings, "spawn_costs"));
        JsonElement probability = attributes.remove("minecraft:gameplay/creature_world_gen_spawn_probability");
        if (probability != null && probability.isJsonPrimitive()) { out.add("creature_spawn_probability", probability); }
        else if (probability != null) { note.accept("changes its creature spawn chance by a modifier, which 26.2 cannot, so the default is kept"); }
        for (Map.Entry<String, JsonElement> entry : attributes.entrySet()) {
            JsonElement value = entry.getValue();
            if (value.isJsonObject() && value.getAsJsonObject().has("argument") && "append".equals(text(value.getAsJsonObject(), "modifier"))) {
                note.accept("appends to its dimension's " + entry.getKey() + ", which 26.2 cannot, so the biome's own replace them");
                entry.setValue(value.getAsJsonObject().get("argument"));
            }
        }
        if (attributes.size() == 0) { out.remove(ATTRIBUTES); }
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
        boolean offset = "minecraft:offset".equals(type(in));
        boolean spawn = in.has("weight") && in.has(COUNT) && in.has(TYPE);
        JsonObject out = new JsonObject();
        for (Map.Entry<String, JsonElement> entry : in.entrySet()) {
            String key = entry.getKey();
            if (offset && ("x".equals(key) || "z".equals(key))) { continue; }
            if (offset && "y".equals(key)) { out.add("y_spread", entry.getValue()); }
            else if (spawn && COUNT.equals(key)) { counts(out, entry.getValue()); }
            else { out.add(key, placed(entry.getValue(), note)); }
        }
        if (offset) {
            out.addProperty(TYPE, MINECRAFT + "random_offset");
            JsonElement x = in.get("x");
            out.add("xz_spread", x == null ? new JsonPrimitive(0) : x);
            if (x != null && !x.equals(in.get("z"))) { note.accept("offsets x and z by different amounts, which 26.2 cannot, so both use the x offset"); }
        }
        return noise(out, note);
    }

    private static JsonObject noise(JsonObject noise, Consumer<String> note) {
        if (!noise.has("base_octave") || noise.has(TYPE)) { return noise; }
        int count = noise.has("octave_count") ? noise.get("octave_count").getAsInt() : 1;
        List<Double> modifiers = new ArrayList<>();
        JsonElement listed = noise.get("amplitude_modifiers");
        for (int i = 0; i < count; i++) { modifiers.add(listed != null && listed.isJsonArray() && i < listed.getAsJsonArray().size() ? listed.getAsJsonArray().get(i).getAsDouble() : 1.0); }
        double base = noise.has("base_amplitude") ? noise.get("base_amplitude").getAsDouble() : 1.0;
        double scale = base / NoiseParity.baseAmplitude(modifiers);
        if (noise.has("normalize") && !(noise.get("normalize").isJsonPrimitive() && noise.get("normalize").getAsJsonPrimitive().isBoolean() && noise.get("normalize").getAsBoolean())) { note.accept("sets its own noise normalization, which 26.2 cannot, so the standard one is used"); }
        JsonArray amplitudes = new JsonArray();
        for (double modifier : modifiers) { amplitudes.add(Math.abs(scale - 1.0) < 1.0E-9 ? modifier : modifier * scale); }
        JsonObject out = new JsonObject();
        out.add("amplitudes", amplitudes);
        out.add("firstOctave", noise.get("base_octave"));
        return out;
    }

    private static void counts(JsonObject out, JsonElement count) {
        if (count.isJsonObject() && "uniform".equals(bare(type(count.getAsJsonObject())))) {
            out.add("minCount", count.getAsJsonObject().get("min_inclusive"));
            out.add("maxCount", count.getAsJsonObject().get("max_inclusive"));
        }
        else {
            out.add("minCount", count);
            out.add("maxCount", count);
        }
    }

    public static JsonElement states(JsonElement element, Consumer<String> note) {
        if (element.isJsonArray()) {
            JsonArray out = new JsonArray();
            for (JsonElement item : element.getAsJsonArray()) { out.add(states(item, note)); }
            return out;
        }
        if (!element.isJsonObject()) { return JsonTree.copy(element); }
        JsonObject in = element.getAsJsonObject();
        if (newState(in)) {
            JsonObject out = new JsonObject();
            out.add("Name", JsonTree.copy(in.get(ID)));
            if (in.has(PROPERTIES)) { out.add("Properties", JsonTree.copy(in.get(PROPERTIES))); }
            return out;
        }
        JsonObject out = new JsonObject();
        for (Map.Entry<String, JsonElement> entry : in.entrySet()) {
            String key = entry.getKey();
            JsonElement value = "templates".equals(key) ? JsonTree.copy(entry.getValue()) : states(entry.getValue(), note);
            if (STATE_KEYS.contains(key) || "entries".equals(key) && type(in).endsWith("weighted")) { value = named(value, "entries".equals(key)); }
            if (providing(key, in)) { value = provided(value, key, note); }
            out.add(key, "feature".equals(key) && value.isJsonObject() && value.getAsJsonObject().has(TYPE) ? unflattened(value.getAsJsonObject()) : value);
        }
        String[] renamed = PROVIDERS.get(type(out));
        if (renamed != null && out.has(renamed[1])) { out.addProperty(TYPE, renamed[0]); }
        return out;
    }

    public static boolean providing(String key, JsonObject owner) { return key.endsWith("_provider") || PROVIDER_KEYS.contains(key) || "block_state".equals(key) && type(owner).startsWith(MINECRAFT + "replace_"); }

    private static JsonElement named(JsonElement value, boolean entries) {
        if (value.isJsonArray()) {
            JsonArray out = new JsonArray();
            for (JsonElement item : value.getAsJsonArray()) {
                if (entries && item.isJsonObject() && item.getAsJsonObject().has("data")) { item.getAsJsonObject().add("data", named(item.getAsJsonObject().get("data"), false)); }
                out.add(entries ? item : named(item, false));
            }
            return out;
        }
        if (!value.isJsonPrimitive() || !value.getAsJsonPrimitive().isString()) { return value; }
        JsonObject out = new JsonObject();
        out.add("Name", value);
        return out;
    }

    private static JsonElement provided(JsonElement value, String key, Consumer<String> note) {
        if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isString() && !"fallback".equals(key)) {
            note.accept("names the block state provider " + value.getAsString() + ", which 26.2 has no registry for, so it is kept as written and will not load");
            return value;
        }
        if (!value.isJsonObject() || !value.getAsJsonObject().has("Name")) { return value; }
        JsonObject out = new JsonObject();
        out.addProperty(TYPE, MINECRAFT + "simple_state_provider");
        out.add("state", value);
        return out;
    }

    public static JsonObject noiseSettings(JsonObject settings, Consumer<String> note) {
        JsonObject out = new JsonObject();
        JsonObject router = object(settings, ROUTER);
        JsonObject aquifers = settings.has("aquifers") && settings.get("aquifers").isJsonObject() ? settings.getAsJsonObject("aquifers") : null;
        for (Map.Entry<String, JsonElement> entry : settings.entrySet()) {
            switch (entry.getKey()) {
                case "material_rule":
                    out.add("surface_rule", surfaceRule(entry.getValue(), note));
                    break;
                case "aquifers":
                case ROUTER:
                case SPAWN_TARGET: break;
                case "debug_functions":
                    note.accept("lists debug density functions, which 26.2 does not have, so they are dropped");
                    break;
                default: out.add(entry.getKey(), SETTINGS_STATES.contains(entry.getKey()) ? named(states(entry.getValue(), note), false) : JsonTree.copy(entry.getValue()));
            }
        }
        out.addProperty("aquifers_enabled", aquifers != null);
        out.addProperty("ore_veins_enabled", false);
        if (aquifers != null && (aquifers.has("exclusion") || aquifers.has("surface_level"))) { note.accept("sets its aquifer exclusion and surface level, which 26.2 builds in, so its own are dropped"); }
        JsonObject routed = new JsonObject();
        for (String key : AQUIFER_KEYS) { routed.add(key, aquifers != null && aquifers.has(key) ? density(aquifers.get(key), note) : new JsonPrimitive(0.0)); }
        for (String key : CLIMATE_KEYS) { routed.add(key, router.has(key) ? density(router.get(key), note) : new JsonPrimitive(0.0)); }
        routed.add("preliminary_surface_level", router.has("chunk_surface_level") ? density(uninterpolated(router.get("chunk_surface_level")), note) : new JsonPrimitive(0.0));
        routed.add("final_density", router.has("final_density") ? density(router.get("final_density"), note) : new JsonPrimitive(0.0));
        for (String key : VEIN_KEYS) { routed.addProperty(key, 0.0); }
        out.add(ROUTER, routed);
        out.add(SPAWN_TARGET, spawnTargets(settings.get(SPAWN_TARGET), router, note));
        return out;
    }

    public static JsonElement density(JsonElement element, Consumer<String> note) {
        if (element.isJsonArray()) {
            JsonArray out = new JsonArray();
            for (JsonElement item : element.getAsJsonArray()) { out.add(density(item, note)); }
            return out;
        }
        if (NoiseParity.isEndIslands(element)) {
            JsonObject islands = new JsonObject();
            islands.addProperty(TYPE, MINECRAFT + "end_islands");
            return islands;
        }
        if (!element.isJsonObject()) { return JsonTree.copy(element); }
        JsonObject in = element.getAsJsonObject();
        String type = bare(type(in));
        JsonObject out = new JsonObject();
        for (Map.Entry<String, JsonElement> entry : in.entrySet()) { out.add(type.isEmpty() ? entry.getKey() : densityKey(entry.getKey(), type), density(entry.getValue(), note)); }
        if (type.isEmpty()) { return noise(out, note); }
        if (CARRIED.contains(type) && !"noise".equals(type)) { return out; }
        switch (type) {
            case "noise":
                if (out.has("shift_x") || out.has("shift_z")) {
                    out.addProperty(TYPE, MINECRAFT + "shifted_noise");
                    for (String key : SHIFTED) {
                        if (!out.has(key)) { out.addProperty(key, 0.0); }
                    }
                }
                break;
            case "cache":
                out.addProperty(TYPE, MINECRAFT + "cache_once");
                break;
            case "interpolated":
                out.remove("cell_size_xz");
                out.remove("cell_size_y");
                break;
            case "reciprocal":
                out.addProperty(TYPE, MINECRAFT + "invert");
                break;
            case "negate": return binary("mul", new JsonPrimitive(-1.0), out.get(ARGUMENT));
            case "sub": return binary("add", out.get("argument1"), binary("mul", new JsonPrimitive(-1.0), out.get("argument2")));
            case "div": {
                JsonElement right = out.get("argument2");
                if (right != null && right.isJsonPrimitive() && right.getAsJsonPrimitive().isNumber() && right.getAsDouble() != 0.0) { return binary("mul", out.get("argument1"), new JsonPrimitive(1.0 / right.getAsDouble())); }
                return dropped("divides by a density function", note);
            }
            case "pow": {
                JsonElement exponent = out.get("exponent");
                double power = exponent != null && exponent.isJsonPrimitive() && exponent.getAsJsonPrimitive().isNumber() ? exponent.getAsDouble() : Double.NaN;
                if (power == 1.0) { return out.get("base"); }
                if (power != 2.0 && power != 3.0) { return dropped("raises a density function to a power other than 1, 2 or 3", note); }
                JsonObject raised = new JsonObject();
                raised.addProperty(TYPE, MINECRAFT + (power == 2.0 ? "square" : "cube"));
                raised.add(ARGUMENT, out.get("base"));
                return raised;
            }
            case "slice": {
                if (!"y".equals(text(out, "axis")) || !"0".equals(text(out, "coordinate"))) { return dropped("slices a density function at a fixed x, z or y other than 0", note); }
                note.accept("slices a density function at y 0, which 26.2 writes as a flat cache sampled every 4 blocks");
                JsonObject flat = new JsonObject();
                flat.addProperty(TYPE, MINECRAFT + "flat_cache");
                flat.add(ARGUMENT, out.get("input"));
                return flat;
            }
            case "lerp": {
                JsonElement first = out.get("first");
                JsonElement span = binary("add", out.get("second"), binary("mul", new JsonPrimitive(-1.0), first));
                return binary("add", first, binary("mul", out.get("alpha"), span));
            }
            case "gradient":
                if (!out.has("axis") || !"y".equals(out.get("axis").getAsString())) { return dropped("uses a gradient along x or z", note); }
                out.addProperty(TYPE, MINECRAFT + "y_clamped_gradient");
                out.remove("axis");
                break;
            default:
                if (!type.contains(":")) { return dropped("uses the density function " + type, note); }
        }
        return out;
    }

    private static JsonElement dropped(String what, Consumer<String> note) {
        note.accept(what + ", which 26.2 has no form for, so that function is dropped to 0");
        return new JsonPrimitive(0.0);
    }

    private static String densityKey(String key, String type) {
        switch (key) {
            case "left": return BINARY.contains(type) ? "argument1" : key;
            case "right": return BINARY.contains(type) ? "argument2" : key;
            case "input": return UNARY.contains(type) ? ARGUMENT : key;
            case "noise": return SHIFTS.contains(type) ? ARGUMENT : key;
            case "from_coordinate": return "gradient".equals(type) ? "from_y" : key;
            case "to_coordinate": return "gradient".equals(type) ? "to_y" : key;
            default: return key;
        }
    }

    private static JsonObject binary(String type, JsonElement left, JsonElement right) {
        JsonObject out = new JsonObject();
        out.addProperty(TYPE, MINECRAFT + type);
        out.add("argument1", left == null ? new JsonPrimitive(0.0) : left);
        out.add("argument2", right == null ? new JsonPrimitive(0.0) : right);
        return out;
    }

    private static JsonElement uninterpolated(JsonElement surface) {
        if (!surface.isJsonObject()) { return surface; }
        JsonObject held = surface.getAsJsonObject();
        return "interpolated".equals(bare(type(held))) && held.has("input") ? held.get("input") : surface;
    }

    private static JsonElement surfaceRule(JsonElement rule, Consumer<String> note) {
        if (!rule.isJsonObject()) {
            note.accept("names its material rule by id, which 26.2 cannot, so it is kept as written and will not load");
            return JsonTree.copy(rule);
        }
        return withoutVeins(states(rule, note), note);
    }

    private static JsonElement withoutVeins(JsonElement element, Consumer<String> note) {
        if (!element.isJsonObject()) { return element; }
        JsonObject rule = element.getAsJsonObject();
        if (rule.has(SEQUENCE) && rule.get(SEQUENCE).isJsonArray()) {
            JsonArray kept = new JsonArray();
            for (JsonElement step : rule.getAsJsonArray(SEQUENCE)) {
                if (step.isJsonObject() && "ore_vein".equals(bare(type(step.getAsJsonObject())))) {
                    note.accept("places ore veins through its material rule, which 26.2 cannot, so they are dropped");
                    continue;
                }
                kept.add(withoutVeins(step, note));
            }
            rule.add(SEQUENCE, kept);
        }
        if (rule.has("then_run")) { rule.add("then_run", withoutVeins(rule.get("then_run"), note)); }
        return rule;
    }

    private static JsonArray spawnTargets(JsonElement targets, JsonObject router, Consumer<String> note) {
        JsonArray out = new JsonArray();
        if (targets == null || !targets.isJsonArray()) { return out; }
        for (JsonElement target : targets.getAsJsonArray()) {
            if (!target.isJsonObject()) { continue; }
            JsonObject point = new JsonObject();
            for (String axis : TARGET_AXES) { point.add(axis, range()); }
            for (Map.Entry<String, JsonElement> entry : target.getAsJsonObject().entrySet()) {
                String axis = axis(router, entry.getKey());
                if (axis == null) { note.accept("aims its spawn target at " + entry.getKey() + ", which is not one of its noise router's climate axes, so 26.2 drops it"); }
                else { point.add(axis, JsonTree.copy(entry.getValue())); }
            }
            point.addProperty("depth", 0.0);
            point.addProperty("offset", 0.0);
            out.add(point);
        }
        return out;
    }

    private static String axis(JsonObject router, String function) {
        for (Map.Entry<String, String> entry : SPAWN_AXES.entrySet()) {
            JsonElement held = router.get(entry.getKey());
            if (held != null && held.isJsonPrimitive() && function.equals(held.getAsString())) { return entry.getValue(); }
        }
        return null;
    }

    private static JsonArray range() {
        JsonArray out = new JsonArray();
        out.add(-1.0);
        out.add(1.0);
        return out;
    }

    private static JsonObject unflattened(JsonObject flat) {
        if (flat.has(CONFIG) && flat.size() == 2) { return flat; }
        JsonObject out = new JsonObject();
        JsonObject config = new JsonObject();
        for (Map.Entry<String, JsonElement> entry : flat.entrySet()) {
            if (TYPE.equals(entry.getKey())) { out.add(TYPE, entry.getValue()); }
            else { config.add(entry.getKey(), entry.getValue()); }
        }
        out.add(CONFIG, config);
        return out;
    }

    private static boolean newState(JsonObject object) {
        if (!object.has(ID) || !object.get(ID).isJsonPrimitive()) { return false; }
        for (String key : JsonTree.keys(object)) {
            if (!ID.equals(key) && !PROPERTIES.equals(key)) { return false; }
        }
        return true;
    }

    private static JsonObject object(JsonObject parent, String key) { return parent.has(key) && parent.get(key).isJsonObject() ? parent.getAsJsonObject(key) : new JsonObject(); }

    private static String type(JsonObject object) { return text(object, TYPE); }

    private static String text(JsonObject object, String key) { return object.has(key) && object.get(key).isJsonPrimitive() ? object.get(key).getAsString() : ""; }

    private static String bare(String id) { return id.startsWith(MINECRAFT) ? id.substring(MINECRAFT.length()) : id; }
}
