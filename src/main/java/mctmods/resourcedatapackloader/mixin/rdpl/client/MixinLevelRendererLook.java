package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.client.ContentDimensionEffects;
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
import org.joml.Matrix4f;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import javax.annotation.Nullable;

@Mixin(LevelRenderer.class) public abstract class MixinLevelRendererLook {
    @Shadow @Nullable private ClientLevel level;

    @ModifyArg(method = "renderSky", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderColor(FFFF)V", ordinal = 2), index = 3)
    private float rdpl$dimSun(float alpha) { return alpha * ContentDimensionEffects.sun(level); }

    @Redirect(method = "renderSky", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/VertexBuffer;drawWithShader(Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;Lnet/minecraft/client/renderer/ShaderInstance;)V", ordinal = 1))
    private void rdpl$tintStars(VertexBuffer buffer, Matrix4f modelViewMatrix, Matrix4f projectionMatrix, ShaderInstance shader) {
        if (level == null || !ContentStars.replacesVanilla(level)) {
            buffer.drawWithShader(modelViewMatrix, projectionMatrix, shader);
            return;
        }
        ContentStars.VANILLA.draw(level, RenderSystem.getShaderColor()[3], modelViewMatrix, projectionMatrix, Minecraft.getInstance().getFrameTime());
    }

    @Inject(method = "renderSky", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;enableBlend()V"))
    private void rdpl$skybox(PoseStack poseStack, Matrix4f projectionMatrix, float partialTick, Camera camera, boolean isFoggy, Runnable skyFogSetup, CallbackInfo ci) { ContentSkyExtras.backdrop(level, poseStack.last().pose()); }

    @Inject(method = "renderSky", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V", ordinal = 1, shift = At.Shift.AFTER))
    private void rdpl$skyOverlay(PoseStack poseStack, Matrix4f projectionMatrix, float partialTick, Camera camera, boolean isFoggy, Runnable skyFogSetup, CallbackInfo ci) { ContentSkyExtras.overlay(level, poseStack.last().pose(), partialTick); }

    @Inject(method = "renderSky", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/LevelRenderer;MOON_LOCATION:Lnet/minecraft/resources/ResourceLocation;", opcode = Opcodes.GETSTATIC))
    private void rdpl$dimMoon(PoseStack poseStack, Matrix4f projectionMatrix, float partialTick, Camera camera, boolean isFoggy, Runnable skyFogSetup, CallbackInfo ci) {
        if (level != null) { RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, (1.0F - level.getRainLevel(partialTick)) * ContentDimensionEffects.moon(level)); }
    }
}
