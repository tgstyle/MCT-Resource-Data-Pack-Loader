package mctmods.resourcedatapackloader.common.util.entity;

import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandResultCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.FunctionInstantiationException;
import net.minecraft.commands.execution.ExecutionContext;
import net.minecraft.commands.functions.CommandFunction;
import net.minecraft.commands.functions.InstantiatedFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.LevelBasedPermissionSet;
import net.minecraft.server.permissions.PermissionSet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public final class Functions {
    private static final PermissionSet LEVEL = LevelBasedPermissionSet.GAMEMASTER;

    private Functions() {}

    public static void runWith(CommandSourceStack source, String named, String asking, Map<String, String> values) {
        MinecraftServer server = source.getServer();
        Identifier id = Identifier.tryParse(named);
        CommandFunction<CommandSourceStack> held = id == null ? null : server.getFunctions().get(id).orElse(null);
        if (held == null) {
            ContentLog.LOGGER.error("{} asks to run the function {}, which no pack provides, so nothing is run", asking, named);
            return;
        }
        CompoundTag arguments = new CompoundTag();
        values.forEach(arguments::putString);
        InstantiatedFunction<CommandSourceStack> ready;
        try { ready = held.instantiate(arguments, server.getFunctions().getDispatcher()); }
        catch (FunctionInstantiationException ex) {
            ContentLog.LOGGER.error("{} runs the function {}, which does not parse after its arguments are filled in ({}), so nothing is run", asking, named, ex.messageComponent().getString());
            return;
        }
        CommandSourceStack runner = source.getPlayer() == null ? server.createCommandSourceStack() : source.getPlayer().createCommandSourceStack().withPermission(LEVEL).withSuppressedOutput();
        Commands.executeCommandInContext(runner, context -> ExecutionContext.queueInitialFunctionCall(context, ready, runner, CommandResultCallback.EMPTY));
    }

    public static void run(MinecraftServer server, String named, String asking) { run(server, named, asking, server.createCommandSourceStack()); }

    public static void runAs(ServerPlayer player, String named, String asking) { run(player.level().getServer(), named, asking, player.createCommandSourceStack().withPermission(LEVEL).withSuppressedOutput()); }

    public static void runAs(Entity entity, String named, String asking) {
        if (entity.level() instanceof ServerLevel level) { run(level.getServer(), named, asking, entity.createCommandSourceStackForNameResolution(level).withPermission(LEVEL).withSuppressedOutput()); }
    }

    public static void runAt(ServerLevel level, BlockPos pos, String named, String asking) {
        run(level.getServer(), named, asking, level.getServer().createCommandSourceStack().withLevel(level).withPosition(Vec3.atBottomCenterOf(pos)).withPermission(LEVEL).withSuppressedOutput());
    }

    public static boolean command(ServerLevel level, @Nullable Entity entity, String command) {
        MinecraftServer server = level.getServer();
        CommandSourceStack source = entity == null ? server.createCommandSourceStack().withLevel(level).withPosition(Vec3.atBottomCenterOf(level.getRespawnData().pos())) : entity.createCommandSourceStackForNameResolution(level);
        boolean[] done = {false};
        server.getCommands().performPrefixedCommand(source.withPermission(LEVEL).withSuppressedOutput().withCallback((success, result) -> done[0] |= success && result > 0), command);
        return done[0];
    }

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
