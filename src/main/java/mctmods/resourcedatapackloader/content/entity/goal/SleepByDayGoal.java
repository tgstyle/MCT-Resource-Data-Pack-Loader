package mctmods.resourcedatapackloader.content.entity.goal;

import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import java.util.EnumSet;
import javax.annotation.Nullable;

public final class SleepByDayGoal extends Goal {
    private static final int RETRY = 40;
    private static final int TRIES = 3;
    private final PathfinderMob mob;
    @Nullable private BlockPos shade;
    private boolean settled;

    public SleepByDayGoal(PathfinderMob mob) {
        this.mob = mob;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
    }

    @Override public boolean requiresUpdateEveryTick() { return true; }

    private boolean calm() { return mob.getTarget() == null && mob.getLastHurtByMob() == null; }

    @Override public boolean canUse() {
        if (!mob.level().isDay() || !calm()) { return false; }
        BlockPos feet = mob.blockPosition();
        if (covered(feet)) {
            shade = feet;
            return true;
        }
        if (mob.tickCount % RETRY != 0) { return false; }
        for (int tries = 0; tries < TRIES; tries++) {
            Vec3 spot = DefaultRandomPos.getPos(mob, 8, 3);
            if (spot == null) { continue; }
            BlockPos at = BlockPos.containing(spot);
            for (int lift = 0; lift < 3 && blocked(at); lift++) { at = at.above(); }
            if (blocked(at) || !covered(at)) { continue; }
            shade = at;
            return true;
        }
        return false;
    }

    private boolean blocked(BlockPos at) { return !mob.level().getBlockState(at).getCollisionShape(mob.level(), at).isEmpty(); }

    private boolean covered(BlockPos feet) { return mob.level().getHeight(Heightmap.Types.MOTION_BLOCKING, feet.getX(), feet.getZ()) > feet.getY() + 1; }

    @Override public boolean canContinueToUse() { return mob.level().isDay() && calm() && shade != null; }

    @Override public void start() {
        settled = false;
        if (shade == null) { return; }
        ContentLog.LOGGER.debug("{} at {}, {}, {} heads for shade at {}, {}, {} for the day", mob.getName().getString(), mob.getBlockX(), mob.getBlockY(), mob.getBlockZ(), shade.getX(), shade.getY(), shade.getZ());
        walk();
    }

    private void walk() {
        if (shade != null) { mob.getNavigation().moveTo(shade.getX() + 0.5D, shade.getY(), shade.getZ() + 0.5D, 1.0D); }
    }

    @Override public void stop() {
        shade = null;
        settled = false;
        mob.getNavigation().stop();
    }

    @Override public void tick() {
        if (settled || shade == null) { return; }
        if (mob.distanceToSqr(shade.getX() + 0.5D, shade.getY(), shade.getZ() + 0.5D) <= 2.25D) {
            settled = true;
            mob.getNavigation().stop();
            return;
        }
        if (mob.getNavigation().isDone() && mob.tickCount % 20 == 0) { walk(); }
    }
}
