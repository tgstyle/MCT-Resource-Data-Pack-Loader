package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.def.TeamDef;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Summary;

import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.EntityList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

public final class ContentTeams {
    private static boolean armed;
    private static final Map<String, Map<String, String>> BALLOTS = new LinkedHashMap<>();
    private static final Map<String, String> CLAIMED = new LinkedHashMap<>();
    private static final Map<String, String> WAITING = new LinkedHashMap<>();
    private static final Map<String, TeamDef> BY_NAME = new LinkedHashMap<>();

    private ContentTeams() {}

    public static boolean load() {
        BY_NAME.clear();
        PackManager.get().forEach(PackManager.TEAMS, PackManager.JSON, (namespace, path, contents) -> {
            ResourceLocation key = new ResourceLocation(namespace, path);
            TeamDef def = ContentParser.teamFile(key, contents);
            if (def == null) { return; }
            if (BY_NAME.containsKey(def.name)) {
                ContentLog.LOGGER.error("Team file {} names the team '{}', which another pack already named, so the later one is left out", key, def.name);
                return;
            }
            BY_NAME.put(def.name, def);
        });
        if (!BY_NAME.isEmpty()) { Summary.info("teams", "Fielding " + BY_NAME.size() + " team(s): " + BY_NAME.keySet()); }
        if (!BY_NAME.isEmpty() && !armed) {
            net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(ContentTeams.class);
            armed = true;
        }
        return !BY_NAME.isEmpty();
    }

    public static Map<String, TeamDef> all() { return BY_NAME; }

    public static boolean any() { return !BY_NAME.isEmpty(); }

    @SuppressWarnings({"ConstantConditions", "ConstantValue"}) public static void field(World world) {
        if (BY_NAME.isEmpty() || world.isRemote) { return; }
        Scoreboard board = world.getScoreboard();
        for (TeamDef def : BY_NAME.values()) {
            if (!def.scoreboard) {
                ScorePlayerTeam stale = board.getTeam(def.name);
                if (stale != null) {
                    ContentLog.LOGGER.info("{} no longer stands on the scoreboard, so its team and its {} member(s) are struck off, which is what lets its mobs fight each other again",
                            def.name, stale.getMembershipCollection().size());
                    board.removeTeam(stale);
                }
                continue;
            }
            ScorePlayerTeam team = board.getTeam(def.name);
            if (team == null) { team = board.createTeam(def.name); }
            team.setDisplayName(def.displayName);
            team.setColor(def.color);
            team.setPrefix(def.color + def.prefix);
            team.setSuffix(def.suffix);
            team.setAllowFriendlyFire(def.friendlyFire);
            team.setSeeFriendlyInvisiblesEnabled(def.seeFriendlyInvisibles);
            team.setNameTagVisibility(def.nameTags);
            team.setDeathMessageVisibility(def.deathMessages);
            team.setCollisionRule(def.collision);
        }
    }

    @Nullable public static TeamDef spawnedInto(double x, double y, double z) {
        for (TeamDef def : BY_NAME.values()) {
            if (def.holds(x, y, z)) { return def; }
        }
        return null;
    }

    @SubscribeEvent public static void onWorldLoad(WorldEvent.Load event) { field(event.getWorld()); }

    @SubscribeEvent(priority = net.minecraftforge.fml.common.eventhandler.EventPriority.LOWEST)
    public static void onNamed(EntityJoinWorldEvent event) {
        if (BY_NAME.isEmpty() || event.getWorld().isRemote || event.isCanceled()) { return; }
        Entity entity = event.getEntity();
        if (entity instanceof EntityPlayer) { return; }
        ResourceLocation id = EntityList.getKey(entity);
        if (id == null) { return; }
        for (TeamDef def : BY_NAME.values()) {
            if (def.scoreboard || !def.entities.contains(id.toString())) { continue; }
            String named = entity.hasCustomName() ? entity.getCustomNameTag() : def.displayName;
            if (named.indexOf('§') < 0) { entity.setCustomNameTag(def.color + named); }
            return;
        }
    }

    @SubscribeEvent public static void onJoin(EntityJoinWorldEvent event) {
        if (BY_NAME.isEmpty() || event.getWorld().isRemote) { return; }
        Entity entity = event.getEntity();
        if (entity instanceof EntityPlayer) { return; }
        TeamDef wanted = teamFor(entity);
        if (wanted == null || !wanted.scoreboard) { return; }
        join(event.getWorld(), entity.getCachedUniqueIdString(), wanted);
    }

    @SubscribeEvent public static void onLogin(PlayerLoggedInEvent event) {
        if (BY_NAME.isEmpty() || !(event.player instanceof EntityPlayerMP)) { return; }
        String named = event.player.getName();
        for (TeamDef def : BY_NAME.values()) {
            if (def.players.contains(named)) {
                join(event.player.world, named, def);
                return;
            }
        }
    }

    @SubscribeEvent public static void onFriendlyFire(LivingAttackEvent event) {
        if (BY_NAME.isEmpty()) { return; }
        EntityLivingBase hurt = event.getEntityLiving();
        if (hurt.world.isRemote) { return; }
        Entity by = event.getSource().getTrueSource();
        if (by == null || by == hurt) { return; }
        Team side = hurt.getTeam();
        TeamDef def = side == null ? null : BY_NAME.get(side.getName());
        if (def == null || def.mobFriendlyFire || !hurt.isOnSameTeam(by)) { return; }
        event.setCanceled(true);
    }

    @Nullable private static TeamDef teamFor(Entity entity) {
        ResourceLocation id = EntityList.getKey(entity);
        if (id != null) {
            for (TeamDef def : BY_NAME.values()) {
                if (def.entities.contains(id.toString())) { return def; }
            }
        }
        return spawnedInto(entity.posX, entity.posY, entity.posZ);
    }

    @SuppressWarnings({"ConstantConditions", "ConstantValue"}) private static void join(World world, String member, TeamDef def) {
        Scoreboard board = world.getScoreboard();
        if (board.getTeam(def.name) == null) { field(world); }
        ScorePlayerTeam held = board.getPlayersTeam(member);
        if (held != null && def.name.equals(held.getName())) { return; }
        board.addPlayerToTeam(member, def.name);
    }

    @Nullable public static TeamDef named(String name) { return BY_NAME.get(name); }

    public static boolean standsFor(World world, String voter, String choice, TeamDef def) {
        if (!"vote".equals(def.lead) || !onTeam(world, voter, def) || !onTeam(world, choice, def)) { return false; }
        BALLOTS.computeIfAbsent(def.name, team -> new LinkedHashMap<>()).put(voter, choice);
        return true;
    }

    public static boolean claim(World world, String member, TeamDef def) {
        if (!"claim".equals(def.lead) || !onTeam(world, member, def)) { return false; }
        String held = CLAIMED.get(def.name);
        if (held != null && !held.equals(member) && onTeam(world, held, def)) { return false; }
        CLAIMED.put(def.name, member);
        return true;
    }

    @Nullable public static String holding(TeamDef def) { return CLAIMED.get(def.name); }

    private static boolean onTeam(World world, String member, TeamDef def) {
        ScorePlayerTeam held = world.getScoreboard().getPlayersTeam(member);
        return held != null && held.getName().equals(def.name);
    }

    @Nullable private static String voted(World world, TeamDef def) {
        Map<String, String> cast = BALLOTS.get(def.name);
        if (cast == null || cast.isEmpty()) { return null; }
        Map<String, Integer> tally = new LinkedHashMap<>();
        for (Map.Entry<String, String> ballot : cast.entrySet()) {
            if (!onTeam(world, ballot.getKey(), def)) { continue; }
            tally.merge(ballot.getValue(), 1, Integer::sum);
        }
        String best = null;
        int most = 0;
        boolean tied = false;
        for (Map.Entry<String, Integer> one : tally.entrySet()) {
            if (one.getValue() > most) { most = one.getValue(); best = one.getKey(); tied = false; }
            else if (one.getValue() == most) { tied = true; }
        }
        return tied ? null : best;
    }

    @SuppressWarnings({"ConstantConditions", "ConstantValue"}) @Nullable public static String leadOf(World world, TeamDef def) {
        if (!def.leads() || world == null) { return null; }
        if ("appointed".equals(def.lead)) { return def.leadIs.isEmpty() ? null : def.leadIs; }
        if ("vote".equals(def.lead)) { return voted(world, def); }
        if ("claim".equals(def.lead)) {
            String held = CLAIMED.get(def.name);
            return held != null && onTeam(world, held, def) ? held : null;
        }
        Scoreboard board = world.getScoreboard();
        ScorePlayerTeam team = board.getTeam(def.name);
        net.minecraft.scoreboard.ScoreObjective objective = def.leadOn.isEmpty() ? null : board.getObjective(def.leadOn);
        if (team == null || objective == null) { return null; }
        String best = null;
        int most = Integer.MIN_VALUE;
        for (String member : team.getMembershipCollection()) {
            if (!board.entityHasObjective(member, objective)) { continue; }
            int held = board.getOrCreateScore(member, objective).getScorePoints();
            if (held > most) { most = held; best = member; }
        }
        return best;
    }

    public static List<String> joinableNames() {
        List<String> found = new ArrayList<>();
        for (TeamDef def : BY_NAME.values()) {
            if (def.joinable) { found.add(def.name); }
        }
        return found;
    }

    @Nullable public static String standingOf(EntityPlayer player) {
        ScorePlayerTeam held = player.world.getScoreboard().getPlayersTeam(player.getName());
        return held == null ? null : held.getName();
    }

    public static boolean take(EntityPlayer player, TeamDef def) {
        join(player.world, player.getName(), def);
        return true;
    }

    public static void waitFor(EntityPlayer player, TeamDef def) { WAITING.put(player.getName(), def.name); }

    public static void seatWaiting() {
        if (WAITING.isEmpty()) { return; }
        MinecraftServer server = net.minecraftforge.fml.common.FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server == null) { return; }
        for (Map.Entry<String, String> one : new LinkedHashMap<>(WAITING).entrySet()) {
            TeamDef def = BY_NAME.get(one.getValue());
            EntityPlayerMP player = server.getPlayerList().getPlayerByUsername(one.getKey());
            if (def == null || player == null) { continue; }
            join(player.world, player.getName(), def);
            mctmods.resourcedatapackloader.util.Says.tell(player, "The round ended, so you are on " + def.displayName, def.color);
        }
        WAITING.clear();
    }

    @SuppressWarnings({"ConstantConditions", "ConstantValue"}) @Nullable public static TeamDef smallest(World world) {
        Scoreboard board = world.getScoreboard();
        TeamDef best = null;
        int fewest = Integer.MAX_VALUE;
        for (TeamDef def : BY_NAME.values()) {
            if (!def.balance) { continue; }
            ScorePlayerTeam team = board.getTeam(def.name);
            int players = 0;
            if (team != null) {
                for (String member : team.getMembershipCollection()) {
                    if (member.length() <= 16 && !member.contains("-")) { players++; }
                }
            }
            if (players < fewest) { fewest = players; best = def; }
        }
        return best;
    }

    public static boolean stand(EntityPlayer player) {
        Scoreboard board = player.world.getScoreboard();
        ScorePlayerTeam held = board.getPlayersTeam(player.getName());
        if (held == null) { return false; }
        board.removePlayerFromTeam(player.getName(), held);
        return true;
    }

    public static List<TeamDef> claiming(String entityId) {
        List<TeamDef> found = new ArrayList<>();
        for (TeamDef def : BY_NAME.values()) {
            if (def.entities.contains(entityId)) { found.add(def); }
        }
        return found;
    }
}
