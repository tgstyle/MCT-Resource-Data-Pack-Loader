package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.def.TeamDef;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Says;
import mctmods.resourcedatapackloader.util.Scores;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;

public final class ContentTeamsPicks {
    private static final Map<String, Map<String, String>> PICKED = new LinkedHashMap<>();
    static final Map<String, Set<String>> LEFT = new LinkedHashMap<>();

    private ContentTeamsPicks() {}

    static void stoodDown(MinecraftServer server, TeamDef def, String member) {
        Map<String, String> held = PICKED.get(def.name());
        if (held == null || held.remove(member) == null) { return; }
        LEFT.computeIfAbsent(def.name(), team -> new HashSet<>()).add(member);
        fill(server, def, null);
    }

    private static boolean left(TeamDef def, String member) {
        Set<String> gone = LEFT.get(def.name());
        return gone != null && gone.contains(member);
    }

    public static void draw(MinecraftServer server) {
        for (TeamDef def : ContentTeams.BY_NAME.values()) {
            if (def.picks() > 0 && def.scoreboard()) { draw(server, def, true, null); }
        }
    }

    static void fill(MinecraftServer server, TeamDef def, @Nullable Entity joining) {
        if (def.picks() <= 0 || !def.scoreboard() || picked(server, def) >= def.picks()) { return; }
        draw(server, def, false, joining);
    }

    private static void draw(MinecraftServer server, TeamDef def, boolean afresh, @Nullable Entity joining) {
        ServerLevel overworld = server.overworld();
        Scoreboard board = Scores.board(server);
        Map<String, String> held = PICKED.computeIfAbsent(def.name(), team -> new LinkedHashMap<>());
        Set<String> drawnBefore = new HashSet<>(held.keySet());
        if (afresh) {
            release(board, def, held);
            LEFT.remove(def.name());
        }
        List<Entity> pool = new ArrayList<>();
        if (def.picksFrom().contains(TeamDef.PLAYERS)) { pool.addAll(server.getPlayerList().getPlayers()); }
        for (ServerLevel each : server.getAllLevels()) {
            for (Entity one : each.getAllEntities()) {
                if (one instanceof Player || !one.isAlive()) { continue; }
                if (def.picksFrom().contains(EntityType.getKey(one.getType()).toString())) { pool.add(one); }
            }
        }
        if (joining != null && !pool.contains(joining)) { pool.add(joining); }
        Collections.shuffle(pool, new Random(overworld.getRandom().nextLong()));
        int have = picked(server, def);
        List<String> newlySeated = new ArrayList<>();
        for (Entity one : pool) {
            if (have >= def.picks()) { break; }
            String member = one instanceof Player player ? player.getGameProfile().getName() : one.getStringUUID();
            if (held.containsKey(member) || pickedAlready(member) || left(def, member)) { continue; }
            PlayerTeam before = Scores.teamOf(board, member);
            if (before != null && one instanceof Player) { continue; }
            held.put(member, before == null ? "" : before.getName());
            if (Scores.team(board, def.name()) == null) { ContentTeams.field(overworld); }
            PlayerTeam team = Scores.team(board, def.name());
            if (team == null) { continue; }
            Scores.join(board, member, team);
            if (one instanceof ServerPlayer player) {
                ContentTeams.seated(member, def);
                if (drawnBefore.contains(member)) { ContentLog.LOGGER.debug("{} was drawn for {} again", member, def.displayName()); }
                else {
                    if (ContentWelcome.arrived(player)) { Says.tell(player, mctmods.resourcedatapackloader.content.card.CardIds.TEAM_PICKED, "You were picked for " + def.displayName(), def.color()); }
                    ContentTeams.give(player, def);
                    newlySeated.add(member);
                }
            }
            ContentLog.LOGGER.info("{} was picked for {}", one.getName().getString(), def.displayName());
            have++;
        }
        String lead = ContentTeams.leadOf(overworld, def);
        if (lead != null && newlySeated.contains(lead)) { ContentTeams.tellLead(server, def, lead); }
    }

    private static void release(Scoreboard board, TeamDef def, Map<String, String> held) {
        for (Map.Entry<String, String> one : held.entrySet()) {
            PlayerTeam standing = Scores.teamOf(board, one.getKey());
            if (standing == null || !standing.getName().equals(def.name())) { continue; }
            Scores.leave(board, one.getKey());
            PlayerTeam back = one.getValue().isEmpty() ? null : Scores.team(board, one.getValue());
            if (back != null) { Scores.join(board, one.getKey(), back); }
        }
        held.clear();
    }

    private static int picked(MinecraftServer server, TeamDef def) {
        Map<String, String> held = PICKED.get(def.name());
        if (held == null) { return 0; }
        Scoreboard board = Scores.board(server);
        int count = 0;
        for (String member : new ArrayList<>(held.keySet())) {
            if (alive(server, member) && ContentTeams.onTeam(server, member, def)) {
                count++;
                continue;
            }
            PlayerTeam standing = Scores.teamOf(board, member);
            if (standing != null && standing.getName().equals(def.name())) { Scores.leave(board, member); }
            held.remove(member);
        }
        return count;
    }

    static boolean pickedAlready(String member) {
        for (Map<String, String> held : PICKED.values()) {
            if (held.containsKey(member)) { return true; }
        }
        return false;
    }

    private static boolean alive(MinecraftServer server, String member) {
        if (member.length() != 36 || member.indexOf('-') != 8) { return server.getPlayerList().getPlayerByName(member) != null; }
        UUID id;
        try { id = UUID.fromString(member); }
        catch (IllegalArgumentException notAnId) { return false; }
        for (ServerLevel level : server.getAllLevels()) {
            Entity held = level.getEntity(id);
            if (held != null) { return held.isAlive(); }
        }
        return false;
    }
}
