package mctmods.resourcedatapackloader.content.entity;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.SoundEvents;

public final class EntityAIKamikaze extends EntityAIBase {
    private final EntityCreature mob;
    private final float power;
    private final int fuse;
    private final boolean fire;
    private EntityLivingBase target;
    private int lit;

    public EntityAIKamikaze(EntityCreature mob, float power, int fuse, boolean fire) {
        this.mob = mob;
        this.power = power;
        this.fuse = fuse;
        this.fire = fire;
        setMutexBits(1);
    }

    @Override public boolean shouldExecute() {
        EntityLivingBase found = mob.getAttackTarget();
        return lit > 0 || found != null && mob.getDistanceSq(found) < 9.0D;
    }

    @Override public void startExecuting() {
        mob.getNavigator().clearPath();
        target = mob.getAttackTarget();
        if (lit == 0) { mob.playSound(SoundEvents.ENTITY_CREEPER_PRIMED, 1.0F, 0.5F); }
    }

    @Override public void resetTask() {
        target = null;
        lit = 0;
    }

    @Override public void updateTask() {
        if (target == null || target.isDead) {
            lit = 0;
            return;
        }
        if (mob.getDistanceSq(target) > 49.0D || !mob.getEntitySenses().canSee(target)) {
            lit = 0;
            return;
        }

        lit++;
        if (lit < fuse) { return; }
        if (mob.world.isRemote) { return; }

        mob.world.newExplosion(mob, mob.posX, mob.posY, mob.posZ, power, fire, mob.world.getGameRules().getBoolean("mobGriefing"));
        mob.setDead();
    }
}
