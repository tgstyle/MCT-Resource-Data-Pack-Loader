package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.client.render.SeamSkyRenderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.RenderGlobal;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderGlobal.class) public abstract class MixinRenderGlobalSeamSky {
    @Shadow private WorldClient world;
    @Shadow @Final private Minecraft mc;

    @Inject(method = "renderSky(FI)V", at = @At("RETURN")) private void rdpl$seamSky(float partialTicks, int pass, CallbackInfo ci) { SeamSkyRenderer.render(world, mc, partialTicks); }
}
