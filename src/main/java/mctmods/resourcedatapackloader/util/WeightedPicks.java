package mctmods.resourcedatapackloader.util;

import net.minecraft.util.RandomSource;
import java.util.Collection;
import java.util.function.ToIntFunction;
import javax.annotation.Nullable;

public final class WeightedPicks {
    private WeightedPicks() {}

    @Nullable public static <T> T pick(Collection<T> choices, ToIntFunction<T> weight, RandomSource random) {
        int total = 0;
        for (T choice : choices) { total += Math.max(0, weight.applyAsInt(choice)); }
        if (total <= 0) { return null; }
        int roll = random.nextInt(total);
        for (T choice : choices) {
            roll -= Math.max(0, weight.applyAsInt(choice));
            if (roll < 0) { return choice; }
        }
        return null;
    }
}
