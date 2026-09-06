package mctmods.resourcedatapackloader.content.entity.goal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import java.util.EnumSet;
import javax.annotation.Nullable;

public final class SwoopGoal extends Goal {
    private static final double RING = 8.0D;
    private static final double HEIGHT = 6.0D;
    private static final int DIVE_MOST = 60;
    private final PathfinderMob mob;
    @Nullable private LivingEntity target;
    private boolean circling;
    private double angle;
    private int ticks;
    private int circleFor;

    public SwoopGoal(PathfinderMob mob) {
        this.mob = mob;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override public boolean requiresUpdateEveryTick() { return true; }

    @Override public boolean canUse() {
        LivingEntity found = mob.getTarget();
        return found != null && found.isAlive() && mob.distanceToSqr(found) < 1024.0D;
    }

    @Override public boolean canContinueToUse() { return target != null && target.isAlive() && mob.getTarget() == target; }

    @Override public void start() {
        target = mob.getTarget();
        angle = mob.getRandom().nextDouble() * Math.PI * 2.0D;
        circle();
    }

    private void circle() {
        circling = true;
        ticks = 0;
        circleFor = 100 + mob.getRandom().nextInt(100);
    }

    @Override public void stop() {
        target = null;
        circling = true;
        mob.getNavigation().stop();
    }

    @Override public void tick() {
        if (target == null) { return; }
        mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
        ticks++;
        if (circling) {
            angle += 0.08D;
            mob.getMoveControl().setWantedPosition(target.getX() + Math.cos(angle) * RING, target.getY() + HEIGHT, target.getZ() + Math.sin(angle) * RING, 1.0D);
            if (ticks >= circleFor && mob.getSensing().hasLineOfSight(target)) {
                circling = false;
                ticks = 0;
            }
            return;
        }
        mob.getMoveControl().setWantedPosition(target.getX(), target.getY() + target.getBbHeight() * 0.5D, target.getZ(), 1.6D);
        if (mob.distanceToSqr(target) < 6.25D) {
            float damage = (float) mob.getAttributeValue(Attributes.ATTACK_DAMAGE);
            target.hurt(mob.damageSources().mobAttack(mob), damage);
            circle();
            return;
        }
        if (ticks > DIVE_MOST || mob.horizontalCollision || mob.onGround()) { circle(); }
    }
}
