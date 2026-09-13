package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.entity.ContentEntities;

import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class) public abstract class MixinEntityFire {
    @Inject(method = "setSecondsOnFire", at = @At("HEAD"), cancellable = true)
    private void rdpl$noFireFromBlows(int seconds, CallbackInfo ci) {
        if (ContentEntities.struckFireless((Entity) (Object) this)) { ci.cancel(); }
    }
}
