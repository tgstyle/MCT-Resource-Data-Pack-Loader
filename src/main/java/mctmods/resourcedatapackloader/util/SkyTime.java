package mctmods.resourcedatapackloader.util;

import net.minecraft.SharedConstants;
import net.minecraft.util.Mth;

public final class SkyTime {
    private SkyTime() {}

    public static double timeOfDay(long time) {
        double turn = (time / (double) SharedConstants.TICKS_PER_GAME_DAY - 0.25) - Math.floor(time / (double) SharedConstants.TICKS_PER_GAME_DAY - 0.25);
        return (turn * 2.0 + (0.5 - Math.cos(turn * Math.PI) / 2.0)) / 3.0;
    }

    public static double daylight(long time) { return Mth.clamp(Math.cos(timeOfDay(time) * Math.PI * 2.0) * 2.0 + 0.5, 0.0, 1.0); }
}
