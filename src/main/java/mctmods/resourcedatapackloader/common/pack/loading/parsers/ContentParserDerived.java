package mctmods.resourcedatapackloader.common.pack.loading.parsers;

import mctmods.resourcedatapackloader.common.pack.definitions.items.DerivedDef;
import mctmods.resourcedatapackloader.common.pack.definitions.items.ItemDef;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.resources.Identifier;

public final class ContentParserDerived {
    private static final List<String> NEEDS_RDPL_CLASS = List.of("type", "variants", "runs", "script", "cooldown", "rolls", "rollStore", "locates", "sleepingBag", "firework", "container", "upgrade", "portway", "eat", "alwaysEdible", "useDuration", "material", "toolClass", "slot", "crop", "soil", "potionTypes", "attackSpeed", "passesTurn", "record", "seasonClock");

    private ContentParserDerived() {}

    @Nullable static ItemDef item(Identifier key, JsonObject json) {
        DerivedDef derived = derived(key, json);
        if (derived == null) { return null; }
        for (String ignored : NEEDS_RDPL_CLASS) {
            if (json.has(ignored)) { ContentLog.LOGGER.warn("The derived item {} sets {}, which needs an RDPL item class, so the key is ignored", key, ignored); }
        }
        return ItemDef.derived(key,
                Json.getString(json, "creativeTab", ""),
                Json.strings(json, "requires"),
                ContentParserContainers.curioSlot(key, json),
                Collections.unmodifiableList(ContentParser.effects(key, Json.getJsonArray(json, "wornEffects", new JsonArray()), 0, false)),
                Collections.unmodifiableList(ContentParserContainers.wornAttributes(key, json)),
                ContentParserWeapons.weapon(key, json),
                ContentParserWeights.weight(json, key),
                derived);
    }

    @Nullable static DerivedDef derived(Identifier key, JsonObject json) {
        String named = Json.getString(json, "item", "").trim();
        Identifier base = named.isEmpty() || named.indexOf(':') == named.length() - 1 ? null : Identifier.tryParse(named);
        if (base == null) {
            ContentLog.LOGGER.error("Derived item {} names no item to copy, ignoring it", key);
            return null;
        }
        String addTo = Json.getString(json, "addTo", "").trim();
        return new DerivedDef(base, object(key, json, "set"), object(key, json, "returns"), object(key, json, "components"), addTo.isEmpty() ? null : addTo, Json.getString(json, "name", "").trim());
    }

    private static JsonObject object(Identifier key, JsonObject json, String member) {
        if (!json.has(member)) { return new JsonObject(); }
        if (!json.get(member).isJsonObject()) {
            ContentLog.LOGGER.error("The {} of derived item {} is not an object, so it is treated as empty", member, key);
            return new JsonObject();
        }
        return json.getAsJsonObject(member);
    }
}
