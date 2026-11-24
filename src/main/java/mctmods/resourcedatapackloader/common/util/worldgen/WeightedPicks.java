package mctmods.resourcedatapackloader.common.util.worldgen;

import net.minecraft.util.RandomSource;
import java.util.Collection;
import java.util.Random;
import java.util.function.IntUnaryOperator;
import java.util.function.ToIntFunction;
import javax.annotation.Nullable;

public final class WeightedPicks {
    private WeightedPicks() {}

    @Nullable public static <T> T pick(Collection<T> choices, ToIntFunction<T> weight, Random random) { return pick(choices, weight, random::nextInt); }

    @Nullable public static <T> T pick(Collection<T> choices, ToIntFunction<T> weight, RandomSource random) { return pick(choices, weight, random::nextInt); }

    public static <T> T pickOr(Collection<T> choices, ToIntFunction<T> weight, Random random, T fallback) {
        T picked = pick(choices, weight, random);
        return picked == null ? fallback : picked;
    }

    @Nullable public static <T> T raw(Collection<T> choices, ToIntFunction<T> weight, Random random, @Nullable T fallback) { return raw(choices, weight, random::nextInt, fallback); }

    @Nullable public static <T> T raw(Collection<T> choices, ToIntFunction<T> weight, RandomSource random, @Nullable T fallback) { return raw(choices, weight, random::nextInt, fallback); }

    public static int index(int[] weights, Random random) {
        int total = 0;
        for (int weight : weights) { total += Math.max(0, weight); }
        int roll = random.nextInt(total);
        for (int i = 0; i < weights.length; i++) {
            roll -= Math.max(0, weights[i]);
            if (roll < 0) { return i; }
        }
        return 0;
    }

    @Nullable private static <T> T raw(Collection<T> choices, ToIntFunction<T> weight, IntUnaryOperator roller, @Nullable T fallback) {
        int total = 0;
        for (T choice : choices) { total += weight.applyAsInt(choice); }
        int roll = roller.applyAsInt(total);
        for (T choice : choices) {
            roll -= weight.applyAsInt(choice);
            if (roll < 0) { return choice; }
        }
        return fallback;
    }

    @Nullable private static <T> T pick(Collection<T> choices, ToIntFunction<T> weight, IntUnaryOperator roller) {
        int total = 0;
        for (T choice : choices) { total += Math.max(0, weight.applyAsInt(choice)); }
        if (total <= 0) { return null; }
        int roll = roller.applyAsInt(total);
        for (T choice : choices) {
            roll -= Math.max(0, weight.applyAsInt(choice));
            if (roll < 0) { return choice; }
        }
        return null;
    }
}
