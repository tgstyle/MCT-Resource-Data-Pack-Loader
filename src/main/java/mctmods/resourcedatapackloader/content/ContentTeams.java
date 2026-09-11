package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.def.TeamDef;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Says;
import mctmods.resourcedatapackloader.util.Scores;
import mctmods.resourcedatapackloader.util.Summary;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.LevelEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

public final class ContentTeams {
    private static final Map<String, Map<String, String>> BALLOTS = new LinkedHashMap<>();
    private static final Map<String, String> CLAIMED = new LinkedHashMap<>();
    private static final Map<String, String> WAITING = new LinkedHashMap<>();
    private static final Map<String, TeamDef> BY_NAME = new LinkedHashMap<>();

    private ContentTeams() {}

    public static boolean load() {
        BY_NAME.clear();
        PackManager.get().forEach(PackManager.TEAMS, PackManager.JSON, (namespace, path, contents) -> {
            ResourceLocation key = ResourceLocation.fromNamespaceAndPath(namespace, path);
            TeamDef def = ContentParser.teamFile(key, contents);
            if (def == null) { return; }
            if (BY_NAME.containsKey(def.name())) {
                ContentLog.LOGGER.error("Team file {} names the team '{}', which another pack already named, so the later one is left out", key, def.name());
                return;
            }
            BY_NAME.put(def.name(), def);
        });
        if (!BY_NAME.isEmpty()) { Summary.info("teams", "Fielding " + BY_NAME.size() + " team(s): " + BY_NAME.keySet()); }
        return !BY_NAME.isEmpty();
    }

    public static Map<String, TeamDef> all() { return BY_NAME; }

    public static boolean any() { return !BY_NAME.isEmpty(); }

    @Nullable public static TeamDef named(String name) { return BY_NAME.get(name); }

    public static void field(ServerLevel level) {
        if (BY_NAME.isEmpty()) { return; }
        Scoreboard board = Scores.board(level.getServer());
        for (TeamDef def : BY_NAME.values()) {
            PlayerTeam team = Scores.team(board, def.name());
            if (!def.scoreboard()) {
                if (team != null) {
                    ContentLog.LOGGER.info("{} no longer stands on the scoreboard, so its team and its {} member(s) are struck off, which is what lets its mobs fight each other again", def.name(), Scores.members(team).size());
                    Scores.removeTeam(board, team);
                }
                continue;
            }
            if (team == null) { team = Scores.addTeam(board, def.name()); }
            team.setDisplayName(Component.literal(def.displayName()));
            team.setColor(def.color());
            team.setPlayerPrefix(Component.literal(def.prefix()).withStyle(def.color()));
            team.setPlayerSuffix(Component.literal(def.suffix()));
            team.setAllowFriendlyFire(def.friendlyFire());
            team.setSeeFriendlyInvisibles(def.seeFriendlyInvisibles());
            team.setNameTagVisibility(def.nameTags());
            team.setDeathMessageVisibility(def.deathMessages());
            team.setCollisionRule(def.collision());
        }
    }

    @Nullable public static TeamDef spawnedInto(double x, double y, double z) {
        for (TeamDef def : BY_NAME.values()) {
            if (def.holds(x, y, z)) { return def; }
        }
        return null;
    }

    public static void onLevelLoad(LevelEvent.Load event) {
        if (event.getLevel() instanceof ServerLevel level) { field(level); }
    }

    public static void onJoin(EntityJoinLevelEvent event) {
        if (BY_NAME.isEmpty() || !(event.getLevel() instanceof ServerLevel level) || event.isCanceled()) { return; }
        Entity entity = event.getEntity();
        if (entity instanceof Player) { return; }
        String id = EntityType.getKey(entity.getType()).toString();
        for (TeamDef def : BY_NAME.values()) {
            if (def.scoreboard() || !def.entities().contains(id)) { continue; }
            Component held = entity.getCustomName();
            if (held == null || held.getStyle().getColor() == null) { entity.setCustomName(Component.literal(held == null ? def.displayName() : held.getString()).withStyle(def.color())); }
            break;
        }
        TeamDef wanted = teamFor(entity, id);
        if (wanted == null || !wanted.scoreboard()) { return; }
        join(level, entity.getStringUUID(), wanted);
    }

    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (BY_NAME.isEmpty() || !(event.getEntity() instanceof ServerPlayer player)) { return; }
        String named = player.getGameProfile().getName();
        for (TeamDef def : BY_NAME.values()) {
            if (def.players().contains(named)) {
                join(player.serverLevel(), named, def);
                return;
            }
        }
    }

    public static void onFriendlyFire(LivingAttackEvent event) {
        if (BY_NAME.isEmpty()) { return; }
        LivingEntity hurt = event.getEntity();
        if (hurt.level().isClientSide()) { return; }
        Entity by = event.getSource().getEntity();
        if (by == null || by == hurt) { return; }
        Team side = hurt.getTeam();
        TeamDef def = side == null ? null : BY_NAME.get(side.getName());
        if (def == null || def.mobFriendlyFire() || !hurt.isAlliedTo(by)) { return; }
        event.setCanceled(true);
    }

    @Nullable private static TeamDef teamFor(Entity entity, String id) {
        for (TeamDef def : BY_NAME.values()) {
            if (def.entities().contains(id)) { return def; }
        }
        return spawnedInto(entity.getX(), entity.getY(), entity.getZ());
    }

    private static void join(ServerLevel level, String member, TeamDef def) {
        Scoreboard board = Scores.board(level.getServer());
        if (Scores.team(board, def.name()) == null) { field(level); }
        PlayerTeam team = Scores.team(board, def.name());
        if (team == null) { return; }
        PlayerTeam held = Scores.teamOf(board, member);
        if (held != null && def.name().equals(held.getName())) { return; }
        Scores.join(board, member, team);
    }

    public static boolean standsFor(ServerLevel level, String voter, String choice, TeamDef def) {
        if (!TeamDef.VOTE.equals(def.lead()) || !onTeam(level, voter, def) || !onTeam(level, choice, def)) { return false; }
        BALLOTS.computeIfAbsent(def.name(), team -> new LinkedHashMap<>()).put(voter, choice);
        return true;
    }

    public static boolean claim(ServerLevel level, String member, TeamDef def) {
        if (!TeamDef.CLAIM.equals(def.lead()) || !onTeam(level, member, def)) { return false; }
        String held = CLAIMED.get(def.name());
        if (held != null && !held.equals(member) && onTeam(level, held, def)) { return false; }
        CLAIMED.put(def.name(), member);
        return true;
    }

    @Nullable public static String holding(TeamDef def) { return CLAIMED.get(def.name()); }

    private static boolean onTeam(ServerLevel level, String member, TeamDef def) {
        PlayerTeam held = Scores.teamOf(Scores.board(level.getServer()), member);
        return held != null && held.getName().equals(def.name());
    }

    @Nullable private static String voted(ServerLevel level, TeamDef def) {
        Map<String, String> cast = BALLOTS.get(def.name());
        if (cast == null || cast.isEmpty()) { return null; }
        Map<String, Integer> tally = new LinkedHashMap<>();
        for (Map.Entry<String, String> ballot : cast.entrySet()) {
            if (!onTeam(level, ballot.getKey(), def)) { continue; }
            tally.merge(ballot.getValue(), 1, Integer::sum);
        }
        String best = null;
        int most = 0;
        boolean tied = false;
        for (Map.Entry<String, Integer> one : tally.entrySet()) {
            if (one.getValue() > most) {
                most = one.getValue();
                best = one.getKey();
                tied = false;
            }
            else if (one.getValue() == most) { tied = true; }
        }
        return tied ? null : best;
    }

    @Nullable public static String leadOf(ServerLevel level, TeamDef def) {
        if (!def.leads()) { return null; }
        if (TeamDef.APPOINTED.equals(def.lead())) { return def.leadIs().isEmpty() ? null : def.leadIs(); }
        if (TeamDef.VOTE.equals(def.lead())) { return voted(level, def); }
        if (TeamDef.CLAIM.equals(def.lead())) {
            String held = CLAIMED.get(def.name());
            return held != null && onTeam(level, held, def) ? held : null;
        }
        Scoreboard board = Scores.board(level.getServer());
        PlayerTeam team = Scores.team(board, def.name());
        Objective objective = def.leadOn().isEmpty() ? null : Scores.objective(board, def.leadOn());
        if (team == null || objective == null) { return null; }
        String best = null;
        int most = Integer.MIN_VALUE;
        for (String member : Scores.members(team)) {
            if (!Scores.has(board, member, objective)) { continue; }
            int held = Scores.score(board, member, objective);
            if (held > most) {
                most = held;
                best = member;
            }
        }
        return best;
    }

    public static List<String> joinableNames() {
        List<String> found = new ArrayList<>();
        for (TeamDef def : BY_NAME.values()) {
            if (def.joinable()) { found.add(def.name()); }
        }
        return found;
    }

    @Nullable public static String standingOf(ServerPlayer player) {
        PlayerTeam held = Scores.teamOf(Scores.board(player.server), player.getGameProfile().getName());
        return held == null ? null : held.getName();
    }

    public static void take(ServerPlayer player, TeamDef def) { join(player.serverLevel(), player.getGameProfile().getName(), def); }

    public static void waitFor(ServerPlayer player, TeamDef def) { WAITING.put(player.getGameProfile().getName(), def.name()); }

    public static void seatWaiting(MinecraftServer server) {
        if (WAITING.isEmpty()) { return; }
        for (Map.Entry<String, String> one : new LinkedHashMap<>(WAITING).entrySet()) {
            TeamDef def = BY_NAME.get(one.getValue());
            ServerPlayer player = server.getPlayerList().getPlayerByName(one.getKey());
            if (def == null || player == null) { continue; }
            join(player.serverLevel(), player.getGameProfile().getName(), def);
            Says.tell(player, "The round ended, so you are on " + def.displayName(), def.color());
        }
        WAITING.clear();
    }

    @Nullable public static TeamDef smallest(ServerLevel level) {
        Scoreboard board = Scores.board(level.getServer());
        TeamDef best = null;
        int fewest = Integer.MAX_VALUE;
        for (TeamDef def : BY_NAME.values()) {
            if (!def.balance()) { continue; }
            PlayerTeam team = Scores.team(board, def.name());
            int players = 0;
            if (team != null) {
                for (String member : Scores.members(team)) {
                    if (member.length() <= 16 && !member.contains("-")) { players++; }
                }
            }
            if (players < fewest) {
                fewest = players;
                best = def;
            }
        }
        return best;
    }

    public static boolean stand(ServerPlayer player) { return Scores.leave(Scores.board(player.server), player.getGameProfile().getName()); }

    public static List<TeamDef> claiming(String entityId) {
        List<TeamDef> found = new ArrayList<>();
        for (TeamDef def : BY_NAME.values()) {
            if (def.entities().contains(entityId)) { found.add(def); }
        }
        return found;
    }
}
