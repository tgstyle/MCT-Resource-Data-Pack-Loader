package mctmods.resourcedatapackloader.util;

import com.google.gson.JsonObject;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
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

    public boolean met(EntityPlayer player) {
        if (!team.isEmpty() && !team.equals(teamOf(player))) { return false; }
        if (objective.isEmpty() || scoreAtLeast == null) { return true; }
        Integer score = score(player, objective);
        return score != null && score >= scoreAtLeast;
    }

    public boolean any() { return !team.isEmpty() || !objective.isEmpty() && scoreAtLeast != null; }

    public boolean earnedBy(EntityPlayer player) { return any() && met(player); }

    public static String teamOf(EntityPlayer player) {
        ScorePlayerTeam held = player.world.getScoreboard().getPlayersTeam(player.getName());
        return held == null ? "" : held.getName();
    }

    @Nullable public static Integer score(EntityPlayer player, String objective) {
        Scoreboard board = player.world.getScoreboard();
        ScoreObjective found = board.getObjective(objective);
        if (found == null || !board.entityHasObjective(player.getName(), found)) { return null; }
        return board.getOrCreateScore(player.getName(), found).getScorePoints();
    }
}
