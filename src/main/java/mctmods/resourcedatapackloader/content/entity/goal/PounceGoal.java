package mctmods.resourcedatapackloader.content.entity.goal;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec3;

public final class PounceGoal extends CooldownGoal {
    private static final int CROUCH = 20;
    private int crouched;
    private boolean airborne;

    public PounceGoal(PathfinderMob mob) { super(mob, true, 9.0D, 64.0D); }

    @Override public boolean canContinueToUse() { return target != null && target.isAlive() && (crouched < CROUCH || airborne); }

    @Override public void start() {
        super.start();
        crouched = 0;
        airborne = false;
        mob.getNavigation().stop();
    }

    @Override public void stop() {
        super.stop();
        airborne = false;
    }

    @Override protected void act(LivingEntity current) {
        if (!airborne) {
            crouched++;
            if (crouched < CROUCH) { return; }
            Vec3 leap = new Vec3(current.getX() - mob.getX(), 0.0D, current.getZ() - mob.getZ()).normalize();
            mob.setDeltaMovement(leap.x * 0.9D, 0.5D, leap.z * 0.9D);
            mob.hurtMarked = true;
            airborne = true;
            return;
        }
        if (mob.distanceToSqr(current) < 4.0D) {
            StrikeGoal.hit(mob, current);
            current.knockback(1.0D, Mth.sin(mob.getYRot() * Mth.DEG_TO_RAD), -Mth.cos(mob.getYRot() * Mth.DEG_TO_RAD));
            airborne = false;
            return;
        }
        if (mob.onGround()) { airborne = false; }
    }
}
