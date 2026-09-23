package mctmods.resourcedatapackloader.content.worldgen;

import net.minecraft.world.level.ChunkPos;

public final class ContentChunkOrder {
    private static final int REGION = 32;
    private static final int[] CURVE = new int[REGION * REGION * 2];
    private final int middleX;
    private final int middleZ;
    private final int lowX;
    private final int lowZ;
    private final int highX;
    private final int highZ;
    private final int middleRegionX;
    private final int middleRegionZ;
    private final int rings;
    private final long tiles;
    private final long total;
    private long at;
    private long tileAt;
    private long within;
    private int regionX;
    private int regionZ;
    private int ring;
    private int side;
    private int step;
    private int baseX;
    private int baseZ;
    private int spanX;
    private int spanZ;
    private boolean whole;

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

    public ContentChunkOrder(int middleX, int middleZ, int reach) {
        this.middleX = middleX;
        this.middleZ = middleZ;
        this.lowX = middleX - reach;
        this.lowZ = middleZ - reach;
        this.highX = middleX + reach;
        this.highZ = middleZ + reach;
        this.middleRegionX = middleX >> 5;
        this.middleRegionZ = middleZ >> 5;
        this.rings = Math.max(Math.max(middleRegionX - (lowX >> 5), (highX >> 5) - middleRegionX), Math.max(middleRegionZ - (lowZ >> 5), (highZ >> 5) - middleRegionZ));
        this.tiles = (long) ((highX >> 5) - (lowX >> 5) + 1) * ((highZ >> 5) - (lowZ >> 5) + 1);
        long across = 2L * reach + 1L;
        this.total = across * across;
        rewind();
    }

    public long total() { return total; }

    public int tileSize() { return REGION; }

    public long tiles() { return tiles; }

    public long tile() { return tileAt; }

    public long within() { return within; }

    public boolean hasNext() { return at < total; }

    public ChunkPos next() {
        int index = (int) within;
        ChunkPos found = whole ? new ChunkPos(baseX + CURVE[index * 2], baseZ + CURVE[index * 2 + 1]) : new ChunkPos(baseX + index / spanZ, baseZ + index % spanZ);
        at++;
        within++;
        if (within >= held() && at < total) { toNextTile(); }
        return found;
    }

    public long skipTo(long tile, long inTile) {
        rewind();
        if (tile < 0L || tile >= tiles) { return 0L; }
        while (tileAt < tile) {
            at += held();
            toNextTile();
        }
        long into = inTile > 0L && inTile < held() ? inTile : 0L;
        within = into;
        at += into;
        return at;
    }

    public long skipMadeBefore(long count) {
        rewind();
        long widest = (long) Math.sqrt((double) count);
        while ((widest + 1L) * (widest + 1L) <= count) { widest++; }
        while (widest > 0L && widest * widest > count) { widest--; }
        long made = (widest - 1L) / 2L;
        if (made <= 0L) { return 0L; }
        while (at < total && inside((int) Math.min(Integer.MAX_VALUE, made))) {
            at += held();
            if (at >= total) { break; }
            toNextTile();
        }
        return at;
    }

    public boolean nearTile(int x, int z, int reach) { return x >= baseX - reach && x <= baseX + spanX - 1 + reach && z >= baseZ - reach && z <= baseZ + spanZ - 1 + reach; }

    private long held() { return (long) spanX * spanZ; }

    private boolean inside(int made) {
        int reachX = Math.max(Math.abs(baseX - middleX), Math.abs(baseX + spanX - 1 - middleX));
        int reachZ = Math.max(Math.abs(baseZ - middleZ), Math.abs(baseZ + spanZ - 1 - middleZ));
        return Math.max(reachX, reachZ) <= made;
    }

    private void rewind() {
        at = 0L;
        tileAt = 0L;
        within = 0L;
        regionX = middleRegionX;
        regionZ = middleRegionZ;
        ring = 0;
        side = 0;
        step = 0;
        open();
    }

    private void toNextTile() {
        tileAt++;
        within = 0L;
        do {
            walk();
            open();
        }
        while (held() == 0L && ring <= rings);
    }

    private void walk() {
        if (ring == 0) {
            ring = 1;
            regionX++;
            regionZ--;
            return;
        }
        if (side == 0) { regionZ++; }
        else if (side == 1) { regionX--; }
        else if (side == 2) { regionZ--; }
        else { regionX++; }
        step++;
        if (step < 2 * ring) { return; }
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
        spanX = Math.max(0, Math.min((regionX << 5) + REGION - 1, highX) - baseX + 1);
        spanZ = Math.max(0, Math.min((regionZ << 5) + REGION - 1, highZ) - baseZ + 1);
        whole = spanX == REGION && spanZ == REGION;
    }
}
