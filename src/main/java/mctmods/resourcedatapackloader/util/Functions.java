package mctmods.resourcedatapackloader.util;

import net.minecraft.command.CommandResultStats;
import net.minecraft.command.FunctionObject;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class Functions {
    private static final int LEVEL = 2;

    private Functions() {}

    public static void run(MinecraftServer server, String named, String asking) {
        FunctionObject function = find(server, named, asking);
        if (function != null) { server.getFunctionManager().execute(function, server); }
    }

    public static void runAs(EntityPlayerMP player, String named, String asking) {
        MinecraftServer server = player.getServer();
        FunctionObject function = server == null ? null : find(server, named, asking);
        if (function != null) { server.getFunctionManager().execute(function, new As(player, server)); }
    }

    @Nullable private static FunctionObject find(MinecraftServer server, String named, String asking) {
        FunctionObject function = server.getFunctionManager().getFunction(new ResourceLocation(named));
        if (function == null) { ContentLog.LOGGER.error("{} asks to run the function {}, which no pack provides, so nothing is run", asking, named); }
        return function;
    }

    private static final class As implements ICommandSender {
        private final EntityPlayerMP player;
        private final MinecraftServer server;

        private As(EntityPlayerMP player, MinecraftServer server) {
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

        @Override public boolean sendCommandFeedback() { return server.worlds[0].getGameRules().getBoolean("commandBlockOutput"); }

        @Override public void setCommandStat(@Nonnull CommandResultStats.Type type, int amount) { player.setCommandStat(type, amount); }

        @Override public MinecraftServer getServer() { return server; }
    }
}
