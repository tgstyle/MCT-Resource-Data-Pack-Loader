package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentTerrain;

import net.minecraft.core.Holder;
import net.minecraft.world.clock.ServerClockManager;
import net.minecraft.world.clock.WorldClock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerClockManager.class) public abstract class MixinServerClockTicks {
    @Inject(method = "getTotalTicks(Lnet/minecraft/core/Holder;)J", at = @At("HEAD"), cancellable = true)
    private void rdpl$lockedTime(Holder<WorldClock> definition, CallbackInfoReturnable<Long> cir) {
        long locked = ContentTerrain.lockedTime(definition);
        if (locked >= 0) { cir.setReturnValue(locked); }
    }

    @Inject(method = "getPartialTick(Lnet/minecraft/core/Holder;)F", at = @At("HEAD"), cancellable = true)
    private void rdpl$lockedPartial(Holder<WorldClock> definition, CallbackInfoReturnable<Float> cir) {
        if (ContentTerrain.lockedTime(definition) >= 0) { cir.setReturnValue(0.0F); }
    }
}
