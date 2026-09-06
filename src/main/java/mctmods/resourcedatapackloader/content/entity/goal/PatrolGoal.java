package mctmods.resourcedatapackloader.content.entity.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nullable;

public final class PatrolGoal extends Goal {
    private static final String LEADER = "rdplPatrolLeader";
    private static final int LEG = 400;
    private static final int LEG_LEAST = 24;
    private static final int LEG_MOST = 48;
    private static final double MUSTER = 32.0D;
    private final PathfinderMob mob;
    @Nullable private PathfinderMob leader;
    @Nullable private BlockPos waypoint;
    private int walked;

    public PatrolGoal(PathfinderMob mob) {
        this.mob = mob;
        setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override public boolean requiresUpdateEveryTick() { return true; }

    private boolean leads() { return mob.getPersistentData().getBoolean(LEADER); }

    private void lead() { mob.getPersistentData().putBoolean(LEADER, true); }

    private void decide() {
        if (mob.getPersistentData().contains(LEADER)) { return; }
        mob.getPersistentData().putBoolean(LEADER, leaderNear() == null);
    }

    @Nullable private PathfinderMob leaderNear() {
        AABB around = mob.getBoundingBox().inflate(MUSTER, 8.0D, MUSTER);
        List<? extends PathfinderMob> others = mob.level().getEntitiesOfClass(mob.getClass(), around, other -> other != mob && other.isAlive() && other.getType() == mob.getType() && other.getPersistentData().getBoolean(LEADER));
        PathfinderMob best = null;
        double nearest = Double.MAX_VALUE;
        for (PathfinderMob other : others) {
            double away = mob.distanceToSqr(other);
            if (away < nearest) {
                nearest = away;
                best = other;
            }
        }
        return best;
    }

    @Override public boolean canUse() {
        if (mob.getTarget() != null) { return false; }
        decide();
        if (leads()) { return true; }
        if (leader == null || !leader.isAlive()) { leader = leaderNear(); }
        if (leader == null) { lead(); }
        return true;
    }

    @Override public boolean canContinueToUse() { return mob.getTarget() == null; }

    @Override public void start() {
        waypoint = null;
        walked = LEG;
    }

    @Override public void stop() {
        waypoint = null;
        mob.getNavigation().stop();
    }

    @Override public void tick() {
        if (leads()) {
            walked++;
            if (waypoint != null && walked < LEG && mob.distanceToSqr(waypoint.getX() + 0.5D, waypoint.getY(), waypoint.getZ() + 0.5D) > 16.0D && !mob.getNavigation().isDone()) { return; }
            double angle = mob.getRandom().nextDouble() * Math.PI * 2.0D;
            double reach = LEG_LEAST + mob.getRandom().nextInt(LEG_MOST - LEG_LEAST + 1);
            int x = (int) (mob.getX() + Math.cos(angle) * reach);
            int z = (int) (mob.getZ() + Math.sin(angle) * reach);
            waypoint = new BlockPos(x, mob.level().getHeight(Heightmap.Types.MOTION_BLOCKING, x, z), z);
            walked = 0;
            mob.getNavigation().moveTo(waypoint.getX() + 0.5D, waypoint.getY(), waypoint.getZ() + 0.5D, 1.0D);
            return;
        }
        if (leader == null || !leader.isAlive()) {
            leader = leaderNear();
            if (leader == null) {
                lead();
                walked = LEG;
            }
            return;
        }
        LivingEntity shared = leader.getTarget();
        if (shared != null && shared.isAlive()) {
            mob.setTarget(shared);
            return;
        }
        double away = mob.distanceToSqr(leader);
        if (away > 64.0D || (away > 16.0D && mob.getNavigation().isDone())) { mob.getNavigation().moveTo(leader, 1.1D); }
    }
}
