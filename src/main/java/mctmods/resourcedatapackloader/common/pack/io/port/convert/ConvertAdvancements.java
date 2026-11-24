package mctmods.resourcedatapackloader.common.pack.io.port.convert;

import mctmods.resourcedatapackloader.common.pack.io.port.Ids;
import mctmods.resourcedatapackloader.common.pack.io.port.Ported;
import mctmods.resourcedatapackloader.core.util.IdNames;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Map;
import net.minecraft.util.JsonUtils;

public final class ConvertAdvancements {
    private ConvertAdvancements() {}

    public static String advancement(JsonObject json, String from, Ported pack) {
        JsonObject display = Json.object(json, "display");
        if (display.has("icon") && display.get("icon").isJsonObject()) { display.add("icon", ConvertRecipes.stack(display.get("icon"), from, pack)); }
        Json.rewriteText(display, "background", ConvertAssets::textureRef);
        json.remove("sends_telemetry_event");
        for (Map.Entry<String, JsonElement> criterion : Json.object(json, "criteria").entrySet()) {
            if (!criterion.getValue().isJsonObject()) { continue; }
            JsonObject conditions = Json.object(criterion.getValue().getAsJsonObject(), "conditions");
            if (conditions.size() == 0) { continue; }
            pack.dropMember(conditions, "player", Ported.where(from) + " checks the player in its criterion '" + criterion.getKey() + "', which 1.12.2 cannot, so that check is left out");
            predicates(conditions, from, pack);
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
            JsonElement head = Json.first(items);
            String first = head == null ? "" : head.getAsString();
            if (items.isJsonArray() && items.getAsJsonArray().size() > 1) { pack.note(Ported.where(from) + " accepts any of " + items + ", and a 1.12.2 item predicate names one, so only " + first + " is kept"); }
            if (IdNames.isTag(first)) { held.addProperty("tag", IdNames.tagPath(first)); }
            else if (!first.isEmpty()) {
                Convert.Ref ref = Convert.item(first, pack, Ported.where(from));
                held.addProperty("item", Convert.idOr(ref, first));
                if (ref != null && ref.specific) { held.addProperty("data", ref.meta); }
            }
        }
        if (JsonUtils.isJsonPrimitive(held, "tag")) {
            String tag = held.remove("tag").getAsString();
            held.addProperty("type", Ids.ORE_DICT);
            held.addProperty("ore", Ids.oreKey(tag));
        }
        pack.dropMember(held, "components", Ported.where(from) + " matches item components, which have no twin on 1.12.2, so that part of the match is left out");
        for (Map.Entry<String, JsonElement> entry : new ArrayList<>(held.entrySet())) { predicates(entry.getValue(), from, pack); }
    }
}
