package mctmods.resourcedatapackloader.common.util.world;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;

public final class ChunkSaves {
    private ChunkSaves() {}

    public static int pending(ChunkProviderServer provider) { return provider.chunkLoader instanceof AnvilChunkLoader ? ((AnvilChunkLoader) provider.chunkLoader).getPendingSaveCount() : -1; }

    public static int pending(MinecraftServer server) {
        int total = 0;
        for (WorldServer world : server.worlds) { total += Math.max(0, pending(world.getChunkProvider())); }
        return total;
    }
}
