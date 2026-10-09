package mctmods.resourcedatapackloader.content.entity.ai;

import mctmods.resourcedatapackloader.util.MathUtil;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.MathHelper;
import javax.annotation.Nullable;

public final class EntityAICharge extends EntityAIBase {
    private static final int RUN = 40;
    private static final int REST = 60;
    private final EntityCreature mob;
    private final double speed;
    @Nullable private EntityLivingBase target;
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
        if (target == null) { return; }
        running--;
        mob.getLookHelper().setLookPositionWithEntity(target, 30.0F, 30.0F);
        mob.getNavigator().tryMoveToEntityLiving(target, speed);
        if (mob.getDistanceSq(target) > 6.25D) { return; }
        EntityAIStrike.hit(mob, target);
        target.knockBack(mob, 2.0F, MathHelper.sin(mob.rotationYaw * MathUtil.DEG_TO_RAD), -MathHelper.cos(mob.rotationYaw * MathUtil.DEG_TO_RAD));
        running = 0;
    }
}
