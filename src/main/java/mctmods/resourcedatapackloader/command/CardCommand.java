package mctmods.resourcedatapackloader.command;

import mctmods.resourcedatapackloader.content.card.CardFire;
import mctmods.resourcedatapackloader.content.card.CardRule;
import mctmods.resourcedatapackloader.content.card.CardRules;
import mctmods.resourcedatapackloader.util.Lang;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextFormatting;
import java.util.Collections;
import java.util.List;

final class CardCommand {
    static final int LEVEL = 2;

    private CardCommand() {}

    static void run(MinecraftServer server, ICommandSender sender, String[] args, String usage) throws CommandException {
        if (args.length < 2 || args.length > 3) { throw new WrongUsageException(usage); }
        CardRule rule = CardRules.find(args[1]);
        if (rule == null) { throw new CommandException(Lang.tr(sender, "rdpl.command.nocard", args[1])); }
        List<EntityPlayerMP> players = args.length == 3 ? CommandBase.getPlayers(server, sender, args[2]) : Collections.singletonList(CommandBase.getCommandSenderAsPlayer(sender));
        for (EntityPlayerMP player : players) { CardFire.show(rule, player); }
        CommandShared.send(sender, TextFormatting.GREEN, Lang.tr(sender, "rdpl.command.cardshown", rule.key(), players.size()));
    }
}
