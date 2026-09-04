package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.ContentParser;
import mctmods.resourcedatapackloader.content.def.PathIntersectDef;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.world.SeededRandom;
import mctmods.resourcedatapackloader.util.WeightedPicks;

import net.minecraft.world.World;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class ContentPathIntersects {
    private static final Map<String, PathIntersectDef> DEFS = new LinkedHashMap<>();
    private static boolean loaded = false;

    public static void load() {
        if (loaded) { return; }
        loaded = true;
        Json.eachFile(PackManager.PATHINTERSECTS, "path intersect design", (key, contents) -> {
            PathIntersectDef def = ContentParser.pathIntersect(key, contents);
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
