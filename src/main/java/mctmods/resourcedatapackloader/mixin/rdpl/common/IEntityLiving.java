package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityLiving.class) public interface IEntityLiving { @Accessor("deathLootTable") void rdpl$setDeathLootTable(ResourceLocation table); }
