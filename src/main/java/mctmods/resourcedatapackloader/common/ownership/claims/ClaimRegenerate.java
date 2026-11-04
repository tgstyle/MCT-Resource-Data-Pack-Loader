package mctmods.resourcedatapackloader.common.ownership.claims;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;

public final class ClaimRegenerate {
    private static final int REACH = 8;
    private static ServerLevel scratchLevel;
    private static int scratchX;
    private static int scratchZ;
    private static ProtoChunk scratch;

    private ClaimRegenerate() {}

    public static void section(ServerLevel level, int chunkX, int chunkZ, int sectionY) {
        LevelChunk live = level.getChunkSource().getChunkNow(chunkX, chunkZ);
        if (live == null || sectionY < level.getMinSection() || sectionY >= level.getMaxSection()) { return; }
        ProtoChunk fresh = scratchFor(level, chunkX, chunkZ);
        ClaimRestore.replace(live, sectionY, fresh.getSection(fresh.getSectionIndexFromSectionY(sectionY)).getStates());
    }

    private static ProtoChunk scratchFor(ServerLevel level, int chunkX, int chunkZ) {
        if (scratch == null || scratchLevel != level || scratchX != chunkX || scratchZ != chunkZ) {
            scratch = terrain(level, chunkX, chunkZ);
            scratchLevel = level;
            scratchX = chunkX;
            scratchZ = chunkZ;
        }
        return scratch;
    }

    private static ProtoChunk terrain(ServerLevel level, int chunkX, int chunkZ) {
        Registry<Biome> biomes = level.registryAccess().registryOrThrow(Registries.BIOME);
        ChunkGenerator generator = level.getChunkSource().getGenerator();
        RandomState random = level.getChunkSource().randomState();
        List<ChunkAccess> around = new ArrayList<>();
        for (int dz = -REACH; dz <= REACH; dz++) {
            for (int dx = -REACH; dx <= REACH; dx++) { around.add(new ProtoChunk(new ChunkPos(chunkX + dx, chunkZ + dz), UpgradeData.EMPTY, level, biomes, null)); }
        }
        ProtoChunk center = (ProtoChunk) around.get(around.size() / 2);
        WorldGenRegion region = new WorldGenRegion(level, around, ChunkStatus.CARVERS, 0);
        StructureManager structures = level.structureManager().forWorldGenRegion(region);
        generator.createBiomes(Runnable::run, random, Blender.empty(), structures, center).join();
        generator.fillFromNoise(Runnable::run, Blender.empty(), random, structures, center).join();
        generator.buildSurface(region, structures, random, center);
        generator.applyCarvers(region, level.getSeed(), random, level.getBiomeManager(), structures, center, GenerationStep.Carving.AIR);
        return center;
    }
}
