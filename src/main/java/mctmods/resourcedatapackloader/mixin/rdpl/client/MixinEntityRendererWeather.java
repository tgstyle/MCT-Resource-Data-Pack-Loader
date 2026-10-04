package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.content.def.RainDef;
import mctmods.resourcedatapackloader.content.worldgen.ContentWeather;
import mctmods.resourcedatapackloader.content.worldgen.ContentWorldProvider;
import mctmods.resourcedatapackloader.util.RainSplash;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class) public abstract class MixinEntityRendererWeather {
    @Shadow private int rendererUpdateCount;

    @Unique private boolean rdpl$aboveCeiling() {
        Entity view = Minecraft.getMinecraft().getRenderViewEntity();
        return view != null && ContentWeather.above(view.world, MathHelper.floor(view.posY));
    }

    @Inject(method = "renderRainSnow", at = @At("HEAD"), cancellable = true)
    private void rdpl$skipWeatherRender(float partialTicks, CallbackInfo ci) {
        if (rdpl$aboveCeiling()) { ci.cancel(); }
    }

    @Inject(method = "addRainParticles", at = @At("HEAD"), cancellable = true)
    private void rdpl$skipWeatherParticles(CallbackInfo ci) {
        if (rdpl$aboveCeiling()) {
            ci.cancel();
            return;
        }
        Minecraft mc = Minecraft.getMinecraft();
        RainDef rain = mc.world.provider instanceof ContentWorldProvider ? ((ContentWorldProvider) mc.world.provider).rainSplash() : null;
        if (rain == null) { return; }
        RainSplash.tick(mc, rain, rendererUpdateCount);
        ci.cancel();
    }
}
