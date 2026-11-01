package mctmods.resourcedatapackloader.common.environment;

import mctmods.resourcedatapackloader.common.pack.definitions.sky.TimeWindowDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DimensionDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DimensionTraitsDef;
import mctmods.resourcedatapackloader.common.rules.ContentRuleSwitch;

import javax.annotation.Nullable;
import net.minecraft.world.World;

public final class ContentTimeWindows {
    private static final long STILL = 12000L;

    private ContentTimeWindows() {}

    public static long clock(World world, @Nullable DimensionDef def) {
        long length = ContentRuleSwitch.dayLength(world, def);
        if (length == DimensionTraitsDef.NO_DAY) { return STILL; }
        long time = def != null && def.fixedTime >= 0 ? def.fixedTime : world.getWorldTime();
        return Math.floorDiv(time * DimensionTraitsDef.VANILLA_DAY, length);
    }

    private static long dayTick(World world, DimensionDef def) { return Math.floorMod(clock(world, def), DimensionTraitsDef.VANILLA_DAY); }

    @Nullable public static TimeWindowDef active(World world, DimensionDef def) {
        if (def.windows.isEmpty()) { return null; }
        long tick = dayTick(world, def);
        for (TimeWindowDef window : def.windows) {
            if (window.contains(tick)) { return window; }
        }
        return null;
    }
}
