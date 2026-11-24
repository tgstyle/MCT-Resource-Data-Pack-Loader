package mctmods.resourcedatapackloader.common.entities.ai;

import mctmods.resourcedatapackloader.common.util.entity.Looking;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.WorldServer;
import java.util.List;
import javax.annotation.Nullable;

public final class GustGoal extends RestingEngageGoal {
    private static final int WIND = 20;
    private static final double SPREAD = 3.0D;
    private final float power;
    @Nullable private EntityLivingBase target;
    private int winding;

    public GustGoal(EntityCreature mob, float power) {
        super(mob, 16.0D, 144.0D, 60, false);
        this.power = power;
    }

    @Override public boolean shouldContinueExecuting() { return target != null && target.isEntityAlive() && winding < WIND; }

    @Override public void startExecuting() {
        target = mob.getAttackTarget();
        winding = 0;
        mob.getNavigator().clearPath();
    }

    @Override public void resetTask() {
        target = null;
        super.resetTask();
    }

    @Override public void updateTask() {
        if (target == null) { return; }
        Looking.at(mob, target);
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
