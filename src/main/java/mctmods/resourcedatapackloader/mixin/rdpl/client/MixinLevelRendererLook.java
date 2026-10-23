package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.client.ContentDimensionEffects;
import mctmods.resourcedatapackloader.content.def.DimensionTraitsDef;
import mctmods.resourcedatapackloader.client.ContentSkyExtras;
import mctmods.resourcedatapackloader.client.ContentStars;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexBuffer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import javax.annotation.Nullable;

@Mixin(LevelRenderer.class) public abstract class MixinLevelRendererLook {
    @Shadow @Final private static ResourceLocation SUN_LOCATION;
    @Shadow @Nullable private ClientLevel level;

    @Inject(method = "renderSky", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/resources/ResourceLocation;)V", ordinal = 0)) private void rdpl$sunHaze(PoseStack poseStack, Matrix4f projectionMatrix, float partialTick, Camera camera, boolean isFoggy, Runnable skyFogSetup, CallbackInfo ci) { if (level != null) { ContentDimensionEffects.sunHaze(level, (1.0F - level.getRainLevel(partialTick)) * ContentDimensionEffects.sun(level), DimensionTraitsDef.VANILLA_SUN, poseStack.last().pose(), ContentDimensionEffects.sunTexture(level, SUN_LOCATION)); } }

    @Redirect(method = "renderSky", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/VertexBuffer;drawWithShader(Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;Lnet/minecraft/client/renderer/ShaderInstance;)V", ordinal = 1)) private void rdpl$tintStars(VertexBuffer buffer, Matrix4f modelViewMatrix, Matrix4f projectionMatrix, ShaderInstance shader) {
        if (level == null || ContentStars.keepsVanilla(level)) {
            buffer.drawWithShader(modelViewMatrix, projectionMatrix, shader);
            return;
        }
        ContentStars.VANILLA.draw(level, RenderSystem.getShaderColor()[3], modelViewMatrix, projectionMatrix, Minecraft.getInstance().getFrameTime(), 0.0F);
    }

    @Inject(method = "renderSky", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;enableBlend()V")) private void rdpl$skybox(PoseStack poseStack, Matrix4f projectionMatrix, float partialTick, Camera camera, boolean isFoggy, Runnable skyFogSetup, CallbackInfo ci) { ContentSkyExtras.backdrop(level, poseStack.last().pose()); }

    @Inject(method = "renderSky", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V", ordinal = 1, shift = At.Shift.AFTER)) private void rdpl$skyOverlay(PoseStack poseStack, Matrix4f projectionMatrix, float partialTick, Camera camera, boolean isFoggy, Runnable skyFogSetup, CallbackInfo ci) { ContentSkyExtras.overlay(level, poseStack.last().pose(), partialTick); }

    @ModifyArg(method = "renderSky", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/resources/ResourceLocation;)V", ordinal = 0), index = 1) private ResourceLocation rdpl$sunTexture(ResourceLocation pTextureId) { return ContentDimensionEffects.sunTexture(level, pTextureId); }

    @ModifyArg(method = "renderSky", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/resources/ResourceLocation;)V", ordinal = 1), index = 1) private ResourceLocation rdpl$moonTexture(ResourceLocation pTextureId) { return ContentDimensionEffects.moonTexture(level, pTextureId); }

    @Inject(method = "renderSky", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/LevelRenderer;MOON_LOCATION:Lnet/minecraft/resources/ResourceLocation;", opcode = Opcodes.GETSTATIC)) private void rdpl$dimMoon(PoseStack poseStack, Matrix4f projectionMatrix, float partialTick, Camera camera, boolean isFoggy, Runnable skyFogSetup, CallbackInfo ci) { if (level != null) { RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, (1.0F - level.getRainLevel(partialTick)) * ContentDimensionEffects.moon(level)); } }
}
