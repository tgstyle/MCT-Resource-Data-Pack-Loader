package mctmods.resourcedatapackloader.util;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITasks;
import java.util.ArrayList;
import java.util.function.Predicate;

public final class AiTasks {
    private AiTasks() {}

    public static void remove(EntityAITasks tasks, Predicate<EntityAIBase> match) {
        for (EntityAITasks.EntityAITaskEntry entry : new ArrayList<>(tasks.taskEntries)) {
            if (match.test(entry.action)) { tasks.removeTask(entry.action); }
        }
    }

    public static void clear(EntityAITasks tasks) { remove(tasks, action -> true); }
}
