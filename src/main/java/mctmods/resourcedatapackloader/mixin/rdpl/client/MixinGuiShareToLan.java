package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.content.ContentServer;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiShareToLan;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiShareToLan.class) public abstract class MixinGuiShareToLan {
    @Shadow private GuiButton allowCheatsButton;
    @Shadow private boolean allowCheats;

    @Inject(method = "updateDisplayNames", at = @At("HEAD")) private void rdpl$lockCommands(CallbackInfo ci) {
        if (ContentServer.lanCommands()) { return; }
        allowCheats = false;
        allowCheatsButton.enabled = false;
    }
}
