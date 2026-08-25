package mctmods.resourcedatapackloader.content.rubic.worldgen.generator;

import mctmods.resourcedatapackloader.content.rubic.Rubic;
import mctmods.resourcedatapackloader.content.rubic.RubicWorldControl;
import mctmods.resourcedatapackloader.content.rubic.world.cube.Cube;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IColumnInternal;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IMinMaxHeight;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.ICube;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorldInternal;
import mctmods.resourcedatapackloader.content.rubic.worldgen.CubePrimer;
import mctmods.resourcedatapackloader.content.rubic.worldgen.WorldgenHangWatchdog;
import mctmods.resourcedatapackloader.content.rubic.worldgen.interfaces.ICubeGenerator;
import mctmods.resourcedatapackloader.content.worldgen.ContentVoidWorld;
import mctmods.resourcedatapackloader.content.worldgen.ContentSeams;
import mctmods.resourcedatapackloader.content.worldgen.ContentWorldgen;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IGameRegistry;
import mctmods.resourcedatapackloader.util.Box;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Coords;
import mctmods.resourcedatapackloader.util.compat.CompatHandler;
import mctmods.resourcedatapackloader.util.compat.GcRubicWorldgen;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;
import java.util.HashMap;
import java.util.Objects;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class VanillaCompatibilityGenerator implements ICubeGenerator {
    private boolean isInit = false;
    private int worldHeightCubes;
    private final int offsetCubes;
    @Nonnull private final IChunkGenerator vanilla;
    @Nonnull private final World world;
    private Chunk lastChunk;
    private boolean optimizationHack;
    private Biome[] biomes;
    @Nonnull private IBlockState extensionBlockBottom = Objects.requireNonNull(Blocks.STONE).getDefaultState();
    @Nonnull private IBlockState extensionBlockTop = Objects.requireNonNull(Blocks.AIR).getDefaultState();
    @Nonnull private IBlockState fillBlockTop = Objects.requireNonNull(Blocks.AIR).getDefaultState();
    private boolean hasTopBedrock = false, hasBottomBedrock = true;
    private DeepGeneration deep;
    private boolean deepPopulation;

    public VanillaCompatibilityGenerator(@Nonnull IChunkGenerator vanilla, @Nonnull World world) {
        this.vanilla = vanilla;
        this.world = world;
        this.offsetCubes = RubicWorldControl.terrainOffsetCubes();
    }

    private void tryInit(IChunkGenerator vanilla, World world) {
        if (isInit) { return; }
        isInit = true;
        lastChunk = vanilla.generateChunk(0, 0);
        IBlockState airState = Objects.requireNonNull(Blocks.AIR).getDefaultState();
        IBlockState bedrockState = Objects.requireNonNull(Blocks.BEDROCK).getDefaultState();
        int worldHeightBlocks = ((IRubicWorld) world).rdpl$getMaxGenerationHeight();
        worldHeightCubes = worldHeightBlocks / Cube.SIZE;
        Map<IBlockState, Integer> blockHistogramBottom = new HashMap<>();
        Map<IBlockState, Integer> blockHistogramTop = new HashMap<>();
        ExtendedBlockStorage bottomEBS = lastChunk.getBlockStorageArray()[0];
        for (int x = 0; x < Cube.SIZE; x++) {
            for (int z = 0; z < Cube.SIZE; z++) {
                for (int y = 0; y < 3; y++) {
                    IBlockState blockState = bottomEBS == null ?
                            airState : bottomEBS.get(x, y, z);
                    int count = blockHistogramBottom.getOrDefault(blockState, 0);
                    blockHistogramBottom.put(blockState, count + 1);
                }
                for (int y = worldHeightBlocks - 1; y > worldHeightBlocks - 4; y--) {
                    int localY = Coords.blockToLocal(y);
                    ExtendedBlockStorage ebs = lastChunk.getBlockStorageArray()[Coords.blockToCube(y)];
                    IBlockState blockState = ebs == null ? airState : ebs.get(x, localY, z);
                    int count = blockHistogramTop.getOrDefault(blockState, 0);
                    blockHistogramTop.put(blockState, count + 1);
                }
            }
        }
        ContentLog.LOGGER.debug("Block histograms: \nTop: {}\nBottom: {}", blockHistogramTop, blockHistogramBottom);
        int topcount = 0;
        for (Map.Entry<IBlockState, Integer> entry : blockHistogramBottom.entrySet()) {
            if (entry.getValue() > topcount && entry.getKey().getBlock() != Blocks.BEDROCK) {
                extensionBlockBottom = entry.getKey();
                topcount = entry.getValue();
            }
        }
        hasBottomBedrock = blockHistogramBottom.getOrDefault(bedrockState, 0) > 0;
        Rubic.LOGGER.info("Detected filler block {} from layers [0, 2], bedrock={}", extensionBlockBottom.getBlock().getTranslationKey(), hasBottomBedrock);
        topcount = 0;
        for (Map.Entry<IBlockState, Integer> entry : blockHistogramTop.entrySet()) {
            if (entry.getValue() > topcount && entry.getKey().getBlock() != Blocks.BEDROCK) {
                extensionBlockTop = entry.getKey();
                topcount = entry.getValue();
            }
        }
        hasTopBedrock = blockHistogramTop.getOrDefault(bedrockState, 0) > 0;
        fillBlockTop = hasTopBedrock ? airState : extensionBlockTop;
        Rubic.LOGGER.info("Detected filler block {} from layers [{}, {}], bedrock={}", extensionBlockTop.getBlock().getTranslationKey(), worldHeightBlocks - 3, worldHeightBlocks - 1, hasTopBedrock);
        if (hasTopBedrock) { Rubic.LOGGER.info("Dimension {} is capped with bedrock at {}, so the world above it is left empty instead of being filled with {}", world.provider.getDimension(), worldHeightBlocks, extensionBlockTop.getBlock().getTranslationKey()); }
        if (hasBottomBedrock && ContentSeams.opensFloor(world.provider.getDimension())) {
            hasBottomBedrock = false;
            Rubic.LOGGER.info("A world seam opens the floor of dimension {}, so its bedrock is not generated", world.provider.getDimension());
        }
        if (hasTopBedrock && ContentSeams.opensCeiling(world.provider.getDimension())) {
            hasTopBedrock = false;
            Rubic.LOGGER.info("A world seam opens the ceiling of dimension {}, so its bedrock is not generated", world.provider.getDimension());
        }
        deep = new DeepGeneration(world, offsetCubes << 4, extensionBlockBottom);
        deepPopulation = ContentWorldgen.deepestMinHeight() < 0;
    }

    @Override public void generateColumn(Chunk column) {
        this.biomes = this.world.getBiomeProvider()
                .getBiomes(this.biomes,
                        Coords.cubeToMinBlock(column.x),
                        Coords.cubeToMinBlock(column.z),
                        Cube.SIZE, Cube.SIZE);
        byte[] abyte = column.getBiomeArray();
        for (int i = 0; i < abyte.length; ++i) { abyte[i] = (byte) Biome.getIdForBiome(this.biomes[i]); }
    }

    @Override public void recreateStructures(Chunk column) { vanilla.recreateStructures(column, column.x, column.z); }

    @Override public CubePrimer generateCube(int cubeX, int cubeY, int cubeZ) { return generateCube(cubeX, cubeY, cubeZ, new CubePrimer()); }

    @Override public CubePrimer generateCube(int cubeX, int cubeY, int cubeZ, CubePrimer primer) {
        try {
            WorldgenHangWatchdog.startWorldGen();
            tryInit(vanilla, world);
            Random rand = new Random(world.getSeed());
            rand.setSeed(rand.nextInt() ^ cubeX);
            rand.setSeed(rand.nextInt() ^ cubeZ);
            int vanillaY = cubeY - offsetCubes;
            if (vanillaY < 0 || vanillaY >= worldHeightCubes) {
                if (vanillaY < 0 && deep.wantsDeep()) { deep.fillDeepCube(primer, cubeX, cubeY, cubeZ, rand, hasTopBedrock, hasBottomBedrock); }
                else if (vanillaY >= worldHeightCubes && deep.wantsSky()) { deep.fillSkyCube(primer, cubeX, cubeY, cubeZ, rand, hasTopBedrock, hasBottomBedrock); }
                else {
                    for (int y = 0; y < Cube.SIZE; y++) {
                        for (int z = 0; z < Cube.SIZE; z++) {
                            for (int x = 0; x < Cube.SIZE; x++) {
                                IBlockState state = vanillaY < 0 ? extensionBlockBottom : fillBlockTop;
                                int blockY = Coords.localToBlock(vanillaY, y);
                                state = WorldGenUtils.getRandomBedrockReplacement(world, rand, state, blockY, 5,
                                        hasTopBedrock, hasBottomBedrock);
                                primer.setBlockState(x, y, z, state);
                            }
                        }
                    }
                }
            }
            else {
                if (lastChunk.x != cubeX || lastChunk.z != cubeZ) {
                    try (IRubicWorldInternal.CompatGenerationScope ignored =
                                 ((IRubicWorldInternal.Server) world).rdpl$doCompatibilityGeneration()) {
                        lastChunk = vanilla.generateChunk(cubeX, cubeZ);
                        ChunkPrimer chunkPrimer = ((IColumnInternal) lastChunk).getCompatGenerationPrimer();
                        if (chunkPrimer == null) { Rubic.LOGGER.error("Optimized compatibility generation failed for chunk at {}, {}", cubeX, cubeZ); }
                        else {
                            ((IColumnInternal) lastChunk).syncCompatGenerationWrites();
                            replaceBedrock(chunkPrimer, rand);
                            deep.dressBandPrimer(chunkPrimer, cubeX, cubeZ);
                        }
                    }
                }
                if (!optimizationHack) {
                    optimizationHack = true;
                    for (int y = worldHeightCubes - 1; y >= 0; y--) {
                        if (y == vanillaY) { continue; }
                        ((IRubicWorld) world).rdpl$getCubeFromCubeCoords(cubeX, y + offsetCubes, cubeZ);
                    }
                    optimizationHack = false;
                }
                ChunkPrimer chunkPrimer = ((IColumnInternal) lastChunk).getCompatGenerationPrimer();
                if (chunkPrimer != null) { return new CubePrimerWrapper(chunkPrimer, vanillaY); }
                ExtendedBlockStorage storage = lastChunk.getBlockStorageArray()[vanillaY];
                if (((IRubicWorld) world).rdpl$getMaxHeight() == 16) {
                    if (vanillaY != 0) { storage = null; }
                    else { storage = lastChunk.getBlockStorageArray()[4]; }
                }
                if (storage != null && !storage.isEmpty()) {
                    IBlockState bedrockState = Objects.requireNonNull(Blocks.BEDROCK).getDefaultState();
                    for (int y = 0; y < Cube.SIZE; y++) {
                        int blockY = Coords.localToBlock(vanillaY, y);
                        for (int z = 0; z < Cube.SIZE; z++) {
                            for (int x = 0; x < Cube.SIZE; x++) {
                                IBlockState state = storage.get(x, y, z);
                                if (state == bedrockState) {
                                    if (y < Cube.SIZE / 2) { state = extensionBlockBottom; }
                                    else { state = extensionBlockTop; }
                                }
                                state = WorldGenUtils.getRandomBedrockReplacement(world, rand, state, blockY, 5,
                                        hasTopBedrock, hasBottomBedrock);
                                primer.setBlockState(x, y, z, state);
                            }
                        }
                    }
                }
            }
            return primer;
        } finally {
            WorldgenHangWatchdog.endWorldGen();
        }
    }

    private void replaceBedrock(ChunkPrimer chunkPrimer, Random rand) {
        for (int y = 0; y < 8; y++) { replaceBedrockAtLayer(chunkPrimer, rand, y); }
        int startY = Coords.localToBlock(worldHeightCubes - 1, 8);
        int endY = Coords.cubeToMinBlock(worldHeightCubes);
        for (int y = startY; y < endY; y++) { replaceBedrockAtLayer(chunkPrimer, rand, y); }
    }

    private void replaceBedrockAtLayer(ChunkPrimer chunkPrimer, Random rand, int y) {
        IBlockState bedrockState = Objects.requireNonNull(Blocks.BEDROCK).getDefaultState();
        for (int z = 0; z < Cube.SIZE; z++) {
            for (int x = 0; x < Cube.SIZE; x++) {
                IBlockState state = chunkPrimer.getBlockState(x, y, z);
                if (state == bedrockState) {
                    if (y < 64) {
                        chunkPrimer.setBlockState(x, y, z,
                                WorldGenUtils.getRandomBedrockReplacement(world, rand, extensionBlockBottom, y, 5, hasTopBedrock, hasBottomBedrock));
                    }
                    else {
                        chunkPrimer.setBlockState(x, y, z,
                                WorldGenUtils.getRandomBedrockReplacement(world, rand, extensionBlockTop, y, 5, hasTopBedrock, hasBottomBedrock));
                    }
                }
            }
        }
    }

    @Override public void populate(ICube cube) {
        try {
            WorldgenHangWatchdog.startWorldGen();
            tryInit(vanilla, world);
            int vanillaY = cube.getY() - offsetCubes;
            if (vanillaY < 0 || vanillaY >= worldHeightCubes) {
                if (vanillaY < 0 && deep.wantsDeep()) { deep.populateDeepCube(cube.getX(), cube.getY(), cube.getZ()); }
                return;
            }
            for (int y = worldHeightCubes - 1; y >= 0; y--) { ((IRubicWorldInternal) world).rdpl$getCubeFromCubeCoords(cube.getX(), y + offsetCubes, cube.getZ()).setPopulated(true); }
            if (!ContentVoidWorld.appliesTo(world)) {
                try {
                    CompatHandler.beforePopulate(world);
                    vanilla.populate(cube.getX(), cube.getZ());
                } finally {
                    CompatHandler.afterPopulate(world);
                }
            }
            applyModGenerators(cube.getX(), cube.getZ(), world, vanilla, world.getChunkProvider());
        } finally {
            WorldgenHangWatchdog.endWorldGen();
        }
    }

    private void applyModGenerators(int x, int z, World world, IChunkGenerator vanillaGen, IChunkProvider provider) {
        List<IWorldGenerator> generators = IGameRegistry.getSortedGeneratorList();
        if (generators == null) {
            IGameRegistry.computeGenerators();
            generators = IGameRegistry.getSortedGeneratorList();
            assert generators != null;
        }
        long worldSeed = world.getSeed();
        Random fmlRandom = new Random(worldSeed);
        long xSeed = fmlRandom.nextLong() >> 2 + 1L;
        long zSeed = fmlRandom.nextLong() >> 2 + 1L;
        long chunkSeed = (xSeed * x + zSeed * z) ^ worldSeed;
        for (IWorldGenerator generator : generators) {
            if (!GcRubicWorldgen.allows(world, generator)) { continue; }
            fmlRandom.setSeed(chunkSeed);
            try {
                CompatHandler.beforeGenerate(world);
                generator.generate(fmlRandom, x, z, world, vanillaGen, provider);
            } finally {
                CompatHandler.afterGenerate(world);
            }
        }
    }

    @Override public Box getFullPopulationRequirements(ICube cube) {
        int vanillaY = cube.getY() - offsetCubes;
        if (vanillaY >= 0 && vanillaY < worldHeightCubes) {
            return new Box(
                    -1, rdpl$populationFloorOffset(cube), -1,
                    0, worldHeightCubes - vanillaY - 1, 0
            );
        }
        return NO_REQUIREMENT;
    }

    @Override public Box getPopulationPregenerationRequirements(ICube cube) {
        int vanillaY = cube.getY() - offsetCubes;
        if (vanillaY >= 0 && vanillaY < worldHeightCubes) {
            return new Box(
                    0, rdpl$populationFloorOffset(cube), 0,
                    1, worldHeightCubes - vanillaY - 1, 1
            );
        }
        return NO_REQUIREMENT;
    }

    private int rdpl$populationFloorOffset(ICube cube) {
        int vanillaY = cube.getY() - offsetCubes;
        if (!deepPopulation) { return -vanillaY; }
        return Coords.blockToCube(((IMinMaxHeight) world).rdpl$getMinHeight()) - cube.getY();
    }

    @Override public List<SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) { return vanilla.getPossibleCreatures(creatureType, pos); }

    @Override @Nullable public BlockPos getClosestStructure(String name, BlockPos pos, boolean findUnexplored) {
        return vanilla.getNearestStructurePos(world, name, pos, findUnexplored);
    }

    private static class CubePrimerWrapper extends CubePrimer {
        private final ChunkPrimer chunkPrimer;
        private final int cubeYBase;

        public CubePrimerWrapper(ChunkPrimer chunkPrimer, int cubeY) {
            super(new char[0]);
            this.chunkPrimer = chunkPrimer;
            this.cubeYBase = Coords.cubeToMinBlock(cubeY);
        }

        @Override public IBlockState getBlockState(int x, int y, int z) { return chunkPrimer.getBlockState(x, y | cubeYBase, z); }

        @Override public void setBlockState(int x, int y, int z, IBlockState state) { chunkPrimer.setBlockState(x, y | cubeYBase, z, state); }
    }
}
