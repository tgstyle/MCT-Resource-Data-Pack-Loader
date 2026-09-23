package mctmods.resourcedatapackloader.util;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public final class Functions {
    private static final int LEVEL = 2;

    private Functions() {}

    public static void run(MinecraftServer server, String named, String asking) { run(server, named, asking, server.createCommandSourceStack()); }

    public static void runAs(ServerPlayer player, String named, String asking) { run(player.server, named, asking, player.createCommandSourceStack().withPermission(LEVEL).withSuppressedOutput()); }

    private static void run(MinecraftServer server, String named, String asking, CommandSourceStack source) {
        ResourceLocation id = ResourceLocation.tryParse(named);
        var held = id == null ? null : server.getFunctions().get(id).orElse(null);
        if (held == null) {
            ContentLog.LOGGER.error("{} asks to run the function {}, which no pack provides, so nothing is run", asking, named);
            return;
        }
        server.getFunctions().execute(held, source);
    }
}
