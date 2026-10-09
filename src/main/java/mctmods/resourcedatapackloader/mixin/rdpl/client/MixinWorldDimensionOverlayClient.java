package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.content.worldgen.ContentDimensionRules;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(World.class) public abstract class MixinWorldDimensionOverlayClient {
    @WrapOperation(method = "getSkyColor", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldProvider;getSkyColor(Lnet/minecraft/entity/Entity;F)Lnet/minecraft/util/math/Vec3d;", remap = false)) private Vec3d rdpl$existingSkyColor(WorldProvider provider, Entity cameraEntity, float partialTicks, Operation<Vec3d> original) { return ContentDimensionRules.existingSkyColor((World) (Object) this, partialTicks, original.call(provider, cameraEntity, partialTicks)); }

    @WrapOperation(method = "getCloudColour", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldProvider;getCloudColor(F)Lnet/minecraft/util/math/Vec3d;", remap = false)) private Vec3d rdpl$existingCloudColor(WorldProvider provider, float partialTicks, Operation<Vec3d> original) { return ContentDimensionRules.existingCloudColor((World) (Object) this, original.call(provider, partialTicks)); }

    @WrapOperation(method = "getFogColor", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldProvider;getFogColor(FF)Lnet/minecraft/util/math/Vec3d;")) private Vec3d rdpl$existingFogColor(WorldProvider provider, float p_76562_1_, float p_76562_2_, Operation<Vec3d> original) { return ContentDimensionRules.existingFogColor((World) (Object) this, p_76562_1_, original.call(provider, p_76562_1_, p_76562_2_)); }

    @WrapOperation(method = "getStarBrightness", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldProvider;getStarBrightness(F)F", remap = false)) private float rdpl$existingStarBrightness(WorldProvider provider, float par1, Operation<Float> original) { return ContentDimensionRules.existingStarBrightness((World) (Object) this, original.call(provider, par1)); }
}
