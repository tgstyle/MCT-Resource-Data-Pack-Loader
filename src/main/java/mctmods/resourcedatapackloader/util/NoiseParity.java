package mctmods.resourcedatapackloader.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.List;

public final class NoiseParity {
    private static final double LAYER_DEVIATION = 0.2702247831245211;
    private static final double THIRD = 0.3333333333333333;
    private static final String END_ISLANDS = "{\"type\":\"minecraft:cache\",\"input\":{\"type\":\"minecraft:max\",\"left\":{\"type\":\"minecraft:slice\",\"axis\":\"y\",\"coordinate\":0,\"input\":{\"type\":\"minecraft:mul\",\"left\":{\"type\":\"minecraft:sub\",\"left\":{\"type\":\"minecraft:clamp\",\"input\":{\"type\":\"minecraft:sub\",\"left\":100.0,\"right\":{\"type\":\"minecraft:distance_to_point\",\"metric\":\"euclidean\",\"point\":[0,0,0]}},\"max\":80.0,\"min\":-100.0},\"right\":8.0},\"right\":0.0078125}},\"right\":{\"type\":\"minecraft:end_outer_islands\"}}}";

    private NoiseParity() {}

    public static JsonObject endIslands() { return JsonParser.parseString(END_ISLANDS).getAsJsonObject(); }

    public static boolean isEndIslands(JsonElement element) {
        JsonObject form = endIslands();
        return element.equals(form) || element.equals(form.get("input")) || element.isJsonPrimitive() && "minecraft:end/islands".equals(element.getAsString());
    }

    public static double baseAmplitude(List<Double> modifiers) {
        int count = modifiers.size();
        double amplitude = Math.pow(0.5, -(count - 1)) / (Math.pow(0.5, -count) - 1.0);
        double target = 0.0;
        double variance = 0.0;
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < count; i++) {
            double modifier = modifiers.get(i);
            if (modifier != 0.0) {
                double layer = Math.abs(amplitude * modifier);
                target += layer;
                double deviation = LAYER_DEVIATION * layer;
                variance += deviation * deviation;
                min = Math.min(min, i);
                max = Math.max(max, i);
            }
            amplitude *= 0.5;
        }
        if (variance == 0.0) { return 1.0; }
        double factor = target * THIRD / (Math.sqrt(variance) * Math.sqrt(2.0));
        double parity = 0.5 * THIRD / (0.1 * (1.0 + 1.0 / (max - min + 1)));
        return parity / factor;
    }
}
