package mctmods.resourcedatapackloader.mixin.client.renderer;

import mctmods.resourcedatapackloader.client.renderer.sky.ContentDimensionEffects;
import mctmods.resourcedatapackloader.client.renderer.sky.ContentSkyExtras;
import mctmods.resourcedatapackloader.client.renderer.sky.ContentStars;
import mctmods.resourcedatapackloader.common.dimension.ContentDimensions;
import mctmods.resourcedatapackloader.common.environment.ContentWeather;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DimensionTraitsDef;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexBuffer;
import javax.annotation.Nullable;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.joml.Matrix4f;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class) public abstract class LevelRendererMixin {
    @Shadow @Final private static ResourceLocation SUN_LOCATION;
    @Shadow @Nullable private ClientLevel level;

    @Redirect(method = "renderClouds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/DimensionSpecialEffects;getCloudHeight()F")) private float rdpl$cloudHeightAsked(DimensionSpecialEffects effects) {
        float own = effects.getCloudHeight();
        if (level == null || Float.isNaN(own) || effects instanceof ContentDimensionEffects content && content.drawsLayer()) { return own; }
        Integer asked = ContentDimensions.cloudHeight(level.dimension().location().toString());
        return asked != null ? asked : own;
    }

    @Unique private boolean rdpl$aboveCeiling() {
        Entity view = Minecraft.getInstance().getCameraEntity();
        return view != null && ContentWeather.above(view.level(), view.getBlockY());
    }

    @Inject(method = "renderSnowAndRain", at = @At("HEAD"), cancellable = true) private void rdpl$skipWeatherRender(CallbackInfo ci) { if (rdpl$aboveCeiling()) { ci.cancel(); } }

    @Inject(method = "tickRain", at = @At("HEAD"), cancellable = true) private void rdpl$skipWeatherParticles(CallbackInfo ci) { if (rdpl$aboveCeiling()) { ci.cancel(); } }

    @Inject(method = "renderSky", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/resources/ResourceLocation;)V", ordinal = 0)) private void rdpl$sunHaze(PoseStack poseStack, Matrix4f projectionMatrix, float partialTick, Camera camera, boolean isFoggy, Runnable skyFogSetup, CallbackInfo ci) { if (level != null) { ContentDimensionEffects.sunHaze(level, (1.0F - level.getRainLevel(partialTick)) * ContentDimensionEffects.sun(level), DimensionTraitsDef.VANILLA_SUN, poseStack.last().pose(), ContentDimensionEffects.sunTexture(level, SUN_LOCATION)); } }

    @Redirect(method = "renderSky", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/VertexBuffer;drawWithShader(Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;Lnet/minecraft/client/renderer/ShaderInstance;)V", ordinal = 1)) private void rdpl$tintStars(VertexBuffer buffer, Matrix4f modelViewMatrix, Matrix4f projectionMatrix, ShaderInstance shader) {
        if (level == null || ContentStars.keepsVanilla(level)) {
            buffer.drawWithShader(modelViewMatrix, projectionMatrix, shader);
            return;
        }
        ContentStars.VANILLA.draw(level, RenderSystem.getShaderColor()[3], modelViewMatrix, projectionMatrix, Minecraft.getInstance().getFrameTime(), 0.0F);
    }

    @Inject(method = "renderSky", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;enableBlend()V")) private void rdpl$skybox(PoseStack poseStack, Matrix4f projectionMatrix, float partialTick, Camera camera, boolean isFoggy, Runnable skyFogSetup, CallbackInfo ci) { ContentSkyExtras.backdrop(level, poseStack.last().pose(), partialTick); }

    @Inject(method = "renderSky", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V", ordinal = 1, shift = At.Shift.AFTER)) private void rdpl$skyOverlay(PoseStack poseStack, Matrix4f projectionMatrix, float partialTick, Camera camera, boolean isFoggy, Runnable skyFogSetup, CallbackInfo ci) { ContentSkyExtras.overlay(level, poseStack.last().pose(), partialTick); }

    @ModifyArg(method = "renderSky", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/resources/ResourceLocation;)V", ordinal = 0), index = 1) private ResourceLocation rdpl$sunTexture(ResourceLocation pTextureId) { return ContentDimensionEffects.sunTexture(level, pTextureId); }

    @ModifyArg(method = "renderSky", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/resources/ResourceLocation;)V", ordinal = 1), index = 1) private ResourceLocation rdpl$moonTexture(ResourceLocation pTextureId) { return ContentDimensionEffects.moonTexture(level, pTextureId); }

    @Inject(method = "renderSky", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/LevelRenderer;MOON_LOCATION:Lnet/minecraft/resources/ResourceLocation;", opcode = Opcodes.GETSTATIC)) private void rdpl$dimMoon(PoseStack poseStack, Matrix4f projectionMatrix, float partialTick, Camera camera, boolean isFoggy, Runnable skyFogSetup, CallbackInfo ci) { if (level != null) { RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, (1.0F - level.getRainLevel(partialTick)) * ContentDimensionEffects.moon(level)); } }
}
