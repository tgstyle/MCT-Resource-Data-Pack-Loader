package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.entity.ContentThreat;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIFindEntityNearestPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityAIFindEntityNearestPlayer.class) public abstract class MixinEntityAIFindEntityNearestPlayer {
    @Shadow @Final private EntityLiving entityLiving;
    @Shadow private EntityLivingBase entityTarget;

    @Inject(method = "shouldExecute", at = @At("RETURN"), cancellable = true) private void rdpl$docile(CallbackInfoReturnable<Boolean> cir) {
        if (ContentThreat.docile(cir.getReturnValueZ(), entityTarget, entityLiving)) {
            entityTarget = null;
            cir.setReturnValue(Boolean.FALSE);
        }
    }
}
