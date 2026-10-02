package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.def.DimensionDef;
import mctmods.resourcedatapackloader.content.def.DimensionTraitsDef;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.DimensionValues;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import javax.annotation.Nullable;

public final class ContentWeather {
    private static final String KEY = "weatherCeiling";
    private static final DimensionValues<Integer> HEIGHTS = new DimensionValues<>(KEY, ContentDimensions::height, "which is not a whole number");

    private ContentWeather() {}

    public static boolean above(@Nullable LevelReader world, int y) {
        Level level = level(world);
        if (level == null) { return false; }
        Integer ceiling = ceilingFor(level);
        return ceiling != null && y > ceiling;
    }

    public static DimensionTraitsDef traits(@Nullable LevelReader world) {
        Level level = level(world);
        DimensionDef def = level == null ? null : ContentDimensions.def(level);
        return def == null ? DimensionTraitsDef.DEFAULTS : def.traits();
    }

    public static Biome.Precipitation fallsAs(Level level, Biome biome, Biome.Precipitation found) {
        if (found != Biome.Precipitation.SNOW || traits(level).snow()) { return found; }
        return biome.getBaseTemperature() >= 0.15F ? Biome.Precipitation.RAIN : found;
    }

    @Nullable private static Level level(@Nullable LevelReader world) { return world instanceof Level own ? own : world instanceof WorldGenLevel region ? region.getLevel() : null; }

    @Nullable private static Integer ceilingFor(Level level) {
        if (ContentControl.off(ContentControl.TERRAIN)) { return null; }
        return HEIGHTS.at(level.dimension().location().toString(),ContentControl.lines(ContentControl.TERRAIN, KEY, Config.worldgen.weatherCeiling()));
    }
}
