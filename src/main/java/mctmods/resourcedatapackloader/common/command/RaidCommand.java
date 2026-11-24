package mctmods.resourcedatapackloader.common.command;

import mctmods.resourcedatapackloader.common.pack.definitions.villages.RaidDef;
import mctmods.resourcedatapackloader.common.raids.ContentRaids;
import mctmods.resourcedatapackloader.common.raids.RaidStorage;
import mctmods.resourcedatapackloader.common.raids.RaidTarget;
import mctmods.resourcedatapackloader.common.raids.RaidTargets;
import mctmods.resourcedatapackloader.common.raids.base.RaidOwner;
import mctmods.resourcedatapackloader.core.util.text.Lang;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
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
        if (args.length < 2 || args.length > 5) { throw new WrongUsageException(usage); }
        RaidDef def = ContentRaids.def(new ResourceLocation(args[1]));
        if (def == null) { throw new CommandException(Lang.tr(sender, "rdpl.command.noraid", args[1])); }
        if (args.length == 5) {
            WorldServer world = (WorldServer) sender.getEntityWorld();
            BlockPos at = CommandBase.parseBlockPos(sender, args, 2, false);
            if (!ContentRaids.begin(world, def, at)) { throw new CommandException(Lang.tr(sender, "rdpl.command.raidbusy", def.registryName.toString())); }
            CommandShared.send(sender, TextFormatting.GREEN, Lang.tr(sender, "rdpl.command.raidstarted", def.registryName.toString(), at.getX(), at.getY(), at.getZ()));
            return;
        }
        RaidTarget.Kind kind = args.length >= 3 ? forced(args[2]) : null;
        if (args.length == 4 && kind == null || args.length == 3 && kind == RaidTarget.Kind.PLAYER) { throw new WrongUsageException(usage); }
        String named = args.length == 4 ? args[3] : args.length == 3 && kind == null ? args[2] : null;
        EntityPlayerMP player = CommandShared.target(server, sender, named);
        WorldServer world = player.getServerWorld();
        if (kind != null && RaidStorage.get(world).ownerRaid(RaidOwner.of(player)) != null) { throw new CommandException(Lang.tr(sender, "rdpl.command.raidownerbusy", def.registryName.toString(), player.getName())); }
        RaidTarget target = kind == null ? RaidTargets.resolve(world, player, def) : RaidTargets.resolve(world, player, def, kind);
        if (target == null) { throw new CommandException(Lang.tr(sender, kind == null ? "rdpl.command.raidnotarget" : "rdpl.command.raidnobase", def.registryName.toString(), player.getName())); }
        if (!RaidTargets.launch(world, player, def, target)) { throw new CommandException(Lang.tr(sender, "rdpl.command.raidownerbusy", def.registryName.toString(), player.getName())); }
        BlockPos at = target.center();
        CommandShared.send(sender, TextFormatting.GREEN, Lang.tr(sender, "rdpl.command.raidstarted", def.registryName.toString(), at.getX(), at.getY(), at.getZ()));
    }

    static List<String> complete(MinecraftServer server, String[] args) {
        if (args.length == 2) { return CommandBase.getListOfStringsMatchingLastWord(args, ContentRaids.keys()); }
        if (args.length == 3) {
            List<String> options = new ArrayList<>(Arrays.asList("base", "player"));
            options.addAll(Arrays.asList(server.getOnlinePlayerNames()));
            return CommandBase.getListOfStringsMatchingLastWord(args, options);
        }
        if (args.length == 4 && forced(args[2]) != null) { return CommandBase.getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames()); }
        return Collections.emptyList();
    }

    @Nullable private static RaidTarget.Kind forced(String word) {
        if ("base".equals(word)) { return RaidTarget.Kind.BASE; }
        return "player".equals(word) ? RaidTarget.Kind.PLAYER : null;
    }
}
