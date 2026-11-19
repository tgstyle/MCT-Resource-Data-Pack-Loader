package mctmods.resourcedatapackloader.common.command;

import mctmods.resourcedatapackloader.common.syncdata.SyncData;
import mctmods.resourcedatapackloader.common.syncdata.SyncDataRules;
import mctmods.resourcedatapackloader.common.syncdata.SyncValue;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.server.level.ServerPlayer;

final class DataCommand {
    private static final String WORLD = "world";
    private static final String ASKING = "The command /rdplserver";
    private static final List<String> ACTIONS = Arrays.asList("set", "add", "remove", "get");
    private static final List<String> TYPES = Arrays.asList("text", "number", "bool");

    private DataCommand() {}

    static LiteralArgumentBuilder<CommandSourceStack> data(String root) {
        LiteralArgumentBuilder<CommandSourceStack> data = Commands.literal("data").requires(source -> source.hasPermission(CommandShared.MODERATOR));
        for (String action : ACTIONS) {
            data.then(Commands.literal(action).then(Commands.argument("args", StringArgumentType.greedyString()).suggests(rest(action))
                    .executes(context -> run(context, root, action))));
        }
        return data.then(Commands.literal("list").executes(context -> list(context, root, ""))
                .then(Commands.argument("args", StringArgumentType.greedyString()).suggests(rest("list"))
                        .executes(context -> list(context, root, StringArgumentType.getString(context, "args")))));
    }

    private static SuggestionProvider<CommandSourceStack> rest(String action) {
        return (context, builder) -> {
            String remaining = builder.getRemaining();
            SuggestionsBuilder word = builder.createOffset(builder.getStart() + remaining.lastIndexOf(' ') + 1);
            String[] before = remaining.split(" ", -1);
            int at = before.length - 1;
            if (at == 0) {
                List<String> targets = new ArrayList<>(context.getSource().getOnlinePlayerNames());
                targets.add(WORLD);
                return SharedSuggestionProvider.suggest(targets, word);
            }
            if (at == 1 && !"list".equals(action)) {
                List<String> names = new ArrayList<>();
                for (SyncDataRules.Declaration declared : SyncDataRules.all()) { names.add(declared.name); }
                return SharedSuggestionProvider.suggest(names, word);
            }
            if (at == 2 && "set".equals(action)) {
                return SharedSuggestionProvider.suggest(SyncDataRules.declared(before[1]) == null ? TYPES : Arrays.asList("true", "false"), word);
            }
            return word.buildFuture();
        };
    }

    @Nullable private static ServerPlayer target(CommandSourceStack source, String token) throws CommandSyntaxException {
        if (WORLD.equals(token)) { return null; }
        return new EntitySelectorParser(new StringReader(token)).parse().findSinglePlayer(source);
    }

    private static String label(@Nullable ServerPlayer target) { return target == null ? WORLD : target.getGameProfile().getName(); }

    private static CommandSyntaxException usage() { return CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().create(); }

    private static int run(CommandContext<CommandSourceStack> context, String root, String action) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        String raw = StringArgumentType.getString(context, "args");
        String[] words = raw.split(" ");
        if (words.length < 2) { throw usage(); }
        ServerPlayer target = target(source, words[0]);
        CommandShared.ran(source, root, "data " + action + " " + raw);
        String name = words[1];
        if ("get".equals(action) && words.length == 2) { return get(source, target, name); }
        if ("remove".equals(action) && words.length == 2) { return remove(source, target, name); }
        if ("add".equals(action) && words.length == 3) { return add(source, target, name, number(words[2])); }
        if ("set".equals(action) && words.length >= 3) { return set(source, target, name, words); }
        throw usage();
    }

    private static double number(String text) throws CommandSyntaxException {
        double value;
        try {
            value = Double.parseDouble(text);
        } catch (NumberFormatException e) {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidDouble().create(text);
        }
        if (!Double.isFinite(value)) { throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidDouble().create(text); }
        return value;
    }

    private static boolean bool(String text) throws CommandSyntaxException {
        if ("true".equals(text) || "1".equals(text)) { return true; }
        if ("false".equals(text) || "0".equals(text)) { return false; }
        throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidBool().create(text);
    }

    @Nullable private static SyncValue own(CommandSourceStack source, @Nullable ServerPlayer target, String name) {
        SyncData data = SyncData.peek(source.getLevel());
        if (data == null) { return null; }
        Map<String, SyncValue> values = target == null ? data.worldValues() : data.playerValues(target.getUUID());
        return values.get(name);
    }

    private static int get(CommandSourceStack source, @Nullable ServerPlayer target, String name) {
        SyncValue value = target == null ? own(source, null, name) : SyncData.read(target, name);
        if (value == null && target == null) { value = SyncDataRules.defaultOf(name); }
        if (value == null) { CommandShared.send(source, ChatFormatting.YELLOW, CommandShared.tr("rdpl.command.dataunset", name, label(target))); }
        else { CommandShared.send(source, ChatFormatting.GREEN, CommandShared.tr("rdpl.command.datashown", name, label(target), value.display)); }
        return 1;
    }

    private static int remove(CommandSourceStack source, @Nullable ServerPlayer target, String name) {
        boolean removed = SyncData.remove(source.getLevel(), target, name);
        CommandShared.send(source, removed ? ChatFormatting.GREEN : ChatFormatting.YELLOW, CommandShared.tr(removed ? "rdpl.command.dataremoved" : "rdpl.command.dataunset", name, label(target)));
        return removed ? 1 : 0;
    }

    private static int add(CommandSourceStack source, @Nullable ServerPlayer target, String name, double amount) {
        SyncValue old = own(source, target, name);
        if (old == null) { old = SyncDataRules.defaultOf(name); }
        if (old != null && old.kind != SyncValue.NUMBER) {
            CommandShared.send(source, ChatFormatting.RED, CommandShared.tr("rdpl.command.datarefused", name, label(target)));
            return 0;
        }
        return store(source, target, name, SyncValue.number((old == null ? 0.0D : old.number) + amount));
    }

    private static int set(CommandSourceStack source, @Nullable ServerPlayer target, String name, String[] words) throws CommandSyntaxException {
        SyncDataRules.Declaration declared = SyncDataRules.declared(name);
        boolean typed = TYPES.contains(words[2]) && words.length >= 4 && (declared == null || SyncValue.kindName(declared.kind).equals(words[2]));
        if (declared == null && !typed) { throw usage(); }
        String type = typed ? words[2] : SyncValue.kindName(declared.kind);
        String text = String.join(" ", Arrays.copyOfRange(words, typed ? 3 : 2, words.length));
        SyncValue value;
        if ("number".equals(type)) { value = SyncValue.number(number(text)); }
        else if ("bool".equals(type)) { value = SyncValue.bool(bool(text)); }
        else { value = SyncValue.text(text); }
        return store(source, target, name, value);
    }

    private static int store(CommandSourceStack source, @Nullable ServerPlayer target, String name, SyncValue value) {
        boolean applied = SyncData.set(source.getLevel(), target, name, value.raw(), ASKING);
        CommandShared.send(source, applied ? ChatFormatting.GREEN : ChatFormatting.RED,
                applied ? CommandShared.tr("rdpl.command.dataset", name, value.display, label(target)) : CommandShared.tr("rdpl.command.datarefused", name, label(target)));
        return applied ? 1 : 0;
    }

    private static int list(CommandContext<CommandSourceStack> context, String root, String raw) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        String[] words = raw.split(" ");
        if (words.length > 1) { throw usage(); }
        String word = raw.isEmpty() ? WORLD : raw;
        ServerPlayer target = target(source, word);
        CommandShared.ran(source, root, "data list " + word);
        SyncData data = SyncData.peek(source.getLevel());
        Map<String, SyncValue> values = data == null ? Collections.emptyMap() : target == null ? data.worldValues() : data.playerValues(target.getUUID());
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
