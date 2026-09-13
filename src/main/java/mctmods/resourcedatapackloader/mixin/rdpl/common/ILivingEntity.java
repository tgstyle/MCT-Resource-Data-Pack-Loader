package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LivingEntity.class) public interface ILivingEntity { @Accessor("lastHurtByPlayerTime") void rdpl$setLastHurtByPlayerTime(int ticks); }
