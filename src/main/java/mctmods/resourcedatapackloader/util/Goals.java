package mctmods.resourcedatapackloader.util;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public final class Goals {
    private Goals() {}

    public static void remove(GoalSelector selector, Predicate<Goal> match) {
        for (WrappedGoal wrapped : new ArrayList<>(selector.getAvailableGoals())) {
            if (match.test(wrapped.getGoal())) { selector.removeGoal(wrapped.getGoal()); }
        }
    }

    public static List<WrappedGoal> entries(GoalSelector selector) { return new ArrayList<>(selector.getAvailableGoals()); }

    public static List<WrappedGoal> missing(List<WrappedGoal> from, List<WrappedGoal> in) {
        List<WrappedGoal> found = new ArrayList<>(from);
        found.removeAll(in);
        return found;
    }

    public static void undo(GoalSelector selector, List<WrappedGoal> added, List<WrappedGoal> dropped) {
        for (WrappedGoal wrapped : added) { selector.removeGoal(wrapped.getGoal()); }
        for (WrappedGoal wrapped : dropped) { selector.addGoal(wrapped.getPriority(), wrapped.getGoal()); }
    }
}
