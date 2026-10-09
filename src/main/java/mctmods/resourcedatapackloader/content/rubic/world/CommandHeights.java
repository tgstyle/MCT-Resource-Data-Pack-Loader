package mctmods.resourcedatapackloader.content.rubic.world;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;
import mctmods.resourcedatapackloader.util.compat.CompatHandler;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import java.lang.ref.WeakReference;
import javax.annotation.Nullable;

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

    public static int minY(@Nullable WeakReference<IRubicWorld> held, int orig) {
        IRubicWorld world = held == null ? null : held.get();
        return world == null ? orig : world.rdpl$getMinHeight() + orig;
    }

    public static int maxY(@Nullable WeakReference<IRubicWorld> held, int orig) {
        IRubicWorld world = held == null ? null : held.get();
        return world == null ? orig : world.rdpl$getMaxHeight() + orig - CompatHandler.VANILLA_HEIGHT;
    }
}
