package mctmods.resourcedatapackloader.util;

import net.minecraft.block.state.IBlockState;

public final class StateIdCache {
    public static final int MISS = Integer.MIN_VALUE;
    private static final int SLOTS = 32;
    private final IBlockState[] states = new IBlockState[SLOTS];
    private final int[] ids = new int[SLOTS];

    public int held(IBlockState key) {
        int slot = System.identityHashCode(key) & SLOTS - 1;
        return states[slot] == key ? ids[slot] : MISS;
    }

    public int remember(IBlockState key, int id) {
        int slot = System.identityHashCode(key) & SLOTS - 1;
        states[slot] = key;
        ids[slot] = id;
        return id;
    }
}
