package mctmods.resourcedatapackloader.common.ownership;

import mctmods.resourcedatapackloader.common.util.entity.Scores;
import mctmods.resourcedatapackloader.common.util.entity.Standing;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.NameAndId;
import net.minecraft.world.scores.PlayerTeam;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public final class ScoreboardOwnerSource implements OwnerSource {
    public static final ScoreboardOwnerSource INSTANCE = new ScoreboardOwnerSource();
    private static volatile OwnerSource current = INSTANCE;

    private ScoreboardOwnerSource() {}

    public static OwnerSource current() { return current; }

    public static void use(OwnerSource source) { current = source == null ? INSTANCE : source; }

    public static OwnerSource of(MinecraftServer server, int handle) {
        OwnerTable table = OwnerTable.get(server);
        return OwnerRecord.SOURCE_FTB.equals(table.sourceOf(table.resolve(handle))) ? current : INSTANCE;
    }

    @Override public int teamOf(ServerPlayer player) {
        MinecraftServer server = player.level().getServer();
        int handle = OwnerTable.handleForTeam(server, Standing.teamOf(player));
        return OwnerTable.get(server).isLive(handle) ? handle : 0;
    }

    @Override public List<String> memberNames(int handle) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) { return List.of(); }
        OwnerTable table = OwnerTable.get(server);
        if (!table.isTeam(handle)) { return List.of(table.labelOf(handle)); }
        PlayerTeam team = Scores.team(Scores.board(server), table.keyOf(handle));
        return team == null ? List.of() : new ArrayList<>(Scores.members(team));
    }

    @Override @Nullable public Integer byName(MinecraftServer server, String name) {
        if (Scores.team(Scores.board(server), name) != null) { return OwnerTable.handleForTeam(server, name); }
        NameAndId profile = server.services().nameToIdCache().get(name).orElse(null);
        return profile == null ? null : OwnerTable.handleForPlayer(server, profile.id(), profile.name());
    }

    @Override public List<String> names(MinecraftServer server) {
        List<String> names = new ArrayList<>(Scores.board(server).getTeamNames());
        names.addAll(List.of(server.getPlayerNames()));
        return names;
    }
}
