package mctmods.resourcedatapackloader.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import javax.annotation.Nullable;

public final class DimensionValues<T> {
    private final String key;
    private final Function<String, T> parse;
    private final String wrong;
    private volatile Snapshot<T> held = new Snapshot<>(new String[0], null, new HashMap<>());

    public DimensionValues(String key, Function<String, T> parse, String wrong) {
        this.key = key;
        this.parse = parse;
        this.wrong = wrong;
    }

    @Nullable public T at(int dimension, String[] asked) { return at(dimension, asked, null); }

    @Nullable public T at(int dimension, String[] asked, @Nullable T own) {
        if (asked.length == 0) { return own; }
        Snapshot<T> now = held;
        if (!Arrays.equals(asked, now.raw)) {
            T bare = null;
            Map<Integer, T> scoped = new HashMap<>();
            for (String entry : asked) {
                String line = entry.trim();
                String[] sides = Settings.split(line);
                T found = parse.apply(sides == null ? line : sides[1]);
                if (found == null) {
                    ContentLog.LOGGER.error("{} names '{}', {}, ignoring it", key, line, wrong);
                    continue;
                }
                if (sides == null) { bare = found; }
                else {
                    try { scoped.put(Integer.parseInt(sides[0]), found); }
                    catch (NumberFormatException wrongDimension) { ContentLog.LOGGER.error("{} names '{}', whose dimension is not a whole number, ignoring it", key, line); }
                }
            }
            now = new Snapshot<>(asked.clone(), bare, scoped);
            held = now;
        }
        T scoped = now.byDimension.get(dimension);
        if (scoped != null) { return scoped; }
        return own != null ? own : now.everywhere;
    }

    private static final class Snapshot<V> {
        private final String[] raw;
        @Nullable private final V everywhere;
        private final Map<Integer, V> byDimension;

        private Snapshot(String[] raw, @Nullable V everywhere, Map<Integer, V> byDimension) {
            this.raw = raw;
            this.everywhere = everywhere;
            this.byDimension = byDimension;
        }
    }
}
