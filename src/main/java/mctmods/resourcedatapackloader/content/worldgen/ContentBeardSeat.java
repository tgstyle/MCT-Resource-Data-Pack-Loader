package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IMapGenStructure;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public final class ContentBeardSeat {
    private static final int RADIUS = 12;
    private static final int SIZE = 24;
    private static final double SCALE = 8.0D;
    private static final float[] KERNEL = new float[SIZE * SIZE * SIZE];
    static {
        for (int z = 0; z < SIZE; z++) {
            for (int x = 0; x < SIZE; x++) {
                for (int y = 0; y < SIZE; y++) { KERNEL[z * SIZE * SIZE + x * SIZE + y] = (float) seed(x - RADIUS, (double) (y - RADIUS) + 0.5D, z - RADIUS); }
            }
        }
    }

    public enum Mode { NONE, BURY, BEARD_THIN, BEARD_BOX, ENCAPSULATE }

    static final Map<String, Mode> MODES = new LinkedHashMap<>();
    private static boolean modesLoaded;
    private static boolean applying;
    private static final ChunkPrimer UNUSED = new ChunkPrimer();

    private ContentBeardSeat() {}

    public static void apply(World world, MapGenStructure[] generators, String[] names, double[] heightMap, int chunkX, int chunkZ) {
        if (applying) { return; }
        applying = true;
        try { seat(world, generators, names, heightMap, chunkX, chunkZ); }
        finally { applying = false; }
    }

    private static void seat(World world, MapGenStructure[] generators, String[] names, double[] heightMap, int chunkX, int chunkZ) {
        loadModes();
        double[] before = heightMap.clone();
        int blockX = chunkX << 4;
        int blockZ = chunkZ << 4;
        StructureBoundingBox reach = new StructureBoundingBox(blockX - RADIUS, 0, blockZ - RADIUS, blockX + 15 + RADIUS, 255, blockZ + 15 + RADIUS);
        List<StructureBoundingBox> boxes = new ArrayList<>();
        IntList bases = new IntArrayList();
        List<Mode> modes = new ArrayList<>();
        for (int at = 0; at < generators.length; at++) {
            Mode mode = MODES.getOrDefault(names[at], Mode.NONE);
            if (mode == Mode.NONE) { continue; }
            ContentBeard.samplerWorld = world;
            generators[at].generate(world, chunkX, chunkZ, UNUSED);
            ContentBeard.samplerWorld = null;
            for (StructureStart start : ((IMapGenStructure) generators[at]).rdpl$getStructureMap().values()) {
                if (start == null || !start.isSizeableStructure() || !start.getBoundingBox().intersectsWith(reach)) { continue; }
                for (StructureComponent piece : start.getComponents()) {
                    if (!piece.getBoundingBox().intersectsWith(reach)) { continue; }
                    if (piece instanceof StructureVillagePieces.Start) { continue; }
                    boxes.add(piece.getBoundingBox());
                    bases.add(piece.getBoundingBox().minY + 1);
                    modes.add(mode);
                }
            }
        }
        System.arraycopy(before, 0, heightMap, 0, heightMap.length);
        if (boxes.isEmpty()) { return; }
        if (ContentLog.LOGGER.debugEnabled()) {
            int lowest = Integer.MAX_VALUE;
            int highest = Integer.MIN_VALUE;
            for (int base : bases) {
                lowest = Math.min(lowest, base);
                highest = Math.max(highest, base);
            }
            ContentLog.LOGGER.debug("Seating {} structure piece(s) under chunk {}, {}, on bases from y {} to y {}", boxes.size(), chunkX, chunkZ, lowest, highest);
        }
        int at = 0;
        for (int gridX = 0; gridX < 5; gridX++) {
            int x = blockX + gridX * 4;
            for (int gridZ = 0; gridZ < 5; gridZ++) {
                int z = blockZ + gridZ * 4;
                for (int gridY = 0; gridY < 33; gridY++) {
                    heightMap[at] += sink(boxes, bases, modes, x, gridY * 8, z) * SCALE;
                    at++;
                }
            }
        }
    }

    private static double sink(List<StructureBoundingBox> boxes, IntList bases, List<Mode> modes, int x, int y, int z) {
        double sum = 0.0D;
        for (int i = 0; i < boxes.size(); i++) {
            StructureBoundingBox box = boxes.get(i);
            int dx = Math.max(0, Math.max(box.minX - x, x - box.maxX));
            int dz = Math.max(0, Math.max(box.minZ - z, z - box.maxZ));
            if (dx > RADIUS || dz > RADIUS) { continue; }
            int base = bases.getInt(i);
            int dy = y - base;
            Mode mode = modes.get(i);
            if (mode == Mode.BURY) { sum += bury(dx, dy / 2.0D, dz); }
            else if (mode == Mode.BEARD_THIN) { sum += beard(dx, dy, dz, dy) * 0.8D; }
            else if (mode == Mode.BEARD_BOX) { sum += beard(dx, Math.max(0, Math.max(base - y, y - box.maxY)), dz, dy) * 0.8D; }
            else if (mode == Mode.ENCAPSULATE) { sum += bury(dx / 2.0D, Math.max(0, Math.max(box.minY - y, y - box.maxY)) / 2.0D, dz / 2.0D) * 0.8D; }
        }
        return sum;
    }

    private static double bury(double x, double y, double z) {
        double length = Math.sqrt(x * x + y * y + z * z);
        if (length >= 6.0D) { return 0.0D; }
        return 1.0D - length / 6.0D;
    }

    static void loadModes() {
        if (modesLoaded) { return; }
        modesLoaded = true;
        MODES.put("villages", Mode.BEARD_THIN);
        MODES.put("mansions", Mode.BEARD_THIN);
        for (String entry : ContentControl.list(ContentControl.STRUCTURES, "structureAdaptation", Config.worldgen.structureAdaptation)) {
            String[] parts = entry.split("=", 2);
            if (parts.length != 2) {
                ContentLog.LOGGER.error("structureAdaptation entry '{}' is not structure=mode, ignoring it", entry);
                continue;
            }
            String name = parts[0].trim().toLowerCase(Locale.ROOT);
            String asked = parts[1].trim().toUpperCase(Locale.ROOT);
            if (("temples".equals(name) || "mansions".equals(name)) && !"NONE".equals(asked) && !"BEARD_THIN".equals(asked)) {
                ContentLog.LOGGER.error("structureAdaptation asks for {}={}, but that structure settles itself only as it is built, so the terrain cannot be shaped for it beforehand. Only beard_thin is offered there, which banks the ground around it once it stands", name, parts[1].trim());
                continue;
            }
            try { MODES.put(name, Mode.valueOf(asked)); }
            catch (IllegalArgumentException ex) { ContentLog.LOGGER.error("structureAdaptation entry '{}' asks for mode '{}', which is not none, bury, beard_thin, beard_box or encapsulate, ignoring it", entry, parts[1].trim()); }
        }
    }

    private static double beard(int x, int y, int z, int height) {
        int atX = x + RADIUS;
        int atY = y + RADIUS;
        int atZ = z + RADIUS;
        if (atX < 0 || atX >= SIZE || atY < 0 || atY >= SIZE || atZ < 0 || atZ >= SIZE) { return 0.0D; }
        double lifted = (double) height + 0.5D;
        double squared = (double) x * (double) x + lifted * lifted + (double) z * (double) z;
        double falloff = -lifted * MathHelper.fastInvSqrt(squared / 2.0D) / 2.0D;
        return falloff * (double) KERNEL[atZ * SIZE * SIZE + atX * SIZE + atY];
    }

    private static double seed(int x, double y, int z) {
        double squared = (double) x * (double) x + y * y + (double) z * (double) z;
        return Math.pow(Math.E, -squared / 16.0D);
    }
}
