package mctmods.resourcedatapackloader.mixin.world.level.levelgen;

import mctmods.resourcedatapackloader.common.worldgen.surface.ContentCraters;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.world.level.levelgen.SurfaceRules$Context") public abstract class SurfaceRulesContextMixin {
    @Shadow int blockX;
    @Shadow int blockZ;

    @Inject(method = "getMinSurfaceLevel", at = @At("RETURN"), cancellable = true) private void rdpl$craterFloor(CallbackInfoReturnable<Integer> cir) {
        int sunk = ContentCraters.sunk(blockX, blockZ);
        if (sunk > 0) { cir.setReturnValue(cir.getReturnValueI() - sunk); }
    }
}
