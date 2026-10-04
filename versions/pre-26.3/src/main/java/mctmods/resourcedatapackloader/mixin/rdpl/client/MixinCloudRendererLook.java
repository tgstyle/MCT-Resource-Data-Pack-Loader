package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.client.ContentCloudLayers;

import net.minecraft.client.CloudStatus;
import net.minecraft.client.renderer.CloudRenderer;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CloudRenderer.class) public abstract class MixinCloudRendererLook {
    @ModifyConstant(method = "render", constant = @Constant(floatValue = 0.030000001F))
    private float rdpl$cloudSpeed(float drift) { return ContentCloudLayers.drift(drift); }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void rdpl$cloudLayers(int color, CloudStatus cloudStatus, float bottomY, int range, Vec3 cameraPosition, long gameTime, float partialTicks, CallbackInfo ci) {
        if (ContentCloudLayers.draw((CloudRenderer) (Object) this, color, bottomY, (renderer, tint, height) -> renderer.render(tint, cloudStatus, height, range, cameraPosition, gameTime, partialTicks))) { ci.cancel(); }
    }

    @Inject(method = "endFrame", at = @At("TAIL"))
    private void rdpl$endLayers(CallbackInfo ci) {
        for (CloudRenderer extra : ContentCloudLayers.extras((CloudRenderer) (Object) this)) { extra.endFrame(); }
    }
}
