package mctmods.resourcedatapackloader.common.entities.ai;

import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.BreakDoorGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.monster.Vindicator;

public final class RankBreakDoorGoal extends BreakDoorGoal {
    public RankBreakDoorGoal(Mob mob) { super(mob, difficulty -> difficulty == Difficulty.HARD); }

    @Override public boolean canUse() {
        grant();
        return super.canUse();
    }

    @Override public boolean canContinueToUse() {
        grant();
        return super.canContinueToUse();
    }

    private void grant() { if (mob.getNavigation() instanceof GroundPathNavigation ground) { ground.setCanOpenDoors(true); } }

    public static void holdDoors(Entity entity) {
        if (entity instanceof Vindicator vindicator && !vindicator.level().isClientSide() && held(vindicator) && vindicator.getNavigation() instanceof GroundPathNavigation ground) {
            ground.setCanOpenDoors(true);
        }
    }

    private static boolean held(Mob mob) {
        for (WrappedGoal wrapped : mob.goalSelector.getAvailableGoals()) {
            if (wrapped.getGoal() instanceof RankBreakDoorGoal) { return true; }
        }
        return false;
    }
}
