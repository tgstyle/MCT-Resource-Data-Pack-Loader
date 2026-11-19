package mctmods.resourcedatapackloader.common.command;

import mctmods.resourcedatapackloader.common.cards.CardFire;
import mctmods.resourcedatapackloader.common.cards.CardRule;
import mctmods.resourcedatapackloader.common.cards.CardRules;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
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
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

final class CommandCards {
    private static final Pattern PAIR = Pattern.compile("(\\w+)=(.*)");
    private CommandCards() {}

    static LiteralArgumentBuilder<CommandSourceStack> card(String name) {
        return Commands.literal("card").requires(source -> source.hasPermission(CommandShared.MODERATOR))
                .then(Commands.argument("rule", ResourceLocationArgument.id()).suggests((context, suggestions) -> SharedSuggestionProvider.suggest(CardRules.keys(), suggestions))
                        .executes(context -> show(context, name, new String[0]))
                        .then(Commands.argument("words", StringArgumentType.greedyString()).executes(context -> show(context, name, StringArgumentType.getString(context, "words").split(" ")))));
    }

    private static Map<String, String> given(String[] tokens, int from) throws CommandSyntaxException {
        Map<String, String> given = new LinkedHashMap<>();
        String last = null;
        for (int at = from; at < tokens.length; at++) {
            String token = tokens[at];
            Matcher pair = PAIR.matcher(token);
            if (pair.matches()) {
                last = pair.group(1);
                given.put(last, pair.group(2));
            } else if (last == null) { throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().create(); }
            else { given.put(last, given.get(last) + " " + token); }
        }
        return given;
    }

    private static int show(CommandContext<CommandSourceStack> context, String name, String[] tokens) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        ResourceLocation asked = ResourceLocationArgument.getId(context, "rule");
        CommandShared.ran(source, name, "card " + asked);
        CardRule rule = CardRules.find(asked.toString());
        if (rule == null) { rule = CardRules.find(asked.getPath()); }
        if (rule == null) { return CommandShared.refuse(source, CommandShared.tr("rdpl.command.nocard", asked.toString())); }
        boolean named = tokens.length > 0 && !PAIR.matcher(tokens[0]).matches();
        Collection<ServerPlayer> players = named ? EntityArgument.players().parse(new StringReader(tokens[0])).findPlayers(source) : List.of(source.getPlayerOrException());
        Map<String, String> given = given(tokens, named ? 1 : 0);
        for (ServerPlayer player : players) { CardFire.show(rule, player, given); }
        CommandShared.send(source, ChatFormatting.GREEN, CommandShared.tr("rdpl.command.cardshown", rule.key(), players.size()));
        return players.size();
    }
}
