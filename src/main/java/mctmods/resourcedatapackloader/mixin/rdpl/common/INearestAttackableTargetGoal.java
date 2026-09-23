package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(NearestAttackableTargetGoal.class) public interface INearestAttackableTargetGoal {
    @Accessor("targetType") Class<?> rdpl$targetType();
}
