package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.CaveRegionDef;
import mctmods.resourcedatapackloader.content.def.WorldgenDef;
import mctmods.resourcedatapackloader.content.interfaces.IContentShape;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fml.common.IWorldGenerator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;

public final class ContentWorldgen implements IWorldGenerator {
    private static int deepestAsked = 0;
    private static int highestAsked = 0;
    private final List<WorldgenDef> defs;
    private final Map<Integer, List<WorldgenDef>> byDimension = new HashMap<>();

    public ContentWorldgen(List<WorldgenDef> defs) {
        this.defs = defs;
        int lowest = 0;
        int highest = 0;
        for (WorldgenDef def : defs) {
            lowest = Math.min(lowest, def.minHeight);
            highest = Math.max(highest, def.maxHeight);
        }
        deepestAsked = lowest;
        highestAsked = highest;
    }

    public static int deepestMinHeight() { return deepestAsked; }

    public static int highestMaxHeight() { return Math.max(highestAsked, ContentCaveRegions.highestAsked()); }

    @Override public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator generator, IChunkProvider provider) {
        ContentRetrogen.markGenerated(world, chunkX, chunkZ);
        ContentCaveRegions.decorate(world, chunkX, chunkZ, random);
        ContentCaveRegions.placeStructures(world, chunkX, chunkZ);
        List<WorldgenDef> active = forDimension(world.provider.getDimension());
        if (active.isEmpty()) { return; }
        generate(random, chunkX, chunkZ, world, active);
    }

    public void generate(Random random, int chunkX, int chunkZ, World world, List<WorldgenDef> subset) {
        int dimension = world.provider.getDimension();
        int baseX = chunkX * 16;
        int baseZ = chunkZ * 16;
        for (WorldgenDef def : subset) {
            IContentShape figure = def.getShape();
            if (figure == null || !dimensionAllowed(def, dimension)) { continue; }
            if (!farEnoughFromSpawn(def, world, baseX, baseZ)) { continue; }
            boolean filtered = def.hasBiomeFilter();
            Random region = def.spread.isSprawl() ? ContentSpread.regionRandom(world, chunkX, chunkZ) : random;
            ContentOreControl.beginPack(def.registryName.getNamespace());
            try {
                if (figure instanceof ContentImprint && ((ContentImprint) figure).pinnedAt() != null) {
                    int[] at = ((ContentImprint) figure).pinnedAt();
                    if (at[0] >> 4 == chunkX && at[1] >> 4 == chunkZ) {
                        BlockPos pos = world.getTopSolidOrLiquidBlock(new BlockPos(at[0], 0, at[1]));
                        figure.generate(world, random, pos);
                    }
                    continue;
                }
                if (figure instanceof ContentBelt) {
                    ((ContentBelt) figure).generateChunk(world, chunkX, chunkZ, source -> allows(def, world, source, filtered));
                    continue;
                }
                if (figure instanceof ContentFieldShape) {
                    ((ContentFieldShape) figure).generateChunk(world, chunkX, chunkZ, source -> allows(def, world, source, filtered));
                    continue;
                }
                int tries = def.attempts.pick(random);
                if (def.shape.rarity > 0) {
                    if (def.shape.perChunk) { tries = def.shape.rarity; }
                    else if (random.nextInt(def.shape.rarity) != 0) { continue; }
                }
                for (int attempt = 0; attempt < tries; attempt++) {
                    BlockPos pos = ContentSpread.position(def, world, random, region, baseX, baseZ);
                    if (pos == null) { continue; }
                    if (!def.snap.isEmpty()) {
                        boolean ceiling = "ceiling".equals(def.snap);
                        pos = snap(world, pos, ceiling);
                        if (pos == null) { continue; }
                        if (def.snapDepth > 0) {
                            pos = ceiling ? pos.up(def.snapDepth) : pos.down(def.snapDepth);
                            if (world.isOutsideBuildHeight(pos) || !world.isBlockLoaded(pos)) { continue; }
                        }
                    }
                    if (!def.caveRegions.isEmpty()) {
                        CaveRegionDef cave = ContentCaveRegions.regionAt(world, pos.getX(), pos.getY(), pos.getZ());
                        if (cave == null || !def.caveRegions.contains(cave.key)) { continue; }
                    }
                    Biome biome = world.getBiome(pos);
                    if (filtered && biomeBlocked(def, biome)) { continue; }
                    if (!def.climateAllows(biome.getDefaultTemperature(), biome.getRainfall())) { continue; }
                    figure.generate(world, random, pos);
                }
            }
            finally { ContentOreControl.endPack(); }
        }
    }

    @Nullable private static BlockPos snap(World world, BlockPos pos, boolean ceiling) {
        BlockPos at = pos;
        for (int step = 0; step < 24; step++) {
            if (!world.isBlockLoaded(at)) { return null; }
            boolean airHere = world.isAirBlock(at);
            BlockPos against = ceiling ? at.up() : at.down();
            if (airHere && !world.isAirBlock(against)) {
                return world.getBlockState(against).getMaterial().isLiquid() ? null : at;
            }
            if (ceiling) { at = airHere ? at.up() : at.down(); }
            else { at = airHere ? at.down() : at.up(); }
        }
        return null;
    }

    private static boolean allows(WorldgenDef def, World world, BlockPos source, boolean filtered) {
        Biome biome = world.getBiome(source);
        if (filtered && biomeBlocked(def, biome)) { return false; }
        return def.climateAllows(biome.getDefaultTemperature(), biome.getRainfall());
    }

    private List<WorldgenDef> forDimension(int dimension) {
        List<WorldgenDef> cached = byDimension.get(dimension);
        if (cached != null) { return cached; }
        List<WorldgenDef> active = new ArrayList<>();
        for (WorldgenDef def : defs) {
            if (dimensionAllowed(def, dimension)) { active.add(def); }
        }
        byDimension.put(dimension, active);
        return active;
    }

    private static boolean farEnoughFromSpawn(WorldgenDef def, World world, int baseX, int baseZ) {
        if (def.minDistanceFromSpawn <= 0) { return true; }
        BlockPos spawn = world.getSpawnPoint();
        double dx = (baseX + 8) - spawn.getX();
        double dz = (baseZ + 8) - spawn.getZ();
        return dx * dx + dz * dz >= (double) def.minDistanceFromSpawn * def.minDistanceFromSpawn;
    }

    private static boolean dimensionAllowed(WorldgenDef def, int dimension) {
        if (def.dimensions.isEmpty()) { return true; }
        return def.dimensions.contains(dimension) != def.dimensionsAreBlacklist;
    }

    private static boolean biomeBlocked(WorldgenDef def, Biome biome) { return matchesBiome(def, biome) == def.biomesAreBlacklist; }

    private static boolean matchesBiome(WorldgenDef def, Biome biome) {
        if (biome.getRegistryName() != null && def.getBiomeNames().contains(biome.getRegistryName().toString())) { return true; }
        for (BiomeDictionary.Type type : def.getTypes()) {
            if (BiomeDictionary.hasType(biome, type)) { return true; }
        }
        return false;
    }
}
