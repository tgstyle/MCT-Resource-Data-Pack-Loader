package mctmods.resourcedatapackloader.mixin.client.renderer;

import mctmods.resourcedatapackloader.client.util.TrueDarkness;
import mctmods.resourcedatapackloader.common.pack.definitions.sky.DarknessDef;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.platform.NativeImage;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.LightTexture;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightTexture.class) public abstract class LightTextureMixin {
    @Shadow @Final private NativeImage lightPixels;
    @Unique @Nullable private DarknessDef rdpl$darkness;
    @Unique private float rdpl$ambient;

    @WrapOperation(method = "updateLightTexture(F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;getSkyDarken(F)F")) private float rdpl$darkSun(ClientLevel level, float partialTick, Operation<Float> original) {
        rdpl$darkness = TrueDarkness.of(level);
        rdpl$ambient = level.dimensionType().ambientLight();
        return TrueDarkness.sun(rdpl$darkness, level, original.call(level, partialTick));
    }

    @ModifyConstant(method = "updateLightTexture(F)V", constant = @Constant(floatValue = 0.95F), require = 1) private float rdpl$darkScale(float own) { return TrueDarkness.scale(rdpl$darkness, own); }

    @ModifyConstant(method = "updateLightTexture(F)V", constant = {@Constant(floatValue = 0.05F), @Constant(floatValue = 0.04F)}, require = 3) private float rdpl$darkFloor(float own) { return TrueDarkness.floor(rdpl$darkness, own); }

    @ModifyExpressionValue(method = "updateLightTexture(F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LightTexture;getBrightness(Lnet/minecraft/world/level/dimension/DimensionType;I)F"), require = 2) private float rdpl$darkTable(float own) { return TrueDarkness.table(rdpl$darkness, rdpl$ambient, own); }

    @ModifyExpressionValue(method = "updateLightTexture(F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/DimensionSpecialEffects;forceBrightLightmap()Z")) private boolean rdpl$darkEnd(boolean own) { return TrueDarkness.endLight(rdpl$darkness, own); }

    @WrapOperation(method = "updateLightTexture(F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/DimensionSpecialEffects;adjustLightmapColors(Lnet/minecraft/client/multiplayer/ClientLevel;FFFFIILorg/joml/Vector3f;)V")) private void rdpl$nightVision(DimensionSpecialEffects effects, ClientLevel level, float partialTicks, float skyDarken, float blockLightRedFlicker, float skyLight, int pixelX, int pixelY, Vector3f colors, Operation<Void> original) {
        original.call(effects, level, partialTicks, skyDarken, blockLightRedFlicker, skyLight, pixelX, pixelY, colors);
        TrueDarkness.nightVision(rdpl$darkness, colors);
    }

    @ModifyExpressionValue(method = "updateLightTexture(F)V", at = @At(value = "INVOKE", target = "Ljava/lang/Double;floatValue()F"), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/Options;gamma()Lnet/minecraft/client/OptionInstance;"))) private float rdpl$lockedGamma(float own) { return TrueDarkness.gamma(rdpl$darkness, own); }

    @Inject(method = "updateLightTexture(F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/texture/DynamicTexture;upload()V")) private void rdpl$grayNight(float partialTicks, CallbackInfo ci) { TrueDarkness.gray(rdpl$darkness, lightPixels); }
}
