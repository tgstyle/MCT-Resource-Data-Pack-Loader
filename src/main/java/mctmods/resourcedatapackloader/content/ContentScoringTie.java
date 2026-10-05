package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.def.ScoreDef;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;

public final class ContentScoringTie {
    private ContentScoringTie() {}

    public static List<Score> top(Collection<Score> scores) {
        List<Score> top = new ArrayList<>();
        for (Score one : scores) {
            if (!top.isEmpty() && one.getScorePoints() < top.get(0).getScorePoints()) { continue; }
            if (!top.isEmpty() && one.getScorePoints() > top.get(0).getScorePoints()) { top.clear(); }
            top.add(one);
        }
        return top;
    }

    public static List<String> names(List<Score> scores) {
        List<String> names = new ArrayList<>();
        for (Score one : scores) { names.add(one.getPlayerName()); }
        return names;
    }

    @Nullable static Score best(MinecraftServer server, ScoreDef def, Collection<Score> scores) {
        List<Score> top = top(scores);
        if (top.size() < 2) { return top.isEmpty() ? null : top.get(0); }
        return def.tiebreak ? draw(server, def, top) : null;
    }

    static void match(MinecraftServer server, ScoreDef def, List<String> lines) {
        Scoreboard board = server.getWorld(0).getScoreboard();
        ScoreObjective objective = board.getObjective(def.name);
        List<Score> top = objective == null ? new ArrayList<>() : top(board.getSortedScores(objective));
        if (top.size() > 1) { lines.add(0, ContentTurns.shown(draw(server, def, top).getPlayerName()) + " won the tiebreak"); }
    }

    private static Score draw(MinecraftServer server, ScoreDef def, List<Score> top) {
        Score drawn = top.get(server.getWorld(0).rand.nextInt(top.size()));
        ContentLog.LOGGER.info("The {} round ended level at {} between {}, so the tiebreak drew {}", def.displayName, drawn.getScorePoints(), names(top), drawn.getPlayerName());
        return drawn;
    }
}
