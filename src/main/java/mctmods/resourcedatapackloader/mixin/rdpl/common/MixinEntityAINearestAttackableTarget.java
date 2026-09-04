package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.entity.ContentThreat;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityAINearestAttackableTarget.class) public abstract class MixinEntityAINearestAttackableTarget {
    @Shadow protected EntityLivingBase targetEntity;

    @Inject(method = "shouldExecute", at = @At("RETURN"), cancellable = true) private void rdpl$docile(CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ() || !(targetEntity instanceof EntityPlayer)) { return; }
        if (ContentThreat.provokes((EntityPlayer) targetEntity, ((IEntityAITarget) this).rdpl$getTaskOwner())) { return; }
        targetEntity = null;
        cir.setReturnValue(Boolean.FALSE);
    }
}
