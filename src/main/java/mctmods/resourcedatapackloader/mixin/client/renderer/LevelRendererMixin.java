package mctmods.resourcedatapackloader.mixin.client.renderer;

import mctmods.resourcedatapackloader.client.renderer.sky.ContentDimensionEffects;
import mctmods.resourcedatapackloader.client.renderer.sky.ContentSkyExtras;
import mctmods.resourcedatapackloader.client.renderer.sky.ContentStars;
import mctmods.resourcedatapackloader.client.renderer.sky.ContentSunHaze;
import mctmods.resourcedatapackloader.common.blocks.shield.ShieldIndex;
import mctmods.resourcedatapackloader.common.blocks.shield.ShieldIndexes;
import mctmods.resourcedatapackloader.common.dimension.ContentDimensions;
import mctmods.resourcedatapackloader.common.environment.ContentWeather;
import mctmods.resourcedatapackloader.common.pack.definitions.sky.SkyFlip;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DimensionTraitsDef;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.VertexBuffer;
import javax.annotation.Nullable;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class) public abstract class LevelRendererMixin {
    @Shadow @Nullable private ClientLevel level;
    @Unique private final ShieldIndex.Near rdpl$shields = new ShieldIndex.Near();
    @Unique private int rdpl$viewY;

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

    @Unique private void rdpl$fillShields(ClientLevel world, double x, double y, double z, int range) {
        rdpl$viewY = Mth.floor(y);
        ShieldIndexes.near(world, Mth.floor(x), Mth.floor(z), range, rdpl$shields);
    }

    @Inject(method = "renderSnowAndRain", at = @At("HEAD"), cancellable = true) private void rdpl$skipWeatherRender(LightTexture lightTexture, float partialTick, double camX, double camY, double camZ, CallbackInfo ci) {
        if (rdpl$aboveCeiling()) {
            ci.cancel();
            return;
        }
        if (level != null) { rdpl$fillShields(level, camX, camY, camZ, Minecraft.useFancyGraphics() ? 10 : 5); }
    }

    @Redirect(method = "renderSnowAndRain", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getHeight(Lnet/minecraft/world/level/levelgen/Heightmap$Types;II)I")) private int rdpl$shutRain(Level world, Heightmap.Types heightmapType, int x, int z) { return rdpl$shields.shut(x, z) ? rdpl$viewY + 11 : world.getHeight(heightmapType, x, z); }

    @Redirect(method = "tickRain", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/LevelReader;getHeightmapPos(Lnet/minecraft/world/level/levelgen/Heightmap$Types;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/core/BlockPos;", ordinal = 0)) private BlockPos rdpl$shutSplash(LevelReader world, Heightmap.Types heightmapType, BlockPos pos) { return rdpl$shields.shut(pos.getX(), pos.getZ()) ? new BlockPos(pos.getX(), rdpl$viewY + 11, pos.getZ()) : world.getHeightmapPos(heightmapType, pos); }

    @Inject(method = "tickRain", at = @At("HEAD"), cancellable = true) private void rdpl$skipWeatherParticles(Camera camera, CallbackInfo ci) {
        if (rdpl$aboveCeiling()) {
            ci.cancel();
            return;
        }
        if (level != null) {
            Vec3 at = camera.getPosition();
            rdpl$fillShields(level, at.x, at.y, at.z, 10);
        }
    }

    @ModifyArg(method = "renderSky", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderColor(FFFF)V", ordinal = 2), index = 3) private float rdpl$dimSun(float alpha) { return alpha * ContentDimensionEffects.sun(level); }

    @Inject(method = "renderSky", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/LevelRenderer;SUN_LOCATION:Lnet/minecraft/resources/ResourceLocation;", opcode = Opcodes.GETSTATIC)) private void rdpl$sunAura(Matrix4f frustumMatrix, Matrix4f projectionMatrix, float partialTick, Camera camera, boolean isFoggy, Runnable skyFogSetup, CallbackInfo ci) { if (level != null) { Matrix4f pose = ContentSunHaze.sunPose(level, frustumMatrix, partialTick); float alpha = RenderSystem.getShaderColor()[3]; ContentSunHaze.aura(level, pose, alpha, DimensionTraitsDef.VANILLA_SUN, frustumMatrix); ContentSunHaze.lines(level, pose, alpha, DimensionTraitsDef.VANILLA_SUN); } }

    @WrapOperation(method = "renderSky", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/BufferUploader;drawWithShader(Lcom/mojang/blaze3d/vertex/MeshData;)V", ordinal = 1)) private void rdpl$sunTint(MeshData meshData, Operation<Void> original, @Local(argsOnly = true, ordinal = 0) Matrix4f frustumMatrix, @Local(argsOnly = true) float partialTick) {
        if (level == null) { original.call(meshData); }
        else { ContentSunHaze.sun(level, ContentSunHaze.sunPose(level, frustumMatrix, partialTick), RenderSystem.getShaderColor()[3], DimensionTraitsDef.VANILLA_SUN, SkyFlip.NONE, () -> original.call(meshData)); }
    }

    @Redirect(method = "renderSky", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/VertexBuffer;drawWithShader(Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;Lnet/minecraft/client/renderer/ShaderInstance;)V", ordinal = 1)) private void rdpl$tintStars(VertexBuffer buffer, Matrix4f modelViewMatrix, Matrix4f projectionMatrix, ShaderInstance shader) {
        if (level == null || ContentStars.keepsVanilla(level)) {
            buffer.drawWithShader(modelViewMatrix, projectionMatrix, shader);
            return;
        }
        ContentStars.VANILLA.draw(level, RenderSystem.getShaderColor()[3], modelViewMatrix, projectionMatrix, Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false), 0.0F);
    }

    @Inject(method = "renderSky", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;enableBlend()V")) private void rdpl$skybox(Matrix4f frustumMatrix, Matrix4f projectionMatrix, float partialTick, Camera camera, boolean isFoggy, Runnable skyFogSetup, CallbackInfo ci) { ContentSkyExtras.backdrop(level, frustumMatrix, partialTick); }

    @Inject(method = "renderSky", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;defaultBlendFunc()V", shift = At.Shift.AFTER)) private void rdpl$skyOverlay(Matrix4f frustumMatrix, Matrix4f projectionMatrix, float partialTick, Camera camera, boolean isFoggy, Runnable skyFogSetup, CallbackInfo ci) { ContentSkyExtras.overlay(level, frustumMatrix, partialTick); }

    @ModifyExpressionValue(method = "renderSky", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/LevelRenderer;SUN_LOCATION:Lnet/minecraft/resources/ResourceLocation;", opcode = Opcodes.GETSTATIC)) private ResourceLocation rdpl$sunTexture(ResourceLocation original) { return ContentDimensionEffects.sunTexture(level, original); }

    @ModifyExpressionValue(method = "renderSky", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/LevelRenderer;MOON_LOCATION:Lnet/minecraft/resources/ResourceLocation;", opcode = Opcodes.GETSTATIC)) private ResourceLocation rdpl$moonTexture(ResourceLocation original) { return ContentDimensionEffects.moonTexture(level, original); }

    @Inject(method = "renderSky", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/LevelRenderer;MOON_LOCATION:Lnet/minecraft/resources/ResourceLocation;", opcode = Opcodes.GETSTATIC)) private void rdpl$dimMoon(Matrix4f frustumMatrix, Matrix4f projectionMatrix, float partialTick, Camera camera, boolean isFoggy, Runnable skyFogSetup, CallbackInfo ci) { if (level != null) { RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, (1.0F - level.getRainLevel(partialTick)) * ContentDimensionEffects.moon(level)); } }
}
