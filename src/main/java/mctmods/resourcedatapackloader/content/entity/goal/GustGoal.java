package mctmods.resourcedatapackloader.content.entity.goal;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec3;
import java.util.List;

public final class GustGoal extends CooldownGoal {
    private static final int WIND = 20;
    private static final double SPREAD = 3.0D;
    private final float power;
    private int winding;

    public GustGoal(PathfinderMob mob, float power) {
        super(mob, false, 16.0D, 144.0D);
        this.power = power;
    }

    @Override public boolean canContinueToUse() { return target != null && target.isAlive() && winding < WIND; }

    @Override public void start() {
        super.start();
        winding = 0;
        mob.getNavigation().stop();
    }

    @Override protected void act(LivingEntity current) {
        winding++;
        if (winding < WIND) { return; }
        if (mob.level() instanceof ServerLevel server) { server.sendParticles(ParticleTypes.POOF, current.getX(), current.getY() + current.getBbHeight() * 0.5D, current.getZ(), 12, 0.6D, 0.4D, 0.6D, 0.0D); }
        mob.level().playSound(null, current.getX(), current.getY(), current.getZ(), SoundEvents.GHAST_SHOOT, mob.getSoundSource(), 0.8F, 1.6F);
        List<LivingEntity> struck = mob.level().getEntitiesOfClass(LivingEntity.class, current.getBoundingBox().inflate(SPREAD), other -> other != mob && other.isAlive());
        for (LivingEntity other : struck) {
            other.hurt(mob.damageSources().mobAttack(mob), 1.0F);
            other.knockback(power, mob.getX() - other.getX(), mob.getZ() - other.getZ());
            Vec3 motion = other.getDeltaMovement();
            other.setDeltaMovement(motion.x, motion.y + 0.25D * power, motion.z);
            other.hurtMarked = true;
        }
    }
}
