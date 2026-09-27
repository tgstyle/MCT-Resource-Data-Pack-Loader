package mctmods.resourcedatapackloader.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.List;

public final class NumberUp {
    private static final String TYPE = "type";
    private static final String MINECRAFT = "minecraft:";
    private static final String INPUT = "input";
    private static final String VALUE = "value";

    private NumberUp() {}

    public static JsonElement fitted(JsonElement element) { return NumberSlots.walk(element, NumberUp::number); }

    private static JsonElement number(JsonElement value, boolean whole) {
        if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isNumber()) { return whole ? new JsonPrimitive(Math.round(value.getAsFloat())) : value.deepCopy(); }
        if (!value.isJsonObject()) { return value.deepCopy(); }
        JsonObject out = value.getAsJsonObject().deepCopy();
        String type = NumberSlots.type(out);
        if (type.isEmpty() && (out.has("min") || out.has("max"))) {
            type = "uniform";
            out.addProperty(TYPE, MINECRAFT + type);
        }
        switch (type) {
            case "constant" -> {
                if (whole && out.has(VALUE)) { out.addProperty(VALUE, Math.round(out.get(VALUE).getAsFloat())); }
            }
            case "uniform" -> {
                for (String key : List.of("min", "max")) {
                    if (out.has(key)) { out.add(key, number(out.get(key), whole)); }
                }
            }
            case "add" -> {
                if (out.has("inputs") && out.get("inputs").isJsonArray()) {
                    JsonArray inputs = new JsonArray();
                    for (JsonElement item : out.getAsJsonArray("inputs")) { inputs.add(number(item, whole)); }
                    out.add("inputs", inputs);
                }
            }
            case "binomial" -> {
                if (out.has("n")) { out.add("n", number(out.get("n"), true)); }
                if (out.has("p")) { out.add("p", number(out.get("p"), false)); }
                return whole ? out : unary("from_int", out);
            }
            case "score" -> {
                JsonElement scale = out.remove("scale");
                boolean scaled = scale != null && !(scale.isJsonPrimitive() && scale.getAsDouble() == 1.0);
                if (!scaled) { return whole ? out : unary("from_int", out); }
                JsonObject product = new JsonObject();
                product.addProperty(TYPE, MINECRAFT + "mul");
                product.add("left", unary("from_int", out));
                product.add("right", scale);
                return whole ? unary("from_float", unary("round", product)) : product;
            }
            case "enchantment_level" -> {
                if (whole) { return unary("from_float", unary("round", out)); }
            }
            default -> { }
        }
        return out;
    }

    private static JsonObject unary(String type, JsonElement input) {
        JsonObject out = new JsonObject();
        out.addProperty(TYPE, MINECRAFT + type);
        out.add(INPUT, input);
        return out;
    }
}
