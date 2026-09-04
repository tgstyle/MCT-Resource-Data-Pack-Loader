package mctmods.resourcedatapackloader.content.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.math.Vec3d;

public final class EntityAIFleeWhenHurt extends EntityAIBase {
    private static final int FLIGHT = 100;
    private final EntityCreature mob;
    private final float fraction;
    private final double speed;
    private int fleeing;

    public EntityAIFleeWhenHurt(EntityCreature mob, float fraction, double speed) {
        this.mob = mob;
        this.fraction = fraction;
        this.speed = speed;
        setMutexBits(1);
    }

    @Override public boolean shouldExecute() {
        EntityLivingBase threat = mob.getAttackTarget() != null ? mob.getAttackTarget() : mob.getRevengeTarget();
        return threat != null && threat.isEntityAlive() && mob.getHealth() < mob.getMaxHealth() * fraction && away(threat);
    }

    private boolean away(EntityLivingBase threat) {
        Vec3d spot = RandomPositionGenerator.findRandomTargetBlockAwayFrom(mob, 16, 7, new Vec3d(threat.posX, threat.posY, threat.posZ));
        if (spot == null || threat.getDistanceSq(spot.x, spot.y, spot.z) < threat.getDistanceSq(mob)) { return false; }
        mob.getNavigator().tryMoveToXYZ(spot.x, spot.y, spot.z, speed);
        return true;
    }

    @Override public boolean shouldContinueExecuting() { return fleeing < FLIGHT && mob.getHealth() < mob.getMaxHealth() * fraction; }

    @Override public void startExecuting() { fleeing = 0; }

    @Override public void resetTask() { mob.getNavigator().clearPath(); }

    @Override public void updateTask() {
        fleeing++;
        if (fleeing % 20 != 0 || !mob.getNavigator().noPath()) { return; }
        EntityLivingBase threat = mob.getAttackTarget() != null ? mob.getAttackTarget() : mob.getRevengeTarget();
        if (threat != null) { away(threat); }
    }
}
