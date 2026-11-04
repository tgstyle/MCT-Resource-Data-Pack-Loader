package mctmods.resourcedatapackloader.core.integration.ftbteams;

import mctmods.resourcedatapackloader.common.ownership.OwnerRecord;
import mctmods.resourcedatapackloader.common.ownership.OwnershipSettings;
import mctmods.resourcedatapackloader.common.ownership.TeamDissolve;

import dev.ftb.mods.ftbteams.api.event.TeamEvent;
import net.minecraft.server.MinecraftServer;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

final class FtbTeamDeleted {
    private FtbTeamDeleted() {}

    static void register() { TeamEvent.DELETED.register(FtbTeamDeleted::onDeleted); }

    private static void onDeleted(TeamEvent event) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null || OwnershipSettings.teamSource() != OwnershipSettings.TeamSource.FTB || !event.getTeam().isPartyTeam()) { return; }
        TeamDissolve.dissolveForeign(server, OwnerRecord.SOURCE_FTB, FtbPartyTeams.keyOf(event.getTeam()));
    }
}
