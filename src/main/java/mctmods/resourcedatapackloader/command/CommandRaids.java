package mctmods.resourcedatapackloader.command;

import mctmods.resourcedatapackloader.content.ContentRaids;
import mctmods.resourcedatapackloader.content.def.RaidDef;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

final class CommandRaids {
    private CommandRaids() {}

    static LiteralArgumentBuilder<CommandSourceStack> raid(String name) {
        return Commands.literal("raid").requires(source -> CommandShared.allowed(source, CommandShared.OPERATOR))
                .then(Commands.argument("raid", IdentifierArgument.id()).suggests((_, suggestions) -> SharedSuggestionProvider.suggest(ContentRaids.keys(), suggestions))
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            return start(context, name, player.level(), player.blockPosition());
                        })
                        .then(Commands.argument("player", EntityArgument.player()).executes(context -> {
                            ServerPlayer player = EntityArgument.getPlayer(context, "player");
                            return start(context, name, player.level(), player.blockPosition());
                        }))
                        .then(Commands.argument("pos", BlockPosArgument.blockPos()).executes(context -> start(context, name, context.getSource().getLevel(), BlockPosArgument.getBlockPos(context, "pos")))));
    }

    private static int start(CommandContext<CommandSourceStack> context, String name, ServerLevel level, BlockPos at) {
        CommandSourceStack source = context.getSource();
        Identifier asked = IdentifierArgument.getId(context, "raid");
        CommandShared.ran(source, name, "raid " + asked);
        RaidDef def = ContentRaids.def(asked);
        if (def == null) { return CommandShared.refuse(source, CommandShared.tr("rdpl.command.noraid", asked.toString())); }
        if (!ContentRaids.begin(level, def, at)) { return CommandShared.refuse(source, CommandShared.tr("rdpl.command.raidbusy", asked.toString())); }
        CommandShared.send(source, ChatFormatting.GREEN, CommandShared.tr("rdpl.command.raidstarted", asked.toString(), at.getX(), at.getY(), at.getZ()));
        return 1;
    }
}
