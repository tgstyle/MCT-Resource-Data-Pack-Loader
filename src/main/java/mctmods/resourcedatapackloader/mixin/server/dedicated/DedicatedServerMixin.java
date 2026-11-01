package mctmods.resourcedatapackloader.mixin.server.dedicated;

import mctmods.resourcedatapackloader.common.pack.loading.ContentServer;

import net.minecraft.server.dedicated.DedicatedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DedicatedServer.class) public abstract class DedicatedServerMixin {
    @Inject(method = "allowFlight()Z", at = @At("HEAD"), cancellable = true) private void rdpl$flight(CallbackInfoReturnable<Boolean> cir) {
        Boolean asked = ContentServer.flight();
        if (asked != null) { cir.setReturnValue(asked); }
    }

    @Inject(method = "enforceSecureProfile", at = @At("HEAD"), cancellable = true) private void rdpl$acceptUnsigned(CallbackInfoReturnable<Boolean> cir) { if (ContentServer.privacy()) { cir.setReturnValue(false); } }
}
