package mctmods.resourcedatapackloader.core.util.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;

public final class JsonTree {
    private JsonTree() {}

    public static boolean badPath(String path) {
        if (path.isEmpty()) { return true; }
        for (String part : path.split("\\.", -1)) {
            if (part.isEmpty()) { return true; }
        }
        return false;
    }

    public static String last(String path) { return path.substring(path.lastIndexOf('.') + 1); }

    @Nullable public static JsonElement get(JsonElement root, String path) {
        JsonElement current = root;
        for (String part : path.split("\\.", -1)) {
            current = child(current, part);
            if (current == null) { return null; }
        }
        return current;
    }

    @Nullable public static JsonElement parent(JsonElement root, String path) {
        int cut = path.lastIndexOf('.');
        JsonElement holder = cut < 0 ? root : get(root, path.substring(0, cut));
        String last = last(path);
        if (holder == null) { return null; }
        if (holder.isJsonObject()) { return holder; }
        return holder.isJsonArray() && index(last) >= 0 && index(last) < holder.getAsJsonArray().size() ? holder : null;
    }

    public static void put(JsonElement parent, String last, JsonElement value) {
        if (parent.isJsonObject()) { parent.getAsJsonObject().add(last, value); }
        else { parent.getAsJsonArray().set(index(last), value); }
    }

    @Nullable private static JsonElement child(JsonElement holder, String part) {
        if (holder.isJsonObject()) { return holder.getAsJsonObject().get(part); }
        int index = index(part);
        if (!holder.isJsonArray() || index < 0 || index >= holder.getAsJsonArray().size()) { return null; }
        return holder.getAsJsonArray().get(index);
    }

    private static int index(String part) {
        if (part.isEmpty() || part.length() > 9) { return -1; }
        for (int i = 0; i < part.length(); i++) {
            if (!Character.isDigit(part.charAt(i))) { return -1; }
        }
        return Integer.parseInt(part);
    }

    public static JsonArray map(JsonArray held, UnaryOperator<JsonElement> itemFn) {
        JsonArray out = new JsonArray();
        for (JsonElement item : held) { out.add(itemFn.apply(item)); }
        return out;
    }

    public static JsonObject map(JsonObject held, BiFunction<String, JsonElement, JsonElement> entryFn) {
        JsonObject out = new JsonObject();
        for (Map.Entry<String, JsonElement> entry : held.entrySet()) {
            JsonElement value = entryFn.apply(entry.getKey(), entry.getValue());
            if (value != null) { out.add(entry.getKey(), value); }
        }
        return out;
    }
}
