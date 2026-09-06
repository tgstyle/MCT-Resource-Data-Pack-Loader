package mctmods.resourcedatapackloader.content.entity.goal;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;
import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nullable;

public final class GustGoal extends Goal {
    private static final int WIND = 20;
    private static final int REST = 60;
    private static final double SPREAD = 3.0D;
    private final PathfinderMob mob;
    private final float power;
    @Nullable private LivingEntity target;
    private int winding;
    private int resting;

    public GustGoal(PathfinderMob mob, float power) {
        this.mob = mob;
        this.power = power;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override public boolean requiresUpdateEveryTick() { return true; }

    @Override public boolean canUse() {
        if (resting > 0) {
            resting--;
            return false;
        }
        LivingEntity found = mob.getTarget();
        if (found == null || !found.isAlive()) { return false; }
        double away = mob.distanceToSqr(found);
        return away > 16.0D && away < 144.0D && mob.getSensing().hasLineOfSight(found);
    }

    @Override public boolean canContinueToUse() { return target != null && target.isAlive() && winding < WIND; }

    @Override public void start() {
        target = mob.getTarget();
        winding = 0;
        mob.getNavigation().stop();
    }

    @Override public void stop() {
        target = null;
        resting = REST;
    }

    @Override public void tick() {
        if (target == null) { return; }
        mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
        winding++;
        if (winding < WIND) { return; }
        if (mob.level() instanceof ServerLevel server) { server.sendParticles(ParticleTypes.POOF, target.getX(), target.getY() + target.getBbHeight() * 0.5D, target.getZ(), 12, 0.6D, 0.4D, 0.6D, 0.0D); }
        mob.level().playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.GHAST_SHOOT, mob.getSoundSource(), 0.8F, 1.6F);
        List<LivingEntity> struck = mob.level().getEntitiesOfClass(LivingEntity.class, target.getBoundingBox().inflate(SPREAD), other -> other != mob && other.isAlive());
        for (LivingEntity other : struck) {
            other.hurt(mob.damageSources().mobAttack(mob), 1.0F);
            other.knockback(power, mob.getX() - other.getX(), mob.getZ() - other.getZ());
            Vec3 motion = other.getDeltaMovement();
            other.setDeltaMovement(motion.x, motion.y + 0.25D * power, motion.z);
            other.hurtMarked = true;
        }
    }
}
