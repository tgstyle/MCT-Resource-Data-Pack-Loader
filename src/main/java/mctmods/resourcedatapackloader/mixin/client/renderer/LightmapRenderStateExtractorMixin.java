package mctmods.resourcedatapackloader.mixin.client.renderer;

import mctmods.resourcedatapackloader.client.util.TrueDarkness;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightmapRenderStateExtractor;
import net.minecraft.client.renderer.state.LightmapRenderState;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightmapRenderStateExtractor.class) public class LightmapRenderStateExtractorMixin {
    @ModifyExpressionValue(method = "extract(Lnet/minecraft/client/renderer/state/LightmapRenderState;F)V", at = @At(value = "INVOKE", target = "Ljava/lang/Double;floatValue()F", ordinal = 0), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/Options;gamma()Lnet/minecraft/client/OptionInstance;"))) private float rdpl$lockedGamma(float own) { return TrueDarkness.gamma(TrueDarkness.of(Minecraft.getInstance().level), own); }
    @Inject(method = "extract(Lnet/minecraft/client/renderer/state/LightmapRenderState;F)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/LightmapRenderStateExtractor;needsUpdate:Z", opcode = Opcodes.PUTFIELD)) private void rdpl$darkAmbient(LightmapRenderState renderState, float partialTicks, CallbackInfo ci, @Local ClientLevel level) { TrueDarkness.ambient(renderState, level); }
}
