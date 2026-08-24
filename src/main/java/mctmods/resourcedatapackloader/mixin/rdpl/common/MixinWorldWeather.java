package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentWeather;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class) public abstract class MixinWorldWeather {
    @Inject(method = "isRainingAt", at = @At("HEAD"), cancellable = true)
    private void rdpl$rainBelowCeiling(BlockPos position, CallbackInfoReturnable<Boolean> cir) {
        if (ContentWeather.above((World) (Object) this, position.getY())) { cir.setReturnValue(false); }
    }

    @Inject(method = "canSnowAt", at = @At("HEAD"), cancellable = true)
    private void rdpl$snowBelowCeiling(BlockPos pos, boolean checkLight, CallbackInfoReturnable<Boolean> cir) {
        if (ContentWeather.above((World) (Object) this, pos.getY())) { cir.setReturnValue(false); }
    }
}
