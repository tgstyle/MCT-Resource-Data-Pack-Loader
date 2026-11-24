package mctmods.resourcedatapackloader.common.rubic.world;

import mctmods.resourcedatapackloader.common.rubic.world.interfaces.IRubicWorld;
import mctmods.resourcedatapackloader.common.util.worldgen.GenHeights;

import java.lang.ref.WeakReference;
import javax.annotation.Nullable;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

public final class CommandHeights {
    private CommandHeights() {}

    @Nullable public static WeakReference<IRubicWorld> worldOf(MinecraftServer server, ICommandSender sender, String target) {
        try { return new WeakReference<>((IRubicWorld) CommandBase.getEntity(server, sender, target).getEntityWorld()); }
        catch (CommandException e) { return null; }
    }

    @Nullable public static WeakReference<IRubicWorld> senderWorld(ICommandSender sender) {
        try { return new WeakReference<>((IRubicWorld) CommandBase.getCommandSenderAsPlayer(sender).getEntityWorld()); }
        catch (CommandException e) { return null; }
    }

    public static WeakReference<IRubicWorld> of(@Nullable World world) { return new WeakReference<>(world instanceof IRubicWorld ? (IRubicWorld) world : null); }

    @Nullable private static IRubicWorld world(@Nullable WeakReference<IRubicWorld> held) { return held == null ? null : held.get(); }

    public static int minY(@Nullable WeakReference<IRubicWorld> held, int orig) {
        IRubicWorld world = world(held);
        return world == null ? orig : GenHeights.floor((World) world, orig);
    }

    public static int maxY(@Nullable WeakReference<IRubicWorld> held, int orig) {
        IRubicWorld world = world(held);
        return world == null ? orig : GenHeights.ceilingOffset((World) world, orig);
    }
}
