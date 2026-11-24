package mctmods.resourcedatapackloader.common.entities.behavior;

import mctmods.resourcedatapackloader.common.entities.ai.DigGoal;
import mctmods.resourcedatapackloader.common.entities.ai.RankBreakDoorGoal;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.player.Player;

public final class MobAbilities {
    private MobAbilities() {}

    public static void dig(PathfinderMob mob, boolean sightlessPlayer) {
        mob.goalSelector.addGoal(1, new DigGoal(mob));
        if (sightlessPlayer) { mob.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(mob, Player.class, false)); }
    }

    @Nullable public static Goal breakDoor(Mob mob) {
        if (!(mob.getNavigation() instanceof GroundPathNavigation)) {
            if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("{} cannot break doors, it has no ground navigation", mob.getType()); }
            return null;
        }
        return new RankBreakDoorGoal(mob);
    }

    public static void armPick(Mob mob, String digItem, float dropChance, String owner) {
        if (mob.getMainHandItem().isEmpty() && !digItem.isEmpty()) { MobGear.fixed(mob, Map.of("mainhand", digItem), dropChance, owner); }
    }
}
