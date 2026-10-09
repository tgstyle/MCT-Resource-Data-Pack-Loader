package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentDimensionRules;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(World.class) public abstract class MixinWorldDimensionOverlay {
    @WrapOperation(method = "getWorldTime", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldProvider;getWorldTime()J", remap = false)) private long rdpl$existingWorldTime(WorldProvider provider, Operation<Long> original) { return ContentDimensionRules.existingWorldTime((World) (Object) this, original.call(provider)); }

    @WrapOperation(method = "isDaytime", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldProvider;isDaytime()Z", remap = false)) private boolean rdpl$existingDaytime(WorldProvider provider, Operation<Boolean> original) { return ContentDimensionRules.existingDaytime((World) (Object) this, original.call(provider)); }

    @WrapOperation(method = "getCelestialAngle", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldProvider;calculateCelestialAngle(JF)F")) private float rdpl$existingCelestialAngle(WorldProvider provider, long worldTime, float partialTicks, Operation<Float> original) { return ContentDimensionRules.existingCelestialAngle((World) (Object) this, worldTime, partialTicks, original.call(provider, worldTime, partialTicks)); }

    @WrapOperation(method = "updateWeather", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldProvider;updateWeather()V", remap = false)) private void rdpl$existingWeather(WorldProvider provider, Operation<Void> original) { ContentDimensionRules.existingWeather((World) (Object) this, () -> original.call(provider)); }

    @WrapOperation(method = "canBlockFreeze", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldProvider;canBlockFreeze(Lnet/minecraft/util/math/BlockPos;Z)Z", remap = false)) private boolean rdpl$existingFreezes(WorldProvider provider, BlockPos pos, boolean byWater, Operation<Boolean> original) { return ContentDimensionRules.existingFreezes((World) (Object) this, original.call(provider, pos, byWater)); }

    @WrapOperation(method = "canSnowAt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldProvider;canSnowAt(Lnet/minecraft/util/math/BlockPos;Z)Z", remap = false)) private boolean rdpl$existingSnows(WorldProvider provider, BlockPos pos, boolean checkLight, Operation<Boolean> original) { return ContentDimensionRules.existingSnows((World) (Object) this, original.call(provider, pos, checkLight)); }
}
