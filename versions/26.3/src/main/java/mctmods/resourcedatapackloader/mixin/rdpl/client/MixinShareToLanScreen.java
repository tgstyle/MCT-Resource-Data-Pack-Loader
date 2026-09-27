package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.content.ContentServer;

import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.WorldOptionsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldOptionsScreen.class) public abstract class MixinShareToLanScreen {
    @Shadow private Boolean wantedGuestCommandAccess;
    @Shadow private CycleButton<Boolean> guestCommandAccessButton;

    @Inject(method = "updateGuestCommandAccessButton(Lnet/minecraft/client/server/IntegratedServer;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/WorldOptionsScreen;updateForceGameModeButton(Lnet/minecraft/client/server/IntegratedServer;)V")) private void rdpl$lockCommands(CallbackInfo ci) {
        if (ContentServer.lanCommands() || guestCommandAccessButton == null) { return; }
        wantedGuestCommandAccess = Boolean.FALSE;
        guestCommandAccessButton.setValue(Boolean.FALSE);
        guestCommandAccessButton.active = false;
    }
}
