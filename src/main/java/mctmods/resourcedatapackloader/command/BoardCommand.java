package mctmods.resourcedatapackloader.command;

import mctmods.resourcedatapackloader.content.ContentBoardPlay;
import mctmods.resourcedatapackloader.content.ContentBoards;
import mctmods.resourcedatapackloader.content.board.BoardGame;
import mctmods.resourcedatapackloader.content.board.BoardRules;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;

final class BoardCommand {
    static final List<String> ACTIONS = Arrays.asList("list", "start", "end", "show", "move", "resign", "draw", "takeback", "ai");
    private static final List<String> LEVELS = Arrays.asList("0", "1", "2", "3", "4");

    private BoardCommand() {}

    @Nullable private static EntityPlayer viewer(ICommandSender sender) { return sender instanceof EntityPlayer ? (EntityPlayer) sender : null; }

    private static String say(ICommandSender sender, String key, Object... pairs) { return mctmods.resourcedatapackloader.content.ContentDice.words(viewer(sender), "board." + key, pairs); }

    static void run(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 3 || !ACTIONS.contains(args[2])) { throw new CommandException(say(sender, "usage")); }
        String action = args[2];
        if ("list".equals(action)) {
            list(server, sender);
            return;
        }
        if ("start".equals(action)) {
            start(server, sender, args);
            return;
        }
        if (args.length < 4) { throw new CommandException(say(sender, "usage")); }
        String name = args[3];
        if ("end".equals(action)) {
            done(ContentBoardPlay.end(server, viewer(sender), name));
            return;
        }
        BoardGame game = ContentBoards.board(server, name);
        if (game == null) { throw new CommandException(say(sender, "noboard", "board", name)); }
        switch (action) {
            case "show":
                for (String line : ContentBoardPlay.show(viewer(sender), game)) { CommandShared.send(sender, TextFormatting.GRAY, line); }
                return;
            case "ai":
                if (args.length != 6) { throw new CommandException(say(sender, "usage")); }
                done(ContentBoardPlay.computer(server, viewer(sender), game, args[4], CommandBase.parseInt(args[5], 0, BoardRules.MOST_LEVEL)));
                return;
            default:
                break;
        }
        if (!(sender instanceof EntityPlayerMP)) { throw new CommandException(say(sender, "playersonly")); }
        EntityPlayerMP player = (EntityPlayerMP) sender;
        switch (action) {
            case "move":
                move(player, game, args);
                return;
            case "resign":
                done(ContentBoardPlay.resign(player, game));
                return;
            case "draw":
                done(ContentBoardPlay.draw(player, game));
                return;
            default:
                done(ContentBoardPlay.takeback(player, game));
        }
    }

    private static void done(String refused) throws CommandException {
        if (!refused.isEmpty()) { throw new CommandException(refused); }
    }

    private static void list(MinecraftServer server, ICommandSender sender) {
        List<String> names = ContentBoards.boardNames(server);
        if (names.isEmpty()) { CommandShared.send(sender, TextFormatting.GRAY, say(sender, "listnone")); }
        for (String name : names) {
            BoardGame game = ContentBoards.board(server, name);
            if (game != null) { CommandShared.send(sender, TextFormatting.GRAY, say(sender, "listed", "board", name, "game", game.game, "x", game.x, "y", game.y, "z", game.z, "state", ContentBoardPlay.state(viewer(sender), game))); }
        }
    }

    private static void start(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length != 5 && args.length != 8) { throw new CommandException(say(sender, "usage")); }
        BlockPos at = sender.getPosition();
        if (args.length == 8) { at = CommandBase.parseBlockPos(sender, args, 5, false); }
        done(ContentBoardPlay.start(server, viewer(sender), args[3], args[4], sender.getEntityWorld(), at));
    }

    private static void move(EntityPlayerMP player, BoardGame game, String[] args) throws CommandException {
        BoardRules rules = ContentBoards.rules(game.game);
        if (rules == null) { throw new CommandException(say(player, "nogame", "game", game.game)); }
        if (args.length != 6 && args.length != 7) { throw new CommandException(say(player, "usage")); }
        int from = rules.square(args[4]);
        int to = rules.square(args[5]);
        if (from < 0 || to < 0) { throw new CommandException(say(player, "badsquare", "square", from < 0 ? args[4] : args[5], "board", game.name)); }
        int promotion = args.length == 7 ? BoardRules.index(rules.pieces, args[6]) : -1;
        if (args.length == 7 && promotion < 0) { throw new CommandException(say(player, "nopiece", "piece", args[6])); }
        ContentBoardPlay.move(player, game, rules, from, to, promotion);
    }

    static List<String> complete(MinecraftServer server, String[] args) {
        if (args.length == 3) { return CommandBase.getListOfStringsMatchingLastWord(args, ACTIONS); }
        String action = args[2];
        if ("start".equals(action)) {
            if (args.length == 4) { return CommandBase.getListOfStringsMatchingLastWord(args, ContentBoards.gameNames()); }
            return new ArrayList<>();
        }
        if (args.length == 4 && !"list".equals(action)) { return CommandBase.getListOfStringsMatchingLastWord(args, ContentBoards.boardNames(server)); }
        BoardGame game = args.length > 4 ? ContentBoards.board(server, args[3]) : null;
        BoardRules rules = game == null ? null : ContentBoards.rules(game.game);
        if (rules == null) { return new ArrayList<>(); }
        if ("ai".equals(action) && args.length == 5) { return CommandBase.getListOfStringsMatchingLastWord(args, Arrays.asList(rules.sides)); }
        if ("ai".equals(action) && args.length == 6) { return CommandBase.getListOfStringsMatchingLastWord(args, LEVELS); }
        if ("move".equals(action) && args.length == 7) {
            List<String> pieces = new ArrayList<>();
            rules.pieces.forEach(piece -> pieces.add(piece.name));
            return CommandBase.getListOfStringsMatchingLastWord(args, pieces);
        }
        return new ArrayList<>();
    }
}
