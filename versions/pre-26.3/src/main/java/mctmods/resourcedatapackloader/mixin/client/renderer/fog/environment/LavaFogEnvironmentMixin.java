package mctmods.resourcedatapackloader.mixin.client.renderer.fog.environment;

import mctmods.resourcedatapackloader.client.renderer.sky.ContentFogSampler;
import mctmods.resourcedatapackloader.common.pack.definitions.sky.SkyLookDef;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.environment.LavaFogEnvironment;
import net.minecraft.util.ARGB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LavaFogEnvironment.class) public abstract class LavaFogEnvironmentMixin {
    @Inject(method = "getBaseColor", at = @At("HEAD"), cancellable = true) private void rdpl$lavaFog(ClientLevel level, Camera camera, int renderDistance, float partialTicks, CallbackInfoReturnable<Integer> cir) {
        int color = ContentFogSampler.lavaFog();
        if (color != SkyLookDef.UNSET) { cir.setReturnValue(ARGB.opaque(color)); }
    }
}
