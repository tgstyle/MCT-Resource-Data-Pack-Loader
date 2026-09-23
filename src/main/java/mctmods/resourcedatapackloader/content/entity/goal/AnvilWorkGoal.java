package mctmods.resourcedatapackloader.content.entity.goal;

import mctmods.resourcedatapackloader.content.ContentAnvils;
import mctmods.resourcedatapackloader.content.def.AnvilDef;
import mctmods.resourcedatapackloader.content.entity.ContentMobExperience;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import java.util.EnumSet;
import javax.annotation.Nullable;

public final class AnvilWorkGoal extends Goal {
    private static final int FIRST_LOOK = 100;
    private static final int LOOK_EVERY = 300;
    private static final int REACH = 16;
    private static final int RISE = 4;
    private static final double USE_DISTANCE_SQ = 9.0D;
    private static final int GIVE_UP = 600;
    private final PathfinderMob mob;
    @Nullable private BlockPos anvil;
    @Nullable private AnvilDef work;
    private int walked;
    private int nextLook = FIRST_LOOK;

    public AnvilWorkGoal(PathfinderMob mob) {
        this.mob = mob;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override public boolean requiresUpdateEveryTick() { return true; }

    @Override public boolean canUse() {
        if (mob.tickCount < nextLook) { return false; }
        nextLook = mob.tickCount + LOOK_EVERY - Math.floorMod(mob.tickCount - FIRST_LOOK, LOOK_EVERY);
        work = ContentAnvils.affordable(mob, ContentMobExperience.level(mob));
        if (work == null) { return false; }
        anvil = nearestAnvil();
        return anvil != null;
    }

    @Nullable private BlockPos nearestAnvil() {
        BlockPos feet = mob.blockPosition();
        BlockPos best = null;
        double bestDistance = Double.MAX_VALUE;
        for (BlockPos at : BlockPos.betweenClosed(feet.offset(-REACH, -RISE, -REACH), feet.offset(REACH, RISE, REACH))) {
            if (!ContentAnvils.anvil(mob.level().getBlockState(at))) { continue; }
            double distance = at.distSqr(feet);
            if (distance < bestDistance) {
                bestDistance = distance;
                best = at.immutable();
            }
        }
        return best;
    }

    @Override public boolean canContinueToUse() { return anvil != null && work != null && walked < GIVE_UP && ContentAnvils.anvil(mob.level().getBlockState(anvil)); }

    @Override public void start() {
        walked = 0;
        if (anvil == null || work == null) { return; }
        ContentLog.LOGGER.debug("{} at {}, {}, {} can pay for {} and heads for the anvil at {}, {}, {}", mob.getName().getString(), mob.getBlockX(), mob.getBlockY(), mob.getBlockZ(), work.key(), anvil.getX(), anvil.getY(), anvil.getZ());
        walk();
    }

    private void walk() {
        if (anvil != null) { mob.getNavigation().moveTo(anvil.getX() + 0.5D, anvil.getY(), anvil.getZ() + 0.5D, 1.0D); }
    }

    @Override public void stop() {
        anvil = null;
        work = null;
        mob.getNavigation().stop();
    }

    @Override public void tick() {
        if (!canContinueToUse()) { return; }
        if (anvil == null || work == null) { return; }
        walked++;
        double x = anvil.getX() + 0.5D;
        double y = anvil.getY() + 0.5D;
        double z = anvil.getZ() + 0.5D;
        mob.getLookControl().setLookAt(x, y, z, 30.0F, 30.0F);
        if (mob.distanceToSqr(x, y, z) <= USE_DISTANCE_SQ) {
            ContentAnvils.work(mob, work, anvil);
            anvil = null;
            return;
        }
        if (mob.getNavigation().isDone() && walked % 20 == 0) { walk(); }
    }
}
