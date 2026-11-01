package mctmods.resourcedatapackloader.common.entities.ai;

import mctmods.resourcedatapackloader.common.entities.ContentEntities;

import javax.annotation.Nonnull;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;

public final class StrikeGoal extends EntityAIAttackMelee {
    public StrikeGoal(EntityCreature mob, double speed, boolean longMemory) { super(mob, speed, longMemory); }

    @Override protected void checkAndPerformAttack(@Nonnull EntityLivingBase enemy, double distance) {
        if (distance > getAttackReachSqr(enemy) || this.attackTick > 0) { return; }
        this.attackTick = ContentEntities.attackInterval(this.attacker);
        this.attacker.swingArm(EnumHand.MAIN_HAND);
        hit(this.attacker, enemy);
    }

    public static void hit(EntityLiving mob, EntityLivingBase target) {
        IAttributeInstance held = mob.getAttributeMap().getAttributeInstanceByName(SharedMonsterAttributes.ATTACK_DAMAGE.getName());
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
