package mctmods.resourcedatapackloader.core.util.math;

public final class Longs {
    private Longs() {}

    public static long pack(int high, int low) { return ((long) high << 32) | (low & 0xFFFFFFFFL); }

    public static long packPos(int x, int y, int z) { return ((long) (x & 0x3FFFFFF) << 38) | ((long) (y & 0xFFF) << 26) | (z & 0x3FFFFFFL); }

    public static long pack21(int x, int y, int z) { return ((x & 0x1FFFFFL) << 42) | ((y & 0x1FFFFFL) << 21) | (z & 0x1FFFFFL); }

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
