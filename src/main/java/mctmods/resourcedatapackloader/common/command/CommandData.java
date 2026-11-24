package mctmods.resourcedatapackloader.common.command;

import mctmods.resourcedatapackloader.common.syncdata.SyncData;
import mctmods.resourcedatapackloader.common.syncdata.SyncDataRules;
import mctmods.resourcedatapackloader.common.syncdata.SyncValue;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.server.level.ServerPlayer;

final class CommandData {
    private static final String WORLD = "world";
    private static final String ASKING = "The command /rdplserver";
    private static final List<String> TYPES = List.of("text", "number", "bool");
    private static final List<String> FLAGS = List.of("true", "false");

    private CommandData() {}

    static LiteralArgumentBuilder<CommandSourceStack> data(String name) {
        return Commands.literal("data").requires(source -> CommandShared.allowed(source, CommandShared.MODERATOR))
                .then(Commands.literal("set").then(rest("set").executes(context -> set(context, name))))
                .then(Commands.literal("add").then(rest("add").executes(context -> add(context, name))))
                .then(Commands.literal("remove").then(rest("remove").executes(context -> remove(context, name))))
                .then(Commands.literal("get").then(rest("get").executes(context -> get(context, name))))
                .then(Commands.literal("list").executes(context -> list(context, name, new String[0])).then(rest("list").executes(context -> list(context, name, words(context)))));
    }

    private static RequiredArgumentBuilder<CommandSourceStack, String> rest(String action) {
        return Commands.argument("args", StringArgumentType.greedyString()).suggests((context, builder) -> suggest(context.getSource(), builder, action));
    }

    private static CompletableFuture<Suggestions> suggest(CommandSourceStack source, SuggestionsBuilder builder, String action) {
        String remaining = builder.getRemaining();
        String[] tokens = remaining.split(" ", -1);
        SuggestionsBuilder last = builder.createOffset(builder.getStart() + remaining.lastIndexOf(' ') + 1);
        return SharedSuggestionProvider.suggest(hints(source, action, tokens), last);
    }

    private static List<String> hints(CommandSourceStack source, String action, String[] tokens) {
        int at = tokens.length - 1;
        if (at == 0) {
            List<String> targets = new ArrayList<>(source.getServer().getPlayerList().getPlayers().stream().map(player -> player.getGameProfile().name()).toList());
            targets.add(WORLD);
            return targets;
        }
        if ("list".equals(action)) { return Collections.emptyList(); }
        if (at == 1) { return SyncDataRules.all().stream().map(declared -> declared.name).toList(); }
        if (!"set".equals(action)) { return Collections.emptyList(); }
        if (at == 2) { return SyncDataRules.declared(tokens[1]) == null ? TYPES : FLAGS; }
        return at == 3 && "bool".equals(tokens[2]) ? FLAGS : Collections.emptyList();
    }

    private static String[] words(CommandContext<CommandSourceStack> context) {
        return StringArgumentType.getString(context, "args").split(" ");
    }

    private static String[] words(CommandContext<CommandSourceStack> context, int count) throws CommandSyntaxException {
        String[] words = words(context);
        if (words.length != count) { throw unusable(String.join(" ", words)); }
        return words;
    }

    private static double number(String text) throws CommandSyntaxException {
        double parsed;
        try {
            parsed = Double.parseDouble(text);
        } catch (NumberFormatException exception) {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidDouble().create(text);
        }
        if (Double.isNaN(parsed) || Double.isInfinite(parsed)) { throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidDouble().create(text); }
        return parsed;
    }

    private static boolean bool(String text) throws CommandSyntaxException {
        if ("true".equals(text) || "1".equals(text)) { return true; }
        if ("false".equals(text) || "0".equals(text)) { return false; }
        throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidBool().create(text);
    }

    @Nullable private static ServerPlayer player(CommandSourceStack source, String word) throws CommandSyntaxException {
        if (WORLD.equals(word)) { return null; }
        return new EntitySelectorParser(new StringReader(word), true).parse().findSinglePlayer(source);
    }

    private static String label(@Nullable ServerPlayer target) { return target == null ? WORLD : target.getGameProfile().name(); }

    @Nullable private static SyncValue own(CommandSourceStack source, @Nullable ServerPlayer target, String name) {
        SyncData data = SyncData.peek(source.getLevel());
        if (data == null) { return null; }
        return values(data, target).get(name);
    }

    private static Map<String, SyncValue> values(SyncData data, @Nullable ServerPlayer target) { return target == null ? data.worldValues() : data.playerValues(target.getUUID()); }

    private static int get(CommandContext<CommandSourceStack> context, String command) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        String[] words = words(context, 2);
        ServerPlayer target = player(source, words[0]);
        String name = words[1];
        CommandShared.ran(source, command, "data get " + label(target) + " " + name);
        SyncValue value = target == null ? own(source, null, name) : SyncData.read(target, name);
        if (value == null && target == null) { value = SyncDataRules.defaultOf(name); }
        if (value == null) {
            CommandShared.send(source, ChatFormatting.YELLOW, CommandShared.tr("rdpl.command.dataunset", name, label(target)));
            return 0;
        }
        CommandShared.send(source, ChatFormatting.GREEN, CommandShared.tr("rdpl.command.datashown", name, label(target), value.display));
        return 1;
    }

    private static int remove(CommandContext<CommandSourceStack> context, String command) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        String[] words = words(context, 2);
        ServerPlayer target = player(source, words[0]);
        String name = words[1];
        CommandShared.ran(source, command, "data remove " + label(target) + " " + name);
        boolean removed = SyncData.remove(source.getLevel(), target, name);
        CommandShared.send(source, removed ? ChatFormatting.GREEN : ChatFormatting.YELLOW, CommandShared.tr(removed ? "rdpl.command.dataremoved" : "rdpl.command.dataunset", name, label(target)));
        return removed ? 1 : 0;
    }

    private static int add(CommandContext<CommandSourceStack> context, String command) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        String[] words = words(context, 3);
        ServerPlayer target = player(source, words[0]);
        String name = words[1];
        double amount = number(words[2]);
        CommandShared.ran(source, command, "data add " + label(target) + " " + name + " " + amount);
        SyncValue old = own(source, target, name);
        if (old == null) { old = SyncDataRules.defaultOf(name); }
        if (old != null && old.kind != SyncValue.NUMBER) { return CommandShared.refuse(source, CommandShared.tr("rdpl.command.datarefused", name, label(target))); }
        return store(source, target, name, SyncValue.number((old == null ? 0.0D : old.number) + amount));
    }

    private static int set(CommandContext<CommandSourceStack> context, String command) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        String[] words = words(context);
        if (words.length < 3) { throw unusable(String.join(" ", words)); }
        ServerPlayer target = player(source, words[0]);
        String name = words[1];
        SyncDataRules.Declaration declared = SyncDataRules.declared(name);
        boolean typed = TYPES.contains(words[2]) && words.length >= 4 && (declared == null || SyncValue.kindName(declared.kind).equals(words[2]));
        if (declared == null && !typed) { throw unusable(String.join(" ", words)); }
        String type = typed ? words[2] : SyncValue.kindName(declared.kind);
        String text = String.join(" ", Arrays.copyOfRange(words, typed ? 3 : 2, words.length));
        CommandShared.ran(source, command, "data set " + label(target) + " " + name + " " + text);
        SyncValue value;
        if ("number".equals(type)) { value = SyncValue.number(number(text)); }
        else if ("bool".equals(type)) { value = SyncValue.bool(bool(text)); }
        else { value = SyncValue.text(text); }
        return store(source, target, name, value);
    }

    private static CommandSyntaxException unusable(String text) {
        return CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().createWithContext(new StringReader(text));
    }

    private static int store(CommandSourceStack source, @Nullable ServerPlayer target, String name, SyncValue value) {
        boolean applied = SyncData.set(source.getLevel(), target, name, value.raw(), ASKING);
        if (!applied) { return CommandShared.refuse(source, CommandShared.tr("rdpl.command.datarefused", name, label(target))); }
        CommandShared.send(source, ChatFormatting.GREEN, CommandShared.tr("rdpl.command.dataset", name, value.display, label(target)));
        return 1;
    }

    private static int list(CommandContext<CommandSourceStack> context, String command, String[] words) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        if (words.length > 1) { throw unusable(String.join(" ", words)); }
        String word = words.length == 0 ? WORLD : words[0];
        ServerPlayer target = player(source, word);
        CommandShared.ran(source, command, "data list " + word);
        SyncData data = SyncData.peek(source.getLevel());
        Map<String, SyncValue> values = data == null ? Collections.emptyMap() : values(data, target);
        if (values.isEmpty()) {
            CommandShared.send(source, ChatFormatting.YELLOW, CommandShared.tr("rdpl.command.datanone", label(target)));
            return 0;
        }
        for (Map.Entry<String, SyncValue> entry : values.entrySet()) {
            CommandShared.send(source, ChatFormatting.GREEN, CommandShared.tr("rdpl.command.datashown", entry.getKey(), label(target), entry.getValue().display));
        }
        return values.size();
    }
}
