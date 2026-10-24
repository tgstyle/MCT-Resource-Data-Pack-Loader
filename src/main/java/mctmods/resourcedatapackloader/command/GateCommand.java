package mctmods.resourcedatapackloader.command;

import mctmods.resourcedatapackloader.content.def.GateDef;
import mctmods.resourcedatapackloader.content.gate.ContentGates;
import mctmods.resourcedatapackloader.util.Lang;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextFormatting;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

final class GateCommand {
    private static final List<String> ACTIONS = Arrays.asList("list", "check", "grant", "revoke");

    private GateCommand() {}

    static void run(MinecraftServer server, ICommandSender sender, String[] args, String usage) throws CommandException {
        if (!ContentGates.enabled()) {
            CommandShared.send(sender, TextFormatting.YELLOW, Lang.tr(sender, "rdpl.command.nogates"));
            return;
        }
        String action = args.length < 2 ? "list" : args[1];
        if ("list".equals(action)) {
            list(sender);
            return;
        }
        if (args.length < 3) { throw new WrongUsageException(usage); }
        EntityPlayerMP player = CommandBase.getPlayer(server, sender, args[2]);
        if ("check".equals(action)) {
            check(sender, player);
            return;
        }
        if (args.length < 4) { throw new WrongUsageException(usage); }
        GateDef def = ContentGates.find(args[3]);
        if (def == null) { throw new CommandException(Lang.tr(sender, "rdpl.command.nogate", args[3])); }
        if ("grant".equals(action)) {
            ContentGates.unlock(player, def, false);
            CommandShared.send(sender, TextFormatting.GREEN, Lang.tr(sender, "rdpl.command.gateopened", def.getKey(), player.getName(), def.scope));
        }
        else if ("revoke".equals(action)) {
            ContentGates.lock(player, def);
            CommandShared.send(sender, TextFormatting.GREEN, Lang.tr(sender, "rdpl.command.gateclosed", def.getKey(), player.getName(), def.scope));
        }
        else { throw new WrongUsageException(usage); }
    }

    static List<String> complete(MinecraftServer server, String[] args) {
        if (args.length == 2) { return CommandBase.getListOfStringsMatchingLastWord(args, ACTIONS); }
        if (args.length == 3 && !"list".equals(args[1])) { return CommandBase.getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames()); }
        if (args.length == 4) { return CommandBase.getListOfStringsMatchingLastWord(args, names()); }
        return Collections.emptyList();
    }

    private static void list(ICommandSender sender) {
        CommandShared.send(sender, TextFormatting.GREEN, Lang.tr(sender, "rdpl.command.gates", ContentGates.all().size()));
        for (GateDef def : ContentGates.all().values()) {
            String dimension = def.guardsDimension() ? String.valueOf(def.dimension) : "-";
            CommandShared.send(sender, TextFormatting.WHITE, "  " + def.getKey() + TextFormatting.GRAY + "  dimension=" + dimension + " scope=" + def.scope + (def.open ? Lang.tr(sender, "rdpl.command.open") : ""));
        }
    }

    private static void check(ICommandSender sender, EntityPlayerMP player) {
        CommandShared.send(sender, TextFormatting.GREEN, Lang.tr(sender, "rdpl.command.gatesfor", player.getName()));
        for (GateDef def : ContentGates.all().values()) {
            boolean unlocked = ContentGates.unlocked(player, def);
            CommandShared.send(sender, unlocked ? TextFormatting.WHITE : TextFormatting.GRAY, "  " + def.getKey() + Lang.tr(sender, unlocked ? "rdpl.command.open" : "rdpl.command.closed"));
        }
    }

    private static List<String> names() {
        List<String> names = new ArrayList<>();
        for (GateDef def : ContentGates.all().values()) { names.add(def.registryName.getPath()); }
        return names;
    }
}
