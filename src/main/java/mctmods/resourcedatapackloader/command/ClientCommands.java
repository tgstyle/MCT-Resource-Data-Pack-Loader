package mctmods.resourcedatapackloader.command;

import mctmods.resourcedatapackloader.pack.PackManager;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public final class ClientCommands {
    private static final String NAME = "rdpl";

    private ClientCommands() {}

    public static void register(RegisterClientCommandsEvent event) {
        event.getDispatcher().register(CommandShared.tree(NAME, ClientCommands::reload, "rdpl.command.unusednote", "rdpl.command.config.note"));
    }

    private static int reload(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        CommandShared.ran(source, NAME, "reload");
        Path root = PackManager.get().getRoot();
        if (root == null) {
            source.sendFailure(CommandShared.tr("rdpl.command.noroot"));
            return 0;
        }
        long start = System.currentTimeMillis();
        PackManager.get().scan(root);
        PackManager.get().report();
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
