package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.entity.ContentThreat;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.monster.IMob;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityAITarget.class) public abstract class MixinEntityAITarget {
    @Shadow @Final protected EntityCreature taskOwner;

    @Inject(method = "getTargetDistance", at = @At("RETURN"), cancellable = true) private void rdpl$notice(CallbackInfoReturnable<Double> cir) {
        if (!(taskOwner instanceof IMob)) { return; }
        double base = cir.getReturnValueD();
        double wanted = ContentThreat.notice(taskOwner, base);
        if (wanted != base) { cir.setReturnValue(wanted); }
    }
}
