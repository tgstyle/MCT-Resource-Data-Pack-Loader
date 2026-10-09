package mctmods.resourcedatapackloader.util;

import mctmods.resourcedatapackloader.content.ContentFormats;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import javax.annotation.Nullable;

public final class DimensionValues<T> {
    private final String key;
    private final Function<String, T> parse;
    private final String wrong;
    private volatile Snapshot<T> held = new Snapshot<>(List.of(), null, Map.of());

    public DimensionValues(String key, Function<String, T> parse, String wrong) {
        this.key = key;
        this.parse = parse;
        this.wrong = wrong;
    }

    private static boolean contains(String dimension, Collection<String> entries) {
        for (String entry : entries) {
            if (ContentFormats.dimensionId(entry).equals(dimension)) { return true; }
        }
        return false;
    }

    public static boolean listed(String dimension, Collection<String> entries, boolean blacklist) { return entries.isEmpty() || contains(dimension, entries) != blacklist; }

    @Nullable public T at(String dimension, List<String> asked) { return at(dimension, asked, null); }

    @Nullable public T at(String dimension, List<String> asked, @Nullable T own) {
        if (asked.isEmpty()) { return own; }
        Snapshot<T> now = held;
        if (!asked.equals(now.raw())) {
            T bare = null;
            Map<String, T> scoped = new HashMap<>();
            for (String entry : asked) {
                String line = entry.trim();
                String[] sides = Settings.split(line);
                T found = parse.apply(sides == null ? line : sides[1]);
                if (found == null) {
                    ContentLog.LOGGER.error("{} names '{}', {}, ignoring it", key, line, wrong);
                    continue;
                }
                if (sides == null) { bare = found; }
                else { scoped.put(ContentFormats.dimensionId(sides[0]), found); }
            }
            now = new Snapshot<>(List.copyOf(asked), bare, scoped);
            held = now;
        }
        T scoped = now.byDimension().get(dimension);
        return scoped != null ? scoped : own != null ? own : now.everywhere();
    }

    private record Snapshot<V>(List<String> raw, @Nullable V everywhere, Map<String, V> byDimension) {}
}
