package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.entity.ContentEntities;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Shulker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Shulker.class) public abstract class MixinShulker {
    @Inject(method = "teleportSomewhere", at = @At("HEAD"), cancellable = true)
    private void rdpl$staysPut(CallbackInfoReturnable<Boolean> cir) {
        if (ContentEntities.staysPut(Entity.class.cast(this))) { cir.setReturnValue(Boolean.FALSE); }
    }
}
