package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.content.def.ItemGiveDef;
import mctmods.resourcedatapackloader.content.def.TeamDef;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Functions;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.Says;
import mctmods.resourcedatapackloader.util.Scores;
import mctmods.resourcedatapackloader.util.Summary;
import mctmods.resourcedatapackloader.util.Travel;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.LevelEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public final class ContentTeams {
    private static final Map<String, Map<String, String>> BALLOTS = new LinkedHashMap<>();
    private static final Map<String, String> CLAIMED = new LinkedHashMap<>();
    private static final Map<String, String> WAITING = new LinkedHashMap<>();
    private static final Map<String, List<String>> ARRIVALS = new LinkedHashMap<>();
    private static final Map<String, String> DECIDED = new LinkedHashMap<>();
    private static final Set<String> UNMADE = new HashSet<>();
    private static final int STAND_IN_EVERY = 100;
    private static final int DECIDED_EVERY = 20;
    static final Map<String, TeamDef> BY_NAME = new LinkedHashMap<>();

    private ContentTeams() {}

    public static void load() {
        BY_NAME.clear();
        Json.eachFile(PackManager.TEAMS, "team file", (key, contents) -> {
            TeamDef def = ContentParserGames.teamFile(key, contents);
            if (def == null) { return; }
            if (BY_NAME.containsKey(def.name())) {
                ContentLog.LOGGER.error("Team file {} names the team '{}', which another pack already named, so the later one is left out", key, def.name());
                return;
            }
            BY_NAME.put(def.name(), def);
        });
        if (!BY_NAME.isEmpty()) { Summary.info("teams", "Fielding " + BY_NAME.size() + " team(s): " + BY_NAME.keySet()); }
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
        if (wanted != null && wanted.scoreboard()) { join(level, entity.getStringUUID(), wanted); }
        for (TeamDef def : BY_NAME.values()) {
            if (def.picksFrom().contains(id)) { ContentTeamsPicks.fill(level.getServer(), def, entity); }
        }
    }

    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (BY_NAME.isEmpty() || !(event.getEntity() instanceof ServerPlayer player)) { return; }
        String named = player.getGameProfile().getName();
        if (!ContentTeamsPicks.pickedAlready(named)) {
            for (TeamDef def : BY_NAME.values()) {
                if (def.players().contains(named)) {
                    join(player.serverLevel(), named, def);
                    break;
                }
            }
        }
        for (TeamDef def : BY_NAME.values()) {
            if (def.picksFrom().contains(TeamDef.PLAYERS)) { ContentTeamsPicks.fill(player.server, def, player); }
        }
    }

    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (BY_NAME.isEmpty() || !(event.getEntity() instanceof ServerPlayer leaving)) { return; }
        String name = leaving.getGameProfile().getName();
        MinecraftServer server = leaving.server;
        for (TeamDef def : BY_NAME.values()) {
            if (!TeamDef.FIRST.equals(def.lead()) || !name.equals(firstHere(server, def, null))) { continue; }
            String next = firstHere(server, def, name);
            ServerPlayer player = next == null ? null : server.getPlayerList().getPlayerByName(next);
            if (player != null) { leadTold(player, def, " (" + name + " left)"); }
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

    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) { return; }
        tick(event.getServer());
    }

    private static void tick(MinecraftServer server) {
        if (BY_NAME.isEmpty()) { return; }
        ServerLevel overworld = server.overworld();
        long now = overworld.getGameTime();
        if (now % DECIDED_EVERY == 0) { decided(server); }
        if (now % STAND_IN_EVERY != 0) { return; }
        for (TeamDef def : BY_NAME.values()) {
            if (def.standsIn() && def.scoreboard()) { standIn(server, overworld, def, false); }
        }
    }

    @Nullable private static TeamDef teamFor(Entity entity, String id) {
        for (TeamDef def : BY_NAME.values()) {
            if (def.entities().contains(id)) { return def; }
        }
        return spawnedInto(entity.getX(), entity.getY(), entity.getZ());
    }

    private static void join(ServerLevel level, String member, TeamDef def) {
        MinecraftServer server = level.getServer();
        Scoreboard board = Scores.board(server);
        if (Scores.team(board, def.name()) == null) { field(level); }
        PlayerTeam team = Scores.team(board, def.name());
        if (team == null) { return; }
        PlayerTeam held = Scores.teamOf(board, member);
        if (held != null && def.name().equals(held.getName())) { return; }
        Set<String> gone = ContentTeamsPicks.LEFT.get(def.name());
        if (gone != null) { gone.remove(member); }
        Scores.join(board, member, team);
        ServerPlayer player = server.getPlayerList().getPlayerByName(member);
        if (player == null) { return; }
        give(player, def);
        seated(member, def);
        tellLead(server, def, member);
    }

    static void seated(String member, TeamDef def) {
        if (!TeamDef.FIRST.equals(def.lead())) { return; }
        List<String> arrivals = ARRIVALS.computeIfAbsent(def.name(), team -> new ArrayList<>());
        if (!arrivals.contains(member)) { arrivals.add(member); }
    }

    static void tellLead(MinecraftServer server, TeamDef def, String member) {
        ServerPlayer player = server.getPlayerList().getPlayerByName(member);
        if (player == null || !TeamDef.FIRST.equals(def.lead()) || !member.equals(leadOf(server.overworld(), def))) { return; }
        if (ContentWelcome.arrived(player)) { leadTold(player, def, ""); }
    }

    private static void leadTold(ServerPlayer player, TeamDef def, String how) {
        if (!def.leadSays().isEmpty()) { Says.tell(player, mctmods.resourcedatapackloader.content.card.CardIds.TEAM_LEAD, def.leadSays().replace("{side}", def.displayName()) + how, def.color()); }
        ContentLog.LOGGER.info("{} leads {}{}", player.getGameProfile().getName(), def.displayName(), how);
    }

    public static void greet(ServerPlayer player) {
        PlayerTeam held = Scores.teamOf(Scores.board(player.server), player.getGameProfile().getName());
        TeamDef def = held == null ? null : BY_NAME.get(held.getName());
        if (def == null) { return; }
        Says.tell(player, mctmods.resourcedatapackloader.content.card.CardIds.TEAM_JOINED, "You are on " + def.displayName(), def.color());
        if (player.getGameProfile().getName().equals(leadOf(player.serverLevel(), def))) { leadTold(player, def, ""); }
    }

    @Nullable private static String firstHere(MinecraftServer server, TeamDef def, @Nullable String except) {
        List<String> arrivals = ARRIVALS.get(def.name());
        if (arrivals == null) { return null; }
        for (String member : arrivals) {
            if (member.equals(except)) { continue; }
            if (server.getPlayerList().getPlayerByName(member) != null && onTeam(server, member, def)) { return member; }
        }
        return null;
    }

    public static boolean leadsNoSide(ServerPlayer player) {
        for (TeamDef def : BY_NAME.values()) {
            if (player.getGameProfile().getName().equals(leadOf(player.serverLevel(), def))) { return false; }
        }
        return true;
    }

    public static List<String> leaders(ServerLevel level) {
        List<String> names = new ArrayList<>();
        for (TeamDef def : BY_NAME.values()) {
            String lead = leadOf(level, def);
            if (lead != null && !names.contains(lead)) { names.add(lead); }
        }
        return names;
    }

    public static void placeAtSpawns(MinecraftServer server) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            PlayerTeam held = Scores.teamOf(Scores.board(server), player.getGameProfile().getName());
            TeamDef def = held == null ? null : BY_NAME.get(held.getName());
            if (def == null || def.spawnAt() == null) { continue; }
            Travel.to(player, server.overworld(), def.spawnAt()[0] + 0.5D, def.spawnAt()[1], def.spawnAt()[2] + 0.5D, player.getYRot(), player.getXRot());
        }
    }

    public static void give(ServerPlayer player, TeamDef def) {
        if (def.gives().isEmpty()) { return; }
        for (ItemGiveDef one : def.gives()) {
            ItemStack stack = ContentStacks.parse(ResourceLocation.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, "teams/" + def.name()), one.item(), one.count());
            if (stack.isEmpty()) { continue; }
            if (one.unbreakable()) { stack.getOrCreateTag().putBoolean("Unbreakable", true); }
            if (!player.getInventory().add(stack)) { player.drop(stack, false); }
        }
        player.inventoryMenu.broadcastChanges();
    }

    public static void giveAll(MinecraftServer server) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            PlayerTeam held = Scores.teamOf(Scores.board(server), player.getGameProfile().getName());
            TeamDef def = held == null ? null : BY_NAME.get(held.getName());
            if (def != null) { give(player, def); }
        }
    }

    public static boolean standsFor(ServerLevel level, String voter, String choice, TeamDef def) {
        if (!TeamDef.VOTE.equals(def.lead()) || !onTeam(level.getServer(), voter, def) || !onTeam(level.getServer(), choice, def)) { return false; }
        BALLOTS.computeIfAbsent(def.name(), team -> new LinkedHashMap<>()).put(voter, choice);
        return true;
    }

    public static boolean claim(ServerLevel level, String member, TeamDef def) {
        if (!TeamDef.CLAIM.equals(def.lead()) || !onTeam(level.getServer(), member, def)) { return false; }
        String held = CLAIMED.get(def.name());
        if (held != null && !held.equals(member) && onTeam(level.getServer(), held, def)) { return false; }
        CLAIMED.put(def.name(), member);
        return true;
    }

    @Nullable public static String holding(TeamDef def) { return CLAIMED.get(def.name()); }

    static boolean onTeam(MinecraftServer server, String member, TeamDef def) {
        PlayerTeam held = Scores.teamOf(Scores.board(server), member);
        return held != null && held.getName().equals(def.name());
    }

    @Nullable private static String voted(ServerLevel level, TeamDef def) {
        Map<String, String> cast = BALLOTS.get(def.name());
        if (cast == null || cast.isEmpty()) { return null; }
        Map<String, Integer> tally = new LinkedHashMap<>();
        for (Map.Entry<String, String> ballot : cast.entrySet()) {
            if (!onTeam(level.getServer(), ballot.getKey(), def)) { continue; }
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
        if (def.leaderless()) { return null; }
        MinecraftServer server = level.getServer();
        if (TeamDef.APPOINTED.equals(def.lead())) { return def.leadIs().isEmpty() ? null : def.leadIs(); }
        if (TeamDef.VOTE.equals(def.lead())) { return voted(level, def); }
        if (TeamDef.FIRST.equals(def.lead())) { return firstHere(server, def, null); }
        if (TeamDef.CLAIM.equals(def.lead())) {
            String held = CLAIMED.get(def.name());
            return held != null && onTeam(server, held, def) ? held : null;
        }
        Scoreboard board = Scores.board(server);
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
            Says.tell(player, mctmods.resourcedatapackloader.content.card.CardIds.TEAM_ROUND_ENDED, "The round ended, so you are on " + def.displayName(), def.color());
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

    public static boolean stand(ServerPlayer player) {
        String name = player.getGameProfile().getName();
        PlayerTeam held = Scores.teamOf(Scores.board(player.server), name);
        if (held == null || !Scores.leave(Scores.board(player.server), name)) { return false; }
        TeamDef def = BY_NAME.get(held.getName());
        if (def != null) { ContentTeamsPicks.stoodDown(player.server, def, name); }
        return true;
    }

    private static void decided(MinecraftServer server) {
        for (TeamDef def : BY_NAME.values()) {
            if (def.leadRuns().isEmpty() || def.leaderless()) { continue; }
            String lead = leadOf(server.overworld(), def);
            if (lead == null) {
                DECIDED.remove(def.name());
                continue;
            }
            if (lead.equals(DECIDED.get(def.name()))) { continue; }
            ServerPlayer player = server.getPlayerList().getPlayerByName(lead);
            if (player == null) { continue; }
            DECIDED.put(def.name(), lead);
            ContentLog.LOGGER.info("{} is decided as the lead of {}, so {} runs", lead, def.displayName(), def.leadRuns());
            Functions.runAs(player, def.leadRuns(), "The lead of " + def.name());
        }
    }

    public static void standInsNow(MinecraftServer server) {
        for (TeamDef def : BY_NAME.values()) {
            if (def.standsIn() && def.scoreboard()) { standIn(server, server.overworld(), def, true); }
        }
    }

    private static void standIn(MinecraftServer server, ServerLevel level, TeamDef def, boolean now) {
        boolean manned = false;
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (onTeam(server, player.getGameProfile().getName(), def)) {
                manned = true;
                break;
            }
        }
        ResourceLocation id = ResourceLocation.tryParse(def.standIn());
        EntityType<?> kind = id == null ? null : EntityType.byString(id.toString()).orElse(null);
        List<Entity> standing = new ArrayList<>();
        for (Entity one : level.getAllEntities()) {
            if (one instanceof Player || !one.isAlive() || one.getType() != kind) { continue; }
            if (onTeam(server, one.getStringUUID(), def)) { standing.add(one); }
        }
        if (manned) {
            for (Entity one : standing) { one.discard(); }
            if (!standing.isEmpty()) { ContentLog.LOGGER.info("A player stands on {}, so its {} stand-in(s) of {} step aside", def.displayName(), standing.size(), def.standIn()); }
            return;
        }
        if (!standing.isEmpty() || ContentScoring.standInWaits() && !now) { return; }
        int[] at = def.standInAt();
        if (at == null || !level.isLoaded(new BlockPos(at[0], at[1], at[2]))) { return; }
        Entity made = kind == null ? null : kind.create(level);
        if (made == null) {
            if (UNMADE.add(def.name())) { ContentLog.LOGGER.error("Team {} names {} as its stand-in, which nothing registers, so the side has none; this is said once", def.name(), def.standIn()); }
            return;
        }
        made.moveTo(at[0] + 0.5D, at[1], at[2] + 0.5D, 0.0F, 0.0F);
        if (made instanceof Mob mob) { ForgeEventFactory.onFinalizeSpawn(mob, level, level.getCurrentDifficultyAt(made.blockPosition()), MobSpawnType.EVENT, null, null); }
        level.addFreshEntity(made);
        join(level, made.getStringUUID(), def);
        ContentLog.LOGGER.info("No player stands on {}, so a {} stands in at {}, {}, {}", def.displayName(), def.standIn(), at[0], at[1], at[2]);
    }

    public static List<TeamDef> claiming(String entityId) {
        List<TeamDef> found = new ArrayList<>();
        for (TeamDef def : BY_NAME.values()) {
            if (def.entities().contains(entityId)) { found.add(def); }
        }
        return found;
    }
}
