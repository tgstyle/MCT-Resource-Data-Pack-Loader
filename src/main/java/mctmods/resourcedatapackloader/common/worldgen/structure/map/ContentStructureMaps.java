package mctmods.resourcedatapackloader.common.worldgen.structure.map;

import mctmods.resourcedatapackloader.common.pack.definitions.games.PickDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.StructureMapDef;
import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.pack.loading.parsers.ContentParserWorlds;
import mctmods.resourcedatapackloader.common.respawn.RespawnInstance;
import mctmods.resourcedatapackloader.common.respawn.RespawnRecorder;
import mctmods.resourcedatapackloader.common.util.world.Coords;
import mctmods.resourcedatapackloader.common.util.worldgen.ChunkOrigin;
import mctmods.resourcedatapackloader.common.util.worldgen.GenHeights;
import mctmods.resourcedatapackloader.common.worldgen.feature.shape.ContentImprint;
import mctmods.resourcedatapackloader.common.worldgen.structure.city.layout.BeardSurface;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.ContentDungeons;
import mctmods.resourcedatapackloader.core.integration.CompatHandler;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.json.Json;
import mctmods.resourcedatapackloader.core.util.math.MathUtil;
import mctmods.resourcedatapackloader.core.util.math.Turns;

import com.google.gson.JsonObject;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.template.ITemplateProcessor;
import net.minecraft.world.gen.structure.template.Template;

public class ContentStructureMaps {
    private static final Map<String, StructureMapDef> DEFS = new LinkedHashMap<>();
    private static final Set<String> MISSING = new HashSet<>();
    private static final List<String> STRUCTURE_MAP_KEYS = Collections.unmodifiableList(Arrays.asList("layers", "ground", "cell", "spacing", "chance", "at", "dimensions", "depth", "spawners", "loot", "respawn", "name"));
    private static final int FIXED_SALT = 0;
    private static final int GRID_SALT = 1;
    private static boolean loaded = false;

    public static void load() {
        if (loaded) { return; }
        loaded = true;
        Json.loadDefs(DEFS, PackManager.STRUCTUREMAPS, "structure map", (key, contents) -> {
            JsonObject json = Json.GSON.fromJson(contents, JsonObject.class);
            if (json != null) { Json.unknownKeys(json, "the top level", STRUCTURE_MAP_KEYS); }
            return ContentParserWorlds.structureMap(key, contents);
        });
    }

    public static boolean none() { return DEFS.isEmpty(); }

    public static Collection<StructureMapDef> defs() { return DEFS.values(); }

    @Nullable public static StructureMapDef byName(String name) { return Json.byName(DEFS, name); }

    public static void place(World world, int chunkX, int chunkZ) {
        if (DEFS.isEmpty() || !(world instanceof WorldServer)) { return; }
        StructureBoundingBox window = new StructureBoundingBox(ChunkOrigin.base(chunkX), GenHeights.floor(world, 0), ChunkOrigin.base(chunkZ), ChunkOrigin.base(chunkX) + 15, GenHeights.ceiling(world, CompatHandler.VANILLA_HEIGHT) - 1, ChunkOrigin.base(chunkZ) + 15);
        sites(world, window, (def, seed, originX, originZ) -> {
            slices((WorldServer) world, def, seed, originX, originZ, window);
            return false;
        });
    }

    public static boolean claims(World world, StructureBoundingBox area, Predicate<StructureBoundingBox> hit) {
        if (!DEFS.isEmpty() && sites(world, area, (def, seed, originX, originZ) -> hit.test(footprint(def, seed, originX, originZ)))) { return true; }
        return ContentDungeons.claims(world, area, hit);
    }

    private interface ISite { boolean at(StructureMapDef def, long seed, int originX, int originZ); }

    private static boolean sites(World world, StructureBoundingBox area, ISite site) {
        int dimension = world.provider.getDimension();
        for (StructureMapDef def : DEFS.values()) {
            if (!def.allowsDimension(dimension)) { continue; }
            if (def.at != null && site.at(def, MathUtil.mix(world.getSeed() ^ def.key.hashCode(), def.at[0], FIXED_SALT, def.at[1]), def.at[0], def.at[1])) { return true; }
            if (def.spacing <= 0) { continue; }
            int pitch = Coords.cubeToMinBlock(def.spacing);
            for (int gridX = Math.floorDiv(area.minX - def.widest, pitch); gridX <= Math.floorDiv(area.maxX, pitch); gridX++) {
                for (int gridZ = Math.floorDiv(area.minZ - def.widest, pitch); gridZ <= Math.floorDiv(area.maxZ, pitch); gridZ++) {
                    long seed = MathUtil.mix(world.getSeed() ^ def.key.hashCode(), gridX, GRID_SALT, gridZ);
                    if (MathUtil.percent(seed >>> 16) >= def.chance) { continue; }
                    int give = Math.max(1, pitch - def.widest);
                    int originX = gridX * pitch + (int) Math.floorMod(seed, give);
                    int originZ = gridZ * pitch + (int) Math.floorMod(seed >>> 40, give);
                    if (site.at(def, seed, originX, originZ)) { return true; }
                }
            }
        }
        return false;
    }

    private static Rotation turn(long seed) { return Turns.ALL[(int) Math.floorMod(seed >>> 8, Turns.ALL.length)]; }

    private static StructureBoundingBox footprint(StructureMapDef def, long seed, int originX, int originZ) {
        Rotation turn = turn(seed);
        boolean swapped = Coords.quarter(turn);
        int wide = def.cellsWide * def.cell;
        int deep = def.cellsDeep * def.cell;
        int spanX = swapped ? deep : wide;
        int spanZ = swapped ? wide : deep;
        return new StructureBoundingBox(originX, 0, originZ, originX + spanX - 1, 0, originZ + spanZ - 1);
    }

    private static void slices(WorldServer world, StructureMapDef def, long seed, int originX, int originZ, StructureBoundingBox window) {
        Rotation turn = turn(seed);
        StructureBoundingBox footprint = footprint(def, seed, originX, originZ);
        if (!footprint.intersectsWith(window.minX, window.minZ, window.maxX, window.maxZ)) { return; }
        int anchor = BeardSurface.surfaceAt(world, originX + footprint.getXSize() / 2, originZ + footprint.getZSize() / 2);
        if (anchor < 0) { anchor = world.getSeaLevel() - 1; }
        int base = Math.max(def.baseY(anchor, seed), GenHeights.floor(world, 0) + 1);
        int placedCells = cells(world, def, seed, turn, originX, base, originZ, window);
        if (placedCells > 0 && def.respawn != null) { RespawnRecorder.record(world, def.respawn, new RespawnInstance(RespawnInstance.MAP, def.key.toString(), "", new BlockPos(originX, base, originZ), turn, Mirror.NONE, RespawnInstance.WHOLE, seed, new StructureBoundingBox(originX, base, originZ, footprint.maxX, base + def.layers.length * def.cell - 1, footprint.maxZ)), window); }
        if (placedCells > 0 && window.isVecInside(new BlockPos(originX, 64, originZ)) && ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("Structure map {} builds at {}, {}, {}, turned {}, its ground layer floored at y {}", def.key, originX, base, originZ, turn, base + def.ground * def.cell); }
    }

    public static int cells(WorldServer world, StructureMapDef def, long seed, Rotation turn, int originX, int base, int originZ, StructureBoundingBox window) { return cells(world, def, seed, turn, originX, base, originZ, window, null); }

    public static int cells(WorldServer world, StructureMapDef def, long seed, Rotation turn, int originX, int base, int originZ, StructureBoundingBox window, @Nullable ITemplateProcessor filter) {
        int wide = def.cellsWide * def.cell;
        int deep = def.cellsDeep * def.cell;
        MinecraftServer host = world.getMinecraftServer();
        int placedCells = 0;
        int floor = GenHeights.floor(world, 0);
        int ceiling = GenHeights.ceiling(world, CompatHandler.VANILLA_HEIGHT) - 1;
        for (int layer = 0; layer < def.layers.length; layer++) {
            StructureMapDef.Layer held = def.layers[layer];
            int layerY = base + layer * def.cell;
            if (layerY > ceiling || layerY + def.cell - 1 < floor) { continue; }
            for (int row = 0; row < held.rows.length; row++) {
                String cells = held.rows[row];
                for (int column = 0; column < cells.length(); column++) {
                    char mark = cells.charAt(column);
                    if (mark == '.') { continue; }
                    int flatX = column * def.cell;
                    int flatZ = row * def.cell;
                    int cellX;
                    int cellZ;
                    if (turn == Rotation.NONE) {
                        cellX = flatX;
                        cellZ = flatZ;
                    }
                    else if (turn == Rotation.CLOCKWISE_90) {
                        cellX = deep - def.cell - flatZ;
                        cellZ = flatX;
                    }
                    else if (turn == Rotation.CLOCKWISE_180) {
                        cellX = wide - def.cell - flatX;
                        cellZ = deep - def.cell - flatZ;
                    }
                    else {
                        cellX = flatZ;
                        cellZ = wide - def.cell - flatX;
                    }
                    int cornerX = originX + cellX;
                    int cornerZ = originZ + cellZ;
                    if (!window.intersectsWith(cornerX, cornerZ, cornerX + def.cell - 1, cornerZ + def.cell - 1)) { continue; }
                    long cellSeed = MathUtil.mix(seed, column, layer, row);
                    String named = PickDef.pick(held.palette.get(mark), new Random(cellSeed), null);
                    if (named == null || named.isEmpty()) { continue; }
                    Template piece = world.getStructureTemplateManager().get(host, new ResourceLocation(named));
                    if (piece == null) {
                        ContentLog.LOGGER.errorOnce(MISSING, named, "Structure map {} places structure '{}', which could not be loaded, so its cells stay empty", def.key, named);
                        continue;
                    }
                    ITemplateProcessor first = def.fills() ? (placed, at, info) -> ContentMapFill.fill(def.fillRules, cellSeed, at, info, null, null) : null;
                    ContentImprint.place(world, piece, cornerX, cornerZ, layerY, turn, new Random(cellSeed), window, first, filter);
                    placedCells++;
                }
            }
        }
        return placedCells;
    }
}
