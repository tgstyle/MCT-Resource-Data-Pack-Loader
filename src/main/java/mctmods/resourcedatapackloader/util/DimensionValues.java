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
    private String[] raw = new String[0];
    @Nullable private T everywhere;
    private Map<Integer, T> byDimension = new HashMap<>();

    public DimensionValues(String key, Function<String, T> parse, String wrong) {
        this.key = key;
        this.parse = parse;
        this.wrong = wrong;
    }

    @Nullable public T at(int dimension, String[] asked) {
        if (asked.length == 0) { return null; }
        if (!Arrays.equals(asked, raw)) {
            T bare = null;
            Map<Integer, T> scoped = new HashMap<>();
            for (String entry : asked) {
                String line = entry.trim();
                int split = line.indexOf('=');
                T found = parse.apply(split < 0 ? line : line.substring(split + 1).trim());
                if (found == null) {
                    ContentLog.LOGGER.error("{} names '{}', {}, ignoring it", key, line, wrong);
                    continue;
                }
                if (split < 0) { bare = found; }
                else {
                    try { scoped.put(Integer.parseInt(line.substring(0, split).trim()), found); }
                    catch (NumberFormatException wrongDimension) { ContentLog.LOGGER.error("{} names '{}', whose dimension is not a whole number, ignoring it", key, line); }
                }
            }
            everywhere = bare;
            byDimension = scoped;
            raw = asked;
        }
        T scoped = byDimension.get(dimension);
        return scoped != null ? scoped : everywhere;
    }
}
