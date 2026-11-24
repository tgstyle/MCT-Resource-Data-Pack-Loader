package mctmods.resourcedatapackloader.common.entities.ai;

import mctmods.resourcedatapackloader.common.util.entity.Looking;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.Vec3d;

public final class PounceGoal extends RestingEngageGoal {
    private static final int CROUCH = 20;
    @Nullable private EntityLivingBase target;
    private int crouched;
    private boolean airborne;

    public PounceGoal(EntityCreature mob) { super(mob, 9.0D, 64.0D, 60, true); }

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
        super.resetTask();
    }

    @Override public void updateTask() {
        if (target == null) { return; }
        Looking.at(mob, target);
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
            StrikeGoal.hit(mob, target);
            StrikeGoal.knockAlongYaw(mob, target, 1.0F);
            airborne = false;
            return;
        }
        if (mob.onGround) { airborne = false; }
    }
}
