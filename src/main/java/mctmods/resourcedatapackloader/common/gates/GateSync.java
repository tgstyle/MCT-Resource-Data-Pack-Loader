package mctmods.resourcedatapackloader.common.gates;

import mctmods.resourcedatapackloader.common.pack.definitions.portals.GateDef;
import mctmods.resourcedatapackloader.core.network.RDPLNetwork;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public final class GateSync {
    private static final int INTERVAL = 20;
    private static final Set<String> WATCHED = new HashSet<>();
    private static final Map<UUID, Set<String>> SENT = new HashMap<>();
    private static int ticks;

    private GateSync() {}

    public static void watch(String gate) { WATCHED.add(gate); }

    @SubscribeEvent public static void onTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || WATCHED.isEmpty()) { return; }
        if (++ticks % INTERVAL != 0) { return; }
        for (EntityPlayerMP player : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers()) { push(player); }
    }

    @SubscribeEvent public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) { refresh(event.player); }

    @SubscribeEvent public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) { refresh(event.player); }

    @SubscribeEvent public static void onDimension(PlayerEvent.PlayerChangedDimensionEvent event) { refresh(event.player); }

    @SubscribeEvent public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) { SENT.remove(event.player.getUniqueID()); }

    private static void refresh(EntityPlayer player) {
        if (!(player instanceof EntityPlayerMP)) { return; }
        SENT.remove(player.getUniqueID());
        push((EntityPlayerMP) player);
    }

    private static void push(EntityPlayerMP player) {
        if (WATCHED.isEmpty()) { return; }
        Set<String> open = new HashSet<>();
        for (String name : WATCHED) {
            GateDef def = ContentGates.find(name);
            if (def != null && !ContentGates.bars(player, def)) { open.add(def.getKey()); }
        }
        UUID id = player.getUniqueID();
        if (open.equals(SENT.get(id))) { return; }
        SENT.put(id, open);
        RDPLNetwork.sendGates(player, open);
    }
}
