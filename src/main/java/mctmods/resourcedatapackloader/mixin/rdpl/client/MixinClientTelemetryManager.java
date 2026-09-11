package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.util.Config;

import net.minecraft.client.telemetry.ClientTelemetryManager;
import net.minecraft.client.telemetry.TelemetryEventSender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientTelemetryManager.class) public abstract class MixinClientTelemetryManager {
    @Inject(method = "createEventSender", at = @At("HEAD"), cancellable = true) private void rdpl$sendNothing(CallbackInfoReturnable<TelemetryEventSender> cir) {
        if (Config.tweaks.privacy()) { cir.setReturnValue(TelemetryEventSender.DISABLED); }
    }
}
