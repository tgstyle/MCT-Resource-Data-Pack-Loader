package mctmods.resourcedatapackloader.common.entities.ai;

import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;

public final class FleeLightGoal extends Goal {
    private static final int CHECK_TICKS = 10;
    private static final int SAMPLES = 10;
    private final PathfinderMob mob;
    private final int level;
    private final double speed;
    private Vec3 target = Vec3.ZERO;
    private int wait;

    public FleeLightGoal(PathfinderMob mob, int level, double speed) {
        this.mob = mob;
        this.level = level;
        this.speed = speed;
        setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override public boolean canUse() {
        if (--wait > 0) { return false; }
        wait = CHECK_TICKS;
        if (mob.level().getMaxLocalRawBrightness(mob.blockPosition()) < level) { return false; }
        int darkest = level;
        boolean found = false;
        for (int i = 0; i < SAMPLES; i++) {
            Vec3 spot = DefaultRandomPos.getPos(mob, 10, 4);
            if (spot == null) { continue; }
            int light = mob.level().getMaxLocalRawBrightness(BlockPos.containing(spot));
            if (light < darkest) {
                darkest = light;
                target = spot;
                found = true;
            }
        }
        return found;
    }

    @Override public void start() { mob.getNavigation().moveTo(target.x, target.y, target.z, speed); }

    @Override public boolean canContinueToUse() { return !mob.getNavigation().isDone(); }

    @Override public void stop() { mob.getNavigation().stop(); }
}
