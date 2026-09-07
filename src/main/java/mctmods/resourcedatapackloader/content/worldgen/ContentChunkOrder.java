package mctmods.resourcedatapackloader.content.worldgen;

import net.minecraft.world.level.ChunkPos;

public final class ContentChunkOrder {
    private final int middleX;
    private final int middleZ;
    private final int reach;
    private final long total;
    private long at;

    public ContentChunkOrder(int middleX, int middleZ, int reach) {
        this.middleX = middleX;
        this.middleZ = middleZ;
        this.reach = reach;
        long side = 2L * reach + 1L;
        this.total = side * side;
    }

    public long total() { return total; }

    public boolean hasNext() { return at < total; }

    public long skip(long count) {
        long moved = Math.min(count, total - at);
        at += moved;
        return moved;
    }

    public ChunkPos next() {
        ChunkPos found = positionOf(at);
        at++;
        return found;
    }

    private ChunkPos positionOf(long index) {
        if (index == 0L) { return new ChunkPos(middleX, middleZ); }
        int ring = 1;
        long before = 1L;
        while (before + 8L * ring <= index) {
            before += 8L * ring;
            ring++;
        }
        long along = index - before;
        int side = 2 * ring;
        int leg = (int) (along / side);
        int step = (int) (along % side);
        int x;
        int z;
        switch (leg) {
            case 0 -> {
                x = middleX - ring + step;
                z = middleZ - ring;
            }
            case 1 -> {
                x = middleX + ring;
                z = middleZ - ring + step;
            }
            case 2 -> {
                x = middleX + ring - step;
                z = middleZ + ring;
            }
            default -> {
                x = middleX - ring;
                z = middleZ + ring - step;
            }
        }
        return new ChunkPos(x, z);
    }

    public int reach() { return reach; }
}
