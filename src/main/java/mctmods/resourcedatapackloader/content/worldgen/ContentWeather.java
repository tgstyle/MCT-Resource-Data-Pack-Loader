package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.DimensionHeights;

import net.minecraft.world.World;
import javax.annotation.Nullable;

public final class ContentWeather {
    private static final String KEY = "weatherCeiling";
    private static final DimensionHeights HEIGHTS = new DimensionHeights(KEY);

    private ContentWeather() {}

    public static boolean above(@Nullable World world, int y) {
        if (world == null) { return false; }
        Integer ceiling = ceilingFor(world.provider.getDimension());
        return ceiling != null && y > ceiling;
    }

    @Nullable public static Integer ceilingFor(int dimension) {
        if (ContentControl.off(ContentControl.TERRAIN)) { return null; }
        return HEIGHTS.at(dimension, ContentControl.lines(ContentControl.TERRAIN, KEY, Config.worldgen.weatherCeiling));
    }
}
