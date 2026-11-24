package mctmods.resourcedatapackloader.common.util.worldgen;

public final class ChunkOrigin {
    public static final int OFFSET = 8;
    public static final int SIZE = 16;

    private ChunkOrigin() {}

    public static int base(int chunk) { return chunk * SIZE + OFFSET; }

    public static int baseOf(int block) { return ((block - OFFSET) >> 4) * SIZE + OFFSET; }
}
