package mctmods.resourcedatapackloader.util;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.LevelBasedPermissionSet;
import net.minecraft.server.permissions.PermissionSet;

public final class Functions {
    private static final PermissionSet LEVEL = LevelBasedPermissionSet.GAMEMASTER;

    private Functions() {}

    public static void run(MinecraftServer server, String named, String asking) { run(server, named, asking, server.createCommandSourceStack()); }

    public static void runAs(ServerPlayer player, String named, String asking) { run(player.level().getServer(), named, asking, player.createCommandSourceStack().withPermission(LEVEL).withSuppressedOutput()); }

    private static void run(MinecraftServer server, String named, String asking, CommandSourceStack source) {
        Identifier id = Identifier.tryParse(named);
        var held = id == null ? null : server.getFunctions().get(id).orElse(null);
        if (held == null) {
            ContentLog.LOGGER.error("{} asks to run the function {}, which no pack provides, so nothing is run", asking, named);
            return;
        }
        server.getFunctions().execute(held, source);
    }
}
