package mctmods.resourcedatapackloader.mixin.client.renderer;

import mctmods.resourcedatapackloader.client.util.TrueDarkness;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.buffers.Std140Builder;
import net.minecraft.client.renderer.Lightmap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Lightmap.class) public class LightmapMixin {
    @WrapOperation(method = "render(Lnet/minecraft/client/renderer/state/LightmapRenderState;)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/buffers/Std140Builder;putFloat(F)Lcom/mojang/blaze3d/buffers/Std140Builder;", ordinal = 5)) private Std140Builder rdpl$grayLift(Std140Builder builder, float value, Operation<Std140Builder> original) { return original.call(builder, value).putFloat(TrueDarkness.grayLift()).putFloat(TrueDarkness.ambientBlend()); }
}
