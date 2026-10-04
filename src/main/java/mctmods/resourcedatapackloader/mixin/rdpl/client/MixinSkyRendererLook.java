package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.client.ContentFogSampler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SkyRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(SkyRenderer.class) public abstract class MixinSkyRendererLook {
    @ModifyVariable(method = "renderSun", at = @At("HEAD"), argsOnly = true, name = "rainBrightness")
    private float rdpl$dimSun(float rainBrightness) { return rainBrightness * ContentFogSampler.sun(Minecraft.getInstance().level); }

    @ModifyVariable(method = "renderMoon", at = @At("HEAD"), argsOnly = true, name = "rainBrightness")
    private float rdpl$dimMoon(float rainBrightness) { return rainBrightness * ContentFogSampler.moon(Minecraft.getInstance().level); }
}
