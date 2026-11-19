package mctmods.resourcedatapackloader.common.zones;

import mctmods.resourcedatapackloader.common.pack.definitions.zones.ZoneDef;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Abilities;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

public final class ZoneFly {
    private static final int EVERY = 20;

    private ZoneFly() {}

    public static void onTick(ServerTickEvent.Post event) {
        MinecraftServer server = event.getServer();
        if (server.getTickCount() % EVERY != 0) { return; }
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (ZoneRules.none(player.level())) { return; }
            ZoneDef zone = ZoneRules.covering(player, player.blockPosition(), ZoneDef::noFly);
            if (zone != null) { ground(player, zone); }
        }
    }

    private static void ground(ServerPlayer player, ZoneDef zone) {
        boolean keeps = player.isCreative() || player.isSpectator();
        Abilities.Packed now = player.getAbilities().pack();
        if (!now.flying() && now.mayFly() == keeps) { return; }
        player.getAbilities().apply(new Abilities.Packed(now.invulnerable(), false, keeps, now.instabuild(), now.mayBuild(), now.flyingSpeed(), now.walkingSpeed()));
        player.onUpdateAbilities();
        ZoneGuard.tell(player, zone);
    }
}
