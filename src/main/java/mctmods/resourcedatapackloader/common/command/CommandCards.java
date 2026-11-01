package mctmods.resourcedatapackloader.common.command;

import mctmods.resourcedatapackloader.common.cards.CardFire;
import mctmods.resourcedatapackloader.common.cards.CardRule;
import mctmods.resourcedatapackloader.common.cards.CardRules;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.Collection;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

final class CommandCards {
    private CommandCards() {}

    static LiteralArgumentBuilder<CommandSourceStack> card(String name) {
        return Commands.literal("card").requires(source -> source.hasPermission(CommandShared.MODERATOR))
                .then(Commands.argument("rule", ResourceLocationArgument.id()).suggests((context, suggestions) -> SharedSuggestionProvider.suggest(CardRules.keys(), suggestions))
                        .executes(context -> show(context, name, List.of(context.getSource().getPlayerOrException())))
                        .then(Commands.argument("targets", EntityArgument.players()).executes(context -> show(context, name, EntityArgument.getPlayers(context, "targets")))));
    }

    private static int show(CommandContext<CommandSourceStack> context, String name, Collection<ServerPlayer> players) {
        CommandSourceStack source = context.getSource();
        ResourceLocation asked = ResourceLocationArgument.getId(context, "rule");
        CommandShared.ran(source, name, "card " + asked);
        CardRule rule = CardRules.find(asked.toString());
        if (rule == null) { rule = CardRules.find(asked.getPath()); }
        if (rule == null) { return CommandShared.refuse(source, CommandShared.tr("rdpl.command.nocard", asked.toString())); }
        for (ServerPlayer player : players) { CardFire.show(rule, player); }
        CommandShared.send(source, ChatFormatting.GREEN, CommandShared.tr("rdpl.command.cardshown", rule.key(), players.size()));
        return players.size();
    }
}
