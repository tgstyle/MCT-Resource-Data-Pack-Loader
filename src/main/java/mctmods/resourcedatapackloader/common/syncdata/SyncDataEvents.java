package mctmods.resourcedatapackloader.common.syncdata;

import mctmods.resourcedatapackloader.core.network.RDPLNetwork;
import mctmods.resourcedatapackloader.core.network.SyncDataMessage;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public final class SyncDataEvents {
    private SyncDataEvents() {}

    @SubscribeEvent public static void onTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || SyncData.QUEUE.isEmpty()) { return; }
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server != null) { flush(server); }
        SyncData.QUEUE.clear();
    }

    @SubscribeEvent public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.player instanceof EntityPlayerMP)) { return; }
        EntityPlayerMP player = (EntityPlayerMP) event.player;
        snapshot(player, SyncData.peek(player.world), false);
    }

    @SubscribeEvent public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) { SyncData.logout(event.player.getUniqueID()); }

    public static void reloaded(MinecraftServer server) {
        boolean had = SyncDataRules.any();
        SyncDataRules.load();
        SyncData data = SyncData.peek(server.getWorld(0));
        if (!had && !SyncDataRules.any() && data == null) { return; }
        SyncData.revalidate(server.getWorld(0));
        for (EntityPlayerMP player : server.getPlayerList().getPlayers()) { snapshot(player, data, true); }
    }

    private static void flush(MinecraftServer server) {
        SyncData data = SyncData.peek(server.getWorld(0));
        Map<UUID, EntityPlayerMP> online = new HashMap<>();
        for (EntityPlayerMP each : server.getPlayerList().getPlayers()) { online.put(each.getUniqueID(), each); }
        for (Map.Entry<UUID, Set<String>> queued : SyncData.QUEUE.entrySet()) {
            EntityPlayerMP player = online.get(queued.getKey());
            if (player == null) { continue; }
            List<SyncDataMessage.Entry> entries = new ArrayList<>();
            for (String key : queued.getValue()) { entries.add(entry(data, queued.getKey(), key)); }
            send(player, false, entries);
        }
    }

    private static SyncDataMessage.Entry entry(@Nullable SyncData data, UUID id, String key) {
        String name = key.substring(SyncData.WORLD_KEY.length());
        if (key.startsWith(SyncData.PLAYER_KEY)) { return new SyncDataMessage.Entry(name, true, data == null ? null : data.playerValue(id, name)); }
        SyncValue shared = data == null ? null : data.worldValue(name);
        return new SyncDataMessage.Entry(name, false, shared != null ? shared : SyncDataRules.defaultOf(name));
    }

    private static void snapshot(EntityPlayerMP player, @Nullable SyncData data, boolean always) {
        SyncData.QUEUE.remove(player.getUniqueID());
        Map<String, SyncValue> shared = new LinkedHashMap<>();
        for (SyncDataRules.Declaration declared : SyncDataRules.all()) {
            if (declared.fallback != null) { shared.put(declared.name, declared.fallback); }
        }
        Map<String, SyncValue> own = data == null ? Collections.emptyMap() : data.playerValues(player.getUniqueID());
        if (data != null) { shared.putAll(data.worldValues()); }
        List<SyncDataMessage.Entry> entries = new ArrayList<>();
        for (Map.Entry<String, SyncValue> held : shared.entrySet()) { entries.add(new SyncDataMessage.Entry(held.getKey(), false, held.getValue())); }
        for (Map.Entry<String, SyncValue> held : own.entrySet()) { entries.add(new SyncDataMessage.Entry(held.getKey(), true, held.getValue())); }
        if (always || !entries.isEmpty()) { send(player, true, entries); }
    }

    private static void send(EntityPlayerMP player, boolean full, List<SyncDataMessage.Entry> entries) {
        if (entries.isEmpty() && !full) { return; }
        int from = 0;
        do {
            List<SyncDataMessage.Entry> part = new ArrayList<>(entries.subList(from, Math.min(entries.size(), from + SyncDataMessage.MAX_ENTRIES)));
            if (!RDPLNetwork.sendSyncData(player, new SyncDataMessage(full && from == 0, part))) {
                ContentLog.LOGGER.debug("Synced data is not sent to {}: no RDPL on the client", player.getName());
                return;
            }
            ContentLog.LOGGER.debug("Synced data sent to {}: {} value(s), full={}", player.getName(), part.size(), full && from == 0);
            from += SyncDataMessage.MAX_ENTRIES;
        } while (from < entries.size());
    }
}
