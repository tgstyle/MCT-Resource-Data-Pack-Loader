package mctmods.resourcedatapackloader.common.util.worldgen;

import mctmods.resourcedatapackloader.core.util.math.MathUtil;

import java.util.Random;

public final class SeedStreams {
    private SeedStreams() {}

    public static long seed(long siteSeed, String purpose, int index) { return MathUtil.mix(siteSeed, purpose.hashCode(), index, 0); }

    public static long keyed(long worldSeed, Object key, int x, int salt, int z) { return MathUtil.mix(worldSeed ^ key.hashCode(), x, salt, z); }

    public static Random random(long siteSeed, String purpose, int index) { return new Random(seed(siteSeed, purpose, index)); }
}
