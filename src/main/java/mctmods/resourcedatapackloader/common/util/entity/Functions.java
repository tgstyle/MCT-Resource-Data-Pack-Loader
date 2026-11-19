package mctmods.resourcedatapackloader.common.util.entity;

import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandFunction;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public final class Functions {
    private static final int LEVEL = 2;
    public static final Pattern PLACEHOLDER = Pattern.compile("\\$\\(([a-z0-9_]+)\\)");

    private Functions() {}

    public static void run(MinecraftServer server, String named, String asking) { run(server, named, asking, server.createCommandSourceStack()); }

    public static void runAs(Entity entity, String named, String asking) {
        MinecraftServer server = entity.getServer();
        if (server != null) { run(server, named, asking, entity.createCommandSourceStack().withPermission(LEVEL).withSuppressedOutput()); }
    }

    public static void runAt(ServerLevel level, BlockPos pos, String named, String asking) {
        run(level.getServer(), named, asking, level.getServer().createCommandSourceStack().withLevel(level).withPosition(Vec3.atBottomCenterOf(pos)).withPermission(LEVEL).withSuppressedOutput());
    }

    public static boolean command(ServerLevel level, @Nullable Entity entity, String command) {
        MinecraftServer server = level.getServer();
        CommandSourceStack source = entity == null ? server.createCommandSourceStack().withLevel(level).withPosition(Vec3.atBottomCenterOf(level.getSharedSpawnPos())) : entity.createCommandSourceStack();
        return server.getCommands().performPrefixedCommand(source.withPermission(LEVEL).withSuppressedOutput(), command) > 0;
    }

    public static void runWith(CommandSourceStack source, String named, String asking, Map<String, String> values) {
        MinecraftServer server = source.getServer();
        ResourceLocation id = ResourceLocation.tryParse(named);
        String text = id == null ? null : PackManager.get().read(id.getNamespace(), id.getPath(), PackManager.FUNCTIONS, PackManager.MCFUNCTION);
        if (text == null) {
            ContentLog.LOGGER.error("{} asks to run the function {}, which no pack provides, so nothing is run", asking, named);
            return;
        }
        List<String> lines = new ArrayList<>();
        for (String line : text.split("\r\n|\r|\n", -1)) { lines.add(fill(line.trim(), values)); }
        CommandSourceStack runner = source.getEntity() instanceof ServerPlayer player ? player.createCommandSourceStack().withPermission(LEVEL).withSuppressedOutput() : server.createCommandSourceStack();
        CommandFunction function;
        try { function = CommandFunction.fromLines(id, server.getFunctions().getDispatcher(), runner, lines); }
        catch (IllegalArgumentException ex) {
            ContentLog.LOGGER.error("{} runs the function {}, which does not parse after its arguments are filled in ({}), so nothing is run", asking, named, ex.getMessage());
            return;
        }
        server.getFunctions().execute(function, runner);
    }

    private static String fill(String line, Map<String, String> values) {
        String body = line.startsWith("$") && !line.startsWith("$(") ? line.substring(1) : line;
        Matcher matcher = PLACEHOLDER.matcher(body);
        StringBuilder filled = new StringBuilder();
        while (matcher.find()) {
            String value = values.get(matcher.group(1));
            matcher.appendReplacement(filled, Matcher.quoteReplacement(value == null ? matcher.group() : value));
        }
        return matcher.appendTail(filled).toString();
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
