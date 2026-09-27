package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.ContentServer;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({MinecraftServer.class, DedicatedServer.class}) public abstract class MixinServerFlight {
    @Inject(method = "allowFlight()Z", at = @At("HEAD"), cancellable = true) private void rdpl$flight(CallbackInfoReturnable<Boolean> cir) {
        Boolean asked = ContentServer.flight();
        if (asked != null) { cir.setReturnValue(asked); }
    }
}
