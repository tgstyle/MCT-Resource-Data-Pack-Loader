package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.rubic.world.CommandHeights;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.server.CommandTeleport;
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

@Mixin(CommandTeleport.class) public class MixinCommandTeleport {
    @Unique @Nullable private WeakReference<IRubicWorld> rdpl$commandWorld;

    @Inject(method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraft/command/server/CommandTeleport;getEntity(Lnet/minecraft/server/MinecraftServer;Lnet/minecraft/command/ICommandSender;Ljava/lang/String;)Lnet/minecraft/entity/Entity;", ordinal = 0)) private void rdpl$postGetEntityInject(MinecraftServer server, ICommandSender sender, String[] args, CallbackInfo ci) { rdpl$commandWorld = CommandHeights.worldOf(server, sender, args[0]); }

    @ModifyConstant(method = "execute", constant = @Constant(intValue = -4096)) private int rdpl$getMinY(int original) { return CommandHeights.minY(rdpl$commandWorld, original); }

    @ModifyConstant(method = "execute", constant = @Constant(intValue = 4096), expect = 2) private int rdpl$getMaxY(int original) { return CommandHeights.maxY(rdpl$commandWorld, original); }
}
