package mctmods.resourcedatapackloader.pack.port;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Map;

final class ConvertAdvancements {
    private ConvertAdvancements() {}

    static String advancement(JsonObject json, String from, Ported pack) {
        if (json.has("display") && json.get("display").isJsonObject()) {
            JsonObject display = json.getAsJsonObject("display");
            if (display.has("icon") && display.get("icon").isJsonObject()) { display.add("icon", ConvertRecipes.stack(display.get("icon"), from, pack)); }
            if (display.has("background") && display.get("background").isJsonPrimitive()) { display.addProperty("background", ConvertAssets.textureRef(display.get("background").getAsString())); }
        }
        json.remove("sends_telemetry_event");
        if (json.has("criteria") && json.get("criteria").isJsonObject()) {
            for (Map.Entry<String, JsonElement> criterion : json.getAsJsonObject("criteria").entrySet()) {
                if (!criterion.getValue().isJsonObject()) { continue; }
                JsonObject held = criterion.getValue().getAsJsonObject();
                if (!held.has("conditions") || !held.get("conditions").isJsonObject()) { continue; }
                JsonObject conditions = held.getAsJsonObject("conditions");
                if (conditions.has("player")) {
                    conditions.remove("player");
                    pack.note("'" + from + "' checks the player in its criterion '" + criterion.getKey() + "', which 1.12.2 cannot, so that check is left out");
                }
                predicates(conditions, from, pack);
            }
        }
        return Ported.GSON.toJson(json);
    }

    private static void predicates(JsonElement element, String from, Ported pack) {
        if (element.isJsonArray()) {
            for (JsonElement inner : element.getAsJsonArray()) { predicates(inner, from, pack); }
            return;
        }
        if (!element.isJsonObject()) { return; }
        JsonObject held = element.getAsJsonObject();
        JsonElement listed = held.get("items");
        boolean names = listed != null && (listed.isJsonPrimitive() || listed.isJsonArray() && listed.getAsJsonArray().size() > 0 && listed.getAsJsonArray().get(0).isJsonPrimitive());
        if (names) {
            JsonElement items = held.remove("items");
            String first = items.isJsonArray() ? (items.getAsJsonArray().size() > 0 ? items.getAsJsonArray().get(0).getAsString() : "") : items.getAsString();
            if (items.isJsonArray() && items.getAsJsonArray().size() > 1) { pack.note("'" + from + "' accepts any of " + items + ", and a 1.12.2 item predicate names one, so only " + first + " is kept"); }
            if (first.startsWith("#")) { held.addProperty("tag", first.substring(1)); }
            else if (!first.isEmpty()) {
                Convert.Ref ref = Convert.item(first, pack, "'" + from + "'");
                held.addProperty("item", ref == null ? first : ref.id);
                if (ref != null && ref.specific) { held.addProperty("data", ref.meta); }
            }
        }
        if (held.has("tag") && held.get("tag").isJsonPrimitive()) {
            String tag = held.remove("tag").getAsString();
            String ore = Ids.oreName(tag);
            held.addProperty("type", "forge:ore_dict");
            held.addProperty("ore", ore == null ? Ids.camel(Ids.namespaced(tag).replace(':', '_').replace('/', '_'), false) : ore);
        }
        if (held.has("components")) {
            held.remove("components");
            pack.note("'" + from + "' matches item components, which have no twin on 1.12.2, so that part of the match is left out");
        }
        for (Map.Entry<String, JsonElement> entry : new ArrayList<>(held.entrySet())) { predicates(entry.getValue(), from, pack); }
    }
}
