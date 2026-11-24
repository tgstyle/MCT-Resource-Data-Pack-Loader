package mctmods.resourcedatapackloader.common.util.world;

public final class ChunkSpan {
    private ChunkSpan() {}

    public static int chebyshev(int dx, int dz) { return Math.max(Math.abs(dx), Math.abs(dz)); }

    public static boolean forEachWithin(int chunkX, int chunkZ, int radius, ChunkVisitor visitor) {
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                if (!visitor.visit(chunkX + dx, chunkZ + dz)) { return false; }
            }
        }
        return true;
    }

    public static boolean allChunks(int minX, int minZ, int maxX, int maxZ, ChunkVisitor visitor) {
        for (int chunkX = minX >> 4; chunkX <= maxX >> 4; chunkX++) {
            for (int chunkZ = minZ >> 4; chunkZ <= maxZ >> 4; chunkZ++) {
                if (!visitor.visit(chunkX, chunkZ)) { return false; }
            }
        }
        return true;
    }

    @FunctionalInterface public interface ChunkVisitor { boolean visit(int x, int z); }
}
