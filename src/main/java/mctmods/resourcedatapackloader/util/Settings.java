package mctmods.resourcedatapackloader.util;

import net.minecraft.util.Mth;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class Settings {
    private Settings() {}

    public static String lower(String value) { return value.trim().toLowerCase(Locale.ROOT); }

    public static Set<String> lower(Collection<? extends String> values) {
        Set<String> set = new LinkedHashSet<>();
        for (String value : values) {
            String trimmed = lower(value);
            if (!trimmed.isEmpty()) { set.add(trimmed); }
        }
        return set;
    }

    @Nullable public static String[] split(String entry) {
        int at = entry.indexOf('=');
        return at < 0 ? null : new String[] {entry.substring(0, at).trim(), entry.substring(at + 1).trim()};
    }

    @Nullable public static String[] filled(String entry) {
        String[] sides = split(entry);
        return sides == null || sides[0].isEmpty() || sides[1].isEmpty() ? null : sides;
    }

    public record Weighted(String name, int weight, boolean unreadable) {}

    public static Weighted weighted(String entry, int fallback) {
        int split = entry.lastIndexOf('=');
        if (split < 0) { return new Weighted(entry.trim(), fallback, false); }
        String name = entry.substring(0, split).trim();
        try { return new Weighted(name, Integer.parseInt(entry.substring(split + 1).trim()), false); }
        catch (NumberFormatException held) { return new Weighted(name, fallback, true); }
    }

    @Nullable public static String[] pair(String entry, String setting, String shape) {
        String[] sides = filled(entry);
        if (sides == null) {
            ContentLog.LOGGER.error("{} entry '{}' is not written as {}, ignoring it", setting, entry, shape);
            return null;
        }
        return sides;
    }

    @Nullable public static Integer clamped(String text, int least, int most) {
        try { return Mth.clamp(Integer.parseInt(text.trim()), least, most); }
        catch (NumberFormatException notNumber) { return null; }
    }

    public static List<String> lowered(List<String> values) { return List.copyOf(lower(values)); }
}
