package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.def.ScoreDef;
import mctmods.resourcedatapackloader.util.BossBars;

import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.BossInfoServer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

final class ContentScoringBar {
    private static final Map<String, BossInfoServer> BARS = new HashMap<>();

    private ContentScoringBar() {}

    static void second(MinecraftServer server, Collection<ScoreDef> defs, Set<String> finished, int ticks) {
        for (ScoreDef def : defs) {
            if (def.bossBar == null || finished.contains(def.name)) { continue; }
            BossInfoServer bar = BARS.computeIfAbsent(def.name, name -> BossBars.create(def.bossBar, new TextComponentString(def.displayName)));
            BossBars.progress(bar, progress(server, def, ticks));
            BossBars.sync(bar, new ArrayList<>(server.getPlayerList().getPlayers()));
        }
    }

    static void over(ScoreDef def) {
        BossInfoServer bar = BARS.remove(def.name);
        if (bar != null) { BossBars.clear(bar); }
    }

    static void clear() {
        for (BossInfoServer bar : BARS.values()) { BossBars.clear(bar); }
        BARS.clear();
    }

    private static float progress(MinecraftServer server, ScoreDef def, int ticks) {
        if (def.endsAfterMinutes > 0) { return 1.0F - ticks / (float) (def.endsAfterMinutes * ContentScoring.MINUTE_TICKS); }
        if (def.endsAtScore <= 0) { return 1.0F; }
        Scoreboard board = ContentTeams.board(server);
        ScoreObjective objective = board.getObjective(def.name);
        int best = 0;
        if (objective != null) {
            for (Score score : board.getSortedScores(objective)) { best = Math.max(best, score.getScorePoints()); }
        }
        return best / (float) def.endsAtScore;
    }
}
