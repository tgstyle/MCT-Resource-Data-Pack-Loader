package mctmods.resourcedatapackloader.content.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomSupport;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.HeightmapPlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public final class ContentSkyDecoration {
    private static final List<GenerationStep.Decoration> STEPS = List.of(GenerationStep.Decoration.LOCAL_MODIFICATIONS, GenerationStep.Decoration.SURFACE_STRUCTURES, GenerationStep.Decoration.UNDERGROUND_ORES, GenerationStep.Decoration.VEGETAL_DECORATION);
    private static final int HERDS = GenerationStep.Decoration.values().length;
    private static final ThreadLocal<int[]> BAND = new ThreadLocal<>();

    private ContentSkyDecoration() {}

    public static int surface(LevelReader level, ChunkGenerator generator, Heightmap.Types type, int x, int z, int found) {
        ContentSkyLand.Land land = ContentSkyLand.landOf(generator);
        if (land == null) { return found; }
        int[] band = BAND.get();
        if (band == null) { return found <= land.floor() ? found : walk(level, type.isOpaque(), x, z, land.floor() - 1, level.getMinBuildHeight(), true); }
        if (found <= band[0]) { return level.getMinBuildHeight(); }
        int from = Math.min(band[1], found - 1);
        return walk(level, type.isOpaque(), x, z, from, band[0], !type.isOpaque().test(level.getBlockState(new BlockPos(x, from + 1, z))));
    }

    public static void decorate(WorldGenLevel level, ChunkGenerator generator, ChunkAccess chunk) {
        ContentSkyLand.Land land = ContentSkyLand.landOf(generator);
        if (land == null) { return; }
        ChunkPos at = chunk.getPos();
        WorldgenRandom random = new WorldgenRandom(new XoroshiroRandomSource(RandomSupport.generateUniqueSeed()));
        long seed = random.setDecorationSeed(level.getSeed(), at.getMinBlockX(), at.getMinBlockZ());
        for (int section = SectionPos.blockToSectionCoord(land.floor()); section <= SectionPos.blockToSectionCoord(land.ceiling()); section++) {
            int index = chunk.getSectionIndexFromSectionY(section);
            if (index < 0 || index >= chunk.getSectionsCount() || chunk.getSection(index).hasOnlyAir()) { continue; }
            int bottom = SectionPos.sectionToBlockCoord(section);
            BAND.set(new int[] {bottom, bottom + SectionPos.SECTION_MAX_INDEX});
            try {
                Holder<Biome> biome = level.getBiome(new BlockPos(at.getMiddleBlockX(), bottom + SectionPos.SECTION_HALF_SIZE, at.getMiddleBlockZ()));
                List<HolderSet<PlacedFeature>> features = biome.value().getGenerationSettings().features();
                BlockPos origin = new BlockPos(at.getMinBlockX(), bottom, at.getMinBlockZ());
                for (GenerationStep.Decoration step : STEPS) {
                    if (step.ordinal() >= features.size()) { continue; }
                    int feature = 0;
                    for (Holder<PlacedFeature> placed : features.get(step.ordinal())) {
                        random.setFeatureSeed(seed + section, feature++, step.ordinal());
                        place(level, generator, random, placed.value(), origin);
                    }
                }
                random.setFeatureSeed(seed + section, 0, HERDS);
                NaturalSpawner.spawnMobsForChunkGeneration(level, biome, at, random);
            }
            finally { BAND.remove(); }
        }
    }

    private static void place(WorldGenLevel level, ChunkGenerator generator, WorldgenRandom random, PlacedFeature placed, BlockPos origin) {
        if (placed.placement().stream().noneMatch(HeightmapPlacement.class::isInstance) || ContentPopulateControl.refusesPlacement(level, generator, placed)) { return; }
        PlacementContext context = new PlacementContext(level, generator, Optional.of(placed));
        Stream<BlockPos> spots = Stream.of(origin);
        for (PlacementModifier modifier : placed.placement()) { spots = spots.flatMap(spot -> modifier.getPositions(context, random, spot)); }
        ConfiguredFeature<?, ?> feature = placed.feature().value();
        int bottom = origin.getY();
        spots.filter(spot -> spot.getY() > bottom && spot.getY() <= bottom + SectionPos.SECTION_SIZE).forEach(spot -> feature.place(level, generator, random, spot));
    }

    private static int walk(LevelReader level, Predicate<BlockState> solid, int x, int z, int from, int to, boolean open) {
        ChunkAccess chunk = level.getChunk(SectionPos.blockToSectionCoord(x), SectionPos.blockToSectionCoord(z));
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        boolean clear = open;
        int y = from;
        while (y >= to) {
            int index = chunk.getSectionIndex(y);
            LevelChunkSection section = index >= 0 && index < chunk.getSectionsCount() ? chunk.getSection(index) : null;
            if (section == null || section.hasOnlyAir()) {
                y = SectionPos.sectionToBlockCoord(SectionPos.blockToSectionCoord(y)) - 1;
                clear = true;
                continue;
            }
            boolean filled = solid.test(chunk.getBlockState(at.set(x, y, z)));
            if (filled && clear) { return y + 1; }
            clear = !filled;
            y--;
        }
        return level.getMinBuildHeight();
    }
}
