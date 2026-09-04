package mctmods.resourcedatapackloader.content.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public final class EntityAIPounce extends EntityAIBase {
    private static final int CROUCH = 20;
    private static final int REST = 60;
    private final EntityCreature mob;
    private EntityLivingBase target;
    private int crouched;
    private boolean airborne;
    private int resting;

    public EntityAIPounce(EntityCreature mob) {
        this.mob = mob;
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
        return away > 9.0D && away < 64.0D && mob.getEntitySenses().canSee(found);
    }

    @Override public boolean shouldContinueExecuting() { return target != null && target.isEntityAlive() && (crouched < CROUCH || airborne); }

    @Override public void startExecuting() {
        target = mob.getAttackTarget();
        crouched = 0;
        airborne = false;
        mob.getNavigator().clearPath();
    }

    @Override public void resetTask() {
        target = null;
        airborne = false;
        resting = REST;
    }

    @Override public void updateTask() {
        mob.getLookHelper().setLookPositionWithEntity(target, 30.0F, 30.0F);
        if (!airborne) {
            crouched++;
            if (crouched < CROUCH) { return; }
            Vec3d leap = new Vec3d(target.posX - mob.posX, 0.0D, target.posZ - mob.posZ).normalize();
            mob.motionX = leap.x * 0.9D;
            mob.motionZ = leap.z * 0.9D;
            mob.motionY = 0.5D;
            mob.velocityChanged = true;
            airborne = true;
            return;
        }
        if (mob.getDistanceSq(target) < 4.0D) {
            float damage = (float) mob.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
            target.attackEntityFrom(DamageSource.causeMobDamage(mob), damage);
            target.knockBack(mob, 1.0F, MathHelper.sin(mob.rotationYaw * 0.017453292F), -MathHelper.cos(mob.rotationYaw * 0.017453292F));
            airborne = false;
            return;
        }
        if (mob.onGround) { airborne = false; }
    }
}
