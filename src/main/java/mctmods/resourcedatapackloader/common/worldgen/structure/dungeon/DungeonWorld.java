package mctmods.resourcedatapackloader.common.worldgen.structure.dungeon;

import mctmods.resourcedatapackloader.common.worldgen.surface.ContentWorldShape;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public record DungeonWorld(Structure.GenerationContext context, String dimension, BlockPos spawn) {
    private static final int BIOME_Y = 64;

    public long seed() { return context.seed(); }

    public ChunkGenerator generator() { return context.chunkGenerator(); }

    public StructureTemplateManager templates() { return context.structureTemplateManager(); }

    public int floor() { return context.heightAccessor().getMinBuildHeight(); }

    public int seaLevel() { return context.chunkGenerator().getSeaLevel(); }

    public int surfaceAt(int blockX, int blockZ) { return ContentWorldShape.surfaceAt(context, blockX, blockZ); }

    public Holder<Biome> biomeAt(int blockX, int blockZ) { return context.biomeSource().getNoiseBiome(QuartPos.fromBlock(blockX), QuartPos.fromBlock(BIOME_Y), QuartPos.fromBlock(blockZ), context.randomState().sampler()); }
}
