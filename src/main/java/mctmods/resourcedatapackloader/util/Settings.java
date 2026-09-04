package mctmods.resourcedatapackloader.util;

import net.minecraft.resources.ResourceLocation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
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

    public static List<String> forDimension(Collection<? extends String> entries, ResourceLocation dimension, boolean bareApplies, String setting) {
        List<String> bare = new ArrayList<>();
        List<String> scoped = new ArrayList<>();
        for (String entry : entries) {
            String line = entry.trim();
            if (line.isEmpty()) { continue; }
            int split = line.indexOf('=');
            if (split < 0) {
                bare.add(line);
                continue;
            }
            ResourceLocation named = ResourceLocation.tryParse(line.substring(0, split).trim());
            if (named == null) {
                ContentLog.LOGGER.error("{} entry '{}' does not begin with a dimension id such as minecraft:overworld, ignoring it", setting, line);
                continue;
            }
            if (named.equals(dimension)) { scoped.add(line.substring(split + 1).trim()); }
        }
        if (!scoped.isEmpty()) { return scoped; }
        return bareApplies ? bare : Collections.emptyList();
    }

    public static Set<String> lower(Collection<? extends String> values) {
        Set<String> set = new LinkedHashSet<>();
        for (String value : values) { set.add(value.trim().toLowerCase(Locale.ROOT)); }
        return set;
    }
}
