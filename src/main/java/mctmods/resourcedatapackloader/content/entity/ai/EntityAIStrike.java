package mctmods.resourcedatapackloader.content.entity.ai;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;

import javax.annotation.Nonnull;

public final class EntityAIStrike extends EntityAIAttackMelee {
    public EntityAIStrike(EntityCreature mob, double speed, boolean longMemory) {
        super(mob, speed, longMemory);
    }

    @Override protected void checkAndPerformAttack(@Nonnull EntityLivingBase enemy, double distance) {
        if (distance > getAttackReachSqr(enemy) || this.attackTick > 0) { return; }
        this.attackTick = 20;
        this.attacker.swingArm(EnumHand.MAIN_HAND);
        IAttributeInstance held = this.attacker.getAttributeMap().getAttributeInstanceByName(SharedMonsterAttributes.ATTACK_DAMAGE.getName());
        if (held == null) {
            this.attacker.attackEntityAsMob(enemy);
            return;
        }
        if (enemy.attackEntityFrom(DamageSource.causeMobDamage(this.attacker), (float) held.getAttributeValue())) {
            EnchantmentHelper.applyThornEnchantments(enemy, this.attacker);
            EnchantmentHelper.applyArthropodEnchantments(this.attacker, enemy);
        }
    }
}
