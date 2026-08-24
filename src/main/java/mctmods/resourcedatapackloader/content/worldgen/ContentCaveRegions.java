package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.ContentRegistry;
import mctmods.resourcedatapackloader.content.def.CaveRegionDef;
import mctmods.resourcedatapackloader.content.def.PickDef;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IMinMaxHeight;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;
import mctmods.resourcedatapackloader.util.Config;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;

public final class ContentCaveRegions {
    private static final int OFFSET = 8;
    private static final int MEMO_LIMIT = 4096;
    private static final ThreadLocal<Map<Long, Object>> MEMO = ThreadLocal.withInitial(HashMap::new);
    private static final Object NONE = new Object();
    private static final Map<Integer, List<CaveRegionDef>> BY_DIMENSION = new HashMap<>();

    private ContentCaveRegions() {}

    public static boolean any() { return !ContentRegistry.caveRegions().isEmpty(); }

    @Nullable public static CaveRegionDef regionAt(World world, int x, int y, int z) {
        List<CaveRegionDef> defs = forDimension(world.provider.getDimension());
        if (defs.isEmpty()) { return null; }
        int qx = x >> 2;
        int qy = y >> 2;
        int qz = z >> 2;
        long memoKey = world.getSeed() * 0x9E3779B97F4A7C15L
                ^ (((long) world.provider.getDimension() & 0xFF) << 54) ^ (((long) qx & 0x3FFFF) << 36) ^ (((long) qy & 0x3FFFF) << 18) ^ ((long) qz & 0x3FFFF);
        Map<Long, Object> memo = MEMO.get();
        Object held = memo.get(memoKey);
        if (held != null) {
            CaveRegionDef cached = held == NONE ? null : (CaveRegionDef) held;
            return clampBand(cached, y);
        }
        int cellsXZ = Math.max(16, ContentControl.number(ContentControl.TERRAIN, "caveRegionCells", Config.worldgen.caveRegionCells));
        int cellsY = Math.max(16, ContentControl.number(ContentControl.TERRAIN, "caveRegionCellsY", Config.worldgen.caveRegionCellsY));
        CaveRegionDef found = resolve(world.getSeed(), defs, qx, qy, qz, cellsXZ >> 2, cellsY >> 2);
        if (memo.size() > MEMO_LIMIT) { memo.clear(); }
        memo.put(memoKey, found == null ? NONE : found);
        return clampBand(found, y);
    }

    @Nullable private static CaveRegionDef clampBand(@Nullable CaveRegionDef def, int y) {
        if (def == null || y < def.minHeight || y > def.maxHeight) { return null; }
        return def;
    }

    @Nullable private static CaveRegionDef resolve(long seed, List<CaveRegionDef> defs, int qx, int qy, int qz, int spanXZ, int spanY) {
        int cellX = Math.floorDiv(qx, spanXZ);
        int cellY = Math.floorDiv(qy, spanY);
        int cellZ = Math.floorDiv(qz, spanXZ);
        long bestDistance = Long.MAX_VALUE;
        long bestHash = 0;
        int bestCenterY = 0;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    int cx = cellX + dx;
                    int cy = cellY + dy;
                    int cz = cellZ + dz;
                    long cellHash = mix(seed, cx, cy, cz);
                    long jx = cx * (long) spanXZ + Math.floorMod(cellHash, spanXZ);
                    long jy = cy * (long) spanY + Math.floorMod(cellHash >>> 20, spanY);
                    long jz = cz * (long) spanXZ + Math.floorMod(cellHash >>> 40, spanXZ);
                    long offX = jx - qx;
                    long offY = (jy - qy) * spanXZ / Math.max(1, spanY);
                    long offZ = jz - qz;
                    long distance = offX * offX + offY * offY + offZ * offZ;
                    if (distance < bestDistance) {
                        bestDistance = distance;
                        bestHash = cellHash;
                        bestCenterY = (int) (jy << 2);
                    }
                }
            }
        }
        return regionForCell(bestHash, bestCenterY, defs);
    }

    @Nullable private static CaveRegionDef regionForCell(long cellHash, int centerY, List<CaveRegionDef> defs) {
        int plain = Math.max(0, ContentControl.number(ContentControl.TERRAIN, "caveRegionPlainWeight", Config.worldgen.caveRegionPlainWeight));
        int total = plain;
        for (CaveRegionDef def : defs) {
            if (centerY >= def.minHeight && centerY <= def.maxHeight) { total += def.weight; }
        }
        if (total <= 0) { return null; }
        long roll = Math.floorMod(cellHash >>> 13, total);
        if (roll < plain) { return null; }
        roll -= plain;
        for (CaveRegionDef def : defs) {
            if (centerY < def.minHeight || centerY > def.maxHeight) { continue; }
            if (roll < def.weight) { return def; }
            roll -= def.weight;
        }
        return null;
    }

    public static void placeStructures(World world, int chunkX, int chunkZ) {
        if (!(world instanceof WorldServer)) { return; }
        List<CaveRegionDef> defs = forDimension(world.provider.getDimension());
        boolean any = false;
        for (CaveRegionDef def : defs) {
            if (def.hasStructures()) {
                any = true;
                break;
            }
        }
        if (!any) { return; }
        int cellsXZ = Math.max(16, ContentControl.number(ContentControl.TERRAIN, "caveRegionCells", Config.worldgen.caveRegionCells));
        int cellsY = Math.max(16, ContentControl.number(ContentControl.TERRAIN, "caveRegionCellsY", Config.worldgen.caveRegionCellsY));
        int spanXZ = cellsXZ >> 2;
        int spanY = cellsY >> 2;
        int blockX0 = chunkX * 16 + OFFSET;
        int blockZ0 = chunkZ * 16 + OFFSET;
        boolean rubic = ((IRubicWorld) world).rdpl$isRubicWorld();
        int floor = rubic ? ((IMinMaxHeight) world).rdpl$getMinHeight() : 0;
        int ceiling = rubic ? ((IMinMaxHeight) world).rdpl$getMaxHeight() : 256;
        int cellY0 = Math.floorDiv(Math.floorDiv(floor, 4), spanY);
        int cellY1 = Math.floorDiv(Math.floorDiv(ceiling - 1, 4), spanY);
        int qx0 = blockX0 >> 2;
        int qz0 = blockZ0 >> 2;
        for (int cellX = Math.floorDiv(qx0 + 3 - (spanXZ - 1), spanXZ); cellX <= Math.floorDiv(qx0 + 3, spanXZ); cellX++) {
            for (int cellZ = Math.floorDiv(qz0 + 3 - (spanXZ - 1), spanXZ); cellZ <= Math.floorDiv(qz0 + 3, spanXZ); cellZ++) {
                for (int cellY = cellY0; cellY <= cellY1; cellY++) {
                    long cellHash = mix(world.getSeed(), cellX, cellY, cellZ);
                    int wx = (int) ((cellX * (long) spanXZ + Math.floorMod(cellHash, spanXZ)) << 2);
                    int wz = (int) ((cellZ * (long) spanXZ + Math.floorMod(cellHash >>> 40, spanXZ)) << 2);
                    if (wx < blockX0 || wx > blockX0 + 15 || wz < blockZ0 || wz > blockZ0 + 15) { continue; }
                    int centerY = (int) ((cellY * (long) spanY + Math.floorMod(cellHash >>> 20, spanY)) << 2);
                    CaveRegionDef region = regionForCell(cellHash, centerY, defs);
                    if (region == null || !region.hasStructures()) { continue; }
                    if (region.structureChance < 1.0F && ((cellHash >>> 24) & 0xFFFFF) / (float) (1 << 20) >= region.structureChance) { continue; }
                    int lowest = Math.max(region.minHeight, floor + 1);
                    int highest = Math.min(region.maxHeight, ceiling - 1);
                    int start = Math.max(lowest, Math.min(highest, centerY));
                    int seat = caveFloor(world, wx, wz, start, lowest, highest);
                    if (seat == Integer.MIN_VALUE) { continue; }
                    place((WorldServer) world, region, cellHash, wx, seat, wz);
                }
            }
        }
    }

    private static int caveFloor(World world, int x, int z, int start, int lowest, int highest) {
        for (int y = start; y >= lowest; y--) {
            if (seated(world, x, y, z)) { return y; }
        }
        for (int y = start + 1; y <= highest; y++) {
            if (seated(world, x, y, z)) { return y; }
        }
        return Integer.MIN_VALUE;
    }

    private static boolean seated(World world, int x, int y, int z) {
        BlockPos at = new BlockPos(x, y, z);
        if (!world.isBlockLoaded(at) || !world.isAirBlock(at)) { return false; }
        IBlockState below = world.getBlockState(at.down());
        return below.getMaterial() != Material.AIR && !below.getMaterial().isLiquid();
    }

    private static void place(WorldServer world, CaveRegionDef region, long cellHash, int x, int y, int z) {
        Random random = new Random(cellHash);
        String named = PickDef.pick(region.structures, random, "");
        if (named == null || named.isEmpty()) { return; }
        MinecraftServer host = world.getMinecraftServer();
        Template loaded = world.getStructureTemplateManager().get(host, new ResourceLocation(named));
        if (loaded == null) {
            ContentLog.LOGGER.error("Cave region {} places structure '{}', which could not be loaded, so nothing generates", region.key, named);
            return;
        }
        Rotation rotation = Rotation.values()[random.nextInt(4)];
        PlacementSettings settings = new PlacementSettings();
        settings.setRotation(rotation);
        settings.setRandom(random);
        BlockPos span = loaded.transformedSize(rotation);
        int backX = rotation == Rotation.CLOCKWISE_90 || rotation == Rotation.CLOCKWISE_180 ? span.getX() - 1 : 0;
        int backZ = rotation == Rotation.CLOCKWISE_180 || rotation == Rotation.COUNTERCLOCKWISE_90 ? span.getZ() - 1 : 0;
        BlockPos fitted = new BlockPos(x - span.getX() / 2 + backX, y, z - span.getZ() / 2 + backZ);
        if (span.getX() > 16 || span.getZ() > 16) {
            if (!ContentCascade.loaded(world, fitted, Math.max(span.getX(), span.getZ()))) { return; }
        }
        loaded.addBlocksToWorld(world, fitted, settings, 2);
    }

    private static long mix(long seed, int x, int y, int z) {
        long h = seed ^ 0x28B7BD766A05068BL;
        h ^= x * 0x2545F4914F6CDD1DL;
        h ^= (long) y * 0x6C62272E07BB0142L;
        h ^= (long) z * 0xCBF29CE484222325L;
        h ^= h >>> 33;
        h *= 0xFF51AFD7ED558CCDL;
        h ^= h >>> 33;
        return h;
    }

    public static void decorate(World world, int chunkX, int chunkZ, Random random) {
        List<CaveRegionDef> defs = forDimension(world.provider.getDimension());
        if (defs.isEmpty()) { return; }
        int lowest = Integer.MAX_VALUE;
        int highest = Integer.MIN_VALUE;
        boolean covered = false;
        for (CaveRegionDef def : defs) {
            if (!def.hasCovers()) { continue; }
            covered = true;
            lowest = Math.min(lowest, def.minHeight);
            highest = Math.max(highest, def.maxHeight);
        }
        if (!covered) { return; }
        int floor = ((IRubicWorld) world).rdpl$isRubicWorld() ? ((IMinMaxHeight) world).rdpl$getMinHeight() : 0;
        lowest = Math.max(lowest, floor + 1);
        highest = Math.min(highest, world.getActualHeight() - 2);
        if (lowest > highest) { return; }
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int x = chunkX * 16 + OFFSET; x < chunkX * 16 + OFFSET + 16; x++) {
            for (int z = chunkZ * 16 + OFFSET; z < chunkZ * 16 + OFFSET + 16; z++) {
                IBlockState below = world.getBlockState(pos.setPos(x, lowest - 1, z));
                for (int y = lowest; y <= highest + 1; y++) {
                    IBlockState state = world.getBlockState(pos.setPos(x, y, z));
                    boolean airNow = state.getMaterial() == Material.AIR;
                    boolean airBelow = below.getMaterial() == Material.AIR;
                    if (airNow && !airBelow && y <= highest) { coverFloor(world, random, x, y, z, below); }
                    else if (!airNow && airBelow && y - 1 >= lowest) { coverCeiling(world, random, x, y, z, state); }
                    below = state;
                }
            }
        }
    }

    private static void coverFloor(World world, Random random, int x, int y, int z, IBlockState under) {
        CaveRegionDef region = regionAt(world, x, y, z);
        if (region == null) { return; }
        IBlockState cover = region.floorState();
        if (cover == null || region.rejectsCover(under)) { return; }
        if (random.nextFloat() >= region.floorChance) { return; }
        BlockPos pos = new BlockPos(x, y, z);
        if (world.canSeeSky(pos)) { return; }
        world.setBlockState(pos.down(), cover, 2 | 16);
    }

    private static void coverCeiling(World world, Random random, int x, int y, int z, IBlockState ceiling) {
        CaveRegionDef region = regionAt(world, x, y - 1, z);
        if (region == null) { return; }
        IBlockState cover = region.ceilingState();
        if (cover == null || region.rejectsCover(ceiling)) { return; }
        if (random.nextFloat() >= region.ceilingChance) { return; }
        BlockPos pos = new BlockPos(x, y - 1, z);
        if (world.canSeeSky(pos)) { return; }
        world.setBlockState(pos.up(), cover, 2 | 16);
    }

    private static List<CaveRegionDef> forDimension(int dimension) {
        List<CaveRegionDef> cached = BY_DIMENSION.get(dimension);
        if (cached != null) { return cached; }
        List<CaveRegionDef> active = new ArrayList<>();
        for (CaveRegionDef def : ContentRegistry.caveRegions()) {
            if (def.weight > 0 && def.inDimension(dimension)) { active.add(def); }
        }
        BY_DIMENSION.put(dimension, active);
        return active;
    }
}
