package mctmods.resourcedatapackloader.common.worldgen.structure.dungeon;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.common.generated.ContentFormats;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DungeonDef;
import mctmods.resourcedatapackloader.common.pack.io.GeneratedResources;
import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.pack.loading.WorldgenFiles;
import mctmods.resourcedatapackloader.common.pack.loading.parsers.ContentParserDungeons;
import mctmods.resourcedatapackloader.common.worldgen.placement.ContentWorldgen;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.DungeonPlanner;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.PlanRecords;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Summary;
import mctmods.resourcedatapackloader.core.util.json.Json;
import mctmods.resourcedatapackloader.core.util.math.MathUtil;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.neoforged.neoforge.event.level.LevelEvent;

public final class ContentDungeons {
    public static final String DUNGEON_STRUCTURE = "dungeon";
    public static final String DUNGEON_PIECE = "dungeon_piece";
    private static final String FOLDER = "dungeons/";
    private static final int LOCATE_REACH = 8192;
    private static final int LOCATE_PLANS = 16;
    private static final Map<String, DungeonDef> DEFS = new TreeMap<>();
    private static final List<String> SETS = new ArrayList<>();
    private static volatile Map<ChunkGenerator, ServerLevel> levels = Map.of();
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

    public static void generate() {
        SETS.clear();
        for (DungeonDef def : DEFS.values()) {
            String namespace = def.key().getNamespace();
            String path = FOLDER + def.key().getPath();
            JsonObject structure = new JsonObject();
            structure.addProperty("type", ResourceDataPackLoader.MOD_ID + ":" + DUNGEON_STRUCTURE);
            structure.addProperty("dungeon", def.key().toString());
            structure.add("biomes", ContentFormats.anyBiomes());
            structure.addProperty("step", "top_layer_modification");
            structure.add("spawn_overrides", new JsonObject());
            structure.addProperty("terrain_adaptation", "none");
            GeneratedResources.put(PackType.SERVER_DATA, namespace, WorldgenFiles.file(WorldgenFiles.STRUCTURE, path), structure.toString());
            GeneratedResources.put(PackType.SERVER_DATA, namespace, WorldgenFiles.file(WorldgenFiles.STRUCTURE_SET, path), set(def, namespace + ":" + path).toString());
            SETS.add(namespace + ":" + path);
        }
        if (!SETS.isEmpty()) { Summary.info("dungeons.generated", "Generated " + SETS.size() + " structure(s) with their sets from dungeons"); }
    }

    private static JsonObject set(DungeonDef def, String structureId) {
        JsonObject entry = new JsonObject();
        entry.addProperty("structure", structureId);
        entry.addProperty("weight", 1);
        JsonArray structures = new JsonArray();
        structures.add(entry);
        JsonObject placement = new JsonObject();
        placement.addProperty("type", ResourceDataPackLoader.MOD_ID + ":" + ContentWorldgen.SPREAD_PLACEMENT);
        placement.addProperty("salt", def.key().toString().hashCode() & Integer.MAX_VALUE);
        placement.addProperty("spacing", ContentDungeonStructure.REGION_CHUNKS);
        placement.addProperty("separation", ContentDungeonStructure.REGION_CHUNKS - 1);
        JsonObject set = new JsonObject();
        set.add("placement", placement);
        set.add("structures", structures);
        return set;
    }

    public static List<String> sets() { return List.copyOf(SETS); }

    public static void onLevelLoad(LevelEvent.Load event) {
        if (DEFS.isEmpty() || !(event.getLevel() instanceof ServerLevel level)) { return; }
        Map<ChunkGenerator, ServerLevel> next = level.dimension() == Level.OVERWORLD ? new IdentityHashMap<>() : new IdentityHashMap<>(levels);
        next.put(level.getChunkSource().getGenerator(), level);
        levels = next;
    }

    @Nullable public static DungeonWorld world(Structure.GenerationContext context) {
        ServerLevel level = levels.get(context.chunkGenerator());
        return level == null ? null : new DungeonWorld(context, level.dimension().location().toString(), level.getSharedSpawnPos());
    }

    public static DungeonWorld world(WorldGenLevel level) {
        ServerLevel server = level.getLevel();
        ChunkGenerator generator = server.getChunkSource().getGenerator();
        Structure.GenerationContext context = new Structure.GenerationContext(server.registryAccess(), generator, generator.getBiomeSource(), server.getChunkSource().randomState(), server.getStructureManager(), server.getSeed(), new ChunkPos(0, 0), server, biome -> true);
        return new DungeonWorld(context, server.dimension().location().toString(), server.getSharedSpawnPos());
    }

    @Nullable public static DungeonSites.Site site(DungeonWorld world, DungeonDef def, int originX, int originZ) {
        for (DungeonSites.Site site : DungeonSites.sitesTouching(world, new BoundingBox(originX, 0, originZ, originX, 0, originZ))) {
            if (site.def.key().equals(def.key()) && site.originX == originX && site.originZ == originZ) { return site; }
        }
        return null;
    }

    public static boolean claims(WorldGenLevel level, BoundingBox area, Predicate<BoundingBox> hit) {
        if (DEFS.isEmpty()) { return false; }
        DungeonWorld world = world(level);
        for (DungeonSites.Site site : DungeonSites.sitesTouching(world, area)) {
            PlanRecords.Entrance entrance = DungeonPlanner.get(world, site.def, site).entrance;
            if (entrance == null) { continue; }
            if (hit.test(footprint(entrance))) { return true; }
            for (PlanRecords.Entrance extra : entrance.extras()) {
                if (hit.test(footprint(extra))) { return true; }
            }
        }
        return false;
    }

    public static Set<String> locateNames(ServerLevel level) {
        Set<String> names = new TreeSet<>();
        String dimension = level.dimension().location().toString();
        for (DungeonDef def : DEFS.values()) {
            if (def.locateAs() != null && !def.locateAs().isEmpty() && def.allowsDimension(dimension)) { names.add(def.locateAs()); }
        }
        return names;
    }

    @Nullable public static BlockPos nearestEntrance(ServerLevel level, String name, BlockPos from, Predicate<BlockPos> skipped) {
        DungeonWorld world = world(level);
        List<DungeonSites.Site> named = new ArrayList<>();
        for (DungeonSites.Site site : DungeonSites.sitesNear(world, from, LOCATE_REACH)) {
            if (name.equals(site.def.locateAs())) { named.add(site); }
        }
        named.sort(Comparator.comparingLong(site -> site.distanceSq(from.getX(), from.getZ())));
        BlockPos best = null;
        long bestAway = Long.MAX_VALUE;
        for (DungeonSites.Site site : named.subList(0, Math.min(named.size(), LOCATE_PLANS))) {
            PlanRecords.Entrance entrance = DungeonPlanner.get(world, site.def, site).entrance;
            BlockPos pin = entrance == null ? new BlockPos(site.originX, 64, site.originZ) : new BlockPos(entrance.x(), 64, entrance.z());
            if (skipped.test(pin)) { continue; }
            long away = MathUtil.dist2(pin.getX(), pin.getZ(), from.getX(), from.getZ());
            if (away >= bestAway) { continue; }
            bestAway = away;
            best = pin;
        }
        return best;
    }

    private static BoundingBox footprint(PlanRecords.Entrance entrance) { return new BoundingBox(entrance.x() - PlanRecords.ENTRANCE_REACH, 0, entrance.z() - PlanRecords.ENTRANCE_REACH, entrance.x() + PlanRecords.ENTRANCE_REACH, 0, entrance.z() + PlanRecords.ENTRANCE_REACH); }

    public static boolean none() { return DEFS.isEmpty(); }

    public static List<DungeonDef> all() { return List.copyOf(DEFS.values()); }

    @Nullable public static DungeonDef byName(String id) { return id == null || id.isEmpty() ? null : DEFS.get(ResourceLocation.parse(id).toString()); }
}
