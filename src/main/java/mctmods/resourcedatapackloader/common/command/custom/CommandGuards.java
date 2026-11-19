package mctmods.resourcedatapackloader.common.command.custom;

import mctmods.resourcedatapackloader.common.cards.CardWhen;
import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.pack.loading.parsers.CardParser;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Summary;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonObject;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.tree.CommandNode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.CommandEvent;

public final class CommandGuards {
    private static final Set<String> OWN = Set.of("rdpl", "rdplserver");
    private static final String DENIED = "commands.help.failed";
    private static final List<Guard> GUARDS = new ArrayList<>();

    private CommandGuards() {}

    public static void load() {
        GUARDS.clear();
        Json.eachObject(PackManager.COMMAND_GUARDS, "command guard file", CommandGuards::read);
        if (!GUARDS.isEmpty()) { Summary.info("commandguards", "Loaded " + GUARDS.size() + " command guard(s)"); }
    }

    private static void read(Identifier key, JsonObject json) {
        String name = Json.getString(json, "guard", "").trim().toLowerCase(Locale.ROOT);
        if (name.isEmpty()) {
            ContentLog.LOGGER.error("Command guard file {} names no command in guard, ignoring it", key);
            return;
        }
        if (OWN.contains(name)) {
            ContentLog.LOGGER.error("Command guard file {} guards /{}, which no guard may block, ignoring it", key, name);
            return;
        }
        CardWhen when = condition(json, "when", key);
        CardWhen unless = condition(json, "unless", key);
        if (when == null && unless == null) { ContentLog.LOGGER.warn("Command guard file {} has neither unless nor when, so it blocks /{} for everyone", key, name); }
        GUARDS.add(new Guard(key.toString(), name, when, unless, Json.getString(json, "message", "").trim()));
    }

    @Nullable private static CardWhen condition(JsonObject json, String member, Identifier key) {
        if (!json.has(member)) { return null; }
        JsonObject object = Json.getJsonObject(json, member, new JsonObject());
        if (object.isEmpty()) {
            ContentLog.LOGGER.error("Command guard file {} has an empty '{}', ignoring that key", key, member);
            return null;
        }
        return CardParser.when(object, key, "Command guard file");
    }

    public static void onCommand(CommandEvent event) {
        if (GUARDS.isEmpty()) { return; }
        ParseResults<CommandSourceStack> parse = event.getParseResults();
        ServerPlayer player = parse.getContext().getSource().getPlayer();
        if (player == null) { return; }
        List<String> names = namesOf(parse.getContext());
        if (!Collections.disjoint(names, OWN)) { return; }
        for (Guard guard : GUARDS) {
            if (!names.contains(guard.name) || !guard.blocks(player)) { continue; }
            event.setCanceled(true);
            player.sendSystemMessage(guard.text());
            ContentLog.LOGGER.info("Command guard {} stopped /{} for {}", guard.file, guard.name, player.getPlainTextName());
            return;
        }
    }

    private static List<String> namesOf(CommandContextBuilder<CommandSourceStack> context) {
        CommandNode<CommandSourceStack> root = context.getRootNode();
        List<String> names = new ArrayList<>();
        for (CommandContextBuilder<CommandSourceStack> at = context; at != null; at = at.getChild()) {
            if (at.getRootNode() != root || at.getNodes().isEmpty()) { continue; }
            for (CommandNode<CommandSourceStack> node = at.getNodes().getFirst().getNode(); node != null && node != root && !names.contains(node.getName().toLowerCase(Locale.ROOT)); node = node.getRedirect()) { names.add(node.getName().toLowerCase(Locale.ROOT)); }
        }
        for (CommandNode<CommandSourceStack> child : root.getChildren()) {
            CommandNode<CommandSourceStack> target = child.getRedirect();
            if (target != null && target != root && names.contains(target.getName().toLowerCase(Locale.ROOT)) && !names.contains(child.getName().toLowerCase(Locale.ROOT))) { names.add(child.getName().toLowerCase(Locale.ROOT)); }
        }
        return names;
    }

    private record Guard(String file, String name, @Nullable CardWhen when, @Nullable CardWhen unless, String message) {
        private boolean blocks(ServerPlayer player) {
            if (when != null && when.fails(player)) { return false; }
            return unless == null || unless.fails(player);
        }

        private Component text() {
            String said = message.isEmpty() ? DENIED : message;
            boolean key = message.isEmpty() || said.indexOf(' ') < 0 && said.indexOf('.') >= 0;
            MutableComponent text = key ? Component.translatable(said) : Component.literal(said);
            return text.withStyle(ChatFormatting.RED);
        }
    }
}
