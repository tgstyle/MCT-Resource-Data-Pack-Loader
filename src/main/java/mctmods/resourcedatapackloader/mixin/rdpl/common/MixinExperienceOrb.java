package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.entity.ContentEntityTicks;
import mctmods.resourcedatapackloader.content.worldgen.ContentPhysics;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
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

    @ModifyConstant(method = "tick", constant = @Constant(doubleValue = -0.03D))
    private double rdpl$worldGravity(double vanilla) { return ContentPhysics.scaledFall((Entity) (Object) this, vanilla); }
}
