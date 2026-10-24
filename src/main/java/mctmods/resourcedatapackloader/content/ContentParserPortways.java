package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.def.CarryDef;
import mctmods.resourcedatapackloader.content.def.CostDef;
import mctmods.resourcedatapackloader.content.def.CostPart;
import mctmods.resourcedatapackloader.content.def.GateDef;
import mctmods.resourcedatapackloader.content.def.PortwayDef;
import mctmods.resourcedatapackloader.content.def.PortwayItemDef;
import mctmods.resourcedatapackloader.content.def.PortwayMenu;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.Settings;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

final class ContentParserPortways {
    private ContentParserPortways() {}

    @Nullable static PortwayDef portway(ResourceLocation key, JsonObject json) { return GsonHelper.isObjectNode(json, "portway") ? settings(key, Json.getJsonObject(json, "portway")) : null; }

    @Nullable static PortwayItemDef item(ResourceLocation key, JsonObject json) {
        if (!GsonHelper.isObjectNode(json, "portway")) { return null; }
        JsonObject held = Json.getJsonObject(json, "portway");
        return new PortwayItemDef(settings(key, held), Json.getBoolean(held, "consume", false), Json.getBoolean(held, "bind", false));
    }

    private static PortwayDef settings(ResourceLocation key, JsonObject held) {
        return new PortwayDef(Json.getString(held, "activateSound", PortwayDef.PLAIN.activateSound()).trim(), Json.getString(held, "travelSound", PortwayDef.PLAIN.travelSound()).trim(),
                trimmed(Json.strings(held, "names")), dimensions(Json.strings(held, "allow")), dimensions(Json.strings(held, "deny")), Json.getBoolean(held, "crossDimension", true),
                Json.getString(held, "gate", "").trim(), Json.getString(held, "network", "").trim(), Json.choice(held, "scope", Json.getBoolean(held, "global", false) ? GateDef.GLOBAL : GateDef.PLAYER, "Portway " + key, GateDef.SCOPES), Json.getBoolean(held, "tall", false), Json.getBoolean(held, "facing", false),
                Json.getString(held, "activateParticle", "").trim(), Json.getString(held, "travelParticle", "").trim(), carry(Json.object(held, "carry")), cost(key, Json.object(held, "cost")), menu(key, Json.object(held, "menu")));
    }

    private static PortwayMenu menu(ResourceLocation key, JsonObject json) {
        PortwayMenu plain = PortwayMenu.PLAIN;
        return new PortwayMenu(Json.choice(json, "order", plain.order(), "Portway " + key + " menu", PortwayMenu.ORDERS),
                icon(json, "previous", plain.previous()), icon(json, "next", plain.next()), icon(json, "page", plain.page()),
                icon(json, "sort", plain.sort()), icon(json, "locked", plain.locked()));
    }

    private static String icon(JsonObject json, String key, String fallback) {
        String id = Json.getString(json, key, fallback).trim();
        return id.isEmpty() ? fallback : id;
    }

    private static CarryDef carry(JsonObject json) {
        return new CarryDef(Json.getBoolean(json, "mount", false), Json.getBoolean(json, "leashed", false), Json.getBoolean(json, "pets", false), Json.getBoolean(json, "crossDimension", true));
    }

    private static List<String> trimmed(List<String> texts) {
        List<String> out = new ArrayList<>();
        for (String text : texts) {
            if (!text.isBlank()) { out.add(text.trim()); }
        }
        return List.copyOf(out);
    }

    private static List<String> dimensions(List<String> named) {
        List<String> out = new ArrayList<>();
        for (String text : named) { out.add(Settings.lower(ContentFormats.dimensionId(text))); }
        return List.copyOf(out);
    }

    private static CostDef cost(ResourceLocation key, JsonObject cost) {
        JsonObject xp = Json.object(cost, "xp");
        JsonObject item = Json.object(cost, "item");
        String itemId = Json.getString(item, "id", "").trim();
        if (!itemId.isEmpty() && ResourceLocation.tryParse(itemId) == null) {
            ContentLog.LOGGER.error("Portway {} asks for the item '{}', which is not an item id, so it costs no item", key, itemId);
            itemId = "";
        }
        return new CostDef(part(xp), Json.getBoolean(xp, "points", false), itemId, part(item), part(Json.object(cost, "health")), part(Json.object(cost, "hunger")), Math.max(0, Json.getInt(cost, "cooldown", 0)));
    }

    private static CostPart part(JsonObject part) {
        return new CostPart(Math.max(0, Json.getInt(part, "amount", 0)), Math.max(0, Json.getInt(part, "perBlocks", 0)), Math.max(0, Json.getInt(part, "dimension", 0)), Math.max(0, Json.getInt(part, "max", 0)));
    }
}
