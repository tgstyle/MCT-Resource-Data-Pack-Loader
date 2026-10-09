package mctmods.resourcedatapackloader.util;

public final class Longs {
    private Longs() {}

    public static long pack(int high, int low) { return ((long) high << 32) | (low & 0xFFFFFFFFL); }

    public static int high(long packed) { return (int) (packed >> 32); }

    public static int low(long packed) { return (int) packed; }

    public static int[] toInts(long[] values) {
        int[] halves = new int[values.length * 2];
        for (int i = 0; i < values.length; i++) {
            halves[i * 2] = high(values[i]);
            halves[i * 2 + 1] = low(values[i]);
        }
        return halves;
    }

    public static long[] fromInts(int[] halves) {
        long[] out = new long[halves.length / 2];
        for (int i = 0; i < out.length; i++) { out[i] = pack(halves[i * 2], halves[i * 2 + 1]); }
        return out;
    }
}
