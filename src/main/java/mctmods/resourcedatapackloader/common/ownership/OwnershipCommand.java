package mctmods.resourcedatapackloader.common.ownership;

import mctmods.resourcedatapackloader.common.command.CommandShared;
import mctmods.resourcedatapackloader.core.util.text.Lang;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.DimensionManager;

public final class OwnershipCommand {
    private static final List<String> ACTIONS = Arrays.asList("sweep", "status", "cancel");

    private OwnershipCommand() {}

    public static void run(MinecraftServer server, ICommandSender sender, String[] args, String usage) throws CommandException {
        if (args.length < 2 || args.length > 3) { throw new WrongUsageException(usage); }
        String action = args[1];
        if ("sweep".equals(action)) { sweep(server, sender, args); }
        else if (args.length == 2 && "status".equals(action)) { status(server, sender); }
        else if (args.length == 2 && "cancel".equals(action)) {
            OwnershipSweep.cancel(server);
            CommandShared.send(sender, TextFormatting.GREEN, Lang.tr(sender, "rdpl.command.ownershipcancel"));
        }
        else { throw new WrongUsageException(usage); }
    }

    public static List<String> complete(String[] args) {
        if (args.length == 2) { return CommandBase.getListOfStringsMatchingLastWord(args, ACTIONS); }
        if (args.length == 3 && "sweep".equals(args[1])) {
            List<String> dimensions = new ArrayList<>();
            for (Integer dimension : DimensionManager.getStaticDimensionIDs()) { dimensions.add(String.valueOf(dimension)); }
            return CommandBase.getListOfStringsMatchingLastWord(args, dimensions);
        }
        return Collections.emptyList();
    }

    private static void sweep(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (!OwnershipSettings.any()) {
            CommandShared.send(sender, TextFormatting.YELLOW, Lang.tr(sender, "rdpl.command.ownershipoff"));
            return;
        }
        if (OwnershipSweep.progress(server) != null) { throw new CommandException(Lang.tr(sender, "rdpl.command.ownershiprunning")); }
        boolean all = args.length == 2;
        int dimension = all ? 0 : CommandBase.parseInt(args[2]);
        if (!all && !DimensionManager.isDimensionRegistered(dimension)) { throw new CommandException(Lang.tr(sender, "rdpl.command.nodimension", dimension)); }
        int[] progress = OwnershipSweep.start(server, all, dimension);
        if (progress == null) { throw new CommandException(Lang.tr(sender, all ? "rdpl.command.ownershipnoneall" : "rdpl.command.ownershipnone", dimension)); }
        CommandShared.send(sender, TextFormatting.GREEN, Lang.tr(sender, all ? "rdpl.command.ownershipstartall" : "rdpl.command.ownershipstart", progress[4], progress[0], progress[2]));
    }

    private static void status(MinecraftServer server, ICommandSender sender) {
        int[] progress = OwnershipSweep.progress(server);
        if (progress == null) { CommandShared.send(sender, TextFormatting.YELLOW, Lang.tr(sender, "rdpl.command.ownershipidle")); }
        else { CommandShared.send(sender, TextFormatting.GREEN, Lang.tr(sender, progress[5] == 1 ? "rdpl.command.ownershipstatus" : "rdpl.command.ownershipstatusone", progress[0], progress[3], progress[4], progress[1], progress[2])); }
    }
}
