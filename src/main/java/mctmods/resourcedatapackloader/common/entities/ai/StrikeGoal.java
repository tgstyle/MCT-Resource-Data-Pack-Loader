package mctmods.resourcedatapackloader.common.entities.ai;

import mctmods.resourcedatapackloader.common.entities.ContentEntities;
import mctmods.resourcedatapackloader.core.util.math.MathUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;

public final class StrikeGoal extends EntityAIAttackMelee {
    public StrikeGoal(EntityCreature mob, double speed, boolean longMemory) { super(mob, speed, longMemory); }

    @Override protected void checkAndPerformAttack(@Nonnull EntityLivingBase enemy, double distance) {
        if (distance > getAttackReachSqr(enemy) || this.attackTick > 0) { return; }
        this.attackTick = ContentEntities.attackInterval(this.attacker);
        this.attacker.swingArm(EnumHand.MAIN_HAND);
        hit(this.attacker, enemy);
    }

    public static void knockAlongYaw(EntityLiving mob, EntityLivingBase target, float power) {
        target.knockBack(mob, power, MathHelper.sin(mob.rotationYaw * MathUtil.DEG_TO_RAD), -MathHelper.cos(mob.rotationYaw * MathUtil.DEG_TO_RAD));
    }

    @Nullable public static IAttributeInstance damage(EntityLiving mob) { return mob.getAttributeMap().getAttributeInstanceByName(SharedMonsterAttributes.ATTACK_DAMAGE.getName()); }

    public static void hit(EntityLiving mob, EntityLivingBase target) {
        IAttributeInstance held = damage(mob);
        if (held == null) {
            mob.attackEntityAsMob(target);
            return;
        }
        if (target.attackEntityFrom(DamageSource.causeMobDamage(mob), (float) held.getAttributeValue())) {
            EnchantmentHelper.applyThornEnchantments(target, mob);
            EnchantmentHelper.applyArthropodEnchantments(mob, target);
        }
    }
}
