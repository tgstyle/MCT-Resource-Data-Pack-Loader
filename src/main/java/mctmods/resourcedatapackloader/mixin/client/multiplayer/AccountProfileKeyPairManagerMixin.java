package mctmods.resourcedatapackloader.mixin.client.multiplayer;

import mctmods.resourcedatapackloader.common.pack.loading.ContentServer;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.multiplayer.AccountProfileKeyPairManager;
import net.minecraft.world.entity.player.ProfileKeyPair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AccountProfileKeyPairManager.class) public abstract class AccountProfileKeyPairManagerMixin {
    @Inject(method = "prepareKeyPair", at = @At("HEAD"), cancellable = true) private void rdpl$signNothing(CallbackInfoReturnable<CompletableFuture<Optional<ProfileKeyPair>>> cir) { if (ContentServer.privacy()) { cir.setReturnValue(CompletableFuture.completedFuture(Optional.empty())); } }
}
