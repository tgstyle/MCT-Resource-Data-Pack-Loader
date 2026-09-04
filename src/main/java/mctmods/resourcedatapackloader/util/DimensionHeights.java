package mctmods.resourcedatapackloader.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

public final class DimensionHeights {
    private final String key;
    private String[] raw = new String[0];
    @Nullable private Integer everywhere;
    private Map<Integer, Integer> byDimension = new HashMap<>();

    public DimensionHeights(String key) { this.key = key; }

    @Nullable public Integer at(int dimension, String[] asked) {
        if (asked.length == 0) { return null; }
        if (!Arrays.equals(asked, raw)) {
            Integer bare = null;
            Map<Integer, Integer> scoped = new HashMap<>();
            for (String entry : asked) {
                String line = entry.trim();
                int split = line.indexOf('=');
                String value = split < 0 ? line : line.substring(split + 1).trim();
                int found;
                try { found = Integer.parseInt(value); }
                catch (NumberFormatException wrong) {
                    ContentLog.LOGGER.error("{} names '{}', whose height is not a whole number, ignoring it", key, line);
                    continue;
                }
                if (split < 0) { bare = found; }
                else {
                    try { scoped.put(Integer.parseInt(line.substring(0, split).trim()), found); }
                    catch (NumberFormatException wrong) { ContentLog.LOGGER.error("{} names '{}', whose dimension is not a whole number, ignoring it", key, line); }
                }
            }
            everywhere = bare;
            byDimension = scoped;
            raw = asked;
        }
        Integer scoped = byDimension.get(dimension);
        return scoped != null ? scoped : everywhere;
    }
}
