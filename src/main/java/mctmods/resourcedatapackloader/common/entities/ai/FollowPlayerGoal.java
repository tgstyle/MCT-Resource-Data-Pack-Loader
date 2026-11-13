package mctmods.resourcedatapackloader.common.entities.ai;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import java.util.EnumSet;
import javax.annotation.Nullable;

public final class FollowPlayerGoal extends Goal {
    private static final int INTERVAL = 10;
    private final PathfinderMob mob;
    private final int keep;
    private final int lightLevel;
    private final double speed;
    @Nullable private Player player;
    private long next;

    public FollowPlayerGoal(PathfinderMob mob, int keep, int lightLevel, double speed) {
        this.mob = mob;
        this.keep = keep;
        this.lightLevel = lightLevel;
        this.speed = speed;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    private double range() { return mob.getAttributeValue(Attributes.FOLLOW_RANGE); }

    private boolean dark(Player found) { return lightLevel <= 0 || found.level().getMaxLocalRawBrightness(found.blockPosition()) < lightLevel; }

    @Override public boolean canUse() {
        if (mob.getTarget() != null) { return false; }
        long now = mob.level().getGameTime();
        if (now < next) { return false; }
        next = now + INTERVAL;
        Player found = mob.level().getNearestPlayer(TargetingConditions.forCombat().range(range()), mob);
        if (found == null || !dark(found)) { return false; }
        player = found;
        return true;
    }

    @Override public boolean canContinueToUse() { return player != null && player.isAlive() && mob.getTarget() == null && mob.distanceToSqr(player) <= range() * range() && dark(player); }

    @Override public void start() { next = 0L; }

    @Override public void stop() {
        player = null;
        mob.getNavigation().stop();
    }

    @Override public void tick() {
        if (player == null) { return; }
        mob.getLookControl().setLookAt(player, 30.0F, 30.0F);
        long now = mob.level().getGameTime();
        if (now < next) { return; }
        next = now + INTERVAL;
        if (mob.distanceToSqr(player) > (double) keep * keep) { mob.getNavigation().moveTo(player, speed); }
        else { mob.getNavigation().stop(); }
    }
}
