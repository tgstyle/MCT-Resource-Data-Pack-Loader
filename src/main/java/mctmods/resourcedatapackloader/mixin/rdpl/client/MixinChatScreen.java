package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.client.ChatHistoryKeeper;

import net.minecraft.client.gui.screens.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatScreen.class) public abstract class MixinChatScreen {
    @Shadow public abstract String normalizeChatMessage(String message);

    @Inject(method = "handleChatInput", at = @At("HEAD"))
    private void rdpl$keepTyped(String input, boolean addToRecentChat, CallbackInfoReturnable<Boolean> cir) {
        if (addToRecentChat) { ChatHistoryKeeper.caught(normalizeChatMessage(input)); }
    }
}
