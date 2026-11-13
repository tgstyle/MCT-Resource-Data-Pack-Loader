package mctmods.resourcedatapackloader.mixin.client.renderer.fog;

import mctmods.resourcedatapackloader.client.renderer.sky.ContentFogSampler;
import mctmods.resourcedatapackloader.client.util.ClientCompat;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.renderer.fog.FogRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FogRenderer.class) public abstract class FogRendererMixin {
    @WrapOperation(method = "computeFogColor(Lnet/minecraft/client/Camera;FLnet/minecraft/client/multiplayer/ClientLevel;IFLorg/joml/Vector4f;)V", at = @At(value = "INVOKE", target = ClientCompat.NIGHT_VISION_SCALE)) private float rdpl$tintedVisionUnboosted(LivingEntity camera, float a, Operation<Float> original) { return ContentFogSampler.visionStrength(ContentFogSampler.look(camera.level()), camera, a) > 0.0F ? 0.0F : original.call(camera, a); }
}
