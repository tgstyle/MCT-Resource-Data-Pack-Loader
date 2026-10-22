package mctmods.resourcedatapackloader.command;

import mctmods.resourcedatapackloader.content.ContentRaids;
import mctmods.resourcedatapackloader.content.def.RaidDef;
import mctmods.resourcedatapackloader.util.Lang;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.WorldServer;

final class RaidCommand {
    private RaidCommand() {}

    static void run(MinecraftServer server, ICommandSender sender, String[] args, String usage) throws CommandException {
        if (args.length != 2 && args.length != 3 && args.length != 5) { throw new WrongUsageException(usage); }
        RaidDef def = ContentRaids.def(new ResourceLocation(args[1]));
        if (def == null) { throw new CommandException(Lang.tr(sender, "rdpl.command.noraid", args[1])); }
        WorldServer world;
        BlockPos at;
        if (args.length == 5) {
            world = (WorldServer) sender.getEntityWorld();
            at = CommandBase.parseBlockPos(sender, args, 2, false);
        }
        else {
            EntityPlayerMP player = args.length == 3 ? CommandBase.getPlayer(server, sender, args[2]) : CommandShared.goer(sender);
            world = player.getServerWorld();
            at = new BlockPos(player);
        }
        if (!ContentRaids.begin(world, def, at)) { throw new CommandException(Lang.tr(sender, "rdpl.command.raidbusy", def.registryName.toString())); }
        CommandShared.send(sender, TextFormatting.GREEN, Lang.tr(sender, "rdpl.command.raidstarted", def.registryName.toString(), at.getX(), at.getY(), at.getZ()));
    }
}
