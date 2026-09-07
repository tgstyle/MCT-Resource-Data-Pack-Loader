package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.entity.ContentEntityTicks;

import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
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
}
