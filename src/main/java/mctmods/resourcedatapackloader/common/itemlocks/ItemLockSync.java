package mctmods.resourcedatapackloader.common.itemlocks;

import mctmods.resourcedatapackloader.common.util.PlayerValueSync;
import mctmods.resourcedatapackloader.core.network.RDPLNetwork;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.util.Set;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

public final class ItemLockSync {
    private static final int INTERVAL = 20;
    private static final PlayerValueSync<Set<String>> SYNC = new PlayerValueSync<>(ContentItemLocks::open, ContentItemLocks::any, ItemLockSync::send);
    private static int ticks;

    private ItemLockSync() {}

    public static void onTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !ContentItemLocks.any() || ++ticks % INTERVAL != 0) { return; }
        SYNC.pushAll(event.getServer().getPlayerList().getPlayers());
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
