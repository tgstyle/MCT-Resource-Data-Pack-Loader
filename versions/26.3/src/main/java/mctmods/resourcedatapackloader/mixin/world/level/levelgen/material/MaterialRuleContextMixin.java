package mctmods.resourcedatapackloader.mixin.world.level.levelgen.material;

import mctmods.resourcedatapackloader.common.worldgen.surface.ContentCraters;

import net.minecraft.world.level.levelgen.material.MaterialRuleContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MaterialRuleContext.class) public abstract class MaterialRuleContextMixin {
    @Shadow private int blockX;
    @Shadow private int blockZ;

    @Inject(method = "getMinSurfaceLevel", at = @At("RETURN"), cancellable = true) private void rdpl$craterFloor(CallbackInfoReturnable<Integer> cir) {
        int sunk = ContentCraters.sunk(blockX, blockZ);
        if (sunk > 0) { cir.setReturnValue(cir.getReturnValueI() - sunk); }
    }
}
