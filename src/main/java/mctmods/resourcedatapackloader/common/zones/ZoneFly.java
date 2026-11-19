package mctmods.resourcedatapackloader.common.zones;

import mctmods.resourcedatapackloader.common.pack.definitions.zones.ZoneDef;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Abilities;
import net.minecraftforge.event.TickEvent;

public final class ZoneFly {
    private static final int EVERY = 20;

    private ZoneFly() {}

    public static void onTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || ZoneRules.noZones() || event.getServer().getTickCount() % EVERY != 0) { return; }
        for (ServerPlayer player : event.getServer().getPlayerList().getPlayers()) {
            ZoneDef zone = ZoneRules.covering(player, player.blockPosition(), ZoneDef::noFly);
            if (zone != null) { ground(player, zone); }
        }
    }

    private static void ground(ServerPlayer player, ZoneDef zone) {
        Abilities abilities = player.getAbilities();
        boolean keeps = player.isCreative() || player.isSpectator();
        if (!abilities.flying && abilities.mayfly == keeps) { return; }
        abilities.flying = false;
        abilities.mayfly = keeps;
        player.onUpdateAbilities();
        ZoneGuard.tell(player, zone);
    }
}
