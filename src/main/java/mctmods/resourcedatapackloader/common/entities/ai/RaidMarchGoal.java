package mctmods.resourcedatapackloader.common.entities.ai;

import mctmods.resourcedatapackloader.common.raids.ActiveRaid;
import mctmods.resourcedatapackloader.common.util.entity.Walking;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class RaidMarchGoal extends EntityAIBase {
    private static final double ARRIVED_SQ = 100.0D;
    private static final double SPEED = 1.0D;
    private static final int LOOK_EVERY = 20;
    private final EntityCreature mob;
    @Nullable private Vec3d toward;

    public RaidMarchGoal(EntityCreature mob) {
        this.mob = mob;
        setMutexBits(1);
    }

    @Override public boolean shouldExecute() {
        if (mob.getAttackTarget() != null || mob.ticksExisted % LOOK_EVERY != 0) { return false; }
        BlockPos center = ActiveRaid.home(mob);
        if (center == null) { return false; }
        if (mob.getDistanceSq(center) <= ARRIVED_SQ) { return false; }
        toward = RandomPositionGenerator.findRandomTargetBlockTowards(mob, 15, 4, Walking.feet(center));
        return toward != null;
    }

    @Override public boolean shouldContinueExecuting() { return mob.getAttackTarget() == null && !mob.getNavigator().noPath(); }

    @Override public void startExecuting() { if (toward != null) { mob.getNavigator().tryMoveToXYZ(toward.x, toward.y, toward.z, SPEED); } }

    @Override public void resetTask() { toward = null; }
}
