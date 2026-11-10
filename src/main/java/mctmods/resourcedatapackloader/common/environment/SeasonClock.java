package mctmods.resourcedatapackloader.common.environment;

import javax.annotation.Nullable;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

public final class SeasonClock {
    private static long received;
    private static long since;

    private SeasonClock() {}

    public static long ticks(@Nullable MinecraftServer server) {
        if (server == null) { return received + since; }
        WorldServer overworld = DimensionManager.getWorld(0);
        return overworld == null ? 0L : overworld.getWorldTime();
    }

    public static void received(long overworldTicks) {
        received = overworldTicks;
        since = 0L;
    }

    @SubscribeEvent public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) { since++; }
    }

    @SubscribeEvent public static void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) { received(0L); }
}
