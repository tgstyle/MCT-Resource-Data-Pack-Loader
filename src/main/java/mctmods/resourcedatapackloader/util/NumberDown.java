package mctmods.resourcedatapackloader.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nullable;

public final class NumberDown {
    private static final String INPUT = "input";
    private static final String INPUTS = "inputs";
    private static final String VALUE = "value";
    private static final String FALLBACK = "fallback";
    private static final Set<String> KEPT = new HashSet<>(Arrays.asList("constant", "storage", "score", "enchantment_level", "environment_attribute"));
    private static final int DEPTH = 8;

    private NumberDown() {}

    public static JsonElement fitted(JsonElement element, LineNote note) { return NumberSlots.walk(element, (value, whole) -> number(value, whole, note, 0)); }

    private static JsonArray listed(JsonArray values, boolean whole, LineNote note, int depth) {
        JsonArray out = new JsonArray();
        for (JsonElement item : values) { out.add(number(item, whole, note, depth)); }
        return out;
    }

    private static JsonElement number(JsonElement value, boolean whole, LineNote note, int depth) {
        if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isString()) {
            JsonElement carried = depth < DEPTH ? note.carried(whole ? "context_int_provider" : "context_float_provider", value.getAsString()) : null;
            if (carried != null) { return number(carried, whole, note, depth + 1); }
            note.accept("names the number provider " + value.getAsString() + ", which the pack does not carry and 26.2 has no registry for, so it is dropped to 0");
            return new JsonPrimitive(0);
        }
        if (!value.isJsonObject()) { return JsonTree.copy(value); }
        JsonObject out = JsonTree.copy(value.getAsJsonObject());
        String type = NumberSlots.type(out);
        if (KEPT.contains(type) || type.isEmpty() || type.contains(":")) { return fallback(out, note); }
        switch (type) {
            case "from_int":
            case "from_float": {
                JsonElement input = out.get(INPUT);
                if (input == null) { break; }
                boolean rounded = input.isJsonObject() && "round".equals(NumberSlots.type(input.getAsJsonObject())) && input.getAsJsonObject().has(INPUT);
                if ("from_float".equals(type) && !rounded) { note.accept("truncates a number to a whole one, which 26.2 rounds instead"); }
                return number(rounded ? input.getAsJsonObject().get(INPUT) : input, "from_int".equals(type), note, depth);
            }
            case "uniform": {
                for (String key : new String[] {"min", "max"}) {
                    if (out.has(key)) { out.add(key, number(out.get(key), whole, note, depth)); }
                }
                return out;
            }
            case "add": {
                if (out.has(INPUTS) && out.get(INPUTS).isJsonArray()) { out.add(INPUTS, listed(out.getAsJsonArray(INPUTS), whole, note, depth)); }
                return out;
            }
            case "binomial": {
                if (out.has("n")) { out.add("n", number(out.get("n"), true, note, depth)); }
                if (out.has("p")) { out.add("p", number(out.get("p"), false, note, depth)); }
                return out;
            }
            case "mul": {
                JsonObject score = scored(out);
                if (score != null) { return score; }
                break;
            }
            default: break;
        }
        Double folded = folded(out);
        if (folded != null) { return whole ? new JsonPrimitive(Math.round(folded)) : new JsonPrimitive(folded); }
        note.accept("uses the number provider " + type + ", which 26.2 has no form for, so it is dropped to 0");
        return new JsonPrimitive(0);
    }

    @Nullable private static JsonObject scored(JsonObject product) {
        for (String[] side : new String[][] {{"left", "right"}, {"right", "left"}}) {
            JsonElement input = product.get(side[0]);
            JsonElement scale = product.get(side[1]);
            if (input == null || scale == null || !input.isJsonObject() || !"from_int".equals(NumberSlots.type(input.getAsJsonObject()))) { continue; }
            JsonElement score = input.getAsJsonObject().get(INPUT);
            Double factor = folded(scale);
            if (factor == null || score == null || !score.isJsonObject() || !"score".equals(NumberSlots.type(score.getAsJsonObject()))) { continue; }
            JsonObject out = JsonTree.copy(score.getAsJsonObject());
            out.addProperty("scale", factor);
            return out;
        }
        return null;
    }

    private static JsonObject fallback(JsonObject provider, LineNote note) {
        JsonElement fallback = provider.remove(FALLBACK);
        if (fallback != null && !(fallback.isJsonPrimitive() && fallback.getAsJsonPrimitive().isNumber() && fallback.getAsDouble() == 0.0)) { note.accept("falls back to a value other than 0 when a score or stored number is missing, which 26.2 cannot, so it falls back to 0"); }
        return provider;
    }

    @Nullable private static Double folded(JsonElement value) {
        if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isNumber()) { return value.getAsDouble(); }
        if (!value.isJsonObject()) { return null; }
        JsonObject held = value.getAsJsonObject();
        String type = NumberSlots.type(held);
        if ("constant".equals(type)) { return held.has(VALUE) ? folded(held.get(VALUE)) : null; }
        if (held.has(INPUT)) {
            Double input = folded(held.get(INPUT));
            return input == null ? null : unary(type, input);
        }
        if (held.has("left") && held.has("right")) {
            Double left = folded(held.get("left"));
            Double right = folded(held.get("right"));
            return left == null || right == null ? null : binary(type, left, right);
        }
        if (!held.has(INPUTS) || !held.get(INPUTS).isJsonArray() || held.getAsJsonArray(INPUTS).size() == 0) { return null; }
        double sum = 0.0;
        double least = Double.MAX_VALUE;
        double most = -Double.MAX_VALUE;
        JsonArray inputs = held.getAsJsonArray(INPUTS);
        for (JsonElement item : inputs) {
            Double folded = folded(item);
            if (folded == null) { return null; }
            sum += folded;
            least = Math.min(least, folded);
            most = Math.max(most, folded);
        }
        switch (type) {
            case "add": return sum;
            case "avg": return sum / inputs.size();
            case "min": return least;
            case "max": return most;
            default: return null;
        }
    }

    @Nullable private static Double unary(String type, double input) {
        switch (type) {
            case "abs": return Math.abs(input);
            case "negate": return -input;
            case "ceil": return Math.ceil(input);
            case "floor": return Math.floor(input);
            case "round": return (double) Math.round(input);
            case "truncate":
            case "from_float": return (double) (long) input;
            case "sqrt": return Math.sqrt(input);
            case "sin": return Math.sin(input);
            case "cos": return Math.cos(input);
            case "from_int": return input;
            default: return null;
        }
    }

    @Nullable private static Double binary(String type, double left, double right) {
        switch (type) {
            case "sub": return left - right;
            case "mul": return left * right;
            case "div": return right == 0.0 ? null : left / right;
            case "pow": return Math.pow(left, right);
            case "mod": return right == 0.0 ? null : left % right;
            case "floor_mod": return right == 0.0 ? null : left - Math.floor(left / right) * right;
            case "floor_div": return right == 0.0 ? null : Math.floor(left / right);
            default: return null;
        }
    }
}
