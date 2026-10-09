package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.content.worldgen.ContentDimensionRules;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.world.WorldProvider;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntityRenderer.class) public abstract class MixinEntityRendererDimensionOverlay {
    @Shadow @Final private Minecraft mc;

    @WrapOperation(method = "setupFog", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldProvider;doesXZShowFog(II)Z")) private boolean rdpl$existingShowsFog(WorldProvider provider, int x, int z, Operation<Boolean> original) { return ContentDimensionRules.existingShowsFog(mc.world, original.call(provider, x, z)); }

    @WrapOperation(method = "updateFogColor", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldProvider;calcSunriseSunsetColors(FF)[F")) private float[] rdpl$existingSunrise(WorldProvider provider, float celestialAngle, float partialTicks, Operation<float[]> original) { return ContentDimensionRules.existingSunrise(mc.world, original.call(provider, celestialAngle, partialTicks)); }

    @ModifyExpressionValue(method = "updateFogColor", at = {@At(value = "FIELD", target = "Lnet/minecraft/client/renderer/EntityRenderer;fogColor1:F", opcode = Opcodes.GETFIELD), @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/EntityRenderer;fogColor2:F", opcode = Opcodes.GETFIELD)}) private float rdpl$existingFogLight(float own) { return ContentDimensionRules.existingFogLight(mc.world, own); }

    @WrapOperation(method = "updateLightmap", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldProvider;getLightmapColors(FFFF[F)V", remap = false)) private void rdpl$existingLightmap(WorldProvider provider, float partialTicks, float sunBrightness, float skyLight, float blockLight, float[] colors, Operation<Void> original) {
        original.call(provider, partialTicks, sunBrightness, skyLight, blockLight, colors);
        ContentDimensionRules.existingLightmap(mc.world, partialTicks, sunBrightness, skyLight, blockLight, colors);
    }
}
