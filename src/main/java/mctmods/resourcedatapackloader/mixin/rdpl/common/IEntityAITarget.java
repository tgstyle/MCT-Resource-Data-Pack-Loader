package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAITarget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityAITarget.class) public interface IEntityAITarget { @Accessor("taskOwner") EntityCreature rdpl$getTaskOwner(); }
