package mctmods.resourcedatapackloader.common.gates;

import mctmods.resourcedatapackloader.common.pack.definitions.portals.GateDef;
import mctmods.resourcedatapackloader.common.util.PlayerSetSync;
import mctmods.resourcedatapackloader.core.network.RDPLNetwork;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public final class GateSync {
    private static final int INTERVAL = 20;
    private static final Set<String> WATCHED = new HashSet<>();
    private static final PlayerSetSync SYNC = new PlayerSetSync(GateSync::open, () -> !WATCHED.isEmpty(), RDPLNetwork::sendGates);
    private static int ticks;

    private GateSync() {}

    public static void watch(String gate) { WATCHED.add(gate); }

    @SubscribeEvent public static void onTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || WATCHED.isEmpty()) { return; }
        if (++ticks % INTERVAL != 0) { return; }
        SYNC.pushAll(FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers());
    }

    @SubscribeEvent public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) { refresh(event.player); }

    @SubscribeEvent public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) { refresh(event.player); }

    @SubscribeEvent public static void onDimension(PlayerEvent.PlayerChangedDimensionEvent event) { refresh(event.player); }

    @SubscribeEvent public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) { SYNC.forget(event.player); }

    private static void refresh(EntityPlayer player) {
        if (player instanceof EntityPlayerMP) { SYNC.fresh((EntityPlayerMP) player); }
    }

    private static Set<String> open(EntityPlayerMP player) {
        Set<String> open = new HashSet<>();
        for (String name : WATCHED) {
            GateDef def = ContentGates.find(name);
            if (def != null && !ContentGates.bars(player, def)) { open.add(def.getKey()); }
        }
        return open;
    }
}
