package mctmods.resourcedatapackloader.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public final class FeatureForms {
    private static final String TABLE = "/assets/resourcedatapackloader/port/features.json";
    private static final String TYPE = "type";
    private static final String FEATURE = "feature";
    private static final String FEATURES = "features";
    private static final String PLACEMENT = "placement";
    private static final String MINECRAFT = "minecraft:";
    private static final String CONFIGURED = "worldgen/configured_feature";
    private static final String NETHER = "minecraft:nether_forest_vegetation";
    private static final String SEAGRASS = "minecraft:seagrass";
    private static final String TWISTING = "minecraft:twisting_vines";
    private static final String WIDTH = "spread_width";
    private static final String HEIGHT = "spread_height";
    private static final String PROVIDER = "state_provider";
    private static final Set<String> ADDED = Set.of("minecraft:end_podium", "minecraft:projected_random_patchy_square", "minecraft:random_neighbor_spread", "minecraft:single_block_pillar", "minecraft:stepped_column_cluster");
    private static final List<String> CORALS = List.of("minecraft:coral_tree", "minecraft:coral_claw", "minecraft:coral_mushroom");
    private static final Map<String, JsonObject> FORMS = new LinkedHashMap<>();
    private static final Map<String, JsonObject> VANILLA = new HashMap<>();
    private static final List<String> REMOVED = new ArrayList<>();
    private static boolean loaded;

    private FeatureForms() {}

    public static JsonElement up(JsonElement element, LineNote note) {
        load();
        if (element.isJsonArray()) {
            JsonArray out = new JsonArray();
            for (JsonElement item : element.getAsJsonArray()) { out.add(up(item, note)); }
            return out;
        }
        if (!element.isJsonObject()) { return element.deepCopy(); }
        JsonObject out = new JsonObject();
        for (Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) { out.add(entry.getKey(), up(entry.getValue(), note)); }
        JsonObject form = FORMS.get(id(text(out, TYPE)));
        if (form != null && !out.has(FEATURE)) {
            Map<String, JsonElement> params = params(id(text(out, TYPE)), out, note);
            return params == null ? out : filled(form, params);
        }
        String reference = referenced(out);
        JsonObject vanilla = reference == null || note.carried("worldgen/feature", reference) != null ? null : VANILLA.get(reference);
        if (vanilla == null) { return out; }
        JsonObject placed = up(vanilla, note).getAsJsonObject();
        if (REMOVED.contains(reference)) { out.add(FEATURE, placed); }
        else { out.getAsJsonArray(PLACEMENT).addAll(extras(placed)); }
        return out;
    }

    public static JsonElement down(JsonElement element, LineNote note) {
        load();
        if (element.isJsonArray()) {
            JsonArray out = new JsonArray();
            for (JsonElement item : element.getAsJsonArray()) { out.add(down(item, note)); }
            return out;
        }
        if (!element.isJsonObject()) { return element.deepCopy(); }
        JsonObject in = element.getAsJsonObject();
        JsonObject matched = matched(in, note);
        if (matched != null) { return matched; }
        JsonObject grouped = "minecraft:simple_random_selector".equals(id(text(in, TYPE))) ? corals(in) : null;
        if (grouped != null) { return grouped; }
        JsonObject out = new JsonObject();
        for (Map.Entry<String, JsonElement> entry : in.entrySet()) { out.add(entry.getKey(), down(entry.getValue(), note)); }
        String type = id(text(out, TYPE));
        if ("minecraft:overlay".equals(type)) {
            note.accept("places a 26.3 overlay feature, which 26.2 writes as a sequence that stops at the first entry placing nothing");
            out.addProperty(TYPE, MINECRAFT + "sequence");
        }
        else if (ADDED.contains(type)) {
            note.accept("uses the feature " + type + ", which 26.2 does not have, so it places nothing");
            JsonObject none = new JsonObject();
            none.addProperty(TYPE, MINECRAFT + "no_op");
            return none;
        }
        else if (CORALS.contains(type) && out.has(FEATURE)) {
            note.accept("grows " + type + " from one coral block, which 26.2 cannot pin, so it grows from a random coral");
            JsonObject coral = new JsonObject();
            coral.addProperty(TYPE, type);
            return coral;
        }
        stripped(out, note);
        return out;
    }

    @Nullable private static JsonObject matched(JsonObject in, LineNote note) {
        for (Map.Entry<String, JsonObject> form : FORMS.entrySet()) {
            Map<String, JsonElement> caught = new HashMap<>();
            if (!text(form.getValue(), TYPE).equals(text(in, TYPE)) || differs(form.getValue(), in, caught)) { continue; }
            JsonObject base = base(form.getKey(), caught);
            if (base == null) { continue; }
            Map<String, JsonElement> params = params(form.getKey(), base, note);
            if (params != null && filled(form.getValue(), params).equals(in)) { return base; }
        }
        return null;
    }

    private static boolean differs(JsonElement form, JsonElement held, Map<String, JsonElement> caught) {
        if (form.isJsonPrimitive() && form.getAsJsonPrimitive().isString() && form.getAsString().startsWith("$")) {
            JsonElement seen = caught.putIfAbsent(form.getAsString().substring(1), held);
            return seen != null && !seen.equals(held);
        }
        if (form.isJsonArray() && held.isJsonArray()) {
            JsonArray left = form.getAsJsonArray();
            JsonArray right = held.getAsJsonArray();
            if (left.size() != right.size()) { return true; }
            for (int i = 0; i < left.size(); i++) {
                if (differs(left.get(i), right.get(i), caught)) { return true; }
            }
            return false;
        }
        if (form.isJsonObject() && held.isJsonObject()) {
            JsonObject left = form.getAsJsonObject();
            JsonObject right = held.getAsJsonObject();
            if (!left.keySet().equals(right.keySet())) { return true; }
            for (Map.Entry<String, JsonElement> entry : left.entrySet()) {
                if (differs(entry.getValue(), right.get(entry.getKey()), caught)) { return true; }
            }
            return false;
        }
        return !form.equals(held);
    }

    @Nullable private static JsonObject base(String type, Map<String, JsonElement> caught) {
        JsonObject base = new JsonObject();
        base.addProperty(TYPE, type);
        switch (type) {
            case "minecraft:basalt_columns" -> {
                base.add("height", caught.get("height"));
                base.add("reach", caught.get("reach"));
            }
            case NETHER -> {
                if (unfit(caught.get("wide")) || unfit(caught.get("high"))) { return null; }
                base.add(PROVIDER, caught.get(PROVIDER));
                base.addProperty(WIDTH, caught.get("wide").getAsInt() + 1);
                base.addProperty(HEIGHT, caught.get("high").getAsInt() + 1);
            }
            case SEAGRASS -> {
                if (unfit(caught.get("tall")) || unfit(caught.get("short")) || caught.get("tall").getAsInt() + caught.get("short").getAsInt() <= 0) { return null; }
                base.addProperty("probability", caught.get("tall").getAsDouble() / (caught.get("tall").getAsInt() + caught.get("short").getAsInt()));
            }
            case "minecraft:sea_pickle" -> base.add("count", caught.get("count"));
            case TWISTING -> {
                if (unfit(caught.get("w")) || unfit(caught.get("h")) || unfit(caught.get("max1"))) { return null; }
                base.add(WIDTH, caught.get("w"));
                base.add(HEIGHT, caught.get("h"));
                base.addProperty("max_height", caught.get("max1").getAsInt() + 1);
            }
            default -> { }
        }
        return base;
    }

    @Nullable private static Map<String, JsonElement> params(String type, JsonObject config, LineNote note) {
        Map<String, JsonElement> out = new HashMap<>();
        switch (type) {
            case "minecraft:basalt_columns" -> {
                if (!config.has("height") || !config.has("reach")) { return null; }
                out.put("height", config.get("height"));
                out.put("reach", config.get("reach"));
            }
            case NETHER -> {
                if (unfit(config.get(WIDTH)) || unfit(config.get(HEIGHT)) || !config.has(PROVIDER)) { return null; }
                int width = config.get(WIDTH).getAsInt();
                int height = config.get(HEIGHT).getAsInt();
                out.put(PROVIDER, config.get(PROVIDER));
                out.put("area", new JsonPrimitive(width * width));
                out.put("wide", new JsonPrimitive(width - 1));
                out.put("-wide", new JsonPrimitive(1 - width));
                out.put("high", new JsonPrimitive(height - 1));
                out.put("-high", new JsonPrimitive(1 - height));
            }
            case SEAGRASS -> {
                if (!config.has("probability") || !config.get("probability").isJsonPrimitive()) { return null; }
                double chance = config.get("probability").getAsDouble();
                int scale = 100;
                while (scale < 10000 && Math.abs(chance * scale - Math.round(chance * scale)) > 1.0E-6) { scale *= 10; }
                if (Math.abs(chance * scale - Math.round(chance * scale)) > 1.0E-6) { note.accept("grows tall seagrass at a chance of " + chance + ", which 26.3 weighs in parts of " + scale + ", so it is rounded"); }
                long tall = Math.round(chance * scale);
                out.put("tall", new JsonPrimitive(tall));
                out.put("short", new JsonPrimitive(scale - tall));
            }
            case "minecraft:sea_pickle" -> {
                if (!config.has("count")) { return null; }
                out.put("count", config.get("count"));
            }
            case TWISTING -> {
                if (unfit(config.get(WIDTH)) || unfit(config.get(HEIGHT)) || unfit(config.get("max_height"))) { return null; }
                int width = config.get(WIDTH).getAsInt();
                int height = config.get(HEIGHT).getAsInt();
                int tallest = config.get("max_height").getAsInt();
                out.put("area", new JsonPrimitive(width * width));
                out.put("w", new JsonPrimitive(width));
                out.put("-w", new JsonPrimitive(-width));
                out.put("h", new JsonPrimitive(height));
                out.put("-h", new JsonPrimitive(-height));
                out.put("max1", new JsonPrimitive(tallest - 1));
                out.put("max2", new JsonPrimitive(2 * tallest - 1));
            }
            default -> { }
        }
        return out;
    }

    private static JsonElement filled(JsonElement form, Map<String, JsonElement> params) {
        if (form.isJsonPrimitive() && form.getAsJsonPrimitive().isString() && form.getAsString().startsWith("$")) { return params.get(form.getAsString().substring(1)).deepCopy(); }
        if (form.isJsonArray()) {
            JsonArray out = new JsonArray();
            for (JsonElement item : form.getAsJsonArray()) { out.add(filled(item, params)); }
            return out;
        }
        if (!form.isJsonObject()) { return form.deepCopy(); }
        JsonObject out = new JsonObject();
        for (Map.Entry<String, JsonElement> entry : form.getAsJsonObject().entrySet()) { out.add(entry.getKey(), filled(entry.getValue(), params)); }
        return out;
    }

    @Nullable private static JsonObject corals(JsonObject selector) {
        if (!selector.has(FEATURES) || !selector.get(FEATURES).isJsonArray()) { return null; }
        List<JsonElement> left = new ArrayList<>(selector.getAsJsonArray(FEATURES).asList());
        JsonArray kept = new JsonArray();
        for (String coral : CORALS) {
            List<JsonElement> group = FORMS.get(coral).getAsJsonArray(FEATURES).asList();
            if (!new HashSet<>(left).containsAll(group)) { continue; }
            group.forEach(left::remove);
            JsonObject grown = new JsonObject();
            grown.addProperty(TYPE, coral);
            JsonObject entry = new JsonObject();
            entry.add(FEATURE, grown);
            entry.add(PLACEMENT, new JsonArray());
            kept.add(entry);
        }
        if (kept.isEmpty() || !left.isEmpty()) { return null; }
        if (kept.size() == 1) { return kept.get(0).getAsJsonObject().getAsJsonObject(FEATURE); }
        JsonObject out = new JsonObject();
        out.addProperty(TYPE, MINECRAFT + "simple_random_selector");
        out.add(FEATURES, kept);
        return out;
    }

    private static void stripped(JsonObject placed, LineNote note) {
        String reference = referenced(placed);
        if (reference == null || REMOVED.contains(reference) || !VANILLA.containsKey(reference) || note.carried(CONFIGURED, reference) != null) { return; }
        List<JsonElement> extras = extras(up(VANILLA.get(reference), note).getAsJsonObject()).asList();
        List<JsonElement> placement = placed.getAsJsonArray(PLACEMENT).asList();
        int from = placement.size() - extras.size();
        if (from < 0 || !placement.subList(from, placement.size()).equals(extras)) { return; }
        JsonArray out = new JsonArray();
        for (int i = 0; i < from; i++) { out.add(placement.get(i)); }
        placed.add(PLACEMENT, out);
    }

    private static JsonArray extras(JsonObject overlay) {
        JsonArray features = overlay.getAsJsonArray(FEATURES);
        return features == null || features.isEmpty() ? new JsonArray() : features.get(0).getAsJsonObject().getAsJsonArray(PLACEMENT).deepCopy();
    }

    @Nullable private static String referenced(JsonObject placed) {
        if (!placed.has(PLACEMENT) || !placed.get(PLACEMENT).isJsonArray()) { return null; }
        String feature = text(placed, FEATURE);
        return feature.isEmpty() || feature.startsWith("#") ? null : id(feature);
    }

    private static boolean unfit(@Nullable JsonElement value) { return value == null || !value.isJsonPrimitive() || !value.getAsJsonPrimitive().isNumber() || value.getAsDouble() != Math.rint(value.getAsDouble()); }

    private static String id(String id) { return id.isEmpty() || id.contains(":") ? id : MINECRAFT + id; }

    private static String text(JsonObject object, String key) { return object.has(key) && object.get(key).isJsonPrimitive() ? object.get(key).getAsString() : ""; }

    private static synchronized void load() {
        if (loaded) { return; }
        loaded = true;
        try (InputStream stream = FeatureForms.class.getResourceAsStream(TABLE)) {
            if (stream == null) {
                ContentLog.LOGGER.error("The feature form table {} is missing from the jar, so features removed in 26.3 are not rewritten", TABLE);
                return;
            }
            JsonObject table = JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonObject();
            table.getAsJsonObject("forms").entrySet().forEach(entry -> FORMS.put(entry.getKey(), entry.getValue().getAsJsonObject()));
            table.getAsJsonObject("vanilla").entrySet().forEach(entry -> VANILLA.put(entry.getKey(), entry.getValue().getAsJsonObject()));
            table.getAsJsonArray("removed").forEach(entry -> REMOVED.add(entry.getAsString()));
        }
        catch (IOException | RuntimeException failed) { ContentLog.LOGGER.error("Could not read the feature form table {}", TABLE, failed); }
    }
}
