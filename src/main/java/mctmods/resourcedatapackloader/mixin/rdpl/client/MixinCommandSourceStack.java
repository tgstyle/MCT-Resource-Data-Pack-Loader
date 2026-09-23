package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.client.ChatHistoryKeeper;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CommandSourceStack.class) public abstract class MixinCommandSourceStack {
    @Inject(method = "sendFailure", at = @At("HEAD"))
    private void rdpl$noteFailure(Component message, CallbackInfo ci) { ChatHistoryKeeper.failed(CommandSourceStack.class.cast(this)); }
}
