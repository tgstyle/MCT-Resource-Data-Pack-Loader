package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.def.TeamDef;
import mctmods.resourcedatapackloader.util.ContentLog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public final class ContentTeamsPicks {
    private static final Map<String, Map<String, String>> PICKED = new LinkedHashMap<>();
    static final Map<String, Set<String>> LEFT = new LinkedHashMap<>();

    private ContentTeamsPicks() {}

    static void stoodDown(TeamDef def, String member) {
        Map<String, String> held = PICKED.get(def.name);
        if (held == null || held.remove(member) == null) { return; }
        LEFT.computeIfAbsent(def.name, team -> new HashSet<>()).add(member);
        fill(def, null);
    }

    private static boolean left(TeamDef def, String member) {
        Set<String> gone = LEFT.get(def.name);
        return gone != null && gone.contains(member);
    }

    public static void draw() {
        MinecraftServer server = net.minecraftforge.fml.common.FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server == null) { return; }
        for (TeamDef def : ContentTeams.BY_NAME.values()) {
            if (def.picks > 0 && def.scoreboard) { draw(server, def, true, null); }
        }
    }

    static void fill(TeamDef def, @Nullable Entity joining) {
        if (def.picks <= 0 || !def.scoreboard) { return; }
        MinecraftServer server = net.minecraftforge.fml.common.FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server == null || picked(server, def) >= def.picks) { return; }
        draw(server, def, false, joining);
    }

    @SuppressWarnings({"ConstantConditions", "ConstantValue"}) private static void draw(MinecraftServer server, TeamDef def, boolean afresh, @Nullable Entity joining) {
        World world = server.getWorld(0);
        if (world == null) { return; }
        Scoreboard board = world.getScoreboard();
        Map<String, String> held = PICKED.computeIfAbsent(def.name, team -> new LinkedHashMap<>());
        Set<String> drawnBefore = new HashSet<>(held.keySet());
        if (afresh) {
            release(board, def, held);
            LEFT.remove(def.name);
        }
        List<Entity> pool = new ArrayList<>();
        if (def.picksFrom.contains("players")) { pool.addAll(server.getPlayerList().getPlayers()); }
        for (WorldServer each : server.worlds) {
            for (Entity one : each.loadedEntityList) {
                if (one instanceof EntityPlayer || one.isDead) { continue; }
                ResourceLocation id = EntityList.getKey(one);
                if (id != null && def.picksFrom.contains(id.toString())) { pool.add(one); }
            }
        }
        if (joining != null && !pool.contains(joining)) { pool.add(joining); }
        Collections.shuffle(pool, world.rand);
        int have = picked(server, def);
        List<String> newlySeated = new ArrayList<>();
        for (Entity one : pool) {
            if (have >= def.picks) { break; }
            String member = one instanceof EntityPlayer ? one.getName() : one.getCachedUniqueIdString();
            if (held.containsKey(member) || pickedAlready(member) || left(def, member)) { continue; }
            ScorePlayerTeam before = board.getPlayersTeam(member);
            if (before != null && one instanceof EntityPlayer) { continue; }
            held.put(member, before == null ? "" : before.getName());
            if (board.getTeam(def.name) == null) { ContentTeams.field(world); }
            board.addPlayerToTeam(member, def.name);
            if (one instanceof EntityPlayerMP) {
                ContentTeams.seated(member, def);
                if (drawnBefore.contains(member)) { ContentLog.LOGGER.debug("{} was drawn for {} again", one.getName(), def.displayName); }
                else {
                    if (mctmods.resourcedatapackloader.content.worldgen.ContentPregenHold.arrived((EntityPlayerMP) one)) { mctmods.resourcedatapackloader.util.Says.tell((EntityPlayerMP) one, mctmods.resourcedatapackloader.content.card.CardIds.TEAM_PICKED, "You were picked for " + def.displayName, def.color); }
                    ContentTeams.give((EntityPlayerMP) one, def);
                    newlySeated.add(member);
                }
            }
            ContentLog.LOGGER.info("{} was picked for {}", one.getName(), def.displayName);
            have++;
        }
        String lead = ContentTeams.leadOf(world, def);
        if (lead != null && newlySeated.contains(lead)) { ContentTeams.tellLead(world, def, lead); }
    }

    private static void release(Scoreboard board, TeamDef def, Map<String, String> held) {
        for (Map.Entry<String, String> one : held.entrySet()) {
            ScorePlayerTeam standing = board.getPlayersTeam(one.getKey());
            if (standing == null || !standing.getName().equals(def.name)) { continue; }
            board.removePlayerFromTeam(one.getKey(), standing);
            if (!one.getValue().isEmpty()) { board.addPlayerToTeam(one.getKey(), one.getValue()); }
        }
        held.clear();
    }

    @SuppressWarnings({"ConstantConditions", "ConstantValue"}) private static int picked(MinecraftServer server, TeamDef def) {
        Map<String, String> held = PICKED.get(def.name);
        World world = server.getWorld(0);
        if (held == null || world == null) { return 0; }
        int count = 0;
        for (String member : new ArrayList<>(held.keySet())) {
            if (alive(server, member) && ContentTeams.onTeam(world, member, def)) {
                count++;
                continue;
            }
            ScorePlayerTeam standing = world.getScoreboard().getPlayersTeam(member);
            if (standing != null && standing.getName().equals(def.name)) { world.getScoreboard().removePlayerFromTeam(member, standing); }
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
        if (member.length() == 36 && member.indexOf('-') == 8) {
            try {
                Entity held = server.getEntityFromUuid(UUID.fromString(member));
                return held != null && !held.isDead;
            }
            catch (IllegalArgumentException notAnId) { return false; }
        }
        return server.getPlayerList().getPlayerByUsername(member) != null;
    }
}
