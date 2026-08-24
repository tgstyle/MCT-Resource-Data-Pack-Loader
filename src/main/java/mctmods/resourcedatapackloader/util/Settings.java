package mctmods.resourcedatapackloader.util;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import javax.annotation.Nullable;

public final class Settings {
    private Settings() {}

    @Nullable public static String[] pair(String entry, String setting, String shape) {
        int split = entry.indexOf('=');
        String left = split < 0 ? "" : entry.substring(0, split).trim();
        String right = split < 0 ? "" : entry.substring(split + 1).trim();
        if (left.isEmpty() || right.isEmpty()) {
            ContentLog.LOGGER.error("{} entry '{}' is not written as {}, ignoring it", setting, entry, shape);
            return null;
        }
        return new String[] {left, right};
    }

    public static Set<String> lower(String[] values) {
        Set<String> set = new LinkedHashSet<>();
        for (String value : values) { set.add(value.trim().toLowerCase(Locale.ROOT)); }
        return set;
    }
}
