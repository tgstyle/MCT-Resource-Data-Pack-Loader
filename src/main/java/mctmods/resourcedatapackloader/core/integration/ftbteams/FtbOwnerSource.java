package mctmods.resourcedatapackloader.core.integration.ftbteams;

import mctmods.resourcedatapackloader.common.ownership.OwnerRecord;
import mctmods.resourcedatapackloader.common.ownership.OwnerSource;
import mctmods.resourcedatapackloader.common.ownership.OwnerTable;

import net.minecraft.server.level.ServerPlayer;

public final class FtbOwnerSource implements OwnerSource {
    @Override public int teamOf(ServerPlayer player) {
        String key = FtbPartyTeams.partyKeyOf(player.getUUID());
        return key.isEmpty() ? 0 : OwnerTable.handleForSource(player.getServer(), OwnerRecord.SOURCE_FTB, key, FtbPartyTeams.partyLabel(key));
    }
}
