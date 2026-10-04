package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.board.BoardGame;
import mctmods.resourcedatapackloader.content.board.BoardPiece;
import mctmods.resourcedatapackloader.content.board.BoardRules;
import mctmods.resourcedatapackloader.content.board.BoardSearch;
import mctmods.resourcedatapackloader.content.board.BoardState;
import mctmods.resourcedatapackloader.content.card.CardFire;
import mctmods.resourcedatapackloader.content.card.CardIds;
import mctmods.resourcedatapackloader.content.card.CardLook;
import mctmods.resourcedatapackloader.content.card.CardRules;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Says;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;

public final class ContentBoardPlay {
    private static final int HEARD = 48;
    private static final int THINK_AFTER = 20;
    private static final int DRAW_MARGIN = -100;
    private static final int CHOOSE_TICKS = 200;
    private static final Map<UUID, Picked> PICKED = new LinkedHashMap<>();
    private static final Map<UUID, Choice> CHOOSING = new LinkedHashMap<>();

    private ContentBoardPlay() {}

    static String say(@Nullable EntityPlayer viewer, String key, Object... pairs) { return ContentDice.words(viewer, "board." + key, pairs); }

    static void tell(MinecraftServer server, BoardGame game, TextFormatting color, String key, Object... pairs) {
        for (EntityPlayerMP hearer : hearers(server, game)) { Says.chat(hearer, color, say(hearer, key, pairs)); }
        ContentLog.LOGGER.info("Board {}: {}", game.name, say(null, key, pairs));
    }

    private static Set<EntityPlayerMP> hearers(MinecraftServer server, BoardGame game) {
        Set<EntityPlayerMP> hearers = new LinkedHashSet<>();
        for (EntityPlayerMP player : server.getPlayerList().getPlayers()) {
            boolean near = Integer.toString(player.dimension).equals(game.dimension) && player.getDistanceSq(game.x, game.y, game.z) <= HEARD * HEARD;
            if (near || side(player, game) >= 0) { hearers.add(player); }
        }
        return hearers;
    }

    private static String me(EntityPlayer player) {
        String team = ContentTeams.standingOf(player);
        return team == null ? player.getName() : team;
    }

    static int side(EntityPlayer player, BoardGame game) {
        String me = me(player);
        boolean first = me.equals(game.owners[0]);
        boolean second = me.equals(game.owners[1]);
        if (first && second) { return game.state == null ? 0 : game.state.turn; }
        return first ? 0 : second ? 1 : -1;
    }

    private static String sideName(BoardRules rules, int side) { return rules.sides[side]; }

    private static boolean barred(EntityPlayerMP player, BoardGame game, BoardRules rules, int side) {
        String me = me(player);
        if (me.equals(game.owners[side])) { return false; }
        if (!game.owners[side].isEmpty()) {
            Says.chat(player, TextFormatting.RED, say(player, "notyours", "side", sideName(rules, side), "owner", game.owners[side]));
            return true;
        }
        if (game.levels[side] > 0) {
            Says.chat(player, TextFormatting.RED, say(player, "computer", "side", sideName(rules, side)));
            return true;
        }
        game.owners[side] = me;
        game.teams[side] = ContentTeams.standingOf(player) != null;
        game.version++;
        ContentBoards.dirty(player.server);
        tell(player.server, game, TextFormatting.AQUA, "taken", "player", me, "side", sideName(rules, side), "board", game.name);
        return false;
    }

    public static String start(MinecraftServer server, @Nullable EntityPlayer viewer, String gameName, String name, World world, BlockPos at) {
        ContentBoardData data = ContentBoardData.get(server);
        BoardRules rules = ContentBoards.rules(gameName);
        if (rules == null || data == null) { return say(viewer, "nogame", "game", gameName); }
        if (data.boards.containsKey(name)) { return say(viewer, "exists", "board", name); }
        BoardGame game = new BoardGame(name, gameName, Integer.toString(world.provider.getDimension()), at.getX(), at.getY(), at.getZ());
        game.clock[0] = rules.clockTicks;
        game.clock[1] = rules.clockTicks;
        game.spawn = world.rand.nextLong();
        game.replay(rules);
        game.wait = THINK_AFTER;
        data.boards.put(name, game);
        data.markDirty();
        ContentBoardPieces.squares(world, game, rules);
        ContentBoardPieces.sync(world, game, rules);
        tell(server, game, TextFormatting.GREEN, "started", "game", rules.name, "board", name, "x", at.getX(), "y", at.getY(), "z", at.getZ());
        return "";
    }

    public static String end(MinecraftServer server, @Nullable EntityPlayer viewer, String name) {
        ContentBoardData data = ContentBoardData.get(server);
        BoardGame game = data == null ? null : data.boards.get(name);
        if (game == null) { return say(viewer, "noboard", "board", name); }
        World world = ContentBoards.world(server, game);
        if (world != null) { ContentBoardPieces.clear(world, game); }
        tell(server, game, TextFormatting.GREEN, "ended", "board", name);
        data.boards.remove(name);
        data.markDirty();
        PICKED.values().removeIf(picked -> picked.board.equals(name));
        CHOOSING.values().removeIf(choice -> choice.board.equals(name));
        return "";
    }

    static void clickPiece(EntityPlayerMP player, BoardGame game, Entity piece) {
        moveOn(player);
        BoardRules rules = ContentBoards.rules(game.game);
        BoardState state = game.state;
        if (rules == null || state == null) { return; }
        if (!game.running()) {
            Says.chat(player, TextFormatting.GRAY, say(player, "over", "board", game.name));
            return;
        }
        int square = ContentBoardPieces.square(game, rules, new BlockPos(piece));
        if (square < 0) { return; }
        int side = ContentBoardPieces.sideOf(piece);
        Picked picked = PICKED.get(player.getUniqueID());
        if (picked != null && picked.board.equals(game.name) && BoardState.side(state.at(picked.square)) != side) {
            move(player, game, rules, picked.square, square, -1);
            return;
        }
        pick(player, game, rules, square, side);
    }

    static boolean clickSquare(EntityPlayerMP player, BoardGame game, int square) {
        moveOn(player);
        Picked picked = PICKED.get(player.getUniqueID());
        BoardRules rules = ContentBoards.rules(game.game);
        BoardState state = game.state;
        if (picked == null || !picked.board.equals(game.name) || rules == null || state == null || !game.running()) { return false; }
        int there = state.at(square);
        if (there != 0 && BoardState.side(there) == BoardState.side(state.at(picked.square))) { pick(player, game, rules, square, BoardState.side(there)); }
        else { move(player, game, rules, picked.square, square, -1); }
        return true;
    }

    private static void pick(EntityPlayerMP player, BoardGame game, BoardRules rules, int square, int side) {
        BoardState state = game.state;
        if (state == null || barred(player, game, rules, side)) { return; }
        if (side != state.turn) {
            Says.chat(player, TextFormatting.GRAY, say(player, "notturn", "side", sideName(rules, state.turn)));
            return;
        }
        String piece = rules.pieces.get(BoardState.kind(state.at(square))).name;
        List<String> targets = new ArrayList<>();
        for (int target : state.targets(square)) { targets.add(rules.square(target)); }
        if (targets.isEmpty()) {
            PICKED.remove(player.getUniqueID());
            Says.chat(player, TextFormatting.GRAY, say(player, "stuck", "piece", piece, "square", rules.square(square)));
            return;
        }
        PICKED.put(player.getUniqueID(), new Picked(game.name, square));
        Says.chat(player, TextFormatting.YELLOW, say(player, "picked", "piece", piece, "square", rules.square(square), "targets", String.join(", ", targets)));
    }

    public static void move(EntityPlayerMP player, BoardGame game, BoardRules rules, int from, int to, int promotion) {
        BoardState state = game.state;
        PICKED.remove(player.getUniqueID());
        Choice waiting = CHOOSING.remove(player.getUniqueID());
        if (waiting != null && (promotion < 0 || !waiting.board.equals(game.name) || BoardState.from(waiting.move) != from || BoardState.to(waiting.move) != to)) { settle(player.server, waiting); }
        if (state == null || !game.running()) {
            refuse(player, say(player, "over", "board", game.name));
            return;
        }
        int held = state.at(from);
        if (held == 0 || BoardState.side(held) != state.turn) {
            refuse(player, say(player, "notturn", "side", sideName(rules, state.turn)));
            return;
        }
        if (barred(player, game, rules, state.turn)) { return; }
        int move = state.find(from, to, promotion);
        List<Integer> options = move < 0 || promotion >= 0 ? Collections.emptyList() : promotions(state, move);
        if (move < 0) { refuse(player, say(player, "illegal", "from", rules.square(from), "to", rules.square(to))); }
        else if (options.size() > 1) { offer(player, game, rules, state, options); }
        else { play(player.server, game, rules, move); }
    }

    private static List<Integer> promotions(BoardState state, int move) {
        List<Integer> found = new ArrayList<>();
        for (int legal : state.legal()) {
            if (BoardState.from(legal) == BoardState.from(move) && BoardState.to(legal) == BoardState.to(move) && BoardState.promotion(legal) >= 0) { found.add(legal); }
        }
        return found;
    }

    private static void offer(EntityPlayerMP player, BoardGame game, BoardRules rules, BoardState state, List<Integer> options) {
        int move = options.get(0);
        String from = rules.square(BoardState.from(move));
        String to = rules.square(BoardState.to(move));
        String piece = rules.pieces.get(BoardState.kind(state.at(BoardState.from(move)))).name;
        Map<String, String> commands = new LinkedHashMap<>();
        for (int option : options) {
            String into = rules.pieces.get(BoardState.promotion(option)).name;
            commands.put(into, "/rdplserver game board move " + game.name + " " + from + " " + to + " " + into);
        }
        CHOOSING.put(player.getUniqueID(), new Choice(game.name, move, game.version, player.server.getTickCounter() + CHOOSE_TICKS));
        Says.options(player, TextFormatting.YELLOW, say(player, "choose", "piece", piece, "square", to, "first", rules.pieces.get(BoardState.promotion(move)).name), commands);
        ContentLog.LOGGER.info("Board {}: {} chooses what the {} on {} becomes from {}", game.name, player.getName(), piece, to, String.join(", ", commands.keySet()));
    }

    private static void moveOn(EntityPlayerMP player) {
        Choice waiting = CHOOSING.remove(player.getUniqueID());
        if (waiting != null) { settle(player.server, waiting); }
    }

    static void expire(MinecraftServer server) {
        if (CHOOSING.isEmpty()) { return; }
        List<Choice> due = new ArrayList<>();
        for (Choice choice : CHOOSING.values()) {
            if (server.getTickCounter() >= choice.deadline) { due.add(choice); }
        }
        CHOOSING.values().removeAll(due);
        for (Choice choice : due) { settle(server, choice); }
    }

    private static void settle(MinecraftServer server, Choice waiting) {
        BoardGame game = ContentBoards.board(server, waiting.board);
        BoardRules rules = game == null ? null : ContentBoards.rules(game.game);
        if (game == null || rules == null || game.version != waiting.version || !game.running()) { return; }
        play(server, game, rules, waiting.move);
    }

    private static void refuse(EntityPlayerMP player, String said) {
        Says.chat(player, TextFormatting.RED, said);
        ContentLog.LOGGER.info("Board move refused for {}: {}", player.getName(), said);
    }

    static void answer(MinecraftServer server, BoardGame game, int version, int move) {
        if (game.version != version) { return; }
        game.thinking = false;
        BoardRules rules = ContentBoards.rules(game.game);
        if (rules == null || move < 0 || !game.running()) { return; }
        play(server, game, rules, move);
    }

    private static void play(MinecraftServer server, BoardGame game, BoardRules rules, int move) {
        BoardState state = game.state;
        if (state == null) { return; }
        int side = state.turn;
        int took = state.taken.get(side).size();
        BoardPiece piece = rules.pieces.get(BoardState.kind(state.at(BoardState.from(move))));
        state.play(move);
        game.moves.add(move);
        if (rules.clockTicks > 0 && state.turn != side) { game.clock[side] += rules.addTicks; }
        game.offer = -1;
        game.takeback = -1;
        game.wait = THINK_AFTER;
        game.version++;
        World world = ContentBoards.world(server, game);
        if (world != null) { ContentBoardPieces.sync(world, game, rules); }
        String who = game.owners[side].isEmpty() ? say(null, "ai") : game.owners[side];
        tell(server, game, TextFormatting.WHITE, "moved", "side", sideName(rules, side), "player", who, "piece", piece.name, "from", rules.square(BoardState.from(move)), "to", rules.square(BoardState.to(move)));
        if (BoardState.promotion(move) >= 0) { tell(server, game, TextFormatting.WHITE, "promoted", "side", sideName(rules, side), "piece", piece.name, "to", rules.pieces.get(BoardState.promotion(move)).name); }
        if (state.taken.get(side).size() > took) { tell(server, game, TextFormatting.GOLD, "takes", "side", sideName(rules, side), "piece", rules.pieces.get(state.taken.get(side).get(took)).name); }
        int status = state.status();
        if (status == BoardState.WON) { finish(server, game, rules, 1 - state.turn, state.reason()); }
        else if (status == BoardState.DRAWN) { finish(server, game, rules, BoardGame.DRAW, state.reason()); }
        else if (state.checked(state.turn)) { tell(server, game, TextFormatting.RED, "check", "side", sideName(rules, state.turn)); }
        ContentBoards.dirty(server);
    }

    static void finish(MinecraftServer server, BoardGame game, BoardRules rules, int winner, String reason) {
        game.winner = winner;
        game.reason = reason;
        game.version++;
        ContentBoards.dirty(server);
        ContentLog.LOGGER.info("Board {} is over: {}", game.name, result(null, game, rules));
        for (int side = 0; side < 2; side++) {
            int points = winner == BoardGame.DRAW ? rules.draw : winner == side ? rules.win : rules.loss;
            if (!game.owners[side].isEmpty() && !rules.objective.isEmpty() && points != 0) { ContentScoring.award(server, rules.objective, game.owners[side], points); }
        }
        for (EntityPlayerMP hearer : hearers(server, game)) {
            String said = result(hearer, game, rules);
            if (CardRules.unset(CardIds.BOARD_RESULT)) { Says.chat(hearer, TextFormatting.GOLD, said); }
            else { CardFire.builtin(CardIds.BOARD_RESULT, hearer, CardLook.card(rules.name, Collections.singletonList(said), ItemStack.EMPTY, "", Says.background(), Says.CARD_TICKS)); }
        }
    }

    public static String resign(EntityPlayerMP player, BoardGame game) {
        BoardRules rules = ContentBoards.rules(game.game);
        int side = side(player, game);
        if (rules == null || !game.running()) { return say(player, "over", "board", game.name); }
        if (side < 0) { return say(player, "notseated", "board", game.name); }
        finish(player.server, game, rules, 1 - side, "resign");
        return "";
    }

    public static String draw(EntityPlayerMP player, BoardGame game) {
        BoardRules rules = ContentBoards.rules(game.game);
        BoardState state = game.state;
        int side = side(player, game);
        if (rules == null || state == null || !game.running()) { return say(player, "over", "board", game.name); }
        if (side < 0) { return say(player, "notseated", "board", game.name); }
        int other = 1 - side;
        boolean agreed = game.offer == other || game.owners[other].equals(game.owners[side]);
        if (!agreed && game.computer(other)) {
            if (BoardSearch.judge(state, other) >= DRAW_MARGIN) { return say(player, "declined", "side", sideName(rules, other)); }
            agreed = true;
        }
        if (agreed) {
            finish(player.server, game, rules, BoardGame.DRAW, "agreed");
            return "";
        }
        game.offer = side;
        tell(player.server, game, TextFormatting.AQUA, "offer", "side", sideName(rules, side), "board", game.name);
        return "";
    }

    public static String takeback(EntityPlayerMP player, BoardGame game) {
        BoardRules rules = ContentBoards.rules(game.game);
        int side = side(player, game);
        if (rules == null || !game.running()) { return say(player, "over", "board", game.name); }
        if (side < 0) { return say(player, "notseated", "board", game.name); }
        int other = 1 - side;
        int asker = game.takeback == other ? other : side;
        int back = lastBy(game, rules, asker);
        if (back < 0) { return say(player, "nothingback", "board", game.name); }
        if (asker == side && !game.computer(other) && !game.owners[other].equals(game.owners[side])) {
            game.takeback = side;
            tell(player.server, game, TextFormatting.AQUA, "askback", "side", sideName(rules, side), "board", game.name);
            return "";
        }
        game.moves.subList(back, game.moves.size()).clear();
        BoardState state = game.replay(rules);
        game.offer = -1;
        game.takeback = -1;
        game.thinking = false;
        game.wait = THINK_AFTER * 2;
        game.version++;
        World world = ContentBoards.world(player.server, game);
        if (world != null) { ContentBoardPieces.sync(world, game, rules); }
        ContentBoards.dirty(player.server);
        tell(player.server, game, TextFormatting.AQUA, "tookback", "board", game.name, "side", sideName(rules, state.turn));
        return "";
    }

    private static int lastBy(BoardGame game, BoardRules rules, int side) {
        BoardState replay = new BoardState(rules);
        int last = -1;
        for (int at = 0; at < game.moves.size(); at++) {
            if (replay.turn == side) { last = at; }
            replay.play(game.moves.get(at));
        }
        return last;
    }

    public static String computer(MinecraftServer server, @Nullable EntityPlayer viewer, BoardGame game, String sideName, int level) {
        BoardRules rules = ContentBoards.rules(game.game);
        if (rules == null) { return say(viewer, "nogame", "game", game.game); }
        int side = rules.sides[0].equalsIgnoreCase(sideName) ? 0 : rules.sides[1].equalsIgnoreCase(sideName) ? 1 : -1;
        if (side < 0) { return say(viewer, "noside", "side", sideName, "board", game.name); }
        game.levels[side] = MathHelper.clamp(level, 0, BoardRules.MOST_LEVEL);
        if (game.levels[side] > 0) { game.owners[side] = ""; }
        game.version++;
        game.thinking = false;
        ContentBoards.dirty(server);
        if (game.levels[side] > 0) { tell(server, game, TextFormatting.AQUA, "aion", "side", sideName(rules, side), "board", game.name, "level", game.levels[side]); }
        else { tell(server, game, TextFormatting.AQUA, "aioff", "side", sideName(rules, side), "board", game.name); }
        return "";
    }

    public static List<String> show(@Nullable EntityPlayer viewer, BoardGame game) {
        List<String> lines = new ArrayList<>();
        BoardRules rules = ContentBoards.rules(game.game);
        BoardState state = game.state;
        if (rules == null || state == null) {
            lines.add(say(viewer, "nogame", "game", game.game));
            return lines;
        }
        for (int rank = rules.ranks - 1; rank >= 0; rank--) {
            StringBuilder row = new StringBuilder(String.format("%2d ", rank + 1));
            for (int file = 0; file < rules.files; file++) {
                int held = state.at(rank * rules.files + file);
                char letter = held == 0 ? '.' : rules.pieces.get(BoardState.kind(held)).letter;
                row.append(BoardState.side(held) == 0 && held != 0 ? Character.toUpperCase(letter) : letter).append(' ');
            }
            lines.add(row.toString());
        }
        StringBuilder files = new StringBuilder("   ");
        for (int file = 0; file < rules.files; file++) { files.append((char) ('a' + file)).append(' '); }
        lines.add(files.toString());
        for (int side = 0; side < 2; side++) {
            String holder = game.owners[side].isEmpty() ? game.computer(side) ? say(viewer, "ai") : say(viewer, "open") : game.owners[side];
            String clock = rules.clockTicks > 0 ? String.format(" %d:%02d", game.clock[side] / 1200, game.clock[side] / 20 % 60) : "";
            lines.add(say(viewer, "seat", "side", sideName(rules, side), "player", holder) + clock);
        }
        lines.add(game.running() ? say(viewer, "turn", "side", sideName(rules, state.turn), "ply", game.moves.size()) : say(viewer, "over", "board", game.name));
        return lines;
    }

    public static String state(@Nullable EntityPlayer viewer, BoardGame game) {
        BoardRules rules = ContentBoards.rules(game.game);
        if (rules == null || game.state == null) { return say(viewer, "nogame", "game", game.game); }
        return game.running() ? say(viewer, "turn", "side", sideName(rules, game.state.turn), "ply", game.moves.size()) : result(viewer, game, rules);
    }

    private static String result(@Nullable EntityPlayer viewer, BoardGame game, BoardRules rules) {
        String why = say(viewer, "reason." + game.reason);
        if (game.winner == BoardGame.DRAW) { return say(viewer, "drawn", "board", game.name, "reason", why); }
        return say(viewer, "won", "side", sideName(rules, game.winner), "board", game.name, "reason", why);
    }

    private static final class Picked {
        final String board;
        final int square;

        Picked(String board, int square) {
            this.board = board;
            this.square = square;
        }
    }

    private static final class Choice {
        final String board;
        final int move;
        final int version;
        final int deadline;

        Choice(String board, int move, int version, int deadline) {
            this.board = board;
            this.move = move;
            this.version = version;
            this.deadline = deadline;
        }
    }
}
