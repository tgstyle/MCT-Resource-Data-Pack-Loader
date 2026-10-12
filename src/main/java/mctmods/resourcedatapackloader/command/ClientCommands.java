package mctmods.resourcedatapackloader.command;

import mctmods.resourcedatapackloader.content.worldgen.ContentStructureSearch;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.ContentLog;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public final class ClientCommands {
    private static final String NAME = "rdpl";
    private static final String SERVER = "rdplserver";
    private static final List<String> FORWARDED = List.of("oregen", "generators", "gate", "dimensions", "pregen", "intro", "locate", "goto", "vein", "team", "round", "game");

    private ClientCommands() {}

    public static void register(RegisterClientCommandsEvent event) {
        LiteralArgumentBuilder<CommandSourceStack> tree = CommandShared.tree(NAME, ClientCommands::reload, "rdpl.command.unusednote", "rdpl.command.config.note", false);
        for (String sent : FORWARDED) {
            tree.then(Commands.literal(sent).executes(context -> forward(sent))
                    .then(Commands.argument("rest", StringArgumentType.greedyString())
                    .suggests((context, suggestions) -> suggest(sent, suggestions))
                    .executes(context -> forward(sent + " " + StringArgumentType.getString(context, "rest")))));
        }
        tree.then(Commands.literal("biome").then(Commands.literal("find").then(Commands.argument("rest", StringArgumentType.greedyString())
                .suggests((context, suggestions) -> suggest("biome find", suggestions))
                .executes(context -> forward("biome find " + StringArgumentType.getString(context, "rest"))))));
        tree.then(Commands.literal("reload").then(Commands.argument("group", StringArgumentType.word())
                .suggests((context, suggestions) -> SharedSuggestionProvider.suggest(ReloadGroups.names(), suggestions))
                .executes(context -> reloadGroup(context, StringArgumentType.getString(context, "group")))));
        event.getDispatcher().register(tree);
    }

    private static CompletableFuture<Suggestions> suggest(String sent, SuggestionsBuilder suggestions) {
        IntegratedServer server = Minecraft.getInstance().getSingleplayerServer();
        LocalPlayer player = Minecraft.getInstance().player;
        ServerPlayer held = server == null || player == null ? null : server.getPlayerList().getPlayer(player.getUUID());
        if (held == null) { return "goto".equals(sent) ? SharedSuggestionProvider.suggest(ContentStructureSearch.aliases(), suggestions) : suggestions.buildFuture(); }
        String prefix = SERVER + " " + sent + " ";
        String typed = prefix + suggestions.getRemaining();
        int shift = suggestions.getStart() - prefix.length();
        CommandDispatcher<CommandSourceStack> dispatcher = server.getCommands().getDispatcher();
        return CompletableFuture.supplyAsync(() -> dispatcher.parse(typed, held.createCommandSourceStack()), server)
                .thenCompose(dispatcher::getCompletionSuggestions)
                .thenApply(found -> shifted(found, shift, suggestions.getInput()));
    }

    private static Suggestions shifted(Suggestions found, int shift, String input) {
        List<Suggestion> moved = found.getList().stream()
                .map(one -> new Suggestion(StringRange.between(one.getRange().getStart() + shift, one.getRange().getEnd() + shift), one.getText(), one.getTooltip()))
                .toList();
        return Suggestions.create(input, moved);
    }

    private static int forward(String rest) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) { return 0; }
        player.connection.sendCommand(SERVER + " " + rest);
        return 1;
    }

    private static int reloadGroup(CommandContext<CommandSourceStack> context, String typed) {
        CommandSourceStack source = context.getSource();
        String group = typed.toLowerCase(Locale.ROOT);
        CommandShared.ran(source, NAME, "reload " + group);
        if (!ReloadGroups.names().contains(group)) { return CommandShared.refuse(source, CommandShared.tr("rdpl.command.nogroup", group, String.join(", ", ReloadGroups.names()))); }
        long start = System.currentTimeMillis();
        Minecraft minecraft = Minecraft.getInstance();
        ReloadGroups.reload(minecraft, group).whenComplete((ignored, error) -> minecraft.execute(() -> {
            if (error != null) {
                ContentLog.LOGGER.error("/{} reload {} failed: {}", NAME, group, error);
                CommandShared.refuse(source, CommandShared.tr("command.failed"));
                return;
            }
            CommandShared.send(source, ChatFormatting.GREEN, CommandShared.tr("rdpl.command.reloadedgroup", group, CommandShared.elapsed(start)));
            CommandShared.send(source, ChatFormatting.GRAY, CommandShared.tr("rdpl.command.groupnote"));
        }));
        return 1;
    }

    private static int reload(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        CommandShared.ran(source, NAME, "reload");
        long start = System.currentTimeMillis();
        if (CommandShared.rescanFailed(source)) { return 0; }
        Minecraft minecraft = Minecraft.getInstance();
        IntegratedServer server = minecraft.getSingleplayerServer();
        CompletableFuture<Void> done = minecraft.reloadResourcePacks();
        if (server != null) { done = done.thenCompose(finished -> CommandShared.reloadServer(server)); }
        done.thenRun(() -> minecraft.execute(() -> {
            CommandShared.send(source, ChatFormatting.GREEN, CommandShared.tr("rdpl.command.reloaded", PackManager.get().getPacks().size(), CommandShared.elapsed(start)));
            if (server == null) { CommandShared.send(source, ChatFormatting.GRAY, CommandShared.tr("rdpl.command.clientonly")); }
        }));
        return 1;
    }
}
