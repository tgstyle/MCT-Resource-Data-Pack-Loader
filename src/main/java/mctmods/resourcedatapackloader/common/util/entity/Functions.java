package mctmods.resourcedatapackloader.common.util.entity;

import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandResultCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.FunctionInstantiationException;
import net.minecraft.commands.execution.ExecutionContext;
import net.minecraft.commands.functions.InstantiatedFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public final class Functions {
    private static final int LEVEL = 2;

    private Functions() {}

    public static void run(MinecraftServer server, String named, String asking) { run(server, named, asking, server.createCommandSourceStack()); }

    public static void runAs(Entity entity, String named, String asking) {
        MinecraftServer server = entity.getServer();
        if (server != null) { run(server, named, asking, entity.createCommandSourceStack().withPermission(LEVEL).withSuppressedOutput()); }
    }

    public static void runAt(ServerLevel level, BlockPos pos, String named, String asking) {
        run(level.getServer(), named, asking, level.getServer().createCommandSourceStack().withLevel(level).withPosition(Vec3.atBottomCenterOf(pos)).withPermission(LEVEL).withSuppressedOutput());
    }

    public static void runWith(CommandSourceStack source, String named, String asking, Map<String, String> values) {
        MinecraftServer server = source.getServer();
        ResourceLocation id = ResourceLocation.tryParse(named);
        var held = id == null ? null : server.getFunctions().get(id).orElse(null);
        if (held == null) {
            ContentLog.LOGGER.error("{} asks to run the function {}, which no pack provides, so nothing is run", asking, named);
            return;
        }
        CompoundTag arguments = new CompoundTag();
        for (Map.Entry<String, String> value : values.entrySet()) { arguments.putString(value.getKey(), value.getValue()); }
        InstantiatedFunction<CommandSourceStack> filled;
        try { filled = held.instantiate(arguments, server.getFunctions().getDispatcher()); }
        catch (FunctionInstantiationException ex) {
            ContentLog.LOGGER.error("{} runs the function {}, which does not parse after its arguments are filled in ({}), so nothing is run", asking, named, ex.messageComponent().getString());
            return;
        }
        CommandSourceStack runner = source.getEntity() instanceof ServerPlayer player ? player.createCommandSourceStack().withPermission(LEVEL).withSuppressedOutput() : server.createCommandSourceStack();
        Commands.executeCommandInContext(runner, context -> ExecutionContext.queueInitialFunctionCall(context, filled, runner, CommandResultCallback.EMPTY));
    }

    public static boolean command(ServerLevel level, @Nullable Entity entity, String command) {
        MinecraftServer server = level.getServer();
        CommandSourceStack source = entity == null ? server.createCommandSourceStack().withLevel(level).withPosition(Vec3.atBottomCenterOf(level.getSharedSpawnPos())) : entity.createCommandSourceStack();
        boolean[] done = {false};
        server.getCommands().performPrefixedCommand(source.withPermission(LEVEL).withSuppressedOutput().withCallback((success, result) -> done[0] |= success && result > 0), command);
        return done[0];
    }

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
