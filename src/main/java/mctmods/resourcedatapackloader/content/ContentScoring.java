package mctmods.resourcedatapackloader.content;

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
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
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
    private static int starting;
    @Nullable private static ScoreDef opening;
    private static final int A_MINUTE = 60000;
    private static long opened;
    private static int beat;
    private static int waiting;
    @Nullable private static ScoreDef resetting;

    private ContentScoring() {}

    public static boolean load() {
        BY_NAME.clear();
        PackManager.get().forEach(PackManager.SCORING, PackManager.JSON, (namespace, path, contents) -> {
            ResourceLocation key = new ResourceLocation(namespace, path);
            ScoreDef def = ContentParser.scoreFile(key, contents);
            if (def == null) { return; }
            if (BY_NAME.containsKey(def.name)) {
                ContentLog.LOGGER.error("Score file {} names the objective '{}', which another pack already named, so the later one is left out", key, def.name);
                return;
            }
            BY_NAME.put(def.name, def);
        });
        if (!BY_NAME.isEmpty()) { Summary.info("scoring", "Keeping " + BY_NAME.size() + " score(s): " + BY_NAME.keySet()); }
        if (!BY_NAME.isEmpty() && !armed) {
            net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(ContentScoring.class);
            armed = true;
        }
        return !BY_NAME.isEmpty();
    }

    public static Map<String, ScoreDef> all() { return BY_NAME; }

    public static boolean roundRunning() {
        for (ScoreDef def : BY_NAME.values()) {
            if (def.ends() && def.endsLocksTeams && !FINISHED.contains(def.name)) { return true; }
        }
        return false;
    }

    public static boolean any() { return !BY_NAME.isEmpty(); }

    @Nullable public static ScoreDef named(String name) { return BY_NAME.get(name); }

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
        if (def.ends() && FINISHED.contains(def.name)) { return; }
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

    @Nullable private static String sideOf(World world, Entity who, String member) {
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
        if (event.phase != TickEvent.Phase.END || BY_NAME.isEmpty() || opened == 0L) { return; }
        if (++beat % 20 != 0) { return; }
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
                mctmods.resourcedatapackloader.content.worldgen.ContentPregen.tellBar(FMLCommonHandler.instance().getMinecraftServerInstance(),
                        resetting.intermissionSays.replace("{seconds}", Integer.toString(waiting)));
            }
        }
        long running = System.currentTimeMillis() - opened;
        for (ScoreDef def : BY_NAME.values()) {
            if (def.endsAfterMinutes <= 0 || FINISHED.contains(def.name)) { continue; }
            if (running >= (long) def.endsAfterMinutes * A_MINUTE) { finish(def, "time"); }
        }
    }

    private static void watch(ScoreDef def, int standing) {
        if (def.endsAtScore <= 0 || standing < def.endsAtScore || FINISHED.contains(def.name)) { return; }
        finish(def, "score");
    }

    private static void finish(ScoreDef def, String why) {
        if (!FINISHED.add(def.name)) { return; }
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server == null) { return; }
        List<String> lines = standings(server, def);
        ContentLog.LOGGER.info("The {} round is over on {}: {}", def.displayName, why, lines);
        ContentLog.LOGGER.info("Kills this round by the killer's kind: {}; deaths by kind: {}; kills of their own side: {}", KILLS, DEATHS, OWN);
        award(server, def);
        if (def.endsResets) {
            resetting = def;
            waiting = Math.max(1, def.endsIntermission);
            ContentLog.LOGGER.info("The map is reset in {} second(s), so the scores can be read first", def.endsIntermission);
        }
        if (!def.resultsCard) {
            Says.tellAll(def.displayName + " is over", TextFormatting.GOLD);
            for (String line : lines) { Says.tellAll(line, TextFormatting.YELLOW); }
            return;
        }
        ItemStack icon = def.resultsIcon.isEmpty() ? ItemStack.EMPTY
                : ContentStacks.parse(new ResourceLocation("resourcedatapackloader", "results"), def.resultsIcon, 1);
        MessageCard card = new MessageCard(def.resultsTitle, lines, icon, def.resultsImage,
                def.resultsBackground, 0xFFFFFF, def.resultsTicks);
        for (EntityPlayerMP player : server.getPlayerList().getPlayers()) {
            if (RDPLNetwork.vanilla(player)) {
                Says.tell(player, def.resultsTitle, TextFormatting.GOLD);
                for (String line : lines) { Says.tell(player, line, TextFormatting.YELLOW); }
            }
            else { RDPLNetwork.sendTo(card, player); }
        }
    }

    private static void award(MinecraftServer server, ScoreDef def) {
        if (def.awardsTo.isEmpty()) { return; }
        Scoreboard board = server.getWorld(0).getScoreboard();
        ScoreObjective round = board.getObjective(def.name);
        ScoreObjective match = board.getObjective(def.awardsTo);
        if (round == null || match == null) {
            ContentLog.LOGGER.error("The round {} awards to {}, which is not an objective this pack keeps, so no round win is recorded", def.name, def.awardsTo);
            return;
        }
        Score best = null;
        boolean tied = false;
        for (Score one : board.getSortedScores(round)) {
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
        if (whole.endsAfterRounds > 0 && played >= whole.endsAfterRounds) { finish(whole, "rounds"); }
        else { watch(whole, held.getScorePoints()); }
    }

    public static void starting(MinecraftServer server) {
        starting = OPENS_IN;
        count(server);
    }

    private static void count(MinecraftServer server) {
        if (opening == null || opening.startsSays.isEmpty()) { return; }
        mctmods.resourcedatapackloader.content.worldgen.ContentPregen.tellBar(server, opening.startsSays.replace("{seconds}", Integer.toString(starting)));
    }

    private static void open(MinecraftServer server) {
        opening = null;
        if (ContentControl.flag(ContentControl.CHUNKS, "resetClearsEntities", Config.chunks.resetClearsEntities)) { mctmods.resourcedatapackloader.content.worldgen.ContentReset.sweep(server); }
        roundOver();
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
        opened = System.currentTimeMillis();
        KILLS.clear();
        DEATHS.clear();
        OWN.clear();
        mctmods.resourcedatapackloader.content.ContentTeams.seatWaiting();
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
        if (opened == 0L) { opened = System.currentTimeMillis(); }
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
