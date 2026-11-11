package mctmods.resourcedatapackloader.common.command;

import mctmods.resourcedatapackloader.common.dimension.ContentEnvironments;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.SneakGrowDef;
import mctmods.resourcedatapackloader.common.rules.ContentSneakGrow;
import mctmods.resourcedatapackloader.core.util.text.Lang;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextFormatting;

final class SneakCommand {
    private static final List<String> ACTIONS = Arrays.asList("on", "off");

    private SneakCommand() {}

    static boolean valid(String[] args) { return args.length == 1 || args.length == 2 && ACTIONS.contains(args[1]); }

    static void run(ICommandSender sender, String[] args) throws CommandException {
        EntityPlayerMP player = CommandShared.goer(sender);
        SneakGrowDef def = ContentEnvironments.growth(player.world, player.getPosition()).sneak();
        if (def == null || !def.toggle) {
            CommandShared.send(sender, TextFormatting.YELLOW, Lang.tr(sender, "rdpl.command.sneakoff"));
            return;
        }
        boolean muted = args.length == 2 ? "off".equals(args[1]) : !ContentSneakGrow.muted(player);
        ContentSneakGrow.mute(player, muted);
        CommandShared.send(sender, TextFormatting.GREEN, Lang.tr(sender, muted ? "rdpl.command.sneakstopped" : "rdpl.command.sneakon"));
    }

    static List<String> complete(String[] args) { return args.length == 2 ? CommandBase.getListOfStringsMatchingLastWord(args, ACTIONS) : Collections.emptyList(); }
}
