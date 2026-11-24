package mctmods.resourcedatapackloader.common.entities.ai;

import mctmods.resourcedatapackloader.common.util.entity.Walking;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

public final class SniffGoal extends EntityAIBase {
    private static final int LISTEN = 20;
    private static final int GIVE_UP = 300;
    private static final double SPEED = 1.0D;
    private final EntityCreature mob;
    private final int radius;
    @Nullable private EntityPlayer heard;
    @Nullable private BlockPos heardAt;
    private int listening;
    private int walked;

    public SniffGoal(EntityCreature mob, int radius) {
        this.mob = mob;
        this.radius = radius;
        setMutexBits(1);
    }

    @Override public boolean shouldExecute() {
        if (mob.getAttackTarget() != null) { return false; }
        if (listening++ < LISTEN) { return false; }
        listening = 0;
        EntityPlayer found = loudest();
        if (found == null) { return false; }
        heard = found;
        heardAt = new BlockPos(found);
        walked = 0;
        return true;
    }

    @Nullable private EntityPlayer loudest() {
        return Walking.nearest(mob.world.playerEntities, SniffGoal::audible, mob::getDistanceSq, (double) radius * radius);
    }

    private static boolean audible(EntityPlayer player) {
        if (player.isSpectator() || player.isCreative() || player.isSneaking() || !player.isEntityAlive()) { return false; }
        return player.motionX * player.motionX + player.motionZ * player.motionZ >= 1.0E-4D || player.isSprinting();
    }

    @Override public boolean shouldContinueExecuting() { return heardAt != null && mob.getAttackTarget() == null && walked < GIVE_UP && !mob.getNavigator().noPath(); }

    @Override public void startExecuting() { if (heardAt != null) { walkTo(heardAt); } }

    private void walkTo(BlockPos at) { Walking.toBlock(mob, at, SPEED); }

    @Override public void resetTask() {
        heard = null;
        heardAt = null;
        mob.getNavigator().clearPath();
    }

    @Override public void updateTask() {
        walked++;
        if (heard != null && heard.isEntityAlive() && mob.getEntitySenses().canSee(heard) && mob.getDistanceSq(heard) < 64.0D) {
            mob.setAttackTarget(heard);
            return;
        }
        if (walked % LISTEN != 0) { return; }
        EntityPlayer again = loudest();
        if (again == null) { return; }
        heard = again;
        heardAt = new BlockPos(again);
        walkTo(heardAt);
    }
}
