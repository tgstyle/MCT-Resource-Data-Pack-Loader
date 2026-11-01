package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.pack.PackListClient;

import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.repository.Pack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Mixin(Minecraft.class) public abstract class MixinMinecraftPackFailure {
    @Inject(method = "clearResourcePacksOnError", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Options;save()V")) private void rdpl$fallBack(CallbackInfo ci) { PackListClient.failed((Minecraft) (Object) this); }

    @Inject(method = "abortResourcePackRecovery()V", at = @At("HEAD"), cancellable = true) private void rdpl$dropAfterFailedRecovery(CallbackInfo ci) {
        if (PackListClient.dropAfterFailedRecovery((Minecraft) (Object) this)) { ci.cancel(); }
    }

    @Redirect(method = "rollbackResourcePacks", at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;anyMatch(Ljava/util/function/Predicate;)Z")) private boolean rdpl$clearableOrAlwaysOn(Stream<Pack> packs, Predicate<? super Pack> clearable) { return packs.anyMatch(clearable) || PackListClient.holdsAlwaysOn(); }
}
