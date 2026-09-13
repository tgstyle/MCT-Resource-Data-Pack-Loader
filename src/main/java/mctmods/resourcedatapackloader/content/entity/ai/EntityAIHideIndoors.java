package mctmods.resourcedatapackloader.content.entity.ai;

import mctmods.resourcedatapackloader.content.ContentRaids;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.Village;
import net.minecraft.village.VillageDoorInfo;
import javax.annotation.Nullable;

public final class EntityAIHideIndoors extends EntityAIBase {
    private static final int LOOK_EVERY = 20;
    private static final int SEARCH_SQ = 32 * 32;
    private static final double ARRIVED_SQ = 2.25D;
    private static final double SPEED = 0.7D;
    private static final double FAR_SQ = 256.0D;
    private final EntityCreature mob;
    private BlockPos inside;

    public EntityAIHideIndoors(EntityCreature mob) {
        this.mob = mob;
        setMutexBits(1);
    }

    @Override public boolean shouldExecute() {
        if (mob.ticksExisted % LOOK_EVERY != 0 || !ContentRaids.hiding(mob)) { return false; }
        inside = hidingPlace();
        return inside != null;
    }

    @Nullable private BlockPos hidingPlace() {
        BlockPos at = new BlockPos(mob);
        BlockPos best = null;
        int bestDistance = SEARCH_SQ;
        for (Village village : mob.world.getVillageCollection().getVillageList()) {
            if (!village.isBlockPosWithinSqVillageRadius(at)) { continue; }
            for (VillageDoorInfo door : village.getVillageDoorInfoList()) {
                int distance = door.getDistanceToInsideBlockSq(at);
                if (distance < bestDistance) {
                    bestDistance = distance;
                    best = door.getInsideBlockPos();
                }
            }
        }
        return best;
    }

    @Override public boolean shouldContinueExecuting() { return inside != null && ContentRaids.hiding(mob); }

    @Override public void startExecuting() {
        ContentLog.LOGGER.debug("{} at {}, {}, {} hides indoors at {}, {}, {}", mob.getName(), (int) mob.posX, (int) mob.posY, (int) mob.posZ, inside.getX(), inside.getY(), inside.getZ());
        walkIn();
    }

    private void walkIn() {
        Vec3d door = new Vec3d(inside.getX() + 0.5D, inside.getY(), inside.getZ() + 0.5D);
        if (mob.getDistanceSq(inside) > FAR_SQ) {
            Vec3d toward = RandomPositionGenerator.findRandomTargetBlockTowards(mob, 14, 3, door);
            if (toward != null) { mob.getNavigator().tryMoveToXYZ(toward.x, toward.y, toward.z, SPEED); }
            return;
        }
        mob.getNavigator().tryMoveToXYZ(door.x, door.y, door.z, SPEED);
    }

    @Override public void resetTask() {
        inside = null;
        mob.getNavigator().clearPath();
    }

    @Override public void updateTask() {
        if (mob.getDistanceSq(inside.getX() + 0.5D, inside.getY(), inside.getZ() + 0.5D) <= ARRIVED_SQ) {
            mob.getNavigator().clearPath();
            return;
        }
        if (mob.getNavigator().noPath() && mob.ticksExisted % LOOK_EVERY == 0) { walkIn(); }
    }
}
