package mctmods.resourcedatapackloader.content.rubic.lighting;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IColumn;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IColumnInternal;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.ICube;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IMinMaxHeight;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.ICubeProvider;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;
import mctmods.resourcedatapackloader.util.compat.GcRubicSunlight;
import mctmods.resourcedatapackloader.util.Coords;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import java.util.Arrays;
import java.util.Objects;
import javax.annotation.Nullable;

public final class RubicLightEngine implements ICubeLightEngine {
    private static final IBlockState AIR = Objects.requireNonNull(Blocks.AIR).getDefaultState();
    private static final int MAX_LIGHT = 15;
    private static final int SPILL_AT = 1 << 20;
    private static final int[] STEP_X = {-1, 1, 0, 0, 0, 0};
    private static final int[] STEP_Y = {0, 0, -1, 1, 0, 0};
    private static final int[] STEP_Z = {0, 0, 0, 0, -1, 1};
    private final World world;
    private final int lowestCube;
    private final int highestCube;
    private final LightQueue[] scheduled = new LightQueue[EnumSkyBlock.values().length];
    private final LightQueue[] brightening = new LightQueue[MAX_LIGHT + 1];
    private final LightQueue[] darkening = new LightQueue[MAX_LIGHT + 1];
    private final BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
    private final LongOpenHashSet owed = new LongOpenHashSet();
    private final LongArrayList ready = new LongArrayList();
    private final ICube[] nearby = new ICube[27];
    private int probedSlots;
    private int nearbyCubeX = Integer.MIN_VALUE;
    private int nearbyCubeY = Integer.MIN_VALUE;
    private int nearbyCubeZ = Integer.MIN_VALUE;
    private boolean working;
    private boolean seeding;

    public RubicLightEngine(World world) {
        this.world = world;
        this.lowestCube = Coords.blockToCube(((IRubicWorld) world).rdpl$getMinHeight());
        this.highestCube = Coords.blockToCube(((IRubicWorld) world).rdpl$getMaxHeight()) - 1;
        for (int i = 0; i < scheduled.length; i++) { scheduled[i] = new LightQueue(); }
        for (int i = 0; i <= MAX_LIGHT; i++) {
            brightening[i] = new LightQueue();
            darkening[i] = new LightQueue();
        }
    }

    @Override public String getId() { return "rubic"; }

    @Override public void cubeStorageMade(ICube cube, ExtendedBlockStorage storage) {
        if (world.isRemote || !world.provider.hasSkyLight() || !cube.isCubeLoaded()) { return; }
        Chunk column = cube.getColumn();
        int floor = storage.getYLocation();
        int ceiling = floor + 15;
        for (int localX = 0; localX < 16; localX++) {
            for (int localZ = 0; localZ < 16; localZ++) {
                int top = topAt(column, localX, localZ);
                if (top >= floor || blockedAbove(column, localX, localZ, ceiling + 1)) { continue; }
                for (int localY = 0; localY < 16; localY++) { storage.setSkyLight(localX, localY, localZ, MAX_LIGHT); }
                seedSides(column, localX, localZ, top, floor, ceiling);
            }
        }
    }

    @Override public void updateBetween(Chunk column, int localX, int y1, int y2, int localZ) {
        int x = (column.x << 4) + localX;
        int z = (column.z << 4) + localZ;
        int from = Math.min(y1, y2);
        int to = Math.max(y1, y2);
        int fromCube = Coords.blockToCube(from);
        int toCube = Coords.blockToCube(to);
        for (ICube cube : ((IColumn) column).getLoadedCubes(toCube, fromCube)) {
            int cubeY = cube.getY();
            int base = Coords.cubeToMinBlock(cubeY);
            int minLocal = cubeY == fromCube ? Coords.blockToLocal(from) : 0;
            int maxLocal = cubeY == toCube ? Coords.blockToLocal(to) : 15;
            for (int localY = minLocal; localY <= maxLocal; localY++) { scheduleLightUpdate(EnumSkyBlock.SKY, at.setPos(x, base + localY, z)); }
        }
    }

    @Override public void cubeLoaded(ICube cube) {
        if (!owed.isEmpty() && owed.remove(cubeKey(cube.getX(), cube.getY(), cube.getZ()))) { ready.add(cubeKey(cube.getX(), cube.getY(), cube.getZ())); }
        if (world.isRemote) { return; }
        int minX = cube.getCoords().getMinBlockX();
        int minY = cube.getCoords().getMinBlockY();
        int minZ = cube.getCoords().getMinBlockZ();
        for (int face = 0; face < 6; face++) {
            if (((ICubeProvider) world.getChunkProvider()).getLoadedCube(cube.getX() + STEP_X[face], cube.getY() + STEP_Y[face], cube.getZ() + STEP_Z[face]) == null) { continue; }
            for (int a = 0; a < 16; a++) {
                for (int b = 0; b < 16; b++) {
                    int x = minX + (STEP_X[face] != 0 ? (STEP_X[face] < 0 ? 0 : 15) : a);
                    int y = minY + (STEP_Y[face] != 0 ? (STEP_Y[face] < 0 ? 0 : 15) : (STEP_X[face] != 0 ? a : b));
                    int z = minZ + (STEP_Z[face] != 0 ? (STEP_Z[face] < 0 ? 0 : 15) : b);
                    scheduleLightUpdate(EnumSkyBlock.SKY, at.setPos(x, y, z));
                    scheduleLightUpdate(EnumSkyBlock.BLOCK, at.setPos(x, y, z));
                }
            }
        }
    }

    @Override public void firstLight(ICube cube) {
        seeding = true;
        try { seedFrom(cube); }
        finally { seeding = false; }
    }

    @Override public void reshadeBelow(ICube cube) {
        IColumn column = cube.getColumn();
        for (ICube below : column.getLoadedCubes(cube.getY() - 1, Coords.blockToCube(((IMinMaxHeight) world).rdpl$getMinHeight()))) {
            reshadeStaleSky(below);
        }
    }

    private void reshadeStaleSky(ICube cube) {
        if (!world.provider.hasSkyLight() || cube.getStorage() == null) { return; }
        Chunk column = cube.getColumn();
        int minX = cube.getCoords().getMinBlockX();
        int minY = cube.getCoords().getMinBlockY();
        int minZ = cube.getCoords().getMinBlockZ();
        int maxY = minY + 15;
        for (int localX = 0; localX < 16; localX++) {
            for (int localZ = 0; localZ < 16; localZ++) {
                int from = Math.min(maxY, topAt(column, localX, localZ));
                if (from < minY) { continue; }
                int x = minX + localX;
                int z = minZ + localZ;
                for (int y = from; y >= minY; y--) {
                    if (lightIn(cube, EnumSkyBlock.SKY, x, y, z) == 0) { continue; }
                    scheduleLightUpdate(EnumSkyBlock.SKY, at.setPos(x, y, z));
                }
            }
        }
    }

    private void seedFrom(ICube cube) {
        int minY = cube.getCoords().getMinBlockY();
        int maxY = cube.getCoords().getMaxBlockY();
        int minX = cube.getCoords().getMinBlockX();
        int minZ = cube.getCoords().getMinBlockZ();
        Chunk column = cube.getColumn();
        boolean sky = world.provider.hasSkyLight();
        boolean hasStorage = cube.getStorage() != null;
        for (int localX = 0; localX < 16; localX++) {
            for (int localZ = 0; localZ < 16; localZ++) {
                int x = minX + localX;
                int z = minZ + localZ;
                if (hasStorage) {
                    for (int y = maxY; y >= minY; y--) {
                        if (GcRubicSunlight.lightValue(stateAt(cube, x, y, z), world, at.setPos(x, y, z)) > 0) { scheduleLightUpdate(EnumSkyBlock.BLOCK, at.setPos(x, y, z)); }
                    }
                }
                if (!sky) { continue; }
                int top = topAt(column, localX, localZ);
                if (top < maxY) { fillSkyAbove(cube, x, z, Math.max(minY, top + 1), maxY); }
                if (top >= minY) { darkenColumn(cube, x, z, minY, Math.min(maxY, top)); }
                seedSides(column, localX, localZ, top, minY, maxY);
                if (top > maxY) {
                    seedUnderTop(cube, x, z, minY, maxY);
                    continue;
                }
                if (top < minY - 1) { continue; }
                scheduleLightUpdate(EnumSkyBlock.SKY, at.setPos(x, Math.min(maxY, top + 1), z));
                if (top >= minY && anyNeighbourLower(column, localX, localZ, top)) { scheduleLightUpdate(EnumSkyBlock.SKY, at.setPos(x, top, z)); }
            }
        }
    }

    private boolean blockedAbove(Chunk column, int localX, int localZ, int fromY) {
        int topCube = (((IMinMaxHeight) world).rdpl$getMaxHeight() >> 4) - 1;
        int x = (column.x << 4) + localX;
        int z = (column.z << 4) + localZ;
        for (int cubeY = topCube; cubeY >= Coords.blockToCube(fromY); cubeY--) {
            ICube above = ((ICubeProvider) world.getChunkProvider()).getLoadedCube(column.x, cubeY, column.z);
            if (above == null) { continue; }
            ExtendedBlockStorage held = above.getStorage();
            if (held == null || held.isEmpty()) { continue; }
            int lowest = cubeY == Coords.blockToCube(fromY) ? Coords.blockToLocal(fromY) : 0;
            int base = Coords.cubeToMinBlock(cubeY);
            for (int localY = 15; localY >= lowest; localY--) {
                if (held.get(localX, localY, localZ).getLightOpacity(world, at.setPos(x, base + localY, z)) > 0) { return true; }
            }
        }
        return false;
    }

    private void fillSkyAbove(ICube cube, int x, int z, int from, int to) {
        if (blockedAbove(cube.getColumn(), Coords.blockToLocal(x), Coords.blockToLocal(z), to + 1)) { return; }
        ExtendedBlockStorage storage = cube.getStorage();
        if (storage == null) {
            setLight(cube, EnumSkyBlock.SKY, x, to, z, MAX_LIGHT);
            storage = cube.getStorage();
            if (storage == null) { return; }
            to--;
        }
        int localX = Coords.blockToLocal(x);
        int localZ = Coords.blockToLocal(z);
        for (int y = from; y <= to; y++) { storage.setSkyLight(localX, Coords.blockToLocal(y), localZ, MAX_LIGHT); }
    }

    private void darkenColumn(ICube cube, int x, int z, int from, int to) {
        ExtendedBlockStorage storage = cube.getStorage();
        if (storage == null || !world.provider.hasSkyLight()) { return; }
        int localX = Coords.blockToLocal(x);
        int localZ = Coords.blockToLocal(z);
        for (int y = from; y <= to; y++) { storage.setSkyLight(localX, Coords.blockToLocal(y), localZ, 0); }
    }

    private void seedUnderTop(ICube cube, int x, int z, int minY, int maxY) {
        if (opacityAt(cube, x, maxY, z) < MAX_LIGHT) { scheduleLightUpdate(EnumSkyBlock.SKY, at.setPos(x, maxY, z)); }
        if (opacityAt(cube, x, minY, z) < MAX_LIGHT) { scheduleLightUpdate(EnumSkyBlock.SKY, at.setPos(x, minY, z)); }
    }

    private void seedSides(Chunk column, int localX, int localZ, int top, int minY, int maxY) {
        if (top >= maxY) { return; }
        for (int face = 0; face < 6; face++) {
            if (STEP_Y[face] != 0) { continue; }
            int theirTop = topBeside(column, localX + STEP_X[face], localZ + STEP_Z[face]);
            if (theirTop <= top) { continue; }
            int x = (column.x << 4) + localX + STEP_X[face];
            int z = (column.z << 4) + localZ + STEP_Z[face];
            for (int y = Math.min(maxY, theirTop); y > top && y >= minY; y--) { scheduleLightUpdate(EnumSkyBlock.SKY, at.setPos(x, y, z)); }
        }
    }

    private boolean anyNeighbourLower(Chunk column, int localX, int localZ, int top) {
        if (topBeside(column, localX - 1, localZ) < top) { return true; }
        if (topBeside(column, localX + 1, localZ) < top) { return true; }
        if (topBeside(column, localX, localZ - 1) < top) { return true; }
        return topBeside(column, localX, localZ + 1) < top;
    }

    private int topBeside(Chunk column, int localX, int localZ) {
        if (localX >= 0 && localX < 16 && localZ >= 0 && localZ < 16) { return topAt(column, localX, localZ); }
        Chunk beside = world.getChunkProvider().getLoadedChunk(column.x + (localX >> 4), column.z + (localZ >> 4));
        if (beside == null) { return Integer.MIN_VALUE; }
        return ((IColumnInternal) beside).getTopYWithStaging(localX & 15, localZ & 15);
    }

    @Override public void scheduleLightUpdate(EnumSkyBlock lightType, BlockPos pos) {
        scheduled[lightType.ordinal()].add(pos.getX(), pos.getY(), pos.getZ());
        if (scheduled[lightType.ordinal()].size() >= SPILL_AT) { processFor(lightType); }
    }

    @Override public void processLightUpdates() {
        replayOwed();
        processFor(EnumSkyBlock.SKY);
        processFor(EnumSkyBlock.BLOCK);
    }

    private void replayOwed() {
        if (ready.isEmpty() || working) { return; }
        ICubeProvider cubes = (ICubeProvider) world.getChunkProvider();
        for (int i = 0; i < ready.size(); i++) {
            long key = ready.getLong(i);
            ICube cube = cubes.getLoadedCube(keyX(key), keyY(key), keyZ(key));
            if (cube == null) { continue; }
            cubeLoaded(cube);
        }
        ready.clear();
    }

    private static long cubeKey(int cubeX, int cubeY, int cubeZ) {
        return ((long) (cubeX & 0x1FFFFF) << 42) | ((long) (cubeY & 0x1FFFFF) << 21) | (cubeZ & 0x1FFFFFL);
    }

    private static int keyX(long key) { return (int) (key >> 42 & 0x1FFFFFL) << 11 >> 11; }

    private static int keyY(long key) { return (int) (key >> 21 & 0x1FFFFFL) << 11 >> 11; }

    private static int keyZ(long key) { return (int) (key & 0x1FFFFFL) << 11 >> 11; }

    private void processFor(EnumSkyBlock lightType) {
        LightQueue queue = scheduled[lightType.ordinal()];
        if (queue.isEmpty() || working) { return; }
        working = true;
        try {
            forget();
            sortIntoBuckets(lightType, queue);
            for (int light = MAX_LIGHT; light >= 0; light--) { darkenFrom(lightType, light); }
            do {
                for (int light = MAX_LIGHT; light >= 0; light--) { brightenFrom(lightType, light); }
            }
            while (anyPending(brightening));
        }
        finally { working = false; }
    }

    private void sortIntoBuckets(EnumSkyBlock lightType, LightQueue queue) {
        for (int i = 0; i < queue.size(); i++) {
            int x = queue.x(i);
            int y = queue.y(i);
            int z = queue.z(i);
            ICube cube = cubeAt(x, y, z);
            if (cube == null) { continue; }
            int held = lightIn(cube, lightType, x, y, z);
            int wanted = lightWanted(cube, lightType, x, y, z);
            if (wanted > held) {
                setLight(cube, lightType, x, y, z, wanted);
                brightening[wanted].add(x, y, z);
            }
            else if (wanted < held && !seeding) {
                setLight(cube, lightType, x, y, z, 0);
                darkening[held].add(x, y, z);
            }
        }
        queue.clear();
    }

    private void darkenFrom(EnumSkyBlock lightType, int light) {
        LightQueue queue = darkening[light];
        for (int i = 0; i < queue.size(); i++) {
            int x = queue.x(i);
            int y = queue.y(i);
            int z = queue.z(i);
            ICube cube = cubeAt(x, y, z);
            if (cube == null || lightIn(cube, lightType, x, y, z) != 0) { continue; }
            int wanted = lightWanted(cube, lightType, x, y, z);
            if (wanted > 0) {
                setLight(cube, lightType, x, y, z, wanted);
                brightening[wanted].add(x, y, z);
            }
            for (int face = 0; face < 6; face++) {
                int nx = x + STEP_X[face];
                int ny = y + STEP_Y[face];
                int nz = z + STEP_Z[face];
                ICube next = cubeAt(nx, ny, nz);
                if (next == null) { continue; }
                int theirs = lightIn(next, lightType, nx, ny, nz);
                if (theirs == 0) { continue; }
                if (theirs < light) {
                    setLight(next, lightType, nx, ny, nz, 0);
                    darkening[theirs].add(nx, ny, nz);
                }
                else { brightening[theirs].add(nx, ny, nz); }
            }
        }
        queue.clear();
    }

    private static boolean anyPending(LightQueue[] buckets) {
        for (LightQueue bucket : buckets) {
            if (!bucket.isEmpty()) { return true; }
        }
        return false;
    }

    private void brightenFrom(EnumSkyBlock lightType, int light) {
        LightQueue queue = brightening[light];
        for (int i = 0; i < queue.size(); i++) {
            int x = queue.x(i);
            int y = queue.y(i);
            int z = queue.z(i);
            ICube cube = cubeAt(x, y, z);
            if (cube == null) { continue; }
            if (lightIn(cube, lightType, x, y, z) != light) { continue; }
            if (light <= 1) { continue; }
            for (int face = 0; face < 6; face++) {
                int nx = x + STEP_X[face];
                int ny = y + STEP_Y[face];
                int nz = z + STEP_Z[face];
                ICube next = cubeAt(nx, ny, nz);
                if (next == null) {
                    owed.add(cubeKey(Coords.blockToCube(nx), Coords.blockToCube(ny), Coords.blockToCube(nz)));
                    continue;
                }
                int theirs = lightIn(next, lightType, nx, ny, nz);
                int spread = light - opacityAt(next, nx, ny, nz);
                if (spread <= theirs) { continue; }
                setLight(next, lightType, nx, ny, nz, spread);
                brightening[spread].add(nx, ny, nz);
                world.notifyLightSet(at.setPos(nx, ny, nz));
            }
        }
        queue.clear();
    }

    private int lightWanted(ICube cube, EnumSkyBlock lightType, int x, int y, int z) {
        int own = lightType == EnumSkyBlock.SKY
                ? (skyReaches(cube, x, y, z) ? MAX_LIGHT : 0)
                : GcRubicSunlight.lightValue(stateAt(cube, x, y, z), world, at.setPos(x, y, z));
        if (own >= MAX_LIGHT) { return MAX_LIGHT; }
        int opacity = opacityAt(cube, x, y, z);
        if (opacity >= MAX_LIGHT && own == 0) { return 0; }
        int best = own;
        for (int face = 0; face < 6; face++) {
            int nx = x + STEP_X[face];
            int ny = y + STEP_Y[face];
            int nz = z + STEP_Z[face];
            ICube next = cubeAt(nx, ny, nz);
            if (next == null) { continue; }
            int reaching = lightIn(next, lightType, nx, ny, nz) - opacity;
            if (reaching > best) { best = reaching; }
            if (best >= MAX_LIGHT) { return MAX_LIGHT; }
        }
        return Math.max(best, 0);
    }

    private boolean skyReaches(ICube cube, int x, int y, int z) {
        if (!world.provider.hasSkyLight()) { return false; }
        return y > topAt(cube.getColumn(), Coords.blockToLocal(x), Coords.blockToLocal(z));
    }

    private int topAt(Chunk column, int localX, int localZ) {
        return ((IColumnInternal) column).getTopYWithStaging(localX, localZ);
    }

    private IBlockState stateAt(ICube cube, int x, int y, int z) {
        ExtendedBlockStorage storage = cube.getStorage();
        if (storage == null) { return AIR; }
        return storage.get(Coords.blockToLocal(x), Coords.blockToLocal(y), Coords.blockToLocal(z));
    }

    private int opacityAt(ICube cube, int x, int y, int z) { return MathHelper.clamp(stateAt(cube, x, y, z).getLightOpacity(world, at.setPos(x, y, z)), 1, MAX_LIGHT); }

    private int lightIn(ICube cube, EnumSkyBlock lightType, int x, int y, int z) {
        ExtendedBlockStorage storage = cube.getStorage();
        if (storage == null) {
            if (lightType == EnumSkyBlock.SKY && skyReaches(cube, x, y, z)) { return MAX_LIGHT; }
            return 0;
        }
        int localX = Coords.blockToLocal(x);
        int localY = Coords.blockToLocal(y);
        int localZ = Coords.blockToLocal(z);
        if (lightType == EnumSkyBlock.SKY) { return world.provider.hasSkyLight() ? storage.getSkyLight(localX, localY, localZ) : 0; }
        return storage.getBlockLight(localX, localY, localZ);
    }

    private void setLight(ICube cube, EnumSkyBlock lightType, int x, int y, int z, int light) {
        if (lightType == EnumSkyBlock.SKY && !world.provider.hasSkyLight()) { return; }
        if (lightType == EnumSkyBlock.SKY && light == MAX_LIGHT
                && blockedAbove(cube.getColumn(), Coords.blockToLocal(x), Coords.blockToLocal(z), y + 1)) { return; }
        cube.setLightFor(lightType, at.setPos(x, y, z), light);
    }

    @Nullable private ICube cubeAt(int x, int y, int z) {
        int cubeX = Coords.blockToCube(x);
        int cubeY = Coords.blockToCube(y);
        int cubeZ = Coords.blockToCube(z);
        if (cubeY < lowestCube || cubeY > highestCube) { return null; }
        int slot = slotFor(cubeX - nearbyCubeX, cubeY - nearbyCubeY, cubeZ - nearbyCubeZ);
        if (slot >= 0 && (probedSlots & 1 << slot) != 0) { return nearby[slot]; }
        ICube found = ((ICubeProvider) world.getChunkProvider()).getLoadedCube(cubeX, cubeY, cubeZ);
        if (slot >= 0) {
            nearby[slot] = found;
            probedSlots |= 1 << slot;
        }
        else if (found != null) { centreOn(cubeX, cubeY, cubeZ, found); }
        return found;
    }

    private static int slotFor(int dx, int dy, int dz) {
        if (dx < -1 || dx > 1 || dy < -1 || dy > 1 || dz < -1 || dz > 1) { return -1; }
        return ((dx + 1) * 3 + dy + 1) * 3 + dz + 1;
    }

    private void centreOn(int cubeX, int cubeY, int cubeZ, ICube middle) {
        forget();
        nearbyCubeX = cubeX;
        nearbyCubeY = cubeY;
        nearbyCubeZ = cubeZ;
        nearby[13] = middle;
        probedSlots = 1 << 13;
    }

    private void forget() {
        Arrays.fill(nearby, null);
        probedSlots = 0;
        nearbyCubeX = Integer.MIN_VALUE;
        nearbyCubeY = Integer.MIN_VALUE;
        nearbyCubeZ = Integer.MIN_VALUE;
    }
}
