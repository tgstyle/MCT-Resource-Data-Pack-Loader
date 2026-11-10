package mctmods.resourcedatapackloader.common.environment;

import javax.annotation.Nullable;
import net.minecraft.server.MinecraftServer;

public final class SeasonClock {
    private static long received;
    private static long since;

    private SeasonClock() {}

    public static long ticks(@Nullable MinecraftServer server) {
        if (server == null) { return received + since; }
        return server.overworld().getOverworldClockTime();
    }

    public static void received(long overworldTicks) {
        received = overworldTicks;
        since = 0L;
    }

    public static void tick() { since++; }
}
