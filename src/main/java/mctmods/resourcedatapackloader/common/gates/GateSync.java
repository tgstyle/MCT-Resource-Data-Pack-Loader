package mctmods.resourcedatapackloader.common.gates;

import mctmods.resourcedatapackloader.common.pack.definitions.portals.GateDef;
import mctmods.resourcedatapackloader.common.util.PlayerValueSync;
import mctmods.resourcedatapackloader.core.network.RDPLNetwork;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

public final class GateSync {
    private static final Set<String> WATCHED = new HashSet<>();
    private static final PlayerValueSync<Set<String>> SYNC = new PlayerValueSync<>(GateSync::open, () -> !WATCHED.isEmpty(), RDPLNetwork::sendGates);
    private static final int INTERVAL = 20;
    private static int ticks;

    private GateSync() {}

    public static void watch(String gate) { WATCHED.add(gate); }

    private static Set<String> open(ServerPlayer player) {
        Set<String> open = new HashSet<>();
        for (String name : WATCHED) {
            GateDef def = ContentGates.find(name);
            if (def != null && !ContentGates.bars(player, def)) { open.add(def.id()); }
        }
        return open;
    }

    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) { SYNC.fresh(player); }
    }

    public static void onDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) { SYNC.fresh(player); }
    }

    public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) { SYNC.fresh(player); }
    }

    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) { SYNC.forget(event.getEntity()); }

    public static void onServerTick(ServerTickEvent.Post event) {
        if (WATCHED.isEmpty() || ++ticks % INTERVAL != 0) { return; }
        SYNC.pushAll(event.getServer().getPlayerList().getPlayers());
    }
}
