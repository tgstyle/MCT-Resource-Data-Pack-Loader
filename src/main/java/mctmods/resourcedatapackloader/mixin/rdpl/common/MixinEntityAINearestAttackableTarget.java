package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.entity.ContentThreat;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityAINearestAttackableTarget.class) public abstract class MixinEntityAINearestAttackableTarget {
    @Shadow protected EntityLivingBase targetEntity;

    @Inject(method = "shouldExecute", at = @At("RETURN"), cancellable = true) private void rdpl$docile(CallbackInfoReturnable<Boolean> cir) {
        if (ContentThreat.docile(cir.getReturnValueZ(), targetEntity, ((IEntityAITarget) this).rdpl$getTaskOwner())) {
            targetEntity = null;
            cir.setReturnValue(Boolean.FALSE);
        }
    }
}
