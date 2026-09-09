package mctmods.resourcedatapackloader.command;

import mctmods.resourcedatapackloader.content.extra.ContentIntroPlay;
import mctmods.resourcedatapackloader.content.worldgen.ContentPregen;
import mctmods.resourcedatapackloader.pack.PackManager;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public final class ServerCommands {
    private static final String NAME = "rdplserver";
    private static final int OPERATOR = 3;

    private ServerCommands() {}

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(CommandShared.tree(NAME, ServerCommands::reload, "rdpl.command.serverunusednote", "rdpl.command.config.servernote", true).requires(source -> source.hasPermission(OPERATOR))
                .then(Commands.literal("pregen")
                        .then(Commands.literal("status").executes(context -> {
                            CommandShared.ran(context.getSource(), NAME, "pregen status");
                            CommandShared.send(context.getSource(), ChatFormatting.GREEN, Component.literal(ContentPregen.state()));
                            return 1;
                        }))
                        .then(Commands.literal("stop").executes(context -> {
                            CommandShared.ran(context.getSource(), NAME, "pregen stop");
                            CommandShared.send(context.getSource(), ChatFormatting.YELLOW, CommandShared.tr(ContentPregen.stop() ? "rdpl.command.pregenstopping" : "rdpl.command.pregennothing"));
                            return 1;
                        }))
                        .then(Commands.argument("radius", IntegerArgumentType.integer(0, 8192)).executes(context -> {
                            CommandSourceStack source = context.getSource();
                            int radius = IntegerArgumentType.getInteger(context, "radius");
                            CommandShared.ran(source, NAME, "pregen " + radius);
                            if (ContentPregen.busy()) {
                                source.sendFailure(CommandShared.tr("rdpl.command.pregenbusy"));
                                return 0;
                            }
                            BlockPos at = BlockPos.containing(source.getPosition());
                            long total = ContentPregen.start(source.getPlayer(), source.getServer(), source.getLevel().dimension(), at.getX() >> 4, at.getZ() >> 4, radius);
                            CommandShared.send(source, ChatFormatting.GREEN, CommandShared.tr("rdpl.command.pregenmaking", total, at.getX() >> 4, at.getZ() >> 4, source.getLevel().dimension().location().toString()));
                            return 1;
                        })))
                .then(Commands.literal("intro").executes(context -> {
                    CommandSourceStack source = context.getSource();
                    CommandShared.ran(source, NAME, "intro");
                    ServerPlayer player = source.getPlayer();
                    if (player == null) { return 0; }
                    ContentIntroPlay.replay(player);
                    CommandShared.send(source, ChatFormatting.GREEN, CommandShared.tr("rdpl.command.introreplay"));
                    return 1;
                })));
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
