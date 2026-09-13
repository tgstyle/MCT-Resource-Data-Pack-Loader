package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.def.EntityVariantDef;
import mctmods.resourcedatapackloader.content.entity.ContentEntities;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class) public abstract class MixinMobReach {
    @Inject(method = "getAttackBoundingBox", at = @At("RETURN"), cancellable = true)
    private void rdpl$ownReach(CallbackInfoReturnable<AABB> cir) {
        Mob self = Mob.class.cast(this);
        EntityVariantDef def = ContentEntities.def(self);
        if (def == null || def.combat().attackReach() <= 0.0F) { return; }
        double wanted = def.combat().attackReach() - self.getBbWidth() / 2.0D;
        if (wanted > 0.0D) { cir.setReturnValue(cir.getReturnValue().inflate(wanted, 0.0D, wanted)); }
    }
}
