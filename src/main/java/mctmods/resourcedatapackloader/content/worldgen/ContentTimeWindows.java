package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.DimensionDef;
import mctmods.resourcedatapackloader.content.def.DimensionTraitsDef;
import mctmods.resourcedatapackloader.content.def.TimeWindowDef;

import net.minecraft.world.level.Level;
import javax.annotation.Nullable;

public final class ContentTimeWindows {
    private ContentTimeWindows() {}

    private static long dayTick(Level level, DimensionDef def) {
        long length = def.traits().dayLength();
        long time = def.fixedTime() >= 0L ? def.fixedTime() : level.getOverworldClockTime();
        return Math.floorMod(time, length) * DimensionTraitsDef.VANILLA_DAY / length;
    }

    @Nullable public static TimeWindowDef active(Level level, DimensionDef def) {
        if (def.windows().isEmpty()) { return null; }
        long tick = dayTick(level, def);
        for (TimeWindowDef window : def.windows()) {
            if (window.contains(tick)) { return window; }
        }
        return null;
    }
}
