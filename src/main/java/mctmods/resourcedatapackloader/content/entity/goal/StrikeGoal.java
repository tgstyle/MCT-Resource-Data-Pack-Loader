package mctmods.resourcedatapackloader.content.entity.goal;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import javax.annotation.Nonnull;

public final class StrikeGoal extends MeleeAttackGoal {
    public StrikeGoal(PathfinderMob mob, double speed, boolean followingTargetEvenIfNotSeen) { super(mob, speed, followingTargetEvenIfNotSeen); }

    @Override protected void checkAndPerformAttack(@Nonnull LivingEntity target) {
        if (!canPerformAttack(target)) { return; }
        resetAttackCooldown();
        mob.swing(InteractionHand.MAIN_HAND);
        DamageSource source = mob.damageSources().mobAttack(mob);
        if (target.hurt(source, (float) mob.getAttributeValue(Attributes.ATTACK_DAMAGE)) && mob.level() instanceof ServerLevel level) { EnchantmentHelper.doPostAttackEffects(level, target, source); }
    }
}
