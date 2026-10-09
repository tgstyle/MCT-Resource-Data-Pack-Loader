package mctmods.resourcedatapackloader.content.entity.goal;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;

public final class ChargeGoal extends CooldownGoal {
    private static final int RUN = 40;
    private final double speed;
    private int running;

    public ChargeGoal(PathfinderMob mob, double speed) {
        super(mob, true, 16.0D, 256.0D);
        this.speed = speed;
    }

    @Override public boolean canContinueToUse() { return running > 0 && target != null && target.isAlive(); }

    @Override public void start() {
        super.start();
        running = RUN;
    }

    @Override public void stop() {
        super.stop();
        running = 0;
        mob.getNavigation().stop();
    }

    @Override protected void act(LivingEntity current) {
        running--;
        mob.getNavigation().moveTo(current, speed);
        if (mob.distanceToSqr(current) > 6.25D) { return; }
        StrikeGoal.hit(mob, current);
        current.knockback(2.0D, Mth.sin(mob.getYRot() * Mth.DEG_TO_RAD), -Mth.cos(mob.getYRot() * Mth.DEG_TO_RAD));
        running = 0;
    }
}
