package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LookAtPlayerGoal.class) public interface ILookAtPlayerGoal {
    @Accessor("lookAtContext") TargetingConditions rdpl$lookAtContext();
}
