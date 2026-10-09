package mctmods.resourcedatapackloader.util;

public final class Longs {
    private Longs() {}

    public static long pack(int high, int low) { return ((long) high << 32) | (low & 0xFFFFFFFFL); }

    public static int high(long packed) { return (int) (packed >> 32); }

    public static int low(long packed) { return (int) packed; }
}
