package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.compat.GcRubicSunlight;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import java.util.Arrays;

public final class ContentFirstLight {
    private static final int MAX = 15;
    private static final int SPAN = 48;
    private static final int[] STEP_X = {-1, 1, 0, 0, 0, 0};
    private static final int[] STEP_Y = {0, 0, -1, 1, 0, 0};
    private static final int[] STEP_Z = {0, 0, 0, 0, -1, 1};
    private static final ContentFirstLight ENGINE = new ContentFirstLight();
    private final Chunk[] ring = new Chunk[9];
    private final boolean[] touched = new boolean[9];
    private final byte[] opacity = new byte[1 << 20];
    private final int[][] buckets = new int[MAX + 1][];
    private final int[] filled = new int[MAX + 1];
    private final int[][] dark = new int[MAX + 1][];
    private final int[] darkFilled = new int[MAX + 1];
    private final BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
    private World world;
    private int originX;
    private int originZ;
    private int ceiling;
    private boolean sky;
    private int settled;
    private int rounds;

    private ContentFirstLight() {
        for (int i = 0; i <= MAX; i++) {
            buckets[i] = new int[1 << 12];
            dark[i] = new int[1 << 8];
        }
    }

    public static boolean relight(Chunk chunk) { return ENGINE.run(chunk); }

    private boolean run(Chunk chunk) {
        World held = chunk.getWorld();
        int x0 = chunk.x << 4;
        int z0 = chunk.z << 4;
        if (!held.isAreaLoaded(new BlockPos(x0 - 1, 0, z0 - 1), new BlockPos(x0 + 16, held.getSeaLevel(), z0 + 16))) { return false; }
        world = held;
        originX = x0 - 16;
        originZ = z0 - 16;
        ceiling = 16;
        for (int dx = 0; dx < 3; dx++) {
            for (int dz = 0; dz < 3; dz++) {
                Chunk near = held.getChunk(chunk.x + dx - 1, chunk.z + dz - 1);
                ring[dx * 3 + dz] = near;
                touched[dx * 3 + dz] = false;
                ceiling = Math.max(ceiling, near.getTopFilledSegment() + 16);
            }
        }
        Arrays.fill(opacity, (byte) 0);
        settled = 0;
        rounds = 0;
        try {
            if (held.provider.hasSkyLight()) {
                sky = true;
                fillSky(chunk);
                seedRing();
                spread();
                settleRing();
            }
            sky = false;
            fillBlock(chunk);
            seedRing();
            spread();
            settleRing();
        }
        finally {
            Arrays.fill(filled, 0);
            Arrays.fill(darkFilled, 0);
            for (int i = 0; i < 9; i++) {
                if (touched[i] && ring[i] != null) { ring[i].markDirty(); }
            }
            Arrays.fill(ring, null);
            world = null;
        }
        chunk.markDirty();
        if (settled > 0 && ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("The first light of chunk {}, {} settled {} cell(s) around it that were brighter than their neighbors allow, in {} round(s)", chunk.x, chunk.z, settled, rounds); }
        return true;
    }

    private void fillSky(Chunk chunk) {
        ExtendedBlockStorage[] storages = chunk.getBlockStorageArray();
        int worldX = chunk.x << 4;
        int worldZ = chunk.z << 4;
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int light = MAX;
                for (int y = ceiling - 1; y >= 0; y--) {
                    ExtendedBlockStorage storage = storages[y >> 4];
                    int raw = 0;
                    if (storage != null) {
                        IBlockState state = storage.get(x, y & 15, z);
                        raw = state.getLightOpacity(world, at.setPos(worldX + x, y, worldZ + z));
                        opacity[key(x + 16, y, z + 16)] = (byte) Math.min(MAX, Math.max(1, raw));
                    }
                    if (raw == 0 && light != MAX) { raw = 1; }
                    light = Math.max(0, light - raw);
                    if (storage != null) { storage.setSkyLight(x, y & 15, z, light); }
                    if (light >= 2) { push(light, x + 16, y, z + 16); }
                }
            }
        }
        touched[4] = true;
    }

    private void fillBlock(Chunk chunk) {
        ExtendedBlockStorage[] storages = chunk.getBlockStorageArray();
        int worldX = chunk.x << 4;
        int worldZ = chunk.z << 4;
        for (int y = 0; y < ceiling; y++) {
            ExtendedBlockStorage storage = storages[y >> 4];
            if (storage == null) { continue; }
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    IBlockState state = storage.get(x, y & 15, z);
                    int light = GcRubicSunlight.lightValue(state, world, at.setPos(worldX + x, y, worldZ + z));
                    storage.setBlockLight(x, y & 15, z, light);
                    if (light >= 2) { push(light, x + 16, y, z + 16); }
                }
            }
        }
        touched[4] = true;
    }

    private void seedRing() {
        for (int along = 16; along < 32; along++) {
            for (int y = 0; y < ceiling; y++) {
                seed(15, y, along);
                seed(32, y, along);
                seed(along, y, 15);
                seed(along, y, 32);
            }
        }
    }

    private void seed(int lx, int y, int lz) {
        int light = lightAt(lx, y, lz);
        if (light >= 2) { push(light, lx, y, lz); }
    }

    private void spread() {
        for (int level = MAX; level >= 2; level--) {
            int[] queue = buckets[level];
            int count = filled[level];
            for (int i = 0; i < count; i++) {
                int key = queue[i];
                int lx = key >> 14;
                int lz = (key >> 8) & 63;
                int y = key & 255;
                if (lightAt(lx, y, lz) != level) { continue; }
                for (int face = 0; face < 6; face++) {
                    int nx = lx + STEP_X[face];
                    int ny = y + STEP_Y[face];
                    int nz = lz + STEP_Z[face];
                    if (nx < 0 || nx >= SPAN || nz < 0 || nz >= SPAN || ny < 0 || ny >= ceiling) { continue; }
                    int next = level - opacityAt(nx, ny, nz);
                    if (next <= 0 || next <= lightAt(nx, ny, nz)) { continue; }
                    if (setLight(nx, ny, nz, next) && next >= 2) { push(next, nx, ny, nz); }
                }
            }
            filled[level] = 0;
        }
    }

    private void settleRing() {
        for (int round = 0; round < 64; round++) {
            int before = settled;
            for (int along = 16; along < 32; along++) {
                for (int y = ceiling - 1; y >= 0; y--) {
                    settle(15, y, along);
                    settle(32, y, along);
                    settle(along, y, 15);
                    settle(along, y, 32);
                }
            }
            if (settled == before) { return; }
            rounds = Math.max(rounds, round + 1);
            darken();
            spread();
        }
    }

    private void settle(int lx, int y, int lz) {
        int stored = lightAt(lx, y, lz);
        if (stored == 0 || stored <= wanted(lx, y, lz)) { return; }
        settled++;
        if (setLight(lx, y, lz, 0)) { darkPush(stored, lx, y, lz); }
    }

    private void darken() {
        for (int level = MAX; level >= 1; level--) {
            int[] queue = dark[level];
            int count = darkFilled[level];
            for (int i = 0; i < count; i++) {
                int key = queue[i];
                int lx = key >> 14;
                int lz = (key >> 8) & 63;
                int y = key & 255;
                if (lightAt(lx, y, lz) != 0) { continue; }
                int wanted = wanted(lx, y, lz);
                if (wanted > 0 && setLight(lx, y, lz, wanted) && wanted >= 2) { push(wanted, lx, y, lz); }
                for (int face = 0; face < 6; face++) {
                    int nx = lx + STEP_X[face];
                    int ny = y + STEP_Y[face];
                    int nz = lz + STEP_Z[face];
                    if (nx < 0 || nx >= SPAN || nz < 0 || nz >= SPAN || ny < 0 || ny >= ceiling) { continue; }
                    int theirs = lightAt(nx, ny, nz);
                    if (theirs == 0) { continue; }
                    if (theirs < level && setLight(nx, ny, nz, 0)) { darkPush(theirs, nx, ny, nz); }
                    else if (theirs >= 2) { push(theirs, nx, ny, nz); }
                }
            }
            darkFilled[level] = 0;
        }
    }

    private int wanted(int lx, int y, int lz) {
        int own = 0;
        if (sky) {
            if (y >= ring[(lx >> 4) * 3 + (lz >> 4)].getHeightValue(lx & 15, lz & 15)) { return MAX; }
        }
        else { own = emissionAt(lx, y, lz); }
        int through = opacityAt(lx, y, lz);
        int best = own;
        for (int face = 0; face < 6 && best < MAX; face++) {
            int nx = lx + STEP_X[face];
            int ny = y + STEP_Y[face];
            int nz = lz + STEP_Z[face];
            if (nx < 0 || nx >= SPAN || nz < 0 || nz >= SPAN || ny < 0 || ny >= 256) { continue; }
            int reaching = (ny >= ceiling ? (sky ? MAX : 0) : lightAt(nx, ny, nz)) - through;
            if (reaching > best) { best = reaching; }
        }
        return Math.max(best, 0);
    }

    private int emissionAt(int lx, int y, int lz) {
        ExtendedBlockStorage storage = ring[(lx >> 4) * 3 + (lz >> 4)].getBlockStorageArray()[y >> 4];
        if (storage == null) { return 0; }
        return GcRubicSunlight.lightValue(storage.get(lx & 15, y & 15, lz & 15), world, at.setPos(originX + lx, y, originZ + lz));
    }

    private int lightAt(int lx, int y, int lz) {
        Chunk chunk = ring[(lx >> 4) * 3 + (lz >> 4)];
        ExtendedBlockStorage storage = chunk.getBlockStorageArray()[y >> 4];
        if (storage == null) { return sky && y >= chunk.getHeightValue(lx & 15, lz & 15) ? MAX : 0; }
        return sky ? storage.getSkyLight(lx & 15, y & 15, lz & 15) : storage.getBlockLight(lx & 15, y & 15, lz & 15);
    }

    private boolean setLight(int lx, int y, int lz, int light) {
        int slot = (lx >> 4) * 3 + (lz >> 4);
        ExtendedBlockStorage storage = ring[slot].getBlockStorageArray()[y >> 4];
        if (storage == null) { return false; }
        if (sky) { storage.setSkyLight(lx & 15, y & 15, lz & 15, light); }
        else { storage.setBlockLight(lx & 15, y & 15, lz & 15, light); }
        touched[slot] = true;
        return true;
    }

    private int opacityAt(int lx, int y, int lz) {
        int key = key(lx, y, lz);
        int known = opacity[key];
        if (known != 0) { return known; }
        ExtendedBlockStorage storage = ring[(lx >> 4) * 3 + (lz >> 4)].getBlockStorageArray()[y >> 4];
        int raw = 1;
        if (storage != null) {
            IBlockState state = storage.get(lx & 15, y & 15, lz & 15);
            raw = Math.min(MAX, Math.max(1, state.getLightOpacity(world, at.setPos(originX + lx, y, originZ + lz))));
        }
        opacity[key] = (byte) raw;
        return raw;
    }

    private static int key(int lx, int y, int lz) { return lx << 14 | lz << 8 | y; }

    private void push(int level, int lx, int y, int lz) {
        int[] queue = buckets[level];
        if (filled[level] == queue.length) {
            queue = Arrays.copyOf(queue, queue.length << 1);
            buckets[level] = queue;
        }
        queue[filled[level]++] = key(lx, y, lz);
    }

    private void darkPush(int level, int lx, int y, int lz) {
        int[] queue = dark[level];
        if (darkFilled[level] == queue.length) {
            queue = Arrays.copyOf(queue, queue.length << 1);
            dark[level] = queue;
        }
        queue[darkFilled[level]++] = key(lx, y, lz);
    }
}
