package mctmods.resourcedatapackloader.util;


public class MathUtil {
    public static float unlerp(final long v, final long min, final long max) { return (v - min) / (float) (max - min); }

    public static float lerp(final float a, final float min, final float max) { return min + a * (max - min); }

    public static int max(int a, int b, int c) { return Math.max(Math.max(a, b), c); }

    public static int max(int a, int b, int c, int d) { return Math.max(Math.max(a, b), Math.max(c, d)); }

    public static boolean rangesIntersect(int min1, int max1, int min2, int max2) { return min1 <= max2 && min2 <= max1; }
}
