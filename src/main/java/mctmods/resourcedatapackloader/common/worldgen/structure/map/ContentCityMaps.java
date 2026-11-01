package mctmods.resourcedatapackloader.common.worldgen.structure.map;

import mctmods.resourcedatapackloader.common.pack.definitions.worlds.CityMapDef;
import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.pack.loading.parsers.ContentParserWorlds;
import mctmods.resourcedatapackloader.common.villages.CityLayout;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonElement;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;

public final class ContentCityMaps {
    private static final Map<String, CityMapDef> DEFS = new LinkedHashMap<>();
    private static boolean loaded;

    private ContentCityMaps() {}

    public static void reload() {
        loaded = false;
        DEFS.clear();
        load();
    }

    public static void load() {
        if (loaded) { return; }
        loaded = true;
        Json.eachFile(PackManager.CITYMAPS, "city map", (key, contents) -> {
            CityMapDef def = ContentParserWorlds.cityMap(key, contents);
            if (def != null) { DEFS.put(key.toString(), def); }
        });
        if (!DEFS.isEmpty()) { ContentLog.LOGGER.debug("Loaded {} city map(s): {}", DEFS.size(), DEFS.keySet()); }
    }

    @Nullable public static JsonElement setting(String key) {
        if (DEFS.isEmpty()) { return null; }
        CityMapDef def = byName(CityLayout.named());
        if (def == null || def.settings == null || !def.settings.has(key)) { return null; }
        return def.settings.get(key);
    }

    @Nullable public static CityMapDef byName(String name) { return name == null || name.isEmpty() ? null : DEFS.get(name.toLowerCase(Locale.ROOT)); }
}
