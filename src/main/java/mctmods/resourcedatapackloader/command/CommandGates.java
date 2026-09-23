package mctmods.resourcedatapackloader.command;

import mctmods.resourcedatapackloader.content.def.GateDef;
import mctmods.resourcedatapackloader.content.gate.ContentGates;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

final class CommandGates {
    private CommandGates() {}

    static CompletableFuture<Suggestions> suggestGates(SuggestionsBuilder suggestions) {
        List<String> names = new ArrayList<>();
        for (GateDef def : ContentGates.all()) { names.add(def.key().getPath()); }
        return SharedSuggestionProvider.suggest(names, suggestions);
    }

    static int gateList(CommandContext<CommandSourceStack> context, String name) {
        CommandShared.ran(context.getSource(), name, "gate list");
        gates(context.getSource());
        return 1;
    }

    static void gates(CommandSourceStack source) {
        if (ContentGates.idle()) {
            CommandShared.send(source, ChatFormatting.YELLOW, CommandShared.tr("rdpl.command.nogates"));
            return;
        }
        Collection<GateDef> defs = ContentGates.all();
        CommandShared.send(source, ChatFormatting.GREEN, CommandShared.tr("rdpl.command.gates", defs.size()));
        for (GateDef def : defs) {
            CommandShared.send(source, ChatFormatting.WHITE, Component.literal("  " + def.key()
                    + "  dimension=" + def.dimension() + " scope=" + (def.global() ? GateDef.GLOBAL : GateDef.PLAYER)
                    + (def.open() ? Component.translatable("rdpl.command.open").getString() : "")));
        }
    }

    static void blockedReport(CommandSourceStack source, Map<String, Integer> blocked, String none, String some) {
        if (blocked.isEmpty()) {
            CommandShared.send(source, ChatFormatting.YELLOW, CommandShared.tr(none));
            return;
        }
        CommandShared.send(source, ChatFormatting.GREEN, CommandShared.tr(some));
        for (Map.Entry<String, Integer> entry : blocked.entrySet()) { CommandShared.send(source, ChatFormatting.GRAY, Component.literal("  " + entry.getKey() + ": " + entry.getValue())); }
    }

    static void gatesFor(CommandSourceStack source, ServerPlayer player) {
        if (ContentGates.idle()) {
            CommandShared.send(source, ChatFormatting.YELLOW, CommandShared.tr("rdpl.command.nogates"));
            return;
        }
        CommandShared.send(source, ChatFormatting.GREEN, CommandShared.tr("rdpl.command.gatesfor", player.getGameProfile().getName()));
        for (GateDef def : ContentGates.all()) {
            boolean open = ContentGates.unlocked(player, def);
            CommandShared.send(source, open ? ChatFormatting.WHITE : ChatFormatting.GRAY, Component.literal("  " + def.key())
                    .append(Component.translatable(open ? "rdpl.command.open" : "rdpl.command.closed")));
        }
    }

    static int gateFor(CommandContext<CommandSourceStack> context, String name, boolean grant) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        ServerPlayer player = EntityArgument.getPlayer(context, "player");
        String asked = StringArgumentType.getString(context, "gate").trim();
        CommandShared.ran(source, name, "gate " + (grant ? "grant " : "revoke ") + player.getGameProfile().getName() + " " + asked);
        if (ContentGates.idle()) {
            CommandShared.send(source, ChatFormatting.YELLOW, CommandShared.tr("rdpl.command.nogates"));
            return 0;
        }
        GateDef def = ContentGates.find(asked);
        if (def == null) {
            CommandShared.send(source, ChatFormatting.RED, CommandShared.tr("rdpl.command.nogate", asked));
            return 0;
        }
        String scope = def.global() ? GateDef.GLOBAL : GateDef.PLAYER;
        if (grant) { ContentGates.unlock(player, def, false); }
        else { ContentGates.lock(player, def); }
        CommandShared.send(source, ChatFormatting.GREEN, CommandShared.tr(grant ? "rdpl.command.gateopened" : "rdpl.command.gateclosed", def.key(), player.getGameProfile().getName(), scope));
        return 1;
    }
}
