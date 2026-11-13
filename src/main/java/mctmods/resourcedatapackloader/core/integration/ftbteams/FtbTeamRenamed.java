package mctmods.resourcedatapackloader.core.integration.ftbteams;

import mctmods.resourcedatapackloader.common.ownership.OwnerRecord;
import mctmods.resourcedatapackloader.common.ownership.OwnerTable;
import mctmods.resourcedatapackloader.common.ownership.OwnershipSettings;

import dev.ftb.mods.ftbteams.api.Team;
import dev.ftb.mods.ftbteams.api.neoforge.FTBTeamsEvent;
import net.minecraft.server.MinecraftServer;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public final class FtbTeamRenamed {
    private FtbTeamRenamed() {}

    public static void onChanged(FTBTeamsEvent.TeamPropertiesChanged event) {
        Team team = event.getEventData().team();
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null || event.getEventData().isClient() || OwnershipSettings.teamSource() != OwnershipSettings.TeamSource.FTB || !team.isPartyTeam()) { return; }
        String key = FtbPartyTeams.keyOf(team);
        if (OwnerTable.get(server).liveTeamHandle(OwnerRecord.SOURCE_FTB, key) != 0) { OwnerTable.handleForSource(server, OwnerRecord.SOURCE_FTB, key, team.getName().getString()); }
    }
}
