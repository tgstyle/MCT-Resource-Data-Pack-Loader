package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.DimensionDef;
import mctmods.resourcedatapackloader.content.def.DimensionTraitsDef;
import mctmods.resourcedatapackloader.content.def.TimeWindowDef;

import net.minecraft.world.World;
import javax.annotation.Nullable;

public final class ContentTimeWindows {
    private static final long STILL = 12000L;

    private ContentTimeWindows() {}

    public static long clock(World world, @Nullable DimensionDef def) {
        long length = def == null ? DimensionTraitsDef.VANILLA_DAY : def.traits.dayLength;
        if (length == DimensionTraitsDef.NO_DAY) { return STILL; }
        long time = def != null && def.fixedTime >= 0 ? def.fixedTime : world.getWorldTime();
        return Math.floorDiv(time * DimensionTraitsDef.VANILLA_DAY, length);
    }

    private static long dayTick(World world, DimensionDef def) { return Math.floorMod(clock(world, def), DimensionTraitsDef.VANILLA_DAY); }

    @Nullable static TimeWindowDef active(World world, DimensionDef def) {
        if (def.windows.isEmpty()) { return null; }
        long tick = dayTick(world, def);
        for (TimeWindowDef window : def.windows) {
            if (window.contains(tick)) { return window; }
        }
        return null;
    }
}
