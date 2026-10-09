package mctmods.resourcedatapackloader.content.entity.ai;

import mctmods.resourcedatapackloader.content.raid.ActiveRaid;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import javax.annotation.Nullable;

public final class EntityAIRaidMarch extends EntityAIBase {
    private static final double ARRIVED_SQ = 100.0D;
    private static final double SPEED = 1.0D;
    private static final int LOOK_EVERY = 20;
    private final EntityCreature mob;
    @Nullable private Vec3d toward;

    public EntityAIRaidMarch(EntityCreature mob) {
        this.mob = mob;
        setMutexBits(1);
    }

    @Override public boolean shouldExecute() {
        if (mob.getAttackTarget() != null || mob.ticksExisted % LOOK_EVERY != 0 || !mob.getEntityData().hasKey(ActiveRaid.RAIDER)) { return false; }
        BlockPos center = NBTUtil.getPosFromTag(mob.getEntityData().getCompoundTag(ActiveRaid.RAIDER));
        if (mob.getDistanceSq(center) <= ARRIVED_SQ) { return false; }
        toward = RandomPositionGenerator.findRandomTargetBlockTowards(mob, 15, 4, new Vec3d(center.getX() + 0.5D, center.getY(), center.getZ() + 0.5D));
        return toward != null;
    }

    @Override public boolean shouldContinueExecuting() { return mob.getAttackTarget() == null && !mob.getNavigator().noPath(); }

    @Override public void startExecuting() {
        if (toward != null) { mob.getNavigator().tryMoveToXYZ(toward.x, toward.y, toward.z, SPEED); }
    }

    @Override public void resetTask() { toward = null; }
}
