package mctmods.resourcedatapackloader.common.util.world;

public final class ChunkSpan {
    private ChunkSpan() {}

    public static boolean allChunks(int minX, int minZ, int maxX, int maxZ, ChunkTest test) {
        for (int chunkX = minX >> 4; chunkX <= maxX >> 4; chunkX++) {
            for (int chunkZ = minZ >> 4; chunkZ <= maxZ >> 4; chunkZ++) {
                if (!test.test(chunkX, chunkZ)) { return false; }
            }
        }
        return true;
    }

    public static void forEachChunk(int minX, int minZ, int maxX, int maxZ, ChunkAction action) {
        for (int chunkX = minX >> 4; chunkX <= maxX >> 4; chunkX++) {
            for (int chunkZ = minZ >> 4; chunkZ <= maxZ >> 4; chunkZ++) { action.accept(chunkX, chunkZ); }
        }
    }

    @FunctionalInterface public interface ChunkTest { boolean test(int chunkX, int chunkZ); }

    @FunctionalInterface public interface ChunkAction { void accept(int chunkX, int chunkZ); }
}
