package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.world.biome.BiomeForest;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BiomeForest.class) public interface IBiomeForest { @Accessor("type") BiomeForest.Type rdpl$type(); }
