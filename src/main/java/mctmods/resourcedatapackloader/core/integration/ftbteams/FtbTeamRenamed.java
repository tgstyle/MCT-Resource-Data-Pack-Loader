package mctmods.resourcedatapackloader.core.integration.ftbteams;

import mctmods.resourcedatapackloader.common.ownership.OwnerRecord;
import mctmods.resourcedatapackloader.common.ownership.OwnerTable;
import mctmods.resourcedatapackloader.common.ownership.OwnershipSettings;

import dev.ftb.mods.ftbteams.api.event.TeamEvent;
import net.minecraft.server.MinecraftServer;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

final class FtbTeamRenamed {
    private FtbTeamRenamed() {}

    static void register() { TeamEvent.PROPERTIES_CHANGED.register(FtbTeamRenamed::onChanged); }

    private static void onChanged(TeamEvent event) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null || OwnershipSettings.teamSource() != OwnershipSettings.TeamSource.FTB || !event.getTeam().isPartyTeam()) { return; }
        OwnerTable.get(server).relabelSource(OwnerRecord.SOURCE_FTB, FtbPartyTeams.keyOf(event.getTeam()), event.getTeam().getName().getString());
    }
}
