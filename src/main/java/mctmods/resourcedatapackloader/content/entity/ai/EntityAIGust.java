package mctmods.resourcedatapackloader.content.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.WorldServer;

import java.util.List;

public final class EntityAIGust extends EntityAIBase {
    private static final int WIND = 20;
    private static final int REST = 60;
    private static final double SPREAD = 3.0D;
    private final EntityCreature mob;
    private final float power;
    private EntityLivingBase target;
    private int winding;
    private int resting;

    public EntityAIGust(EntityCreature mob, float power) {
        this.mob = mob;
        this.power = power;
        setMutexBits(3);
    }

    @Override public boolean shouldExecute() {
        if (resting > 0) {
            resting--;
            return false;
        }
        EntityLivingBase found = mob.getAttackTarget();
        if (found == null || !found.isEntityAlive()) { return false; }
        double away = mob.getDistanceSq(found);
        return away > 16.0D && away < 144.0D && mob.getEntitySenses().canSee(found);
    }

    @Override public boolean shouldContinueExecuting() { return target != null && target.isEntityAlive() && winding < WIND; }

    @Override public void startExecuting() {
        target = mob.getAttackTarget();
        winding = 0;
        mob.getNavigator().clearPath();
    }

    @Override public void resetTask() {
        target = null;
        resting = REST;
    }

    @Override public void updateTask() {
        mob.getLookHelper().setLookPositionWithEntity(target, 30.0F, 30.0F);
        winding++;
        if (winding < WIND) { return; }
        if (mob.world instanceof WorldServer) { ((WorldServer) mob.world).spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, target.posX, target.posY + target.height * 0.5D, target.posZ, 12, 0.6D, 0.4D, 0.6D, 0.0D); }
        mob.world.playSound(null, target.posX, target.posY, target.posZ, SoundEvents.ENTITY_GHAST_SHOOT, mob.getSoundCategory(), 0.8F, 1.6F);
        List<EntityLivingBase> struck = mob.world.getEntitiesWithinAABB(EntityLivingBase.class, target.getEntityBoundingBox().grow(SPREAD), other -> other != mob && other.isEntityAlive());
        for (EntityLivingBase other : struck) {
            other.attackEntityFrom(DamageSource.causeMobDamage(mob), 1.0F);
            other.knockBack(mob, power, mob.posX - other.posX, mob.posZ - other.posZ);
            other.motionY += 0.25D * power;
            other.velocityChanged = true;
        }
    }
}
