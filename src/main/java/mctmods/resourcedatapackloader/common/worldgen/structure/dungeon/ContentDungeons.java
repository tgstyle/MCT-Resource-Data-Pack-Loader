package mctmods.resourcedatapackloader.common.worldgen.structure.dungeon;

import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DungeonDef;
import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.pack.loading.parsers.ContentParserDungeons;
import mctmods.resourcedatapackloader.common.util.worldgen.GenHeights;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.DungeonPlanner;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.PlanRecords;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.write.DungeonWriter;
import mctmods.resourcedatapackloader.core.integration.CompatHandler;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.json.Json;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public final class ContentDungeons {
    private static final int WINDOW_OFFSET = 8;
    private static final int LOCATE_PLANS = 16;
    private static final Map<String, DungeonDef> DEFS = new TreeMap<>();
    private static boolean loaded = false;

    private ContentDungeons() {}

    public static void load() {
        if (loaded) { return; }
        loaded = true;
        ContentDungeonThemes.load();
        Json.eachFile(PackManager.DUNGEONS, "dungeon", (key, contents) -> {
            DungeonDef def = ContentParserDungeons.dungeon(key, contents);
            if (def != null) { DEFS.put(key.toString(), def); }
        });
        if (!DEFS.isEmpty()) { ContentLog.LOGGER.debug("Loaded {} dungeon(s): {}", DEFS.size(), DEFS.keySet()); }
    }

    public static boolean none() { return DEFS.isEmpty(); }

    public static List<DungeonDef> all() { return Collections.unmodifiableList(new ArrayList<>(DEFS.values())); }

    @Nullable public static DungeonDef byName(String id) { return id == null || id.isEmpty() ? null : DEFS.get(new ResourceLocation(id).toString()); }

    public static void place(World world, int chunkX, int chunkZ) {
        if (DEFS.isEmpty() || !(world instanceof WorldServer) || forDimension(world.provider.getDimension()).isEmpty()) { return; }
        int floor = GenHeights.floor(world, 0);
        int ceiling = GenHeights.ceiling(world, CompatHandler.VANILLA_HEIGHT) - 1;
        int baseX = chunkX * 16 + WINDOW_OFFSET;
        int baseZ = chunkZ * 16 + WINDOW_OFFSET;
        StructureBoundingBox window = new StructureBoundingBox(baseX, floor, baseZ, baseX + 15, ceiling, baseZ + 15);
        for (DungeonSites.Site site : DungeonSites.sitesTouching(world, window)) {
            DungeonWriter.write(world, site, window, floor, ceiling);
        }
    }

    public static boolean claims(World world, StructureBoundingBox area, Predicate<StructureBoundingBox> hit) {
        if (DEFS.isEmpty() || !(world instanceof WorldServer) || forDimension(world.provider.getDimension()).isEmpty()) { return false; }
        for (DungeonSites.Site site : DungeonSites.sitesTouching(world, area)) {
            PlanRecords.Entrance entrance = DungeonPlanner.get(world, site.def, site).entrance;
            if (entrance == null) { continue; }
            if (hit.test(footprint(entrance))) { return true; }
            for (PlanRecords.Entrance extra : entrance.extras) {
                if (hit.test(footprint(extra))) { return true; }
            }
        }
        return false;
    }

    private static StructureBoundingBox footprint(PlanRecords.Entrance entrance) {
        return new StructureBoundingBox(entrance.x - PlanRecords.ENTRANCE_REACH, 0, entrance.z - PlanRecords.ENTRANCE_REACH, entrance.x + PlanRecords.ENTRANCE_REACH, 0, entrance.z + PlanRecords.ENTRANCE_REACH);
    }

    public static Set<String> locateNames(World world) {
        Set<String> names = new TreeSet<>();
        for (DungeonDef def : forDimension(world.provider.getDimension())) {
            if (def.locateAs != null && !def.locateAs.isEmpty()) { names.add(def.locateAs); }
        }
        return names;
    }

    public static List<long[]> locateSpots(World world, String name, BlockPos from, int radius) {
        List<DungeonSites.Site> named = new ArrayList<>();
        for (DungeonSites.Site site : DungeonSites.sitesNear(world, from, radius)) {
            if (name.equals(site.def.locateAs)) { named.add(site); }
        }
        named.sort(Comparator.comparingLong(site -> site.distanceSq(from.getX(), from.getZ())));
        List<long[]> spots = new ArrayList<>();
        for (DungeonSites.Site site : named.subList(0, Math.min(named.size(), LOCATE_PLANS))) {
            PlanRecords.Entrance entrance = DungeonPlanner.get(world, site.def, site).entrance;
            spots.add(entrance == null ? new long[] {site.originX, site.originZ} : new long[] {entrance.x, entrance.z});
        }
        return spots;
    }

    public static List<DungeonDef> forDimension(int dimension) {
        List<DungeonDef> found = new ArrayList<>();
        for (DungeonDef def : DEFS.values()) {
            if (def.allowsDimension(dimension)) { found.add(def); }
        }
        return found;
    }
}
