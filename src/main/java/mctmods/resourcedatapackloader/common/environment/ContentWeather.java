package mctmods.resourcedatapackloader.common.environment;

import mctmods.resourcedatapackloader.common.pack.loading.ContentControl;
import mctmods.resourcedatapackloader.common.util.worldgen.DimensionHeights;
import mctmods.resourcedatapackloader.core.config.Config;

import javax.annotation.Nullable;
import net.minecraft.world.World;

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
