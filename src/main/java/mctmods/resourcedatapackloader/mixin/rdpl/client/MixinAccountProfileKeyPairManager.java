package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.util.Config;

import net.minecraft.client.multiplayer.AccountProfileKeyPairManager;
import net.minecraft.world.entity.player.ProfileKeyPair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Mixin(AccountProfileKeyPairManager.class) public abstract class MixinAccountProfileKeyPairManager {
    @Inject(method = "prepareKeyPair", at = @At("HEAD"), cancellable = true) private void rdpl$signNothing(CallbackInfoReturnable<CompletableFuture<Optional<ProfileKeyPair>>> cir) {
        if (Config.tweaks.privacy()) { cir.setReturnValue(CompletableFuture.completedFuture(Optional.empty())); }
    }
}
