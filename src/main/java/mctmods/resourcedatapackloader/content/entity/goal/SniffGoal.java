package mctmods.resourcedatapackloader.content.entity.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import java.util.EnumSet;
import javax.annotation.Nullable;

public final class SniffGoal extends Goal {
    private static final int LISTEN = 20;
    private static final int GIVE_UP = 300;
    private final PathfinderMob mob;
    private final int radius;
    @Nullable private Player heard;
    @Nullable private BlockPos heardAt;
    private int listening;
    private int walked;

    public SniffGoal(PathfinderMob mob, int radius) {
        this.mob = mob;
        this.radius = radius;
        setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override public boolean requiresUpdateEveryTick() { return true; }

    @Override public boolean canUse() {
        if (mob.getTarget() != null) { return false; }
        if (listening++ < LISTEN) { return false; }
        listening = 0;
        Player found = loudest();
        if (found == null) { return false; }
        heard = found;
        heardAt = found.blockPosition();
        walked = 0;
        return true;
    }

    @Nullable private Player loudest() {
        Player best = null;
        double nearest = Double.MAX_VALUE;
        for (Player player : mob.level().players()) {
            if (player.isSpectator() || player.isCreative() || player.isShiftKeyDown() || !player.isAlive()) { continue; }
            Vec3 motion = player.getDeltaMovement();
            if (motion.x * motion.x + motion.z * motion.z < 1.0E-4D && !player.isSprinting()) { continue; }
            double away = mob.distanceToSqr(player);
            if (away > (double) radius * radius || away >= nearest) { continue; }
            nearest = away;
            best = player;
        }
        return best;
    }

    @Override public boolean canContinueToUse() { return heardAt != null && mob.getTarget() == null && walked < GIVE_UP && !mob.getNavigation().isDone(); }

    private void walk() {
        if (heardAt != null) { mob.getNavigation().moveTo(heardAt.getX() + 0.5D, heardAt.getY(), heardAt.getZ() + 0.5D, 1.0D); }
    }

    @Override public void start() { walk(); }

    @Override public void stop() {
        heard = null;
        heardAt = null;
        mob.getNavigation().stop();
    }

    @Override public void tick() {
        walked++;
        if (heard != null && heard.isAlive() && mob.getSensing().hasLineOfSight(heard) && mob.distanceToSqr(heard) < 64.0D) {
            mob.setTarget(heard);
            return;
        }
        if (walked % LISTEN != 0) { return; }
        Player again = loudest();
        if (again == null) { return; }
        heard = again;
        heardAt = again.blockPosition();
        walk();
    }
}
