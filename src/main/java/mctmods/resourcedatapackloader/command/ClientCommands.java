package mctmods.resourcedatapackloader.command;

import mctmods.resourcedatapackloader.content.worldgen.ContentLocate;
import mctmods.resourcedatapackloader.content.worldgen.ContentStructureSearch;
import mctmods.resourcedatapackloader.pack.PackManager;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.commands.CommandSourceStack;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class ClientCommands {
    private static final String NAME = "rdpl";
    private static final List<String> FORWARDED = List.of("locate", "goto", "vein");

    private ClientCommands() {}

    public static void register(RegisterClientCommandsEvent event) {
        LiteralArgumentBuilder<CommandSourceStack> tree = CommandShared.tree(NAME, ClientCommands::reload, "rdpl.command.unusednote", "rdpl.command.config.note", false);
        for (String sent : FORWARDED) {
            tree.then(Commands.literal(sent).then(Commands.argument("rest", StringArgumentType.greedyString())
                    .suggests((context, suggestions) -> suggestPlaces(sent, suggestions))
                    .executes(context -> forward(sent + " " + StringArgumentType.getString(context, "rest")))));
        }
        event.getDispatcher().register(tree);
    }

    private static CompletableFuture<Suggestions> suggestPlaces(String sent, SuggestionsBuilder suggestions) {
        if (!"goto".equals(sent) && !"locate".equals(sent)) { return suggestions.buildFuture(); }
        if ("goto".equals(sent)) {
            for (String known : ContentStructureSearch.aliases()) { suggestions.suggest(known); }
        }
        IntegratedServer server = Minecraft.getInstance().getSingleplayerServer();
        LocalPlayer player = Minecraft.getInstance().player;
        if (server == null || player == null) { return suggestions.buildFuture(); }
        ServerLevel level = server.getLevel(player.level().dimension());
        if (level != null) { for (String known : ContentLocate.names(level)) { suggestions.suggest(known); } }
        return suggestions.buildFuture();
    }

    private static int forward(String rest) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) { return 0; }
        player.connection.sendCommand("rdplserver " + rest);
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
