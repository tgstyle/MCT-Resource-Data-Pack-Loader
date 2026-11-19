package mctmods.resourcedatapackloader.common.command;

import mctmods.resourcedatapackloader.common.cards.CardFire;
import mctmods.resourcedatapackloader.common.cards.CardRule;
import mctmods.resourcedatapackloader.common.cards.CardRules;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

final class CommandCards {
    private static final Pattern PAIR = Pattern.compile("(\\w+)=(.*)");

    private CommandCards() {}

    static LiteralArgumentBuilder<CommandSourceStack> card(String name) {
        return Commands.literal("card").requires(source -> CommandShared.allowed(source, CommandShared.MODERATOR))
                .then(Commands.argument("rule", IdentifierArgument.id()).suggests((_, suggestions) -> SharedSuggestionProvider.suggest(CardRules.keys(), suggestions))
                        .executes(context -> show(context, name, ""))
                        .then(Commands.argument("targets", StringArgumentType.greedyString()).executes(context -> show(context, name, StringArgumentType.getString(context, "targets")))));
    }

    private static Collection<ServerPlayer> targets(CommandSourceStack source, String token) throws CommandSyntaxException {
        List<ServerPlayer> players = EntityArgument.players().parse(new StringReader(token), source).findPlayers(source);
        if (players.isEmpty()) { throw EntityArgument.NO_PLAYERS_FOUND.create(); }
        return players;
    }

    private static Map<String, String> given(List<String> tokens) {
        Map<String, String> given = new LinkedHashMap<>();
        String last = null;
        for (String token : tokens) {
            Matcher pair = PAIR.matcher(token);
            if (pair.matches()) {
                last = pair.group(1);
                given.put(last, pair.group(2));
            } else if (last != null) { given.put(last, given.get(last) + " " + token); }
        }
        return given;
    }

    private static int show(CommandContext<CommandSourceStack> context, String name, String rest) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        List<String> tokens = new ArrayList<>(Arrays.asList(rest.trim().split("\\s+")));
        tokens.removeIf(String::isEmpty);
        boolean named = !tokens.isEmpty() && !PAIR.matcher(tokens.getFirst()).matches();
        Collection<ServerPlayer> players = named ? targets(source, tokens.removeFirst()) : List.of(source.getPlayerOrException());
        Map<String, String> args = given(tokens);
        Identifier asked = IdentifierArgument.getId(context, "rule");
        CommandShared.ran(source, name, "card " + asked);
        CardRule rule = CardRules.find(asked.toString());
        if (rule == null) { rule = CardRules.find(asked.getPath()); }
        if (rule == null) { return CommandShared.refuse(source, CommandShared.tr("rdpl.command.nocard", asked.toString())); }
        for (ServerPlayer player : players) { CardFire.show(rule, player, args); }
        CommandShared.send(source, ChatFormatting.GREEN, CommandShared.tr("rdpl.command.cardshown", rule.key(), players.size()));
        return players.size();
    }
}
