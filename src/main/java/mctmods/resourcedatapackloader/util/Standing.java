package mctmods.resourcedatapackloader.util;

import com.google.gson.JsonObject;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import javax.annotation.Nullable;

public final class Standing {
    public static final Standing NONE = new Standing("", "", null);
    private final String team;
    private final String objective;
    @Nullable private final Integer scoreAtLeast;

    private Standing(String team, String objective, @Nullable Integer scoreAtLeast) {
        this.team = team;
        this.objective = objective;
        this.scoreAtLeast = scoreAtLeast;
    }

    public static Standing parse(JsonObject json) {
        Integer least = json.has("scoreAtLeast") ? Json.getInt(json, "scoreAtLeast") : null;
        return new Standing(Json.getString(json, "team", "").trim(), Json.getString(json, "objective", "").trim(), least);
    }

    public boolean met(ServerPlayer player) {
        if (!team.isEmpty() && !team.equals(teamOf(player))) { return false; }
        if (objective.isEmpty() || scoreAtLeast == null) { return true; }
        Integer score = score(player, objective);
        return score != null && score >= scoreAtLeast;
    }

    public boolean any() { return !team.isEmpty() || !objective.isEmpty() && scoreAtLeast != null; }

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
