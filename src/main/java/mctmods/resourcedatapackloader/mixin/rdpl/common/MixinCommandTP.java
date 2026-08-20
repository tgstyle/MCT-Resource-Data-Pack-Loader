package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;

import static net.minecraft.command.CommandBase.getCommandSenderAsPlayer;
import static net.minecraft.command.CommandBase.getEntity;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandTP;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.lang.ref.WeakReference;
import javax.annotation.Nullable;

@Mixin(CommandTP.class) public class MixinCommandTP {
    @Unique @Nullable private WeakReference<IRubicWorld> rdpl$commandWorld;

    @Inject(method = "execute",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/command/CommandTP;getEntity(Lnet/minecraft/server/MinecraftServer;"
                            + "Lnet/minecraft/command/ICommandSender;Ljava/lang/String;)Lnet/minecraft/entity/Entity;",
                    ordinal = 0)
    )
    private void postGetEntityInject(MinecraftServer server, ICommandSender sender, String[] args, CallbackInfo ci) {
        try {
            rdpl$commandWorld = new WeakReference<>((IRubicWorld) getEntity(server, sender, args[0]).getEntityWorld());
        } catch (CommandException e) {
            rdpl$commandWorld = null;
        }
    }

    @Inject(method = "execute",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/command/CommandTP;getCommandSenderAsPlayer(Lnet/minecraft/command/ICommandSender;)"
                            + "Lnet/minecraft/entity/player/EntityPlayerMP;",
                    ordinal = 0))
    private void postGetEntityPlayerInject(MinecraftServer server, ICommandSender sender, String[] args, CallbackInfo ci) {
        try {
            rdpl$commandWorld = new WeakReference<>((IRubicWorld) getCommandSenderAsPlayer(sender).getEntityWorld());
        } catch (PlayerNotFoundException e) {
            rdpl$commandWorld = null;
        }
    }

    @ModifyConstant(method = "execute", constant = @Constant(intValue = -4096)) private int getMinY(int orig) {
        if (rdpl$commandWorld == null) { return orig; }
        IRubicWorld world = rdpl$commandWorld.get();
        if (world == null) { return orig; }
        return world.rdpl$getMinHeight() + orig;
    }

    @ModifyConstant(method = "execute", constant = @Constant(intValue = 4096)) private int getMaxY(int orig) {
        if (rdpl$commandWorld == null) { return orig; }
        IRubicWorld world = rdpl$commandWorld.get();
        if (world == null) { return orig; }
        return world.rdpl$getMaxHeight() + orig - 256;
    }
}
