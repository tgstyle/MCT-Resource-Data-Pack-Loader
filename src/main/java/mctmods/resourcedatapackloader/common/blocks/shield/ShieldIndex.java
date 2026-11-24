package mctmods.resourcedatapackloader.common.blocks.shield;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.core.SectionPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;

public final class ShieldIndex {
    private final Long2ObjectOpenHashMap<Cell> cells = new Long2ObjectOpenHashMap<>();
    private final List<Cell> gathered = new ArrayList<>();
    private int spawnCount;
    private int weatherCount;

    private static int unpackX(long pos) { return (int) (pos >> 38); }

    private static int unpackZ(long pos) { return (int) (pos << 38 >> 38); }

    private void count(BlockShield shield, int by) {
        if (shield.blocksSpawns()) { spawnCount += by; }
        if (shield.blocksWeather()) { weatherCount += by; }
    }

    public void drop(long chunk) {
        Cell cell = cells.remove(chunk);
        if (cell == null) { return; }
        for (int i = 0; i < cell.size; i++) { count(cell.shields[i], -1); }
    }

    public void add(long chunk, long pos, int y, BlockShield shield) {
        Cell cell = cells.get(chunk);
        if (cell == null) {
            cell = new Cell();
            cells.put(chunk, cell);
        }
        cell.add(pos, y, shield);
        count(shield, 1);
    }

    public void remove(long chunk, long pos, int y) {
        Cell cell = cells.get(chunk);
        if (cell == null) { return; }
        for (int i = 0; i < cell.size; i++) {
            if (cell.pos[i] != pos || cell.ys[i] != y) { continue; }
            count(cell.shields[i], -1);
            cell.removeAt(i);
            break;
        }
        if (cell.size == 0) { cells.remove(chunk); }
    }

    private void gather(int x, int z, int reachChunks) {
        gathered.clear();
        int chunkX = SectionPos.blockToSectionCoord(x);
        int chunkZ = SectionPos.blockToSectionCoord(z);
        int side = reachChunks * 2 + 1;
        if (cells.size() < side * side) {
            for (Long2ObjectMap.Entry<Cell> entry : cells.long2ObjectEntrySet()) {
                long key = entry.getLongKey();
                if (Math.abs((int) key - chunkX) <= reachChunks && Math.abs((int) (key >> 32) - chunkZ) <= reachChunks) { gathered.add(entry.getValue()); }
            }
            return;
        }
        for (int dx = -reachChunks; dx <= reachChunks; dx++) {
            for (int dz = -reachChunks; dz <= reachChunks; dz++) {
                Cell cell = cells.get(ChunkPos.asLong(chunkX + dx, chunkZ + dz));
                if (cell != null) { gathered.add(cell); }
            }
        }
    }

    public boolean spawnDenied(Entity entity, int x, int y, int z, int reachChunks) {
        if (spawnCount == 0) { return false; }
        gather(x, z, reachChunks);
        for (Cell cell : gathered) {
            for (int i = 0; i < cell.size; i++) {
                BlockShield shield = cell.shields[i];
                if (!shield.blocksSpawns()) { continue; }
                long pos = cell.pos[i];
                int radius = shield.spawnRadius;
                if (Math.abs(unpackX(pos) - x) <= radius && Math.abs(cell.ys[i] - y) <= radius && Math.abs(unpackZ(pos) - z) <= radius && shield.stops(entity)) { return true; }
            }
        }
        return false;
    }

    public boolean weatherShut(int x, int z, int reachChunks) {
        if (weatherCount == 0) { return false; }
        gather(x, z, reachChunks);
        for (Cell cell : gathered) {
            for (int i = 0; i < cell.size; i++) {
                BlockShield shield = cell.shields[i];
                if (!shield.blocksWeather()) { continue; }
                long pos = cell.pos[i];
                int radius = shield.weatherRadius;
                if (Math.abs(unpackX(pos) - x) <= radius && Math.abs(unpackZ(pos) - z) <= radius) { return true; }
            }
        }
        return false;
    }

    public void near(int x, int z, int range, int maxRadius, int reachChunks, Near scratch) {
        scratch.clear();
        if (weatherCount == 0) { return; }
        gather(x, z, reachChunks);
        int within = range + maxRadius;
        for (Cell cell : gathered) {
            for (int i = 0; i < cell.size; i++) {
                BlockShield shield = cell.shields[i];
                if (!shield.blocksWeather()) { continue; }
                long pos = cell.pos[i];
                int shieldX = unpackX(pos);
                int shieldZ = unpackZ(pos);
                if (Math.abs(shieldX - x) <= within && Math.abs(shieldZ - z) <= within) { scratch.put(shieldX, shieldZ, shield.weatherRadius); }
            }
        }
    }

    private static final class Cell {
        private long[] pos = new long[4];
        private int[] ys = new int[4];
        private BlockShield[] shields = new BlockShield[4];
        private int size;

        private void add(long at, int y, BlockShield shield) {
            if (size == pos.length) {
                pos = Arrays.copyOf(pos, size * 2);
                ys = Arrays.copyOf(ys, size * 2);
                shields = Arrays.copyOf(shields, size * 2);
            }
            pos[size] = at;
            ys[size] = y;
            shields[size] = shield;
            size++;
        }

        private void removeAt(int index) {
            size--;
            pos[index] = pos[size];
            ys[index] = ys[size];
            shields[index] = shields[size];
            shields[size] = null;
        }
    }

    public static final class Near {
        private int[] xs = new int[16];
        private int[] zs = new int[16];
        private int[] radii = new int[16];
        private int size;

        private void put(int x, int z, int radius) {
            if (size == xs.length) {
                xs = Arrays.copyOf(xs, size * 2);
                zs = Arrays.copyOf(zs, size * 2);
                radii = Arrays.copyOf(radii, size * 2);
            }
            xs[size] = x;
            zs[size] = z;
            radii[size] = radius;
            size++;
        }

        public void clear() { size = 0; }

        public boolean shut(int x, int z) {
            for (int i = 0; i < size; i++) {
                if (Math.abs(xs[i] - x) <= radii[i] && Math.abs(zs[i] - z) <= radii[i]) { return true; }
            }
            return false;
        }
    }
}
