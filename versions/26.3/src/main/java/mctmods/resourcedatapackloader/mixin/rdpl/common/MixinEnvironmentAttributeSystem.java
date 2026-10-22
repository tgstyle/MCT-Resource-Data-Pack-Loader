package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentEnvironmentLayers;
import mctmods.resourcedatapackloader.content.worldgen.LevelClock;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.attribute.EnvironmentAttributeSystem;
import net.minecraft.world.clock.ClockManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnvironmentAttributeSystem.class) public abstract class MixinEnvironmentAttributeSystem {
    @WrapOperation(method = "addDynamicLayers(Lnet/minecraft/world/attribute/EnvironmentAttributeSystem$Builder;Lnet/minecraft/world/level/Level;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;clockManager()Lnet/minecraft/world/clock/ClockManager;")) private static ClockManager rdpl$levelClock(Level level, Operation<ClockManager> original) { return new LevelClock(level, original.call(level)); }

    @Inject(method = "addStaticLayers(Lnet/minecraft/world/attribute/EnvironmentAttributeSystem$Builder;Lnet/minecraft/world/level/LevelAccessor;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/attribute/EnvironmentAttributeSystem;addBiomeLayer(Lnet/minecraft/world/attribute/EnvironmentAttributeSystem$Builder;Lnet/minecraft/core/HolderLookup;Lnet/minecraft/world/level/biome/BiomeManager;)V")) private static void rdpl$beforeBiomes(EnvironmentAttributeSystem.Builder builder, LevelAccessor level, CallbackInfo ci) {
        if (level instanceof Level real) { ContentEnvironmentLayers.beforeBiomes(builder, real); }
    }

    @Inject(method = "addStaticLayers(Lnet/minecraft/world/attribute/EnvironmentAttributeSystem$Builder;Lnet/minecraft/world/level/LevelAccessor;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/attribute/EnvironmentAttributeSystem;addBiomeLayer(Lnet/minecraft/world/attribute/EnvironmentAttributeSystem$Builder;Lnet/minecraft/core/HolderLookup;Lnet/minecraft/world/level/biome/BiomeManager;)V", shift = At.Shift.AFTER)) private static void rdpl$afterBiomes(EnvironmentAttributeSystem.Builder builder, LevelAccessor level, CallbackInfo ci) {
        if (level instanceof Level real) { ContentEnvironmentLayers.afterBiomes(builder, real); }
    }
}
