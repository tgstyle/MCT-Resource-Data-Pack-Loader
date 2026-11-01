package mctmods.resourcedatapackloader.common.villages;

import mctmods.resourcedatapackloader.common.pack.definitions.worlds.PathIntersectDef;
import mctmods.resourcedatapackloader.common.pack.loading.ContentControl;
import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.pack.loading.parsers.ContentParserWorlds;
import mctmods.resourcedatapackloader.common.util.worldgen.SeededRandom;
import mctmods.resourcedatapackloader.common.util.worldgen.WeightedPicks;
import mctmods.resourcedatapackloader.core.config.Config;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.json.Json;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.world.World;

public class ContentPathIntersects {
    private static final Map<String, PathIntersectDef> DEFS = new LinkedHashMap<>();
    private static boolean loaded = false;

    public static void load() {
        if (loaded) { return; }
        loaded = true;
        Json.eachFile(PackManager.PATHINTERSECTS, "path intersect design", (key, contents) -> {
            PathIntersectDef def = ContentParserWorlds.pathIntersect(key, contents);
            if (def != null) { DEFS.put(key.toString(), def); }
        });
        if (!DEFS.isEmpty()) { ContentLog.LOGGER.debug("Loaded {} path intersect design(s): {}", DEFS.size(), DEFS.keySet()); }
    }

    public static PathIntersectDef forJunction(World world, int x, int z) {
        String[] wanted = ContentControl.list(ContentControl.VILLAGES, "villagePathIntersects", Config.worldgen.villagePathIntersects);
        if (wanted.length == 0) { return null; }
        List<PathIntersectDef> defs = new ArrayList<>();
        for (String key : wanted) {
            PathIntersectDef def = DEFS.get(key);
            if (def != null) { defs.add(def); }
        }
        return WeightedPicks.pick(defs, def -> def.weight, SeededRandom.at(world, x, z));
    }
}
