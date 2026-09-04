package mctmods.resourcedatapackloader.command;

import mctmods.resourcedatapackloader.pack.PackManager;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;

public final class ServerCommands {
    private static final String NAME = "rdplserver";
    private static final int OPERATOR = 3;

    private ServerCommands() {}

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(CommandShared.tree(NAME, ServerCommands::reload, "rdpl.command.serverunusednote", "rdpl.command.config.servernote").requires(source -> source.hasPermission(OPERATOR)));
    }

    private static int reload(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        CommandShared.ran(source, NAME, "reload");
        long start = System.currentTimeMillis();
        if (CommandShared.rescanFailed(source)) { return 0; }
        MinecraftServer server = source.getServer();
        CommandShared.reloadServer(server).thenRun(() -> server.execute(() ->
                CommandShared.send(source, ChatFormatting.GREEN, CommandShared.tr("rdpl.command.serverreloaded", PackManager.get().getPacks().size(), CommandShared.elapsed(start)))));
        return 1;
    }
}
