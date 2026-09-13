package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.entity.ContentEntities;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.EnderMan;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnderMan.class) public abstract class MixinEnderMan {
    @Inject(method = "teleport()Z", at = @At("HEAD"), cancellable = true)
    private void rdpl$staysPut(CallbackInfoReturnable<Boolean> cir) {
        if (!ContentEntities.teleports((Entity) (Object) this)) { cir.setReturnValue(Boolean.FALSE); }
    }
}
