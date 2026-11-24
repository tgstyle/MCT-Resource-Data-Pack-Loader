package mctmods.resourcedatapackloader.common.worldgen.surface;

import mctmods.resourcedatapackloader.common.blocks.ContentStates;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.CaveTerrainDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DimensionDef;
import mctmods.resourcedatapackloader.common.worldgen.carver.ContentCaveTunnels;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.NoiseGeneratorOctaves;
import net.minecraft.world.gen.structure.MapGenMineshaft;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.terraingen.InitMapGenEvent;
import net.minecraftforge.event.terraingen.TerrainGen;

public final class ContentCaveWorld implements IChunkGenerator {
    static final int HEIGHT = 256;
    private static final int CELLS_Y = 32;
    private static final int SLIDE = 24;
    private static final double SOLIDNESS = 0.3;
    private static final Set<String> UNUSABLE = new HashSet<>();
    private final World world;
    private final Random rand;
    private final NoiseGeneratorOctaves noise;
    private final IBlockState fill;
    private final IBlockState sea;
    private final int seaLevel;
    private final boolean structures;
    private final ContentCaveTunnels tunnels;
    private MapGenMineshaft mineshaft = new MapGenMineshaft();
    private double[] buffer;

    public ContentCaveWorld(World world, DimensionDef def) {
        this.world = world;
        this.rand = new Random(world.getSeed());
        this.noise = new NoiseGeneratorOctaves(rand, 8);
        CaveTerrainDef cave = Objects.requireNonNull(def.cave);
        this.fill = fill(def.registryName, cave);
        this.sea = sea(def.registryName, cave);
        this.seaLevel = cave.seaLevel;
        this.structures = def.structures;
        this.tunnels = new ContentCaveTunnels(fill, sea, seaLevel);
        world.setSeaLevel(seaLevel);
        this.mineshaft = (MapGenMineshaft) TerrainGen.getModdedMapGen(mineshaft, InitMapGenEvent.EventType.MINESHAFT);
    }

    public static IBlockState fill(ResourceLocation name, CaveTerrainDef cave) {
        String owner = "Dimension " + name;
        IBlockState parsed = ContentStates.parse(cave.fillBlock, owner);
        if (parsed != null && parsed.isFullCube()) { return parsed; }
        ContentLog.LOGGER.errorOnce(UNUSABLE, owner + cave.fillBlock, "{} terrain fillBlock '{}' is not a full solid block, using minecraft:stone; this is said once", owner, cave.fillBlock);
        return Blocks.STONE.getDefaultState();
    }

    private static IBlockState sea(ResourceLocation name, CaveTerrainDef cave) {
        IBlockState fluid = ContentOceanFluid.fluidBlock(cave.seaFluid, "Dimension " + name + " seaFluid");
        return fluid == null ? Blocks.WATER.getDefaultState() : fluid;
    }

    @Nonnull @Override public Chunk generateChunk(int x, int z) {
        rand.setSeed(x * 341873128712L + z * 132897987541L);
        ChunkPrimer primer = new ChunkPrimer();
        shape(x, z, primer);
        bedrock(primer);
        tunnels.generate(world, x, z, primer);
        if (structures) { mineshaft.generate(world, x, z, primer); }
        Chunk chunk = new Chunk(world, primer, x, z);
        Biome[] biomes = world.getBiomeProvider().getBiomes(null, x * 16, z * 16, 16, 16);
        byte[] ids = chunk.getBiomeArray();
        for (int index = 0; index < ids.length; index++) { ids[index] = (byte) Biome.getIdForBiome(biomes[index]); }
        chunk.generateSkylightMap();
        return chunk;
    }

    private void shape(int chunkX, int chunkZ, ChunkPrimer primer) {
        buffer = noise.generateNoiseOctaves(buffer, chunkX * 4, 0, chunkZ * 4, 5, CELLS_Y + 1, 5, 6.0, 17.0, 6.0);
        for (int cellX = 0; cellX < 4; cellX++) {
            for (int cellZ = 0; cellZ < 4; cellZ++) {
                for (int cellY = 0; cellY < CELLS_Y; cellY++) { cell(primer, cellX, cellY, cellZ); }
            }
        }
    }

    private void cell(ChunkPrimer primer, int cellX, int cellY, int cellZ) {
        double[] low = corners(cellX, cellY, cellZ);
        double[] high = corners(cellX, cellY + 1, cellZ);
        for (int dy = 0; dy < 8; dy++) {
            double fy = dy / 8.0;
            for (int dx = 0; dx < 4; dx++) {
                double fx = dx / 4.0;
                for (int dz = 0; dz < 4; dz++) {
                    double fz = dz / 4.0;
                    double density = blend(low, fx, fz) * (1.0 - fy) + blend(high, fx, fz) * fy;
                    int y = cellY * 8 + dy;
                    primer.setBlockState(cellX * 4 + dx, y, cellZ * 4 + dz, density > 0.0 ? fill : y < seaLevel ? sea : Blocks.AIR.getDefaultState());
                }
            }
        }
    }

    private double[] corners(int cellX, int level, int cellZ) {
        return new double[]{density(cellX, level, cellZ), density(cellX + 1, level, cellZ), density(cellX, level, cellZ + 1), density(cellX + 1, level, cellZ + 1)};
    }

    private static double blend(double[] corners, double fx, double fz) {
        return (corners[0] * (1.0 - fx) + corners[1] * fx) * (1.0 - fz) + (corners[2] * (1.0 - fx) + corners[3] * fx) * fz;
    }

    private double density(int sampleX, int level, int sampleZ) {
        int edge = Math.min(level * 8, HEIGHT - 1 - level * 8);
        double closing = edge < SLIDE ? (SLIDE - edge) / (double) SLIDE * 3.0 : 0.0;
        return buffer[(sampleX * 5 + sampleZ) * (CELLS_Y + 1) + level] / 10.0 + SOLIDNESS + closing;
    }

    private void bedrock(ChunkPrimer primer) {
        IBlockState bedrock = Blocks.BEDROCK.getDefaultState();
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int floor = rand.nextInt(ContentBedrock.MAX_LAYERS);
                int roof = HEIGHT - 1 - rand.nextInt(ContentBedrock.MAX_LAYERS);
                for (int y = 0; y <= floor; y++) { primer.setBlockState(x, y, z, bedrock); }
                for (int y = roof; y < HEIGHT; y++) { primer.setBlockState(x, y, z, bedrock); }
            }
        }
    }

    @Override public void populate(int x, int z) {
        BlockFalling.fallInstantly = true;
        ForgeEventFactory.onChunkPopulate(true, this, world, rand, x, z, false);
        BlockPos origin = new BlockPos(x * 16, 0, z * 16);
        Biome biome = world.getBiome(origin.add(16, 0, 16));
        rand.setSeed(world.getSeed());
        long seedX = rand.nextLong() / 2L * 2L + 1L;
        long seedZ = rand.nextLong() / 2L * 2L + 1L;
        rand.setSeed(x * seedX + z * seedZ ^ world.getSeed());
        if (structures) { mineshaft.generateStructure(world, rand, new ChunkPos(x, z)); }
        biome.decorate(world, rand, origin);
        ForgeEventFactory.onChunkPopulate(false, this, world, rand, x, z, false);
        BlockFalling.fallInstantly = false;
    }

    @Override public boolean generateStructures(@Nonnull Chunk chunk, int x, int z) { return false; }

    @Nonnull @Override public List<Biome.SpawnListEntry> getPossibleCreatures(@Nonnull EnumCreatureType creatureType, @Nonnull BlockPos pos) { return world.getBiome(pos).getSpawnableList(creatureType); }

    @Nullable @Override public BlockPos getNearestStructurePos(@Nonnull World worldIn, @Nonnull String structureName, @Nonnull BlockPos position, boolean findUnexplored) {
        return structures && "Mineshaft".equals(structureName) ? mineshaft.getNearestStructurePos(worldIn, position, findUnexplored) : null;
    }

    @Override public boolean isInsideStructure(@Nonnull World worldIn, @Nonnull String structureName, @Nonnull BlockPos pos) { return structures && "Mineshaft".equals(structureName) && mineshaft.isInsideStructure(pos); }

    @SuppressWarnings("ConstantConditions") @Override public void recreateStructures(@Nonnull Chunk chunk, int x, int z) {
        if (structures) { mineshaft.generate(world, x, z, null); }
    }
}
