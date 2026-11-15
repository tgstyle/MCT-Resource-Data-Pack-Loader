package mctmods.resourcedatapackloader.common.command;

import mctmods.resourcedatapackloader.common.pack.definitions.blocks.TreeGrowthDef;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.TreePickDef;
import mctmods.resourcedatapackloader.common.rules.ContentTreeRules;
import mctmods.resourcedatapackloader.common.worldgen.feature.tree.ContentTreeClock;
import mctmods.resourcedatapackloader.common.worldgen.feature.tree.ContentTreeGrowth;
import mctmods.resourcedatapackloader.common.worldgen.feature.tree.ContentTreeStore;
import mctmods.resourcedatapackloader.common.worldgen.feature.tree.TreeEntry;
import mctmods.resourcedatapackloader.common.worldgen.feature.tree.TreeShape;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Locale;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

final class TreeCommand {
    private static final int MOST = 64;
    private static final String POSITION = "pos";
    private static final String COUNT = "count";

    private TreeCommand() {}

    static LiteralArgumentBuilder<CommandSourceStack> tree(String name) {
        return Commands.literal("tree").requires(source -> source.hasPermission(CommandShared.OPERATOR))
                .then(Commands.argument(POSITION, BlockPosArgument.blockPos())
                        .executes(context -> show(context, name))
                        .then(Commands.literal("grow")
                                .executes(context -> grow(context, name, 1))
                                .then(Commands.argument(COUNT, IntegerArgumentType.integer(1, MOST))
                                        .executes(context -> grow(context, name, IntegerArgumentType.getInteger(context, COUNT))))));
    }

    private static int none(CommandSourceStack source, BlockPos at) {
        CommandShared.send(source, ChatFormatting.YELLOW, CommandShared.tr("rdpl.command.notree", at.getX(), at.getY(), at.getZ()));
        return 0;
    }

    private static int show(CommandContext<CommandSourceStack> context, String name) {
        CommandSourceStack source = context.getSource();
        CommandShared.ran(source, name, "tree");
        ServerLevel level = source.getLevel();
        BlockPos at = BlockPosArgument.getBlockPos(context, POSITION);
        TreeEntry entry = ContentTreeStore.get(level, at.asLong());
        if (entry == null) { entry = ContentTreeStore.ownerOf(level, at.asLong()); }
        if (entry == null) { return none(source, at); }
        BlockPos origin = BlockPos.of(entry.pos);
        if (entry.phase == TreeEntry.PHASE_FELLED) {
            CommandShared.send(source, ChatFormatting.YELLOW, CommandShared.tr("rdpl.command.treefelled", entry.rule, origin.getX(), origin.getY(), origin.getZ(), entry.release.length));
            return 1;
        }
        TreeShape shape = entry.phase == 0 ? null : ContentTreeGrowth.shape(level, entry);
        String height = shape == null ? "-" : String.valueOf(shape.height());
        String radius = shape == null ? "-" : String.valueOf(shape.radius());
        String loose = shape == null ? "-" : String.valueOf(shape.looseLogs(ContentTreeGrowth.current(level, entry)));
        String kind = TreePickDef.Kind.values()[entry.kind].name().toLowerCase(Locale.ROOT);
        CommandShared.send(source, ChatFormatting.GREEN, CommandShared.tr("rdpl.command.tree", entry.rule, origin.getX(), origin.getY(), origin.getZ(), kind, entry.source, entry.pick, entry.turn, entry.mirror, entry.step, entry.steps, height, radius, loose));
        TreeGrowthDef growth = ContentTreeRules.byId(entry.rule);
        String mode = growth == null ? "-" : growth.timed() ? "time" : "chance";
        String ticks = growth == null ? "-" : String.valueOf(growth.stepTicks());
        String reason = ContentTreeClock.reason(level, entry.pos);
        CommandShared.send(source, ChatFormatting.GRAY, CommandShared.tr("rdpl.command.treeclock", entry.phase, mode, entry.progress, ticks, level.getGameTime() - entry.started, reason == null ? "-" : reason));
        CommandShared.send(source, ChatFormatting.GRAY, CommandShared.tr("rdpl.command.treeowned", entry.owned.size(), entry.gone.size()));
        return 1;
    }

    private static int grow(CommandContext<CommandSourceStack> context, String name, int count) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        CommandShared.ran(source, name, "tree grow");
        ServerLevel level = source.getLevel();
        BlockPos at = BlockPosArgument.getBlockPos(context, POSITION);
        int run = 0;
        String stopped = null;
        while (run < count) {
            TreeEntry entry = ContentTreeStore.get(level, at.asLong());
            if (entry != null && entry.phase == TreeEntry.PHASE_FELLED) { break; }
            if (entry == null || entry.phase == 0) {
                TreeGrowthDef growth = ContentTreeRules.growth(level.getBlockState(at));
                if (growth == null || !growth.growing()) { break; }
                ContentTreeGrowth.drive(level, at, level.getBlockState(at), level.random);
            }
            else {
                TreeGrowthDef growth = ContentTreeRules.byId(entry.rule);
                if (growth == null) { throw new SimpleCommandExceptionType(CommandShared.tr("rdpl.command.treenorule", entry.rule)).create(); }
                stopped = ContentTreeGrowth.step(level, entry, growth, growth.timed());
                if (stopped != null) { break; }
            }
            run++;
        }
        TreeEntry after = ContentTreeStore.get(level, at.asLong());
        if (run == 0 && stopped == null && after == null) { return none(source, at); }
        CommandShared.send(source, ChatFormatting.GREEN, CommandShared.tr(after == null ? "rdpl.command.treegrown" : "rdpl.command.treestep", run, after == null ? 0 : after.step, after == null ? 0 : after.steps));
        if (stopped != null) { CommandShared.send(source, ChatFormatting.YELLOW, CommandShared.tr("rdpl.command.treestopped", stopped)); }
        return 1;
    }
}
