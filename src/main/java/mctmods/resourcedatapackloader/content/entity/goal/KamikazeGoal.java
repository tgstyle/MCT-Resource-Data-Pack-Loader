package mctmods.resourcedatapackloader.content.entity.goal;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import java.util.EnumSet;
import javax.annotation.Nullable;

public final class KamikazeGoal extends Goal {
    private final PathfinderMob mob;
    private final float power;
    private final int fuse;
    private final boolean fire;
    @Nullable private LivingEntity target;
    private int lit;

    public KamikazeGoal(PathfinderMob mob, float power, int fuse, boolean fire) {
        this.mob = mob;
        this.power = power;
        this.fuse = fuse;
        this.fire = fire;
        setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override public boolean requiresUpdateEveryTick() { return true; }

    @Override public boolean canUse() {
        LivingEntity found = mob.getTarget();
        return lit > 0 || found != null && mob.distanceToSqr(found) < 9.0D;
    }

    @Override public void start() {
        mob.getNavigation().stop();
        target = mob.getTarget();
        if (lit == 0) { mob.playSound(SoundEvents.CREEPER_PRIMED, 1.0F, 0.5F); }
    }

    @Override public void stop() {
        target = null;
        lit = 0;
    }

    @Override public void tick() {
        if (target == null || !target.isAlive()) {
            lit = 0;
            return;
        }
        if (mob.distanceToSqr(target) > 49.0D || !mob.getSensing().hasLineOfSight(target)) {
            lit = 0;
            return;
        }
        lit++;
        if (lit < fuse || mob.level().isClientSide) { return; }
        mob.level().explode(mob, mob.getX(), mob.getY(), mob.getZ(), power, fire, Level.ExplosionInteraction.MOB);
        mob.discard();
    }
}
