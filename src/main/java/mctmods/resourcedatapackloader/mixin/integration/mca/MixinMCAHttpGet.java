package mctmods.resourcedatapackloader.mixin.integration.mca;

import mctmods.resourcedatapackloader.core.integration.mca.MCADeadApi;

import mca.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Util.class, remap = false) public abstract class MixinMCAHttpGet {
    @Inject(method = "httpGet", at = @At("HEAD"), cancellable = true) private static void rdpl$skipDeadApi(String url, CallbackInfoReturnable<String> cir) {
        MCADeadApi.said();
        cir.setReturnValue("");
    }
}
