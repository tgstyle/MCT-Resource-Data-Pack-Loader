package mctmods.resourcedatapackloader.common.worldgen.structure.map;

import mctmods.resourcedatapackloader.common.pack.definitions.worlds.CityMapDef;
import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.pack.loading.parsers.ContentParserWorlds;
import mctmods.resourcedatapackloader.common.villages.CityLayout;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

public final class ContentCityMaps {
    private static final Map<String, CityMapDef> DEFS = new LinkedHashMap<>();
    private static final List<String> CITY_MAP_KEYS = Collections.unmodifiableList(Arrays.asList("map", "palette", "cell", "settings", "name"));
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
        Json.loadDefs(DEFS, PackManager.CITYMAPS, "city map", (key, contents) -> {
            JsonObject json = Json.GSON.fromJson(contents, JsonObject.class);
            if (json != null) { Json.unknownKeys(json, "the top level", CITY_MAP_KEYS); }
            return ContentParserWorlds.cityMap(key, contents);
        });
    }

    @Nullable public static JsonElement setting(String key) {
        if (DEFS.isEmpty()) { return null; }
        CityMapDef def = byName(CityLayout.named());
        if (def == null || def.settings == null || !def.settings.has(key)) { return null; }
        return def.settings.get(key);
    }

    @Nullable public static CityMapDef byName(String name) { return Json.byName(DEFS, name); }
}
