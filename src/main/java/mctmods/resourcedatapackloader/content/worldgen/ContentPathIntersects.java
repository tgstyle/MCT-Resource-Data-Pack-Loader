package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.ContentParser;
import mctmods.resourcedatapackloader.content.def.PathIntersectDef;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.world.SeededRandom;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

public class ContentPathIntersects {
    private static final Map<String, PathIntersectDef> DEFS = new LinkedHashMap<>();
    private static boolean loaded = false;

    public static void load() {
        if (loaded) { return; }
        loaded = true;
        PackManager.get().forEach(PackManager.PATHINTERSECTS, PackManager.JSON, (namespace, path, contents) -> {
            ResourceLocation key = new ResourceLocation(namespace, path);
            PathIntersectDef def = ContentParser.pathIntersect(key, contents);
            if (def != null) { DEFS.put(key.toString(), def); }
        });
        if (!DEFS.isEmpty()) { ContentLog.LOGGER.debug("Loaded {} path intersect design(s): {}", DEFS.size(), DEFS.keySet()); }
    }

    public static PathIntersectDef forJunction(World world, int x, int z) {
        String[] wanted = ContentControl.list(ContentControl.VILLAGES, "villagePathIntersects", Config.worldgen.villagePathIntersects);
        if (wanted.length == 0) { return null; }
        int total = 0;
        for (String key : wanted) {
            PathIntersectDef def = DEFS.get(key);
            if (def != null) { total += def.weight; }
        }
        if (total == 0) { return null; }
        Random random = SeededRandom.at(world, x, z);
        int roll = random.nextInt(total);
        for (String key : wanted) {
            PathIntersectDef def = DEFS.get(key);
            if (def == null) { continue; }
            roll -= def.weight;
            if (roll < 0) { return def; }
        }
        return null;
    }
}
