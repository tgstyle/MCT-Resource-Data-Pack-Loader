package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IMinMaxHeight;
import mctmods.resourcedatapackloader.content.ContentParser;
import mctmods.resourcedatapackloader.content.def.PickDef;
import mctmods.resourcedatapackloader.content.def.StructureMapDef;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardSurface;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.MathUtil;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;

public class ContentStructureMaps {
    private static final Map<String, StructureMapDef> DEFS = new LinkedHashMap<>();
    private static final Set<String> MISSING = new HashSet<>();
    private static final Rotation[] TURNS = { Rotation.NONE, Rotation.CLOCKWISE_90, Rotation.CLOCKWISE_180, Rotation.COUNTERCLOCKWISE_90 };
    private static final int OFFSET = 8;
    private static final int FLAGS = 2;
    private static boolean loaded = false;

    public static void load() {
        if (loaded) { return; }
        loaded = true;
        PackManager.get().forEach(PackManager.STRUCTUREMAPS, PackManager.JSON, (namespace, path, contents) -> {
            ResourceLocation key = new ResourceLocation(namespace, path);
            StructureMapDef def = ContentParser.structureMap(key, contents);
            if (def != null) { DEFS.put(key.toString(), def); }
        });
        if (!DEFS.isEmpty()) { ContentLog.LOGGER.debug("Loaded {} structure map(s): {}", DEFS.size(), DEFS.keySet()); }
    }

    public static boolean any() { return !DEFS.isEmpty(); }

    @Nullable public static StructureMapDef byName(String name) { return name == null || name.isEmpty() ? null : DEFS.get(name.toLowerCase(java.util.Locale.ROOT)); }

    public static void place(World world, int chunkX, int chunkZ) {
        if (DEFS.isEmpty() || !(world instanceof WorldServer)) { return; }
        int dimension = world.provider.getDimension();
        StructureBoundingBox window = new StructureBoundingBox(chunkX * 16 + OFFSET, ((IMinMaxHeight) world).rdpl$getMinHeight(), chunkZ * 16 + OFFSET, chunkX * 16 + OFFSET + 15, ((IMinMaxHeight) world).rdpl$getMaxHeight() - 1, chunkZ * 16 + OFFSET + 15);
        for (StructureMapDef def : DEFS.values()) {
            if (!def.allowsDimension(dimension)) { continue; }
            if (def.at != null) {
                long seed = MathUtil.mix(world.getSeed() ^ def.key.hashCode(), def.at[0], 0, def.at[1]);
                slices(world, def, seed, def.at[0], def.at[1], window);
            }
            if (def.spacing <= 0) { continue; }
            int pitch = def.spacing * 16;
            for (int gridX = Math.floorDiv(window.minX - def.widest, pitch); gridX <= Math.floorDiv(window.maxX, pitch); gridX++) {
                for (int gridZ = Math.floorDiv(window.minZ - def.widest, pitch); gridZ <= Math.floorDiv(window.maxZ, pitch); gridZ++) {
                    long seed = MathUtil.mix(world.getSeed() ^ def.key.hashCode(), gridX, 1, gridZ);
                    if (Math.floorMod(seed >>> 16, 100) >= def.chance) { continue; }
                    int give = Math.max(1, pitch - def.widest);
                    int originX = gridX * pitch + (int) Math.floorMod(seed, give);
                    int originZ = gridZ * pitch + (int) Math.floorMod(seed >>> 40, give);
                    slices(world, def, seed, originX, originZ, window);
                }
            }
        }
    }

    private static void slices(World world, StructureMapDef def, long seed, int originX, int originZ, StructureBoundingBox window) {
        Rotation turn = TURNS[(int) Math.floorMod(seed >>> 8, TURNS.length)];
        boolean swapped = turn == Rotation.CLOCKWISE_90 || turn == Rotation.COUNTERCLOCKWISE_90;
        int wide = def.cellsWide * def.cell;
        int deep = def.cellsDeep * def.cell;
        int spanX = swapped ? deep : wide;
        int spanZ = swapped ? wide : deep;
        if (originX + spanX - 1 < window.minX || originX > window.maxX || originZ + spanZ - 1 < window.minZ || originZ > window.maxZ) { return; }
        int anchor = BeardSurface.surfaceAt(world, originX + spanX / 2, originZ + spanZ / 2);
        if (anchor < 0) { anchor = world.getSeaLevel() - 1; }
        int base = anchor + 1 - def.ground * def.cell;
        int placedCells = cells(world, def, seed, turn, originX, base, originZ, window);
        if (placedCells > 0 && window.isVecInside(new BlockPos(originX, 64, originZ)) && ContentLog.LOGGER.debugEnabled()) {
            ContentLog.LOGGER.debug("Structure map {} builds at {}, {}, {}, turned {}, its ground layer floored at y {}", def.key, originX, base + def.ground * def.cell, originZ, turn, base + def.ground * def.cell);
        }
    }

    public static int cells(World world, StructureMapDef def, long seed, Rotation turn, int originX, int base, int originZ, StructureBoundingBox window) {
        int wide = def.cellsWide * def.cell;
        int deep = def.cellsDeep * def.cell;
        WorldServer server = (WorldServer) world;
        MinecraftServer host = server.getMinecraftServer();
        int placedCells = 0;
        int floor = ((IMinMaxHeight) world).rdpl$getMinHeight();
        int ceiling = ((IMinMaxHeight) world).rdpl$getMaxHeight() - 1;
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
                        if (MISSING.add(named)) { ContentLog.LOGGER.error("Structure map {} places structure '{}', which could not be loaded, so its cells stay empty", def.key, named); }
                        continue;
                    }
                    BlockPos span = piece.transformedSize(turn);
                    int backX = turn == Rotation.CLOCKWISE_90 || turn == Rotation.CLOCKWISE_180 ? span.getX() - 1 : 0;
                    int backZ = turn == Rotation.CLOCKWISE_180 || turn == Rotation.COUNTERCLOCKWISE_90 ? span.getZ() - 1 : 0;
                    PlacementSettings settings = new PlacementSettings();
                    settings.setRotation(turn);
                    settings.setRandom(new Random(cellSeed));
                    settings.setBoundingBox(window);
                    piece.addBlocksToWorld(world, new BlockPos(cornerX + backX, layerY, cornerZ + backZ), settings, FLAGS);
                    placedCells++;
                }
            }
        }
        return placedCells;
    }
}
