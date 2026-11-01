package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.pack.PackListClient;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourcePack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.List;

@Mixin(Minecraft.class) public abstract class MixinMinecraftPackFailure {
    @Redirect(method = "refreshResources()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/IReloadableResourceManager;reloadResources(Ljava/util/List;)V", ordinal = 1)) private void rdpl$recover(IReloadableResourceManager manager, List<IResourcePack> list) { PackListClient.recover(manager, list); }

    @Inject(method = "refreshResources()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/settings/GameSettings;saveOptions()V")) private void rdpl$keepFallback(CallbackInfo ci) { PackListClient.keepFallback(); }
}
