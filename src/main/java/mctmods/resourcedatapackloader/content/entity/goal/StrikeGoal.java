package mctmods.resourcedatapackloader.content.entity.goal;

import mctmods.resourcedatapackloader.compat.LineCompat;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import javax.annotation.Nonnull;

public final class StrikeGoal extends MeleeAttackGoal {
    public StrikeGoal(PathfinderMob mob, double speed, boolean followingTargetEvenIfNotSeen) { super(mob, speed, followingTargetEvenIfNotSeen); }

    @Override protected void checkAndPerformAttack(@Nonnull LivingEntity target) {
        if (!canPerformAttack(target)) { return; }
        resetAttackCooldown();
        LineCompat.swing(mob);
        hit(mob, target);
    }

    public static float damage(Mob mob) {
        AttributeInstance held = mob.getAttribute(Attributes.ATTACK_DAMAGE);
        return held == null ? 0.0F : (float) held.getValue();
    }

    public static void hit(Mob mob, LivingEntity target) {
        if (!(mob.level() instanceof ServerLevel level)) { return; }
        if (mob.getAttribute(Attributes.ATTACK_DAMAGE) == null) {
            mob.doHurtTarget(level, target);
            return;
        }
        DamageSource source = mob.damageSources().mobAttack(mob);
        if (target.hurtServer(level, source, damage(mob))) { EnchantmentHelper.doPostAttackEffects(level, target, source); }
    }
}
