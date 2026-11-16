package mctmods.resourcedatapackloader.common.util.worldgen;

import mctmods.resourcedatapackloader.core.util.math.Hashes;

import java.util.Random;

public final class SeedStreams {
    private SeedStreams() {}

    public static long seed(long siteSeed, String purpose, int index) { return Hashes.mix(siteSeed, purpose.hashCode(), index, 0); }

    public static Random random(long siteSeed, String purpose, int index) { return new Random(seed(siteSeed, purpose, index)); }
}
