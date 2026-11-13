package mctmods.resourcedatapackloader.common.entities.ai;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;

public final class FollowPlayerGoal extends Goal {
    private static final int CHECK_TICKS = 10;
    private final PathfinderMob mob;
    private final int keep;
    private final int lightLevel;
    private final double speed;
    @Nullable private Player player;
    private int wait;
    private int repath;

    public FollowPlayerGoal(PathfinderMob mob, int keep, int lightLevel, double speed) {
        this.mob = mob;
        this.keep = keep;
        this.lightLevel = lightLevel;
        this.speed = speed;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Nullable public Player player() { return player; }

    private double range() { return mob.getAttributeValue(Attributes.FOLLOW_RANGE); }

    private boolean lit(Player found) { return lightLevel > 0 && mob.level().getMaxLocalRawBrightness(found.blockPosition()) >= lightLevel; }

    @Override public boolean canUse() {
        if (--wait > 0 || mob.getTarget() != null) { return false; }
        wait = CHECK_TICKS;
        Player found = mob.level().getNearestPlayer(TargetingConditions.forCombat().range(range()), mob);
        if (found == null || lit(found)) { return false; }
        player = found;
        return true;
    }

    @Override public boolean canContinueToUse() {
        return player != null && player.isAlive() && mob.getTarget() == null && mob.distanceToSqr(player) <= range() * range() && !lit(player);
    }

    @Override public void start() { repath = 0; }

    @Override public void stop() {
        player = null;
        mob.getNavigation().stop();
    }

    @Override public void tick() {
        if (player == null) { return; }
        mob.getLookControl().setLookAt(player, 30.0F, mob.getMaxHeadXRot());
        if (--repath > 0) { return; }
        repath = CHECK_TICKS;
        if (mob.distanceToSqr(player) > (double) keep * keep) { mob.getNavigation().moveTo(player, speed); }
        else { mob.getNavigation().stop(); }
    }
}
