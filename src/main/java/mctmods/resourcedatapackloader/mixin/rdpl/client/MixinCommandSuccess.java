package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.client.ChatHistoryKeeper;

import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CommandHandler.class) public abstract class MixinCommandSuccess {
    @Inject(method = "executeCommand", at = @At("RETURN")) private void rdpl$keepRanCommand(ICommandSender sender, String rawCommand, CallbackInfoReturnable<Integer> cir) {
        if (cir.getReturnValueI() <= 0 || !(sender instanceof EntityPlayerMP)) { return; }
        ChatHistoryKeeper.commandRan(rawCommand);
    }
}
