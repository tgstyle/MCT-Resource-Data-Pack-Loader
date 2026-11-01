package mctmods.resourcedatapackloader.common.gates;

import mctmods.resourcedatapackloader.common.pack.definitions.portals.GateDef;
import mctmods.resourcedatapackloader.core.network.RDPLNetwork;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

public final class GateSync {
    private static final int INTERVAL = 20;
    private static final Set<String> WATCHED = new HashSet<>();
    private static final Map<UUID, Set<String>> SENT = new HashMap<>();
    private static int ticks;

    private GateSync() {}

    public static void watch(String gate) { WATCHED.add(gate); }

    public static void onTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || WATCHED.isEmpty() || ++ticks % INTERVAL != 0) { return; }
        for (ServerPlayer player : event.getServer().getPlayerList().getPlayers()) { push(player); }
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

    private static void fresh(ServerPlayer player) {
        SENT.remove(player.getUUID());
        push(player);
    }

    private static void push(ServerPlayer player) {
        if (WATCHED.isEmpty()) { return; }
        Set<String> open = new HashSet<>();
        for (String name : WATCHED) {
            GateDef def = ContentGates.find(name);
            if (def != null && !ContentGates.bars(player, def)) { open.add(def.id()); }
        }
        Set<String> now = Set.copyOf(open);
        if (now.equals(SENT.get(player.getUUID()))) { return; }
        SENT.put(player.getUUID(), now);
        RDPLNetwork.sendGates(player, List.copyOf(now));
    }
}
