package mctmods.resourcedatapackloader.common.pack.loading.parsers;

import mctmods.resourcedatapackloader.common.generated.ContentFormats;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.CarryDef;
import mctmods.resourcedatapackloader.common.pack.definitions.effects.CostDef;
import mctmods.resourcedatapackloader.common.pack.definitions.effects.CostPart;
import mctmods.resourcedatapackloader.common.pack.definitions.portals.GateDef;
import mctmods.resourcedatapackloader.common.pack.definitions.portals.PortwayDef;
import mctmods.resourcedatapackloader.common.pack.definitions.portals.PortwayItemDef;
import mctmods.resourcedatapackloader.common.pack.definitions.portals.PortwayMenu;
import mctmods.resourcedatapackloader.core.config.Settings;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

final class ContentParserPortways {
    private static final String[] BUTTONS = {"previous", "sort", "page", "next", "scope"};
    private static final List<String> PORTWAY_KEYS = List.of("scope", "global", "activateSound", "travelSound", "names", "allow", "deny", "crossDimension", "gate", "network", "tall", "facing", "activateParticle", "travelParticle", "carry", "cost", "menu");
    private static final List<String> ITEM_KEYS = List.of("scope", "global", "activateSound", "travelSound", "names", "allow", "deny", "crossDimension", "gate", "network", "tall", "facing", "activateParticle", "travelParticle", "carry", "cost", "menu", "consume", "bind");
    private static final List<String> CARRY_KEYS = List.of("mount", "leashed", "pets", "crossDimension");
    private static final List<String> COST_KEYS = List.of("xp", "item", "health", "hunger", "cooldown");
    private static final List<String> PART_KEYS = List.of("amount", "perBlocks", "dimension", "max");
    private static final List<String> XP_KEYS = List.of("amount", "perBlocks", "dimension", "max", "points");
    private static final List<String> COST_ITEM_KEYS = List.of("id", "amount", "perBlocks", "dimension", "max");
    private static final List<String> MENU_KEYS = List.of("order", "previous", "next", "page", "sort", "locked", "scope", "slots");
    private static final List<String> SLOT_KEYS = List.of(BUTTONS);

    private ContentParserPortways() {}

    @Nullable static PortwayDef portway(ResourceLocation key, JsonObject json) {
        if (!GsonHelper.isObjectNode(json, "portway")) { return null; }
        JsonObject held = Json.getJsonObject(json, "portway");
        Json.unknownKeys(held, "portway", PORTWAY_KEYS);
        return settings(key, held, false);
    }

    @Nullable static PortwayItemDef item(ResourceLocation key, JsonObject json) {
        if (!GsonHelper.isObjectNode(json, "portway")) { return null; }
        JsonObject held = Json.getJsonObject(json, "portway");
        Json.unknownKeys(held, "portway", ITEM_KEYS);
        return new PortwayItemDef(settings(key, held, true), Json.getBoolean(held, "consume", false), Json.getBoolean(held, "bind", false));
    }

    private static boolean toggleAsked(JsonObject held) {
        JsonElement element = held.get("global");
        return element != null && element.isJsonPrimitive() && element.getAsJsonPrimitive().isString() && "toggle".equalsIgnoreCase(element.getAsString().trim());
    }

    private static PortwayDef settings(ResourceLocation key, JsonObject held, boolean item) {
        boolean asked = toggleAsked(held);
        String scope = Json.choice(held, "scope", !asked && Json.getBoolean(held, "global", false) ? GateDef.GLOBAL : GateDef.PLAYER, "Portway " + key, GateDef.SCOPES);
        boolean toggle = asked;
        if (asked && item) {
            ContentLog.LOGGER.warn("Portway global toggle ignored: a portway item has no scope to switch");
            toggle = false;
        }
        else if (asked && GateDef.GLOBAL.equals(scope)) {
            ContentLog.LOGGER.warn("Portway global toggle ignored: scope is global");
            toggle = false;
        }
        return new PortwayDef(Json.getString(held, "activateSound", PortwayDef.PLAIN.activateSound()).trim(), Json.getString(held, "travelSound", PortwayDef.PLAIN.travelSound()).trim(),
                trimmed(Json.strings(held, "names")), dimensions(Json.strings(held, "allow")), dimensions(Json.strings(held, "deny")), Json.getBoolean(held, "crossDimension", true),
                Json.getString(held, "gate", "").trim(), Json.getString(held, "network", "").trim(), scope, toggle, Json.getBoolean(held, "tall", false), Json.getBoolean(held, "facing", false),
                Json.getString(held, "activateParticle", "").trim(), Json.getString(held, "travelParticle", "").trim(), carry(Json.object(held, "carry")), cost(key, Json.object(held, "cost")), menu(key, Json.object(held, "menu"), toggle));
    }

    private static PortwayMenu menu(ResourceLocation key, JsonObject json, boolean scoped) {
        PortwayMenu plain = PortwayMenu.PLAIN;
        Json.unknownKeys(json, "portway.menu", MENU_KEYS);
        JsonObject slots = Json.object(json, "slots");
        Json.unknownKeys(slots, "portway.menu.slots", SLOT_KEYS);
        int[] at = columns(slots, scoped);
        return new PortwayMenu(Json.choice(json, "order", plain.order(), "Portway " + key + " menu", PortwayMenu.ORDERS),
                icon(json, "previous", plain.previous()), icon(json, "next", plain.next()), icon(json, "page", plain.page()),
                icon(json, "sort", plain.sort()), icon(json, "locked", plain.locked()), icon(json, "scope", plain.scope()),
                PortwayMenu.BOTTOM + at[0], PortwayMenu.BOTTOM + at[1], PortwayMenu.BOTTOM + at[2], PortwayMenu.BOTTOM + at[3], PortwayMenu.BOTTOM + at[4]);
    }

    private static int[] columns(JsonObject slots, boolean scoped) {
        int[] defaults = {PortwayMenu.PREVIOUS_COLUMN, PortwayMenu.SORT_COLUMN, PortwayMenu.PAGE_COLUMN, PortwayMenu.NEXT_COLUMN, PortwayMenu.SCOPE_COLUMN};
        int[] at = new int[BUTTONS.length];
        String[] taken = new String[PortwayMenu.WIDTH];
        for (int button = 0; button < (scoped ? BUTTONS.length : BUTTONS.length - 1); button++) {
            String name = BUTTONS[button];
            int column = column(slots, name, defaults[button]);
            if (taken[column] != null) {
                int free = taken[defaults[button]] == null ? defaults[button] : 0;
                while (taken[free] != null) { free++; }
                ContentLog.LOGGER.warn("Portway menu slots.{} column {} is taken by {}, using {}", name, column, taken[column], free);
                column = free;
            }
            taken[column] = name;
            at[button] = column;
        }
        if (!scoped) { at[BUTTONS.length - 1] = defaults[BUTTONS.length - 1]; }
        return at;
    }

    private static int column(JsonObject slots, String name, int fallback) {
        if (!slots.has(name)) { return fallback; }
        JsonElement element = slots.get(name);
        if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
            double value = element.getAsDouble();
            if (value == Math.rint(value) && value >= 0 && value < PortwayMenu.WIDTH) { return (int) value; }
        }
        ContentLog.LOGGER.warn("Portway menu slots.{}={} is not 0 to 8, using {}", name, element, fallback);
        return fallback;
    }

    private static String icon(JsonObject json, String key, String fallback) {
        String id = Json.getString(json, key, fallback).trim();
        return id.isEmpty() ? fallback : id;
    }

    private static CarryDef carry(JsonObject json) {
        Json.unknownKeys(json, "portway.carry", CARRY_KEYS);
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
        Json.unknownKeys(cost, "portway.cost", COST_KEYS);
        JsonObject xp = Json.object(cost, "xp");
        JsonObject item = Json.object(cost, "item");
        JsonObject health = Json.object(cost, "health");
        JsonObject hunger = Json.object(cost, "hunger");
        Json.unknownKeys(xp, "portway.cost.xp", XP_KEYS);
        Json.unknownKeys(item, "portway.cost.item", COST_ITEM_KEYS);
        Json.unknownKeys(health, "portway.cost.health", PART_KEYS);
        Json.unknownKeys(hunger, "portway.cost.hunger", PART_KEYS);
        String itemId = Json.getString(item, "id", "").trim();
        if (!itemId.isEmpty() && ResourceLocation.tryParse(itemId) == null) {
            ContentLog.LOGGER.error("Portway {} asks for the item '{}', which is not an item id, so it costs no item", key, itemId);
            itemId = "";
        }
        return new CostDef(part(xp), Json.getBoolean(xp, "points", false), itemId, part(item), part(health), part(hunger), Math.max(0, Json.getInt(cost, "cooldown", 0)));
    }

    private static CostPart part(JsonObject part) { return new CostPart(Math.max(0, Json.getInt(part, "amount", 0)), Math.max(0, Json.getInt(part, "perBlocks", 0)), Math.max(0, Json.getInt(part, "dimension", 0)), Math.max(0, Json.getInt(part, "max", 0))); }
}
