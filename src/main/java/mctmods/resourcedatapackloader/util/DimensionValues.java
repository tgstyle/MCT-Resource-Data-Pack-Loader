package mctmods.resourcedatapackloader.util;

import mctmods.resourcedatapackloader.content.ContentFormats;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import javax.annotation.Nullable;

public final class DimensionValues<T> {
    private final String key;
    private final Function<String, T> parse;
    private final String wrong;
    private List<String> raw = List.of();
    @Nullable private T everywhere;
    private Map<String, T> byDimension = new HashMap<>();

    public DimensionValues(String key, Function<String, T> parse, String wrong) {
        this.key = key;
        this.parse = parse;
        this.wrong = wrong;
    }

    @Nullable public T at(String dimension, List<String> asked) {
        if (asked.isEmpty()) { return null; }
        if (!asked.equals(raw)) {
            T bare = null;
            Map<String, T> scoped = new HashMap<>();
            for (String entry : asked) {
                String line = entry.trim();
                int split = line.indexOf('=');
                T found = parse.apply(split < 0 ? line : line.substring(split + 1).trim());
                if (found == null) {
                    ContentLog.LOGGER.error("{} names '{}', {}, ignoring it", key, line, wrong);
                    continue;
                }
                if (split < 0) { bare = found; }
                else { scoped.put(ContentFormats.dimensionId(line.substring(0, split)), found); }
            }
            everywhere = bare;
            byDimension = scoped;
            raw = List.copyOf(asked);
        }
        T scoped = byDimension.get(dimension);
        return scoped != null ? scoped : everywhere;
    }

    public Map<String, T> scoped(List<String> asked) {
        at("", asked);
        return byDimension;
    }
}
