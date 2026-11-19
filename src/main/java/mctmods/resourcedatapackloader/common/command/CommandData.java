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
    private static final List<String> TYPES = Arrays.asList("text", "number", "bool");

    private CommandData() {}

    static LiteralArgumentBuilder<CommandSourceStack> data(String name) {
        return Commands.literal("data").requires(source -> source.hasPermission(CommandShared.MODERATOR))
                .then(action("set", name))
                .then(action("add", name))
                .then(action("remove", name))
                .then(action("get", name))
                .then(Commands.literal("list").executes(context -> list(context, name, new String[] {WORLD}))
                        .then(rest("list").executes(context -> list(context, name, words(context)))));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> action(String action, String command) {
        return Commands.literal(action).then(rest(action).executes(context -> run(context, command, action, words(context))));
    }

    private static RequiredArgumentBuilder<CommandSourceStack, String> rest(String action) {
        return Commands.argument("rest", StringArgumentType.greedyString()).suggests((context, suggestions) -> suggest(context, suggestions, action));
    }

    private static String[] words(CommandContext<CommandSourceStack> context) {
        return StringArgumentType.getString(context, "rest").split(" ", -1);
    }

    private static CompletableFuture<Suggestions> suggest(CommandContext<CommandSourceStack> context, SuggestionsBuilder suggestions, String action) {
        String remaining = suggestions.getRemaining();
        int cut = remaining.lastIndexOf(' ');
        String[] done = cut < 0 ? new String[0] : remaining.substring(0, cut).split(" ", -1);
        List<String> options = new ArrayList<>();
        if (done.length == 0) {
            options.addAll(Arrays.asList(context.getSource().getServer().getPlayerNames()));
            options.add(WORLD);
        } else if (done.length == 1 && !"list".equals(action)) {
            for (SyncDataRules.Declaration declared : SyncDataRules.all()) { options.add(declared.name); }
        } else if ("set".equals(action) && done.length == 2) {
            options.addAll(SyncDataRules.declared(done[1]) == null ? TYPES : List.of("true", "false"));
        } else if ("set".equals(action) && done.length == 3 && "bool".equals(done[2])) {
            options.addAll(List.of("true", "false"));
        }
        return SharedSuggestionProvider.suggest(options, suggestions.createOffset(suggestions.getStart() + cut + 1));
    }

    private static int run(CommandContext<CommandSourceStack> context, String command, String action, String[] words) throws CommandSyntaxException {
        boolean fits = switch (action) {
            case "get", "remove" -> words.length == 2;
            case "add" -> words.length == 3;
            default -> words.length >= 3;
        };
        if (!fits) { throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().create(); }
        return switch (action) {
            case "get" -> get(context, command, words);
            case "remove" -> remove(context, command, words);
            case "add" -> add(context, command, words);
            default -> set(context, command, words);
        };
    }

    @Nullable private static ServerPlayer player(CommandSourceStack source, String word) throws CommandSyntaxException {
        if (WORLD.equals(word)) { return null; }
        return new EntitySelectorParser(new StringReader(word), true).parse().findSinglePlayer(source);
    }

    private static String label(@Nullable ServerPlayer target) { return target == null ? WORLD : target.getGameProfile().getName(); }

    @Nullable private static SyncValue own(CommandSourceStack source, @Nullable ServerPlayer target, String name) {
        SyncData data = SyncData.peek(source.getLevel());
        if (data == null) { return null; }
        Map<String, SyncValue> values = target == null ? data.worldValues() : data.playerValues(target.getUUID());
        return values.get(name);
    }

    private static int get(CommandContext<CommandSourceStack> context, String command, String[] words) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        String name = words[1];
        ServerPlayer target = player(source, words[0]);
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

    private static int remove(CommandContext<CommandSourceStack> context, String command, String[] words) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        String name = words[1];
        ServerPlayer target = player(source, words[0]);
        CommandShared.ran(source, command, "data remove " + label(target) + " " + name);
        boolean removed = SyncData.remove(source.getLevel(), target, name);
        CommandShared.send(source, removed ? ChatFormatting.GREEN : ChatFormatting.YELLOW, CommandShared.tr(removed ? "rdpl.command.dataremoved" : "rdpl.command.dataunset", name, label(target)));
        return removed ? 1 : 0;
    }

    private static int add(CommandContext<CommandSourceStack> context, String command, String[] words) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        String name = words[1];
        ServerPlayer target = player(source, words[0]);
        CommandShared.ran(source, command, "data add " + label(target) + " " + name + " " + words[2]);
        double amount = number(words[2]);
        SyncValue old = own(source, target, name);
        if (old == null) { old = SyncDataRules.defaultOf(name); }
        if (old != null && old.kind != SyncValue.NUMBER) {
            CommandShared.send(source, ChatFormatting.RED, CommandShared.tr("rdpl.command.datarefused", name, label(target)));
            return 0;
        }
        return store(source, command, target, name, SyncValue.number((old == null ? 0.0D : old.number) + amount));
    }

    private static int set(CommandContext<CommandSourceStack> context, String command, String[] words) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        String name = words[1];
        ServerPlayer target = player(source, words[0]);
        String[] values = Arrays.copyOfRange(words, 2, words.length);
        CommandShared.ran(source, command, "data set " + label(target) + " " + name + " " + String.join(" ", values));
        SyncDataRules.Declaration declared = SyncDataRules.declared(name);
        boolean typed = TYPES.contains(values[0]) && values.length >= 2 && (declared == null || SyncValue.kindName(declared.kind).equals(values[0]));
        if (declared == null && !typed) { throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().create(); }
        String type = typed ? values[0] : SyncValue.kindName(declared.kind);
        String text = String.join(" ", Arrays.copyOfRange(values, typed ? 1 : 0, values.length));
        SyncValue value;
        if ("number".equals(type)) { value = SyncValue.number(number(text)); }
        else if ("bool".equals(type)) { value = SyncValue.bool(bool(text)); }
        else { value = SyncValue.text(text); }
        return store(source, command, target, name, value);
    }

    private static double number(String text) throws CommandSyntaxException {
        double number;
        try { number = Double.parseDouble(text); }
        catch (NumberFormatException ignored) { throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidDouble().create(text); }
        if (!Double.isNaN(number) && !Double.isInfinite(number)) { return number; }
        throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidDouble().create(text);
    }

    private static boolean bool(String text) throws CommandSyntaxException {
        if ("true".equals(text) || "1".equals(text)) { return true; }
        if ("false".equals(text) || "0".equals(text)) { return false; }
        throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidBool().create(text);
    }

    private static int store(CommandSourceStack source, String command, @Nullable ServerPlayer target, String name, SyncValue value) {
        boolean applied = SyncData.set(source.getLevel(), target, name, value.raw(), "The command /" + command);
        CommandShared.send(source, applied ? ChatFormatting.GREEN : ChatFormatting.RED, applied ? CommandShared.tr("rdpl.command.dataset", name, value.display, label(target)) : CommandShared.tr("rdpl.command.datarefused", name, label(target)));
        return applied ? 1 : 0;
    }

    private static int list(CommandContext<CommandSourceStack> context, String command, String[] words) throws CommandSyntaxException {
        if (words.length != 1) { throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().create(); }
        CommandSourceStack source = context.getSource();
        ServerPlayer target = player(source, words[0]);
        CommandShared.ran(source, command, "data list " + label(target));
        SyncData data = SyncData.peek(source.getLevel());
        Map<String, SyncValue> values = data == null ? Map.of() : target == null ? data.worldValues() : data.playerValues(target.getUUID());
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
