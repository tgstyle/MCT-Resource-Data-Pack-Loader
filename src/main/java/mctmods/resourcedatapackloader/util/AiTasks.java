package mctmods.resourcedatapackloader.util;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITasks;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public final class AiTasks {
    private AiTasks() {}

    public static void remove(EntityAITasks tasks, Predicate<EntityAIBase> match) {
        for (EntityAITasks.EntityAITaskEntry entry : new ArrayList<>(tasks.taskEntries)) {
            if (match.test(entry.action)) { tasks.removeTask(entry.action); }
        }
    }

    public static void clear(EntityAITasks tasks) { remove(tasks, action -> true); }

    public static List<EntityAITasks.EntityAITaskEntry> entries(EntityAITasks tasks) { return new ArrayList<>(tasks.taskEntries); }

    public static List<EntityAITasks.EntityAITaskEntry> missing(List<EntityAITasks.EntityAITaskEntry> from, List<EntityAITasks.EntityAITaskEntry> in) {
        List<EntityAITasks.EntityAITaskEntry> found = new ArrayList<>(from);
        found.removeAll(in);
        return found;
    }

    public static void undo(EntityAITasks tasks, List<EntityAITasks.EntityAITaskEntry> added, List<EntityAITasks.EntityAITaskEntry> dropped) {
        for (EntityAITasks.EntityAITaskEntry entry : added) { tasks.removeTask(entry.action); }
        for (EntityAITasks.EntityAITaskEntry entry : dropped) { tasks.addTask(entry.priority, entry.action); }
    }
}
