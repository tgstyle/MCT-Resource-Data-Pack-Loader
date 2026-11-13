package mctmods.resourcedatapackloader.common.entities.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

public final class FollowPlayerGoal extends EntityAIBase {
    private static final int CHECK_TICKS = 10;
    private final EntityCreature creature;
    private final double keep;
    private final int lightLevel;
    private final double speed;
    private EntityPlayer player;
    private int wait;

    public FollowPlayerGoal(EntityCreature creature, int keep, int lightLevel, double speed) {
        this.creature = creature;
        this.keep = keep;
        this.lightLevel = lightLevel;
        this.speed = speed;
        setMutexBits(3);
    }

    @Nullable public EntityPlayer getPlayer() { return player; }

    private double range() { return creature.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).getAttributeValue(); }

    private boolean dark(EntityPlayer found) { return lightLevel <= 0 || creature.world.getLightFromNeighbors(new BlockPos(found)) < lightLevel; }

    @Override public boolean shouldExecute() {
        if (creature.getAttackTarget() != null || ++wait < CHECK_TICKS) { return false; }
        wait = 0;
        EntityPlayer found = creature.world.getNearestAttackablePlayer(creature, range(), range());
        if (found == null || !dark(found)) { return false; }
        player = found;
        return true;
    }

    @Override public boolean shouldContinueExecuting() {
        return player != null && player.isEntityAlive() && creature.getAttackTarget() == null && creature.getDistanceSq(player) <= range() * range() && dark(player);
    }

    @Override public void startExecuting() { wait = 0; }

    @Override public void updateTask() {
        creature.getLookHelper().setLookPositionWithEntity(player, 30.0F, creature.getVerticalFaceSpeed());
        if (++wait < CHECK_TICKS) { return; }
        wait = 0;
        if (creature.getDistanceSq(player) > keep * keep) { creature.getNavigator().tryMoveToEntityLiving(player, speed); }
        else { creature.getNavigator().clearPath(); }
    }

    @Override public void resetTask() {
        player = null;
        creature.getNavigator().clearPath();
    }
}
