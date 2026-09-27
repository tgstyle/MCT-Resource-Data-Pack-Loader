package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentTerrain;

import net.minecraft.core.Holder;
import net.minecraft.world.clock.ServerClockManager;
import net.minecraft.world.clock.WorldClock;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerClockManager.ServerClockInstance.class) public abstract class MixinServerClockTicks {
    @Shadow @Final private @Nullable Holder<WorldClock> holder;

    @Inject(method = "totalTicks()J", at = @At("HEAD"), cancellable = true)
    private void rdpl$lockedTime(CallbackInfoReturnable<Long> cir) {
        if (holder == null) { return; }
        long locked = ContentTerrain.lockedTime(holder);
        if (locked >= 0) { cir.setReturnValue(locked); }
    }

    @Inject(method = "partialTick()F", at = @At("HEAD"), cancellable = true)
    private void rdpl$lockedPartial(CallbackInfoReturnable<Float> cir) {
        if (holder != null && ContentTerrain.lockedTime(holder) >= 0) { cir.setReturnValue(0.0F); }
    }
}
