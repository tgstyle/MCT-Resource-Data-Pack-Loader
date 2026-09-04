package mctmods.resourcedatapackloader.content.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;

public final class EntityAICharge extends EntityAIBase {
    private static final int RUN = 40;
    private static final int REST = 60;
    private final EntityCreature mob;
    private final double speed;
    private EntityLivingBase target;
    private int running;
    private int resting;

    public EntityAICharge(EntityCreature mob, double speed) {
        this.mob = mob;
        this.speed = speed;
        setMutexBits(3);
    }

    @Override public boolean shouldExecute() {
        if (resting > 0) {
            resting--;
            return false;
        }
        EntityLivingBase found = mob.getAttackTarget();
        if (found == null || !found.isEntityAlive() || !mob.onGround) { return false; }
        double away = mob.getDistanceSq(found);
        return away > 16.0D && away < 256.0D && mob.getEntitySenses().canSee(found);
    }

    @Override public boolean shouldContinueExecuting() { return running > 0 && target != null && target.isEntityAlive(); }

    @Override public void startExecuting() {
        target = mob.getAttackTarget();
        running = RUN;
    }

    @Override public void resetTask() {
        target = null;
        running = 0;
        resting = REST;
        mob.getNavigator().clearPath();
    }

    @Override public void updateTask() {
        running--;
        mob.getLookHelper().setLookPositionWithEntity(target, 30.0F, 30.0F);
        mob.getNavigator().tryMoveToEntityLiving(target, speed);
        if (mob.getDistanceSq(target) > 6.25D) { return; }
        float damage = (float) mob.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
        target.attackEntityFrom(DamageSource.causeMobDamage(mob), damage);
        target.knockBack(mob, 2.0F, MathHelper.sin(mob.rotationYaw * 0.017453292F), -MathHelper.cos(mob.rotationYaw * 0.017453292F));
        running = 0;
    }
}
