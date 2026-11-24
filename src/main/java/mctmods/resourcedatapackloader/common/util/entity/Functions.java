package mctmods.resourcedatapackloader.common.util.entity;

import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.text.TextLines;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.command.CommandResultStats;
import net.minecraft.command.FunctionObject;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

public final class Functions {
    private static final int LEVEL = 2;
    public static final Pattern PLACEHOLDER = Pattern.compile("\\$\\(([a-z0-9_]+)\\)");

    private Functions() {}

    public static void run(MinecraftServer server, String named, String asking) {
        execute(server, named, asking, self -> self);
    }

    public static void runAs(Entity player, String named, String asking) { execute(player.getServer(), named, asking, server -> new As(player, server)); }

    public static void runAt(World world, BlockPos pos, String named, String asking) { execute(world.getMinecraftServer(), named, asking, server -> new At(world, pos, server)); }

    private static void execute(@Nullable MinecraftServer server, String named, String asking, Function<MinecraftServer, ICommandSender> sender) {
        FunctionObject function = server == null ? null : find(server, named, asking);
        if (function != null) { server.getFunctionManager().execute(function, sender.apply(server)); }
    }

    public static void runWith(ICommandSender sender, String named, String asking, Map<String, String> values) {
        MinecraftServer server = sender.getServer();
        if (server == null) { return; }
        ResourceLocation id = new ResourceLocation(named);
        String text = PackManager.get().read(id.getNamespace(), id.getPath(), PackManager.FUNCTIONS, PackManager.MCFUNCTION);
        if (text == null) {
            ContentLog.LOGGER.error("{} asks to run the function {}, which no pack provides, so nothing is run", asking, named);
            return;
        }
        List<String> lines = TextLines.lines(text);
        lines.replaceAll(line -> fill(line.trim(), values));
        FunctionObject function;
        try { function = FunctionObject.create(server.getFunctionManager(), lines); }
        catch (IllegalArgumentException ex) {
            ContentLog.LOGGER.error("{} runs the function {}, which does not parse after its arguments are filled in ({}), so nothing is run", asking, named, ex.getMessage());
            return;
        }
        ICommandSender runner = sender.getCommandSenderEntity() instanceof EntityPlayerMP ? new As(sender.getCommandSenderEntity(), server) : server;
        server.getFunctionManager().execute(function, runner);
    }

    private static String fill(String line, Map<String, String> values) {
        String body = line.startsWith("$") && !line.startsWith("$(") ? line.substring(1) : line;
        Matcher matcher = PLACEHOLDER.matcher(body);
        StringBuffer filled = new StringBuffer();
        while (matcher.find()) {
            String value = values.get(matcher.group(1));
            matcher.appendReplacement(filled, Matcher.quoteReplacement(value == null ? matcher.group() : value));
        }
        return matcher.appendTail(filled).toString();
    }

    public static boolean command(World world, @Nullable Entity player, String command) {
        MinecraftServer server = world.getMinecraftServer();
        if (server == null) { return false; }
        ICommandSender sender = player == null ? new At(world, world.getSpawnPoint(), server) : new As(player, server);
        return server.getCommandManager().executeCommand(sender, command) > 0;
    }

    @Nullable private static FunctionObject find(MinecraftServer server, String named, String asking) {
        FunctionObject function = server.getFunctionManager().getFunction(new ResourceLocation(named));
        if (function == null) { ContentLog.LOGGER.error("{} asks to run the function {}, which no pack provides, so nothing is run", asking, named); }
        return function;
    }

    private static final class At implements ICommandSender {
        private final World world;
        private final BlockPos pos;
        private final MinecraftServer server;

        private At(World world, BlockPos pos, MinecraftServer server) {
            this.world = world;
            this.pos = pos;
            this.server = server;
        }

        @Override @Nonnull public String getName() { return "Functions"; }

        @Override public void sendMessage(@Nonnull ITextComponent component) {}

        @Override public boolean canUseCommand(int permLevel, @Nonnull String commandName) { return permLevel <= LEVEL; }

        @Override @Nonnull public BlockPos getPosition() { return pos; }

        @Override @Nonnull public Vec3d getPositionVector() { return new Vec3d(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D); }

        @Override @Nonnull public World getEntityWorld() { return world; }

        @Override public MinecraftServer getServer() { return server; }
    }

    private static final class As implements ICommandSender {
        private final Entity player;
        private final MinecraftServer server;

        private As(Entity player, MinecraftServer server) {
            this.player = player;
            this.server = server;
        }

        @Override @Nonnull public String getName() { return player.getName(); }

        @Override @Nonnull public ITextComponent getDisplayName() { return player.getDisplayName(); }

        @Override public void sendMessage(@Nonnull ITextComponent component) {}

        @Override public boolean canUseCommand(int permLevel, @Nonnull String commandName) { return permLevel <= LEVEL; }

        @Override @Nonnull public BlockPos getPosition() { return player.getPosition(); }

        @Override @Nonnull public Vec3d getPositionVector() { return player.getPositionVector(); }

        @Override @Nonnull public World getEntityWorld() { return player.world; }

        @Override public Entity getCommandSenderEntity() { return player; }

        @Override public void setCommandStat(@Nonnull CommandResultStats.Type type, int amount) { player.setCommandStat(type, amount); }

        @Override public MinecraftServer getServer() { return server; }
    }
}
