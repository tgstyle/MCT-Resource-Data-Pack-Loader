package mctmods.resourcedatapackloader.common.command;

import mctmods.resourcedatapackloader.common.cards.CardFire;
import mctmods.resourcedatapackloader.common.cards.CardRule;
import mctmods.resourcedatapackloader.common.cards.CardRules;
import mctmods.resourcedatapackloader.core.util.text.Lang;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextFormatting;

final class CardCommand {
    static final int LEVEL = 2;
    private static final Pattern PAIR = Pattern.compile("(\\w+)=(.*)");

    private CardCommand() {}

    static void run(MinecraftServer server, ICommandSender sender, String[] args, String usage) throws CommandException {
        if (args.length < 2) { throw new WrongUsageException(usage); }
        CardRule rule = CardRules.find(args[1]);
        if (rule == null) { throw new CommandException(Lang.tr(sender, "rdpl.command.nocard", args[1])); }
        boolean named = args.length > 2 && !PAIR.matcher(args[2]).matches();
        List<EntityPlayerMP> players = named ? CommandBase.getPlayers(server, sender, args[2]) : Collections.singletonList(CommandShared.goer(sender));
        Map<String, String> given = given(args, named ? 3 : 2, usage);
        for (EntityPlayerMP player : players) { CardFire.show(rule, player, given); }
        CommandShared.send(sender, TextFormatting.GREEN, Lang.tr(sender, "rdpl.command.cardshown", rule.key(), players.size()));
    }

    private static Map<String, String> given(String[] args, int from, String usage) throws WrongUsageException {
        Map<String, String> given = new LinkedHashMap<>();
        String last = null;
        for (int at = from; at < args.length; at++) {
            Matcher pair = PAIR.matcher(args[at]);
            if (pair.matches()) {
                last = pair.group(1);
                given.put(last, pair.group(2));
            } else if (last == null) { throw new WrongUsageException(usage); }
            else { given.put(last, given.get(last) + " " + args[at]); }
        }
        return given;
    }
}
