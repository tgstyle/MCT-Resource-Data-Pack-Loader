package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.entity.ai.EntityAIWander;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityAIWander.class) public interface IEntityAIWander { @Accessor("speed") double rdpl$speed(); }
