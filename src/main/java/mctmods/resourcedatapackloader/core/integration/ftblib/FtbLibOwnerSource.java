package mctmods.resourcedatapackloader.core.integration.ftblib;

import mctmods.resourcedatapackloader.common.ownership.OwnerRecord;
import mctmods.resourcedatapackloader.common.ownership.OwnerSource;
import mctmods.resourcedatapackloader.common.ownership.OwnerTable;

import java.util.Collection;
import java.util.Collections;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

public final class FtbLibOwnerSource implements OwnerSource {
    @Override public int teamOf(EntityPlayer player) {
        String key = FtbLibTeams.partyKeyOf(player.getUniqueID());
        return key.isEmpty() ? 0 : OwnerTable.handleForSource(player.getServer(), OwnerRecord.SOURCE_FTB, key, FtbLibTeams.partyLabel(key));
    }

    @Override public boolean allies(EntityPlayer player, int handle) {
        OwnerTable table = OwnerTable.get(player.getServer());
        return table != null && FtbLibTeams.isAlly(player.getUniqueID(), table.keyOf(handle));
    }

    @Override public Collection<String> memberNames(@Nullable MinecraftServer server, int handle) {
        OwnerTable table = OwnerTable.get(server);
        return table == null ? Collections.emptyList() : FtbLibTeams.memberNames(table.keyOf(handle));
    }

    @Override public int byName(@Nullable MinecraftServer server, String name) {
        String key = FtbLibTeams.keyNamed(name);
        return key.isEmpty() ? 0 : OwnerTable.handleForSource(server, OwnerRecord.SOURCE_FTB, key, FtbLibTeams.partyLabel(key));
    }

    @Override public Collection<String> names(@Nullable MinecraftServer server) { return FtbLibTeams.partyIds(); }
}
