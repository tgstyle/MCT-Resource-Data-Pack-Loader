package mctmods.resourcedatapackloader.util;

import java.util.function.IntPredicate;

public final class Runs {
    private Runs() {}

    public static int end(IntPredicate on, int from, int size) {
        int end = from;
        while (end + 1 < size && on.test(end + 1)) { end++; }
        return end;
    }
}
