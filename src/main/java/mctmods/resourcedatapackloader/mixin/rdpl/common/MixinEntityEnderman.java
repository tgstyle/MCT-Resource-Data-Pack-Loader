package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.entity.ContentEntities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityEnderman;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityEnderman.class) public abstract class MixinEntityEnderman {
    @Inject(method = "teleportTo", at = @At("HEAD"), cancellable = true) private void rdpl$staysPut(double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
        if (ContentEntities.teleports((Entity) (Object) this)) { return; }
        cir.setReturnValue(Boolean.FALSE);
    }
}
