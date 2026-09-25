package mctmods.resourcedatapackloader.content.card;

import mctmods.resourcedatapackloader.util.Advancements;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.scoreboard.ScorePlayerTeam;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;

final class CardWhen {
    List<String> biomes = Collections.emptyList();
    int timeFrom = -1;
    int timeTo = -1;
    long dayAtLeast = -1L;
    String advancement = "";
    String gameMode = "";
    String team = "";
    String objective = "";
    @Nullable Integer scoreAtLeast;

    boolean passes(EntityPlayerMP player) {
        if (!biomes.isEmpty() && !CardPlace.biomeMatches(biomes, player.world.getBiome(player.getPosition()))) { return false; }
        if (timeFrom >= 0 && timeTo >= 0 && !within(CardPlace.timeOfDay(player.world))) { return false; }
        if (dayAtLeast >= 0L && CardPlace.day(player.world) < dayAtLeast) { return false; }
        if (!advancement.isEmpty() && !Advancements.has(player, advancement)) { return false; }
        if (!gameMode.isEmpty() && !gameMode.equalsIgnoreCase(player.interactionManager.getGameType().getName())) { return false; }
        if (!team.isEmpty() && !team.equals(teamOf(player))) { return false; }
        if (objective.isEmpty() || scoreAtLeast == null) { return true; }
        Integer score = CardPlace.score(player, objective);
        return score != null && score >= scoreAtLeast;
    }

    private boolean within(int time) { return timeFrom <= timeTo ? time >= timeFrom && time <= timeTo : time >= timeFrom || time <= timeTo; }

    static String teamOf(EntityPlayerMP player) {
        ScorePlayerTeam team = player.world.getScoreboard().getPlayersTeam(player.getName());
        return team == null ? "" : team.getName();
    }
}
