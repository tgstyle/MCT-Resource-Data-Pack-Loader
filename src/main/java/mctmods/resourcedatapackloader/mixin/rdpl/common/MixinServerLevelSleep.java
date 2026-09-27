package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentDimensions;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.clock.ServerClockManager;
import net.minecraft.world.clock.WorldClock;
import net.neoforged.neoforge.common.util.ClockAdjustment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerLevel.class) public abstract class MixinServerLevelSleep {
    @WrapOperation(method = "tick(Ljava/util/function/BooleanSupplier;)V", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/common/util/ClockAdjustment;apply(Lnet/minecraft/world/clock/ServerClockManager;Lnet/minecraft/core/Holder;)V"))
    private void rdpl$sleepKeepsTime(ClockAdjustment adjustment, ServerClockManager clocks, Holder<WorldClock> clock, Operation<Void> original) {
        if (ContentDimensions.def((ServerLevel) (Object) this) == null) { original.call(adjustment, clocks, clock); }
    }

    @WrapOperation(method = "tick(Ljava/util/function/BooleanSupplier;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;resetWeatherCycle()V"))
    private void rdpl$sleepKeepsWeather(ServerLevel level, Operation<Void> original) {
        if (ContentDimensions.def(level) == null) { original.call(level); }
    }
}
