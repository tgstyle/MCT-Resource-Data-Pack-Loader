package mctmods.resourcedatapackloader.common.environment;

import mctmods.resourcedatapackloader.common.dimension.ContentDimensions;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DimensionDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DimensionTraitsDef;
import mctmods.resourcedatapackloader.common.pack.loading.ContentControl;
import mctmods.resourcedatapackloader.common.util.dimension.DimensionValues;
import mctmods.resourcedatapackloader.core.config.Config;

import javax.annotation.Nullable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;

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
        DimensionDef def = ContentDimensions.def(level);
        return def == null ? DimensionTraitsDef.DEFAULTS : def.traits();
    }

    public static Biome.Precipitation fallsAs(Level level, Biome biome, Biome.Precipitation found) {
        if (found != Biome.Precipitation.SNOW || traits(level).snow()) { return found; }
        return biome.getBaseTemperature() >= 0.15F ? Biome.Precipitation.RAIN : found;
    }

    @Nullable private static Level level(@Nullable LevelReader world) { return world instanceof Level own ? own : world instanceof WorldGenLevel region ? region.getLevel() : null; }

    @Nullable private static Integer ceilingFor(Level level) {
        if (ContentControl.off(ContentControl.TERRAIN)) { return null; }
        return HEIGHTS.at(level.dimension().location().toString(), ContentControl.lines(ContentControl.TERRAIN, KEY, Config.worldgen.weatherCeiling()));
    }
}
