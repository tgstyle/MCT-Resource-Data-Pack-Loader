package mctmods.resourcedatapackloader.core.util.math;

import java.util.function.IntPredicate;

public final class Runs {
    private Runs() {}

    public interface Visitor {
        void run(int from, int end);
    }

    public static void each(boolean[] on, Visitor visit) {
        int i = 0;
        while (i < on.length) {
            if (!on[i]) {
                i++;
                continue;
            }
            int end = end(k -> on[k], i, on.length);
            visit.run(i, end);
            i = end + 1;
        }
    }

    public static int end(IntPredicate on, int from, int size) {
        int end = from;
        while (end + 1 < size && on.test(end + 1)) { end++; }
        return end;
    }
}
