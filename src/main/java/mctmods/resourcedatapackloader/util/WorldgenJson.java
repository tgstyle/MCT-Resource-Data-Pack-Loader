package mctmods.resourcedatapackloader.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.List;

public final class WorldgenJson {
    private WorldgenJson() {}

    public static JsonObject anchor(String kind, int value) {
        JsonObject anchor = new JsonObject();
        anchor.addProperty(kind, value);
        return anchor;
    }

    public static JsonObject yAbove(JsonObject anchor) {
        JsonObject test = new JsonObject();
        test.addProperty("type", "minecraft:y_above");
        test.add("anchor", anchor);
        test.addProperty("surface_depth_multiplier", 0);
        test.addProperty("add_stone_depth", false);
        return test;
    }

    public static JsonObject condition(JsonObject test, JsonObject then) {
        JsonObject out = new JsonObject();
        out.addProperty("type", "minecraft:condition");
        out.add("if_true", test);
        out.add("then_run", then);
        return out;
    }

    public static JsonObject not(JsonObject test) {
        JsonObject out = new JsonObject();
        out.addProperty("type", "minecraft:not");
        out.add("invert", test);
        return out;
    }

    public static JsonObject sequence(List<JsonObject> rules) {
        JsonObject out = new JsonObject();
        out.addProperty("type", "minecraft:sequence");
        JsonArray steps = new JsonArray();
        for (JsonObject rule : rules) { steps.add(rule); }
        out.add("sequence", steps);
        return out;
    }

    public static JsonObject block(String name) {
        JsonObject state = new JsonObject();
        state.addProperty("Name", name);
        JsonObject out = new JsonObject();
        out.addProperty("type", "minecraft:block");
        out.add("result_state", state);
        return out;
    }

    public static JsonObject biomeIs(List<String> biomes) {
        JsonObject test = new JsonObject();
        test.addProperty("type", "minecraft:biome");
        JsonArray names = new JsonArray();
        for (String biome : biomes) { names.add(biome); }
        test.add("biome_is", names);
        return test;
    }

    public static JsonObject stoneDepth(boolean floor, boolean addSurfaceDepth) {
        JsonObject test = new JsonObject();
        test.addProperty("type", "minecraft:stone_depth");
        test.addProperty("offset", 0);
        test.addProperty("surface_type", floor ? "floor" : "ceiling");
        test.addProperty("add_surface_depth", addSurfaceDepth);
        test.addProperty("secondary_depth_range", 0);
        return test;
    }

    public static JsonArray sequenceOf(JsonObject settings) {
        JsonObject rule = settings.getAsJsonObject("surface_rule");
        if (rule != null && "minecraft:sequence".equals(rule.get("type").getAsString())) { return rule.getAsJsonArray("sequence"); }
        JsonArray steps = new JsonArray();
        if (rule != null) { steps.add(rule); }
        settings.add("surface_rule", sequence(List.of()));
        settings.getAsJsonObject("surface_rule").add("sequence", steps);
        return steps;
    }

    public static JsonArray range(float least, float most) {
        JsonArray out = new JsonArray();
        out.add(least);
        out.add(most);
        return out;
    }
}
