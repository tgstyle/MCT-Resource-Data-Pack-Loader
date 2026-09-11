package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.entity.ContentEntities;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityAIAttackMelee.class) public abstract class MixinEntityAIAttackMelee {
    @Shadow protected EntityCreature attacker;
    @Shadow protected int attackTick;
    @Unique private int rdpl$ticksBefore;

    @Inject(method = "getAttackReachSqr", at = @At("RETURN"), cancellable = true) private void rdpl$reach(EntityLivingBase attackTarget, CallbackInfoReturnable<Double> cir) {
        cir.setReturnValue(ContentEntities.attackReachSqr(attacker, attackTarget, cir.getReturnValueD()));
    }

    @Inject(method = "checkAndPerformAttack", at = @At("HEAD")) private void rdpl$noteTheCount(EntityLivingBase enemy, double distToEnemySqr, CallbackInfo ci) { rdpl$ticksBefore = attackTick; }

    @Inject(method = "checkAndPerformAttack", at = @At("RETURN")) private void rdpl$paceTheBlows(EntityLivingBase enemy, double distToEnemySqr, CallbackInfo ci) {
        if (rdpl$ticksBefore <= 0 && attackTick == 20) { attackTick = ContentEntities.attackInterval(attacker); }
    }
}
