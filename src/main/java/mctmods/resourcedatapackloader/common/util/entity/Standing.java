package mctmods.resourcedatapackloader.common.util.entity;

import mctmods.resourcedatapackloader.common.diet.ContentDiet;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;

public final class Standing {
    public static final Standing NONE = new Standing("", "", null, 0, false);
    private final String team;
    private final String objective;
    @Nullable private final Integer scoreAtLeast;
    private final int dietAtLeast;
    private final boolean lifetime;

    private Standing(String team, String objective, @Nullable Integer scoreAtLeast, int dietAtLeast, boolean lifetime) {
        this.team = team;
        this.objective = objective;
        this.scoreAtLeast = scoreAtLeast;
        this.dietAtLeast = dietAtLeast;
        this.lifetime = lifetime;
    }

    public static Standing parse(JsonObject json) {
        Integer least = json.has("scoreAtLeast") ? Json.getInt(json, "scoreAtLeast") : null;
        int diet = json.has("dietAtLeast") ? Math.max(1, Json.getInt(json, "dietAtLeast")) : 0;
        if (diet > 0 && !ContentDiet.loaded()) { ContentLog.LOGGER.error("A dietAtLeast condition is set, but no diet file is loaded, so it is never met"); }
        return new Standing(Json.getString(json, "team", "").trim(), Json.getString(json, "objective", "").trim(), least, diet, diet > 0 && ContentDiet.lifetime(json, "A dietAtLeast condition"));
    }

    public boolean met(EntityPlayer player) {
        if (!team.isEmpty() && !team.equals(teamOf(player))) { return false; }
        if (!objective.isEmpty() && scoreAtLeast != null) {
            Integer score = score(player, objective);
            if (score == null || score < scoreAtLeast) { return false; }
        }
        return dietAtLeast == 0 || (lifetime ? ContentDiet.eatenEver(player) : ContentDiet.distinct(player)) >= dietAtLeast;
    }

    public boolean halfScore() { return objective.isEmpty() != (scoreAtLeast == null); }

    public Standing withoutScore() { return new Standing(team, "", null, dietAtLeast, lifetime); }

    public boolean any() { return !team.isEmpty() || !objective.isEmpty() && scoreAtLeast != null || dietAtLeast > 0; }

    public boolean unreadable(EntityPlayer player) {
        Scoreboard board = player.world.getScoreboard();
        return !team.isEmpty() && !board.getTeamNames().contains(team) || !objective.isEmpty() && board.getObjective(objective) == null;
    }

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
