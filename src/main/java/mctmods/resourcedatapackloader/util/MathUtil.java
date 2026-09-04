package mctmods.resourcedatapackloader.util;


public class MathUtil {
    public static float unlerp(final long v, final long min, final long max) { return (v - min) / (float) (max - min); }

    public static float lerp(final float a, final float min, final float max) { return min + a * (max - min); }

    public static long mix(long seed, int x, int y, int z) { return scramble(seed ^ 0x28B7BD766A05068BL, x, y, z); }

    public static long scramble(long h, int x, int y, int z) {
        h ^= x * 0x2545F4914F6CDD1DL;
        h ^= (long) y * 0x6C62272E07BB0142L;
        h ^= (long) z * 0xCBF29CE484222325L;
        h ^= h >>> 33;
        h *= 0xFF51AFD7ED558CCDL;
        h ^= h >>> 33;
        return h;
    }

    public static int max(int a, int b, int c, int d) { return Math.max(Math.max(a, b), Math.max(c, d)); }

}
