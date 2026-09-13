package mctmods.resourcedatapackloader.content.entity.ai;

import net.minecraft.world.level.pathfinder.BlockPathTypes;
import java.util.Arrays;
import javax.annotation.Nullable;

public final class PathNodeMemo {
    private static final BlockPathTypes[] KINDS = BlockPathTypes.values();
    private static final int BITS = 14;
    private static final int SLOTS = 1 << BITS;
    private static final int MASK = SLOTS - 1;
    private static final long SPREAD = 0x9E3779B97F4A7C15L;
    private static final ThreadLocal<PathNodeMemo> HELD = ThreadLocal.withInitial(PathNodeMemo::new);
    private final long[] where = new long[SLOTS];
    private final long[] when = new long[SLOTS];
    private final int[] worlds = new int[SLOTS];
    private final byte[] kinds = new byte[SLOTS];

    private PathNodeMemo() { Arrays.fill(when, Long.MIN_VALUE); }

    public static PathNodeMemo held() { return HELD.get(); }

    private static long packed(int x, int y, int z) { return ((long) (x & 0x3FFFFFF) << 38) | ((long) (y & 0xFFF) << 26) | (long) (z & 0x3FFFFFF); }

    private static int slot(long key, int world) { return (int) (((key ^ (world * SPREAD)) * SPREAD) >>> (64 - BITS)) & MASK; }

    @Nullable public BlockPathTypes known(int world, long tick, int x, int y, int z) {
        long key = packed(x, y, z);
        int slot = slot(key, world);
        if (when[slot] != tick || worlds[slot] != world || where[slot] != key) { return null; }
        return KINDS[kinds[slot]];
    }

    public void remember(int world, long tick, int x, int y, int z, BlockPathTypes kind) {
        long key = packed(x, y, z);
        int slot = slot(key, world);
        where[slot] = key;
        when[slot] = tick;
        worlds[slot] = world;
        kinds[slot] = (byte) kind.ordinal();
    }
}
