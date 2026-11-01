package mctmods.resourcedatapackloader.mixin.client.renderer;

import mctmods.resourcedatapackloader.client.gui.hud.HoldView;

import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class) public class GameRendererMixin {
    @Inject(method = "tryTakeScreenshotIfNeeded", at = @At("HEAD"), cancellable = true) private void rdpl$iconAfterTheFog(CallbackInfo ci) { if (HoldView.showing()) { ci.cancel(); } }
}
