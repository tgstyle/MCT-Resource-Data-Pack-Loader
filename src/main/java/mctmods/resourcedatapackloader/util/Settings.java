package mctmods.resourcedatapackloader.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class Settings {
    private Settings() {}

    public static Set<String> lower(Collection<? extends String> values) {
        Set<String> set = new LinkedHashSet<>();
        for (String value : values) { set.add(value.trim().toLowerCase(Locale.ROOT)); }
        return set;
    }

    public static List<String> lowered(List<String> values) {
        List<String> out = new ArrayList<>(values.size());
        for (String value : values) { out.add(value.trim().toLowerCase(Locale.ROOT)); }
        return Collections.unmodifiableList(out);
    }

    public static List<String> entries(String text) {
        List<String> found = new ArrayList<>();
        int depth = 0;
        int from = 0;
        for (int at = 0; at < text.length(); at++) {
            char held = text.charAt(at);
            if (held == '[' || held == '{') { depth++; }
            else if (held == ']' || held == '}') { depth--; }
            else if (held == ',' && depth == 0) {
                found.add(text.substring(from, at));
                from = at + 1;
            }
        }
        found.add(text.substring(from));
        return found;
    }
}
