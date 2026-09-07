package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.entity.ContentThreat;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TargetGoal.class) public abstract class MixinTargetGoal {
    @Shadow @Final protected Mob mob;

    @Inject(method = "getFollowDistance", at = @At("RETURN"), cancellable = true)
    private void rdpl$notice(CallbackInfoReturnable<Double> cir) {
        double base = cir.getReturnValueD();
        double wanted = ContentThreat.notice(mob, base);
        if (wanted != base) { cir.setReturnValue(wanted); }
    }
}
