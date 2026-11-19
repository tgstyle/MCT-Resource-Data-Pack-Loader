package mctmods.resourcedatapackloader.common.command;

import mctmods.resourcedatapackloader.common.cards.CardFire;
import mctmods.resourcedatapackloader.common.cards.CardRule;
import mctmods.resourcedatapackloader.common.cards.CardRules;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
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
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

final class CommandCards {
    private static final Pattern PAIR = Pattern.compile("(\\w+)=(.*)");

    private CommandCards() {}

    static LiteralArgumentBuilder<CommandSourceStack> card(String name) {
        return Commands.literal("card").requires(source -> source.hasPermission(CommandShared.MODERATOR))
                .then(Commands.argument("rule", ResourceLocationArgument.id()).suggests((context, suggestions) -> SharedSuggestionProvider.suggest(CardRules.keys(), suggestions))
                        .executes(context -> show(context, name, ""))
                        .then(Commands.argument("args", StringArgumentType.greedyString()).suggests((context, suggestions) -> SharedSuggestionProvider.suggest(context.getSource().getOnlinePlayerNames(), suggestions))
                                .executes(context -> show(context, name, StringArgumentType.getString(context, "args")))));
    }

    private static int show(CommandContext<CommandSourceStack> context, String name, String rest) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        String[] words = rest.trim().isEmpty() ? new String[0] : rest.trim().split(" +");
        boolean named = words.length > 0 && !PAIR.matcher(words[0]).matches();
        Collection<ServerPlayer> players = named ? players(source, words[0]) : List.of(source.getPlayerOrException());
        Map<String, String> given = given(words, named ? 1 : 0);
        ResourceLocation asked = ResourceLocationArgument.getId(context, "rule");
        CommandShared.ran(source, name, "card " + asked);
        CardRule rule = CardRules.find(asked.toString());
        if (rule == null) { rule = CardRules.find(asked.getPath()); }
        if (rule == null) { return CommandShared.refuse(source, CommandShared.tr("rdpl.command.nocard", asked.toString())); }
        for (ServerPlayer player : players) { CardFire.show(rule, player, given); }
        CommandShared.send(source, ChatFormatting.GREEN, CommandShared.tr("rdpl.command.cardshown", rule.key(), players.size()));
        return players.size();
    }

    private static Collection<ServerPlayer> players(CommandSourceStack source, String token) throws CommandSyntaxException {
        StringReader reader = new StringReader(token);
        EntitySelector selector = EntityArgument.players().parse(reader);
        if (reader.canRead()) { throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherExpectedArgumentSeparator().createWithContext(reader); }
        List<ServerPlayer> found = selector.findPlayers(source);
        if (found.isEmpty()) { throw EntityArgument.NO_PLAYERS_FOUND.create(); }
        return found;
    }

    private static Map<String, String> given(String[] words, int from) {
        Map<String, String> given = new LinkedHashMap<>();
        String last = null;
        for (int at = from; at < words.length; at++) {
            Matcher pair = PAIR.matcher(words[at]);
            if (pair.matches()) {
                last = pair.group(1);
                given.put(last, pair.group(2));
            } else if (last != null) { given.put(last, given.get(last) + " " + words[at]); }
        }
        return given;
    }
}
