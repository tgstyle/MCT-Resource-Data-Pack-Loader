package mctmods.resourcedatapackloader.common.ownership;

import mctmods.resourcedatapackloader.common.command.CommandShared;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.server.level.ServerLevel;

public final class OwnershipCommand {
    private static final String DIMENSION = "dimension";

    private OwnershipCommand() {}

    public static LiteralArgumentBuilder<CommandSourceStack> ownership(String name) {
        return Commands.literal("ownership").requires(source -> source.hasPermission(CommandShared.OPERATOR))
                .then(Commands.literal("sweep").executes(context -> sweep(context.getSource(), name, null))
                        .then(Commands.argument(DIMENSION, DimensionArgument.dimension()).executes(context -> sweep(context.getSource(), name, DimensionArgument.getDimension(context, DIMENSION)))))
                .then(Commands.literal("status").executes(context -> status(context.getSource(), name)))
                .then(Commands.literal("cancel").executes(context -> cancel(context.getSource(), name)));
    }

    private static int sweep(CommandSourceStack source, String name, @Nullable ServerLevel level) {
        String dimension = level == null ? "" : level.dimension().location().toString();
        CommandShared.ran(source, name, "ownership sweep " + dimension);
        if (!OwnershipSettings.any()) {
            CommandShared.send(source, ChatFormatting.YELLOW, CommandShared.tr("rdpl.command.ownershipoff"));
            return 0;
        }
        if (OwnershipSweep.progress(source.getServer()) != null) { return CommandShared.refuse(source, CommandShared.tr("rdpl.command.ownershiprunning")); }
        boolean all = level == null;
        OwnershipSweep.Progress progress = OwnershipSweep.start(source.getServer(), all, dimension);
        if (progress == null) { return CommandShared.refuse(source, CommandShared.tr(all ? "rdpl.command.ownershipnoneall" : "rdpl.command.ownershipnone", dimension)); }
        CommandShared.send(source, ChatFormatting.GREEN, CommandShared.tr(all ? "rdpl.command.ownershipstartall" : "rdpl.command.ownershipstart", progress.dimensions(), progress.dimension(), progress.total()));
        return 1;
    }

    private static int status(CommandSourceStack source, String name) {
        CommandShared.ran(source, name, "ownership status");
        OwnershipSweep.Progress progress = OwnershipSweep.progress(source.getServer());
        if (progress == null) {
            CommandShared.send(source, ChatFormatting.YELLOW, CommandShared.tr("rdpl.command.ownershipidle"));
            return 0;
        }
        CommandShared.send(source, ChatFormatting.GREEN, CommandShared.tr(progress.all() ? "rdpl.command.ownershipstatus" : "rdpl.command.ownershipstatusone", progress.dimension(), progress.index(), progress.dimensions(), progress.done(), progress.total()));
        return 1;
    }

    private static int cancel(CommandSourceStack source, String name) {
        CommandShared.ran(source, name, "ownership cancel");
        OwnershipSweep.cancel(source.getServer());
        CommandShared.send(source, ChatFormatting.GREEN, CommandShared.tr("rdpl.command.ownershipcancel"));
        return 1;
    }
}
