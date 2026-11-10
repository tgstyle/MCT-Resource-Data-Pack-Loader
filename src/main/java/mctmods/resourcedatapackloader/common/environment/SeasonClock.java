package mctmods.resourcedatapackloader.common.environment;

import javax.annotation.Nullable;
import net.minecraft.server.MinecraftServer;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public final class SeasonClock {
    private static long received;
    private static long since;

    private SeasonClock() {}

    public static long ticks() { return ticks(ServerLifecycleHooks.getCurrentServer()); }

    public static long ticks(@Nullable MinecraftServer server) { return server == null ? received + since : server.overworld().getDayTime(); }

    public static void received(long overworldTicks) {
        received = overworldTicks;
        since = 0L;
    }

    public static void tick() { since++; }
}
