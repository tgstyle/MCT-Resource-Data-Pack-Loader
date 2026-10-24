package mctmods.resourcedatapackloader.util;

import mctmods.resourcedatapackloader.content.ContentDiet;

import com.google.gson.JsonObject;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import javax.annotation.Nullable;

public final class Standing {
    public static final Standing NONE = new Standing("", "", null, 0);
    private final String team;
    private final String objective;
    @Nullable private final Integer scoreAtLeast;
    private final int dietAtLeast;
    private static boolean dietWaiting;

    private Standing(String team, String objective, @Nullable Integer scoreAtLeast, int dietAtLeast) {
        this.team = team;
        this.objective = objective;
        this.scoreAtLeast = scoreAtLeast;
        this.dietAtLeast = dietAtLeast;
    }

    public static Standing parse(JsonObject json) {
        Integer least = json.has("scoreAtLeast") ? Json.getInt(json, "scoreAtLeast") : null;
        int diet = json.has("dietAtLeast") ? Math.max(1, Json.getInt(json, "dietAtLeast")) : 0;
        if (diet > 0 && !ContentDiet.loaded()) {
            if (ContentDiet.ran()) { reportNoDiet(); }
            else { dietWaiting = true; }
        }
        return new Standing(Json.getString(json, "team", "").trim(), Json.getString(json, "objective", "").trim(), least, diet);
    }

    public static void settleDiet() {
        if (dietWaiting && !ContentDiet.loaded()) { reportNoDiet(); }
        dietWaiting = false;
    }

    private static void reportNoDiet() { ContentLog.LOGGER.error("A dietAtLeast condition is set, but no diet file is loaded, so it is never met"); }

    public boolean met(ServerPlayer player) {
        if (!team.isEmpty() && !team.equals(teamOf(player))) { return false; }
        if (!objective.isEmpty() && scoreAtLeast != null) {
            Integer score = score(player, objective);
            if (score == null || score < scoreAtLeast) { return false; }
        }
        return dietAtLeast == 0 || ContentDiet.distinct(player) >= dietAtLeast;
    }

    public boolean halfScore() { return objective.isEmpty() != (scoreAtLeast == null); }

    public Standing withoutScore() { return new Standing(team, "", null, dietAtLeast); }

    public boolean any() { return !team.isEmpty() || !objective.isEmpty() && scoreAtLeast != null || dietAtLeast > 0; }

    public boolean earnedBy(ServerPlayer player) { return any() && met(player); }

    public static String teamOf(ServerPlayer player) {
        PlayerTeam held = Scores.teamOf(player);
        return held == null ? "" : held.getName();
    }

    @Nullable public static Integer score(ServerPlayer player, String objective) {
        Scoreboard board = player.level().getScoreboard();
        Objective found = Scores.objective(board, objective);
        String owner = player.getScoreboardName();
        if (found == null || Scores.lacks(board, owner, found)) { return null; }
        return Scores.score(board, owner, found);
    }
}
