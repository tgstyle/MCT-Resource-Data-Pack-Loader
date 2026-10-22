package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.world.DifficultyInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DifficultyInstance.class) public interface IDifficultyInstance { @Mutable @Accessor("additionalDifficulty") void rdpl$additional(float value); }
