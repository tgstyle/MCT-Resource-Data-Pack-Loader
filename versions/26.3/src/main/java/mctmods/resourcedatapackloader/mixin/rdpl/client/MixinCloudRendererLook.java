package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.client.ContentCloudLayers;

import com.mojang.renderpearl.api.commands.RenderPass;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import net.minecraft.client.CloudStatus;
import net.minecraft.client.renderer.CloudRenderer;
import net.minecraft.client.renderer.oit.OitRenderPassProvider;
import net.minecraft.client.renderer.oit.OitStage;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CloudRenderer.class) public abstract class MixinCloudRendererLook {
    @ModifyConstant(method = "prepare(ILnet/minecraft/client/CloudStatus;FILnet/minecraft/world/phys/Vec3;JF)V", constant = @Constant(floatValue = 0.030000001F))
    private float rdpl$cloudSpeed(float drift) { return ContentCloudLayers.drift(drift); }

    @Inject(method = "prepare(ILnet/minecraft/client/CloudStatus;FILnet/minecraft/world/phys/Vec3;JF)V", at = @At("HEAD"), cancellable = true)
    private void rdpl$cloudLayers(int color, CloudStatus cloudStatus, float bottomY, int range, Vec3 cameraPosition, long gameTime, float partialTicks, CallbackInfo ci) {
        if (ContentCloudLayers.draw((CloudRenderer) (Object) this, color, bottomY, (renderer, tint, height) -> renderer.prepare(tint, cloudStatus, height, range, cameraPosition, gameTime, partialTicks))) { ci.cancel(); }
    }

    @Inject(method = "render(Lnet/minecraft/client/CloudStatus;Lcom/mojang/renderpearl/api/commands/RenderPass;)V", at = @At("TAIL"))
    private void rdpl$renderLayers(CloudStatus cloudStatus, RenderPass renderPass, CallbackInfo ci) {
        for (CloudRenderer extra : ContentCloudLayers.extras((CloudRenderer) (Object) this)) { extra.render(cloudStatus, renderPass); }
    }

    @Inject(method = "renderOit", at = @At("TAIL"))
    private void rdpl$renderLayersOit(CloudStatus cloudStatus, OitStage stage, GpuTextureView mainDepthTextureView, OitRenderPassProvider.Parameters params, CallbackInfo ci) {
        for (CloudRenderer extra : ContentCloudLayers.extras((CloudRenderer) (Object) this)) { extra.renderOit(cloudStatus, stage, mainDepthTextureView, params); }
    }

    @Inject(method = "endFrame", at = @At("TAIL"))
    private void rdpl$endLayers(CallbackInfo ci) {
        for (CloudRenderer extra : ContentCloudLayers.extras((CloudRenderer) (Object) this)) { extra.endFrame(); }
    }
}
