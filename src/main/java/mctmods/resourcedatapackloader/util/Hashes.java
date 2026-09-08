package mctmods.resourcedatapackloader.util;

public final class Hashes {
    private Hashes() {}

    public static long mix(long seed, int x, int y, int z) {
        long h = seed ^ 0x28B7BD766A05068BL;
        h ^= x * 0x2545F4914F6CDD1DL;
        h ^= (long) y * 0x6C62272E07BB0142L;
        h ^= (long) z * 0xCBF29CE484222325L;
        h ^= h >>> 33;
        h *= 0xFF51AFD7ED558CCDL;
        h ^= h >>> 33;
        return h;
    }

    public static float unit(long seed, int x, int y, int z) { return (mix(seed, x, y, z) >>> 40) / (float) (1 << 24); }
}
