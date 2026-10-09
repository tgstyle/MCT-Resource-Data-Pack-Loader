package mctmods.resourcedatapackloader.util;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import java.util.ArrayList;
import java.util.function.Predicate;

public final class Goals {
    private Goals() {}

    public static void remove(GoalSelector selector, Predicate<Goal> match) {
        for (WrappedGoal wrapped : new ArrayList<>(selector.getAvailableGoals())) {
            if (match.test(wrapped.getGoal())) { selector.removeGoal(wrapped.getGoal()); }
        }
    }
}
