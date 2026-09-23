package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentWeather;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Level.class) public abstract class MixinLevelWeather {
    @Inject(method = "isRainingAt", at = @At("HEAD"), cancellable = true)
    private void rdpl$rainBelowCeiling(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (ContentWeather.above(Level.class.cast(this), pos.getY())) { cir.setReturnValue(false); }
    }
}
