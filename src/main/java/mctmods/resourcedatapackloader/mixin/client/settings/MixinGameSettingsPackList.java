package mctmods.resourcedatapackloader.mixin.client.settings;

import mctmods.resourcedatapackloader.client.util.PackListClient;

import net.minecraft.client.settings.GameSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameSettings.class) public abstract class MixinGameSettingsPackList {
    @Inject(method = "loadOptions()V", at = @At("RETURN")) private void rdpl$packList(CallbackInfo ci) { PackListClient.settingsLoaded((GameSettings) (Object) this); }
}
