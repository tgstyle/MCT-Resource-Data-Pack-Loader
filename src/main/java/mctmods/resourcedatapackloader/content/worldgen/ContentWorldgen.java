package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.BlockWeightDef;
import mctmods.resourcedatapackloader.content.def.CaveRegionDef;
import mctmods.resourcedatapackloader.content.def.FollowDef;
import mctmods.resourcedatapackloader.content.def.WorldgenDef;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.WeightedPicks;
import mctmods.resourcedatapackloader.content.interfaces.IContentShape;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fml.common.IWorldGenerator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;

public final class ContentWorldgen implements IWorldGenerator {
    private static final Map<String, WorldgenDef> BY_NAME = new HashMap<>();
    private static final Set<String> CHAIN_WARNED = new HashSet<>();
    private static final int EDGE_REACH = 16;
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
        BY_NAME.clear();
        CHAIN_WARNED.clear();
        for (WorldgenDef def : defs) { BY_NAME.put(def.registryName.toString(), def); }
    }

    @Nullable public static WorldgenDef byName(String name) {
        WorldgenDef def = BY_NAME.get(name);
        if (def != null || name.indexOf(':') >= 0) { return def; }
        for (Map.Entry<String, WorldgenDef> entry : BY_NAME.entrySet()) { if (entry.getKey().endsWith(":" + name)) { return entry.getValue(); } }
        return null;
    }

    public static List<String> veinNames() {
        List<String> names = new ArrayList<>();
        for (Map.Entry<String, WorldgenDef> entry : BY_NAME.entrySet()) { if (entry.getValue().getShape() instanceof ContentOreVein) { names.add(entry.getKey()); } }
        Collections.sort(names);
        return names;
    }

    public static boolean allowsAt(WorldgenDef def, World world, BlockPos origin) { return dimensionAllowed(def, world.provider.getDimension()) && allows(def, world, origin, def.hasBiomeFilter()); }

    @Nullable public static IContentShape shapeFor(String name) {
        WorldgenDef def = BY_NAME.get(name);
        return def == null ? null : def.getShape();
    }

    public static int deepestMinHeight() { return deepestAsked; }

    public static int highestMaxHeight() { return Math.max(highestAsked, ContentCaveRegions.highestAsked()); }

    @Override public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator generator, IChunkProvider provider) {
        ContentRetrogen.markGenerated(world, chunkX, chunkZ);
        ContentCaveRegions.decorate(world, chunkX, chunkZ, random);
        ContentCaveRegions.placeStructures(world, chunkX, chunkZ);
        ContentStructureMaps.place(world, chunkX, chunkZ);
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
                if (figure instanceof ContentOreVein) {
                    ((ContentOreVein) figure).generateChunk(world, chunkX, chunkZ, source -> allows(def, world, source, filtered));
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
                        if (cave == null || !def.inCaveRegion(cave.key)) { continue; }
                    }
                    Biome biome = world.getBiome(pos);
                    if (filtered && biomeBlocked(def, biome)) { continue; }
                    if (!def.climateAllows(biome.getDefaultTemperature(), biome.getRainfall())) { continue; }
                    if (figure.generate(world, random, pos)) { after(def, world, random, pos, baseX + 8, baseZ + 8, baseX + 23, baseZ + 23, new ArrayList<>()); }
                }
            }
            finally { ContentOreControl.endPack(); }
        }
    }

    private static void after(WorldgenDef def, World world, Random random, BlockPos origin, int lowX, int lowZ, int highX, int highZ, List<WorldgenDef> chain) {
        ContentOreSigns.place(world, def, origin, lowX, lowZ, highX, highZ);
        if (def.then.isEmpty()) { return; }
        chain.add(def);
        int rolls = def.thenCount.pick(random);
        List<FollowDef> left = new ArrayList<>(def.then);
        for (int roll = 0; roll < rolls && !left.isEmpty(); roll++) {
            FollowDef chosen = FollowDef.pick(left, random);
            if (chosen == null) { break; }
            left.remove(chosen);
            if (WeightedPicks.EMPTY.equals(chosen.name)) { continue; }
            WorldgenDef next = BY_NAME.get(chosen.name);
            if (next == null) {
                ContentLog.LOGGER.error("Worldgen entry {} queues {}, which no pack registers as worldgen, so nothing follows it", def.registryName, chosen.name);
                continue;
            }
            if (chain.contains(next)) {
                if (CHAIN_WARNED.add(def.registryName + ">" + chosen.name)) { ContentLog.LOGGER.error("Worldgen entry {} queues {}, which already generated earlier in this chain, so the chain stops here rather than running forever", def.registryName, chosen.name); }
                continue;
            }
            IContentShape figure = next.getShape();
            if (figure == null || figure instanceof ContentBelt || figure instanceof ContentFieldShape || figure instanceof ContentOreVein) {
                ContentLog.LOGGER.error("Worldgen entry {} queues {}, whose shape cannot follow another entry, so nothing follows it", def.registryName, chosen.name);
                continue;
            }
            int spread = chosen.spreadOr(def.thenSpread >= 0 ? def.thenSpread : Math.max(0, def.shape.radius.most));
            int dx = random.nextInt(2 * spread + 1) - spread;
            int dz = random.nextInt(2 * spread + 1) - spread;
            int dy = chosen.depthOr(def.thenDepth, random);
            if (dx == 0 && dy == 0 && dz == 0) { dy = -1; }
            BlockPos pos = edgeOf(world, def, origin, dx, dy, dz, lowX, lowZ, highX, highZ);
            if (world.isOutsideBuildHeight(pos) || !world.isBlockLoaded(pos)) { continue; }
            if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("The {} at {}, {}, {} queues {} at {}, {}, {}, off its edge", def.registryName, origin.getX(), origin.getY(), origin.getZ(), chosen.name, pos.getX(), pos.getY(), pos.getZ()); }
            ContentOreControl.beginPack(next.registryName.getNamespace());
            try {
                if (figure.generate(world, random, pos)) { after(next, world, random, pos, lowX, lowZ, highX, highZ, chain); }
            }
            finally { ContentOreControl.endPack(); }
        }
        chain.remove(chain.size() - 1);
    }

    private static BlockPos edgeOf(World world, WorldgenDef def, BlockPos origin, int dx, int dy, int dz, int lowX, int lowZ, int highX, int highZ) {
        double length = Math.sqrt(dx * dx + dy * dy + dz * dz);
        double ux = dx / length;
        double uy = dy / length;
        double uz = dz / length;
        int edge = 0;
        for (int step = 1; step <= EDGE_REACH; step++) {
            BlockPos at = new BlockPos(origin.getX() + Math.round(ux * step), origin.getY() + Math.round(uy * step), origin.getZ() + Math.round(uz * step));
            if (world.isBlockLoaded(at) && placedBy(def, world.getBlockState(at))) { edge = step; }
        }
        int out = edge + 1;
        return new BlockPos(MathHelper.clamp(origin.getX() + (int) Math.round(ux * out), lowX, highX), origin.getY() + (int) Math.round(uy * out), MathHelper.clamp(origin.getZ() + (int) Math.round(uz * out), lowZ, highZ));
    }

    private static boolean placedBy(WorldgenDef def, IBlockState state) {
        ResourceLocation name = state.getBlock().getRegistryName();
        if (name == null) { return false; }
        if (name.equals(def.block)) { return true; }
        for (BlockWeightDef weighted : def.blocks) { if (name.equals(weighted.block)) { return true; } }
        return false;
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

    private static boolean dimensionAllowed(WorldgenDef def, int dimension) { return def.allowsDimension(dimension); }

    private static boolean biomeBlocked(WorldgenDef def, Biome biome) { return matchesBiome(def, biome) == def.biomesAreBlacklist; }

    private static boolean matchesBiome(WorldgenDef def, Biome biome) {
        if (def.namesBiome(biome)) { return true; }
        for (BiomeDictionary.Type type : def.getTypes()) {
            if (BiomeDictionary.hasType(biome, type)) { return true; }
        }
        return false;
    }
}
