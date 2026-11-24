package mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.ToDoubleFunction;
import javax.annotation.Nullable;

final class BestPick {
    private BestPick() {}

    @Nullable static <T> T of(List<T> pool, ToDoubleFunction<T> score, boolean highest, Random random) {
        List<T> ties = new ArrayList<>();
        double best = 0.0D;
        for (T item : pool) {
            double value = score.applyAsDouble(item);
            if (ties.isEmpty() || (highest ? value > best : value < best)) {
                best = value;
                ties.clear();
            }
            if (value == best) { ties.add(item); }
        }
        return ties.isEmpty() ? null : ties.get(random.nextInt(ties.size()));
    }
}
