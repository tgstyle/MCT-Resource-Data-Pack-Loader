package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.client.ChatHistoryKeeper;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.client.ClientCommandHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientCommandHandler.class) public abstract class MixinClientCommandSuccess {
    @Inject(method = "executeCommand", at = @At("RETURN")) private void rdpl$keepRanClientCommand(ICommandSender sender, String message, CallbackInfoReturnable<Integer> cir) {
        if (cir.getReturnValueI() > 0) { ChatHistoryKeeper.commandRan(message); }
    }
}
