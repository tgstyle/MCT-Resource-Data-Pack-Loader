package mctmods.resourcedatapackloader.mixin.mca;

import mctmods.resourcedatapackloader.util.compat.MCADeadApi;

import mca.core.MCA;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MCA.class, remap = false) public abstract class MixinMCACrashReports {
    @Inject(method = "checkForCrashReports", at = @At("HEAD"), cancellable = true) private void rdpl$skipDeadCrashUpload(CallbackInfo ci) {
        MCADeadApi.said();
        ci.cancel();
    }
}
