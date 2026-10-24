package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.def.CarryDef;
import mctmods.resourcedatapackloader.content.def.CostPart;
import mctmods.resourcedatapackloader.content.def.GateDef;
import mctmods.resourcedatapackloader.content.def.PortwayCost;
import mctmods.resourcedatapackloader.content.def.PortwayDef;
import mctmods.resourcedatapackloader.content.def.PortwayItemDef;
import mctmods.resourcedatapackloader.content.def.PortwayMenu;
import mctmods.resourcedatapackloader.util.Json;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;

public final class ContentParserPortways {
    private ContentParserPortways() {}

    @Nullable static PortwayDef portway(JsonObject json) {
        if (!json.has("portway") || !json.get("portway").isJsonObject()) { return null; }
        return settings(Json.getJsonObject(json, "portway"));
    }

    @Nullable static PortwayItemDef item(JsonObject json) {
        if (!json.has("portway") || !json.get("portway").isJsonObject()) { return null; }
        JsonObject held = Json.getJsonObject(json, "portway");
        return new PortwayItemDef(settings(held), Json.getBoolean(held, "consume", false), Json.getBoolean(held, "bind", false));
    }

    private static PortwayDef settings(JsonObject held) {
        return new PortwayDef(Json.getString(held, "activateSound", PortwayDef.PLAIN.activateSound).trim(),
                Json.getString(held, "travelSound", PortwayDef.PLAIN.travelSound).trim(),
                Json.names(held, "names"),
                dimensions(held, "allow"),
                dimensions(held, "deny"),
                Json.getBoolean(held, "crossDimension", true),
                Json.getString(held, "gate", "").trim(),
                Json.getString(held, "network", "").trim(),
                Json.choice(held, "scope", Json.getBoolean(held, "global", false) ? GateDef.GLOBAL : GateDef.PLAYER, "Portway", ContentParserWorlds.SCOPES),
                Json.getBoolean(held, "tall", false),
                Json.getBoolean(held, "facing", false),
                Json.getString(held, "activateParticle", "").trim(),
                Json.getString(held, "travelParticle", "").trim(),
                carry(Json.object(held, "carry")),
                cost(Json.object(held, "cost")), menu(Json.object(held, "menu")));
    }

    private static PortwayMenu menu(JsonObject json) {
        PortwayMenu plain = PortwayMenu.PLAIN;
        return new PortwayMenu(Json.choice(json, "order", plain.order, "Portway menu", PortwayMenu.ORDERS),
                icon(json, "previous", plain.previous), icon(json, "next", plain.next), icon(json, "page", plain.page),
                icon(json, "sort", plain.sort), icon(json, "locked", plain.locked));
    }

    private static String icon(JsonObject json, String key, String fallback) {
        String id = Json.getString(json, key, fallback).trim();
        return id.isEmpty() ? fallback : id;
    }

    private static CarryDef carry(JsonObject json) {
        return new CarryDef(Json.getBoolean(json, "mount", false), Json.getBoolean(json, "leashed", false), Json.getBoolean(json, "pets", false), Json.getBoolean(json, "crossDimension", true));
    }

    private static List<Integer> dimensions(JsonObject held, String key) {
        if (!held.has(key)) { return Collections.emptyList(); }
        return Collections.unmodifiableList(Json.ints(Json.getJsonArray(held, key, new JsonArray()), key));
    }

    private static PortwayCost cost(JsonObject json) {
        return new PortwayCost(part(Json.object(json, "xp")), part(Json.object(json, "item")), part(Json.object(json, "health")), part(Json.object(json, "hunger")), Math.max(0, Json.getInt(json, "cooldown", 0)));
    }

    private static CostPart part(JsonObject json) {
        return new CostPart(Json.getString(json, "id", "").trim(), Math.max(0, Json.getInt(json, "amount", 0)), Math.max(0, Json.getInt(json, "perBlocks", 0)),
                Math.max(0, Json.getInt(json, "dimension", 0)), Math.max(0, Json.getInt(json, "max", 0)), Json.getBoolean(json, "points", false));
    }
}
