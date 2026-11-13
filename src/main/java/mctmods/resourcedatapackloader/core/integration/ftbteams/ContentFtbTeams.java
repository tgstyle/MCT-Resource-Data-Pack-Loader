package mctmods.resourcedatapackloader.core.integration.ftbteams;

import mctmods.resourcedatapackloader.common.ownership.OwnerRecord;
import mctmods.resourcedatapackloader.common.ownership.OwnerTable;
import mctmods.resourcedatapackloader.common.ownership.OwnershipSettings;
import mctmods.resourcedatapackloader.common.ownership.ScoreboardOwnerSource;
import mctmods.resourcedatapackloader.common.ownership.TeamDissolve;
import mctmods.resourcedatapackloader.common.pack.definitions.PackRequirements;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import dev.ftb.mods.ftbteams.api.event.TeamEvent;
import dev.ftb.mods.ftbteams.api.event.TeamPropertiesChangedEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.ServerLifecycleHooks;

public final class ContentFtbTeams {
    public static final String MOD_ID = "ftbteams";
    private static final FtbOwnerSource SOURCE = new FtbOwnerSource();
    private static boolean installed;

    private ContentFtbTeams() {}

    public static void install() {
        if (!PackRequirements.modLoaded(MOD_ID)) { return; }
        installed = true;
        ContentLog.LOGGER.info("FTB Teams is loaded, so a pack can take its owners from FTB parties with ownership teamSource ftb");
        TeamDissolve.register(OwnerRecord.SOURCE_FTB, (server, key) -> FtbPartyTeams.live(key));
        FtbTeamDeleted.register();
        ScoreboardOwnerSource.register(OwnerRecord.SOURCE_FTB, SOURCE);
        TeamEvent.PROPERTIES_CHANGED.register(ContentFtbTeams::onPropertiesChanged);
    }

    private static void onPropertiesChanged(TeamPropertiesChangedEvent event) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null || !event.getTeam().isPartyTeam()) { return; }
        String key = FtbPartyTeams.keyOf(event.getTeam());
        OwnerTable.get(server).relabelSource(OwnerRecord.SOURCE_FTB, key, FtbPartyTeams.partyLabel(key));
    }

    public static void serverStarting() {
        boolean wanted = OwnershipSettings.teamSource() == OwnershipSettings.TeamSource.FTB;
        if (!installed) {
            if (wanted) { ContentLog.LOGGER.info("ownership teamSource is ftb but FTB Teams is not loaded, so owners come from scoreboard teams"); }
            return;
        }
        ScoreboardOwnerSource.use(wanted ? SOURCE : null);
    }
}
