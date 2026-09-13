package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.entity.ContentEntities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityRabbit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRabbit.class) public abstract class MixinEntityRabbit {
    @Inject(method = "updateAITasks", at = @At("HEAD"), cancellable = true) private void rdpl$walks(CallbackInfo ci) {
        if (ContentEntities.walks((Entity) (Object) this)) { ci.cancel(); }
    }
}
