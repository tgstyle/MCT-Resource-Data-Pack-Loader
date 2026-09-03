package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentParser;
import mctmods.resourcedatapackloader.content.def.CityMapDef;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.util.ResourceLocation;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;

public final class ContentCityMaps {
    private static final Map<String, CityMapDef> DEFS = new LinkedHashMap<>();
    private static boolean loaded;

    private ContentCityMaps() {}

    public static void load() {
        if (loaded) { return; }
        loaded = true;
        PackManager.get().forEach(PackManager.CITYMAPS, PackManager.JSON, (namespace, path, contents) -> {
            ResourceLocation key = new ResourceLocation(namespace, path);
            CityMapDef def = ContentParser.cityMap(key, contents);
            if (def != null) { DEFS.put(key.toString(), def); }
        });
        if (!DEFS.isEmpty()) { ContentLog.LOGGER.debug("Loaded {} city map(s): {}", DEFS.size(), DEFS.keySet()); }
    }

    @Nullable public static CityMapDef byName(String name) { return name == null || name.isEmpty() ? null : DEFS.get(name.toLowerCase(Locale.ROOT)); }
}
