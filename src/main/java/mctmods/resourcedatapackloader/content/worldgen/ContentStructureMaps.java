package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.util.compat.CompatHandler;
import mctmods.resourcedatapackloader.util.world.GenHeights;
import mctmods.resourcedatapackloader.content.ContentParserWorlds;
import mctmods.resourcedatapackloader.content.def.PickDef;
import mctmods.resourcedatapackloader.content.def.StructureMapDef;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardSurface;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.MathUtil;
import mctmods.resourcedatapackloader.util.Turns;
import mctmods.resourcedatapackloader.util.world.BlockFlags;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public class ContentStructureMaps {
    private static final Map<String, StructureMapDef> DEFS = new LinkedHashMap<>();
    private static final Set<String> MISSING = new HashSet<>();
    private static final int OFFSET = 8;
    private static final int FLAGS = BlockFlags.UPDATE_CLIENTS;
    private static boolean loaded = false;

    public static void load() {
        if (loaded) { return; }
        loaded = true;
        Json.eachFile(PackManager.STRUCTUREMAPS, "structure map", (key, contents) -> {
            StructureMapDef def = ContentParserWorlds.structureMap(key, contents);
            if (def != null) { DEFS.put(key.toString(), def); }
        });
        if (!DEFS.isEmpty()) { ContentLog.LOGGER.debug("Loaded {} structure map(s): {}", DEFS.size(), DEFS.keySet()); }
    }

    public static boolean none() { return DEFS.isEmpty(); }

    static Collection<StructureMapDef> defs() { return DEFS.values(); }

    @Nullable public static StructureMapDef byName(String name) { return name == null || name.isEmpty() ? null : DEFS.get(name.toLowerCase(Locale.ROOT)); }

    public static void place(World world, int chunkX, int chunkZ) {
        if (DEFS.isEmpty() || !(world instanceof WorldServer)) { return; }
        StructureBoundingBox window = new StructureBoundingBox(chunkX * 16 + OFFSET, GenHeights.floor(world, 0), chunkZ * 16 + OFFSET, chunkX * 16 + OFFSET + 15, GenHeights.ceiling(world, CompatHandler.VANILLA_HEIGHT) - 1, chunkZ * 16 + OFFSET + 15);
        sites(world, window, (def, seed, originX, originZ) -> {
            slices(world, def, seed, originX, originZ, window);
            return false;
        });
    }

    public static boolean claims(World world, StructureBoundingBox area, Predicate<StructureBoundingBox> hit) {
        if (DEFS.isEmpty()) { return false; }
        return sites(world, area, (def, seed, originX, originZ) -> hit.test(footprint(def, seed, originX, originZ)));
    }

    private interface ISite { boolean at(StructureMapDef def, long seed, int originX, int originZ); }

    private static boolean sites(World world, StructureBoundingBox area, ISite site) {
        int dimension = world.provider.getDimension();
        for (StructureMapDef def : DEFS.values()) {
            if (!def.allowsDimension(dimension)) { continue; }
            if (def.at != null && site.at(def, MathUtil.mix(world.getSeed() ^ def.key.hashCode(), def.at[0], 0, def.at[1]), def.at[0], def.at[1])) { return true; }
            if (def.spacing <= 0) { continue; }
            int pitch = def.spacing * 16;
            for (int gridX = Math.floorDiv(area.minX - def.widest, pitch); gridX <= Math.floorDiv(area.maxX, pitch); gridX++) {
                for (int gridZ = Math.floorDiv(area.minZ - def.widest, pitch); gridZ <= Math.floorDiv(area.maxZ, pitch); gridZ++) {
                    long seed = MathUtil.mix(world.getSeed() ^ def.key.hashCode(), gridX, 1, gridZ);
                    if (Math.floorMod(seed >>> 16, 100) >= def.chance) { continue; }
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
        boolean swapped = turn == Rotation.CLOCKWISE_90 || turn == Rotation.COUNTERCLOCKWISE_90;
        int wide = def.cellsWide * def.cell;
        int deep = def.cellsDeep * def.cell;
        int spanX = swapped ? deep : wide;
        int spanZ = swapped ? wide : deep;
        return new StructureBoundingBox(originX, 0, originZ, originX + spanX - 1, 0, originZ + spanZ - 1);
    }

    private static void slices(World world, StructureMapDef def, long seed, int originX, int originZ, StructureBoundingBox window) {
        Rotation turn = turn(seed);
        StructureBoundingBox footprint = footprint(def, seed, originX, originZ);
        if (!footprint.intersectsWith(window.minX, window.minZ, window.maxX, window.maxZ)) { return; }
        int anchor = BeardSurface.surfaceAt(world, originX + footprint.getXSize() / 2, originZ + footprint.getZSize() / 2);
        if (anchor < 0) { anchor = world.getSeaLevel() - 1; }
        int base = Math.max(def.baseY(anchor, seed), GenHeights.floor(world, 0) + 1);
        int placedCells = cells(world, def, seed, turn, originX, base, originZ, window);
        if (placedCells > 0 && window.isVecInside(new BlockPos(originX, 64, originZ)) && ContentLog.LOGGER.debugEnabled()) {
            ContentLog.LOGGER.debug("Structure map {} builds at {}, {}, {}, turned {}, its ground layer floored at y {}", def.key, originX, base, originZ, turn, base + def.ground * def.cell);
        }
    }

    public static int cells(World world, StructureMapDef def, long seed, Rotation turn, int originX, int base, int originZ, StructureBoundingBox window) {
        int wide = def.cellsWide * def.cell;
        int deep = def.cellsDeep * def.cell;
        WorldServer server = (WorldServer) world;
        MinecraftServer host = server.getMinecraftServer();
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
                    if (cornerX + def.cell - 1 < window.minX || cornerX > window.maxX || cornerZ + def.cell - 1 < window.minZ || cornerZ > window.maxZ) { continue; }
                    long cellSeed = MathUtil.mix(seed, column, layer, row);
                    String named = PickDef.pick(held.palette.get(mark), new Random(cellSeed), null);
                    if (named == null || named.isEmpty()) { continue; }
                    Template piece = server.getStructureTemplateManager().get(host, new ResourceLocation(named));
                    if (piece == null) {
                        ContentLog.LOGGER.errorOnce(MISSING, named, "Structure map {} places structure '{}', which could not be loaded, so its cells stay empty", def.key, named);
                        continue;
                    }
                    BlockPos span = piece.transformedSize(turn);
                    int backX = ContentImprint.backX(turn, span);
                    int backZ = ContentImprint.backZ(turn, span);
                    PlacementSettings settings = new PlacementSettings();
                    settings.setRotation(turn);
                    settings.setRandom(new Random(cellSeed));
                    settings.setBoundingBox(window);
                    BlockPos placeAt = new BlockPos(cornerX + backX, layerY, cornerZ + backZ);
                    if (def.fills()) { piece.addBlocksToWorld(world, placeAt, (placed, at, info) -> ContentMapFill.fill(def, cellSeed, at, info), settings, FLAGS); }
                    else { piece.addBlocksToWorld(world, placeAt, settings, FLAGS); }
                    placedCells++;
                }
            }
        }
        return placedCells;
    }
}
