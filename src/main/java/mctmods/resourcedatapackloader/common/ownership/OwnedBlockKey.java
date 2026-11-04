package mctmods.resourcedatapackloader.common.ownership;

import mctmods.resourcedatapackloader.common.util.world.Coords;

public final class OwnedBlockKey {
    private static final int Y_SHIFT = 8;
    private static final int Z_SHIFT = 4;
    private static final int Y_MASK = 0xFFFF;

    private OwnedBlockKey() {}

    public static int pack(int x, int y, int z) { return Coords.blockToLocal(x) | Coords.blockToLocal(z) << Z_SHIFT | (y & Y_MASK) << Y_SHIFT; }

    public static int x(int packed) { return Coords.blockToLocal(packed); }

    public static int z(int packed) { return Coords.blockToLocal(packed >> Z_SHIFT); }

    public static int y(int packed) { return (short) (packed >> Y_SHIFT & Y_MASK); }
}
