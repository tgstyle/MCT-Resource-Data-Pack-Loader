package mctmods.resourcedatapackloader.common.itemlocks;

import mctmods.resourcedatapackloader.common.util.PlayerValueSync;
import mctmods.resourcedatapackloader.core.network.RDPLNetwork;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.util.Set;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

public final class ItemLockSync {
    private static final int EVERY = 20;
    private static final PlayerValueSync<Set<String>> SYNC = new PlayerValueSync<>(ContentItemLocks::open, ContentItemLocks::any, ItemLockSync::send);

    private ItemLockSync() {}

    public static void onTick(ServerTickEvent.Post event) {
        MinecraftServer server = event.getServer();
        if (!ContentItemLocks.any() || server.getTickCount() % EVERY != 0) { return; }
        SYNC.pushAll(server.getPlayerList().getPlayers());
    }

    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) { SYNC.fresh(player); }
    }

    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) { SYNC.forget(event.getEntity()); }

    public static void resendAll(MinecraftServer server) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            boolean told = SYNC.told(player);
            SYNC.fresh(player);
            if (told && !ContentItemLocks.any()) { send(player, Set.of()); }
        }
    }

    private static void send(ServerPlayer player, Set<String> open) {
        boolean sent = RDPLNetwork.sendUnlocks(player, open);
        ContentLog.LOGGER.debug("item locks: {} open {} of {} {}, {}", player.getName().getString(), open.size(), ContentItemLocks.count(), open, sent ? "sent" : "not reachable");
    }
}
