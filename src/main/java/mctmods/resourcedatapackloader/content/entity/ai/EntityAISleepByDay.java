package mctmods.resourcedatapackloader.content.entity.ai;

import mctmods.resourcedatapackloader.util.Walking;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import javax.annotation.Nullable;

public final class EntityAISleepByDay extends EntityAIBase {
    private static final int RETRY = 40;
    private static final int TRIES = 3;
    private static final double SPEED = 1.0D;
    private final EntityCreature mob;
    @Nullable private BlockPos shade;
    private boolean settled;

    public EntityAISleepByDay(EntityCreature mob) {
        this.mob = mob;
        setMutexBits(7);
    }

    @Override public boolean shouldExecute() {
        if (!mob.world.isDaytime() || mob.getAttackTarget() != null || mob.getRevengeTarget() != null) { return false; }
        BlockPos feet = new BlockPos(mob);
        if (covered(feet)) {
            shade = feet;
            return true;
        }
        if (mob.ticksExisted % RETRY != 0) { return false; }
        for (int tries = 0; tries < TRIES; tries++) {
            Vec3d spot = RandomPositionGenerator.findRandomTarget(mob, 8, 3);
            if (spot == null) { continue; }
            BlockPos at = new BlockPos(spot);
            for (int lift = 0; lift < 3 && mob.world.getBlockState(at).getMaterial().isSolid(); lift++) { at = at.up(); }
            if (mob.world.getBlockState(at).getMaterial().isSolid() || !covered(at)) { continue; }
            shade = at;
            return true;
        }
        return false;
    }

    private boolean covered(BlockPos feet) { return mob.world.getHeight(feet.getX(), feet.getZ()) > feet.getY() + 1; }

    @Override public boolean shouldContinueExecuting() { return mob.world.isDaytime() && mob.getAttackTarget() == null && mob.getRevengeTarget() == null && shade != null; }

    @Override public void startExecuting() {
        settled = false;
        BlockPos place = shade;
        if (place == null) { return; }
        GoalLog.debug(mob, "heads for shade at {}, {}, {} for the day, sky over it {}", place.getX(), place.getY(), place.getZ(), covered(new BlockPos(mob)) ? "covered" : "open");
        walkTo(place);
    }

    private void walkTo(BlockPos place) { Walking.toBlock(mob, place, SPEED); }

    @Override public void resetTask() {
        shade = null;
        settled = false;
        mob.setSneaking(false);
        mob.getNavigator().clearPath();
    }

    @Override public void updateTask() {
        BlockPos place = shade;
        if (settled || place == null) { return; }
        if (mob.getDistanceSq(place.getX() + 0.5D, place.getY(), place.getZ() + 0.5D) <= 2.25D) {
            settled = true;
            mob.setSneaking(true);
            mob.getNavigator().clearPath();
            return;
        }
        if (mob.getNavigator().noPath() && mob.ticksExisted % 20 == 0) { walkTo(place); }
    }
}
