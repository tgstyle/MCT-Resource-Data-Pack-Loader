package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.content.def.ItemGiveDef;
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
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;

public final class ContentTeams {
    private static boolean armed;
    private static final Map<String, Map<String, String>> BALLOTS = new LinkedHashMap<>();
    private static final Map<String, String> CLAIMED = new LinkedHashMap<>();
    private static final Map<String, String> WAITING = new LinkedHashMap<>();
    private static final Map<String, Map<String, String>> PICKED = new LinkedHashMap<>();
    private static final int STAND_IN_EVERY = 100;
    private static final Map<String, List<String>> ARRIVALS = new LinkedHashMap<>();
    private static final Map<String, String> DECIDED = new LinkedHashMap<>();
    private static final int DECIDED_EVERY = 20;
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
        if (wanted != null && wanted.scoreboard) { join(event.getWorld(), entity.getCachedUniqueIdString(), wanted); }
        ResourceLocation id = EntityList.getKey(entity);
        if (id == null) { return; }
        for (TeamDef def : BY_NAME.values()) {
            if (def.picksFrom.contains(id.toString())) { fill(def, entity); }
        }
    }

    @SubscribeEvent public static void onLogin(PlayerLoggedInEvent event) {
        if (BY_NAME.isEmpty() || !(event.player instanceof EntityPlayerMP)) { return; }
        String named = event.player.getName();
        if (!pickedAlready(named)) {
            for (TeamDef def : BY_NAME.values()) {
                if (def.players.contains(named)) {
                    join(event.player.world, named, def);
                    break;
                }
            }
        }
        for (TeamDef def : BY_NAME.values()) {
            if (def.picksFrom.contains("players")) { fill(def, event.player); }
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
        seated(world, member, def);
    }

    private static void seated(World world, String member, TeamDef def) {
        MinecraftServer server = net.minecraftforge.fml.common.FMLCommonHandler.instance().getMinecraftServerInstance();
        EntityPlayerMP player = server == null ? null : server.getPlayerList().getPlayerByUsername(member);
        if (player == null) { return; }
        give(player, def);
        if (!"first".equals(def.lead)) { return; }
        List<String> arrivals = ARRIVALS.computeIfAbsent(def.name, team -> new ArrayList<>());
        if (!arrivals.contains(member)) { arrivals.add(member); }
        if (member.equals(leadOf(world, def)) && mctmods.resourcedatapackloader.content.worldgen.ContentPregen.arrived(player)) { leadTold(player, def, ""); }
    }

    private static void leadTold(EntityPlayerMP player, TeamDef def, String how) {
        if (!def.leadSays.isEmpty()) { mctmods.resourcedatapackloader.util.Says.tell(player, def.leadSays.replace("{side}", def.displayName) + how, def.color); }
        ContentLog.LOGGER.info("{} leads {}{}", player.getName(), def.displayName, how);
    }

    public static void greet(EntityPlayerMP player) {
        ScorePlayerTeam held = player.world.getScoreboard().getPlayersTeam(player.getName());
        TeamDef def = held == null ? null : BY_NAME.get(held.getName());
        if (def == null) { return; }
        mctmods.resourcedatapackloader.util.Says.tell(player, "You are on " + def.displayName, def.color);
        if (player.getName().equals(leadOf(player.world, def))) { leadTold(player, def, ""); }
    }

    @SubscribeEvent public static void onLogout(PlayerLoggedOutEvent event) {
        if (BY_NAME.isEmpty() || !(event.player instanceof EntityPlayerMP)) { return; }
        String leaving = event.player.getName();
        MinecraftServer server = event.player.getServer();
        if (server == null) { return; }
        for (TeamDef def : BY_NAME.values()) {
            if (!"first".equals(def.lead) || !leaving.equals(firstHere(event.player.world, def, null))) { continue; }
            String next = firstHere(event.player.world, def, leaving);
            EntityPlayerMP player = next == null ? null : server.getPlayerList().getPlayerByUsername(next);
            if (player != null) { leadTold(player, def, " (" + leaving + " left)"); }
        }
    }

    @Nullable private static String firstHere(World world, TeamDef def, @Nullable String except) {
        List<String> arrivals = ARRIVALS.get(def.name);
        MinecraftServer server = net.minecraftforge.fml.common.FMLCommonHandler.instance().getMinecraftServerInstance();
        if (arrivals == null || server == null) { return null; }
        for (String member : arrivals) {
            if (member.equals(except)) { continue; }
            if (server.getPlayerList().getPlayerByUsername(member) != null && onTeam(world, member, def)) { return member; }
        }
        return null;
    }

    public static boolean leadsNoSide(EntityPlayer player) {
        for (TeamDef def : BY_NAME.values()) {
            if (player.getName().equals(leadOf(player.world, def))) { return false; }
        }
        return true;
    }

    public static List<String> leaders(World world) {
        List<String> names = new ArrayList<>();
        for (TeamDef def : BY_NAME.values()) {
            String lead = leadOf(world, def);
            if (lead != null && !names.contains(lead)) { names.add(lead); }
        }
        return names;
    }

    public static void placeAtSpawns(MinecraftServer server) {
        for (EntityPlayerMP player : server.getPlayerList().getPlayers()) {
            ScorePlayerTeam held = player.world.getScoreboard().getPlayersTeam(player.getName());
            TeamDef def = held == null ? null : BY_NAME.get(held.getName());
            if (def == null || def.spawnAt == null) { continue; }
            mctmods.resourcedatapackloader.util.world.Travel.to(player, 0, def.spawnAt[0] + 0.5D, def.spawnAt[1], def.spawnAt[2] + 0.5D, player.rotationYaw, player.rotationPitch);
        }
    }

    public static void give(EntityPlayerMP player, TeamDef def) {
        if (def.gives.isEmpty()) { return; }
        for (ItemGiveDef one : def.gives) {
            ItemStack stack = ContentStacks.parse(new ResourceLocation(ResourceDataPackLoader.MOD_ID, "teams/" + def.name), one.item, one.count);
            if (stack.isEmpty()) { continue; }
            if (one.unbreakable) { stack.setTagInfo("Unbreakable", new net.minecraft.nbt.NBTTagByte((byte) 1)); }
            if (!player.inventory.addItemStackToInventory(stack)) { player.dropItem(stack, false); }
        }
        player.inventoryContainer.detectAndSendChanges();
    }

    public static void giveAll(MinecraftServer server) {
        for (EntityPlayerMP player : server.getPlayerList().getPlayers()) {
            ScorePlayerTeam held = player.world.getScoreboard().getPlayersTeam(player.getName());
            TeamDef def = held == null ? null : BY_NAME.get(held.getName());
            if (def != null) { give(player, def); }
        }
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
        if (def.leaderless() || world == null) { return null; }
        if ("appointed".equals(def.lead)) { return def.leadIs.isEmpty() ? null : def.leadIs; }
        if ("vote".equals(def.lead)) { return voted(world, def); }
        if ("first".equals(def.lead)) { return firstHere(world, def, null); }
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

    @SubscribeEvent public static void onTick(TickEvent.WorldTickEvent event) {
        if (event.side != Side.SERVER || event.phase != TickEvent.Phase.END || event.world.provider.getDimension() != 0) { return; }
        MinecraftServer server = event.world.getMinecraftServer();
        if (server == null) { return; }
        if (event.world.getTotalWorldTime() % DECIDED_EVERY == 0) { decided(server, event.world); }
        if (event.world.getTotalWorldTime() % STAND_IN_EVERY != 0) { return; }
        for (TeamDef def : BY_NAME.values()) {
            if (def.standsIn() && def.scoreboard) { standIn(server, (net.minecraft.world.WorldServer) event.world, def, false); }
        }
    }

    private static void decided(MinecraftServer server, World world) {
        for (TeamDef def : BY_NAME.values()) {
            if (def.leadRuns.isEmpty() || def.leaderless()) { continue; }
            String lead = leadOf(world, def);
            if (lead == null) {
                DECIDED.remove(def.name);
                continue;
            }
            if (lead.equals(DECIDED.get(def.name))) { continue; }
            EntityPlayerMP player = server.getPlayerList().getPlayerByUsername(lead);
            if (player == null) { continue; }
            DECIDED.put(def.name, lead);
            ContentLog.LOGGER.info("{} is decided as the lead of {}, so {} runs", lead, def.displayName, def.leadRuns);
            mctmods.resourcedatapackloader.util.Functions.runAs(player, def.leadRuns, "The lead of " + def.name);
        }
    }

    public static void standInsNow(MinecraftServer server) {
        net.minecraft.world.WorldServer world = server.getWorld(0);
        for (TeamDef def : BY_NAME.values()) {
            if (def.standsIn() && def.scoreboard) { standIn(server, world, def, true); }
        }
    }

    private static void standIn(MinecraftServer server, net.minecraft.world.WorldServer world, TeamDef def, boolean now) {
        boolean manned = false;
        for (EntityPlayerMP player : server.getPlayerList().getPlayers()) {
            if (onTeam(player.world, player.getName(), def)) { manned = true; break; }
        }
        ResourceLocation id = new ResourceLocation(def.standIn);
        List<Entity> standing = new ArrayList<>();
        for (Entity one : world.loadedEntityList) {
            if (one instanceof EntityPlayer || one.isDead || !id.equals(EntityList.getKey(one))) { continue; }
            if (onTeam(world, one.getCachedUniqueIdString(), def)) { standing.add(one); }
        }
        if (manned) {
            for (Entity one : standing) { one.setDead(); }
            if (!standing.isEmpty()) { ContentLog.LOGGER.info("A player stands on {}, so its {} stand-in(s) of {} step aside", def.displayName, standing.size(), def.standIn); }
            return;
        }
        if (!standing.isEmpty() || ContentScoring.standInWaits() && !now) { return; }
        int[] at = def.standInAt;
        if (at == null || !world.isBlockLoaded(new net.minecraft.util.math.BlockPos(at[0], at[1], at[2]))) { return; }
        Entity made = EntityList.createEntityByIDFromName(id, world);
        if (made == null) {
            ContentLog.LOGGER.error("Team {} names {} as its stand-in, which nothing registers", def.name, def.standIn);
            return;
        }
        made.setLocationAndAngles(at[0] + 0.5D, at[1], at[2] + 0.5D, 0.0F, 0.0F);
        if (made instanceof net.minecraft.entity.EntityLiving) { ((net.minecraft.entity.EntityLiving) made).onInitialSpawn(world.getDifficultyForLocation(new net.minecraft.util.math.BlockPos(made)), null); }
        world.spawnEntity(made);
        join(world, made.getCachedUniqueIdString(), def);
        ContentLog.LOGGER.info("No player stands on {}, so a {} stands in at {}, {}, {}", def.displayName, def.standIn, at[0], at[1], at[2]);
    }

    public static void draw() {
        MinecraftServer server = net.minecraftforge.fml.common.FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server == null) { return; }
        for (TeamDef def : BY_NAME.values()) {
            if (def.picks > 0 && def.scoreboard) { draw(server, def, true, null); }
        }
    }

    private static void fill(TeamDef def, Entity joining) {
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
        if (afresh) { release(board, def, held); }
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
        for (Entity one : pool) {
            if (have >= def.picks) { break; }
            String member = one instanceof EntityPlayer ? one.getName() : one.getCachedUniqueIdString();
            if (held.containsKey(member) || pickedAlready(member)) { continue; }
            ScorePlayerTeam before = board.getPlayersTeam(member);
            if (before != null && one instanceof EntityPlayer) { continue; }
            held.put(member, before == null ? "" : before.getName());
            if (board.getTeam(def.name) == null) { field(world); }
            board.addPlayerToTeam(member, def.name);
            if (one instanceof EntityPlayerMP) {
                if (mctmods.resourcedatapackloader.content.worldgen.ContentPregen.arrived((EntityPlayerMP) one)) { mctmods.resourcedatapackloader.util.Says.tell((EntityPlayerMP) one, "You were picked for " + def.displayName, def.color); }
                seated(world, member, def);
            }
            ContentLog.LOGGER.info("{} was picked for {}", one.getName(), def.displayName);
            have++;
        }
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
            if (alive(server, member) && onTeam(world, member, def)) {
                count++;
                continue;
            }
            ScorePlayerTeam standing = world.getScoreboard().getPlayersTeam(member);
            if (standing != null && standing.getName().equals(def.name)) { world.getScoreboard().removePlayerFromTeam(member, standing); }
            held.remove(member);
        }
        return count;
    }

    private static boolean pickedAlready(String member) {
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

    public static List<TeamDef> claiming(String entityId) {
        List<TeamDef> found = new ArrayList<>();
        for (TeamDef def : BY_NAME.values()) {
            if (def.entities.contains(entityId)) { found.add(def); }
        }
        return found;
    }
}
