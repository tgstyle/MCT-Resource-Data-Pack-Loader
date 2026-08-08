package mctmods.resourcedatapackloader.mixin;

import mctmods.resourcedatapackloader.util.Config;

import net.minecraft.client.Minecraft;
import net.minecraft.server.integrated.IntegratedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft {
    @Shadow private IntegratedServer integratedServer;

    @ModifyArg(
            method = "launchIntegratedServer",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/LoadingScreenRenderer;displayLoadingString(Ljava/lang/String;)V", ordinal = 0),
            index = 0
    )
    private String rdpl$showSpawnPercent(String message) {
        if (!Config.client.loadingScreenPercent || integratedServer == null || message.isEmpty()) { return message; }

        String task = integratedServer.currentTask;
        if (task == null) { return message; }

        return message + " " + integratedServer.percentDone + "%";
    }
}
