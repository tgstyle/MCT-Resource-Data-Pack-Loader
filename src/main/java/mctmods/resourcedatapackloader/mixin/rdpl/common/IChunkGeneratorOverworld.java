package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.world.WorldType;
import net.minecraft.world.gen.ChunkGeneratorOverworld;
import net.minecraft.world.gen.ChunkGeneratorSettings;
import net.minecraft.world.gen.NoiseGeneratorOctaves;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ChunkGeneratorOverworld.class) public interface IChunkGeneratorOverworld {
    @Accessor("minLimitPerlinNoise") NoiseGeneratorOctaves rdpl$minLimit();
    @Accessor("maxLimitPerlinNoise") NoiseGeneratorOctaves rdpl$maxLimit();
    @Accessor("mainPerlinNoise") NoiseGeneratorOctaves rdpl$mainNoise();
    @Accessor("depthNoise") NoiseGeneratorOctaves rdpl$depthNoise();
    @Accessor("settings") ChunkGeneratorSettings rdpl$settings();
    @Accessor("terrainType") WorldType rdpl$terrainType();
    @Accessor("biomeWeights") float[] rdpl$biomeWeights();
}
