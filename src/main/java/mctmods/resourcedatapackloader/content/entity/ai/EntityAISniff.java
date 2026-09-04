package mctmods.resourcedatapackloader.content.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

public final class EntityAISniff extends EntityAIBase {
    private static final int LISTEN = 20;
    private static final int GIVE_UP = 300;
    private final EntityCreature mob;
    private final int radius;
    private EntityPlayer heard;
    private BlockPos heardAt;
    private int listening;
    private int walked;

    public EntityAISniff(EntityCreature mob, int radius) {
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

    private EntityPlayer loudest() {
        EntityPlayer best = null;
        double nearest = Double.MAX_VALUE;
        for (EntityPlayer player : mob.world.playerEntities) {
            if (player.isSpectator() || player.isCreative() || player.isSneaking() || !player.isEntityAlive()) { continue; }
            if (player.motionX * player.motionX + player.motionZ * player.motionZ < 1.0E-4D && !player.isSprinting()) { continue; }
            double away = mob.getDistanceSq(player);
            if (away > (double) radius * radius || away >= nearest) { continue; }
            nearest = away;
            best = player;
        }
        return best;
    }

    @Override public boolean shouldContinueExecuting() { return heardAt != null && mob.getAttackTarget() == null && walked < GIVE_UP && !mob.getNavigator().noPath(); }

    @Override public void startExecuting() { mob.getNavigator().tryMoveToXYZ(heardAt.getX() + 0.5D, heardAt.getY(), heardAt.getZ() + 0.5D, 1.0D); }

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
        mob.getNavigator().tryMoveToXYZ(heardAt.getX() + 0.5D, heardAt.getY(), heardAt.getZ() + 0.5D, 1.0D);
    }
}
