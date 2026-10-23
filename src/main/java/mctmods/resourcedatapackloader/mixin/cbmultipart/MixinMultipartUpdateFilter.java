package mctmods.resourcedatapackloader.mixin.cbmultipart;

import mctmods.resourcedatapackloader.util.compat.CbmpPlayerCubes;

import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import scala.Tuple2;

@SuppressWarnings("public-target") @Mixin(targets = "codechicken.multipart.handler.MultipartSPH$$anonfun$onTickEnd$2$$anonfun$apply$2", remap = false) public abstract class MixinMultipartUpdateFilter {
    @Inject(method = "apply(Lscala/Tuple2;)Z", at = @At("RETURN"), cancellable = true) private void rdpl$requireCube(Tuple2<BlockPos, Object> entry, CallbackInfoReturnable<Boolean> cir) { if (cir.getReturnValueZ() && !CbmpPlayerCubes.watching(entry._1())) { cir.setReturnValue(false); } }
}
