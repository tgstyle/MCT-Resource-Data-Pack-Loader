package mctmods.resourcedatapackloader.common.environment;

import mctmods.resourcedatapackloader.common.pack.definitions.sky.TimeWindowDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DimensionDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DimensionTraitsDef;
import mctmods.resourcedatapackloader.common.rules.ContentRuleSwitch;

import javax.annotation.Nullable;
import net.minecraft.world.level.Level;

public final class ContentTimeWindows {
    private ContentTimeWindows() {}

    public static long clock(Level level, @Nullable DimensionDef def) {
        long length = def == null ? DimensionTraitsDef.VANILLA_DAY : ContentRuleSwitch.dayLength(level);
        long time = level.dimensionType().fixedTime().orElse(level.getDayTime());
        return Math.floorDiv(time * DimensionTraitsDef.VANILLA_DAY, length);
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
