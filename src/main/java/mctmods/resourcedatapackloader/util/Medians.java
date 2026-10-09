package mctmods.resourcedatapackloader.util;

import java.util.Arrays;

public final class Medians {
    private Medians() {}

    public static int of(int[] values, int count) {
        if (count == 0) { return Integer.MIN_VALUE; }
        Arrays.sort(values, 0, count);
        return values[count / 2];
    }

    public static int majority(int[] values, int count, int total) { return count * 2 <= total ? Integer.MIN_VALUE : of(values, count); }
}
