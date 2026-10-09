package mctmods.resourcedatapackloader.pack.port;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;
import java.util.ArrayList;
import java.util.Map;

final class ConvertAdvancements {
    private ConvertAdvancements() {}

    static String advancement(JsonObject json, Ported pack) {
        if (json.has("display") && json.get("display").isJsonObject()) {
            JsonObject display = json.getAsJsonObject("display");
            if (display.has("icon") && display.get("icon").isJsonObject()) {
                JsonObject icon = display.getAsJsonObject("icon");
                ConvertRecipes.stack(icon, pack);
                if (icon.has("item") && !icon.has("id")) {
                    icon.add("id", icon.remove("item"));
                    pack.rewrote();
                }
            }
            Convert.renamed(display, "background", ConvertAssets::texturePath, pack);
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
        if (GsonHelper.isValidPrimitive(held, "item") && !held.has("items")) {
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
