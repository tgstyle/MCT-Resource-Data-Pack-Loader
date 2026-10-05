package mctmods.resourcedatapackloader.util;

import mctmods.resourcedatapackloader.mixin.rdpl.common.IEntityAIWander;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.EntityAIWander;

public final class Walking {
    private static final double STROLL = 1.0D;

    private Walking() {}

    public static double pace(EntityCreature mob) {
        for (EntityAITasks.EntityAITaskEntry entry : mob.tasks.taskEntries) {
            if (entry.action instanceof EntityAIWander) { return ((IEntityAIWander) entry.action).rdpl$speed(); }
        }
        return STROLL;
    }
}
