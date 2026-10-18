package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentTerrain;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.core.Holder;
import net.minecraft.world.clock.WorldClock;
import org.jspecify.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "net.minecraft.world.clock.ServerClockManager$ClockInstance") public abstract class MixinServerClockAdvance {
    @Shadow @Final private @Nullable Holder<WorldClock> holder;

    @ModifyExpressionValue(method = "tick(Z)V", at = @At(value = "FIELD", target = "Lnet/minecraft/world/clock/ServerClockManager$ClockInstance;totalTicks:J", opcode = Opcodes.GETFIELD)) private long rdpl$advanceFromLockedTime(long totalTicks) {
        long locked = ContentTerrain.overworldLock(holder);
        return locked >= 0 ? locked : totalTicks;
    }
}
