package mctmods.resourcedatapackloader.core.integration.ftbteams;

import mctmods.resourcedatapackloader.common.ownership.OwnerRecord;
import mctmods.resourcedatapackloader.common.ownership.OwnershipSettings;
import mctmods.resourcedatapackloader.common.ownership.ScoreboardOwnerSource;
import mctmods.resourcedatapackloader.common.ownership.TeamDissolve;
import mctmods.resourcedatapackloader.common.pack.definitions.PackRequirements;
import mctmods.resourcedatapackloader.common.scoring.ContentTeams;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import dev.ftb.mods.ftbteams.config.ServerConfig;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.common.NeoForge;

public final class ContentFtbTeams {
    private static final String MOD_ID = "ftbteams";
    private static final String MIRROR_PREFIX = "ftbteams-";
    private static boolean installed;

    private ContentFtbTeams() {}

    public static void install() {
        if (!PackRequirements.modLoaded(MOD_ID)) { return; }
        ContentLog.LOGGER.info("FTB Teams is loaded, so a pack can take its owners from FTB parties with ownership teamSource ftb");
        installed = true;
        TeamDissolve.register(OwnerRecord.SOURCE_FTB, (_, key) -> FtbPartyTeams.live(key));
        NeoForge.EVENT_BUS.addListener(FtbTeamDeleted::onDeleted);
        NeoForge.EVENT_BUS.addListener(FtbTeamRenamed::onChanged);
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGHEST, FtbMirrorSeat::beforeFtb);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, FtbMirrorSeat::afterFtb);
    }

    public static void serverStarting() {
        boolean wanted = OwnershipSettings.teamSource() == OwnershipSettings.TeamSource.FTB;
        if (!installed) {
            if (wanted) { ContentLog.LOGGER.info("ownership teamSource is ftb but FTB Teams is not loaded, so owners come from scoreboard teams"); }
            return;
        }
        ScoreboardOwnerSource.use(wanted ? new FtbOwnerSource() : null);
        warnMirror();
    }

    private static void warnMirror() {
        if (!ContentTeams.any() || !ServerConfig.SCOREBOARD_TEAM_SYNC.get()) { return; }
        ContentLog.LOGGER.warn("FTB Teams scoreboard_team team_sync_enabled is on and this pack defines scoreboard teams, so FTB moves players out of the pack's teams into {}* teams; turn the FTB sync off, or set ftbMirror to packWins to seat players back in their pack team at login", MIRROR_PREFIX);
    }
}
