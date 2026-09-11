package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.entity.ContentEntities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityShulker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityShulker.class) public abstract class MixinEntityShulker {
    @Inject(method = "tryTeleportToNewPosition", at = @At("HEAD"), cancellable = true) private void rdpl$staysPut(CallbackInfoReturnable<Boolean> cir) {
        if (ContentEntities.teleports((Entity) (Object) this)) { return; }
        cir.setReturnValue(Boolean.FALSE);
    }
}
