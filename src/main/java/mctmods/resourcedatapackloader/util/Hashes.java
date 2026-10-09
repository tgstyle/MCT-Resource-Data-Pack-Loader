package mctmods.resourcedatapackloader.util;

public final class Hashes {
    public static final long GOLDEN = 0x9E3779B97F4A7C15L;

    private Hashes() {}

    public static long cell(int x, int y, int z) { return x * 0x2545F4914F6CDD1DL ^ (long) y * 0x6C62272E07BB0142L ^ (long) z * 0xCBF29CE484222325L; }

    public static long scramble(long hash) {
        long h = hash ^ (hash >>> 33);
        h *= 0xFF51AFD7ED558CCDL;
        return h ^ (h >>> 33);
    }

    public static long mix(long seed, int x, int y, int z) { return scramble(seed ^ 0x28B7BD766A05068BL ^ cell(x, y, z)); }

    public static float unit(long hash) { return (hash >>> 40) / (float) (1 << 24); }

    public static float unit(long seed, int x, int y, int z) { return unit(mix(seed, x, y, z)); }
}
