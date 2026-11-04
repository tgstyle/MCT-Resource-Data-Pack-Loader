package mctmods.resourcedatapackloader.core.integration.ftbteams;

import mctmods.resourcedatapackloader.common.ownership.OwnerRecord;
import mctmods.resourcedatapackloader.common.ownership.OwnershipSettings;
import mctmods.resourcedatapackloader.common.ownership.TeamDissolve;

import dev.ftb.mods.ftbteams.api.event.TeamEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.ServerLifecycleHooks;

public final class FtbTeamDeleted {
    private FtbTeamDeleted() {}

    public static void register() { TeamEvent.DELETED.register(FtbTeamDeleted::onDeleted); }

    private static void onDeleted(TeamEvent event) {
        if (OwnershipSettings.teamSource() != OwnershipSettings.TeamSource.FTB || !event.getTeam().isPartyTeam()) { return; }
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) { TeamDissolve.dissolveForeign(server, OwnerRecord.SOURCE_FTB, FtbPartyTeams.keyOf(event.getTeam())); }
    }
}
