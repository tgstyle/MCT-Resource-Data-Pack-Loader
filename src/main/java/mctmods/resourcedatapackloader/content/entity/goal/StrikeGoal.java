package mctmods.resourcedatapackloader.content.entity.goal;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import javax.annotation.Nonnull;

public final class StrikeGoal extends MeleeAttackGoal {
    public StrikeGoal(PathfinderMob mob, double speed, boolean followingTargetEvenIfNotSeen) { super(mob, speed, followingTargetEvenIfNotSeen); }

    @Override protected void checkAndPerformAttack(@Nonnull LivingEntity pEnemy, double pDistToEnemySqr) {
        if (pDistToEnemySqr > getAttackReachSqr(pEnemy) || !isTimeToAttack()) { return; }
        resetAttackCooldown();
        mob.swing(InteractionHand.MAIN_HAND);
        if (pEnemy.hurt(mob.damageSources().mobAttack(mob), (float) mob.getAttributeValue(Attributes.ATTACK_DAMAGE))) {
            EnchantmentHelper.doPostHurtEffects(pEnemy, mob);
            EnchantmentHelper.doPostDamageEffects(mob, pEnemy);
        }
    }
}
