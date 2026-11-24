package mctmods.resourcedatapackloader.common.command;

import mctmods.resourcedatapackloader.common.pack.definitions.blocks.TreeGrowthDef;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.TreePickDef;
import mctmods.resourcedatapackloader.common.rules.ContentTreeRules;
import mctmods.resourcedatapackloader.common.worldgen.feature.tree.ContentTreeClock;
import mctmods.resourcedatapackloader.common.worldgen.feature.tree.ContentTreeGrowth;
import mctmods.resourcedatapackloader.common.worldgen.feature.tree.ContentTreeStore;
import mctmods.resourcedatapackloader.common.worldgen.feature.tree.TreeEntry;
import mctmods.resourcedatapackloader.common.worldgen.feature.tree.TreeShape;
import mctmods.resourcedatapackloader.core.util.text.Lang;
import mctmods.resourcedatapackloader.core.util.text.Placeholders;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.WorldServer;

final class TreeCommand {
    private static final int MOST = 64;

    private TreeCommand() {}

    static boolean valid(String[] args) { return args.length == 4 || args.length == 5 && "grow".equals(args[4]) || args.length == 6 && "grow".equals(args[4]); }

    static void run(ICommandSender sender, String[] args) throws CommandException {
        WorldServer world = (WorldServer) sender.getEntityWorld();
        BlockPos at = CommandBase.parseBlockPos(sender, args, 1, false);
        if (args.length == 4) { show(sender, world, at); }
        else { grow(sender, world, at, args.length == 6 ? CommandBase.parseInt(args[5], 1, MOST) : 1); }
    }

    private static void none(ICommandSender sender, BlockPos at) { CommandShared.send(sender, TextFormatting.YELLOW, Lang.tr(sender, "rdpl.command.notree", at.getX(), at.getY(), at.getZ())); }

    private static void show(ICommandSender sender, WorldServer world, BlockPos at) {
        TreeEntry entry = ContentTreeStore.get(world, at.toLong());
        if (entry == null) { entry = ContentTreeStore.ownerOf(world, at.toLong()); }
        if (entry == null) {
            none(sender, at);
            return;
        }
        BlockPos origin = BlockPos.fromLong(entry.pos);
        if (entry.phase == TreeEntry.PHASE_FELLED) {
            CommandShared.send(sender, TextFormatting.YELLOW, Lang.tr(sender, "rdpl.command.treefelled", entry.rule, origin.getX(), origin.getY(), origin.getZ(), entry.release.length));
            return;
        }
        TreeShape shape = entry.phase == 0 ? null : ContentTreeGrowth.shape(world, entry);
        String height = Placeholders.orDash(shape == null ? null : shape.height());
        String radius = Placeholders.orDash(shape == null ? null : shape.radius());
        String loose = Placeholders.orDash(shape == null ? null : shape.looseLogs(ContentTreeGrowth.current(world, entry)));
        String kind = TreePickDef.Kind.values()[entry.kind].name().toLowerCase(Locale.ROOT);
        CommandShared.send(sender, TextFormatting.GREEN, Lang.tr(sender, "rdpl.command.tree", entry.rule, origin.getX(), origin.getY(), origin.getZ(), kind, entry.source, entry.pick, entry.turn, entry.mirror, entry.step, entry.steps, height, radius, loose));
        TreeGrowthDef growth = ContentTreeRules.byId(entry.rule);
        String mode = growth == null ? "-" : growth.timed() ? "time" : "chance";
        String ticks = Placeholders.orDash(growth == null ? null : growth.stepTicks);
        String reason = ContentTreeClock.reason(world, entry.pos);
        CommandShared.send(sender, TextFormatting.GRAY, Lang.tr(sender, "rdpl.command.treeclock", entry.phase, mode, entry.progress, ticks, world.getTotalWorldTime() - entry.started, Placeholders.orDash(reason)));
        CommandShared.send(sender, TextFormatting.GRAY, Lang.tr(sender, "rdpl.command.treeowned", entry.owned.size(), entry.gone.size()));
    }

    private static void grow(ICommandSender sender, WorldServer world, BlockPos at, int count) throws CommandException {
        int run = 0;
        String stopped = null;
        while (run < count) {
            TreeEntry entry = ContentTreeStore.get(world, at.toLong());
            if (entry != null && entry.phase == TreeEntry.PHASE_FELLED) { break; }
            if (entry == null || entry.phase == 0) {
                TreeGrowthDef growth = ContentTreeRules.growth(world.getBlockState(at));
                if (growth == null || !growth.growing()) { break; }
                ContentTreeGrowth.drive(world, at, world.getBlockState(at), world.rand);
            }
            else {
                TreeGrowthDef growth = ContentTreeRules.byId(entry.rule);
                if (growth == null) { throw new WrongUsageException(Lang.tr(sender, "rdpl.command.treenorule", entry.rule)); }
                stopped = ContentTreeGrowth.step(world, entry, growth, growth.timed());
                if (stopped != null) { break; }
            }
            run++;
        }
        TreeEntry after = ContentTreeStore.get(world, at.toLong());
        if (run == 0 && stopped == null && after == null) {
            none(sender, at);
            return;
        }
        CommandShared.send(sender, TextFormatting.GREEN, Lang.tr(sender, after == null ? "rdpl.command.treegrown" : "rdpl.command.treestep", run, after == null ? 0 : after.step, after == null ? 0 : after.steps));
        if (stopped != null) { CommandShared.send(sender, TextFormatting.YELLOW, Lang.tr(sender, "rdpl.command.treestopped", stopped)); }
    }

    static List<String> complete(String[] args, @Nullable BlockPos target) {
        if (args.length >= 2 && args.length <= 4) { return CommandBase.getTabCompletionCoordinate(args, 1, target); }
        return args.length == 5 ? CommandBase.getListOfStringsMatchingLastWord(args, Collections.singletonList("grow")) : Collections.emptyList();
    }
}
