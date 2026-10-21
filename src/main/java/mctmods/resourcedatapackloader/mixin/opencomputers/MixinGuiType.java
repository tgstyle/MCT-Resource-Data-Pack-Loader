package mctmods.resourcedatapackloader.mixin.opencomputers;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo @Mixin(targets = "li.cil.oc.common.GuiType$", remap = false) public class MixinGuiType {
    @Inject(method = "extractY", at = @At("HEAD"), cancellable = true, remap = false) private void rdpl$signedY(int value, CallbackInfoReturnable<Integer> cir) { cir.setReturnValue(value << 8 >> 8); }
}
