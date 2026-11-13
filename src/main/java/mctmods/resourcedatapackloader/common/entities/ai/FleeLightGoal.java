package mctmods.resourcedatapackloader.common.entities.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;
import java.util.EnumSet;
import javax.annotation.Nullable;

public final class FleeLightGoal extends Goal {
    private static final int INTERVAL = 10;
    private static final int SAMPLES = 10;
    private final PathfinderMob mob;
    private final int level;
    private final double speed;
    @Nullable private Vec3 target;
    private long next;

    public FleeLightGoal(PathfinderMob mob, int level, double speed) {
        this.mob = mob;
        this.level = level;
        this.speed = speed;
        setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override public boolean canUse() {
        long now = mob.level().getGameTime();
        if (now < next) { return false; }
        next = now + INTERVAL;
        if (mob.level().getMaxLocalRawBrightness(mob.blockPosition()) < level) { return false; }
        int darkest = level;
        target = null;
        for (int i = 0; i < SAMPLES; i++) {
            Vec3 spot = DefaultRandomPos.getPos(mob, 10, 4);
            if (spot == null) { continue; }
            int light = mob.level().getMaxLocalRawBrightness(BlockPos.containing(spot));
            if (light < darkest) {
                darkest = light;
                target = spot;
            }
        }
        return target != null;
    }

    @Override public boolean canContinueToUse() { return !mob.getNavigation().isDone(); }

    @Override public void start() { if (target != null) { mob.getNavigation().moveTo(target.x, target.y, target.z, speed); } }

    @Override public void stop() { target = null; }
}
