package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.DimensionValues;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.WorldGenLevel;
import javax.annotation.Nullable;

public final class ContentWeather {
    private static final String KEY = "weatherCeiling";
    private static final DimensionValues<Integer> HEIGHTS = new DimensionValues<>(KEY, ContentDimensions::height, "which is not a whole number");

    private ContentWeather() {}

    public static boolean above(@Nullable LevelReader world, int y) {
        Level level = world instanceof Level own ? own : world instanceof WorldGenLevel region ? region.getLevel() : null;
        if (level == null) { return false; }
        Integer ceiling = ceilingFor(level.dimension().location().toString());
        return ceiling != null && y > ceiling;
    }

    @Nullable private static Integer ceilingFor(String dimension) {
        if (ContentControl.off(ContentControl.TERRAIN)) { return null; }
        return HEIGHTS.at(dimension, ContentControl.lines(ContentControl.TERRAIN, KEY, Config.worldgen.weatherCeiling()));
    }
}
