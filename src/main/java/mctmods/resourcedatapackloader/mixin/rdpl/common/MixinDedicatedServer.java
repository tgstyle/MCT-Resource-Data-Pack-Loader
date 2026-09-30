package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.ContentServer;

import net.minecraft.server.dedicated.DedicatedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DedicatedServer.class) public abstract class MixinDedicatedServer {
    @Inject(method = "enforceSecureProfile", at = @At("HEAD"), cancellable = true) private void rdpl$acceptUnsigned(CallbackInfoReturnable<Boolean> cir) {
        if (ContentServer.privacy()) { cir.setReturnValue(false); }
    }
}
