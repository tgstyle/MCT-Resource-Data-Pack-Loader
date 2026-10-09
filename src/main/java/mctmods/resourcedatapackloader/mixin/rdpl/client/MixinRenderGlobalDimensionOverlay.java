package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.content.worldgen.ContentDimensionRules;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(RenderGlobal.class) public abstract class MixinRenderGlobalDimensionOverlay {
    @Shadow private WorldClient world;

    @WrapOperation(method = "renderSky(FI)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldProvider;calcSunriseSunsetColors(FF)[F")) private float[] rdpl$existingSunrise(WorldProvider provider, float celestialAngle, float partialTicks, Operation<float[]> original) { return ContentDimensionRules.existingSunrise(world, original.call(provider, celestialAngle, partialTicks)); }

    @WrapOperation(method = {"renderSky(FI)V", "renderClouds"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldProvider;isSurfaceWorld()Z")) private boolean rdpl$existingSurface(WorldProvider provider, Operation<Boolean> original) { return ContentDimensionRules.existingSurface(provider, original.call(provider)); }

    @WrapOperation(method = "renderSky(FI)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/texture/TextureManager;bindTexture(Lnet/minecraft/util/ResourceLocation;)V")) private void rdpl$skyTexture(TextureManager textures, ResourceLocation resource, Operation<Void> original) { original.call(textures, ContentDimensionRules.skyTexture(world, resource)); }

    @WrapOperation(method = {"renderClouds", "renderCloudsFancy"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldProvider;getCloudHeight()F")) private float rdpl$existingCloudHeight(WorldProvider provider, Operation<Float> original) { return ContentDimensionRules.existingCloudHeight(world, original.call(provider)); }
}
