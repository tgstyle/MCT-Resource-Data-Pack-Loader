package mctmods.resourcedatapackloader.common.ownership;

import mctmods.resourcedatapackloader.common.scoring.ContentTeams;
import mctmods.resourcedatapackloader.common.util.entity.Standing;

import java.util.Collection;
import java.util.Collections;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;

public final class ScoreboardOwnerSource implements OwnerSource {
    public static final ScoreboardOwnerSource INSTANCE = new ScoreboardOwnerSource();
    private static volatile OwnerSource current = INSTANCE;

    private ScoreboardOwnerSource() {}

    public static OwnerSource current() { return current; }

    public static OwnerSource forSource(String source) { return OwnerRecord.SOURCE_FTB.equals(source) ? current : INSTANCE; }

    public static OwnerSource of(@Nullable MinecraftServer server, int handle) {
        OwnerTable table = OwnerTable.get(server);
        return forSource(table == null ? "" : table.sourceOf(table.resolve(handle)));
    }

    public static void use(OwnerSource source) { current = source == null ? INSTANCE : source; }

    @Override public int teamOf(EntityPlayer player) {
        OwnerTable table = OwnerTable.get(player.getServer());
        if (table == null) { return 0; }
        int handle = OwnerTable.handleForTeam(player.getServer(), Standing.teamOf(player));
        return table.isLive(handle) ? handle : 0;
    }

    @Override public Collection<String> memberNames(@Nullable MinecraftServer server, int handle) {
        OwnerTable table = OwnerTable.get(server);
        if (server == null || table == null || !table.isLive(handle)) { return Collections.emptyList(); }
        if (!table.isTeam(handle)) { return Collections.singletonList(table.labelOf(handle)); }
        Scoreboard board = ContentTeams.board(server);
        String key = table.keyOf(handle);
        return board.getTeamNames().contains(key) ? board.getTeam(key).getMembershipCollection() : Collections.emptyList();
    }

    @Override public int byName(@Nullable MinecraftServer server, String name) {
        return server != null && ContentTeams.board(server).getTeamNames().contains(name) ? OwnerTable.handleForTeam(server, name) : 0;
    }

    @Override public Collection<String> names(@Nullable MinecraftServer server) { return server == null ? Collections.emptyList() : ContentTeams.board(server).getTeamNames(); }
}
