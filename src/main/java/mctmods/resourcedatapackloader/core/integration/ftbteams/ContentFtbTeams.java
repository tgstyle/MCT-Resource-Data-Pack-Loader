package mctmods.resourcedatapackloader.core.integration.ftbteams;

import mctmods.resourcedatapackloader.common.ownership.OwnerRecord;
import mctmods.resourcedatapackloader.common.ownership.OwnershipSettings;
import mctmods.resourcedatapackloader.common.ownership.ScoreboardOwnerSource;
import mctmods.resourcedatapackloader.common.ownership.TeamDissolve;
import mctmods.resourcedatapackloader.common.pack.definitions.PackRequirements;
import mctmods.resourcedatapackloader.core.util.ContentLog;

public final class ContentFtbTeams {
    public static final String MOD_ID = "ftbteams";
    private static boolean installed;

    private ContentFtbTeams() {}

    public static void install() {
        if (!PackRequirements.modLoaded(MOD_ID)) { return; }
        ContentLog.LOGGER.info("FTB Teams is loaded, so a pack can take its owners from FTB parties with ownership teamSource ftb");
        installed = true;
        TeamDissolve.register(OwnerRecord.SOURCE_FTB, (server, key) -> FtbPartyTeams.live(key));
        FtbTeamDeleted.register();
    }

    public static void serverStarting() {
        boolean wanted = OwnershipSettings.teamSource() == OwnershipSettings.TeamSource.FTB;
        if (!installed) {
            if (wanted) { ContentLog.LOGGER.info("ownership teamSource is ftb but FTB Teams is not loaded, so owners come from scoreboard teams"); }
            return;
        }
        ScoreboardOwnerSource.use(wanted ? new FtbOwnerSource() : null);
    }
}
