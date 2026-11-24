package mctmods.resourcedatapackloader.common.entities.ai;

import mctmods.resourcedatapackloader.common.pack.definitions.villages.BaseDef;
import mctmods.resourcedatapackloader.common.raids.ActiveRaid;
import mctmods.resourcedatapackloader.common.raids.RaidDefenders;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public final class RaidTargetGoal extends EntityAITarget {
    private static final int LOOK_CHANCE = 10;
    @Nullable private EntityPlayer chosen;

    public RaidTargetGoal(EntityCreature mob) {
        super(mob, false);
        setMutexBits(1);
    }

    @Override public boolean shouldExecute() {
        if (taskOwner.getRNG().nextInt(LOOK_CHANCE) != 0) { return false; }
        ActiveRaid raid = ActiveRaid.of(taskOwner);
        if (raid == null) { return false; }
        BaseDef base = raid.def().base();
        boolean all = base != null && base.hostileToAll;
        UUID target = raid.target();
        double range = getTargetDistance();
        double best = range * range;
        double bestOther = best;
        EntityPlayer defender = null;
        EntityPlayer other = null;
        for (EntityPlayer player : taskOwner.world.playerEntities) {
            double distance = taskOwner.getDistanceSq(player);
            if (distance > best && distance > bestOther || !isSuitableTarget(player, false)) { continue; }
            boolean owned = player instanceof EntityPlayerMP && RaidDefenders.defends((EntityPlayerMP) player, raid.owner(), target);
            if (owned && distance <= best && (target != null && target.equals(player.getUniqueID()) || taskOwner.canEntityBeSeen(player))) {
                best = distance;
                defender = player;
            } else if (!owned && all && distance <= bestOther && taskOwner.canEntityBeSeen(player)) {
                bestOther = distance;
                other = player;
            }
        }
        chosen = defender != null ? defender : other;
        return chosen != null;
    }

    @Override public void startExecuting() {
        taskOwner.setAttackTarget(chosen);
        super.startExecuting();
    }
}
