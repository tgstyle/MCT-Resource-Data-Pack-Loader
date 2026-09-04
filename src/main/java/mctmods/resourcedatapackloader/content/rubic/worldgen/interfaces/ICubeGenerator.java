package mctmods.resourcedatapackloader.content.rubic.worldgen.interfaces;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.ICube;
import mctmods.resourcedatapackloader.content.rubic.worldgen.CubePrimer;
import mctmods.resourcedatapackloader.util.Box;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;

public interface ICubeGenerator {
    Box NO_REQUIREMENT = new Box(
            0, 0, 0,
            0, 0, 0
    );

    CubePrimer generateCube(int cubeX, int cubeY, int cubeZ);

    default CubePrimer generateCube(int cubeX, int cubeY, int cubeZ, CubePrimer primer) { return this.generateCube(cubeX, cubeY, cubeZ); }

    void generateColumn(Chunk column);

    void populate(ICube cube);

    @SuppressWarnings("unused") default Optional<CubePrimer> tryGenerateCube(int cubeX, int cubeY, int cubeZ, CubePrimer primer, boolean forceGenerate) {
        return Optional.of(this.generateCube(cubeX, cubeY, cubeZ, primer));
    }

    @SuppressWarnings("unused") default Optional<Chunk> tryGenerateColumn(World world, int columnX, int columnZ, ChunkPrimer primer, boolean forceGenerate) {
        Chunk column = new Chunk(world, columnX, columnZ);
        this.generateColumn(column);
        return Optional.of(column);
    }

    Box getFullPopulationRequirements(ICube cube);

    Box getPopulationPregenerationRequirements(ICube cube);

    void recreateStructures(Chunk column);

    List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType type, BlockPos pos);

    @Nullable BlockPos getClosestStructure(String name, BlockPos pos, boolean findUnexplored);

}
