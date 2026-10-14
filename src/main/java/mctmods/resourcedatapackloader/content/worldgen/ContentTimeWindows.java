package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.DimensionDef;
import mctmods.resourcedatapackloader.content.def.DimensionTraitsDef;
import mctmods.resourcedatapackloader.content.def.TimeWindowDef;

import net.minecraft.world.World;
import javax.annotation.Nullable;

final class ContentTimeWindows {
    private static final long STILL = 12000L;

    private ContentTimeWindows() {}

    private static long dayTick(World world, DimensionDef def) {
        long length = def.traits.dayLength;
        if (length == DimensionTraitsDef.NO_DAY) { return STILL; }
        return Math.floorMod(world.getWorldTime(), length) * DimensionTraitsDef.VANILLA_DAY / length;
    }

    @Nullable static TimeWindowDef active(World world, DimensionDef def) {
        if (def.windows.isEmpty()) { return null; }
        long tick = dayTick(world, def);
        for (TimeWindowDef window : def.windows) {
            if (window.contains(tick)) { return window; }
        }
        return null;
    }
}
