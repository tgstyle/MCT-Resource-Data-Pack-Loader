package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.client.ContentFogSampler;
import mctmods.resourcedatapackloader.client.ContentSkyExtras;
import mctmods.resourcedatapackloader.client.ContentSkyRenderer;
import mctmods.resourcedatapackloader.client.ContentStarField;
import mctmods.resourcedatapackloader.client.ContentStars;
import mctmods.resourcedatapackloader.content.def.DimensionTraitsDef;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.renderpearl.api.buffers.GpuBuffer;
import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import com.mojang.renderpearl.api.commands.RenderPass;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SkyRenderer;
import net.minecraft.client.renderer.state.level.SkyRenderState;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.world.level.MoonPhase;
import org.joml.Vector3fc;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SkyRenderer.class) public abstract class MixinSkyRenderer {
    @Shadow @Final private TextureAtlas celestialsAtlas;
    @Shadow @Final private GpuBuffer sunBuffer;

    @Shadow protected abstract void renderMoon(RenderPass renderPass, MoonPhase moonPhase, float rainBrightness, PoseStack poseStack);

    @Inject(method = "renderSun(Lcom/mojang/renderpearl/api/commands/RenderPass;FLcom/mojang/blaze3d/vertex/PoseStack;)V", at = @At("HEAD")) private void rdpl$underSun(RenderPass renderPass, float rainBrightness, PoseStack poseStack, CallbackInfo ci) { ContentSkyExtras.underSun(renderPass, poseStack, DimensionTraitsDef.VANILLA_SUN, rainBrightness * ContentFogSampler.sun(Minecraft.getInstance().level), celestialsAtlas, sunBuffer); }

    @Inject(method = "render(Lcom/mojang/renderpearl/api/buffers/GpuBufferSlice;Lnet/minecraft/client/renderer/state/level/SkyRenderState;)V", at = @At("HEAD")) private void rdpl$preparePackSky(GpuBufferSlice skyFog, SkyRenderState state, CallbackInfo ci) {
        ContentSkyExtras.prepare(state.sunAngle);
        ContentSkyRenderer sky = ContentSkyRenderer.of(Minecraft.getInstance().level);
        if (sky != null) { sky.prepare(state.sunAngle, state.moonAngle, state.starAngle, state.starBrightness); }
        if (ContentStarField.replacesVanilla(Minecraft.getInstance().level)) { ContentStars.VANILLA.prepare(); }
    }

    @Inject(method = "renderSkyDisc(Lcom/mojang/renderpearl/api/commands/RenderPass;Lorg/joml/Vector3fc;)V", at = @At("TAIL")) private void rdpl$skybox(RenderPass renderPass, Vector3fc skyColor, CallbackInfo ci) { ContentSkyExtras.backdrop(renderPass); }

    @Inject(method = "renderStars(Lcom/mojang/renderpearl/api/commands/RenderPass;FLcom/mojang/blaze3d/vertex/PoseStack;)V", at = @At("HEAD"), cancellable = true) private void rdpl$tintStars(RenderPass renderPass, float starBrightness, PoseStack poseStack, CallbackInfo ci) {
        if (!ContentStarField.replacesVanilla(Minecraft.getInstance().level)) { return; }
        ContentStars.VANILLA.draw(renderPass, poseStack, starBrightness);
        ci.cancel();
    }

    @Inject(method = "renderSunMoonAndStars(Lcom/mojang/renderpearl/api/commands/RenderPass;Lcom/mojang/blaze3d/vertex/PoseStack;FFFLnet/minecraft/world/level/MoonPhase;FF)V", at = @At("HEAD"), cancellable = true) private void rdpl$packSky(RenderPass renderPass, PoseStack poseStack, float sunAngle, float moonAngle, float starAngle, MoonPhase moonPhase, float rainBrightness, float starBrightness, CallbackInfo ci) {
        ContentSkyExtras.overlay(renderPass, poseStack);
        ContentSkyRenderer sky = ContentSkyRenderer.of(Minecraft.getInstance().level);
        if (sky == null) { return; }
        sky.render(renderPass, poseStack, sunAngle, moonAngle, starAngle, moonPhase.index(), rainBrightness, starBrightness, turned -> renderMoon(renderPass, moonPhase, rainBrightness, turned));
        ci.cancel();
    }
}
