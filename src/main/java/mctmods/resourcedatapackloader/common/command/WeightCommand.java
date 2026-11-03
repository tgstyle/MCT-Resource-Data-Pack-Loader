package mctmods.resourcedatapackloader.common.command;

import mctmods.resourcedatapackloader.common.weights.ContentWeights;
import mctmods.resourcedatapackloader.common.weights.PlayerWeights;
import mctmods.resourcedatapackloader.core.util.text.Lang;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextFormatting;

final class WeightCommand {
    private WeightCommand() {}

    static void run(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (!ContentWeights.any()) {
            CommandShared.send(sender, TextFormatting.YELLOW, Lang.tr(sender, "rdpl.command.weightoff"));
            return;
        }
        EntityPlayerMP player = args.length > 1 ? CommandBase.getPlayer(server, sender, args[1]) : CommandBase.getCommandSenderAsPlayer(sender);
        PlayerWeights.Entry entry = PlayerWeights.current(player);
        String speed = String.format(Locale.ROOT, "%.4f", player.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue());
        CommandShared.send(sender, TextFormatting.GREEN, Lang.tr(sender, "rdpl.command.weight", player.getName(), thousandths(entry.total), thousandths(entry.limit), entry.tier + 1, speed));
    }

    static List<String> complete(MinecraftServer server, String[] args) {
        if (args.length == 2) { return CommandBase.getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames()); }
        return Collections.emptyList();
    }

    private static String thousandths(int value) { return value / 1000 + "." + String.format("%03d", value % 1000); }
}
