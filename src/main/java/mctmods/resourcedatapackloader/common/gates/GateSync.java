package mctmods.resourcedatapackloader.common.gates;

import mctmods.resourcedatapackloader.common.pack.definitions.portals.GateDef;
import mctmods.resourcedatapackloader.core.network.RDPLNetwork;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

public final class GateSync {
    private static final Set<String> WATCHED = new HashSet<>();
    private static final Map<UUID, Set<String>> SENT = new HashMap<>();
    private static final int INTERVAL = 20;
    private static int ticks;

    private GateSync() {}

    public static void watch(String gate) { WATCHED.add(gate); }

    private static void push(ServerPlayer player) {
        if (WATCHED.isEmpty()) { return; }
        Set<String> open = new HashSet<>();
        for (String name : WATCHED) {
            GateDef def = ContentGates.find(name);
            if (def != null && !ContentGates.bars(player, def)) { open.add(def.id()); }
        }
        if (open.equals(SENT.get(player.getUUID()))) { return; }
        SENT.put(player.getUUID(), open);
        RDPLNetwork.sendGates(player, open);
    }

    private static void fresh(ServerPlayer player) {
        SENT.remove(player.getUUID());
        push(player);
    }

    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) { fresh(player); }
    }

    public static void onDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) { fresh(player); }
    }

    public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) { fresh(player); }
    }

    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) { SENT.remove(event.getEntity().getUUID()); }

    public static void onServerTick(ServerTickEvent.Post event) {
        if (WATCHED.isEmpty() || ++ticks % INTERVAL != 0) { return; }
        for (ServerPlayer player : event.getServer().getPlayerList().getPlayers()) { push(player); }
    }
}
