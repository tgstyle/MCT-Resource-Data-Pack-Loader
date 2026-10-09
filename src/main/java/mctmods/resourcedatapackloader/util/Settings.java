package mctmods.resourcedatapackloader.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import net.minecraft.util.math.MathHelper;
import javax.annotation.Nullable;

public final class Settings {
    private Settings() {}

    public static final class Weighted {
        public final String name;
        public final int weight;
        public final boolean unreadable;

        private Weighted(String name, int weight, boolean unreadable) {
            this.name = name;
            this.weight = weight;
            this.unreadable = unreadable;
        }
    }

    public static Weighted weighted(String entry, int fallback) {
        int split = entry.lastIndexOf('=');
        if (split < 0) { return new Weighted(entry.trim(), fallback, false); }
        String name = entry.substring(0, split).trim();
        try { return new Weighted(name, Integer.parseInt(entry.substring(split + 1).trim()), false); }
        catch (NumberFormatException held) { return new Weighted(name, fallback, true); }
    }

    @Nullable public static Integer clamped(String text, int least, int most) {
        try { return MathHelper.clamp(Integer.parseInt(text.trim()), least, most); }
        catch (NumberFormatException notNumber) { return null; }
    }

    @Nullable public static String[] split(String entry) {
        int at = entry.indexOf('=');
        return at < 0 ? null : new String[] {entry.substring(0, at).trim(), entry.substring(at + 1).trim()};
    }

    @Nullable public static String[] pair(String entry, String setting, String shape) {
        String[] sides = split(entry);
        if (sides == null || sides[0].isEmpty() || sides[1].isEmpty()) {
            ContentLog.LOGGER.error("{} entry '{}' is not written as {}, ignoring it", setting, entry, shape);
            return null;
        }
        return sides;
    }

    public static List<String> forDimension(String[] entries, int dimension, boolean bareApplies, String setting) {
        List<String> bare = new ArrayList<>();
        List<String> scoped = new ArrayList<>();
        for (String entry : entries) {
            String line = entry.trim();
            if (line.isEmpty()) { continue; }
            String[] sides = split(line);
            if (sides == null) {
                bare.add(line);
                continue;
            }
            try {
                if (Integer.parseInt(sides[0]) == dimension) { scoped.add(sides[1]); }
            }
            catch (NumberFormatException wrong) {
                ContentLog.LOGGER.error("{} entry '{}' does not begin with a whole dimension number, ignoring it", setting, line);
            }
        }
        if (!scoped.isEmpty()) { return scoped; }
        return bareApplies ? bare : Collections.emptyList();
    }

    public static String lower(String value) { return value.trim().toLowerCase(Locale.ROOT); }

    public static Set<String> lower(String[] values) {
        Set<String> set = new LinkedHashSet<>();
        for (String value : values) { set.add(lower(value)); }
        return set;
    }
}
