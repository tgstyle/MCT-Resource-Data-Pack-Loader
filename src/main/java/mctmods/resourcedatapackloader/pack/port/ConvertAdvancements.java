package mctmods.resourcedatapackloader.pack.port;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Map;

final class ConvertAdvancements {
    private ConvertAdvancements() {}

    public static String advancement(JsonObject json, Ported pack) {
        if (json.has("display") && json.get("display").isJsonObject()) {
            JsonObject display = json.getAsJsonObject("display");
            if (display.has("icon") && display.get("icon").isJsonObject()) { ConvertRecipes.stack(display.getAsJsonObject("icon"), pack); }
            if (display.has("background") && display.get("background").isJsonPrimitive()) {
                String background = display.get("background").getAsString();
                String moved = background.replace("textures/blocks/", "textures/block/").replace("textures/items/", "textures/item/");
                if (!moved.equals(background)) {
                    display.addProperty("background", moved);
                    pack.rewrote();
                }
            }
        }
        if (json.has("criteria") && json.get("criteria").isJsonObject()) { predicates(json.get("criteria"), pack); }
        return Ported.GSON.toJson(json);
    }

    private static void predicates(JsonElement element, Ported pack) {
        if (element.isJsonArray()) {
            for (JsonElement inner : element.getAsJsonArray()) { predicates(inner, pack); }
            return;
        }
        if (!element.isJsonObject()) { return; }
        JsonObject held = element.getAsJsonObject();
        if (held.has("item") && held.get("item").isJsonPrimitive() && !held.has("items")) {
            ConvertRecipes.stack(held, pack);
            JsonArray items = new JsonArray();
            items.add(held.get("item").getAsString());
            held.remove("item");
            held.add("items", items);
            pack.rewrote();
        }
        for (Map.Entry<String, JsonElement> entry : new ArrayList<>(held.entrySet())) { predicates(entry.getValue(), pack); }
    }
}
