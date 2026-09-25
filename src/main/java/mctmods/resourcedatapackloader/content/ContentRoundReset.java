package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.def.RoundResetDef;
import mctmods.resourcedatapackloader.content.def.ScoreDef;
import mctmods.resourcedatapackloader.content.worldgen.ContentPregenHold;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Says;
import mctmods.resourcedatapackloader.util.Scores;

import net.minecraft.ChatFormatting;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.PlayerTeam;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

public final class ContentRoundReset {
    public static final String CALLED = "Your vote to reset the round is called";
    public static final String DONE = "The round is reset";
    private static final Map<String, Boolean> BALLOTS = new LinkedHashMap<>();
    @Nullable private static ScoreDef voting;
    private static int left;
    private static long quietUntil;

    private ContentRoundReset() {}

    public static void clear() {
        BALLOTS.clear();
        voting = null;
        left = 0;
        quietUntil = 0L;
    }

    public static String call(MinecraftServer server, String name, @Nullable ServerPlayer who, boolean operator) {
        ScoreDef def = ContentScoring.resetOffer();
        if (def == null) { return "This pack's rounds cannot be reset"; }
        if (ContentScoring.noRoundToReset()) { return "There is no round running to reset"; }
        RoundResetDef reset = def.reset();
        String way = operator ? RoundResetDef.NOW : who == null ? RoundResetDef.NONE : wayFor(who, reset);
        if (RoundResetDef.NOW.equals(way)) {
            drop();
            if (!reset.leadSays().isEmpty()) { Says.tellAll(server, mctmods.resourcedatapackloader.content.card.CardIds.RESET_LEAD, reset.leadSays().replace("{player}", name), ChatFormatting.GOLD); }
            ContentLog.LOGGER.info("{} reset the round", name);
            ContentScoring.resetRound(server, def);
            return DONE;
        }
        if (RoundResetDef.NONE.equals(way)) { return reset.playersVote() ? "Your side cannot call a vote to reset the round" : "Only a side's leader resets the round"; }
        if (voting != null) { return "A reset vote is already running: /rdpl round vote yes or no"; }
        long wait = quietUntil - System.currentTimeMillis();
        if (wait > 0L) { return "A reset vote was just held, so another can be called in " + (wait + 999L) / 1000L + " second(s)"; }
        voting = def;
        left = reset.voteSeconds();
        BALLOTS.clear();
        BALLOTS.put(name, true);
        ContentLog.LOGGER.info("{} called a vote to reset the round", name);
        if (!reset.voteSays().isEmpty()) { Says.tellAll(server, mctmods.resourcedatapackloader.content.card.CardIds.RESET_VOTE, reset.voteSays().replace("{player}", name).replace("{seconds}", Integer.toString(left)), ChatFormatting.GOLD); }
        decide(server);
        return CALLED;
    }

    public static String vote(MinecraftServer server, ServerPlayer who, boolean yes) {
        if (voting == null) { return "No reset vote is running"; }
        String name = who.getGameProfile().getName();
        if (!voters(server).contains(name)) { return "Only players on a side vote"; }
        BALLOTS.put(name, yes);
        decide(server);
        return yes ? "You voted to reset the round" : "You voted to play on";
    }

    public static void second(MinecraftServer server) {
        if (voting == null) { return; }
        if (ContentScoring.noRoundToReset()) {
            ContentLog.LOGGER.info("The round is over, so the vote to reset it is dropped");
            drop();
            return;
        }
        left--;
        decide(server);
        if (voting == null || voting.reset().tallySays().isEmpty()) { return; }
        int[] counted = counted(server);
        ContentPregenHold.tellBar(server, voting.reset().tallySays().replace("{yes}", Integer.toString(counted[0])).replace("{no}", Integer.toString(counted[1])).replace("{seconds}", Integer.toString(left)));
    }

    private static String wayFor(ServerPlayer who, RoundResetDef reset) {
        String side = sideOf(who);
        String asPlayer = reset.callableFrom(side) && (side != null || !ContentTeams.any()) ? RoundResetDef.VOTE : RoundResetDef.NONE;
        if (RoundResetDef.NONE.equals(reset.lead()) || ContentTeams.leadsNoSide(who)) { return asPlayer; }
        return reset.lead();
    }

    @Nullable private static String sideOf(ServerPlayer who) {
        PlayerTeam team = Scores.teamOf(Scores.board(who.server), who.getGameProfile().getName());
        return team == null || ContentTeams.named(team.getName()) == null ? null : team.getName();
    }

    private static List<String> voters(MinecraftServer server) {
        List<String> found = new ArrayList<>();
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (ContentTeams.any() && sideOf(player) == null) { continue; }
            found.add(player.getGameProfile().getName());
        }
        return found;
    }

    private static int[] counted(MinecraftServer server) {
        List<String> voters = voters(server);
        int yes = 0;
        int no = 0;
        for (Map.Entry<String, Boolean> ballot : BALLOTS.entrySet()) {
            if (!voters.contains(ballot.getKey())) { continue; }
            if (ballot.getValue()) { yes++; }
            else { no++; }
        }
        return new int[] { yes, no, voters.size() };
    }

    private static void decide(MinecraftServer server) {
        if (voting == null) { return; }
        ScoreDef def = voting;
        RoundResetDef reset = def.reset();
        int[] counted = counted(server);
        int needed = counted[2] * reset.passPercent();
        if (counted[2] > 0 && counted[0] * 100 >= needed) {
            ContentLog.LOGGER.info("The vote to reset the round passed, {} yes and {} no of {} voter(s)", counted[0], counted[1], counted[2]);
            drop();
            if (!reset.passSays().isEmpty()) { Says.tellAll(server, mctmods.resourcedatapackloader.content.card.CardIds.RESET_PASS, reset.passSays(), ChatFormatting.GOLD); }
            ContentScoring.resetRound(server, def);
            return;
        }
        if (left > 0 && (counted[2] - counted[1]) * 100 >= needed) { return; }
        ContentLog.LOGGER.info("The vote to reset the round failed, {} yes and {} no of {} voter(s)", counted[0], counted[1], counted[2]);
        drop();
        quietUntil = System.currentTimeMillis() + reset.cooldownSeconds() * 1000L;
        if (!reset.failSays().isEmpty()) { Says.tellAll(server, mctmods.resourcedatapackloader.content.card.CardIds.RESET_FAIL, reset.failSays(), ChatFormatting.GRAY); }
    }

    private static void drop() {
        voting = null;
        left = 0;
        BALLOTS.clear();
    }
}
