package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.pack.PackListClient;

import net.minecraft.client.Options;
import net.minecraft.server.packs.repository.PackRepository;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Options.class) public abstract class MixinOptionsPackList {
    @Inject(method = "loadSelectedResourcePacks", at = @At("TAIL")) private void rdpl$settleDefaults(PackRepository repository, CallbackInfo ci) { PackListClient.settleDefaults(repository); }
}
