package mctmods.resourcedatapackloader.common.entities.ai;

import mctmods.resourcedatapackloader.common.util.entity.Stalking;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

public final class FollowPlayerGoal extends Goal {
    private static final int CHECK_TICKS = 10;
    private final PathfinderMob mob;
    private final double keep;
    private final int darkLevel;
    private final double speed;
    @Nullable private Player player;
    private int wait;
    private int repath;

    public FollowPlayerGoal(PathfinderMob mob, int keep, int darkLevel, double speed) {
        this.mob = mob;
        this.keep = keep;
        this.darkLevel = darkLevel;
        this.speed = speed;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override public boolean requiresUpdateEveryTick() { return true; }

    private boolean open(Player found) { return darkLevel <= 0 || Stalking.light(found) < darkLevel; }

    @Override public boolean canUse() {
        if (mob.getTarget() != null) { return false; }
        if (wait > 0) {
            wait--;
            return false;
        }
        wait = CHECK_TICKS;
        Player found = Stalking.nearest(mob);
        if (found == null || !open(found)) { return false; }
        player = found;
        return true;
    }

    @Override public boolean canContinueToUse() {
        double range = Stalking.range(mob);
        return player != null && player.isAlive() && !player.isSpectator() && mob.getTarget() == null && mob.distanceToSqr(player) <= range * range && open(player);
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
        if (mob.distanceToSqr(player) > keep * keep) { mob.getNavigation().moveTo(player, speed); }
        else { mob.getNavigation().stop(); }
    }
}
