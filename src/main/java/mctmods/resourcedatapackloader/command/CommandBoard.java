package mctmods.resourcedatapackloader.command;

import mctmods.resourcedatapackloader.content.ContentBoardPlay;
import mctmods.resourcedatapackloader.content.ContentBoards;
import mctmods.resourcedatapackloader.content.ContentDice;
import mctmods.resourcedatapackloader.content.board.BoardGame;
import mctmods.resourcedatapackloader.content.board.BoardRules;

import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

final class CommandBoard {
    static final List<String> ACTIONS = List.of("list", "start", "end", "show", "move", "resign", "draw", "takeback", "ai");
    private static final List<String> LEVELS = List.of("0", "1", "2", "3", "4");

    private CommandBoard() {}

    private static String say(CommandSourceStack source, String key, Object... pairs) { return ContentDice.words(source.getPlayer(), "board." + key, pairs); }

    private static int refuse(CommandSourceStack source, String text) {
        source.sendFailure(Component.literal(text));
        return 0;
    }

    private static int done(CommandSourceStack source, String refused) { return refused.isEmpty() ? 1 : refuse(source, refused); }

    static int run(CommandSourceStack source, String[] args) {
        if (args.length < 2 || !ACTIONS.contains(args[1])) { return refuse(source, say(source, "usage")); }
        MinecraftServer server = source.getServer();
        String action = args[1];
        if ("list".equals(action)) { return list(server, source); }
        if ("start".equals(action)) { return start(server, source, args); }
        if (args.length < 3) { return refuse(source, say(source, "usage")); }
        String name = args[2];
        if ("end".equals(action)) { return done(source, ContentBoardPlay.end(server, source.getPlayer(), name)); }
        BoardGame game = ContentBoards.board(server, name);
        if (game == null) { return refuse(source, say(source, "noboard", "board", name)); }
        if ("show".equals(action)) {
            for (String line : ContentBoardPlay.show(source.getPlayer(), game)) { CommandShared.send(source, ChatFormatting.GRAY, Component.literal(line)); }
            return 1;
        }
        if ("ai".equals(action)) {
            if (args.length != 5 || !LEVELS.contains(args[4])) { return refuse(source, say(source, "usage")); }
            return done(source, ContentBoardPlay.computer(server, source.getPlayer(), game, args[3], Integer.parseInt(args[4])));
        }
        ServerPlayer player = source.getPlayer();
        if (player == null) { return refuse(source, say(source, "playersonly")); }
        return switch (action) {
            case "move" -> move(source, player, game, args);
            case "resign" -> done(source, ContentBoardPlay.resign(player, game));
            case "draw" -> done(source, ContentBoardPlay.draw(player, game));
            default -> done(source, ContentBoardPlay.takeback(player, game));
        };
    }

    private static int list(MinecraftServer server, CommandSourceStack source) {
        List<String> names = ContentBoards.boardNames(server);
        if (names.isEmpty()) { CommandShared.send(source, ChatFormatting.GRAY, Component.literal(say(source, "listnone"))); }
        for (String name : names) {
            BoardGame game = ContentBoards.board(server, name);
            if (game != null) { CommandShared.send(source, ChatFormatting.GRAY, Component.literal(say(source, "listed", "board", name, "game", game.game, "x", game.x, "y", game.y, "z", game.z, "state", ContentBoardPlay.state(source.getPlayer(), game)))); }
        }
        return 1;
    }

    private static int start(MinecraftServer server, CommandSourceStack source, String[] args) {
        if (args.length != 4 && args.length != 7) { return refuse(source, say(source, "usage")); }
        BlockPos here = BlockPos.containing(source.getPosition());
        BlockPos at = here;
        if (args.length == 7) {
            try { at = new BlockPos(coordinate(args[4], here.getX()), coordinate(args[5], here.getY()), coordinate(args[6], here.getZ())); }
            catch (NumberFormatException notNumber) { return refuse(source, say(source, "usage")); }
        }
        return done(source, ContentBoardPlay.start(server, source.getPlayer(), args[2], args[3], source.getLevel(), at));
    }

    private static int coordinate(String typed, int here) {
        if (!typed.startsWith("~")) { return Integer.parseInt(typed); }
        return typed.length() == 1 ? here : here + Integer.parseInt(typed.substring(1));
    }

    private static int move(CommandSourceStack source, ServerPlayer player, BoardGame game, String[] args) {
        BoardRules rules = ContentBoards.rules(game.game);
        if (rules == null) { return refuse(source, say(source, "nogame", "game", game.game)); }
        if (args.length != 5 && args.length != 6) { return refuse(source, say(source, "usage")); }
        int from = rules.square(args[3]);
        int to = rules.square(args[4]);
        if (from < 0 || to < 0) { return refuse(source, say(source, "badsquare", "square", from < 0 ? args[3] : args[4], "board", game.name)); }
        int promotion = args.length == 6 ? BoardRules.index(rules.pieces, args[5]) : -1;
        if (args.length == 6 && promotion < 0) { return refuse(source, say(source, "nopiece", "piece", args[5])); }
        ContentBoardPlay.move(player, game, rules, from, to, promotion);
        return 1;
    }

    static List<String> offered(MinecraftServer server, List<String> words) {
        if (words.size() == 1) { return ACTIONS; }
        String action = words.get(1);
        if ("start".equals(action)) { return words.size() == 2 ? ContentBoards.gameNames() : new ArrayList<>(); }
        if (words.size() == 2 && !"list".equals(action)) { return ContentBoards.boardNames(server); }
        BoardGame game = words.size() > 2 ? ContentBoards.board(server, words.get(2)) : null;
        BoardRules rules = game == null ? null : ContentBoards.rules(game.game);
        if (rules == null) { return new ArrayList<>(); }
        if ("ai".equals(action) && words.size() == 3) { return Arrays.asList(rules.sides); }
        if ("ai".equals(action) && words.size() == 4) { return LEVELS; }
        if ("move".equals(action) && words.size() == 5) {
            List<String> pieces = new ArrayList<>();
            rules.pieces.forEach(piece -> pieces.add(piece.name));
            return pieces;
        }
        return new ArrayList<>();
    }
}
