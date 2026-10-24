package mctmods.resourcedatapackloader.command;

import mctmods.resourcedatapackloader.content.card.CardRule;
import mctmods.resourcedatapackloader.content.def.PortwayDef;
import mctmods.resourcedatapackloader.content.util.PortwayCosts;
import mctmods.resourcedatapackloader.content.util.PortwayFx;
import mctmods.resourcedatapackloader.content.util.PortwayStop;
import mctmods.resourcedatapackloader.content.util.PortwayTravel;
import mctmods.resourcedatapackloader.content.util.Portways;
import mctmods.resourcedatapackloader.util.Lang;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

final class PortwayCommand {
    static final int LEVEL = 2;
    private static final List<String> ACTIONS = Arrays.asList("list", "grant", "forget", "send", "cooldown");
    private static final int COORDS = 3;
    private static final String ALL = "all";
    private static final String NETWORK = "network";

    private PortwayCommand() {}

    static void run(MinecraftServer server, ICommandSender sender, String[] args, String usage) throws CommandException {
        if (args.length < 3) { throw new WrongUsageException(usage); }
        String action = args[1];
        EntityPlayerMP player = CommandBase.getPlayer(server, sender, args[2]);
        if ("list".equals(action) && args.length == 3) { list(sender, player); }
        else if ("grant".equals(action)) { grant(server, sender, player, args, usage); }
        else if ("forget".equals(action)) { forget(sender, player, args, usage); }
        else if ("send".equals(action)) { send(server, sender, player, args, usage); }
        else if ("cooldown".equals(action) && args.length == 4) { cooldown(sender, player, CommandBase.parseInt(args[3], 0)); }
        else { throw new WrongUsageException(usage); }
    }

    static List<String> complete(MinecraftServer server, String[] args) {
        if (args.length == 2) { return CommandBase.getListOfStringsMatchingLastWord(args, ACTIONS); }
        if (args.length == 3) { return CommandBase.getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames()); }
        if (args.length == 4 && "forget".equals(args[1])) { return CommandBase.getListOfStringsMatchingLastWord(args, Arrays.asList(ALL, NETWORK)); }
        return Collections.emptyList();
    }

    private static PortwayStop stopAt(MinecraftServer server, ICommandSender sender, String[] args, String usage) throws CommandException {
        if (args.length != COORDS + 3 && args.length != COORDS + 4) { throw new WrongUsageException(usage); }
        BlockPos pos = new BlockPos(CommandBase.parseInt(args[COORDS]), CommandBase.parseInt(args[COORDS + 1]), CommandBase.parseInt(args[COORDS + 2]));
        int dimension = args.length == COORDS + 4 ? CommandBase.parseInt(args[COORDS + 3]) : sender.getEntityWorld().provider.getDimension();
        if (!DimensionManager.isDimensionRegistered(dimension)) { throw new CommandException(Lang.tr(sender, "rdpl.command.nodimension", dimension)); }
        World world = server.getWorld(dimension);
        PortwayStop live = Portways.snapshot(world, Portways.base(world, pos));
        if (live == null) { throw new CommandException(Lang.tr(sender, "rdpl.command.portwaymissing", pos.getX() + " " + pos.getY() + " " + pos.getZ() + " " + Portways.dimensionName(dimension))); }
        return live;
    }

    private static void list(ICommandSender sender, EntityPlayerMP player) {
        List<PortwayStop> stops = Portways.stops(player);
        CommandShared.send(sender, TextFormatting.GREEN, Lang.tr(sender, "rdpl.command.portwaylist", player.getName(), stops.size()));
        for (PortwayStop stop : stops) { CommandShared.send(sender, TextFormatting.WHITE, "  " + Portways.label(stop) + TextFormatting.GRAY + "  " + Portways.describe(stop)); }
    }

    private static void grant(MinecraftServer server, ICommandSender sender, EntityPlayerMP player, String[] args, String usage) throws CommandException {
        PortwayStop stop = stopAt(server, sender, args, usage);
        Portways.learn(player, stop);
        CommandShared.send(sender, TextFormatting.GREEN, Lang.tr(sender, "rdpl.command.portwaygrant", player.getName(), Portways.describe(stop)));
    }

    private static void forget(ICommandSender sender, EntityPlayerMP player, String[] args, String usage) throws CommandException {
        List<PortwayStop> removed;
        if (args.length == 4 && ALL.equals(args[3])) { removed = Portways.forget(player, stop -> true); }
        else if (args.length == 5 && NETWORK.equals(args[3])) {
            removed = Portways.forget(player, stop -> {
                PortwayDef def = stop.def();
                return def != null && def.network.equals(args[4]);
            });
        }
        else if (args.length == 6 || args.length == 7) {
            BlockPos pos = new BlockPos(CommandBase.parseInt(args[3]), CommandBase.parseInt(args[4]), CommandBase.parseInt(args[5]));
            int dimension = args.length == 7 ? CommandBase.parseInt(args[6]) : sender.getEntityWorld().provider.getDimension();
            removed = Portways.forget(player, stop -> stop.dimension == dimension && stop.pos.equals(pos));
        }
        else { throw new WrongUsageException(usage); }
        for (PortwayStop stop : removed) { PortwayFx.card(player, CardRule.PORTWAY_FORGET, stop.block, "", "command"); }
        CommandShared.send(sender, TextFormatting.GREEN, Lang.tr(sender, "rdpl.command.portwayforget", player.getName(), removed.size()));
    }

    private static void send(MinecraftServer server, ICommandSender sender, EntityPlayerMP player, String[] args, String usage) throws CommandException {
        PortwayStop stop = stopAt(server, sender, args, usage);
        if (!PortwayTravel.send(player, stop)) { throw new CommandException(Lang.tr(sender, "rdpl.command.portwaynone", player.getName())); }
        CommandShared.send(sender, TextFormatting.GREEN, Lang.tr(sender, "rdpl.command.portwaysend", player.getName(), Portways.describe(stop)));
    }

    private static void cooldown(ICommandSender sender, EntityPlayerMP player, int seconds) {
        PortwayCosts.cooldown(player, seconds);
        if (seconds > 0) { CommandShared.send(sender, TextFormatting.GREEN, Lang.tr(sender, "rdpl.command.portwaycooldown", player.getName(), seconds)); }
        else { CommandShared.send(sender, TextFormatting.GREEN, Lang.tr(sender, "rdpl.command.portwaycleared", player.getName())); }
    }
}
