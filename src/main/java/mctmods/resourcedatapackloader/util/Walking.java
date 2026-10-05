package mctmods.resourcedatapackloader.util;

import mctmods.resourcedatapackloader.mixin.rdpl.common.IRandomStrollGoal;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.npc.villager.Villager;

public final class Walking {
    private static final double STROLL = 1.0D;
    private static final double VILLAGER_STROLL = 0.5D;

    private Walking() {}

    public static double pace(PathfinderMob mob) {
        if (mob instanceof Villager) { return VILLAGER_STROLL; }
        for (WrappedGoal wrapped : mob.goalSelector.getAvailableGoals()) {
            if (wrapped.getGoal() instanceof RandomStrollGoal stroll) { return ((IRandomStrollGoal) stroll).rdpl$speedModifier(); }
        }
        return STROLL;
    }
}
