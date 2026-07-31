package mctmods.resourcedatapackloader.util;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

public final class Names {
    private Names() {}

    public static Set<String> lower(String[] values) {
        Set<String> set = new LinkedHashSet<>();
        for (String value : values) { set.add(value.trim().toLowerCase(Locale.ROOT)); }
        return set;
    }
}
