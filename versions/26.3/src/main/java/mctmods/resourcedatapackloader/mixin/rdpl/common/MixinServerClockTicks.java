package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentTerrain;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.core.Holder;
import net.minecraft.world.clock.ServerClockManager;
import net.minecraft.world.clock.WorldClock;
import org.jspecify.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerClockManager.ServerClockInstance.class) public abstract class MixinServerClockTicks {
    @Shadow @Final private @Nullable Holder<WorldClock> holder;

    @Inject(method = "totalTicks()J", at = @At("HEAD"), cancellable = true) private void rdpl$lockedTime(CallbackInfoReturnable<Long> cir) {
        if (holder == null) { return; }
        long locked = ContentTerrain.lockedTime(holder);
        if (locked >= 0) { cir.setReturnValue(locked); }
    }

    @ModifyExpressionValue(method = "tick(Z)V", at = @At(value = "FIELD", target = "Lnet/minecraft/world/clock/ServerClockManager$ServerClockInstance;totalTicks:J", opcode = Opcodes.GETFIELD)) private long rdpl$advanceFromLockedTime(long totalTicks) {
        long locked = ContentTerrain.overworldLock(holder);
        return locked >= 0 ? locked : totalTicks;
    }

    @Inject(method = "partialTick()F", at = @At("HEAD"), cancellable = true) private void rdpl$lockedPartial(CallbackInfoReturnable<Float> cir) { if (holder != null && ContentTerrain.lockedTime(holder) >= 0) { cir.setReturnValue(0.0F); } }
}
