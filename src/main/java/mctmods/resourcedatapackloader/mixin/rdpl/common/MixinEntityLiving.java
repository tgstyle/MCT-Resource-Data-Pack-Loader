package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.entity.ContentEntities;
import mctmods.resourcedatapackloader.content.entity.EntityStorage;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityLiving.class) public abstract class MixinEntityLiving {
    @Shadow protected abstract SoundEvent getAmbientSound();
    @Shadow protected abstract boolean canDespawn();

    @Redirect(method = "despawnEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLiving;canDespawn()Z"))
    private boolean rdpl$despawn(EntityLiving self) { return ContentEntities.despawns(self, canDespawn()); }

    @Inject(method = "canBeLeashedTo", at = @At("RETURN"), cancellable = true) private void rdpl$leashable(EntityPlayer player, CallbackInfoReturnable<Boolean> cir) {
        if (!ContentEntities.leashable((EntityLiving) (Object) this)) { return; }
        cir.setReturnValue(Boolean.TRUE);
    }

    @Inject(method = "canBeSteered", at = @At("RETURN"), cancellable = true) private void rdpl$steerable(CallbackInfoReturnable<Boolean> cir) {
        if (!ContentEntities.steerable((EntityLiving) (Object) this)) { return; }
        cir.setReturnValue(Boolean.TRUE);
    }

    @Redirect(method = "playLivingSound", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLiving;getAmbientSound()Lnet/minecraft/util/SoundEvent;"))
    private SoundEvent rdpl$ambient(EntityLiving self) {
        SoundEvent wanted = ContentEntities.soundEvent(self, 0);
        return wanted != null ? wanted : getAmbientSound();
    }

    @Inject(method = "updateEntityActionState", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/profiler/Profiler;startSection(Ljava/lang/String;)V", args = "ldc=sensing"), cancellable = true)
    private void rdpl$stalled(CallbackInfo ci) {
        if (EntityStorage.stalled((EntityLiving) (Object) this)) { ci.cancel(); }
    }

    @Inject(method = "getMaxFallHeight", at = @At("RETURN"), cancellable = true) private void rdpl$fallHeight(CallbackInfoReturnable<Integer> cir) { cir.setReturnValue(ContentEntities.maxFallHeight((EntityLiving) (Object) this, cir.getReturnValueI())); }
}
