package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.entity.ContentEntities;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Spider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Spider.class) public abstract class MixinSpider {
    @Inject(method = "onClimbable", at = @At("RETURN"), cancellable = true)
    private void rdpl$grounded(CallbackInfoReturnable<Boolean> cir) {
        if (Boolean.FALSE.equals(ContentEntities.climbs(Entity.class.cast(this)))) { cir.setReturnValue(Boolean.FALSE); }
    }
}
