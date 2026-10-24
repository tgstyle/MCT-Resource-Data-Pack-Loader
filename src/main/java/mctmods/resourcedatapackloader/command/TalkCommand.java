package mctmods.resourcedatapackloader.command;

import mctmods.resourcedatapackloader.content.def.DialogDef;
import mctmods.resourcedatapackloader.content.def.EntityVariantDef;
import mctmods.resourcedatapackloader.content.dialog.ContentDialogs;
import mctmods.resourcedatapackloader.content.dialog.DialogTalk;
import mctmods.resourcedatapackloader.content.entity.ContentEntities;
import mctmods.resourcedatapackloader.util.Lang;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextFormatting;
import java.util.Collections;
import java.util.List;

final class TalkCommand {
    private TalkCommand() {}

    static boolean opens(String[] args) { return args.length > 1 && "open".equals(args[1]); }

    static void run(MinecraftServer server, ICommandSender sender, String[] args, String usage) throws CommandException {
        if (opens(args)) { open(server, sender, args, usage); }
        else if (args.length == 2) { talk(sender, args[1]); }
        else { throw new WrongUsageException(usage); }
    }

    static List<String> complete(MinecraftServer server, String[] args) {
        if (args.length == 2) { return CommandBase.getListOfStringsMatchingLastWord(args, Collections.singletonList("open")); }
        if (!opens(args)) { return Collections.emptyList(); }
        if (args.length == 3) { return CommandBase.getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames()); }
        if (args.length == 4) { return CommandBase.getListOfStringsMatchingLastWord(args, ContentDialogs.keys()); }
        return Collections.emptyList();
    }

    private static void talk(ICommandSender sender, String number) throws CommandException {
        EntityPlayerMP player = CommandShared.goer(sender);
        if (DialogTalk.idle(player)) { throw new CommandException(Lang.tr(sender, "rdpl.command.notalking")); }
        DialogTalk.pick(player, CommandBase.parseInt(number, 1));
    }

    private static void open(MinecraftServer server, ICommandSender sender, String[] args, String usage) throws CommandException {
        if (args.length != 5) { throw new WrongUsageException(usage); }
        ContentDialogs.load();
        DialogDef def = ContentDialogs.find(args[3]);
        if (def == null) { throw new CommandException(Lang.tr(sender, "rdpl.command.nodialog", args[3])); }
        List<EntityPlayerMP> players = CommandBase.getPlayers(server, sender, args[2]);
        Entity npc = CommandBase.getEntity(server, sender, args[4]);
        for (EntityPlayerMP player : players) {
            if (player.world != npc.world || npc.getDistance(player) > DialogTalk.REACH) { throw new CommandException(Lang.tr(sender, "rdpl.command.talkfar", player.getName())); }
        }
        EntityVariantDef variant = ContentEntities.variantOf(npc);
        for (EntityPlayerMP player : players) { DialogTalk.open(player, npc, variant == null ? "" : variant.name, def); }
        CommandShared.send(sender, TextFormatting.GREEN, Lang.tr(sender, "rdpl.command.talkopened", def.key, players.size()));
    }
}
