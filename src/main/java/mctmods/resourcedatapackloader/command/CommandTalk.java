package mctmods.resourcedatapackloader.command;

import mctmods.resourcedatapackloader.content.dialog.DialogTalk;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

final class CommandTalk {
    private CommandTalk() {}

    static LiteralArgumentBuilder<CommandSourceStack> talk(String name) {
        return Commands.literal("talk").then(Commands.argument("number", IntegerArgumentType.integer(1)).executes(context -> pick(context, name)));
    }

    private static int pick(CommandContext<CommandSourceStack> context, String name) {
        CommandSourceStack source = context.getSource();
        int number = IntegerArgumentType.getInteger(context, "number");
        CommandShared.ran(source, name, "talk " + number);
        ServerPlayer player = source.getPlayer();
        if (player == null || DialogTalk.idle(player)) { return CommandShared.refuse(source, CommandShared.tr("rdpl.command.notalking")); }
        DialogTalk.pick(player, number);
        return 1;
    }
}
