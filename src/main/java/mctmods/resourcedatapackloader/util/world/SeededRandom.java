package mctmods.resourcedatapackloader.util.world;

import mctmods.resourcedatapackloader.util.Hashes;

import net.minecraft.util.RandomSource;

public final class SeededRandom {
    public static final long ACROSS = 341873128712L;
    public static final long ALONG = 132897987541L;

    private SeededRandom() {}

    public static long salt(int x, int z) { return x * ACROSS + z * ALONG; }

    public static long keyed(long seed, int x, int z) { return seed ^ (x * ACROSS) ^ (z * ALONG); }

    public static long spot(long seed, int x, int z) { return Hashes.mix(seed, x, 0, z); }

    public static RandomSource at(long seed, int x, int z) { return RandomSource.create(seed ^ salt(x, z)); }
}
