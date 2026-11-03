package mctmods.resourcedatapackloader.common.itemlocks;

import mctmods.resourcedatapackloader.common.util.PlayerValueSync;
import mctmods.resourcedatapackloader.core.network.RDPLNetwork;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.util.Collections;
import java.util.Set;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public final class ItemLockSync {
    private static final int INTERVAL = 20;
    private static final PlayerValueSync<Set<String>> SYNC = new PlayerValueSync<>(ContentItemLocks::open, ContentItemLocks::any, ItemLockSync::send);
    private static int ticks;

    private ItemLockSync() {}

    @SubscribeEvent public static void onTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !ContentItemLocks.any()) { return; }
        if (++ticks % INTERVAL != 0) { return; }
        SYNC.pushAll(FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers());
    }

    @SubscribeEvent public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.player instanceof EntityPlayerMP) { SYNC.fresh((EntityPlayerMP) event.player); }
    }

    @SubscribeEvent public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) { SYNC.forget(event.player); }

    public static void resendAll(MinecraftServer server) {
        for (EntityPlayerMP player : server.getPlayerList().getPlayers()) {
            boolean told = SYNC.told(player);
            SYNC.fresh(player);
            if (told && !ContentItemLocks.any()) { send(player, Collections.emptySet()); }
        }
    }

    private static void send(EntityPlayerMP player, Set<String> open) {
        boolean sent = RDPLNetwork.sendUnlocks(player, open);
        ContentLog.LOGGER.debug("item locks: {} open {} of {} {}, {}", player.getName(), open.size(), ContentItemLocks.count(), open, sent ? "sent" : "not reachable");
    }
}
