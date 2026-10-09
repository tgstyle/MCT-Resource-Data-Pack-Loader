package mctmods.resourcedatapackloader.content.entity.goal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import java.util.EnumSet;
import javax.annotation.Nullable;

abstract class CooldownGoal extends Goal {
    private static final int REST = 180;
    protected final PathfinderMob mob;
    @Nullable protected LivingEntity target;
    private final boolean grounded;
    private final double nearSqr;
    private final double farSqr;
    private int restUntil;

    protected CooldownGoal(PathfinderMob mob, boolean grounded, double nearSqr, double farSqr) {
        this.mob = mob;
        this.grounded = grounded;
        this.nearSqr = nearSqr;
        this.farSqr = farSqr;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    protected abstract void act(LivingEntity current);

    @Override public boolean requiresUpdateEveryTick() { return true; }

    @Override public boolean canUse() {
        if (mob.tickCount < restUntil) { return false; }
        LivingEntity found = mob.getTarget();
        if (found == null || !found.isAlive() || (grounded && !mob.onGround())) { return false; }
        double away = mob.distanceToSqr(found);
        return away > nearSqr && away < farSqr && mob.getSensing().hasLineOfSight(found);
    }

    @Override public void start() { target = mob.getTarget(); }

    @Override public void stop() {
        target = null;
        restUntil = mob.tickCount + REST;
    }

    @Override public void tick() {
        if (!canContinueToUse()) { return; }
        LivingEntity current = target;
        if (current == null) { return; }
        mob.getLookControl().setLookAt(current, 30.0F, 30.0F);
        act(current);
    }
}
