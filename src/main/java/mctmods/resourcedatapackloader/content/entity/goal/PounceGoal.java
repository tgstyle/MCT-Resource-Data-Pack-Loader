package mctmods.resourcedatapackloader.content.entity.goal;

import mctmods.resourcedatapackloader.compat.Compat;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;
import java.util.EnumSet;
import javax.annotation.Nullable;

public final class PounceGoal extends Goal {
    private static final int CROUCH = 20;
    private static final int REST = 180;
    private final PathfinderMob mob;
    @Nullable private LivingEntity target;
    private int crouched;
    private boolean airborne;
    private int restUntil;

    public PounceGoal(PathfinderMob mob) {
        this.mob = mob;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override public boolean requiresUpdateEveryTick() { return true; }

    @Override public boolean canUse() {
        if (mob.tickCount < restUntil) { return false; }
        LivingEntity found = mob.getTarget();
        if (found == null || !found.isAlive() || !mob.onGround()) { return false; }
        double away = mob.distanceToSqr(found);
        return away > 9.0D && away < 64.0D && mob.getSensing().hasLineOfSight(found);
    }

    @Override public boolean canContinueToUse() { return target != null && target.isAlive() && (crouched < CROUCH || airborne); }

    @Override public void start() {
        target = mob.getTarget();
        crouched = 0;
        airborne = false;
        mob.getNavigation().stop();
    }

    @Override public void stop() {
        target = null;
        airborne = false;
        restUntil = mob.tickCount + REST;
    }

    @Override public void tick() {
        if (!canContinueToUse()) { return; }
        if (target == null) { return; }
        mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
        if (!airborne) {
            crouched++;
            if (crouched < CROUCH) { return; }
            Vec3 leap = new Vec3(target.getX() - mob.getX(), 0.0D, target.getZ() - mob.getZ()).normalize();
            mob.setDeltaMovement(leap.x * 0.9D, 0.5D, leap.z * 0.9D);
            mob.hurtMarked = true;
            airborne = true;
            return;
        }
        if (mob.distanceToSqr(target) < 4.0D) {
            float damage = (float) mob.getAttributeValue(Attributes.ATTACK_DAMAGE);
            DamageSource source = mob.damageSources().mobAttack(mob);
            if (target.level() instanceof ServerLevel level) { target.hurtServer(level, source, damage); }
            Compat.knockback(target, 1.0D, Mth.sin(mob.getYRot() * 0.017453292F), -Mth.cos(mob.getYRot() * 0.017453292F), source, damage);
            airborne = false;
            return;
        }
        if (mob.onGround()) { airborne = false; }
    }
}
