package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.entity.ContentEntityTicks;
import mctmods.resourcedatapackloader.content.worldgen.ContentPhysics;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class) public abstract class MixinItemEntity {
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void rdpl$slowTick(CallbackInfo ci) {
        ItemEntity self = (ItemEntity) (Object) this;
        if (!ContentEntityTicks.slowedNow(self)) { return; }
        IItemEntity aging = (IItemEntity) self;
        if (aging.rdpl$getAge() != Short.MIN_VALUE) { aging.rdpl$setAge(aging.rdpl$getAge() + 1); }
        ci.cancel();
    }

    @ModifyConstant(method = "tick", constant = @Constant(doubleValue = -0.04D))
    private double rdpl$worldGravity(double vanilla) { return ContentPhysics.scaledFall((Entity) (Object) this, vanilla); }
}
