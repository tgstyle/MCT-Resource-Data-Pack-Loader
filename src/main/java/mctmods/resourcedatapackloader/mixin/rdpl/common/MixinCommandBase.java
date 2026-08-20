package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.lang.ref.WeakReference;
import javax.annotation.Nonnull;

@Mixin(CommandBase.class) public class MixinCommandBase {
    @Unique @Nonnull private static WeakReference<IRubicWorld> rdpl$commandWorld = new WeakReference<>(null);

    @Inject(method = "parseBlockPos", at = @At(value = "HEAD")) private static void parseBlockPosPre(ICommandSender sender, String[] args, int startIndex, boolean centerBlock, CallbackInfoReturnable<?> cbi) {
        rdpl$commandWorld = new WeakReference<>((IRubicWorld) sender.getEntityWorld());
    }

    @ModifyArg(method = "parseBlockPos",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/command/CommandBase;parseDouble(DLjava/lang/String;IIZ)D", ordinal = 1),
            index = 2)
    private static int getMinY(int original) {
        IRubicWorld world = rdpl$commandWorld.get();
        if (world == null) { return original; }
        return world.rdpl$getMinHeight();
    }

    @ModifyArg(method = "parseBlockPos",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/command/CommandBase;parseDouble(DLjava/lang/String;IIZ)D", ordinal = 1),
            index = 3)
    private static int getMaxY(int original) {
        IRubicWorld world = rdpl$commandWorld.get();
        if (world == null) { return original; }
        return world.rdpl$getMaxHeight();
    }
}
