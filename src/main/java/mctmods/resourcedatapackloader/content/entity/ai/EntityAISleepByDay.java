package mctmods.resourcedatapackloader.content.entity.ai;

import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class EntityAISleepByDay extends EntityAIBase {
    private final EntityCreature mob;
    private BlockPos shade;
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
        for (int tries = 0; tries < 10; tries++) {
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
        if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("{} at {}, {}, {} heads for shade at {}, {}, {} for the day, sky over it {}", mob.getName(), (int) mob.posX, (int) mob.posY, (int) mob.posZ, shade.getX(), shade.getY(), shade.getZ(), covered(new BlockPos(mob)) ? "covered" : "open"); }
        mob.getNavigator().tryMoveToXYZ(shade.getX() + 0.5D, shade.getY(), shade.getZ() + 0.5D, 1.0D);
    }

    @Override public void resetTask() {
        shade = null;
        settled = false;
        mob.getNavigator().clearPath();
    }

    @Override public void updateTask() {
        if (settled) { return; }
        if (mob.getDistanceSq(shade.getX() + 0.5D, shade.getY(), shade.getZ() + 0.5D) <= 2.25D) {
            settled = true;
            mob.getNavigator().clearPath();
            return;
        }
        if (mob.getNavigator().noPath() && mob.ticksExisted % 20 == 0) { mob.getNavigator().tryMoveToXYZ(shade.getX() + 0.5D, shade.getY(), shade.getZ() + 0.5D, 1.0D); }
    }
}
