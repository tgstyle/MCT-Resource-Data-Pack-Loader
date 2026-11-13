package mctmods.resourcedatapackloader.common.entities.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class FleeLightGoal extends EntityAIBase {
    private static final int SAMPLES = 10;
    private static final int CHECK_TICKS = 10;
    private final EntityCreature creature;
    private final int level;
    private final double speed;
    private Vec3d target;
    private int wait;

    public FleeLightGoal(EntityCreature creature, int level, double speed) {
        this.creature = creature;
        this.level = level;
        this.speed = speed;
        setMutexBits(1);
    }

    @Override public boolean shouldExecute() {
        if (++wait < CHECK_TICKS) { return false; }
        wait = 0;
        if (creature.world.getLightFromNeighbors(new BlockPos(creature)) < level) { return false; }
        int darkest = level;
        target = null;
        for (int i = 0; i < SAMPLES; i++) {
            Vec3d spot = RandomPositionGenerator.findRandomTarget(creature, 10, 4);
            if (spot == null) { continue; }
            int light = creature.world.getLightFromNeighbors(new BlockPos(spot));
            if (light < darkest) {
                darkest = light;
                target = spot;
            }
        }
        return target != null;
    }

    @Override public void startExecuting() { creature.getNavigator().tryMoveToXYZ(target.x, target.y, target.z, speed); }

    @Override public boolean shouldContinueExecuting() { return !creature.getNavigator().noPath(); }

    @Override public void resetTask() { target = null; }
}
