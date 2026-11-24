package mctmods.resourcedatapackloader.common.entities.ai;

import mctmods.resourcedatapackloader.common.anvils.ContentAnvils;
import mctmods.resourcedatapackloader.common.entities.behavior.ContentMobExperience;
import mctmods.resourcedatapackloader.common.pack.definitions.effects.AnvilDef;
import mctmods.resourcedatapackloader.common.util.entity.Walking;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

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
        List<BlockPos> anvils = new ArrayList<>();
        for (BlockPos at : BlockPos.betweenClosed(feet.offset(-REACH, -RISE, -REACH), feet.offset(REACH, RISE, REACH))) {
            if (ContentAnvils.anvil(mob.level().getBlockState(at))) { anvils.add(at.immutable()); }
        }
        return Walking.nearest(anvils, at -> at.distSqr(feet), Double.MAX_VALUE);
    }

    @Override public boolean canContinueToUse() { return anvil != null && work != null && walked < GIVE_UP && ContentAnvils.anvil(mob.level().getBlockState(anvil)); }

    @Override public void start() {
        walked = 0;
        if (anvil == null || work == null) { return; }
        GoalLog.debug(mob, "can pay for {} and heads for the anvil at {}, {}, {}", work.key(), anvil.getX(), anvil.getY(), anvil.getZ());
        walk();
    }

    private void walk() { if (anvil != null) { Walking.toBlock(mob, anvil, Walking.pace(mob)); } }

    @Override public void stop() {
        anvil = null;
        work = null;
        mob.getNavigation().stop();
    }

    @Override public void tick() {
        if (!canContinueToUse()) { return; }
        if (anvil == null || work == null) { return; }
        walked++;
        BlockBreak.look(mob, anvil);
        if (mob.distanceToSqr(Vec3.atCenterOf(anvil)) <= USE_DISTANCE_SQ) {
            ContentAnvils.work(mob, work, anvil);
            anvil = null;
            return;
        }
        if (mob.getNavigation().isDone() && walked % 20 == 0) { walk(); }
    }
}
