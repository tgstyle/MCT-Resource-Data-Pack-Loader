package mctmods.resourcedatapackloader.common.entities.behavior;

import mctmods.resourcedatapackloader.common.entities.ai.RankBreakDoorGoal;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;

public final class MobAbilities {
    private MobAbilities() {}

    public static void sightlessTarget(PathfinderMob mob, Class<? extends LivingEntity> type, int priority) { mob.targetSelector.addGoal(priority, new NearestAttackableTargetGoal<>(mob, type, false)); }

    @Nullable public static Goal breakDoor(Mob mob) {
        if (!mob.getNavigation().canNavigateGround()) {
            if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("{} has no ground navigation, skipping breakDoor", BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType())); }
            return null;
        }
        return new RankBreakDoorGoal(mob);
    }

    public static void armPick(Mob mob, String digItem, float dropChance, String owner) {
        if (mob.getMainHandItem().isEmpty() && !digItem.isEmpty()) { MobGear.fixed(mob, Map.of("mainhand", digItem), dropChance, owner); }
    }
}
