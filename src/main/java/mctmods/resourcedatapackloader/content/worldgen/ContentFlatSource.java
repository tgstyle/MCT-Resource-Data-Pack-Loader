package mctmods.resourcedatapackloader.content.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.StructureTags;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.RandomSupport;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.LakeFeature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ContentFlatSource extends NoiseBasedChunkGenerator {
    public static final String ID = "flat";
    public static final Codec<ContentFlatSource> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BiomeSource.CODEC.fieldOf("biome_source").forGetter(ChunkGenerator::getBiomeSource),
            NoiseGeneratorSettings.CODEC.fieldOf("noise_settings").forGetter(NoiseBasedChunkGenerator::generatorSettings),
            FlatLevelGeneratorSettings.CODEC.fieldOf("settings").forGetter(ContentFlatSource::settings),
            Codec.BOOL.fieldOf("decorated").orElse(false).forGetter(ContentFlatSource::decorated),
            Codec.BOOL.fieldOf("lakes").orElse(false).forGetter(ContentFlatSource::waterLakes),
            Codec.BOOL.fieldOf("lava_lakes").orElse(false).forGetter(ContentFlatSource::lavaLakes)).apply(instance, instance.stable(ContentFlatSource::new)));
    private static final int LAKE_CORNER = 14;
    private final FlatLevelGeneratorSettings settings;
    private final FlatLevelSource flat;
    private final boolean decorated;
    private final boolean waterLakes;
    private final boolean lavaLakes;
    private final int surface;

    public ContentFlatSource(BiomeSource source, Holder<NoiseGeneratorSettings> noise, FlatLevelGeneratorSettings settings, boolean decorated, boolean waterLakes, boolean lavaLakes) {
        super(source, noise);
        this.settings = settings;
        this.flat = new FlatLevelSource(settings);
        this.decorated = decorated;
        this.waterLakes = waterLakes;
        this.lavaLakes = lavaLakes;
        this.surface = surface(settings);
    }

    private static int surface(FlatLevelGeneratorSettings settings) {
        List<BlockState> layers = settings.getLayers();
        int top = 0;
        for (int y = 0; y < layers.size(); y++) {
            if (!layers.get(y).isAir()) { top = y + 1; }
        }
        return top;
    }

    public FlatLevelGeneratorSettings settings() { return settings; }

    public boolean decorated() { return decorated; }

    public boolean waterLakes() { return waterLakes; }

    public boolean lavaLakes() { return lavaLakes; }

    public static boolean undecorated(ChunkGenerator generator) { return generator instanceof ContentFlatSource source && !source.decorated; }

    public static boolean flat(ChunkGenerator generator) { return generator instanceof FlatLevelSource || generator instanceof ContentFlatSource; }

    public static boolean empty(ChunkGenerator generator) {
        FlatLevelGeneratorSettings held = layered(generator);
        return held != null && held.getLayersInfo().isEmpty();
    }

    @Nullable private static FlatLevelGeneratorSettings layered(ChunkGenerator generator) {
        if (generator instanceof FlatLevelSource source) { return source.settings(); }
        return generator instanceof ContentFlatSource source ? source.settings() : null;
    }

    @Override @Nonnull public ChunkGeneratorStructureState createState(@Nonnull HolderLookup<StructureSet> lookup, @Nonnull RandomState random, long seed) {
        Stream<Holder<StructureSet>> sets = settings.structureOverrides().map(HolderSet::stream).orElseGet(() -> lookup.listElements().map(set -> set));
        return ChunkGeneratorStructureState.createForFlat(random, seed, biomeSource, sets);
    }

    @Override @Nonnull protected Codec<? extends ChunkGenerator> codec() { return CODEC; }

    @Override public void buildSurface(@Nonnull WorldGenRegion level, @Nonnull StructureManager structures, @Nonnull RandomState random, @Nonnull ChunkAccess chunk) {}

    @Override public int getSpawnHeight(@Nonnull LevelHeightAccessor level) { return flat.getSpawnHeight(level); }

    @Override @Nonnull public CompletableFuture<ChunkAccess> fillFromNoise(@Nonnull Executor executor, @Nonnull Blender blender, @Nonnull RandomState random, @Nonnull StructureManager structures, @Nonnull ChunkAccess chunk) { return flat.fillFromNoise(executor, blender, random, structures, chunk); }

    @Override public int getBaseHeight(int x, int z, @Nonnull Heightmap.Types type, @Nonnull LevelHeightAccessor level, @Nonnull RandomState random) { return flat.getBaseHeight(x, z, type, level, random); }

    @Override @Nonnull public NoiseColumn getBaseColumn(int x, int z, @Nonnull LevelHeightAccessor level, @Nonnull RandomState random) { return flat.getBaseColumn(x, z, level, random); }

    @Override public void applyCarvers(@Nonnull WorldGenRegion level, long seed, @Nonnull RandomState random, @Nonnull BiomeManager biomes, @Nonnull StructureManager structures, @Nonnull ChunkAccess chunk, @Nonnull GenerationStep.Carving step) {}

    @Override public void applyBiomeDecoration(@Nonnull WorldGenLevel level, @Nonnull ChunkAccess chunk, @Nonnull StructureManager structures) {
        if ((waterLakes || lavaLakes) && !villaged(level, chunk, structures)) { placeLakes(level, chunk.getPos()); }
        super.applyBiomeDecoration(level, chunk, structures);
    }

    private static boolean villaged(WorldGenLevel level, ChunkAccess chunk, StructureManager structures) {
        Registry<Structure> registry = level.registryAccess().registryOrThrow(Registries.STRUCTURE);
        return !structures.startsForStructure(chunk.getPos(), structure -> registry.wrapAsHolder(structure).is(StructureTags.VILLAGE)).isEmpty();
    }

    private void placeLakes(WorldGenLevel level, ChunkPos at) {
        WorldgenRandom random = new WorldgenRandom(new LegacyRandomSource(RandomSupport.generateUniqueSeed()));
        random.setDecorationSeed(level.getSeed(), at.getMinBlockX(), at.getMinBlockZ());
        int bottom = level.getMinBuildHeight();
        if (waterLakes && random.nextInt(4) == 0) { lake(level, random, Blocks.WATER, Blocks.AIR, corner(at, random.nextInt(16), bottom + random.nextInt(level.getHeight()), random.nextInt(16))); }
        if (lavaLakes && random.nextInt(8) == 0) {
            BlockPos pos = corner(at, random.nextInt(16), bottom + random.nextInt(random.nextInt(level.getHeight() - 8) + 8), random.nextInt(16));
            if (pos.getY() < bottom + surface || random.nextInt(10) == 0) { lake(level, random, Blocks.LAVA, Blocks.STONE, pos); }
        }
    }

    private static BlockPos corner(ChunkPos at, int x, int y, int z) { return at.getBlockAt(Math.min(x, LAKE_CORNER), y, Math.min(z, LAKE_CORNER)); }

    @SuppressWarnings("deprecation") private void lake(WorldGenLevel level, WorldgenRandom random, Block fluid, Block barrier, BlockPos at) {
        BlockPos pos = at;
        while (pos.getY() > level.getMinBuildHeight() + 5 && level.isEmptyBlock(pos)) { pos = pos.below(); }
        if (Feature.LAKE.place(new LakeFeature.Configuration(BlockStateProvider.simple(fluid), BlockStateProvider.simple(barrier)), level, this, random, pos)) { grassRim(level, pos.below(4)); }
    }

    private static void grassRim(WorldGenLevel level, BlockPos corner) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 4; y < 8; y++) {
                    BlockPos cell = corner.offset(x, y, z);
                    BlockPos below = cell.below();
                    BlockState state = level.getBlockState(cell);
                    boolean hollowed = state.is(Blocks.CAVE_AIR) || (y == 4 && state.is(Blocks.ICE));
                    if (hollowed && level.getBlockState(below).is(Blocks.DIRT) && skyLit(level, cell.above())) { level.setBlock(below, level.getBiome(below).is(Biomes.MUSHROOM_FIELDS) ? Blocks.MYCELIUM.defaultBlockState() : Blocks.GRASS_BLOCK.defaultBlockState(), 2); }
                }
            }
        }
    }

    private static boolean skyLit(WorldGenLevel level, BlockPos from) {
        int light = 15;
        for (BlockPos.MutableBlockPos pos = from.mutable(); light > 0 && pos.getY() < level.getMaxBuildHeight(); pos.move(Direction.UP)) { light -= level.getBlockState(pos).getLightBlock(level, pos); }
        return light > 0;
    }

    @Override public void spawnOriginalMobs(@Nonnull WorldGenRegion level) {}

    @Override public int getMinY() { return flat.getMinY(); }

    @Override public int getGenDepth() { return flat.getGenDepth(); }

    @Override public int getSeaLevel() { return flat.getSeaLevel(); }
}
