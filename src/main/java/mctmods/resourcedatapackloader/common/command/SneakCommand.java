package mctmods.resourcedatapackloader.common.command;

import mctmods.resourcedatapackloader.common.dimension.ContentEnvironments;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.SneakGrowDef;
import mctmods.resourcedatapackloader.common.rules.ContentSneakGrow;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

final class SneakCommand {
    private SneakCommand() {}

    static LiteralArgumentBuilder<CommandSourceStack> sneak(String name) {
        return Commands.literal("sneak").executes(context -> run(context, name, null))
                .then(Commands.literal("on").executes(context -> run(context, name, false)))
                .then(Commands.literal("off").executes(context -> run(context, name, true)));
    }

    private static int run(CommandContext<CommandSourceStack> context, String name, Boolean off) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        ServerPlayer player = source.getPlayerOrException();
        CommandShared.ran(source, name, off == null ? "sneak" : off ? "sneak off" : "sneak on");
        SneakGrowDef def = ContentEnvironments.growth(player.level(), player.blockPosition()).sneak();
        if (def == null || !def.toggle) {
            CommandShared.send(source, ChatFormatting.YELLOW, CommandShared.tr("rdpl.command.sneakoff"));
            return 0;
        }
        boolean stop = off == null ? !ContentSneakGrow.muted(player) : off;
        ContentSneakGrow.mute(player, stop);
        CommandShared.send(source, ChatFormatting.GREEN, CommandShared.tr(stop ? "rdpl.command.sneakstopped" : "rdpl.command.sneakon"));
        return 1;
    }
}
