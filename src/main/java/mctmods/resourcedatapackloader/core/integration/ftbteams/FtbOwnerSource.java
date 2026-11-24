package mctmods.resourcedatapackloader.core.integration.ftbteams;

import mctmods.resourcedatapackloader.common.ownership.OwnerRecord;
import mctmods.resourcedatapackloader.common.ownership.OwnerSource;
import mctmods.resourcedatapackloader.common.ownership.OwnerTable;

import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public final class FtbOwnerSource implements OwnerSource {
    @Override public int teamOf(ServerPlayer player) {
        String key = FtbPartyTeams.partyKeyOf(player.getUUID());
        return handleOf(player.getServer(), key);
    }

    private static int handleOf(MinecraftServer server, String key) {
        return key.isEmpty() ? 0 : OwnerTable.handleForSource(server, OwnerRecord.SOURCE_FTB, key, FtbPartyTeams.partyLabel(key));
    }

    @Override public boolean allies(ServerPlayer player, int handle) { return FtbPartyTeams.allies(player.getUUID(), OwnerTable.get(player.server).keyOf(handle)); }

    @Override public List<String> memberNames(MinecraftServer server, int handle) { return FtbPartyTeams.memberNames(server, OwnerTable.get(server).keyOf(handle)); }

    @Override public int byName(MinecraftServer server, String name) {
        return handleOf(server, FtbPartyTeams.keyNamed(name));
    }

    @Override public List<String> names(MinecraftServer server) { return FtbPartyTeams.partyNames(); }
}
