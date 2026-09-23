package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.content.worldgen.ContentWeather;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class) public abstract class MixinLevelRendererWeather {
    @Unique private boolean rdpl$aboveCeiling() {
        Entity view = Minecraft.getInstance().getCameraEntity();
        return view != null && ContentWeather.above(view.level(), view.getBlockY());
    }

    @Inject(method = "renderSnowAndRain", at = @At("HEAD"), cancellable = true)
    private void rdpl$skipWeatherRender(CallbackInfo ci) {
        if (rdpl$aboveCeiling()) { ci.cancel(); }
    }

    @Inject(method = "tickRain", at = @At("HEAD"), cancellable = true)
    private void rdpl$skipWeatherParticles(CallbackInfo ci) {
        if (rdpl$aboveCeiling()) { ci.cancel(); }
    }
}
