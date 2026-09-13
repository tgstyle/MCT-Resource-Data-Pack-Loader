package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.entity.ContentEntities;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MeleeAttackGoal.class) public abstract class MixinMeleeAttackGoal {
    @Shadow @org.spongepowered.asm.mixin.Final protected PathfinderMob mob;

    @Inject(method = "getAttackReachSqr", at = @At("RETURN"), cancellable = true)
    private void rdpl$ownReach(LivingEntity attackTarget, CallbackInfoReturnable<Double> cir) {
        cir.setReturnValue(ContentEntities.attackReachSqr(mob, attackTarget, cir.getReturnValueD()));
    }

    @ModifyConstant(method = "canUse", constant = @Constant(longValue = 20L))
    private long rdpl$askEveryTick(long was) { return ContentEntities.def(mob) == null ? was : 0L; }

    @ModifyConstant(method = "resetAttackCooldown", constant = @Constant(intValue = 20))
    private int rdpl$paceTheBlows(int was) { return ContentEntities.attackInterval(mob); }
}
