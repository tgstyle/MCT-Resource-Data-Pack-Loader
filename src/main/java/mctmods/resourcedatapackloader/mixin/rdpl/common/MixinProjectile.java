package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.entity.ContentEntityTicks;

import net.minecraft.world.entity.projectile.Projectile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Projectile.class) public abstract class MixinProjectile {
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void rdpl$slowTick(CallbackInfo ci) {
        if (ContentEntityTicks.slowedNow((Projectile) (Object) this)) { ci.cancel(); }
    }
}
