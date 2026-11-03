package mctmods.resourcedatapackloader.common.command;

import mctmods.resourcedatapackloader.common.weights.ContentWeights;
import mctmods.resourcedatapackloader.common.weights.PlayerWeights;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.Locale;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attributes;

final class WeightCommand {
    private WeightCommand() {}

    static LiteralArgumentBuilder<CommandSourceStack> weight(String name) {
        return Commands.literal("weight").requires(source -> source.hasPermission(CommandShared.OPERATOR))
                .executes(context -> run(context, name, context.getSource().getPlayerOrException()))
                .then(Commands.argument("player", EntityArgument.player()).executes(context -> run(context, name, EntityArgument.getPlayer(context, "player"))));
    }

    private static int run(CommandContext<CommandSourceStack> context, String name, ServerPlayer player) {
        CommandSourceStack source = context.getSource();
        CommandShared.ran(source, name, "weight " + player.getGameProfile().getName());
        if (!ContentWeights.any()) {
            CommandShared.send(source, ChatFormatting.YELLOW, CommandShared.tr("rdpl.command.weightoff"));
            return 0;
        }
        PlayerWeights.Entry entry = PlayerWeights.current(player);
        String speed = String.format(Locale.ROOT, "%.4f", player.getAttributeValue(Attributes.MOVEMENT_SPEED));
        CommandShared.send(source, ChatFormatting.GREEN, CommandShared.tr("rdpl.command.weight", player.getGameProfile().getName(), thousandths(entry.total), thousandths(entry.limit), entry.tier + 1, speed));
        return 1;
    }

    private static String thousandths(int value) { return value / 1000 + "." + String.format("%03d", value % 1000); }
}
