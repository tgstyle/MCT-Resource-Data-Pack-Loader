package mctmods.resourcedatapackloader.core.integration.ftblib;

import mctmods.resourcedatapackloader.common.ownership.OwnerRecord;
import mctmods.resourcedatapackloader.common.ownership.OwnershipSettings;
import mctmods.resourcedatapackloader.common.ownership.ScoreboardOwnerSource;
import mctmods.resourcedatapackloader.common.ownership.TeamDissolve;
import mctmods.resourcedatapackloader.core.integration.CompatHooks;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import net.minecraftforge.common.MinecraftForge;

public final class ContentFtbLib {
    private static boolean installed;

    private ContentFtbLib() {}

    public static void install() {
        if (!CompatHooks.present("ftblib", "FTBLib is loaded, so a pack can take its owners from FTB parties with ownership teamSource ftb")) { return; }
        installed = true;
        TeamDissolve.register(OwnerRecord.SOURCE_FTB, (server, key) -> FtbLibTeams.live(key));
        MinecraftForge.EVENT_BUS.register(FtbLibTeamDeleted.class);
    }

    public static void serverStarting() {
        boolean wanted = OwnershipSettings.teamSource() == OwnershipSettings.TeamSource.FTB;
        if (!installed) {
            if (wanted) { ContentLog.LOGGER.info("ownership teamSource is ftb but FTBLib is not loaded, so owners come from scoreboard teams"); }
            return;
        }
        ScoreboardOwnerSource.use(wanted ? new FtbLibOwnerSource() : null);
    }
}
