package mctmods.resourcedatapackloader.core.integration.ftbteams;

import mctmods.resourcedatapackloader.common.ownership.OwnerRecord;
import mctmods.resourcedatapackloader.common.ownership.OwnershipSettings;
import mctmods.resourcedatapackloader.common.ownership.TeamDissolve;

import dev.ftb.mods.ftbteams.api.Team;
import dev.ftb.mods.ftbteams.api.neoforge.FTBTeamsEvent;
import net.minecraft.server.MinecraftServer;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public final class FtbTeamDeleted {
    private FtbTeamDeleted() {}

    public static void onDeleted(FTBTeamsEvent.TeamDeleted event) {
        Team team = event.getEventData().team();
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null || OwnershipSettings.teamSource() != OwnershipSettings.TeamSource.FTB || !team.isPartyTeam()) { return; }
        TeamDissolve.dissolveForeign(server, OwnerRecord.SOURCE_FTB, FtbPartyTeams.keyOf(team));
    }
}
