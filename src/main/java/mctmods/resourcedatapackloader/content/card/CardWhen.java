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

    boolean fails(ServerPlayer player) {
        if (!biomes.isEmpty() && !CardPlace.biomeMatches(biomes, player.level().getBiome(player.blockPosition()))) { return true; }
        if (timeFrom >= 0 && timeTo >= 0 && !within(CardPlace.timeOfDay(player.level()))) { return true; }
        if (dayAtLeast >= 0L && CardPlace.day(player.level()) < dayAtLeast) { return true; }
        if (!advancement.isEmpty() && !Advancements.has(player, advancement)) { return true; }
        if (!gameMode.isEmpty() && !gameMode.equalsIgnoreCase(player.gameMode.getGameModeForPlayer().getName())) { return true; }
        if (!team.isEmpty() && !team.equals(teamOf(player))) { return true; }
        if (objective.isEmpty() || scoreAtLeast == null) { return false; }
        Integer score = CardPlace.score(player, objective);
        return score == null || score < scoreAtLeast;
    }

    private boolean within(int time) { return timeFrom <= timeTo ? time >= timeFrom && time <= timeTo : time >= timeFrom || time <= timeTo; }

    static String teamOf(ServerPlayer player) {
        PlayerTeam team = Scores.teamOf(player);
        return team == null ? "" : team.getName();
    }
}
