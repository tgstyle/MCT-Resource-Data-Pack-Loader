package mctmods.resourcedatapackloader.mixin.cbmultipart;

import mctmods.resourcedatapackloader.util.compat.CbmpPlayerCubes;

import net.minecraft.entity.player.EntityPlayerMP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("public-target") @Mixin(targets = "codechicken.multipart.handler.MultipartSPH$$anonfun$onTickEnd$2", remap = false) public abstract class MixinMultipartUpdatePlayer {
    @Inject(method = "apply(Lnet/minecraft/entity/player/EntityPlayerMP;)V", at = @At("HEAD")) private void rdpl$beginPlayer(EntityPlayerMP player, CallbackInfo ci) { CbmpPlayerCubes.begin(player); }

    @Inject(method = "apply(Lnet/minecraft/entity/player/EntityPlayerMP;)V", at = @At("RETURN")) private void rdpl$endPlayer(EntityPlayerMP player, CallbackInfo ci) { CbmpPlayerCubes.begin(null); }
}
