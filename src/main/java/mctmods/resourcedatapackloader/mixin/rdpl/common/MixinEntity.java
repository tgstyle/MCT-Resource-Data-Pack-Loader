package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.ContentBoards;
import mctmods.resourcedatapackloader.content.entity.ContentEntities;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class) public abstract class MixinEntity {
    @Inject(method = "setFire", at = @At("HEAD"), cancellable = true) private void rdpl$neverCatchesFire(int seconds, CallbackInfo ci) {
        Entity self = (Entity) (Object) this;
        if (!ContentEntities.fireproof(self) && !ContentEntities.struckFireless(self) && !ContentBoards.piece(self)) { return; }
        ci.cancel();
    }
}
