package mctmods.resourcedatapackloader.content.entity.goal;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import java.util.EnumSet;
import javax.annotation.Nullable;

public final class ChargeGoal extends Goal {
    private static final int RUN = 40;
    private static final int REST = 60;
    private final PathfinderMob mob;
    private final double speed;
    @Nullable private LivingEntity target;
    private int running;
    private int resting;

    public ChargeGoal(PathfinderMob mob, double speed) {
        this.mob = mob;
        this.speed = speed;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override public boolean requiresUpdateEveryTick() { return true; }

    @Override public boolean canUse() {
        if (resting > 0) {
            resting--;
            return false;
        }
        LivingEntity found = mob.getTarget();
        if (found == null || !found.isAlive() || !mob.onGround()) { return false; }
        double away = mob.distanceToSqr(found);
        return away > 16.0D && away < 256.0D && mob.getSensing().hasLineOfSight(found);
    }

    @Override public boolean canContinueToUse() { return running > 0 && target != null && target.isAlive(); }

    @Override public void start() {
        target = mob.getTarget();
        running = RUN;
    }

    @Override public void stop() {
        target = null;
        running = 0;
        resting = REST;
        mob.getNavigation().stop();
    }

    @Override public void tick() {
        if (target == null) { return; }
        running--;
        mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
        mob.getNavigation().moveTo(target, speed);
        if (mob.distanceToSqr(target) > 6.25D) { return; }
        float damage = (float) mob.getAttributeValue(Attributes.ATTACK_DAMAGE);
        target.hurt(mob.damageSources().mobAttack(mob), damage);
        target.knockback(2.0D, Mth.sin(mob.getYRot() * 0.017453292F), -Mth.cos(mob.getYRot() * 0.017453292F));
        running = 0;
    }
}
