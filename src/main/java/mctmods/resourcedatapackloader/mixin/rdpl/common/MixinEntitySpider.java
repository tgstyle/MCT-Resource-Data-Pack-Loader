package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.entity.ContentEntities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntitySpider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntitySpider.class) public abstract class MixinEntitySpider {
    @Inject(method = "isOnLadder", at = @At("RETURN"), cancellable = true) private void rdpl$grounded(CallbackInfoReturnable<Boolean> cir) {
        Boolean wanted = ContentEntities.climbs((Entity) (Object) this);
        if (wanted == null || wanted) { return; }
        cir.setReturnValue(Boolean.FALSE);
    }
}
