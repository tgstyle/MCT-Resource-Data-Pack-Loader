package mctmods.resourcedatapackloader.util;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;

public final class Scores {
    private Scores() {}

    public record Row(String owner, int value) {}

    public static Scoreboard board(MinecraftServer server) { return server.getScoreboard(); }

    @Nullable public static PlayerTeam team(Scoreboard board, String name) { return board.getPlayerTeam(name); }

    public static PlayerTeam addTeam(Scoreboard board, String name) { return board.addPlayerTeam(name); }

    public static void removeTeam(Scoreboard board, PlayerTeam team) { board.removePlayerTeam(team); }

    @Nullable public static PlayerTeam teamOf(Scoreboard board, String member) { return board.getPlayersTeam(member); }

    public static void join(Scoreboard board, String member, PlayerTeam team) { board.addPlayerToTeam(member, team); }

    public static boolean leave(Scoreboard board, String member) { return board.removePlayerFromTeam(member); }

    public static Collection<String> members(PlayerTeam team) { return team.getPlayers(); }

    @Nullable public static Objective objective(Scoreboard board, String name) { return board.getObjective(name); }

    public static Objective addObjective(Scoreboard board, String name, ObjectiveCriteria criteria, Component display, ObjectiveCriteria.RenderType render) {
        return board.addObjective(name, criteria, display, render);
    }

    public static void removeObjective(Scoreboard board, Objective objective) { board.removeObjective(objective); }

    public static void display(Scoreboard board, String slot, Objective objective) {
        int at = Scoreboard.getDisplaySlotByName(slot);
        if (at < 0) { return; }
        board.setDisplayObjective(at, objective);
    }

    public static boolean has(Scoreboard board, String owner, Objective objective) { return board.hasPlayerScore(owner, objective); }

    public static int score(Scoreboard board, String owner, Objective objective) { return board.getOrCreatePlayerScore(owner, objective).getScore(); }

    public static void set(Scoreboard board, String owner, Objective objective, int value) { board.getOrCreatePlayerScore(owner, objective).setScore(value); }

    public static void reset(Scoreboard board, String owner, Objective objective) { board.resetPlayerScore(owner, objective); }

    public static List<Row> rows(Scoreboard board, Objective objective) {
        List<Row> found = new ArrayList<>();
        for (Score one : board.getPlayerScores(objective)) { found.add(new Row(one.getOwner(), one.getScore())); }
        found.sort((a, b) -> Integer.compare(b.value(), a.value()));
        return found;
    }
}
