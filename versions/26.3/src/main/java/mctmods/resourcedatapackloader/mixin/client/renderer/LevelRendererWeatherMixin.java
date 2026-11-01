package mctmods.resourcedatapackloader.mixin.client.renderer;

import mctmods.resourcedatapackloader.client.renderer.sky.ContentDimensionEffects;

import net.minecraft.client.renderer.WeatherEffectRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WeatherEffectRenderer.class) public abstract class LevelRendererWeatherMixin {
    @Inject(method = "render(Lnet/minecraft/client/renderer/state/level/WeatherRenderState;Lcom/mojang/renderpearl/api/commands/RenderPass;Lcom/mojang/renderpearl/api/pipeline/RenderPipeline;)V", at = @At("HEAD"), cancellable = true) private void rdpl$skipWeatherRender(CallbackInfo ci) { if (ContentDimensionEffects.cameraAboveCeiling()) { ci.cancel(); } }
}
