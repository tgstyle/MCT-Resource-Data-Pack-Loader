package mctmods.resourcedatapackloader.core.integration.ftbteams;

import mctmods.resourcedatapackloader.common.ownership.OwnerRecord;
import mctmods.resourcedatapackloader.common.ownership.OwnershipSettings;
import mctmods.resourcedatapackloader.common.ownership.ScoreboardOwnerSource;
import mctmods.resourcedatapackloader.common.ownership.TeamDissolve;
import mctmods.resourcedatapackloader.common.pack.definitions.PackRequirements;
import mctmods.resourcedatapackloader.common.scoring.ContentTeams;
import mctmods.resourcedatapackloader.common.util.entity.Scores;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.scores.PlayerTeam;
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
    }

    public static void serverStarting(MinecraftServer server) {
        boolean wanted = OwnershipSettings.teamSource() == OwnershipSettings.TeamSource.FTB;
        if (!installed) {
            if (wanted) { ContentLog.LOGGER.info("ownership teamSource is ftb but FTB Teams is not loaded, so owners come from scoreboard teams"); }
            return;
        }
        ScoreboardOwnerSource.use(wanted ? new FtbOwnerSource() : null);
        warnMirror(server);
    }

    private static void warnMirror(MinecraftServer server) {
        if (!ContentTeams.any()) { return; }
        for (PlayerTeam team : Scores.board(server).getPlayerTeams()) {
            if (team.getName().startsWith(MIRROR_PREFIX)) {
                ContentLog.LOGGER.warn("FTB Teams mirrors parties as scoreboard teams named {}* and this pack defines scoreboard teams, so those mirrored teams never count as owners", MIRROR_PREFIX);
                return;
            }
        }
    }
}
