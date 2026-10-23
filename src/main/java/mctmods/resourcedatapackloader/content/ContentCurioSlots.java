package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.def.CurioSlotDef;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.Settings;
import mctmods.resourcedatapackloader.util.Summary;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.resources.Identifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class ContentCurioSlots {
    private static final Set<String> DROP_RULES = Set.of("DEFAULT", "ALWAYS_DROP", "ALWAYS_KEEP", "DESTROY");
    private static final Map<String, CurioSlotDef> SLOTS = new LinkedHashMap<>();
    private static boolean loaded;

    private ContentCurioSlots() {}

    public static void load() {
        if (loaded) { return; }
        loaded = true;
        if (Config.definitionsOff()) { return; }
        Json.eachObject(PackManager.CURIOSLOTS, "curio slot", (key, json) -> SLOTS.put(key.getPath(), parse(key, json)));
        if (!SLOTS.isEmpty()) { Summary.info("curioslots", "Loaded " + SLOTS.size() + " curio slot type(s) from packs"); }
    }

    public static Collection<CurioSlotDef> defs() { return SLOTS.values(); }

    public static JsonObject slotJson(CurioSlotDef def) {
        JsonObject json = Json.obj("size", def.size(), "validators", Json.arr("curios:tag"));
        if (def.order() != null) { json.addProperty("order", def.order()); }
        if (!def.icon().isEmpty()) { json.addProperty("icon", def.icon()); }
        if (def.cosmetic()) { json.addProperty("add_cosmetic", true); }
        if (!def.dropRule().isEmpty()) { json.addProperty("drop_rule", def.dropRule().toLowerCase(Locale.ROOT)); }
        if (def.renderToggle() != null) { json.addProperty("render_toggle", def.renderToggle()); }
        return json;
    }

    public static JsonObject entitiesJson(CurioSlotDef def) {
        JsonArray entities = Json.arr();
        for (String entity : def.entities()) { entities.add(entity); }
        return Json.obj("entities", entities, "slots", Json.arr(def.id()));
    }

    private static CurioSlotDef parse(Identifier key, JsonObject json) {
        String dropRule = Settings.lower(Json.getString(json, "dropRule", "")).toUpperCase(Locale.ROOT);
        if (!dropRule.isEmpty() && !DROP_RULES.contains(dropRule)) {
            ContentLog.LOGGER.error("Curio slot {} names dropRule '{}', which is not one of {}, so the default is used", key, dropRule, DROP_RULES);
            dropRule = "";
        }
        List<String> entities = new ArrayList<>();
        for (String named : Json.strings(json, "entities")) {
            if (Identifier.tryParse(named.trim()) == null) { ContentLog.LOGGER.error("Curio slot {} names the entity '{}', which is not a valid id, skipping it", key, named); }
            else { entities.add(named.trim()); }
        }
        if (entities.isEmpty()) { entities.add("minecraft:player"); }
        return new CurioSlotDef(key.getPath(), Math.max(1, Json.getInt(json, "size", 1)),
                json.has("order") ? Json.getInt(json, "order", 0) : null,
                Json.getString(json, "icon", "").trim(),
                Json.getBoolean(json, "cosmetic", false),
                dropRule,
                json.has("renderToggle") ? Json.getBoolean(json, "renderToggle", true) : null,
                List.copyOf(entities));
    }
}
