package mctmods.resourcedatapackloader.mixin;

import mctmods.resourcedatapackloader.content.entity.ContentEntities;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityLiving.class)
public abstract class MixinEntityLiving {
    @Inject(method = "canDespawn", at = @At("RETURN"), cancellable = true)
    private void rdpl$despawn(CallbackInfoReturnable<Boolean> cir) { cir.setReturnValue(ContentEntities.despawns((EntityLiving) (Object) this, cir.getReturnValueZ())); }

    @Inject(method = "canBeLeashedTo", at = @At("RETURN"), cancellable = true)
    private void rdpl$leashable(EntityPlayer player, CallbackInfoReturnable<Boolean> cir) {
        if (!ContentEntities.leashable((EntityLiving) (Object) this)) { return; }

        cir.setReturnValue(Boolean.TRUE);
    }

    @Inject(method = "canBeSteered", at = @At("RETURN"), cancellable = true)
    private void rdpl$steerable(CallbackInfoReturnable<Boolean> cir) {
        if (!ContentEntities.steerable((EntityLiving) (Object) this)) { return; }

        cir.setReturnValue(Boolean.TRUE);
    }

    @Inject(method = "getMaxFallHeight", at = @At("RETURN"), cancellable = true)
    private void rdpl$fallHeight(CallbackInfoReturnable<Integer> cir) { cir.setReturnValue(ContentEntities.maxFallHeight((EntityLiving) (Object) this, cir.getReturnValueI())); }
}
