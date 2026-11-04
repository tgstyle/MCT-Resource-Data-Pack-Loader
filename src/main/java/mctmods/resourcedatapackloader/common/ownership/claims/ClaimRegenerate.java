package mctmods.resourcedatapackloader.common.ownership.claims;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.chunk.status.ChunkPyramid;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;

public final class ClaimRegenerate {
    private static String lastKey = "";
    @Nullable private static ProtoChunk lastScratch;

    private ClaimRegenerate() {}

    public static void section(ServerLevel level, int chunkX, int chunkZ, int sectionY) {
        LevelChunk live = level.getChunkSource().getChunkNow(chunkX, chunkZ);
        if (live == null || ClaimSections.outOfRange(live, sectionY)) { return; }
        ProtoChunk fresh = scratchFor(level, live);
        LevelChunkSection source = fresh.getSection(fresh.getSectionIndexFromSectionY(sectionY));
        ClaimSections.write(level, live, sectionY, source.hasOnlyAir() ? null : source.getStates());
    }

    private static ProtoChunk scratchFor(ServerLevel level, LevelChunk live) {
        String key = level.dimension().location() + ":" + live.getPos().x + ":" + live.getPos().z;
        if (lastScratch != null && key.equals(lastKey)) { return lastScratch; }
        ChunkGenerator generator = level.getChunkSource().getGenerator();
        RandomState random = level.getChunkSource().randomState();
        Registry<Biome> biomes = level.registryAccess().registryOrThrow(Registries.BIOME);
        ProtoChunk chunk = new ProtoChunk(live.getPos(), UpgradeData.EMPTY, level, biomes, null);
        Region region = new Region(level, chunk, biomes);
        StructureManager structures = level.structureManager().forWorldGenRegion(region);
        generator.createBiomes(random, Blender.empty(), structures, chunk).join();
        generator.fillFromNoise(Blender.empty(), random, structures, chunk).join();
        generator.buildSurface(region, structures, random, chunk);
        generator.applyCarvers(region, level.getSeed(), random, level.getBiomeManager(), structures, chunk, GenerationStep.Carving.AIR);
        lastKey = key;
        lastScratch = chunk;
        return chunk;
    }

    private static final class Region extends WorldGenRegion {
        private final ServerLevel host;
        private final ProtoChunk center;
        private final Registry<Biome> biomes;
        private final Map<Long, ProtoChunk> around = new HashMap<>();

        private Region(ServerLevel level, ProtoChunk center, Registry<Biome> biomes) {
            super(level, null, ChunkPyramid.GENERATION_PYRAMID.getStepTo(ChunkStatus.CARVERS), center);
            this.host = level;
            this.center = center;
            this.biomes = biomes;
        }

        @Override @Nonnull public ChunkAccess getChunk(int x, int z, @Nonnull ChunkStatus status, boolean requireChunk) {
            if (x == center.getPos().x && z == center.getPos().z) { return center; }
            return around.computeIfAbsent(ChunkPos.asLong(x, z), at -> new ProtoChunk(new ChunkPos(x, z), UpgradeData.EMPTY, host, biomes, null));
        }
    }
}
