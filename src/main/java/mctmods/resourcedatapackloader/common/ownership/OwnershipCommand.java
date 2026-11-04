package mctmods.resourcedatapackloader.common.ownership;

import mctmods.resourcedatapackloader.common.command.CommandShared;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
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
                        .then(Commands.argument(DIMENSION, DimensionArgument.dimension()).executes(context -> sweep(context.getSource(), name, level(context)))))
                .then(Commands.literal("status").executes(context -> status(context.getSource(), name)))
                .then(Commands.literal("cancel").executes(context -> cancel(context.getSource(), name)));
    }

    private static ServerLevel level(CommandContext<CommandSourceStack> context) throws CommandSyntaxException { return DimensionArgument.getDimension(context, DIMENSION); }

    private static int sweep(CommandSourceStack source, String name, @Nullable ServerLevel only) {
        String dimension = only == null ? "" : only.dimension().location().toString();
        CommandShared.ran(source, name, "ownership sweep " + dimension);
        if (!OwnershipSettings.any()) {
            CommandShared.send(source, ChatFormatting.YELLOW, CommandShared.tr("rdpl.command.ownershipoff"));
            return 0;
        }
        if (OwnershipSweep.progress(source.getServer()) != null) { return CommandShared.refuse(source, CommandShared.tr("rdpl.command.ownershiprunning")); }
        OwnershipSweep.Progress progress = OwnershipSweep.start(source.getServer(), only);
        if (progress == null) { return CommandShared.refuse(source, only == null ? CommandShared.tr("rdpl.command.ownershipnoneall") : CommandShared.tr("rdpl.command.ownershipnone", dimension)); }
        CommandShared.send(source, ChatFormatting.GREEN, CommandShared.tr(only == null ? "rdpl.command.ownershipstartall" : "rdpl.command.ownershipstart", progress.count(), progress.dimension(), progress.total()));
        return 1;
    }

    private static int status(CommandSourceStack source, String name) {
        CommandShared.ran(source, name, "ownership status");
        OwnershipSweep.Progress progress = OwnershipSweep.progress(source.getServer());
        if (progress == null) {
            CommandShared.send(source, ChatFormatting.YELLOW, CommandShared.tr("rdpl.command.ownershipidle"));
            return 0;
        }
        CommandShared.send(source, ChatFormatting.GREEN, CommandShared.tr(progress.all() ? "rdpl.command.ownershipstatus" : "rdpl.command.ownershipstatusone", progress.dimension(), progress.number(), progress.count(), progress.done(), progress.total()));
        return 1;
    }

    private static int cancel(CommandSourceStack source, String name) {
        CommandShared.ran(source, name, "ownership cancel");
        OwnershipSweep.cancel(source.getServer());
        CommandShared.send(source, ChatFormatting.GREEN, CommandShared.tr("rdpl.command.ownershipcancel"));
        return 1;
    }
}
