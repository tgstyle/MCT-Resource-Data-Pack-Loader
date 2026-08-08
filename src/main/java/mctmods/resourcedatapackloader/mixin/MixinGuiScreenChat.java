package mctmods.resourcedatapackloader.mixin;

import mctmods.resourcedatapackloader.client.ChatHistoryKeeper;

import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiScreen.class)
public abstract class MixinGuiScreenChat {
    @Inject(method = "sendChatMessage(Ljava/lang/String;)V", at = @At("HEAD"))
    private void rdpl$keepTyped(String msg, CallbackInfo ci) { ChatHistoryKeeper.caught(msg); }
}
