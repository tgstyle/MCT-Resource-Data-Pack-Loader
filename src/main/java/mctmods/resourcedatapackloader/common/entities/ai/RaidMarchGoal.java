package mctmods.resourcedatapackloader.common.entities.ai;

import mctmods.resourcedatapackloader.common.raids.ActiveRaid;
import mctmods.resourcedatapackloader.common.util.world.PosTags;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;

public final class RaidMarchGoal extends Goal {
    private static final double ARRIVED_SQ = 100.0D;
    private static final int LOOK_EVERY = 60;
    private final PathfinderMob mob;
    @Nullable private Vec3 toward;
    private int nextLook;

    public RaidMarchGoal(PathfinderMob mob) {
        this.mob = mob;
        setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override public boolean canUse() {
        if (mob.getTarget() != null || mob.tickCount < nextLook || !mob.getPersistentData().contains(ActiveRaid.RAIDER)) { return false; }
        nextLook = mob.tickCount + LOOK_EVERY;
        BlockPos center = PosTags.read(mob.getPersistentData().getCompoundOrEmpty(ActiveRaid.RAIDER));
        if (center.distToLowCornerSqr(mob.getX(), mob.getY(), mob.getZ()) <= ARRIVED_SQ) { return false; }
        toward = DefaultRandomPos.getPosTowards(mob, 15, 4, Vec3.atBottomCenterOf(center), Math.PI / 2.0D);
        return toward != null;
    }

    @Override public boolean canContinueToUse() { return mob.getTarget() == null && !mob.getNavigation().isDone(); }

    @Override public void start() { if (toward != null) { mob.getNavigation().moveTo(toward.x, toward.y, toward.z, 1.0D); } }

    @Override public void stop() { toward = null; }
}
