package mctmods.resourcedatapackloader.content.entity.goal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;
import java.util.EnumSet;
import javax.annotation.Nullable;

public final class FleeWhenHurtGoal extends Goal {
    private static final int FLIGHT = 100;
    private final PathfinderMob mob;
    private final float fraction;
    private final double speed;
    private int fleeing;

    public FleeWhenHurtGoal(PathfinderMob mob, float fraction, double speed) {
        this.mob = mob;
        this.fraction = fraction;
        this.speed = speed;
        setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override public boolean requiresUpdateEveryTick() { return true; }

    @Nullable private LivingEntity threat() { return mob.getTarget() != null ? mob.getTarget() : mob.getLastHurtByMob(); }

    @Override public boolean canUse() {
        LivingEntity threat = threat();
        return threat != null && threat.isAlive() && mob.getHealth() < mob.getMaxHealth() * fraction && away(threat);
    }

    private boolean away(LivingEntity threat) {
        Vec3 spot = DefaultRandomPos.getPosAway(mob, 16, 7, threat.position());
        if (spot == null || threat.distanceToSqr(spot) < threat.distanceToSqr(mob)) { return false; }
        mob.getNavigation().moveTo(spot.x, spot.y, spot.z, speed);
        return true;
    }

    @Override public boolean canContinueToUse() { return fleeing < FLIGHT && mob.getHealth() < mob.getMaxHealth() * fraction; }

    @Override public void start() { fleeing = 0; }

    @Override public void stop() { mob.getNavigation().stop(); }

    @Override public void tick() {
        fleeing++;
        if (fleeing % 20 != 0 || !mob.getNavigation().isDone()) { return; }
        LivingEntity threat = threat();
        if (threat != null) { away(threat); }
    }
}
