package mctmods.resourcedatapackloader.mixin.integration.adastra;

import mctmods.resourcedatapackloader.core.integration.adastra.AdAstraHeights;

import earth.terrarium.adastra.client.screens.player.OverlayScreen;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = OverlayScreen.class, remap = false) public abstract class OverlayScreenMixin {
    @ModifyArg(method = "render(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/DeltaTracker;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;clamp(DDD)D"), index = 0) private static double rdpl$barHeight(double y) { return AdAstraHeights.barHeight(Minecraft.getInstance().level, y); }
}
