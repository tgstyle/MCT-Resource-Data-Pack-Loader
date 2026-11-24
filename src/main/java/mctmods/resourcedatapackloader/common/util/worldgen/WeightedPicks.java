package mctmods.resourcedatapackloader.common.util.worldgen;

import mctmods.resourcedatapackloader.core.config.Settings;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import javax.annotation.Nullable;

public final class WeightedPicks {
    public static final String EMPTY = "empty";

    public static final class Pick {
        public final String name;
        public final String tail;
        Pick(String name, String tail) {
            this.name = name;
            this.tail = tail;
        }
    }

    private final String setting;
    private final List<String> names = new ArrayList<>();
    private final List<String> tails = new ArrayList<>();
    private final List<Integer> weights = new ArrayList<>();
    private final List<Integer> slots = new ArrayList<>();
    private final TemplateMemo<Boolean> template = new TemplateMemo<>();

    public WeightedPicks(String setting) { this.setting = setting; }

    public boolean stale() { return template.stale(); }

    public boolean isEmpty() { return names.isEmpty(); }

    public int size() { return names.size(); }

    public List<String> names() { return names; }

    public void load(String[] entries) {
        names.clear();
        tails.clear();
        weights.clear();
        slots.clear();
        for (String entry : entries) {
            String[] parts = Settings.pair(entry, setting, "name=weight");
            if (parts == null) { continue; }
            String said = parts[1];
            String tail = "";
            int split = said.indexOf(',');
            if (split >= 0) {
                tail = said.substring(split + 1).trim();
                said = said.substring(0, split).trim();
            }
            int weight = weightOf(said, entry);
            if (weight < 1) { continue; }
            names.add(parts[0]);
            tails.add(tail);
            weights.add(weight);
            slots.add(slots.size());
        }
    }

    public void report(String label, Predicate<String> resolves, String badMessage, String countMessage) {
        for (String name : names) {
            if (EMPTY.equals(name) || resolves.test(name)) { continue; }
            ContentLog.LOGGER.error(badMessage, label, name);
        }
        if (!names.isEmpty()) { ContentLog.LOGGER.info(countMessage, names.size()); }
    }

    @Nullable public static <T> T pick(Collection<T> choices, ToIntFunction<T> weight, Random random) {
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

    public static <T> T pickOr(Collection<T> choices, ToIntFunction<T> weight, Random random, T fallback) {
        T picked = pick(choices, weight, random);
        return picked == null ? fallback : picked;
    }

    @Nullable public static <T> T raw(Collection<T> choices, ToIntFunction<T> weight, Random random, @Nullable T fallback) {
        int total = 0;
        for (T choice : choices) { total += weight.applyAsInt(choice); }
        int roll = random.nextInt(total);
        for (T choice : choices) {
            roll -= weight.applyAsInt(choice);
            if (roll < 0) { return choice; }
        }
        return fallback;
    }

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

    @Nullable public Pick pick(Random random) {
        Integer at = pick(slots, weights::get, random);
        return at == null ? null : new Pick(names.get(at), tails.get(at));
    }

    @Nullable public Pick pickFilled(Random random) {
        Pick chosen = pick(random);
        return chosen == null || EMPTY.equals(chosen.name) ? null : chosen;
    }

    private int weightOf(String said, String entry) {
        Integer asked = Settings.whole(said, "{} entry '{}' gives a weight of '{}', which is not a whole number, ignoring the entry", setting, entry, said);
        if (asked == null) { return -1; }
        if (asked >= 1) { return asked; }
        ContentLog.LOGGER.error("{} entry '{}' asks for a weight of {}, which is below 1, ignoring the entry", setting, entry, asked);
        return -1;
    }
}
