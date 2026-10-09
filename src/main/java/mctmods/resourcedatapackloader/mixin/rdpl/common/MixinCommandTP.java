package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.rubic.world.CommandHeights;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;

import net.minecraft.command.CommandTP;
import net.minecraft.command.ICommandSender;
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

    @Inject(method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraft/command/CommandTP;getEntity(Lnet/minecraft/server/MinecraftServer;Lnet/minecraft/command/ICommandSender;Ljava/lang/String;)Lnet/minecraft/entity/Entity;", ordinal = 0)) private void rdpl$postGetEntityInject(MinecraftServer server, ICommandSender sender, String[] args, CallbackInfo ci) { rdpl$commandWorld = CommandHeights.worldOf(server, sender, args[0]); }

    @Inject(method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraft/command/CommandTP;getCommandSenderAsPlayer(Lnet/minecraft/command/ICommandSender;)Lnet/minecraft/entity/player/EntityPlayerMP;", ordinal = 0)) private void rdpl$postGetEntityPlayerInject(MinecraftServer server, ICommandSender sender, String[] args, CallbackInfo ci) { rdpl$commandWorld = CommandHeights.senderWorld(sender); }

    @ModifyConstant(method = "execute", constant = @Constant(intValue = -4096)) private int rdpl$getMinY(int orig) { return CommandHeights.minY(rdpl$commandWorld, orig); }

    @ModifyConstant(method = "execute", constant = @Constant(intValue = 4096)) private int rdpl$getMaxY(int orig) { return CommandHeights.maxY(rdpl$commandWorld, orig); }
}
