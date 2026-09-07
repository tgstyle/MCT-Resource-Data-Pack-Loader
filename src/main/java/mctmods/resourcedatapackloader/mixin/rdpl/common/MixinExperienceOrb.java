package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.entity.ContentEntityTicks;

import net.minecraft.world.entity.ExperienceOrb;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ExperienceOrb.class) public abstract class MixinExperienceOrb {
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void rdpl$slowTick(CallbackInfo ci) {
        ExperienceOrb self = (ExperienceOrb) (Object) this;
        if (!ContentEntityTicks.slowedNow(self)) { return; }
        IExperienceOrb aging = (IExperienceOrb) self;
        aging.rdpl$setAge(aging.rdpl$getAge() + 1);
        ci.cancel();
    }
}
