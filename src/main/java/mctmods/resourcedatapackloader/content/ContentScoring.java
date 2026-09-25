package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.card.CardFire;
import mctmods.resourcedatapackloader.content.card.CardIds;
import mctmods.resourcedatapackloader.content.card.CardLook;
import mctmods.resourcedatapackloader.content.card.CardRules;
import mctmods.resourcedatapackloader.content.def.ScoreDef;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Summary;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import mctmods.resourcedatapackloader.network.MessageCard;
import mctmods.resourcedatapackloader.network.RDPLNetwork;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.ResourceLocation;
import net.minecraft.server.MinecraftServer;

import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import mctmods.resourcedatapackloader.util.Says;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public final class ContentScoring {
    private static boolean armed;
    private static final Map<String, ScoreDef> BY_NAME = new LinkedHashMap<>();
    private static final Set<String> FINISHED = new LinkedHashSet<>();
    private static final Map<String, Integer> KILLS = new LinkedHashMap<>();
    private static final Map<String, Integer> DEATHS = new LinkedHashMap<>();
    private static final Map<String, Integer> OWN = new LinkedHashMap<>();
    private static final int OPENS_IN = 5;
    private static final int SETTLE_TICKS = 3;
    private static final Map<java.util.UUID, Integer> SETTLING = new LinkedHashMap<>();
    private static int starting;
    @Nullable private static ScoreDef opening;
    private static final int MINUTE_TICKS = 20 * 60;
    private static boolean live;
    private static int ticks;
    private static int beat;
    private static int waiting;
    @Nullable private static ScoreDef resetting;
    private static boolean closed;
    static final Map<String, GameType> OUT = new LinkedHashMap<>();
    private static final Set<String> IN_PLAY = new LinkedHashSet<>();
    private static boolean lobbied;
    private static final String RESET = "reset";

    private ContentScoring() {}

    public static void load() {
        BY_NAME.clear();
        ContentRoundReset.clear();
        PackManager.get().forEach(PackManager.SCORING, PackManager.JSON, (namespace, path, contents) -> {
            ResourceLocation key = new ResourceLocation(namespace, path);
            ScoreDef def = ContentParserGames.scoreFile(key, contents);
            if (def == null) { return; }
            if (BY_NAME.containsKey(def.name)) {
                ContentLog.LOGGER.error("Score file {} names the objective '{}', which another pack already named, so the later one is left out", key, def.name);
                return;
            }
            BY_NAME.put(def.name, def);
        });
        if (!BY_NAME.isEmpty()) { Summary.info("scoring", "Keeping " + BY_NAME.size() + " score(s): " + BY_NAME.keySet()); }
        lobbied = lobbyDef() != null;
        if (!BY_NAME.isEmpty() && !armed) {
            net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(ContentScoring.class);
            armed = true;
        }
    }

    public static Map<String, ScoreDef> all() { return BY_NAME; }

    public static boolean roundRunning() {
        if (closed) { return false; }
        for (ScoreDef def : BY_NAME.values()) {
            if (def.ends() && def.endsLocksTeams && !FINISHED.contains(def.name)) { return true; }
        }
        return false;
    }

    public static boolean any() { return !BY_NAME.isEmpty(); }

    @SubscribeEvent public static void onWorldLoad(WorldEvent.Load event) { keep(event.getWorld()); }

    @SubscribeEvent public static void onDeath(LivingDeathEvent event) {
        if (BY_NAME.isEmpty()) { return; }
        Entity died = event.getEntity();
        if (died == null || died.world.isRemote) { return; }
        ResourceLocation id = died instanceof EntityPlayer ? null : EntityList.getKey(died);
        String killed = id == null ? "minecraft:player" : id.toString();
        Entity killer = event.getSource() == null ? null : event.getSource().getTrueSource();
        String killerSide = killer == null ? null : sideOf(died.world, killer, killer instanceof EntityPlayer ? killer.getName() : killer.getCachedUniqueIdString());
        String diedSide = sideOf(died.world, died, died instanceof EntityPlayer ? died.getName() : died.getCachedUniqueIdString());
        boolean own = killerSide != null && killerSide.equals(diedSide);
        boolean scored = false;
        for (ScoreDef def : BY_NAME.values()) {
            if (!def.fed()) { continue; }
            Integer worth = def.killPoints.get(killed);
            if (worth != null && killer != null) {
                scored = true;
                int points = own ? def.ownKillPoints : worth;
                if (points != 0) { credit(died.world, def, killer, points); }
            }
            if (def.deathPoints != 0) { credit(died.world, def, died, def.deathPoints); }
        }
        if (!scored) { return; }
        String kind = killer instanceof EntityPlayer ? "minecraft:player" : String.valueOf(EntityList.getKey(killer));
        KILLS.merge(kind, 1, Integer::sum);
        DEATHS.merge(killed, 1, Integer::sum);
        if (own) { OWN.merge(kind, 1, Integer::sum); }
    }

    private static void credit(World world, ScoreDef def, Entity who, int points) {
        if (closed || def.ends() && FINISHED.contains(def.name)) { return; }
        Scoreboard board = world.getScoreboard();
        ScoreObjective objective = board.getObjective(def.name);
        if (objective == null) { return; }
        String member = who instanceof EntityPlayer ? who.getName() : who.getCachedUniqueIdString();
        if (def.individuals) { bump(board, objective, member, points); }
        if (!def.teamTotals) { return; }
        String side = sideOf(world, who, member);
        if (side == null) { return; }
        bump(board, objective, side, points);
        watch(def, board.getOrCreateScore(side, objective).getScorePoints());
    }

    @Nullable static String sideOf(World world, Entity who, String member) {
        ScorePlayerTeam team = world.getScoreboard().getPlayersTeam(member);
        if (team != null) { return team.getName(); }
        if (who instanceof EntityPlayer) { return null; }
        ResourceLocation id = EntityList.getKey(who);
        if (id == null) { return null; }
        List<mctmods.resourcedatapackloader.content.def.TeamDef> claiming =
                mctmods.resourcedatapackloader.content.ContentTeams.claiming(id.toString());
        return claiming.isEmpty() ? null : claiming.get(0).name;
    }

    private static void bump(Scoreboard board, ScoreObjective objective, String row, int points) {
        Score score = board.getOrCreateScore(row, objective);
        score.setScorePoints(score.getScorePoints() + points);
    }

    @SubscribeEvent public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || BY_NAME.isEmpty() || !live) { return; }
        ContentScoringLobby.keepStill(FMLCommonHandler.instance().getMinecraftServerInstance());
        if (!closed && starting == 0 && waiting == 0 && !mctmods.resourcedatapackloader.content.worldgen.ContentPregen.busy()) { ticks++; }
        if (++beat % 20 != 0) { return; }
        settling(FMLCommonHandler.instance().getMinecraftServerInstance());
        ContentRoundReset.second(FMLCommonHandler.instance().getMinecraftServerInstance());
        if (closed && lobbied) {
            ContentScoringLobby.gather(FMLCommonHandler.instance().getMinecraftServerInstance());
            ContentScoringLobby.lobbyNotes(FMLCommonHandler.instance().getMinecraftServerInstance());
        }
        if (starting > 0) {
            if (--starting > 0) { count(FMLCommonHandler.instance().getMinecraftServerInstance()); }
            else { open(FMLCommonHandler.instance().getMinecraftServerInstance()); }
            return;
        }
        if (waiting > 0 && resetting != null) {
            if (--waiting == 0) {
                opening = resetting;
                resetting = null;
                mctmods.resourcedatapackloader.content.worldgen.ContentReset.run(FMLCommonHandler.instance().getMinecraftServerInstance());
                return;
            }
            if (!resetting.intermissionSays.isEmpty()) {
                mctmods.resourcedatapackloader.content.worldgen.ContentPregenHold.tellBar(FMLCommonHandler.instance().getMinecraftServerInstance(),
                        resetting.intermissionSays.replace("{seconds}", Integer.toString(waiting)));
            }
        }
        if (closed) { return; }
        for (ScoreDef def : BY_NAME.values()) {
            if (def.endsAfterMinutes <= 0 || FINISHED.contains(def.name)) { continue; }
            if (ticks >= def.endsAfterMinutes * MINUTE_TICKS) { finish(def, "time", null); }
        }
        if (starting == 0) { standing(FMLCommonHandler.instance().getMinecraftServerInstance()); }
    }

    private static void watch(ScoreDef def, int standing) {
        if (def.endsAtScore <= 0 || standing < def.endsAtScore || FINISHED.contains(def.name)) { return; }
        finish(def, "score", null);
    }

    private static void finish(ScoreDef def, String why, @Nullable String winner) {
        if (!FINISHED.add(def.name)) { return; }
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server == null) { return; }
        List<String> lines = standings(server, def);
        if (def.endsLastStanding) { backIn(server); }
        if (winner != null) {
            mctmods.resourcedatapackloader.content.def.TeamDef side = ContentTeams.named(winner);
            lines.add(0, (side == null ? winner : side.displayName) + " stood last");
        }
        else if ("last standing".equals(why)) { lines.add(0, "No side was left standing"); }
        else if (RESET.equals(why)) { lines.add(0, "The round was reset"); }
        ContentLog.LOGGER.info("The {} round is over on {}: {}", def.displayName, why, lines);
        ContentLog.LOGGER.info("Kills this round by the killer's kind: {}; deaths by kind: {}; kills of their own side: {}", KILLS, DEATHS, OWN);
        if (RESET.equals(why)) { ContentLog.LOGGER.info("A reset round is awarded to nobody"); }
        else { award(server, def, winner); }
        if (def.endsResets) {
            resetting = def;
            waiting = Math.max(1, def.endsIntermission);
            ContentLog.LOGGER.info("The map is reset in {} second(s), so the scores can be read first", def.endsIntermission);
        }
        ItemStack icon = def.resultsIcon.isEmpty() ? ItemStack.EMPTY
                : ContentStacks.parse(new ResourceLocation("resourcedatapackloader", "results"), def.resultsIcon, 1);
        if (!CardRules.unset(CardIds.SCORING_RESULTS)) {
            for (EntityPlayerMP player : server.getPlayerList().getPlayers()) { CardFire.builtin(CardIds.SCORING_RESULTS, player, CardLook.card(def.resultsTitle, lines, icon, def.resultsImage, def.resultsBackground, def.resultsTicks)); }
            return;
        }
        if (!def.resultsCard) {
            Says.tellAll(def.displayName + " is over", TextFormatting.GOLD);
            for (String line : lines) { Says.tellAll(line, TextFormatting.YELLOW); }
            return;
        }
        MessageCard card = new MessageCard(def.resultsTitle, lines, icon, def.resultsImage,
                def.resultsBackground, 0xFFFFFF, def.resultsTicks, false, Says.panel(), Says.font());
        for (EntityPlayerMP player : server.getPlayerList().getPlayers()) {
            if (RDPLNetwork.vanilla(player)) {
                Says.tell(player, def.resultsTitle, TextFormatting.GOLD);
                for (String line : lines) { Says.tell(player, line, TextFormatting.YELLOW); }
            }
            else { RDPLNetwork.sendTo(card, player); }
        }
    }

    private static void award(MinecraftServer server, ScoreDef def, @Nullable String winner) {
        if (def.awardsTo.isEmpty()) { return; }
        Scoreboard board = server.getWorld(0).getScoreboard();
        ScoreObjective round = board.getObjective(def.name);
        ScoreObjective match = board.getObjective(def.awardsTo);
        if (round == null || match == null) {
            ContentLog.LOGGER.error("The round {} awards to {}, which is not an objective this pack keeps, so no round win is recorded", def.name, def.awardsTo);
            return;
        }
        Score best = winner == null ? null : board.getOrCreateScore(winner, round);
        boolean tied = false;
        for (Score one : winner == null ? board.getSortedScores(round) : new ArrayList<Score>()) {
            if (best == null || one.getScorePoints() > best.getScorePoints()) {
                best = one;
                tied = false;
            }
            else if (one.getScorePoints() == best.getScorePoints()) { tied = true; }
        }
        if (best == null || tied) {
            ContentLog.LOGGER.info("The round ended level, so no round win is recorded");
            return;
        }
        Score held = board.getOrCreateScore(best.getPlayerName(), match);
        held.setScorePoints(held.getScorePoints() + 1);
        ContentLog.LOGGER.info("{} took the round, and now holds {} in {}", best.getPlayerName(), held.getScorePoints(), def.awardsTo);
        ScoreDef whole = BY_NAME.get(def.awardsTo);
        if (whole == null || FINISHED.contains(whole.name)) { return; }
        int played = 0;
        for (Score one : board.getSortedScores(match)) { played += one.getScorePoints(); }
        if (whole.endsAfterRounds > 0 && played >= whole.endsAfterRounds) { finish(whole, "rounds", null); }
        else { watch(whole, held.getScorePoints()); }
    }

    public static void starting(MinecraftServer server) {
        ScoreDef lobby = lobbyDef();
        if (lobby != null) {
            closed = true;
            opening = lobby;
            ContentScoringLobby.waitingSaid(server);
            return;
        }
        starting = OPENS_IN;
        count(server);
    }

    @Nullable public static ScoreDef resetOffer() {
        for (ScoreDef def : BY_NAME.values()) {
            if (def.reset.offered()) { return def; }
        }
        return null;
    }

    public static boolean noRoundToReset() { return closed || starting > 0 || resetting != null; }

    public static void resetRound(ScoreDef offered) {
        for (ScoreDef def : new ArrayList<>(BY_NAME.values())) {
            if (!def.carries && (def.ends() || def == offered)) { finish(def, RESET, null); }
        }
        resetting = offered;
        waiting = Math.max(1, offered.endsIntermission);
        ContentLog.LOGGER.info("The round was reset, so the map is reset in {} second(s)", waiting);
    }

    @Nullable static ScoreDef lobbyDef() {
        for (ScoreDef def : BY_NAME.values()) {
            if ("leader".equals(def.opensBy)) { return def; }
        }
        return null;
    }

    public static boolean eliminating() {
        if (closed || starting > 0) { return false; }
        for (ScoreDef def : BY_NAME.values()) {
            if (def.endsLastStanding && !FINISHED.contains(def.name)) { return true; }
        }
        return false;
    }

    private static void standing(@Nullable MinecraftServer server) {
        if (server == null || !eliminating()) { return; }
        Map<String, Integer> sides = new LinkedHashMap<>();
        WorldServer overworld = server.getWorld(0);
        for (EntityPlayerMP player : server.getPlayerList().getPlayers()) {
            if (OUT.containsKey(player.getName()) || player.isSpectator()) { continue; }
            String side = sideOf(overworld, player, player.getName());
            if (side != null) { sides.merge(side, 1, Integer::sum); }
        }
        for (WorldServer world : server.worlds) {
            for (Entity one : world.loadedEntityList) {
                if (one instanceof EntityPlayer || one.isDead || !(one instanceof net.minecraft.entity.EntityLivingBase) || ((net.minecraft.entity.EntityLivingBase) one).getHealth() <= 0.0F) { continue; }
                String side = sideOf(world, one, one.getCachedUniqueIdString());
                if (side != null) { sides.merge(side, 1, Integer::sum); }
            }
        }
        if (IN_PLAY.isEmpty()) {
            IN_PLAY.addAll(sides.keySet());
            if (IN_PLAY.size() < 2) { ContentLog.LOGGER.info("Only {} side(s) are in play, so the round cannot end on the last side standing", IN_PLAY.size()); }
            else { ContentLog.LOGGER.info("The sides in play this round are {}", IN_PLAY); }
            return;
        }
        if (IN_PLAY.size() < 2) { return; }
        List<String> left = new ArrayList<>();
        for (String side : IN_PLAY) {
            if (sides.containsKey(side)) { left.add(side); }
        }
        if (left.size() > 1) { return; }
        String winner = left.isEmpty() ? null : left.get(0);
        for (ScoreDef def : BY_NAME.values()) {
            if (def.endsLastStanding && !FINISHED.contains(def.name)) { finish(def, "last standing", winner); }
        }
    }

    @SubscribeEvent public static void onPlayerDeath(LivingDeathEvent event) {
        if (!(event.getEntityLiving() instanceof EntityPlayerMP) || event.isCanceled() || !eliminating()) { return; }
        EntityPlayerMP player = (EntityPlayerMP) event.getEntityLiving();
        if (sideOf(player.world, player, player.getName()) == null || OUT.containsKey(player.getName())) { return; }
        OUT.put(player.getName(), player.interactionManager.getGameType());
        ContentLog.LOGGER.info("{} is out of the round", player.getName());
    }

    @SubscribeEvent public static void onRespawn(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent event) {
        if (!(event.player instanceof EntityPlayerMP) || !OUT.containsKey(event.player.getName())) { return; }
        EntityPlayerMP player = (EntityPlayerMP) event.player;
        if (!eliminating()) {
            player.setGameType(OUT.remove(player.getName()));
            return;
        }
        player.setGameType(GameType.SPECTATOR);
        String said = outSays();
        if (!said.isEmpty()) { Says.tell(player, CardIds.SCORING_OUT, said, TextFormatting.GRAY); }
    }

    @SubscribeEvent public static void onBack(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.player instanceof EntityPlayerMP)) { return; }
        EntityPlayerMP player = (EntityPlayerMP) event.player;
        if (!eliminating() && OUT.containsKey(player.getName())) { player.setGameType(OUT.remove(player.getName())); }
        SETTLING.put(player.getUniqueID(), SETTLE_TICKS);
    }

    private static void settling(@Nullable MinecraftServer server) {
        if (server == null || SETTLING.isEmpty()) { return; }
        Set<java.util.UUID> online = new LinkedHashSet<>();
        for (EntityPlayerMP player : server.getPlayerList().getPlayers()) {
            online.add(player.getUniqueID());
            Integer left = SETTLING.get(player.getUniqueID());
            if (left == null) { continue; }
            if (left > 1) {
                SETTLING.put(player.getUniqueID(), left - 1);
                continue;
            }
            SETTLING.remove(player.getUniqueID());
            mctmods.resourcedatapackloader.content.worldgen.ContentReset.arrived(player);
            ContentScoringLobby.waitsInLobby(player);
        }
        SETTLING.keySet().retainAll(online);
    }

    private static void backIn(MinecraftServer server) {
        for (Map.Entry<String, GameType> one : new LinkedHashMap<>(OUT).entrySet()) {
            EntityPlayerMP player = server.getPlayerList().getPlayerByUsername(one.getKey());
            if (player == null) { continue; }
            if (player.isEntityAlive()) { player.setGameType(one.getValue()); }
            else { continue; }
            OUT.remove(one.getKey());
        }
    }

    private static String outSays() {
        for (ScoreDef def : BY_NAME.values()) {
            if (def.endsLastStanding) { return def.endsOutSays; }
        }
        return BY_NAME.isEmpty() ? "" : BY_NAME.values().iterator().next().endsOutSays;
    }

    public static boolean holding() { return lobbied && (closed || starting > 0); }

    public static boolean standInWaits() { return lobbied ? !holding() : eliminating(); }

    @SubscribeEvent public static void onStill(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer || event.getEntityLiving().world.isRemote || !holding()) { return; }
        event.setCanceled(true);
    }

    @SubscribeEvent public static void onHeldHurt(LivingAttackEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer && !event.getEntityLiving().world.isRemote && holding()) { event.setCanceled(true); }
    }

    @SubscribeEvent public static void onHeldHit(AttackEntityEvent event) {
        if (ContentScoringLobby.refused(event.getEntityPlayer())) { event.setCanceled(true); }
    }

    @SubscribeEvent public static void onHeldDig(PlayerInteractEvent.LeftClickBlock event) {
        if (ContentScoringLobby.refused(event.getEntityPlayer())) { event.setCanceled(true); }
    }

    @SubscribeEvent public static void onHeldUse(PlayerInteractEvent.RightClickBlock event) {
        if (ContentScoringLobby.refused(event.getEntityPlayer())) { event.setCanceled(true); }
    }

    @SubscribeEvent public static void onHeldItem(PlayerInteractEvent.RightClickItem event) {
        if (ContentScoringLobby.refused(event.getEntityPlayer())) { event.setCanceled(true); }
    }

    @SubscribeEvent public static void onHeldTouch(PlayerInteractEvent.EntityInteract event) {
        if (ContentScoringLobby.refused(event.getEntityPlayer())) { event.setCanceled(true); }
    }

    @SubscribeEvent public static void onHeldBreak(BlockEvent.BreakEvent event) {
        if (ContentScoringLobby.refused(event.getPlayer())) { event.setCanceled(true); }
    }

    @SubscribeEvent public static void onHeldPlace(BlockEvent.EntityPlaceEvent event) {
        if (event.getEntity() instanceof EntityPlayer && ContentScoringLobby.refused((EntityPlayer) event.getEntity())) { event.setCanceled(true); }
    }

    @SubscribeEvent public static void onHeldToss(ItemTossEvent event) {
        if (!ContentScoringLobby.refused(event.getPlayer())) { return; }
        event.setCanceled(true);
        event.getPlayer().inventory.addItemStackToInventory(event.getEntityItem().getItem());
        event.getPlayer().inventoryContainer.detectAndSendChanges();
    }

    public static void greet(EntityPlayerMP player) {
        if (closed && lobbied) { ContentScoringLobby.DUE.put(player.getName(), player.world.getTotalWorldTime() + ContentScoringLobby.NOTE_TICKS); }
    }

    public static String start(MinecraftServer server, EntityPlayer who, boolean operator) {
        if (lobbyDef() == null) { return "This pack's rounds open on their own"; }
        if (!closed) { return "The round is already running"; }
        if (!operator && mctmods.resourcedatapackloader.content.ContentTeams.leadsNoSide(who)) { return "Only a side's leader starts the round"; }
        List<String> reading = new ArrayList<>();
        for (EntityPlayerMP player : server.getPlayerList().getPlayers()) {
            if (mctmods.resourcedatapackloader.content.extra.ContentIntroPlay.reading(player.getUniqueID())) { reading.add(player.getName()); }
        }
        if (!reading.isEmpty()) { return "Not yet: still reading the intro: " + String.join(", ", reading); }
        closed = false;
        opening = lobbyDef();
        starting = OPENS_IN;
        count(server);
        ContentLog.LOGGER.info("{} started the round", who.getName());
        return "The round starts";
    }

    private static void count(MinecraftServer server) {
        if (opening == null || opening.startsSays.isEmpty()) { return; }
        mctmods.resourcedatapackloader.content.worldgen.ContentPregenHold.tellBar(server, opening.startsSays.replace("{seconds}", Integer.toString(starting)));
    }

    private static void open(MinecraftServer server) {
        opening = null;
        ContentScoringLobby.sendBack(server);
        if (ContentControl.flag(ContentControl.CHUNKS, "resetClearsEntities", Config.chunks.resetClearsEntities)) { mctmods.resourcedatapackloader.content.worldgen.ContentReset.sweep(server); }
        roundOver();
        mctmods.resourcedatapackloader.content.ContentTeams.placeAtSpawns(server);
        mctmods.resourcedatapackloader.content.ContentTeams.standInsNow(server);
        ContentScoringLobby.DUE.clear();
        ContentScoringLobby.SHOWN.clear();
    }

    public static void roundOver() {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        for (ScoreDef def : BY_NAME.values()) {
            if (!def.carries) {
                FINISHED.remove(def.name);
                continue;
            }
            if (!FINISHED.remove(def.name) || server == null) { continue; }
            Scoreboard board = server.getWorld(0).getScoreboard();
            ScoreObjective held = board.getObjective(def.name);
            if (held == null) { continue; }
            for (Score one : new ArrayList<>(board.getSortedScores(held))) { board.removeObjectiveFromEntity(one.getPlayerName(), held); }
            ContentLog.LOGGER.info("The {} match is over, so its standing is cleared for the next one", def.displayName);
        }
        ticks = 0;
        IN_PLAY.clear();
        KILLS.clear();
        DEATHS.clear();
        OWN.clear();
        mctmods.resourcedatapackloader.content.ContentTeams.seatWaiting();
        mctmods.resourcedatapackloader.content.ContentTeamsPicks.draw();
    }

    private static List<String> standings(MinecraftServer server, ScoreDef def) {
        List<String> lines = new ArrayList<>();
        Scoreboard board = server.getWorld(0).getScoreboard();
        ScoreObjective objective = board.getObjective(def.name);
        if (objective == null) { return lines; }
        List<Score> scores = new ArrayList<>(board.getSortedScores(objective));
        scores.sort((a, b) -> Integer.compare(b.getScorePoints(), a.getScorePoints()));
        for (Score score : scores) { lines.add(score.getPlayerName() + " " + score.getScorePoints()); }
        return lines;
    }

    public static void keep(World world) {
        if (BY_NAME.isEmpty() || world.isRemote) { return; }
        if (!live) {
            live = true;
            if (lobbyDef() != null) { closed = true; }
        }
        Scoreboard board = world.getScoreboard();
        for (ScoreDef def : BY_NAME.values()) {
            ScoreObjective held = board.getObjective(def.name);
            if (held == null) { held = board.addScoreObjective(def.name, def.criterion); }
            else if (held.getCriteria() != def.criterion) {
                ContentLog.LOGGER.error("The objective {} is already on this world's scoreboard scoring on something else, so the pack's criterion is left alone", def.name);
            }
            held.setDisplayName(def.displayName);
            if (def.render != null) { held.setRenderType(def.render); }
            if (!def.shown()) { continue; }
            int slot = Scoreboard.getObjectiveDisplaySlotNumber(def.slot);
            if (slot >= 0) { board.setObjectiveInDisplaySlot(slot, held); }
        }
    }
}
