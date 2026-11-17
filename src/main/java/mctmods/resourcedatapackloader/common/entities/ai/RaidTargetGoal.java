package mctmods.resourcedatapackloader.common.entities.ai;

import mctmods.resourcedatapackloader.common.pack.definitions.villages.BaseDef;
import mctmods.resourcedatapackloader.common.raids.ActiveRaid;
import mctmods.resourcedatapackloader.common.raids.RaidDefenders;
import mctmods.resourcedatapackloader.common.raids.RaidStorage;
import mctmods.resourcedatapackloader.common.util.world.PosTags;

import java.util.EnumSet;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;

public final class RaidTargetGoal extends TargetGoal {
    private static final int LOOK_CHANCE = 10;
    private static final TargetingConditions SUITABLE = TargetingConditions.forCombat().ignoreLineOfSight();
    @Nullable private Player chosen;

    public RaidTargetGoal(PathfinderMob mob) {
        super(mob, false);
        setFlags(EnumSet.of(Flag.TARGET));
    }

    @Override public boolean canUse() {
        if (mob.getRandom().nextInt(LOOK_CHANCE) != 0 || !mob.getPersistentData().contains(ActiveRaid.RAIDER) || !(mob.level() instanceof ServerLevel level)) { return false; }
        ActiveRaid raid = RaidStorage.get(level).raidOf(PosTags.read(mob.getPersistentData().getCompound(ActiveRaid.RAIDER)));
        if (raid == null) { return false; }
        BaseDef base = raid.def().base();
        boolean all = base != null && base.hostileToAll();
        UUID target = raid.target();
        double range = getFollowDistance();
        double best = range * range;
        double bestOther = best;
        Player defender = null;
        Player other = null;
        for (Player player : level.players()) {
            double distance = mob.distanceToSqr(player);
            if (distance > best && distance > bestOther || !canAttack(player, SUITABLE)) { continue; }
            boolean owned = player instanceof ServerPlayer member && RaidDefenders.defends(member, raid.owner(), target);
            if (owned && distance <= best && (target != null && target.equals(player.getUUID()) || mob.hasLineOfSight(player))) {
                best = distance;
                defender = player;
            }
            else if (!owned && all && distance <= bestOther && mob.hasLineOfSight(player)) {
                bestOther = distance;
                other = player;
            }
        }
        chosen = defender != null ? defender : other;
        return chosen != null;
    }

    @Override public void start() {
        mob.setTarget(chosen);
        super.start();
    }
}
