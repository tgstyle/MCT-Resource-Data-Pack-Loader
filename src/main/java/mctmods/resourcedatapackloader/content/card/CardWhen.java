package mctmods.resourcedatapackloader.content.card;

import mctmods.resourcedatapackloader.util.Advancements;
import mctmods.resourcedatapackloader.util.Scores;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.PlayerTeam;
import java.util.List;
import javax.annotation.Nullable;

final class CardWhen {
    List<String> biomes = List.of();
    int timeFrom = -1;
    int timeTo = -1;
    long dayAtLeast = -1L;
    String advancement = "";
    String gameMode = "";
    String team = "";
    String objective = "";
    @Nullable Integer scoreAtLeast;

    boolean passes(ServerPlayer player) {
        if (!biomes.isEmpty() && !CardPlace.biomeMatches(biomes, player.level().getBiome(player.blockPosition()))) { return false; }
        if (timeFrom >= 0 && timeTo >= 0 && !within(CardPlace.timeOfDay(player.level()))) { return false; }
        if (dayAtLeast >= 0L && CardPlace.day(player.level()) < dayAtLeast) { return false; }
        if (!advancement.isEmpty() && !Advancements.has(player, advancement)) { return false; }
        if (!gameMode.isEmpty() && !gameMode.equalsIgnoreCase(player.gameMode.getGameModeForPlayer().getName())) { return false; }
        if (!team.isEmpty() && !team.equals(teamOf(player))) { return false; }
        if (objective.isEmpty() || scoreAtLeast == null) { return true; }
        Integer score = CardPlace.score(player, objective);
        return score != null && score >= scoreAtLeast;
    }

    private boolean within(int time) { return timeFrom <= timeTo ? time >= timeFrom && time <= timeTo : time >= timeFrom || time <= timeTo; }

    static String teamOf(ServerPlayer player) {
        PlayerTeam team = Scores.teamOf(player.getScoreboard(), player.getScoreboardName());
        return team == null ? "" : team.getName();
    }
}
