package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RandomStrollGoal.class) public interface IRandomStrollGoal {
    @Accessor("speedModifier") double rdpl$speedModifier();
}
