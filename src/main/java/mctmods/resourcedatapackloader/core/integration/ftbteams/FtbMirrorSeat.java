package mctmods.resourcedatapackloader.core.integration.ftbteams;

import mctmods.resourcedatapackloader.common.ownership.OwnershipSettings;
import mctmods.resourcedatapackloader.common.scoring.ContentTeams;
import mctmods.resourcedatapackloader.common.util.entity.Scores;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public final class FtbMirrorSeat {
    private static final Map<UUID, String> BEFORE = new HashMap<>();

    private FtbMirrorSeat() {}

    public static void beforeFtb(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || !OwnershipSettings.packWinsMirror() || !ContentTeams.any()) { return; }
        PlayerTeam held = ContentTeams.teamOf(player.level().getServer(), player.getGameProfile().name());
        if (held != null && ContentTeams.named(held.getName()) != null) { BEFORE.put(player.getUUID(), held.getName()); }
    }

    public static void afterFtb(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) { return; }
        String name = BEFORE.remove(player.getUUID());
        if (name == null) { return; }
        MinecraftServer server = player.level().getServer();
        Scoreboard board = Scores.board(server);
        PlayerTeam team = Scores.team(board, name);
        PlayerTeam held = ContentTeams.teamOf(server, player.getGameProfile().name());
        if (team != null && (held == null || !held.getName().equals(name))) { Scores.join(board, player.getGameProfile().name(), team); }
    }
}
