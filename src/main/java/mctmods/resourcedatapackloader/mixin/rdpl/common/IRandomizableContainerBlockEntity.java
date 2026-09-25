package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RandomizableContainerBlockEntity.class) public interface IRandomizableContainerBlockEntity { @Accessor("lootTable") ResourceLocation rdpl$getLootTable(); }
