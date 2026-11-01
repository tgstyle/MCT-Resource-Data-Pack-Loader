package mctmods.resourcedatapackloader.mixin.world.entity.ai.goal.target;

import mctmods.resourcedatapackloader.common.entities.behavior.ContentThreat;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TargetGoal.class) public abstract class TargetGoalMixin {
    @Shadow @Final protected Mob mob;

    @Inject(method = "getFollowDistance", at = @At("RETURN"), cancellable = true) private void rdpl$notice(CallbackInfoReturnable<Double> cir) {
        if (!(mob instanceof Enemy)) { return; }
        double base = cir.getReturnValueD();
        double wanted = ContentThreat.notice(mob, base);
        if (wanted != base) { cir.setReturnValue(wanted); }
    }
}
