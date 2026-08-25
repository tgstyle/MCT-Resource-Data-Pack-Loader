package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.world.World;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

public final class ContentWeather {
    private static final String KEY = "weatherCeiling";
    private static String[] raw = new String[0];
    @Nullable private static Integer everywhere;
    private static Map<Integer, Integer> byDimension = new HashMap<>();

    private ContentWeather() {}

    public static boolean above(@Nullable World world, int y) {
        if (world == null) { return false; }
        Integer ceiling = ceilingFor(world.provider.getDimension());
        return ceiling != null && y > ceiling;
    }

    @Nullable public static Integer ceilingFor(int dimension) {
        if (ContentControl.off(ContentControl.TERRAIN)) { return null; }
        String[] asked = ContentControl.lines(ContentControl.TERRAIN, KEY, Config.worldgen.weatherCeiling);
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
                    ContentLog.LOGGER.error("{} names '{}', whose height is not a whole number, ignoring it", KEY, line);
                    continue;
                }
                if (split < 0) { bare = found; }
                else {
                    try { scoped.put(Integer.parseInt(line.substring(0, split).trim()), found); }
                    catch (NumberFormatException wrong) { ContentLog.LOGGER.error("{} names '{}', whose dimension is not a whole number, ignoring it", KEY, line); }
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
