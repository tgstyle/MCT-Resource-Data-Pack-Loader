package mctmods.resourcedatapackloader.mixin.client.telemetry;

import mctmods.resourcedatapackloader.common.pack.loading.ContentServer;

import net.minecraft.client.telemetry.ClientTelemetryManager;
import net.minecraft.client.telemetry.TelemetryEventSender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientTelemetryManager.class) public abstract class ClientTelemetryManagerMixin {
    @Inject(method = "createEventSender", at = @At("HEAD"), cancellable = true) private void rdpl$sendNothing(CallbackInfoReturnable<TelemetryEventSender> cir) { if (ContentServer.privacy()) { cir.setReturnValue(TelemetryEventSender.DISABLED); } }
}
