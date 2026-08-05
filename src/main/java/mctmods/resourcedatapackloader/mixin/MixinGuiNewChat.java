package mctmods.resourcedatapackloader.mixin;

import mctmods.resourcedatapackloader.content.util.ContentHoldLook;

import net.minecraft.client.gui.GuiNewChat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GuiNewChat.class)
public abstract class MixinGuiNewChat {
    @Inject(method = "getChatHeight", at = @At("RETURN"), cancellable = true)
    private void rdpl$smallWhileHeld(CallbackInfoReturnable<Integer> cir) {
        if (ContentHoldLook.small()) { cir.setReturnValue(Math.min(cir.getReturnValue(), 36)); }
    }
}
