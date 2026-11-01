package mctmods.resourcedatapackloader.mixin.client.gui.screens;

import mctmods.resourcedatapackloader.client.util.ChatHistoryKeeper;

import net.minecraft.client.gui.screens.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatScreen.class) public abstract class ChatScreenMixin {
    @Shadow public abstract String normalizeChatMessage(String message);

    @Inject(method = "handleChatInput", at = @At("HEAD")) private void rdpl$keepTyped(String message, boolean addToRecentChat, CallbackInfo ci) { if (addToRecentChat) { ChatHistoryKeeper.caught(normalizeChatMessage(message)); } }
}
