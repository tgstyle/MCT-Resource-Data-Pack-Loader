package mctmods.resourcedatapackloader.util;

import mctmods.resourcedatapackloader.mixin.rdpl.common.IRandomStrollGoal;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.npc.Villager;

public final class Walking {
    private static final double STROLL = 1.0D;
    private static final double VILLAGER_STROLL = 0.5D;

    private Walking() {}

    public static boolean toBlock(PathfinderMob mob, BlockPos at, double speed) { return mob.getNavigation().moveTo(at.getX() + 0.5D, at.getY(), at.getZ() + 0.5D, speed); }

    public static double pace(PathfinderMob mob) {
        if (mob instanceof Villager) { return VILLAGER_STROLL; }
        for (WrappedGoal wrapped : mob.goalSelector.getAvailableGoals()) {
            if (wrapped.getGoal() instanceof RandomStrollGoal stroll) { return ((IRandomStrollGoal) stroll).rdpl$getSpeedModifier(); }
        }
        return STROLL;
    }
}
