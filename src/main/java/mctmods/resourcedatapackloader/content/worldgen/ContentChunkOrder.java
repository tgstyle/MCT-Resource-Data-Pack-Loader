package mctmods.resourcedatapackloader.content.worldgen;

import net.minecraft.util.math.ChunkPos;

public final class ContentChunkOrder {
    private static final int REGION = 32;
    private static final int[] CURVE = new int[REGION * REGION * 2];
    private final int lowX;
    private final int lowZ;
    private final int highX;
    private final int highZ;
    private final int reach;
    private final long total;
    private int ring;
    private int side;
    private int step;
    private int regionX;
    private int regionZ;
    private int inRegion;
    private int spanX;
    private int spanZ;
    private int baseX;
    private int baseZ;
    private boolean whole;
    private boolean spent;

    static {
        for (int d = 0; d < REGION * REGION; d++) {
            int alongX;
            int alongZ;
            int at = d;
            int offsetX = 0;
            int offsetZ = 0;
            for (int span = 1; span < REGION; span *= 2) {
                alongX = 1 & (at / 2);
                alongZ = 1 & (at ^ alongX);
                if (alongZ == 0) {
                    if (alongX == 1) {
                        offsetX = span - 1 - offsetX;
                        offsetZ = span - 1 - offsetZ;
                    }
                    int swap = offsetX;
                    offsetX = offsetZ;
                    offsetZ = swap;
                }
                offsetX += span * alongX;
                offsetZ += span * alongZ;
                at /= 4;
            }
            CURVE[d * 2] = offsetX;
            CURVE[d * 2 + 1] = offsetZ;
        }
    }

    public ContentChunkOrder(int centreChunkX, int centreChunkZ, int radiusChunks) {
        this.lowX = centreChunkX - radiusChunks;
        this.lowZ = centreChunkZ - radiusChunks;
        this.highX = centreChunkX + radiusChunks;
        this.highZ = centreChunkZ + radiusChunks;
        long side = 2L * radiusChunks + 1L;
        this.total = side * side;
        this.regionX = centreChunkX >> 5;
        this.regionZ = centreChunkZ >> 5;
        int lowRegion = Math.max(this.regionX - (lowX >> 5), (highX >> 5) - this.regionX);
        int lowRegionZ = Math.max(this.regionZ - (lowZ >> 5), (highZ >> 5) - this.regionZ);
        this.reach = Math.max(lowRegion, lowRegionZ);
        open();
    }

    public long total() { return total; }

    public boolean hasNext() { return !spent; }

    public long skip(long count) {
        long moved = 0L;
        while (moved < count && hasNext()) {
            next();
            moved++;
        }
        return moved;
    }

    public ChunkPos next() {
        int x;
        int z;
        if (whole) {
            x = baseX + CURVE[inRegion * 2];
            z = baseZ + CURVE[inRegion * 2 + 1];
        }
        else {
            x = baseX + inRegion / spanZ;
            z = baseZ + inRegion % spanZ;
        }
        inRegion++;
        if (inRegion >= spanX * spanZ) { advance(); }

        return new ChunkPos(x, z);
    }

    private void advance() {
        do {
            if (ring > reach) {
                spent = true;
                return;
            }
            if (ring == 0) {
                ring = 1;
                side = 0;
                step = 0;
                regionX += 1;
                regionZ -= 1;
            }
            else { walk(); }
            open();
        }
        while (spanX * spanZ == 0 && !spent);
    }

    private void walk() {
        int span = 2 * ring;
        if (side == 0) { regionZ++; }
        else if (side == 1) { regionX--; }
        else if (side == 2) { regionZ--; }
        else { regionX++; }
        step++;
        if (step < span) { return; }

        step = 0;
        side++;
        if (side < 4) { return; }

        ring++;
        side = 0;
        regionX++;
        regionZ--;
    }

    private void open() {
        baseX = Math.max(regionX << 5, lowX);
        baseZ = Math.max(regionZ << 5, lowZ);
        int topX = Math.min((regionX << 5) + 31, highX);
        int topZ = Math.min((regionZ << 5) + 31, highZ);
        spanX = topX - baseX + 1;
        spanZ = topZ - baseZ + 1;
        if (spanX <= 0 || spanZ <= 0) {
            spanX = 0;
            spanZ = 0;
        }
        whole = spanX == REGION && spanZ == REGION;
        inRegion = 0;
        if (spanX * spanZ == 0) { advance(); }
    }
}
