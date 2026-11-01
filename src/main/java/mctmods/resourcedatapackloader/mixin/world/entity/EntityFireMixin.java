package mctmods.resourcedatapackloader.mixin.world.entity;

import mctmods.resourcedatapackloader.common.board.ContentBoards;
import mctmods.resourcedatapackloader.common.entities.ContentEntities;

import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class) public abstract class EntityFireMixin {
    @Inject(method = "setSecondsOnFire", at = @At("HEAD"), cancellable = true) private void rdpl$noFireFromBlows(int seconds, CallbackInfo ci) {
        Entity self = (Entity) (Object) this;
        if (ContentEntities.struckFireless(self) || ContentBoards.piece(self)) { ci.cancel(); }
    }
}
