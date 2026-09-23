package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.client.ChatHistoryKeeper;

import net.neoforged.neoforge.client.ClientCommandHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ClientCommandHandler.class, remap = false) public abstract class MixinClientCommandHandler {
    @Inject(method = "runCommand", at = @At(value = "INVOKE", target = "Lcom/mojang/brigadier/CommandDispatcher;execute(Lcom/mojang/brigadier/StringReader;Ljava/lang/Object;)I", shift = At.Shift.AFTER))
    private static void rdpl$keepRanClientCommand(String command, CallbackInfoReturnable<Boolean> cir) { ChatHistoryKeeper.commandRan(command); }
}
