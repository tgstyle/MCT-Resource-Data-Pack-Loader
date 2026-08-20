package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.entity.ai.EntityAITasks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityAITasks.class) public interface IEntityAITasks { @Accessor("tickRate") void rdpl$setTickRate(int rate); }
