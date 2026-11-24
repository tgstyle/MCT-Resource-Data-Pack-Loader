package mctmods.resourcedatapackloader.common.entities.ai;

import mctmods.resourcedatapackloader.common.util.entity.Looking;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;

public final class ChargeGoal extends RestingEngageGoal {
    private static final int RUN = 40;
    private final double speed;
    @Nullable private EntityLivingBase target;
    private int running;

    public ChargeGoal(EntityCreature mob, double speed) {
        super(mob, 16.0D, 256.0D, 60, true);
        this.speed = speed;
    }

    @Override public boolean shouldContinueExecuting() { return running > 0 && target != null && target.isEntityAlive(); }

    @Override public void startExecuting() {
        target = mob.getAttackTarget();
        running = RUN;
    }

    @Override public void resetTask() {
        target = null;
        running = 0;
        super.resetTask();
        mob.getNavigator().clearPath();
    }

    @Override public void updateTask() {
        if (target == null) { return; }
        running--;
        Looking.at(mob, target);
        mob.getNavigator().tryMoveToEntityLiving(target, speed);
        if (mob.getDistanceSq(target) > 6.25D) { return; }
        StrikeGoal.hit(mob, target);
        StrikeGoal.knockAlongYaw(mob, target, 2.0F);
        running = 0;
    }
}
