package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.world.gen.ChunkGeneratorEnd;
import net.minecraft.world.gen.structure.MapGenEndCity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ChunkGeneratorEnd.class) public interface IChunkGeneratorEnd { @Accessor("endCityGen") MapGenEndCity rdpl$endCities(); }
