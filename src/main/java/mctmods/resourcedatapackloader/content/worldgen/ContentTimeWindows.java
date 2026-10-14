package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.DimensionDef;
import mctmods.resourcedatapackloader.content.def.DimensionTraitsDef;
import mctmods.resourcedatapackloader.content.def.TimeWindowDef;

import net.minecraft.world.level.Level;
import javax.annotation.Nullable;

public final class ContentTimeWindows {
    private ContentTimeWindows() {}

    public static long clock(Level level, @Nullable DimensionDef def) {
        long length = def == null ? DimensionTraitsDef.VANILLA_DAY : def.traits().dayLength();
        return Math.floorDiv(level.dimensionType().fixedTime().orElse(level.getDayTime()) * DimensionTraitsDef.VANILLA_DAY, length);
    }

    private static long dayTick(Level level, DimensionDef def) { return Math.floorMod(clock(level, def), DimensionTraitsDef.VANILLA_DAY); }

    @Nullable public static TimeWindowDef active(Level level, @Nullable DimensionDef def) {
        if (def == null || def.windows().isEmpty()) { return null; }
        long tick = dayTick(level, def);
        for (TimeWindowDef window : def.windows()) {
            if (window.contains(tick)) { return window; }
        }
        return null;
    }
}
