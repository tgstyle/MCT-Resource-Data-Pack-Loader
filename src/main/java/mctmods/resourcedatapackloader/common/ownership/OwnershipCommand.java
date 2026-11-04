package mctmods.resourcedatapackloader.common.ownership;

import mctmods.resourcedatapackloader.common.command.CommandShared;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;

public final class OwnershipCommand {
    private static final String DIMENSION = "dimension";

    private OwnershipCommand() {}

    public static LiteralArgumentBuilder<CommandSourceStack> ownership(String name) {
        return Commands.literal("ownership").requires(source -> CommandShared.allowed(source, CommandShared.OPERATOR))
                .then(Commands.literal("sweep").executes(context -> sweep(context, name, true, Level.OVERWORLD.identifier().toString()))
                        .then(Commands.argument(DIMENSION, DimensionArgument.dimension()).executes(context -> sweep(context, name, false, DimensionArgument.getDimension(context, DIMENSION).dimension().identifier().toString()))))
                .then(Commands.literal("status").executes(context -> status(context, name)))
                .then(Commands.literal("cancel").executes(context -> {
                    CommandSourceStack source = context.getSource();
                    CommandShared.ran(source, name, "ownership cancel");
                    OwnershipSweep.cancel(source.getServer());
                    CommandShared.send(source, ChatFormatting.GREEN, CommandShared.tr("rdpl.command.ownershipcancel"));
                    return 1;
                }));
    }

    private static int sweep(CommandContext<CommandSourceStack> context, String name, boolean all, String dimension) {
        CommandSourceStack source = context.getSource();
        MinecraftServer server = source.getServer();
        CommandShared.ran(source, name, all ? "ownership sweep" : "ownership sweep " + dimension);
        if (!OwnershipSettings.any()) {
            CommandShared.send(source, ChatFormatting.YELLOW, CommandShared.tr("rdpl.command.ownershipoff"));
            return 1;
        }
        if (OwnershipSweep.progress(server) != null) { return CommandShared.refuse(source, CommandShared.tr("rdpl.command.ownershiprunning")); }
        OwnershipSweep.Progress progress = OwnershipSweep.start(server, all, dimension);
        if (progress == null) { return CommandShared.refuse(source, CommandShared.tr(all ? "rdpl.command.ownershipnoneall" : "rdpl.command.ownershipnone", dimension)); }
        CommandShared.send(source, ChatFormatting.GREEN, CommandShared.tr(all ? "rdpl.command.ownershipstartall" : "rdpl.command.ownershipstart", progress.dimensions(), progress.dimension(), progress.total()));
        return 1;
    }

    private static int status(CommandContext<CommandSourceStack> context, String name) {
        CommandSourceStack source = context.getSource();
        CommandShared.ran(source, name, "ownership status");
        OwnershipSweep.Progress progress = OwnershipSweep.progress(source.getServer());
        if (progress == null) { CommandShared.send(source, ChatFormatting.YELLOW, CommandShared.tr("rdpl.command.ownershipidle")); }
        else { CommandShared.send(source, ChatFormatting.GREEN, CommandShared.tr(progress.all() ? "rdpl.command.ownershipstatus" : "rdpl.command.ownershipstatusone", progress.dimension(), progress.position(), progress.dimensions(), progress.done(), progress.total())); }
        return 1;
    }
}
