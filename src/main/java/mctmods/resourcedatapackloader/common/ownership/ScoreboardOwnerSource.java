package mctmods.resourcedatapackloader.common.ownership;

import mctmods.resourcedatapackloader.common.util.entity.Scores;
import mctmods.resourcedatapackloader.common.util.entity.Standing;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.PlayerTeam;

public final class ScoreboardOwnerSource implements OwnerSource {
    public static final ScoreboardOwnerSource INSTANCE = new ScoreboardOwnerSource();
    private static volatile OwnerSource current = INSTANCE;

    private ScoreboardOwnerSource() {}

    public static OwnerSource current() { return current; }

    public static void use(OwnerSource source) { current = source == null ? INSTANCE : source; }

    @Override public int teamOf(ServerPlayer player) {
        int handle = OwnerTable.handleForTeam(player.server, Standing.teamOf(player));
        return OwnerTable.get(player.server).isLive(handle) ? handle : 0;
    }

    @Override public List<String> memberNames(MinecraftServer server, int handle) {
        OwnerTable table = OwnerTable.get(server);
        if (!table.isLive(handle)) { return List.of(); }
        if (!table.isTeam(handle)) { return List.of(table.labelOf(handle)); }
        PlayerTeam team = Scores.team(Scores.board(server), table.keyOf(handle));
        return team == null ? List.of() : new ArrayList<>(Scores.members(team));
    }

    @Override public int byName(MinecraftServer server, String name) {
        if (Scores.board(server).getTeamNames().contains(name)) { return OwnerTable.handleForTeam(server, name); }
        return server.getProfileCache() == null ? 0 : server.getProfileCache().get(name).map(profile -> OwnerTable.handleForPlayer(server, profile.getId(), profile.getName())).orElse(0);
    }

    @Override public List<String> names(MinecraftServer server) {
        List<String> names = new ArrayList<>(Scores.board(server).getTeamNames());
        names.addAll(List.of(server.getPlayerNames()));
        return names;
    }
}
