package mctmods.resourcedatapackloader.common.zones;

import mctmods.resourcedatapackloader.common.pack.definitions.zones.ZoneDef;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Abilities;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

public final class ZoneFly {
    private static final int EVERY = 20;

    private ZoneFly() {}

    @SubscribeEvent public static void onTick(ServerTickEvent.Post event) {
        if (event.getServer().getTickCount() % EVERY != 0) { return; }
        for (ServerPlayer player : event.getServer().getPlayerList().getPlayers()) {
            if (ZoneRules.none(player.level())) { return; }
            ZoneDef zone = ZoneRules.covering(player, player.blockPosition(), ZoneDef::noFly);
            if (zone != null) { ground(player, zone); }
        }
    }

    @SuppressWarnings("deprecation") private static void ground(ServerPlayer player, ZoneDef zone) {
        boolean keeps = player.isCreative() || player.isSpectator();
        Abilities abilities = player.getAbilities();
        if (!abilities.flying && abilities.mayfly == keeps) { return; }
        abilities.flying = false;
        abilities.mayfly = keeps;
        player.onUpdateAbilities();
        ZoneGuard.tell(player, zone);
    }
}
