package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.GateDef;
import mctmods.resourcedatapackloader.content.gate.ContentGates;
import mctmods.resourcedatapackloader.network.AbstractClientMessageHandler;
import mctmods.resourcedatapackloader.network.MessageGates;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SideOnly(Side.CLIENT) public final class ContentSkyGates {
    private static Set<String> OPEN = Collections.emptySet();
    private static final Set<String> MISSING = new HashSet<>();
    private static final Map<String, Boolean> HIDDEN = new HashMap<>();

    private ContentSkyGates() {}

    public static void receive(List<String> gates) {
        Set<String> open = new HashSet<>(gates);
        if (open.equals(OPEN)) { return; }
        OPEN = open;
        HIDDEN.clear();
        SkyOccluder.invalidate();
    }

    public static void clear() {
        OPEN = Collections.emptySet();
        HIDDEN.clear();
        SkyOccluder.invalidate();
    }

    public static void forget() { HIDDEN.clear(); }

    public static boolean hides(String gate) {
        if (gate.isEmpty()) { return false; }
        Boolean hidden = HIDDEN.get(gate);
        if (hidden == null) {
            hidden = locked(gate);
            HIDDEN.put(gate, hidden);
        }
        return hidden;
    }

    private static boolean locked(String gate) {
        GateDef def = ContentGates.find(gate);
        if (def == null) {
            if (MISSING.add(gate)) { ContentLog.LOGGER.error("A sky body names the gate {}, which no gate defines, so the body shows", gate); }
            return false;
        }
        return !OPEN.contains(def.getKey());
    }

    @SubscribeEvent public static void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) { clear(); }

    public static class Handler extends AbstractClientMessageHandler<MessageGates> {
        @Override public void handleClientMessage(World world, EntityPlayer player, MessageGates message, MessageContext ctx) { receive(message.gates); }
    }
}
