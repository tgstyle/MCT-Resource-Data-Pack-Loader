package mctmods.resourcedatapackloader.common.entities.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;

abstract class RestingEngageGoal extends EntityAIBase {
    protected final EntityCreature mob;
    private final double nearSq;
    private final double farSq;
    private final int rest;
    private final boolean grounded;
    private int resting;

    protected RestingEngageGoal(EntityCreature mob, double nearSq, double farSq, int rest, boolean grounded) {
        this.mob = mob;
        this.nearSq = nearSq;
        this.farSq = farSq;
        this.rest = rest;
        this.grounded = grounded;
        setMutexBits(3);
    }

    @Override public final boolean shouldExecute() {
        if (resting > 0) {
            resting--;
            return false;
        }
        EntityLivingBase found = mob.getAttackTarget();
        if (found == null || !found.isEntityAlive() || grounded && !mob.onGround) { return false; }
        return inBand(mob, found, nearSq, farSq);
    }

    static boolean inBand(EntityCreature mob, EntityLivingBase target, double nearSq, double farSq) {
        double away = mob.getDistanceSq(target);
        return away > nearSq && away < farSq && mob.getEntitySenses().canSee(target);
    }

    @Override public void resetTask() { resting = rest; }
}
