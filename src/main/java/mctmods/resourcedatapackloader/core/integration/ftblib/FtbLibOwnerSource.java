package mctmods.resourcedatapackloader.core.integration.ftblib;

import mctmods.resourcedatapackloader.common.ownership.OwnerRecord;
import mctmods.resourcedatapackloader.common.ownership.OwnerSource;
import mctmods.resourcedatapackloader.common.ownership.OwnerTable;

import net.minecraft.entity.player.EntityPlayer;

public final class FtbLibOwnerSource implements OwnerSource {
    @Override public int teamOf(EntityPlayer player) {
        String key = FtbLibTeams.partyKeyOf(player.getUniqueID());
        return key.isEmpty() ? 0 : OwnerTable.handleForSource(player.getServer(), OwnerRecord.SOURCE_FTB, key, FtbLibTeams.partyLabel(key));
    }
}
