package mctmods.resourcedatapackloader.core.integration.ftbteams;

import mctmods.resourcedatapackloader.common.ownership.OwnerRecord;
import mctmods.resourcedatapackloader.common.ownership.OwnerSource;
import mctmods.resourcedatapackloader.common.ownership.OwnerTable;

import mctmods.resourcedatapackloader.common.ownership.ScoreboardOwnerSource;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public final class FtbOwnerSource implements OwnerSource {
    private static String partyKey(MinecraftServer server, int handle) {
        OwnerTable table = OwnerTable.get(server);
        int live = table.resolve(handle);
        return live != 0 && table.isTeam(live) && OwnerRecord.SOURCE_FTB.equals(table.sourceOf(live)) ? table.keyOf(live) : "";
    }

    @Override public boolean allies(ServerPlayer player, int handle) {
        String key = partyKey(player.server, handle);
        return !key.isEmpty() && FtbPartyTeams.allies(player.getUUID(), key);
    }

    @Override public List<String> memberNames(MinecraftServer server, int handle) {
        String key = partyKey(server, handle);
        return key.isEmpty() ? ScoreboardOwnerSource.INSTANCE.memberNames(server, handle) : FtbPartyTeams.memberNames(server, key);
    }

    @Override public int byName(MinecraftServer server, String name) {
        int scoreboard = ScoreboardOwnerSource.INSTANCE.byName(server, name);
        if (scoreboard != 0) { return scoreboard; }
        return FtbPartyTeams.partyNamed(name).map(team -> OwnerTable.handleForSource(server, OwnerRecord.SOURCE_FTB, FtbPartyTeams.keyOf(team), team.getName().getString())).orElse(0);
    }

    @Override public List<String> names(MinecraftServer server) {
        List<String> names = new ArrayList<>(ScoreboardOwnerSource.INSTANCE.names(server));
        names.addAll(FtbPartyTeams.partyNames());
        return names;
    }

    @Override public int teamOf(ServerPlayer player) {
        String key = FtbPartyTeams.partyKeyOf(player.getUUID());
        return key.isEmpty() ? 0 : OwnerTable.handleForSource(player.server, OwnerRecord.SOURCE_FTB, key, FtbPartyTeams.partyLabel(key));
    }
}
