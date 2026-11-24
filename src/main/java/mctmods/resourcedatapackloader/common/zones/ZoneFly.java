package mctmods.resourcedatapackloader.common.zones;

import mctmods.resourcedatapackloader.common.pack.definitions.zones.ZoneDef;
import mctmods.resourcedatapackloader.common.util.ServerTicks;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public final class ZoneFly {
    private static final int EVERY = 20;

    private ZoneFly() {}

    @SubscribeEvent public static void onTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) { return; }
        MinecraftServer server = ServerTicks.every(EVERY);
        if (server == null) { return; }
        for (EntityPlayerMP player : server.getPlayerList().getPlayers()) {
            if (ZoneRules.none(player.world)) { return; }
            ZoneDef zone = ZoneRules.covering(player, player.getPosition(), held -> held.noFly);
            if (zone != null) { ground(player, zone); }
        }
    }

    private static void ground(EntityPlayerMP player, ZoneDef zone) {
        boolean keeps = player.isCreative() || player.isSpectator();
        if (!player.capabilities.isFlying && player.capabilities.allowFlying == keeps) { return; }
        player.capabilities.isFlying = false;
        player.capabilities.allowFlying = keeps;
        player.sendPlayerAbilities();
        ZoneGuard.tell(player, zone);
    }
}
