package mctmods.resourcedatapackloader.content.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;

public final class ContentStructurePins {
    private static final Set<Long> PINNED = ConcurrentHashMap.newKeySet();

    private ContentStructurePins() {}

    static void hold(List<List<Integer>> pins) {
        for (List<Integer> pin : pins) {
            if (pin.size() == 2) { PINNED.add(chunkOf(pin).pack()); }
        }
    }

    static boolean pinnedAt(List<List<Integer>> pins, int x, int z) {
        for (List<Integer> pin : pins) {
            if (pin.size() == 2 && pin.get(0) >> 4 == x && pin.get(1) >> 4 == z) { return true; }
        }
        return false;
    }

    @Nullable static ChunkPos pinnedRegion(List<List<Integer>> pins, int spacing, int regionX, int regionZ) {
        for (List<Integer> pin : pins) {
            if (pin.size() != 2) { continue; }
            ChunkPos chunk = chunkOf(pin);
            if (Math.floorDiv(chunk.x(), spacing) == Math.floorDiv(regionX, spacing) && Math.floorDiv(chunk.z(), spacing) == Math.floorDiv(regionZ, spacing)) { return chunk; }
        }
        return null;
    }

    static boolean farFromSpawn(int minDistanceFromSpawn, List<Integer> spawn, int x, int z) {
        if (minDistanceFromSpawn <= 0) { return true; }
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        ServerLevel overworld = server == null ? null : server.getLevel(Level.OVERWORLD);
        BlockPos from = overworld != null ? overworld.getRespawnData().pos() : new BlockPos(spawn.size() == 2 ? spawn.get(0) : 0, 0, spawn.size() == 2 ? spawn.get(1) : 0);
        double offX = (x * 16 + 8) - from.getX();
        double offZ = (z * 16 + 8) - from.getZ();
        return offX * offX + offZ * offZ >= (double) minDistanceFromSpawn * minDistanceFromSpawn;
    }

    public static boolean pinned(ChunkPos chunk) { return !PINNED.isEmpty() && PINNED.contains(chunk.pack()); }

    private static ChunkPos chunkOf(List<Integer> pin) { return new ChunkPos(pin.get(0) >> 4, pin.get(1) >> 4); }
}
