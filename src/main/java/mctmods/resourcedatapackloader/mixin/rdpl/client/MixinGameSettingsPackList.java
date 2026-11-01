package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.pack.PackListClient;

import net.minecraft.client.settings.GameSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameSettings.class) public abstract class MixinGameSettingsPackList {
    @Inject(method = "loadOptions()V", at = @At("RETURN")) private void rdpl$packList(CallbackInfo ci) { PackListClient.settingsLoaded((GameSettings) (Object) this); }
}
