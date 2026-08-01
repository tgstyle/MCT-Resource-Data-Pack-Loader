package mctmods.resourcedatapackloader.content.worldgen;

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

public final class ContentWorldgen implements IWorldGenerator {
    private final List<WorldgenDef> defs;
    private final Map<Integer, List<WorldgenDef>> byDimension = new HashMap<>();

    public ContentWorldgen(List<WorldgenDef> defs) { this.defs = defs; }

    @Override public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator generator, IChunkProvider provider) {
        ContentRetrogen.markGenerated(world, chunkX, chunkZ);

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
                if (figure instanceof ContentBelt) {
                    ((ContentBelt) figure).generateChunk(world, chunkX, chunkZ, source -> allows(def, world, source, filtered));
                    continue;
                }

                int tries = def.attempts.pick(random);
                for (int attempt = 0; attempt < tries; attempt++) {
                    BlockPos pos = ContentSpread.position(def, world, random, region, baseX, baseZ);
                    if (pos == null) { continue; }
                    Biome biome = world.getBiome(pos);
                    if (filtered && biomeBlocked(def, biome)) { continue; }
                    if (!def.climateAllows(biome.getDefaultTemperature(), biome.getRainfall())) { continue; }

                    figure.generate(world, random, pos);
                }
            }
            finally { ContentOreControl.endPack(); }
        }
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
