package mctmods.resourcedatapackloader.content.entity.ai;

import net.minecraft.pathfinding.PathNodeType;

public final class PathNodeMemo {
    private static final PathNodeType[] KINDS = PathNodeType.values();
    private static final int BITS = 14;
    private static final int SLOTS = 1 << BITS;
    private static final int MASK = SLOTS - 1;
    private static final long SPREAD = 0x9E3779B97F4A7C15L;
    private static final ThreadLocal<PathNodeMemo> HELD = ThreadLocal.withInitial(PathNodeMemo::new);
    private final long[] where = new long[SLOTS];
    private final int[] when = new int[SLOTS];
    private final int[] worlds = new int[SLOTS];
    private final int[] owners = new int[SLOTS];
    private final byte[] kinds = new byte[SLOTS];

    private PathNodeMemo() {}

    public static PathNodeMemo held() { return HELD.get(); }

    private static long packed(int x, int y, int z) { return ((long) (x & 0x3FFFFFF) << 38) | ((long) (y & 0xFFF) << 26) | (long) (z & 0x3FFFFFF); }

    private static int slot(long key, int world, int owner) { return (int) (((key ^ (world * SPREAD) ^ (owner * SPREAD)) * SPREAD) >>> (64 - BITS)) & MASK; }

    public PathNodeType known(int owner, int world, int tick, int x, int y, int z) {
        long key = packed(x, y, z);
        int slot = slot(key, world, owner);
        if (when[slot] != tick || worlds[slot] != world || owners[slot] != owner || where[slot] != key) { return null; }
        return KINDS[kinds[slot]];
    }

    public void remember(int owner, int world, int tick, int x, int y, int z, PathNodeType kind) {
        long key = packed(x, y, z);
        int slot = slot(key, world, owner);
        where[slot] = key;
        when[slot] = tick;
        worlds[slot] = world;
        owners[slot] = owner;
        kinds[slot] = (byte) kind.ordinal();
    }
}
