package mctmods.resourcedatapackloader.mixin.client.renderer;

import mctmods.resourcedatapackloader.client.gui.hud.HoldView;
import mctmods.resourcedatapackloader.common.blocks.fluid.ContentFluidType;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.fog.FogRenderer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class) public class GameRendererMixin {
    @Shadow @Final private FogRenderer fogRenderer;

    @Inject(method = "tryTakeScreenshotIfNeeded", at = @At("HEAD"), cancellable = true) private void rdpl$iconAfterTheFog(CallbackInfo ci) { if (HoldView.showing()) { ci.cancel(); } }

    @Inject(method = "renderItemInHand", at = @At("HEAD")) private void rdpl$clearHandInPackFluid(CallbackInfo ci) { Player player = Minecraft.getInstance().player; if (player != null && player.getFluidInteraction().getFirstEyeInFluid() instanceof ContentFluidType) { RenderSystem.setShaderFog(fogRenderer.getBuffer(FogRenderer.FogMode.NONE)); } }
}
