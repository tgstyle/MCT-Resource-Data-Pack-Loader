package mctmods.resourcedatapackloader.content.entity.ai;

import mctmods.resourcedatapackloader.util.Walking;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import java.util.List;
import javax.annotation.Nullable;

public final class EntityAIPatrol extends EntityAIBase {
    private static final String LEADER = "rdplPatrolLeader";
    private static final int LEG = 400;
    private static final int RETRY = 20;
    private static final int LEG_LEAST = 24;
    private static final int LEG_MOST = 48;
    private static final double MUSTER = 32.0D;
    private static final double PATROL_SPEED = 1.0D;
    private static final double FOLLOW_SPEED = 1.1D;
    private final EntityCreature mob;
    @Nullable private EntityCreature leader;
    @Nullable private BlockPos waypoint;
    private int walked;

    public EntityAIPatrol(EntityCreature mob) {
        this.mob = mob;
        setMutexBits(1);
    }

    private static boolean leads(EntityCreature who) { return who.getEntityData().getBoolean(LEADER); }

    private void decide() {
        if (mob.getEntityData().hasKey(LEADER)) { return; }
        mob.getEntityData().setBoolean(LEADER, leaderNear() == null);
    }

    @Nullable private EntityCreature leaderNear() {
        AxisAlignedBB around = mob.getEntityBoundingBox().grow(MUSTER, 8.0D, MUSTER);
        List<? extends EntityCreature> others = mob.world.getEntitiesWithinAABB(mob.getClass(), around, other -> other != mob && other.isEntityAlive() && leads(other));
        @Nullable EntityCreature best = null;
        double nearest = Double.MAX_VALUE;
        for (EntityCreature other : others) {
            double away = mob.getDistanceSq(other);
            if (away < nearest) {
                nearest = away;
                best = other;
            }
        }
        return best;
    }

    @Override public boolean shouldExecute() {
        if (mob.getAttackTarget() != null) { return false; }
        decide();
        if (leads(mob)) { return true; }
        if (leader == null || !leader.isEntityAlive()) { leader = leaderNear(); }
        if (leader == null) { mob.getEntityData().setBoolean(LEADER, true); }
        return true;
    }

    @Override public boolean shouldContinueExecuting() { return mob.getAttackTarget() == null; }

    @Override public void startExecuting() {
        waypoint = null;
        walked = LEG;
    }

    @Override public void resetTask() {
        waypoint = null;
        mob.getNavigator().clearPath();
    }

    @Override public void updateTask() {
        if (leads(mob)) {
            walked++;
            if (waypoint != null && walked < LEG && mob.getDistanceSq(waypoint) > 16.0D && (walked < RETRY || !mob.getNavigator().noPath())) { return; }
            double angle = mob.getRNG().nextDouble() * Math.PI * 2.0D;
            double reach = LEG_LEAST + mob.getRNG().nextInt(LEG_MOST - LEG_LEAST + 1);
            BlockPos column = new BlockPos((int) (mob.posX + Math.cos(angle) * reach), 0, (int) (mob.posZ + Math.sin(angle) * reach));
            if (!mob.world.isBlockLoaded(column)) { return; }
            waypoint = mob.world.getHeight(column);
            walked = 0;
            Walking.toBlock(mob, waypoint, PATROL_SPEED);
            return;
        }
        if (leader == null || !leader.isEntityAlive()) {
            leader = leaderNear();
            if (leader == null) {
                mob.getEntityData().setBoolean(LEADER, true);
                walked = LEG;
            }
            return;
        }
        EntityLivingBase shared = leader.getAttackTarget();
        if (shared != null && shared.isEntityAlive()) {
            mob.setAttackTarget(shared);
            return;
        }
        double away = mob.getDistanceSq(leader);
        if (away > 64.0D || (away > 16.0D && mob.getNavigator().noPath())) { mob.getNavigator().tryMoveToEntityLiving(leader, FOLLOW_SPEED); }
    }
}
