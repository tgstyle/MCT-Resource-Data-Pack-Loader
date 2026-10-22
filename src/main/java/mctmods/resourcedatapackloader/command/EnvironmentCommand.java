package mctmods.resourcedatapackloader.command;

import mctmods.resourcedatapackloader.content.def.DimensionDef;
import mctmods.resourcedatapackloader.content.worldgen.ContentDimensions;
import mctmods.resourcedatapackloader.content.worldgen.ContentEnvironments;
import mctmods.resourcedatapackloader.util.Lang;
import mctmods.resourcedatapackloader.util.Settings;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextFormatting;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

final class EnvironmentCommand {
    static final int LEVEL = 2;
    private static final String OFF = "off";

    private EnvironmentCommand() {}

    private static List<String> dimensions() {
        List<String> found = new ArrayList<>();
        for (DimensionDef def : ContentDimensions.all().values()) {
            if (!ContentEnvironments.names(def).isEmpty()) { found.add(String.valueOf(def.id)); }
        }
        return found;
    }

    static List<String> complete(String[] args) {
        if (args.length == 2) { return CommandBase.getListOfStringsMatchingLastWord(args, dimensions()); }
        if (args.length == 4) { return CommandBase.getListOfStringsMatchingLastWord(args, OFF); }
        if (args.length != 3) { return Collections.emptyList(); }
        List<String> names = new ArrayList<>();
        names.add(ContentEnvironments.DEFAULT);
        try { names.addAll(ContentEnvironments.names(ContentDimensions.byId(Integer.parseInt(args[1])))); }
        catch (NumberFormatException ignored) { return Collections.emptyList(); }
        return CommandBase.getListOfStringsMatchingLastWord(args, names);
    }

    static void run(MinecraftServer server, ICommandSender sender, String[] args, String usage) throws CommandException {
        if (args.length == 1) {
            list(sender);
            return;
        }
        if (args.length != 3 && (args.length != 4 || !OFF.equals(Settings.lower(args[3])))) { throw new WrongUsageException(usage); }
        int dimension = CommandBase.parseInt(args[1]);
        String name = Settings.lower(args[2]);
        if (!ContentEnvironments.DEFAULT.equals(name) && !ContentEnvironments.names(ContentDimensions.byId(dimension)).contains(name)) { throw new CommandException(Lang.tr(sender, "rdpl.command.noenvironment", dimension, args[2])); }
        boolean on = args.length == 3;
        ContentEnvironments.set(server, dimension, name, on);
        CommandShared.send(sender, TextFormatting.GREEN, Lang.tr(sender, on ? "rdpl.command.environmentset" : "rdpl.command.environmentoff", dimension, name));
    }

    private static void list(ICommandSender sender) {
        boolean any = false;
        for (DimensionDef def : ContentDimensions.all().values()) {
            Set<String> names = ContentEnvironments.names(def);
            if (names.isEmpty()) { continue; }
            any = true;
            CommandShared.send(sender, TextFormatting.WHITE, Lang.tr(sender, "rdpl.command.environments", def.id, String.join(" + ", ContentEnvironments.active(def.id)), String.join(", ", names)));
        }
        if (!any) { CommandShared.send(sender, TextFormatting.YELLOW, Lang.tr(sender, "rdpl.command.noenvironments")); }
    }
}
