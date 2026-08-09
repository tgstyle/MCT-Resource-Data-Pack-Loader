package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.ContentStates;
import mctmods.resourcedatapackloader.content.def.PathIntersectDef;
import mctmods.resourcedatapackloader.content.def.WorldTemplateDef;
import mctmods.resourcedatapackloader.mixin.*;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;

import mctmods.blastplaster.util.TreeCollector;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStone;
import net.minecraft.block.BlockColored;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGeneratorOverworld;
import net.minecraft.world.gen.ChunkGeneratorSettings;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;

public final class ContentBeard {
    private static final int RADIUS = 12;
    private static final int SIZE = 24;
    private static final double SCALE = 8.0D;
    private static final float[] KERNEL = new float[SIZE * SIZE * SIZE];
    static {
        for (int z = 0; z < SIZE; z++) {
            for (int x = 0; x < SIZE; x++) {
                for (int y = 0; y < SIZE; y++) { KERNEL[z * SIZE * SIZE + x * SIZE + y] = (float) seed(x - RADIUS, (double) (y - RADIUS) + 0.5D, z - RADIUS); }
            }
        }
    }

    public enum Mode { NONE, BURY, BEARD_THIN, BEARD_BOX, ENCAPSULATE }

    private static final int BAND = 6;
    private static final Map<String, Mode> MODES = new LinkedHashMap<>();
    private static boolean modesLoaded;
    private static boolean applying;
    private static boolean layingBuilding;
    private static int peeks;
    private static final Map<World, ChunkGeneratorOverworld> SAMPLERS = new WeakHashMap<>();
    private static final Map<World, Map<Long, Integer>> TOPS = new WeakHashMap<>();
    private static WorldTemplateDef wantedFrom;
    private static boolean wantedHeld;
    private static boolean wantedKnown;
    private static final int SITE_REACH = 2;
    private static final int SITE_TOLERANCE = 10;
    private static final int ATTACH_GAP = 8;
    private static final int EMBANKMENT_CAP = 4;
    private static final int SITE_SEPARATION = 8;
    public static final long NO_SITE = Long.MIN_VALUE;
    private static final ChunkPrimer UNUSED = new ChunkPrimer();
    private static ChunkGeneratorOverworld sampler;
    private static World samplerWorld;

    private ContentBeard() {}

    public static void layingBuilding(boolean now) { layingBuilding = now; }

    public static int footingMisfit(StructureBoundingBox box, List<StructureComponent> pieces) {
        int spread = footingSpread(box);
        if (spread == Integer.MAX_VALUE) { return spread; }

        World world = samplerWorld;
        for (StructureComponent piece : pieces) {
            if (!(piece instanceof StructureVillagePieces.Path)) { continue; }

            StructureBoundingBox road = piece.getBoundingBox();
            int gapX = Math.max(road.minX - box.maxX, box.minX - road.maxX);
            int gapZ = Math.max(road.minZ - box.maxZ, box.minZ - road.maxZ);
            if (Math.max(gapX, gapZ) > 2 || Math.min(gapX, gapZ) > 0) { continue; }

            int ground = surfaceAt(world, (box.minX + box.maxX) / 2, (box.minZ + box.maxZ) / 2);
            if (ground < 0) { return Integer.MAX_VALUE; }

            boolean roadAlongX = road.maxX - road.minX >= road.maxZ - road.minZ;
            int roadX = roadAlongX ? Math.max(road.minX, Math.min(road.maxX, (box.minX + box.maxX) / 2)) : (road.minX + road.maxX) / 2;
            int roadZ = roadAlongX ? (road.minZ + road.maxZ) / 2 : Math.max(road.minZ, Math.min(road.maxZ, (box.minZ + box.maxZ) / 2));
            int roadGround = surfaceAt(world, roadX, roadZ);
            if (roadGround < 0) { return Integer.MAX_VALUE; }

            return Math.max(spread, Math.abs(ground - roadGround));
        }
        return Integer.MAX_VALUE;
    }

    public static int footingSpread(StructureBoundingBox box) {
        World world = samplerWorld;
        if (world == null) { return 0; }

        int lowest = Integer.MAX_VALUE;
        int highest = Integer.MIN_VALUE;
        for (int x = box.minX; x <= box.maxX + 3; x += 4) {
            for (int z = box.minZ; z <= box.maxZ + 3; z += 4) {
                int sampled = surfaceAt(world, Math.min(x, box.maxX), Math.min(z, box.maxZ));
                if (sampled < 0) { return Integer.MAX_VALUE; }
                if (sampled < lowest) { lowest = sampled; }
                if (sampled > highest) { highest = sampled; }
            }
        }
        return highest - lowest;
    }

    public static int lowestIn(World worldIn, int minX, int minZ, int maxX, int maxZ, StructureBoundingBox clip) {
        int floor = worldIn.provider.getAverageGroundLevel() - 1;
        if (samplerFor(worldIn) != null) {
            int lowest = Integer.MAX_VALUE;
            for (int z = minZ; z <= maxZ; z++) {
                for (int x = minX; x <= maxX; x++) {
                    int sampled = surfaceAt(worldIn, x, z);
                    if (sampled < 0) { continue; }

                    lowest = Math.min(lowest, Math.max(sampled, floor));
                }
            }
            if (lowest != Integer.MAX_VALUE) { return lowest; }
        }
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        int lowest = Integer.MAX_VALUE;
        for (int z = minZ; z <= maxZ; z++) {
            for (int x = minX; x <= maxX; x++) {
                at.setPos(x, 64, z);
                if (!clip.isVecInside(at)) { continue; }

                lowest = Math.min(lowest, Math.max(worldIn.getTopSolidOrLiquidBlock(at).getY(), floor));
            }
        }
        return lowest;
    }

    public static boolean spacedLayout() {
        if (!layingBuilding || !wanted()) { return false; }
        if (peeks < 30 && ContentLog.LOGGER.debugEnabled()) {
            peeks++;
            StackTraceElement[] frames = new Throwable().getStackTrace();
            String caller = frames.length > 3 ? frames[3].getClassName() + "." + frames[3].getMethodName() : "unknown";
            ContentLog.LOGGER.debug("The spaced layout test ran for {}", caller);
        }
        return true;
    }

    public static int surfaceAt(World world, int blockX, int blockZ) {
        ChunkGeneratorOverworld sampled = samplerFor(world);
        if (sampled == null) { return -1; }

        Map<Long, Integer> tops = TOPS.computeIfAbsent(world, held -> new HashMap<>());
        long column = ((long) blockX << 32) | (blockZ & 0xFFFFFFFFL);
        Integer known = tops.get(column);
        if (known != null) { return known; }

        int made = surface(sampled, world, blockX, blockZ);
        tops.put(column, made);
        return made;
    }

    private static int surfaceAt(World world, ChunkGeneratorOverworld sampled, Biome[] region, int originX, int originZ, int size, int blockX, int blockZ) {
        Map<Long, Integer> tops = TOPS.computeIfAbsent(world, held -> new HashMap<>());
        long column = ((long) blockX << 32) | (blockZ & 0xFFFFFFFFL);
        Integer known = tops.get(column);
        if (known != null) { return known; }

        Biome[] window = new Biome[25];
        int nx = blockX >> 2;
        int nz = blockZ >> 2;
        for (int dz = 0; dz < 5; dz++) { System.arraycopy(region, nx - 2 - originX + (nz - 2 + dz - originZ) * size, window, dz * 5, 5); }
        int made = surface(sampled, window, blockX, blockZ);
        tops.put(column, made);
        return made;
    }

    public static boolean roughGround(World world, int blockX, int blockZ, int halfWidth, int tolerance) {
        int middle = surfaceAt(world, blockX, blockZ);
        if (middle < 0) { return false; }

        int lowest = middle;
        int highest = middle;
        for (int corner = 0; corner < 4; corner++) {
            int sampled = surfaceAt(world, blockX + ((corner & 1) == 0 ? -halfWidth : halfWidth), blockZ + ((corner & 2) == 0 ? -halfWidth : halfWidth));
            if (sampled < 0) { return false; }

            lowest = Math.min(lowest, sampled);
            highest = Math.max(highest, sampled);
        }
        return highest - lowest > tolerance;
    }

    public static Boolean flatSite(World world, int chunkX, int chunkZ, int spacing) {
        if (samplerFor(world) == null) { return null; }

        ContentSites known = ContentSites.of(world, spacing);
        long chosen = siteFor(world, known, Math.floorDiv(chunkX, spacing), Math.floorDiv(chunkZ, spacing), spacing);
        return chosen != NO_SITE && chosen == packedChunk(chunkX, chunkZ);
    }

    public static boolean adapts(World world) { return samplerFor(world) != null; }

    public static int villageSpacing(World world) {
        if (!(world.getChunkProvider() instanceof ChunkProviderServer)) { return 32; }

        IChunkGenerator maker = ((ChunkProviderServer) world.getChunkProvider()).chunkGenerator;
        if (!(maker instanceof ChunkGeneratorOverworld)) { return 32; }

        return ((AccessorMapGenVillage) ((AccessorChunkGeneratorBeardFields) maker).rdpl$villages()).rdpl$distance();
    }

    public static BlockPos nearestSite(World world, BlockPos from, int spacing, boolean findUnexplored, long budgetNanos) {
        List<long[]> pinned = ContentStructurePlacement.pins(ContentStructurePlacement.VILLAGES);
        if (pinned != null) {
            BlockPos best = null;
            long bestAway = Long.MAX_VALUE;
            for (long[] pin : pinned) {
                int chunkX = (int) pin[0] >> 4;
                int chunkZ = (int) pin[1] >> 4;
                if (findUnexplored && world.isChunkGeneratedAt(chunkX, chunkZ)) { continue; }

                long awayX = pin[0] - from.getX();
                long awayZ = pin[1] - from.getZ();
                long away = awayX * awayX + awayZ * awayZ;
                if (away >= bestAway) { continue; }

                bestAway = away;
                best = new BlockPos((int) pin[0], 64, (int) pin[1]);
            }
            return best;
        }

        ContentSites known = ContentSites.of(world, spacing);
        int cellX = Math.floorDiv(from.getX() >> 4, spacing);
        int cellZ = Math.floorDiv(from.getZ() >> 4, spacing);
        long ending = System.nanoTime() + budgetNanos;
        BlockPos best = null;
        long bestAway = Long.MAX_VALUE;
        int stopAt = 100;
        for (int ring = 0; ring <= stopAt; ring++) {
            for (int dx = -ring; dx <= ring; dx++) {
                for (int dz = -ring; dz <= ring; dz++) {
                    if (Math.max(Math.abs(dx), Math.abs(dz)) != ring) { continue; }
                    if (known.get(packedChunk(cellX + dx, cellZ + dz)) == null && System.nanoTime() >= ending) { return best; }

                    long chosen = siteFor(world, known, cellX + dx, cellZ + dz, spacing);
                    if (chosen == NO_SITE) { continue; }

                    int chunkX = (int) (chosen >> 32);
                    int chunkZ = (int) chosen;
                    if (findUnexplored && world.isChunkGeneratedAt(chunkX, chunkZ)) { continue; }
                    if (!ContentStructurePlacement.allows(ContentStructurePlacement.VILLAGES, world, chunkX, chunkZ) || mansionCandidateNear(world, chunkX, chunkZ)) { continue; }

                    long awayX = (chunkX * 16L + 8) - from.getX();
                    long awayZ = (chunkZ * 16L + 8) - from.getZ();
                    long away = awayX * awayX + awayZ * awayZ;
                    if (away >= bestAway) { continue; }

                    bestAway = away;
                    best = new BlockPos(chunkX * 16 + 8, 64, chunkZ * 16 + 8);
                    stopAt = Math.min(stopAt, ring + 1);
                }
            }
        }
        return best;
    }

    public static long siteIn(World world, ContentSites known, int cellX, int cellZ, int spacing) { return siteFor(world, known, cellX, cellZ, spacing); }

    private static long siteFor(World world, ContentSites known, int cellX, int cellZ, int spacing) {
        long cell = packedChunk(cellX, cellZ);
        Long held = known.get(cell);
        if (held != null) { return held; }
        if (world.getMinecraftServer() != null) { ((AccessorMinecraftServerMessage) world.getMinecraftServer()).rdpl$setUserMessage("menu.generatingTerrain"); }

        long chosen = chooseSite(world, cellX, cellZ, spacing);
        known.put(cell, chosen);
        if (chosen == NO_SITE) { ContentLog.LOGGER.debug("Village cell {}, {} has no chunk both flat within {} block(s) and {} chunk(s) clear of its neighbours, so nothing is founded there", cellX, cellZ, SITE_TOLERANCE, SITE_SEPARATION); }
        else { ContentLog.LOGGER.debug("Village cell {}, {} founds on chunk {}, {}, the flattest ground it has", cellX, cellZ, (int) (chosen >> 32), (int) chosen); }
        return chosen;
    }

    private static long chooseSite(World world, int cellX, int cellZ, int spacing) {
        ChunkGeneratorOverworld sampled = samplerFor(world);
        if (sampled == null) { return NO_SITE; }

        int margin = SITE_SEPARATION / 2;
        int baseX = cellX * spacing;
        int baseZ = cellZ * spacing;
        int span = spacing + SITE_REACH * 2;
        int size = span * 4 + 1;
        int originX = (baseX - SITE_REACH) * 4;
        int originZ = (baseZ - SITE_REACH) * 4;
        Biome[] region = world.getBiomeProvider().getBiomesForGeneration(new Biome[size * size], originX, originZ, size, size);
        int lattice = span * 2;
        int[] heights = new int[lattice * lattice];
        Arrays.fill(heights, Integer.MIN_VALUE);
        long chosen = NO_SITE;
        int bestSpread = Integer.MAX_VALUE;
        int bestPull = Integer.MAX_VALUE;
        for (int x = margin; x < spacing - margin; x++) {
            for (int z = margin; z < spacing - margin; z++) {
                if (!MapGenVillage.VILLAGE_SPAWN_BIOMES.contains(region[(x + SITE_REACH) * 4 + 2 + ((z + SITE_REACH) * 4 + 2) * size])) { continue; }

                int lowest = Integer.MAX_VALUE;
                int highest = Integer.MIN_VALUE;
                for (int dx = 0; dx <= SITE_REACH * 4 + 1; dx++) {
                    for (int dz = 0; dz <= SITE_REACH * 4 + 1; dz++) {
                        int at = (x * 2 + dx) * lattice + z * 2 + dz;
                        int sampledHeight = heights[at];
                        if (sampledHeight == Integer.MIN_VALUE) {
                            sampledHeight = surfaceAt(world, sampled, region, originX, originZ, size, (baseX + x - SITE_REACH) * 16 + 4 + dx * 8, (baseZ + z - SITE_REACH) * 16 + 4 + dz * 8);
                            heights[at] = sampledHeight;
                        }
                        if (sampledHeight < 0) {
                            lowest = Integer.MAX_VALUE;
                            highest = Integer.MIN_VALUE;
                            dx = SITE_REACH * 4 + 2;
                            break;
                        }
                        if (sampledHeight < lowest) { lowest = sampledHeight; }
                        if (sampledHeight > highest) { highest = sampledHeight; }
                    }
                }
                if (lowest == Integer.MAX_VALUE) { continue; }

                int spread = highest - lowest;
                if (spread > SITE_TOLERANCE || spread > bestSpread) { continue; }

                int pull = Math.abs(x * 2 - spacing) + Math.abs(z * 2 - spacing);
                if (spread == bestSpread && pull >= bestPull) { continue; }

                bestSpread = spread;
                bestPull = pull;
                chosen = packedChunk(baseX + x, baseZ + z);
            }
        }
        return chosen;
    }

    private static long packedChunk(int chunkX, int chunkZ) { return ((long) chunkX << 32) | (chunkZ & 0xFFFFFFFFL); }

    public static boolean mansionCandidateNear(World world, int chunkX, int chunkZ) {
        ChunkGeneratorOverworld sampled = samplerFor(world);
        if (sampled == null) { return false; }

        MapGenStructure mansions = ((AccessorChunkGeneratorBeardFields) sampled).rdpl$mansions();
        ((AccessorMapGenBase) mansions).rdpl$setWorld(world);
        AccessorMapGenStructureSpawn asker = (AccessorMapGenStructureSpawn) mansions;
        for (int x = chunkX - 6; x <= chunkX + 2; x++) {
            for (int z = chunkZ - 6; z <= chunkZ + 2; z++) {
                if (asker.rdpl$canSpawnStructureAtCoords(x, z)) { return true; }
            }
        }
        return false;
    }

    @SubscribeEvent public static void onDressed(PopulateChunkEvent.Post event) {
        if (event.getWorld().isRemote || !wanted()) { return; }
        if (!(event.getWorld().getChunkProvider() instanceof ChunkProviderServer)) { return; }

        IChunkGenerator maker = ((ChunkProviderServer) event.getWorld().getChunkProvider()).chunkGenerator;
        if (!(maker instanceof ChunkGeneratorOverworld)) { return; }

        MapGenVillage villages = ((AccessorChunkGeneratorBeardFields) maker).rdpl$villages();
        int blockX = (event.getChunkX() << 4) + 8;
        int blockZ = (event.getChunkZ() << 4) + 8;
        StructureBoundingBox clip = new StructureBoundingBox(blockX, 0, blockZ, blockX + 15, 255, blockZ + 15);
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        for (StructureStart start : ((AccessorMapGenStructure) villages).rdpl$getStructureMap().values()) {
            if (start == null || !start.isSizeableStructure() || !start.getBoundingBox().intersectsWith(clip)) { continue; }
            for (StructureComponent piece : start.getComponents()) {
                if (!(piece instanceof StructureVillagePieces.Village)) { continue; }

                StructureBoundingBox box = piece.getBoundingBox();
                if (box.minX - 2 > clip.maxX || box.maxX + 2 < clip.minX || box.minZ - 2 > clip.maxZ || box.maxZ + 2 < clip.minZ) { continue; }

                int felled = fellAround(event.getWorld(), start, piece, box, clip, at, false);
                if (felled > 0) { ContentLog.LOGGER.debug("Felled {} tree block(s) crowding {} at {}, {}", felled, piece.getClass().getSimpleName(), box.minX, box.minZ); }
                if (piece instanceof StructureVillagePieces.Path) {
                    int[] ring = openOver(start, piece, event.getWorld(), box, clip, at);
                    if (ring[0] > 0) { ContentLog.LOGGER.debug("Opened {} block(s) over the roadway of Path at {}, {}", ring[0], box.minX, box.minZ); }
                    lampPosts(start, piece, event.getWorld(), box, clip, at);
                }
            }
        }
    }

    public static void fellFor(StructureStart start, StructureComponent piece, World world, StructureBoundingBox clip) {
        if (!(piece instanceof StructureVillagePieces.Village)) { return; }

        StructureBoundingBox box = piece.getBoundingBox();
        if (box.minX - 2 > clip.maxX || box.maxX + 2 < clip.minX || box.minZ - 2 > clip.maxZ || box.maxZ + 2 < clip.minZ) { return; }

        int felled = fellAround(world, start, piece, box, clip, new BlockPos.MutableBlockPos(), true);
        if (felled > 0) { ContentLog.LOGGER.debug("Felled {} tree block(s) before {} at {}, {} was built", felled, piece.getClass().getSimpleName(), box.minX, box.minZ); }
    }

    private static int fellAround(World world, StructureStart start, StructureComponent piece, StructureBoundingBox box, StructureBoundingBox clip, BlockPos.MutableBlockPos at, boolean bare) {
        List<BlockPos> seeds = new ArrayList<>();
        List<BlockPos> canopy = new ArrayList<>();
        int felled = 0;
        int top = piece instanceof StructureVillagePieces.Well ? box.maxY + 1 : box.maxY;
        for (int x = box.minX - 2; x <= box.maxX + 2; x++) {
            for (int z = box.minZ - 2; z <= box.maxZ + 2; z++) {
                for (int y = box.minY + 1; y <= box.maxY + 16; y++) {
                    if (!bare && x >= box.minX && x <= box.maxX && z >= box.minZ && z <= box.maxZ && y <= top) { continue; }

                    at.setPos(x, y, z);
                    if (!clip.isVecInside(at) || insideAnother(start, piece, at)) { continue; }

                    IBlockState held = world.getBlockState(at);
                    if (mctmods.blastplaster.Config.isLog(held)) { seeds.add(at.toImmutable()); }
                    else if (held.getMaterial() == Material.LEAVES) { canopy.add(at.toImmutable()); }
                    else if (held.getMaterial() == Material.VINE) { felled += clearAt(world, at); }
                }
            }
        }
        boolean inOwn = !bare;
        Predicate<BlockPos> within = spot -> world.isChunkGeneratedAt(spot.getX() >> 4, spot.getZ() >> 4)
                && !insideAnother(start, piece, spot)
                && !(inOwn && spot.getX() >= box.minX && spot.getX() <= box.maxX && spot.getZ() >= box.minZ && spot.getZ() <= box.maxZ && spot.getY() <= top);
        Set<BlockPos> felledLogs = new HashSet<>();
        for (BlockPos seed : seeds) {
            if (felledLogs.contains(seed)) { continue; }

            TreeCollector.Tree tree = TreeCollector.collect(world, seed, mctmods.blastplaster.Config.view(world).getMaxTreeSize(), within);
            for (BlockPos log : tree.logs) {
                felledLogs.add(log);
                at.setPos(log.getX(), log.getY(), log.getZ());
                felled += clearAt(world, at);
            }
            for (BlockPos leaf : tree.leaves) {
                at.setPos(leaf.getX(), leaf.getY(), leaf.getZ());
                felled += clearAt(world, at);
            }
            for (BlockPos log : tree.logs) {
                for (int x = log.getX() - 5; x <= log.getX() + 5; x++) {
                    for (int z = log.getZ() - 5; z <= log.getZ() + 5; z++) {
                        for (int y = log.getY() - 5; y <= log.getY() + 5; y++) {
                            at.setPos(x, y, z);
                            if (!within.test(at)) { continue; }

                            IBlockState held = world.getBlockState(at);
                            if (held.getMaterial() != Material.LEAVES) { continue; }
                            if (held.getPropertyKeys().contains(BlockLeaves.DECAYABLE) && !held.getValue(BlockLeaves.DECAYABLE)) { continue; }
                            if (sustained(world, at.toImmutable(), within)) { continue; }

                            felled += clearAt(world, at);
                        }
                    }
                }
            }
        }
        for (BlockPos leaf : canopy) {
            IBlockState held = world.getBlockState(leaf);
            if (held.getMaterial() != Material.LEAVES) { continue; }
            if (held.getPropertyKeys().contains(BlockLeaves.DECAYABLE) && !held.getValue(BlockLeaves.DECAYABLE)) { continue; }
            if (sustained(world, leaf, within)) { continue; }

            at.setPos(leaf.getX(), leaf.getY(), leaf.getZ());
            felled += clearAt(world, at);
        }
        return felled;
    }

    private static boolean sustained(World world, BlockPos leaf, Predicate<BlockPos> within) { return sustainer(world, leaf, within) != null; }

    private static BlockPos sustainer(World world, BlockPos leaf, Predicate<BlockPos> within) {
        BlockPos.MutableBlockPos near = new BlockPos.MutableBlockPos();
        for (int dx = -4; dx <= 4; dx++) {
            for (int dy = -4; dy <= 4; dy++) {
                for (int dz = -4; dz <= 4; dz++) {
                    if (Math.abs(dx) + Math.abs(dy) + Math.abs(dz) > 4) { continue; }

                    near.setPos(leaf.getX() + dx, leaf.getY() + dy, leaf.getZ() + dz);
                    if (!within.test(near)) { continue; }
                    if (mctmods.blastplaster.Config.isLog(world.getBlockState(near))) { return near.toImmutable(); }
                }
            }
        }
        return null;
    }

    private static ChunkGeneratorOverworld samplerFor(World world) {
        ChunkGeneratorOverworld sampled = SAMPLERS.get(world);
        if (sampled != null) { return sampled; }

        IChunkGenerator made = world.provider.createChunkGenerator();
        if (!(made instanceof ChunkGeneratorOverworld)) { return null; }

        sampled = (ChunkGeneratorOverworld) made;
        SAMPLERS.put(world, sampled);
        return sampled;
    }

    private static double wander(double raw) {
        double wander = raw / 8000.0;
        if (wander < 0.0) { wander = -wander * 0.3; }

        wander = wander * 3.0 - 2.0;
        if (wander < 0.0) {
            wander /= 2.0;
            if (wander < -1.0) { wander = -1.0; }

            return wander / 1.4 / 2.0;
        }
        if (wander > 1.0) { wander = 1.0; }

        return wander / 8.0;
    }

    private static int surface(ChunkGeneratorOverworld generator, World world, int blockX, int blockZ) {
        Biome[] biomes = world.getBiomeProvider().getBiomesForGeneration(new Biome[25], (blockX >> 2) - 2, (blockZ >> 2) - 2, 5, 5);
        return surface(generator, biomes, blockX, blockZ);
    }

    private static int surface(ChunkGeneratorOverworld generator, Biome[] biomes, int blockX, int blockZ) {
        AccessorChunkGeneratorOverworld inside = (AccessorChunkGeneratorOverworld) generator;
        ChunkGeneratorSettings settings = inside.rdpl$settings();
        int nx = blockX >> 2;
        int nz = blockZ >> 2;
        double[] depth = inside.rdpl$depthNoise().generateNoiseOctaves(new double[1], nx, nz, 1, 1, settings.depthNoiseScaleX, settings.depthNoiseScaleZ, settings.depthNoiseScaleExponent);
        float coordinate = settings.coordinateScale;
        float height = settings.heightScale;
        double[] main = inside.rdpl$mainNoise().generateNoiseOctaves(new double[33], nx, 0, nz, 1, 33, 1, coordinate / settings.mainNoiseScaleX, height / settings.mainNoiseScaleY, coordinate / settings.mainNoiseScaleZ);
        double[] lower = inside.rdpl$minLimit().generateNoiseOctaves(new double[33], nx, 0, nz, 1, 33, 1, coordinate, height, coordinate);
        double[] upper = inside.rdpl$maxLimit().generateNoiseOctaves(new double[33], nx, 0, nz, 1, 33, 1, coordinate, height, coordinate);
        float[] weights = inside.rdpl$biomeWeights();
        Biome middle = biomes[12];
        float variation = 0.0F;
        float base = 0.0F;
        float weight = 0.0F;
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                Biome beside = biomes[dx + 2 + (dz + 2) * 5];
                float depthHere = settings.biomeDepthOffSet + beside.getBaseHeight() * settings.biomeDepthWeight;
                float scaleHere = settings.biomeScaleOffset + beside.getHeightVariation() * settings.biomeScaleWeight;
                if (inside.rdpl$terrainType() == WorldType.AMPLIFIED && depthHere > 0.0F) {
                    depthHere = 1.0F + depthHere * 2.0F;
                    scaleHere = 1.0F + scaleHere * 4.0F;
                }
                float share = weights[dx + 2 + (dz + 2) * 5] / (depthHere + 2.0F);
                if (beside.getBaseHeight() > middle.getBaseHeight()) { share /= 2.0F; }

                variation += scaleHere * share;
                base += depthHere * share;
                weight += share;
            }
        }
        variation /= weight;
        base /= weight;
        variation = variation * 0.9F + 0.1F;
        base = (base * 4.0F - 1.0F) / 8.0F;
        double offset = base + wander(depth[0]) * 0.2;
        offset = offset * settings.baseSize / 8.0;
        double middleHeight = settings.baseSize + offset * 4.0;
        double over = 0.0;
        for (int cell = 32; cell >= 0; cell--) {
            double falloff = (cell - middleHeight) * settings.stretchY * 128.0 / 256.0 / variation;
            if (falloff < 0.0) { falloff *= 4.0; }

            double least = lower[cell] / settings.lowerLimitScale;
            double most = upper[cell] / settings.upperLimitScale;
            double mix = (main[cell] / 10.0 + 1.0) / 2.0;
            double density = MathHelper.clampedLerp(least, most, mix) - falloff;
            if (cell > 29) {
                double taper = (cell - 29) / 3.0F;
                density = density * (1.0 - taper) + -10.0 * taper;
            }
            if (density > 0.0) { return MathHelper.clamp((int) (cell * 8 + density / (density - over) * 8.0), 1, 254); }

            over = density;
        }
        return -1;
    }
    public static void foundAtBirth(StructureStart start) {
        ChunkGeneratorOverworld generator = sampler;
        World world = samplerWorld;
        if (generator == null || world == null || start.getComponents().isEmpty()) { return; }

        StructureBoundingBox well = start.getComponents().get(0).getBoundingBox();
        int nominal = well.maxY - 3;
        int[] sampled = new int[(well.maxX - well.minX + 1) * (well.maxZ - well.minZ + 1)];
        int count = 0;
        for (int z = well.minZ; z <= well.maxZ; z++) {
            for (int x = well.minX; x <= well.maxX; x++) {
                int found = surfaceAt(world, x, z);
                if (found >= 0) { sampled[count++] = found; }
            }
        }
        if (count == 0) { return; }

        Arrays.sort(sampled, 0, count);
        int level = sampled[0];
        if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("The well footprint at {}, {} samples y {}..{} across {} column(s), founding on the lowest y {}", well.minX, well.minZ, sampled[0], sampled[count - 1], count, level); }

        int shift = level - nominal;
        int roads = 0;
        for (StructureComponent piece : start.getComponents()) {
            piece.getBoundingBox().offset(0, shift, 0);
            if (piece instanceof StructureVillagePieces.Path) { roads++; }
        }
        start.getBoundingBox().offset(0, shift, 0);
        ContentLog.LOGGER.debug("A village born at {}, {} is founded at y {}, shifted {} from its nominal ground at y {}, laid with {} piece(s), {} of them roads", (well.minX + well.maxX) / 2, (well.minZ + well.maxZ) / 2, level, shift, nominal, start.getComponents().size(), roads);
    }

    private static void lampPosts(StructureStart start, StructureComponent piece, World world, StructureBoundingBox box, StructureBoundingBox clip, BlockPos.MutableBlockPos at) {
        boolean alongX = box.maxX - box.minX >= box.maxZ - box.minZ;
        int from = alongX ? box.minX : box.minZ;
        int to = alongX ? box.maxX : box.maxZ;
        if (to - from < 4) { return; }

        Random rand = new Random(world.getSeed() ^ ((long) box.minX << 32) ^ box.minZ);
        List<Integer> along = new ArrayList<>();
        along.add(from);
        for (int step = from + 7 + rand.nextInt(6); step < to - 1; step += 7 + rand.nextInt(6)) { along.add(step); }
        along.add(to);
        for (StructureComponent other : start.getComponents()) {
            if (other == piece || !(other instanceof StructureVillagePieces.Path)) { continue; }

            StructureBoundingBox met = other.getBoundingBox();
            if (!box.intersectsWith(met)) { continue; }

            int meeting = alongX ? Math.max(box.minX, met.minX) : Math.max(box.minZ, met.minZ);
            if (meeting >= from && meeting <= to && !along.contains(meeting)) { along.add(meeting); }
        }
        int reach = (pathFullWidth() - 3) / 2;
        int off = pathSidewalkWidth() > 0 ? reach : reach + 1;
        int raised = 0;
        for (int spot : along) {
            for (int side = 0; side < 2; side++) {
                int x = alongX ? spot : (side == 0 ? box.minX - off : box.maxX + off);
                int z = alongX ? (side == 0 ? box.minZ - off : box.maxZ + off) : spot;
                if (inPlaza(start, x, z) || onPaving(start, piece, x, z)) { continue; }
                if (raise(start, piece, world, clip, at, x, z, box)) {
                    raised++;
                    break;
                }
            }
        }
        if (raised > 0) { ContentLog.LOGGER.debug("Raised {} lamp post(s) along the road at {}, {}", raised, box.minX, box.minZ); }
    }

    private static boolean beforeADoor(World world, StructureBoundingBox clip, BlockPos.MutableBlockPos at, int x, int bed, int z) {
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                for (int y = bed; y <= bed + 3; y++) {
                    at.setPos(x + dx, y, z + dz);
                    if (!clip.isVecInside(at)) { continue; }
                    if (world.getBlockState(at).getBlock() instanceof BlockDoor) { return true; }
                }
            }
        }
        return false;
    }

    private static boolean inPlaza(StructureStart start, int x, int z) {
        if (start.getComponents().isEmpty()) { return false; }

        StructureBoundingBox well = start.getComponents().get(0).getBoundingBox();
        int reach = plazaReach();
        return x >= well.minX - reach && x <= well.maxX + reach && z >= well.minZ - reach && z <= well.maxZ + reach;
    }

    private static boolean onPaving(StructureStart start, StructureComponent piece, int x, int z) {
        int reach = (pathFullWidth() - 3) / 2;
        for (StructureComponent other : start.getComponents()) {
            if (!(other instanceof StructureVillagePieces.Path)) { continue; }

            StructureBoundingBox held = ((AccessorStructureComponentBox) other).rdpl$box();
            if (held == null) { continue; }
            if (other == piece) {
                if (x >= held.minX && x <= held.maxX && z >= held.minZ && z <= held.maxZ) { return true; }
                continue;
            }
            if (x >= held.minX - reach && x <= held.maxX + reach && z >= held.minZ - reach && z <= held.maxZ + reach) { return true; }
        }
        return false;
    }

    private static boolean raise(StructureStart start, StructureComponent piece, World world, StructureBoundingBox clip, BlockPos.MutableBlockPos at, int x, int z, StructureBoundingBox box) {
        at.setPos(x, box.minY, z);
        if (!clip.isVecInside(at) || insideAnother(start, piece, at)) { return false; }

        int bed = Integer.MIN_VALUE;
        for (int y = box.minY + 6; y >= box.minY - 3; y--) {
            at.setPos(x, y, z);
            if (!clip.isVecInside(at)) { continue; }

            Material material = world.getBlockState(at).getMaterial();
            if (material.isLiquid()) { return false; }
            if (!material.isSolid()) { continue; }

            bed = y;
            break;
        }
        if (bed == Integer.MIN_VALUE) { return false; }

        for (int y = bed + 1; y <= bed + 4; y++) {
            at.setPos(x, y, z);
            if (!clip.isVecInside(at) || insideAnother(start, piece, at)) { return false; }
            if (!world.getBlockState(at).getMaterial().isReplaceable() && world.getBlockState(at).getMaterial() != Material.AIR) { return false; }
        }
        if (beforeADoor(world, clip, at, x, bed, z)) { return false; }
        for (int y = bed + 1; y <= bed + 3; y++) {
            at.setPos(x, y, z);
            world.setBlockState(at, Blocks.OAK_FENCE.getDefaultState(), 2);
        }
        at.setPos(x, bed + 4, z);
        world.setBlockState(at, Blocks.WOOL.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.BLACK), 2);
        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
            at.setPos(x + facing.getXOffset(), bed + 4, z + facing.getZOffset());
            if (!clip.isVecInside(at) || insideAnother(start, piece, at)) { continue; }
            if (world.getBlockState(at).getMaterial() != Material.AIR) { continue; }

            world.setBlockState(at, Blocks.TORCH.getDefaultState().withProperty(BlockTorch.FACING, facing), 2);
        }
        return true;
    }

    private static int[] openOver(StructureStart start, StructureComponent piece, World world, StructureBoundingBox box, StructureBoundingBox clip, BlockPos.MutableBlockPos at) {
        int opened = 0;
        int spared = 0;
        int notGround = 0;
        int hangingOver = 0;
        List<BlockPos> overhangs = new ArrayList<>();
        boolean roadway = piece instanceof StructureVillagePieces.Path;
        for (int x = box.minX - 2; x <= box.maxX + 2; x++) {
            for (int z = box.minZ - 2; z <= box.maxZ + 2; z++) {
                if (!roadway && x >= box.minX && x <= box.maxX && z >= box.minZ && z <= box.maxZ) { continue; }
                if (underRoad(start, piece, x, z)) { continue; }

                int bed = roadTop(world, start, at, x, z, box.minY + 1, box.minY + 12);
                for (int y = bed == Integer.MIN_VALUE ? box.minY + 1 : bed + 1; y <= box.minY + 12; y++) {
                    at.setPos(x, y, z);
                    if (!clip.isVecInside(at)) { continue; }
                    if (insideAnother(start, piece, at)) {
                        spared++;
                        continue;
                    }

                    IBlockState held = world.getBlockState(at);
                    Material material = held.getMaterial();
                    if (!roadway && opening(material)) { opened += clearAt(world, at); }
                    else if (overhang(held)) { overhangs.add(at.toImmutable()); }
                    else if (material != Material.AIR) { notGround++; }
                }
                at.setPos(x, box.minY + 13, z);
                if (clip.isVecInside(at) && world.getBlockState(at).getMaterial().isSolid() && !insideAnother(start, piece, at)) { hangingOver++; }
            }
        }
        if (roadway) {
            for (int x = box.minX - 2; x <= box.maxX + 2; x++) {
                for (int z = box.minZ - 2; z <= box.maxZ + 2; z++) {
                    for (int y = box.minY - 2; y <= box.minY + 12; y++) {
                        at.setPos(x, y, z);
                        if (!clip.isVecInside(at) || insideAnother(start, piece, at)) { continue; }
                        if (!(world.getBlockState(at).getBlock() instanceof BlockStairs)) { continue; }

                        int embedded = 0;
                        for (EnumFacing side : EnumFacing.HORIZONTALS) {
                            if (opening(world.getBlockState(at.offset(side)).getMaterial())) { embedded++; }
                        }
                        if (embedded >= 2 && world.getBlockState(at.down()).getMaterial().isSolid()) {
                            IBlockState laid = fillGround(world, x, z);
                            if (laid.getBlock() == Blocks.DIRT && !world.getBlockState(at.up()).getMaterial().isSolid()) { laid = Blocks.GRASS.getDefaultState(); }
                            world.setBlockState(at, laid, 2);
                            opened++;
                        }
                        else { opened += clearAt(world, at); }
                    }
                }
            }
        }
        Predicate<BlockPos> within = spot -> world.isChunkGeneratedAt(spot.getX() >> 4, spot.getZ() >> 4)
                && !insideAnother(start, piece, spot)
                && !(spot.getX() >= box.minX && spot.getX() <= box.maxX && spot.getZ() >= box.minZ && spot.getZ() <= box.maxZ && spot.getY() <= box.maxY);
        Set<BlockPos> felledLogs = new HashSet<>();
        for (BlockPos leaf : overhangs) {
            if (!overhang(world.getBlockState(leaf))) { continue; }

            BlockPos trunk = sustainer(world, leaf, within);
            if (trunk == null) {
                at.setPos(leaf.getX(), leaf.getY(), leaf.getZ());
                opened += clearAt(world, at);
                continue;
            }
            if (felledLogs.contains(trunk)) { continue; }

            TreeCollector.Tree tree = TreeCollector.collect(world, trunk, mctmods.blastplaster.Config.view(world).getMaxTreeSize(), within);
            for (BlockPos log : tree.logs) {
                felledLogs.add(log);
                at.setPos(log.getX(), log.getY(), log.getZ());
                opened += clearAt(world, at);
            }
            for (BlockPos held : tree.leaves) {
                at.setPos(held.getX(), held.getY(), held.getZ());
                opened += clearAt(world, at);
            }
        }
        return new int[] {opened, spared, notGround, hangingOver};
    }

    private static void waystoneRing(StructureStart start, StructureComponent piece, World world, StructureBoundingBox clip, StructureBoundingBox box, BlockPos.MutableBlockPos at) {
        int ringed = 0;
        int lowX = Integer.MAX_VALUE;
        int highX = Integer.MIN_VALUE;
        int lowZ = Integer.MAX_VALUE;
        int highZ = Integer.MIN_VALUE;
        for (int x = box.minX; x <= box.maxX; x++) {
            for (int z = box.minZ; z <= box.maxZ; z++) {
                for (int y = box.minY; y <= box.maxY; y++) {
                    at.setPos(x, y, z);
                    if (!clip.isVecInside(at) || terrainBlock(world.getBlockState(at).getBlock()) || !world.getBlockState(at).getMaterial().isSolid()) { continue; }

                    if (x < lowX) { lowX = x; }
                    if (x > highX) { highX = x; }
                    if (z < lowZ) { lowZ = z; }
                    if (z > highZ) { highZ = z; }
                }
            }
        }
        if (lowX > highX) { return; }

        for (int x = lowX - 1; x <= highX + 1; x++) {
            for (int z = lowZ - 1; z <= highZ + 1; z++) {
                if (x >= lowX && x <= highX && z >= lowZ && z <= highZ) { continue; }

                for (int y = box.minY; y <= box.minY + 2; y++) {
                    at.setPos(x, y, z);
                    if (!clip.isVecInside(at) || insideAnother(start, piece, at) || underRoad(start, piece, x, z)) { continue; }

                    IBlockState held = world.getBlockState(at);
                    Material material = held.getMaterial();
                    if (material == Material.PLANTS || material == Material.VINE || material == Material.SNOW || overhang(held)) { ringed += clearAt(world, at); }
                    else if (y == box.minY && opening(material)) { ringed += clearAt(world, at); }
                }
            }
        }
        if (ringed > 0) { ContentLog.LOGGER.debug("Cleared {} block(s) ringing the waystone at {}, {}", ringed, box.minX, box.minZ); }
    }

    public static void openAround(StructureStart start, StructureComponent piece, World world, StructureBoundingBox clip) {
        if (!(piece instanceof StructureVillagePieces.Village) || piece instanceof StructureVillagePieces.Path) { return; }

        StructureBoundingBox box = piece.getBoundingBox();
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        if (piece.getClass().getSimpleName().toLowerCase(Locale.ROOT).contains("waystone")) { waystoneRing(start, piece, world, clip, box, at); }
        if (piece instanceof StructureVillagePieces.Torch) { torchPad(start, piece, world, clip); }
        if (piece instanceof StructureVillagePieces.Start) {
            bankWell(start, piece, world, clip, box, at);
            return;
        }
        int grounded = 0;
        int eaves = 0;
        int width = box.maxX - box.minX + 1;
        int depth = box.maxZ - box.minZ + 1;
        int[] footings = new int[width * depth];
        int lowestFooting = Integer.MAX_VALUE;
        int known = 0;
        for (int x = box.minX; x <= box.maxX; x++) {
            for (int z = box.minZ; z <= box.maxZ; z++) {
                at.setPos(x, box.minY, z);
                if (!clip.isVecInside(at)) {
                    footings[(x - box.minX) * depth + (z - box.minZ)] = Integer.MIN_VALUE;
                    continue;
                }
                int footing = Integer.MAX_VALUE;
                boolean fenced = false;
                for (int y = box.minY; y <= box.maxY; y++) {
                    at.setPos(x, y, z);
                    Block held = world.getBlockState(at).getBlock();
                    if (held == Blocks.AIR || terrainBlock(held)) { continue; }

                    footing = y - 1;
                    fenced = held instanceof BlockFence;
                    break;
                }
                for (int y = box.minY + 1; y <= box.maxY; y++) {
                    at.setPos(x, y, z);
                    if (!clip.isVecInside(at) || insideAnother(start, piece, at)) { continue; }

                    IBlockState held = world.getBlockState(at);
                    if (held.getBlock() == Blocks.STONE && !held.getValue(BlockStone.VARIANT).isNatural()) { continue; }
                    if (terrainBlock(held.getBlock()) || held.getMaterial() == Material.VINE) { eaves += clearAt(world, at); }
                }
                footings[(x - box.minX) * depth + (z - box.minZ)] = fenced && footing != Integer.MAX_VALUE ? -footing : footing;
                known++;
                if (footing < lowestFooting) { lowestFooting = footing; }
            }
        }
        boolean traced = ContentLog.LOGGER.debugEnabled();
        if (traced) { ContentLog.LOGGER.debug("Hook for {} box {},{},{} known {} lowest {}", piece.getClass().getSimpleName(), box.minX, box.minY, box.minZ, known, lowestFooting == Integer.MAX_VALUE ? "none" : String.valueOf(lowestFooting - box.minY)); }
        if (lowestFooting == Integer.MAX_VALUE) { return; }

        StringBuilder trace = traced ? new StringBuilder() : null;
        int[] froms = new int[width * depth];
        int[] tops = new int[width * depth];
        for (int x = box.minX; x <= box.maxX; x++) {
            for (int z = box.minZ; z <= box.maxZ; z++) {
                int spot = (x - box.minX) * depth + (z - box.minZ);
                int footing = footings[spot];
                boolean fenced = footing < 0 && footing != Integer.MIN_VALUE;
                if (fenced) { footing = -footing; }
                froms[spot] = Integer.MIN_VALUE;
                tops[spot] = Integer.MIN_VALUE;
                if (footing == Integer.MIN_VALUE) { continue; }

                int from = box.minY - 1;
                froms[spot] = from;
                for (int y = from; y >= box.minY - 24; y--) {
                    at.setPos(x, y, z);
                    if (!clip.isVecInside(at) || insideAnother(start, piece, at)) { continue; }
                    if (!world.getBlockState(at).getMaterial().isSolid()) { continue; }

                    tops[spot] = y;
                    break;
                }
            }
        }
        for (int x = box.minX; x <= box.maxX; x++) {
            for (int z = box.minZ; z <= box.maxZ; z++) {
                int spot = (x - box.minX) * depth + (z - box.minZ);
                int footing = footings[spot];
                if (footing < 0 && footing != Integer.MIN_VALUE) { footing = -footing; }
                int from = froms[spot];
                char verdict = footing == Integer.MIN_VALUE ? 'c' : from == Integer.MIN_VALUE ? 't' : footing == Integer.MAX_VALUE ? 'y' : 'f';
                int stood = 0;
                if (from != Integer.MIN_VALUE) {
                    IBlockState ground = Blocks.DIRT.getDefaultState();
                    if (tops[spot] != Integer.MIN_VALUE) {
                        at.setPos(x, tops[spot], z);
                        IBlockState resting = world.getBlockState(at);
                        if (resting.isFullBlock()) { ground = resting; }
                    }
                    int floor = restingFloor(tops, depth, spot, from);
                    for (int y = from; y >= floor; y--) {
                        at.setPos(x, y, z);
                        if (!clip.isVecInside(at) || insideAnother(start, piece, at)) { continue; }
                        if (world.getBlockState(at).getMaterial().isSolid()) { break; }

                        boolean exposed = !world.getBlockState(at.up()).getMaterial().isSolid();
                        IBlockState laidAs = ground;
                        if (exposed && ground.getBlock() == Blocks.DIRT) { laidAs = Blocks.GRASS.getDefaultState(); }
                        else if (!exposed && ground.getBlock() == Blocks.GRASS) { laidAs = Blocks.DIRT.getDefaultState(); }
                        world.setBlockState(at, laidAs, 2);
                        stood++;
                    }
                    grounded += stood;
                }
                if (traced) { trace.append(x - box.minX).append(',').append(z - box.minZ).append('=').append(footing == Integer.MIN_VALUE ? "clip" : String.valueOf(footing - box.minY)).append(verdict).append(stood > 0 ? "+" + stood : "").append(' '); }
            }
        }
        if (traced) { ContentLog.LOGGER.debug("Footing for {} box {},{},{} to {},{},{} lowest {} known {} filled {}: {}", piece.getClass().getSimpleName(), box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, lowestFooting - box.minY, known, grounded, trace); }
        int overhead = 0;
        for (int x = box.minX; x <= box.maxX; x++) {
            for (int z = box.minZ; z <= box.maxZ; z++) {
                int roof = box.minY + 2;
                for (int y = box.maxY; y > box.minY; y--) {
                    at.setPos(x, y, z);
                    if (!clip.isVecInside(at)) { break; }

                    Block held = world.getBlockState(at).getBlock();
                    if (held == Blocks.AIR || terrainBlock(held)) { continue; }

                    roof = y;
                    break;
                }
                for (int y = roof + 1; y <= box.maxY + 4; y++) {
                    at.setPos(x, y, z);
                    if (!clip.isVecInside(at) || insideAnother(start, piece, at)) { continue; }
                    if (!terrainBlock(world.getBlockState(at).getBlock())) { continue; }

                    world.setBlockState(at, Blocks.AIR.getDefaultState(), 2);
                    overhead++;
                }
            }
        }
        int banked = 0;
        int roadGrade = roadGradeBeside(world, box);
        int bank = roadGrade == Integer.MIN_VALUE ? box.minY - 1 : roadGrade - 1;
        ContentLog.LOGGER.debug("{} at {}, {} banks its ring at y {} against road grade {}", piece.getClass().getSimpleName(), box.minX, box.minZ, bank, roadGrade == Integer.MIN_VALUE ? "none" : roadGrade);
        for (int x = box.minX - 2; x <= box.maxX + 2; x++) {
            for (int z = box.minZ - 2; z <= box.maxZ + 2; z++) {
                if (x > box.minX && x < box.maxX && z > box.minZ && z < box.maxZ) { continue; }
                if (underAnother(start, piece, x, z)) { continue; }

                at.setPos(x, bank, z);
                if (!clip.isVecInside(at) || insideAnother(start, piece, at)) { continue; }
                if (world.getBlockState(at).getMaterial().isSolid() || world.getBlockState(at).getMaterial().isLiquid()) { continue; }

                for (int y = bank; y >= bank - 5; y--) {
                    at.setPos(x, y, z);
                    IBlockState held = world.getBlockState(at);
                    if (held.getMaterial().isLiquid()) { break; }
                    if (held.getMaterial().isSolid()) { break; }

                    IBlockState laid = fillGround(world, x, z);
                    if (laid.getBlock() == Blocks.SAND && !sandBiome(world, x, z) && (piece instanceof StructureVillagePieces.Field1 || piece instanceof StructureVillagePieces.Field2)) { laid = Blocks.DIRT.getDefaultState(); }
                    if (laid.getBlock() == Blocks.DIRT && !world.getBlockState(at.up()).getMaterial().isSolid()) { laid = Blocks.GRASS.getDefaultState(); }
                    world.setBlockState(at, laid, 2);
                    banked++;
                }
            }
        }
        if (piece instanceof StructureVillagePieces.Field1 || piece instanceof StructureVillagePieces.Field2) {
            int soiled = 0;
            for (int x = box.minX; x <= box.maxX; x++) {
                for (int z = box.minZ; z <= box.maxZ; z++) {
                    for (int y = box.minY - 3; y <= box.minY; y++) {
                        at.setPos(x, y, z);
                        if (!clip.isVecInside(at)) { continue; }
                        if (world.getBlockState(at).getMaterial() == Material.SAND) {
                            world.setBlockState(at, Blocks.DIRT.getDefaultState(), 2);
                            soiled++;
                        }
                    }
                }
            }
            if (soiled > 0) { ContentLog.LOGGER.debug("Turned {} sand block(s) to soil under {} at {}, {}", soiled, piece.getClass().getSimpleName(), box.minX, box.minZ); }
        }
        int[] ring = openOver(start, piece, world, box, clip, at);
        int opened = ring[0];
        int spared = ring[1];
        int notGround = ring[2];
        int hangingOver = ring[3];
        int doorways = 0;
        for (int x = box.minX; x <= box.maxX; x++) {
            for (int z = box.minZ; z <= box.maxZ; z++) {
                if (x != box.minX && x != box.maxX && z != box.minZ && z != box.maxZ) { continue; }

                for (int y = box.minY; y <= box.maxY - 1; y++) {
                    at.setPos(x, y, z);
                    if (!clip.isVecInside(at)) { break; }
                    if (!(world.getBlockState(at).getBlock() instanceof BlockDoor)) { continue; }

                    int outX = x == box.minX ? -1 : x == box.maxX ? 1 : 0;
                    int outZ = outX != 0 ? 0 : z == box.minZ ? -1 : 1;
                    at.setPos(x + outX, y - 1, z + outZ);
                    boolean floored = false;
                    if (clip.isVecInside(at) && !underRoad(start, piece, x + outX, z + outZ) && !insideAnother(start, piece, at) && !world.getBlockState(at).getMaterial().isSolid() && !world.getBlockState(at).getMaterial().isLiquid()) {
                        IBlockState floor = fillGround(world, x + outX, z + outZ);
                        if (floor.getBlock() == Blocks.DIRT && !world.getBlockState(at.up()).getMaterial().isSolid()) { floor = Blocks.GRASS.getDefaultState(); }
                        world.setBlockState(at, floor, 2);
                        doorways++;
                        floored = true;
                    }
                    ContentLog.LOGGER.debug("{} at {}, {} found its door at {}, {}, {} facing out {}, {} and {} the ground in front (spot held {})", piece.getClass().getSimpleName(), box.minX, box.minZ, x, y, z, outX, outZ, floored ? "floored" : "kept", world.getBlockState(at));
                    for (int step = 1; step <= 5; step++) {
                        if (underRoad(start, piece, x + outX * step, z + outZ * step)) { break; }
                        for (int up = 0; up <= 3; up++) {
                            at.setPos(x + outX * step, y + up, z + outZ * step);
                            if (!clip.isVecInside(at) || insideAnother(start, piece, at)) { continue; }

                            Block held = world.getBlockState(at).getBlock();
                            if (opening(world.getBlockState(at).getMaterial()) || held == Blocks.GRASS_PATH || held == Blocks.GRAVEL) { doorways += clearAt(world, at); }
                        }
                    }
                    break;
                }
            }
        }
        for (StructureComponent other : start.getComponents()) {
            if (!(other instanceof StructureVillagePieces.Path)) { continue; }

            StructureBoundingBox road = other.getBoundingBox();
            int lowX = Math.max(box.minX, road.minX);
            int highX = Math.min(box.maxX, road.maxX);
            int lowZ = Math.max(box.minZ, road.minZ);
            int highZ = Math.min(box.maxZ, road.maxZ);
            int fromX;
            int toX;
            int fromZ;
            int toZ;
            if (lowX <= highX && (road.minZ - box.maxZ == 2 || box.minZ - road.maxZ == 2)) {
                fromX = lowX;
                toX = highX;
                fromZ = road.minZ > box.maxZ ? box.maxZ + 1 : road.maxZ + 1;
                toZ = fromZ;
            }
            else if (lowZ <= highZ && (road.minX - box.maxX == 2 || box.minX - road.maxX == 2)) {
                fromZ = lowZ;
                toZ = highZ;
                fromX = road.minX > box.maxX ? box.maxX + 1 : road.maxX + 1;
                toX = fromX;
            }
            else { continue; }
            for (int x = fromX; x <= toX; x++) {
                for (int z = fromZ; z <= toZ; z++) {
                    if (underRoad(start, piece, x, z)) { continue; }

                    for (int y = box.minY + 1; y <= box.minY + 4; y++) {
                        at.setPos(x, y, z);
                        if (!clip.isVecInside(at) || insideAnother(start, piece, at)) { continue; }
                        if (clearable(world.getBlockState(at))) { doorways += clearAt(world, at); }
                    }
                }
            }
        }
        int bridged = 0;
        for (StructureComponent other : start.getComponents()) {
            if (other == piece || other instanceof StructureVillagePieces.Path || !(other instanceof StructureVillagePieces.Village)) { continue; }

            bridged += bridge(world, start, piece, box, ((AccessorStructureComponentBox) other).rdpl$box(), clip, at);
        }
        if (opened + eaves + spared + notGround + hangingOver + grounded + overhead + bridged + doorways + banked > 0) { ContentLog.LOGGER.debug("Opened {} block(s) around {} at {}, {}, spared {} inside neighbouring pieces, left {} that were not ground, stood {} block(s) of ground under it, banked {} up to its grade, lifted {} off its roof, bridged {} between it and a neighbour, freed {} in front of its doors, and the hillside still hangs over {} column(s)", opened + eaves, piece.getClass().getSimpleName(), box.minX, box.minZ, spared, notGround, grounded, banked, overhead, bridged, doorways, hangingOver); }
    }

    private static int restingFloor(int[] tops, int depth, int spot, int from) {
        int own = tops[spot];
        int floor = own == Integer.MIN_VALUE ? from : own + 1;
        int best = own == Integer.MIN_VALUE ? Integer.MAX_VALUE : from - own;
        int spotX = spot / depth;
        int spotZ = spot % depth;
        for (int i = 0; i < tops.length; i++) {
            if (i == spot || tops[i] == Integer.MIN_VALUE) { continue; }

            int cost = (Math.abs(i / depth - spotX) + Math.abs(i % depth - spotZ)) * 4 + Math.max(0, from - tops[i]);
            if (cost >= best) { continue; }

            best = cost;
            floor = tops[i];
        }
        return floor;
    }

    public static void torchPad(StructureStart start, StructureComponent piece, World world, StructureBoundingBox clip) {
        if (!pathChosen()) { return; }

        StructureBoundingBox box = piece.getBoundingBox();
        int grade = roadGradeBeside(world, box);
        if (grade == Integer.MIN_VALUE) { return; }

        IBlockState surface = pathBlock("villagePathBlock", Config.worldgen.villagePathBlock, Blocks.GRASS_PATH.getDefaultState());
        IBlockState sidewalk = pathBlock("villagePathSidewalkBlock", Config.worldgen.villagePathSidewalkBlock, surface);
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        int ground = grade - 1;
        int paved = 0;
        for (int x = box.minX - 1; x <= box.maxX + 1; x++) {
            for (int z = box.minZ - 1; z <= box.maxZ + 1; z++) {
                at.setPos(x, ground, z);
                if (!clip.isVecInside(at) || underRoad(start, piece, x, z)) { continue; }
                if (world.getBlockState(world.getTopSolidOrLiquidBlock(at).down()).getMaterial().isLiquid()) { continue; }

                for (int y = ground + 1; y <= ground + 3; y++) {
                    at.setPos(x, y, z);
                    IBlockState above = world.getBlockState(at);
                    if (above.getBlock() == Blocks.AIR) { continue; }
                    if (!terrainBlock(above.getBlock()) && above.getMaterial().isSolid()) { break; }

                    world.setBlockState(at, Blocks.AIR.getDefaultState(), 2);
                }
                for (int y = ground - 1; y >= ground - 8; y--) {
                    at.setPos(x, y, z);
                    if (world.getBlockState(at).getMaterial().isSolid()) { break; }

                    world.setBlockState(at, fillGround(world, x, z), 2);
                }
                at.setPos(x, ground, z);
                world.setBlockState(at, sidewalk, 2);
                paved++;
            }
        }
        if (paved > 0 && ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("Paved a sidewalk pad of {} column(s) under the lamp at {}, {}", paved, box.minX, box.minZ); }
    }

    private static void bankWell(StructureStart start, StructureComponent piece, World world, StructureBoundingBox clip, StructureBoundingBox box, BlockPos.MutableBlockPos at) {
        int rim = box.maxY - 3;
        int banked = 0;
        int opened = 0;
        for (int x = box.minX - 2; x <= box.maxX + 2; x++) {
            for (int z = box.minZ - 2; z <= box.maxZ + 2; z++) {
                if (x >= box.minX && x <= box.maxX && z >= box.minZ && z <= box.maxZ) { continue; }
                if (underRoad(start, piece, x, z)) { continue; }
                IBlockState ground = Blocks.DIRT.getDefaultState();
                for (int y = rim; y >= rim - BAND + 1; y--) {
                    at.setPos(x, y, z);
                    if (!clip.isVecInside(at) || insideAnother(start, piece, at)) { continue; }
                    if (!world.getBlockState(at).getMaterial().isSolid()) { continue; }

                    IBlockState resting = world.getBlockState(at);
                    if (resting.isFullBlock()) { ground = resting; }
                    break;
                }
                for (int y = rim; y >= rim - BAND + 1; y--) {
                    at.setPos(x, y, z);
                    if (!clip.isVecInside(at) || insideAnother(start, piece, at)) { continue; }
                    if (world.getBlockState(at).getMaterial().isSolid()) { break; }

                    IBlockState laidAs = ground;
                    if (y == rim && ground.getBlock() == Blocks.DIRT) { laidAs = Blocks.GRASS.getDefaultState(); }
                    else if (y != rim && ground.getBlock() == Blocks.GRASS) { laidAs = Blocks.DIRT.getDefaultState(); }
                    world.setBlockState(at, laidAs, 2);
                    banked++;
                }
                for (int y = rim + 1; y <= rim + 4; y++) {
                    at.setPos(x, y, z);
                    if (!clip.isVecInside(at) || insideAnother(start, piece, at)) { continue; }

                    Material material = world.getBlockState(at).getMaterial();
                    if (material == Material.ROCK || material == Material.GROUND || material == Material.GRASS || material == Material.SAND || material == Material.CLAY || material == Material.SNOW) {
                        world.setBlockState(at, Blocks.AIR.getDefaultState(), 2);
                        opened++;
                    }
                }
            }
        }
        int shored = 0;
        for (int x = box.minX; x <= box.maxX; x++) {
            for (int z = box.minZ; z <= box.maxZ; z++) {
                int lx = x - box.minX;
                int lz = z - box.minZ;
                if (lx != 0 && lx != 5 && lz != 0 && lz != 5) { continue; }

                IBlockState ground = Blocks.DIRT.getDefaultState();
                for (int y = rim - 1; y >= rim - BAND; y--) {
                    at.setPos(x, y, z);
                    if (!clip.isVecInside(at) || insideAnother(start, piece, at)) { continue; }
                    if (!world.getBlockState(at).getMaterial().isSolid()) { continue; }

                    IBlockState resting = world.getBlockState(at);
                    if (resting.isFullBlock()) { ground = resting; }
                    break;
                }
                for (int y = rim - 1; y >= rim - BAND; y--) {
                    at.setPos(x, y, z);
                    if (!clip.isVecInside(at) || insideAnother(start, piece, at)) { continue; }
                    if (world.getBlockState(at).getMaterial().isSolid()) { break; }

                    world.setBlockState(at, ground.getBlock() == Blocks.GRASS ? Blocks.DIRT.getDefaultState() : ground, 2);
                    shored++;
                }
            }
        }
        int swept = 0;
        for (int x = box.minX; x <= box.maxX; x++) {
            for (int z = box.minZ; z <= box.maxZ; z++) {
                int lx = x - box.minX;
                int lz = z - box.minZ;
                boolean frame = lx == 0 || lx == 5 || lz == 0 || lz == 5;
                boolean post = (lx == 1 || lx == 4) && (lz == 1 || lz == 4);
                if (!frame && post) { continue; }

                int from = frame ? rim + 1 : box.maxY - 1;
                int to = frame ? box.maxY + 1 : box.maxY;
                for (int y = from; y <= to; y++) {
                    at.setPos(x, y, z);
                    if (!clip.isVecInside(at) || insideAnother(start, piece, at)) { continue; }

                    Material material = world.getBlockState(at).getMaterial();
                    if (material == Material.ROCK || material == Material.GROUND || material == Material.GRASS || material == Material.SAND || material == Material.CLAY || material == Material.SNOW) {
                        world.setBlockState(at, Blocks.AIR.getDefaultState(), 2);
                        swept++;
                    }
                }
            }
        }
        if (banked + opened + swept + shored > 0) { ContentLog.LOGGER.debug("Banked {} block(s), opened {}, swept {} out of the frame and shored {} under the rim around the well at {}, {}, up to its rim at y {}", banked, opened, swept, shored, box.minX, box.minZ, rim); }
    }

    private static int clearAt(World world, BlockPos.MutableBlockPos at) {
        boolean grassy = world.getBlockState(at).getBlock() == Blocks.GRASS;
        world.setBlockState(at, Blocks.AIR.getDefaultState(), 2);
        int cleared = 1;
        at.move(EnumFacing.UP);
        if (world.getBlockState(at).getBlock() == Blocks.SNOW_LAYER) {
            world.setBlockState(at, Blocks.AIR.getDefaultState(), 2);
            cleared++;
        }
        at.move(EnumFacing.DOWN);
        at.move(EnumFacing.DOWN);
        if (grassy && world.getBlockState(at) == Blocks.DIRT.getDefaultState()) { world.setBlockState(at, Blocks.GRASS.getDefaultState(), 2); }
        at.move(EnumFacing.UP);
        return cleared;
    }

    private static StructureStart CURRENT;

    public static void building(StructureStart start) { CURRENT = start; }

    public static StructureStart current() { return CURRENT; }

    private static boolean overhang(IBlockState held) {
        if (held.getMaterial() != Material.LEAVES) { return false; }

        return !held.getPropertyKeys().contains(BlockLeaves.DECAYABLE) || held.getValue(BlockLeaves.DECAYABLE);
    }

    private static boolean opening(Material material) {
        return material == Material.ROCK || material == Material.GROUND || material == Material.GRASS || material == Material.SAND || material == Material.CLAY || material == Material.SNOW;
    }

    private static boolean roadBed(World world, StructureStart start, BlockPos at, int x, int z) {
        Block held = world.getBlockState(at).getBlock();
        if (held == Blocks.GRASS_PATH) { return true; }

        return held == Blocks.GRAVEL && overRoad(start, x, z);
    }

    private static int roadTop(World world, StructureStart start, BlockPos.MutableBlockPos at, int x, int z, int from, int to) {
        if (!world.isChunkGeneratedAt(x >> 4, z >> 4)) { return Integer.MIN_VALUE; }

        for (int y = to; y >= from; y--) {
            at.setPos(x, y, z);
            if (roadBed(world, start, at, x, z)) { return y; }
        }
        return Integer.MIN_VALUE;
    }

    private static boolean overRoad(StructureStart start, int x, int z) {
        for (StructureComponent other : start.getComponents()) {
            if (!(other instanceof StructureVillagePieces.Path)) { continue; }

            StructureBoundingBox held = ((AccessorStructureComponentBox) other).rdpl$box();
            if (held != null && x >= held.minX && x <= held.maxX && z >= held.minZ && z <= held.maxZ) { return true; }
        }
        return false;
    }

    private static int bridge(World world, StructureStart start, StructureComponent piece, StructureBoundingBox box, StructureBoundingBox near, StructureBoundingBox clip, BlockPos.MutableBlockPos at) {
        if (near == null || Math.abs(box.minY - near.minY) > 2) { return 0; }

        int lowX = Math.max(box.minX, near.minX);
        int highX = Math.min(box.maxX, near.maxX);
        int lowZ = Math.max(box.minZ, near.minZ);
        int highZ = Math.min(box.maxZ, near.maxZ);
        int fromX;
        int toX;
        int fromZ;
        int toZ;
        if (lowX <= highX && (near.minZ - box.maxZ > 1 && near.minZ - box.maxZ <= 6 || box.minZ - near.maxZ > 1 && box.minZ - near.maxZ <= 6)) {
            fromX = lowX;
            toX = highX;
            fromZ = near.minZ > box.maxZ ? box.maxZ + 1 : near.maxZ + 1;
            toZ = near.minZ > box.maxZ ? near.minZ - 1 : box.minZ - 1;
        }
        else if (lowZ <= highZ && (near.minX - box.maxX > 1 && near.minX - box.maxX <= 6 || box.minX - near.maxX > 1 && box.minX - near.maxX <= 6)) {
            fromZ = lowZ;
            toZ = highZ;
            fromX = near.minX > box.maxX ? box.maxX + 1 : near.maxX + 1;
            toX = near.minX > box.maxX ? near.minX - 1 : box.minX - 1;
        }
        else { return 0; }
        int cleared = 0;
        for (int x = fromX; x <= toX; x++) {
            for (int z = fromZ; z <= toZ; z++) {
                if (underRoad(start, piece, x, z)) { continue; }

                int toBox = Math.max(0, Math.max(box.minX - x, x - box.maxX)) + Math.max(0, Math.max(box.minZ - z, z - box.maxZ));
                int toNear = Math.max(0, Math.max(near.minX - x, x - near.maxX)) + Math.max(0, Math.max(near.minZ - z, z - near.maxZ));
                int base = toBox <= toNear ? box.minY : near.minY;
                int bed = roadTop(world, start, at, x, z, base + 1, base + 12);
                for (int y = bed == Integer.MIN_VALUE ? base + 1 : bed + 1; y <= base + 12; y++) {
                    at.setPos(x, y, z);
                    if (!clip.isVecInside(at) || insideAnother(start, piece, at)) { continue; }
                    IBlockState held = world.getBlockState(at);
                    if (opening(held.getMaterial()) || overhang(held)) { cleared += clearAt(world, at); }
                }
            }
        }
        return cleared;
    }

    public static boolean pathChosen() { return !ContentControl.text(ContentControl.VILLAGES, "villagePathBlock", Config.worldgen.villagePathBlock).isEmpty(); }

    public static int pathExtraWidth() { return Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villagePathExtraWidth", Config.worldgen.villagePathExtraWidth)); }

    public static int pathLineColumns() { return ContentControl.text(ContentControl.VILLAGES, "villagePathLineBlock", Config.worldgen.villagePathLineBlock).isEmpty() ? 0 : 1; }

    public static int pathSidewalkWidth() {
        if (ContentControl.text(ContentControl.VILLAGES, "villagePathSidewalkBlock", Config.worldgen.villagePathSidewalkBlock).isEmpty()) { return 0; }

        return Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villagePathSidewalkWidth", Config.worldgen.villagePathSidewalkWidth));
    }

    public static int pathFullWidth() { return 3 + 2 * (pathExtraWidth() + pathLineColumns() + pathSidewalkWidth()); }

    public static int pathMinimumWidth() { return Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villagePathMinimumWidth", Config.worldgen.villagePathMinimumWidth)); }

    public static IBlockState pathBlock(String key, String fromConfig, IBlockState vanilla) {
        String named = ContentControl.text(ContentControl.VILLAGES, key, fromConfig);
        if (named.isEmpty()) { return vanilla; }

        IBlockState state = ContentStates.parse(named, key);
        if (state == null) {
            ContentLog.LOGGER.error("{} '{}' is not a registered block, using the vanilla road block", key, named);
            return vanilla;
        }
        return state;
    }

    public static void attach(StructureStart start, StructureComponent piece) {
        if (!(piece instanceof StructureVillagePieces.Path)) { return; }

        StructureBoundingBox box = ((AccessorStructureComponentBox) piece).rdpl$box();
        if (box == null) { return; }

        boolean alongX = roadAlongX(piece);
        for (StructureComponent other : start.getComponents()) {
            if (other == piece || !(other instanceof StructureVillagePieces.Path)) { continue; }

            StructureBoundingBox met = ((AccessorStructureComponentBox) other).rdpl$box();
            if (met == null) { continue; }

            boolean lined = alongX ? met.maxZ >= box.minZ && met.minZ <= box.maxZ : met.maxX >= box.minX && met.minX <= box.maxX;
            if (!lined) { continue; }

            int ahead = alongX ? met.minX - box.maxX : met.minZ - box.maxZ;
            int behind = alongX ? box.minX - met.maxX : box.minZ - met.maxZ;
            if (ahead > 1 && ahead <= ATTACH_GAP && free(start, piece, box, alongX, met)) {
                if (alongX) { box.maxX = met.minX - 1; }
                else { box.maxZ = met.minZ - 1; }
            }
            else if (behind > 1 && behind <= ATTACH_GAP && free(start, piece, box, alongX, met)) {
                if (alongX) { box.minX = met.maxX + 1; }
                else { box.minZ = met.maxZ + 1; }
            }
        }
    }

    private static boolean free(StructureStart start, StructureComponent piece, StructureBoundingBox box, boolean alongX, StructureBoundingBox met) {
        int least = alongX ? Math.min(box.maxX, met.maxX) : Math.min(box.maxZ, met.maxZ);
        int most = alongX ? Math.max(box.minX, met.minX) : Math.max(box.minZ, met.minZ);
        for (StructureComponent other : start.getComponents()) {
            if (other == piece || other instanceof StructureVillagePieces.Path) { continue; }

            StructureBoundingBox held = other.getBoundingBox();
            boolean acrossed = alongX ? held.maxZ >= box.minZ && held.minZ <= box.maxZ : held.maxX >= box.minX && held.minX <= box.maxX;
            boolean along = alongX ? held.maxX >= least && held.minX <= most : held.maxZ >= least && held.minZ <= most;
            if (acrossed && along) { return false; }
        }
        return true;
    }

    public static void pave(StructureComponent piece, World world, StructureBoundingBox clip, IBlockState path, IBlockState gravel, IBlockState planks, boolean chosenSurface) {
        StructureBoundingBox box = piece.getBoundingBox();
        boolean alongX = roadAlongX(piece);
        int least = Math.max(alongX ? box.minX : box.minZ, alongX ? clip.minX : clip.minZ);
        int most = Math.min(alongX ? box.maxX : box.maxZ, alongX ? clip.maxX : clip.maxZ);
        if (most < least) { return; }

        if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("Paving the road at {}, {}, {} across, with surface {} (chosen={}), support {}, bridge {}", box.minX, box.minZ, (alongX ? box.maxZ - box.minZ : box.maxX - box.minX) + 1, path, chosenSurface, gravel, planks); }
        int acrossLeast = alongX ? box.minZ : box.minX;
        int acrossMost = alongX ? box.maxZ : box.maxX;
        int start = least;
        boolean computed = false;
        int[] profile = noiseProfile(world, alongX, alongX ? box.minX : box.minZ, alongX ? box.maxX : box.maxZ, acrossLeast, acrossMost);
        if (profile != null) {
            start = alongX ? box.minX : box.minZ;
            computed = true;
        }
        else {
            int rows = most - least + 1;
            profile = new int[rows];
            for (int i = 0; i < rows; i++) {
                int found = Integer.MIN_VALUE;
                for (int across = acrossLeast; across <= acrossMost; across++) {
                    int x = alongX ? least + i : across;
                    int z = alongX ? across : least + i;
                    BlockPos spot = new BlockPos(x, 64, z);
                    if (!clip.isVecInside(spot)) { continue; }

                    BlockPos top = world.getTopSolidOrLiquidBlock(spot).down();
                    if (top.getY() < world.getSeaLevel() - 1 || world.getBlockState(top).getMaterial().isLiquid()) { continue; }
                    if (top.getY() > found) { found = top.getY(); }
                }
                profile[i] = found;
            }
            int before = roadAnchor(world, alongX, least - 1, acrossLeast, acrossMost, path, gravel);
            if (before != Integer.MIN_VALUE && profile[0] != Integer.MIN_VALUE) { profile[0] = Math.max(before - 1, Math.min(before + 1, profile[0])); }
            int after = roadAnchor(world, alongX, most + 1, acrossLeast, acrossMost, path, gravel);
            if (after != Integer.MIN_VALUE && profile[rows - 1] != Integer.MIN_VALUE) { profile[rows - 1] = Math.max(after - 1, Math.min(after + 1, profile[rows - 1])); }
        }
        int[] ground = profile.clone();
        if (computed) { flatRuns(world, alongX, start, acrossLeast, acrossMost, profile); }
        boolean[] bridged = smooth(profile);
        boolean[] pinned = new boolean[profile.length];
        boolean[] plaza = new boolean[profile.length];
        if (computed) {
            roadApron(world, piece, alongX, start, profile, pinned);
            clampToWell(alongX, start, acrossLeast, acrossMost, profile, plaza);
            for (int i = 0; i < pinned.length; i++) { if (plaza[i]) { pinned[i] = true; } }
            settle(profile, pinned);
        }
        int capped = capEmbankment(profile, ground, bridged, plaza);
        if (capped > 0 && ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("Capped {} row(s) of the road at {}, {} to {} block(s) above their own ground", capped, box.minX, box.minZ, EMBANKMENT_CAP); }
        if (ContentLog.LOGGER.debugEnabled()) {
            StringBuilder trace = new StringBuilder();
            for (int i = 0; i < profile.length; i++) {
                if (i > 0) { trace.append(' '); }
                trace.append(start + i).append(':');
                if (ground[i] == Integer.MIN_VALUE) { trace.append('-'); }
                else { trace.append(ground[i]); }
                trace.append('/');
                if (profile[i] == Integer.MIN_VALUE) { trace.append('-'); }
                else { trace.append(profile[i]); }
                if (bridged[i]) { trace.append('b'); }
            }
            ContentLog.LOGGER.debug("Profile of the road at {}, {} along {}, computed {}, as row:ground/graded, capped {} row(s) at {}: {}", box.minX, box.minZ, alongX ? "x" : "z", computed, capped, EMBANKMENT_CAP, trace);
        }
        if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("The road at {}, {} grades from y {} to y {} along its length", box.minX, box.minZ, profile[0] == Integer.MIN_VALUE ? "water" : profile[0], profile[profile.length - 1] == Integer.MIN_VALUE ? "water" : profile[profile.length - 1]); }
        int cut = 0;
        int filled = 0;
        int paved = 0;
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        for (int i = Math.max(0, least - start); i < profile.length && start + i <= most; i++) {
            for (int across = acrossLeast; across <= acrossMost; across++) {
                int x = alongX ? start + i : across;
                int z = alongX ? across : start + i;
                BlockPos spot = new BlockPos(x, 64, z);
                if (!clip.isVecInside(spot)) { continue; }

                BlockPos top = world.getTopSolidOrLiquidBlock(spot).down();
                if (top.getY() < world.getSeaLevel()) { top = new BlockPos(x, world.getSeaLevel() - 1, z); }
                if (profile[i] == Integer.MIN_VALUE) {
                    if (world.getBlockState(top).getMaterial().isLiquid()) { paved += deckBridge(world, alongX, start + i, across, acrossLeast, acrossMost, top.getY() + 1, planks, at); }
                    continue;
                }
                if (bridged[i]) {
                    paved += deckBridge(world, alongX, start + i, across, acrossLeast, acrossMost, profile[i], planks, at);
                    continue;
                }
                if (world.getBlockState(top).getMaterial().isLiquid()) {
                    paved += deckBridge(world, alongX, start + i, across, acrossLeast, acrossMost, top.getY() + 1, planks, at);
                    continue;
                }

                int target = profile[i];
                at.setPos(x, target, z);
                IBlockState held = world.getBlockState(at);
                Block base = held.getBlock();
                if (held.getMaterial().isSolid() && held.getMaterial() != Material.WOOD && held.getMaterial() != Material.LEAVES && !terrainBlock(base) && base != Blocks.GRASS_PATH && base != Blocks.PLANKS && base != Blocks.SANDSTONE && base != Blocks.RED_SANDSTONE && base != Blocks.HARDENED_CLAY && base != Blocks.STAINED_HARDENED_CLAY && base != Blocks.MYCELIUM) { continue; }

                for (int y = target + 1; y <= target + 4; y++) {
                    at.setPos(x, y, z);
                    IBlockState above = world.getBlockState(at);
                    Block up = above.getBlock();
                    if (up == Blocks.AIR) { continue; }
                    if (above.getMaterial().isLiquid()) { break; }
                    if (terrainBlock(up) || up == Blocks.GRASS_PATH || up == Blocks.SANDSTONE || up == Blocks.MYCELIUM || above.getMaterial() == Material.WOOD || above.getMaterial() == Material.LEAVES || !above.getMaterial().isSolid()) {
                        world.setBlockState(at, Blocks.AIR.getDefaultState(), 2);
                        cut++;
                        continue;
                    }
                    break;
                }
                for (int y = target - 1; y >= target - 8; y--) {
                    at.setPos(x, y, z);
                    if (world.getBlockState(at).getMaterial().isSolid()) { break; }

                    world.setBlockState(at, fillGround(world, x, z), 2);
                    filled++;
                }
                at.setPos(x, target, z);
                boolean earthy = base == Blocks.GRASS || base == Blocks.DIRT || base == Blocks.MYCELIUM || base == Blocks.GRASS_PATH || base == Blocks.AIR || !world.getBlockState(at).getMaterial().isSolid();
                IBlockState natural = chosenSurface ? path : pathForGround(world, x, z, path, gravel, earthy);
                IBlockState dressed = dressSurface(world, piece, alongX, alongX ? x : z, alongX ? z : x, (acrossLeast + acrossMost) / 2, natural);
                world.setBlockState(at, dressed != null ? dressed : natural, 2);
                paved++;
            }
        }
        if ((cut + filled + paved > 0) && ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("Graded the road at {}, {} within its chunk: paved {} column(s), cut {} block(s) off bumps, filled {} into dips", box.minX, box.minZ, paved, cut, filled); }
    }

    @Nullable private static int[] noiseProfile(World world, boolean alongX, int rowLeast, int rowMost, int acrossLeast, int acrossMost) {
        if (samplerFor(world) == null) { return null; }

        int[] profile = new int[rowMost - rowLeast + 1];
        int[] across = new int[acrossMost - acrossLeast + 1];
        for (int i = 0; i < profile.length; i++) {
            int count = 0;
            for (int at = acrossLeast; at <= acrossMost; at++) {
                int sampled = surfaceAt(world, alongX ? rowLeast + i : at, alongX ? at : rowLeast + i);
                if (sampled < world.getSeaLevel() - 1) { continue; }
                across[count++] = sampled;
            }
            if (count == 0) {
                profile[i] = Integer.MIN_VALUE;
                continue;
            }
            Arrays.sort(across, 0, count);
            profile[i] = across[count / 2];
        }
        return profile;
    }

    private static boolean[] smooth(int[] profile) {
        int rows = profile.length;
        for (int i = 1; i < rows; i++) { if (joined(profile, i) && profile[i] > profile[i - 1] + 1) { profile[i] = profile[i - 1] + 1; } }
        for (int i = rows - 2; i >= 0; i--) { if (joined(profile, i + 1) && profile[i] > profile[i + 1] + 1) { profile[i] = profile[i + 1] + 1; } }
        for (int i = 1; i < rows; i++) { if (joined(profile, i) && profile[i] < profile[i - 1] - 1) { profile[i] = profile[i - 1] - 1; } }
        for (int i = rows - 2; i >= 0; i--) { if (joined(profile, i + 1) && profile[i] < profile[i + 1] - 1) { profile[i] = profile[i + 1] - 1; } }
        for (int i = 1; i < rows - 1; i++) {
            if (!joined(profile, i) || profile[i + 1] == Integer.MIN_VALUE) { continue; }
            if (profile[i - 1] == profile[i + 1] && Math.abs(profile[i] - profile[i - 1]) == 1) { profile[i] = profile[i - 1]; }
        }
        boolean[] bridged = new boolean[rows];
        for (int i = 0; i < rows; i++) {
            if (profile[i] != Integer.MIN_VALUE) { continue; }

            int gapEnd = i;
            while (gapEnd < rows && profile[gapEnd] == Integer.MIN_VALUE) { gapEnd++; }
            if (i > 0 && gapEnd < rows && gapEnd - i <= 12) {
                int fromY = profile[i - 1];
                int toY = profile[gapEnd];
                for (int held = i; held < gapEnd; held++) {
                    profile[held] = fromY + (toY - fromY) * (held - i + 1) / (gapEnd - i + 1);
                    bridged[held] = true;
                }
            }
            i = gapEnd;
        }
        return bridged;
    }

    private static int capEmbankment(int[] profile, int[] ground, boolean[] bridged, boolean[] held) {
        int capped = 0;
        for (int i = 0; i < profile.length; i++) {
            if (profile[i] == Integer.MIN_VALUE || ground[i] == Integer.MIN_VALUE || bridged[i] || held[i]) { continue; }
            if (profile[i] <= ground[i] + EMBANKMENT_CAP) { continue; }

            profile[i] = ground[i] + EMBANKMENT_CAP;
            held[i] = true;
            capped++;
        }
        return capped;
    }

    private static void settle(int[] profile, boolean[] held) {
        int rows = profile.length;
        for (int i = 1; i < rows - 1; i++) {
            if (held[i] || !joined(profile, i) || profile[i + 1] == Integer.MIN_VALUE) { continue; }
            if (profile[i - 1] == profile[i + 1] && Math.abs(profile[i] - profile[i - 1]) == 1) { profile[i] = profile[i - 1]; }
        }
        int i = 0;
        while (i < rows) {
            if (profile[i] == Integer.MIN_VALUE) {
                i++;
                continue;
            }
            int end = i;
            while (end + 1 < rows && profile[end + 1] != Integer.MIN_VALUE) { end++; }
            int[] leftMax = new int[end - i + 1];
            int running = Integer.MIN_VALUE;
            for (int k = i; k <= end; k++) {
                running = Math.max(running, profile[k]);
                leftMax[k - i] = running;
            }
            running = Integer.MIN_VALUE;
            int[] rightMax = new int[end - i + 1];
            for (int k = end; k >= i; k--) {
                running = Math.max(running, profile[k]);
                rightMax[k - i] = running;
            }
            int low = -1;
            for (int k = i; k <= end + 1; k++) {
                boolean sunken = k <= end && Math.min(leftMax[k - i], rightMax[k - i]) > profile[k];
                if (sunken && low < 0) { low = k; }
                if (!sunken && low >= 0) {
                    if (k - low <= 12) {
                        for (int fillAt = low; fillAt < k; fillAt++) {
                            int fill = Math.min(leftMax[fillAt - i], rightMax[fillAt - i]);
                            if (!held[fillAt] && fill > profile[fillAt]) { profile[fillAt] = fill; }
                        }
                    }
                    low = -1;
                }
            }
            int[] leftFloor = new int[end - i + 1];
            running = Integer.MAX_VALUE;
            for (int k = i; k <= end; k++) {
                running = Math.min(running, profile[k]);
                leftFloor[k - i] = running;
            }
            running = Integer.MAX_VALUE;
            int[] rightFloor = new int[end - i + 1];
            for (int k = end; k >= i; k--) {
                running = Math.min(running, profile[k]);
                rightFloor[k - i] = running;
            }
            int high = -1;
            for (int k = i; k <= end + 1; k++) {
                boolean raised = k <= end && Math.max(leftFloor[k - i], rightFloor[k - i]) < profile[k];
                if (raised && high < 0) { high = k; }
                if (!raised && high >= 0) {
                    if (k - high <= 12) {
                        for (int cutAt = high; cutAt < k; cutAt++) {
                            int cut = Math.max(leftFloor[cutAt - i], rightFloor[cutAt - i]);
                            if (!held[cutAt] && cut < profile[cutAt]) { profile[cutAt] = cut; }
                        }
                    }
                    high = -1;
                }
            }
            i = end + 1;
        }
        for (int i2 = 1; i2 < rows; i2++) { if (!held[i2] && joined(profile, i2) && profile[i2] > profile[i2 - 1] + 1) { profile[i2] = profile[i2 - 1] + 1; } }
        for (int i2 = rows - 2; i2 >= 0; i2--) { if (!held[i2] && joined(profile, i2 + 1) && profile[i2] > profile[i2 + 1] + 1) { profile[i2] = profile[i2 + 1] + 1; } }
        for (int i2 = 1; i2 < rows; i2++) { if (!held[i2] && joined(profile, i2) && profile[i2] < profile[i2 - 1] - 1) { profile[i2] = profile[i2 - 1] - 1; } }
        for (int i2 = rows - 2; i2 >= 0; i2--) { if (!held[i2] && joined(profile, i2 + 1) && profile[i2] < profile[i2 + 1] - 1) { profile[i2] = profile[i2 + 1] - 1; } }
        for (int i2 = 1; i2 < rows - 1; i2++) {
            if (held[i2] || !joined(profile, i2) || profile[i2 + 1] == Integer.MIN_VALUE) { continue; }
            if (profile[i2 - 1] == profile[i2 + 1] && Math.abs(profile[i2] - profile[i2 - 1]) == 1) { profile[i2] = profile[i2 - 1]; }
        }
    }

    public static int noiseAverage(World world, StructureBoundingBox box) {
        if (samplerFor(world) == null) { return Integer.MIN_VALUE; }

        long total = 0;
        int count = 0;
        for (int z = box.minZ; z <= box.maxZ; z++) {
            for (int x = box.minX; x <= box.maxX; x++) {
                int sampled = surfaceAt(world, x, z);
                if (sampled < 0) { continue; }

                total += sampled + 1;
                count++;
            }
        }
        return count == 0 ? Integer.MIN_VALUE : (int) (total / count);
    }

    @Nullable public static IBlockState dressSurface(World world, StructureComponent piece, boolean alongX, int row, int across, int acrossCenter, IBlockState path) {
        int span = 3 + 2 * (pathExtraWidth() + pathLineColumns() + pathSidewalkWidth());
        StructureBoundingBox box = piece.getBoundingBox();
        if ((alongX ? box.maxZ - box.minZ : box.maxX - box.minX) + 1 != span || span == 3) { return null; }

        int offset = Math.abs(across - acrossCenter);
        int core = 1 + pathExtraWidth();
        char role = offset <= core ? (offset == 0 ? 'c' : 'r') : offset <= core + pathLineColumns() ? 'l' : 's';
        IBlockState stamped = stampAt(world, piece, alongX, row, across, acrossCenter, core, path);
        if (stamped != null) { return stamped; }
        if (role != 'r' && insideMouth(piece, alongX, row, across, acrossCenter)) { return path; }
        if (role == 'c') {
            IBlockState center = pathBlock("villagePathCenterBlock", Config.worldgen.villagePathCenterBlock, path);
            if (center == path) { return path; }

            int dash = Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villagePathCenterDash", Config.worldgen.villagePathCenterDash));
            if (dash > 0 && Math.floorMod(row, dash + 1) == dash) { return path; }

            return axised(center, alongX);
        }
        if (role == 'l') { return axised(pathBlock("villagePathLineBlock", Config.worldgen.villagePathLineBlock, path), alongX); }
        if (role == 's') { return pathBlock("villagePathSidewalkBlock", Config.worldgen.villagePathSidewalkBlock, path); }
        return path;
    }

    private static boolean insideMouth(StructureComponent piece, boolean alongX, int row, int across, int acrossCenter) {
        StructureStart building = current();
        if (building == null) { return false; }

        boolean outward = across > acrossCenter;
        for (StructureComponent other : building.getComponents()) {
            if (other == piece || !(other instanceof StructureVillagePieces.Path)) { continue; }

            StructureBoundingBox road = other.getBoundingBox();
            boolean otherAlongX = road.maxX - road.minX >= road.maxZ - road.minZ;
            if (otherAlongX == alongX) { continue; }

            int otherCore = 1 + pathExtraWidth();
            int otherCenter = alongX ? (road.minX + road.maxX) / 2 : (road.minZ + road.maxZ) / 2;
            if (row < otherCenter - otherCore || row > otherCenter + otherCore) { continue; }

            int edge = alongX ? (outward ? road.minZ : road.maxZ) : (outward ? road.minX : road.maxX);
            int gap = outward ? edge - across : across - edge;
            if (gap >= -1 && gap <= 2 + pathLineColumns() + pathSidewalkWidth()) { return true; }
        }
        return false;
    }

    @Nullable private static IBlockState stampAt(World world, StructureComponent piece, boolean alongX, int row, int across, int acrossCenter, int core, IBlockState path) {
        StructureStart building = current();
        if (building == null || Math.abs(across - acrossCenter) > core) { return null; }

        for (StructureComponent other : building.getComponents()) {
            if (other == piece || !(other instanceof StructureVillagePieces.Path)) { continue; }

            StructureBoundingBox road = other.getBoundingBox();
            boolean otherAlongX = road.maxX - road.minX >= road.maxZ - road.minZ;
            if (otherAlongX == alongX) { continue; }

            int otherCenter = alongX ? (road.minX + road.maxX) / 2 : (road.minZ + road.maxZ) / 2;
            PathIntersectDef def = ContentPathIntersects.forJunction(world, alongX ? otherCenter : acrossCenter, alongX ? acrossCenter : otherCenter);
            if (def == null) { continue; }

            int otherCore = 1 + pathExtraWidth();
            int before = otherCenter - otherCore - 1;
            int after = otherCenter + otherCore + 1;
            IBlockState fromMouth = mouthCell(def, row, across, acrossCenter, core, before, after, path);
            if (fromMouth != null) { return fromMouth; }

            IBlockState fromCorner = cornerCell(def, row, across, acrossCenter, core, otherCenter, otherCore, path);
            if (fromCorner != null) { return fromCorner; }
        }
        return null;
    }

    @Nullable private static IBlockState mouthCell(PathIntersectDef def, int row, int across, int acrossCenter, int core, int before, int after, IBlockState path) {
        if (def.mouth.length == 0) { return null; }

        int line = -1;
        if (row <= before && row > before - def.mouth.length) { line = before - row; }
        if (row >= after && row < after + def.mouth.length) { line = row - after; }
        if (line < 0) { return null; }

        String cells = def.mouth[line];
        if (cells.isEmpty()) { return null; }

        return cellState(def, cells.charAt(Math.floorMod(across - (acrossCenter - core), cells.length())), path);
    }

    @Nullable private static IBlockState cornerCell(PathIntersectDef def, int row, int across, int acrossCenter, int core, int otherCenter, int otherCore, IBlockState path) {
        if (def.corner.length == 0 || row < otherCenter - otherCore || row > otherCenter + otherCore) { return null; }

        int fromRowEdge = Math.min(row - (otherCenter - otherCore), (otherCenter + otherCore) - row);
        int fromColEdge = core - Math.abs(across - acrossCenter);
        if (fromRowEdge >= def.corner.length) { return null; }

        String cells = def.corner[fromRowEdge];
        if (fromColEdge >= cells.length()) { return null; }

        return cellState(def, cells.charAt(fromColEdge), path);
    }

    @Nullable private static IBlockState cellState(PathIntersectDef def, char cell, IBlockState path) {
        if (cell == '.') { return null; }
        if (cell == 'r' || cell == 'c') { return path; }
        if (cell == 'l') { return pathBlock("villagePathLineBlock", Config.worldgen.villagePathLineBlock, path); }
        if (cell == 's') { return pathBlock("villagePathSidewalkBlock", Config.worldgen.villagePathSidewalkBlock, path); }
        return def.legend.getOrDefault(cell, path);
    }

    @SuppressWarnings("unchecked") private static IBlockState axised(IBlockState state, boolean alongX) {
        for (net.minecraft.block.properties.IProperty<?> property : state.getPropertyKeys()) {
            if ("axis".equals(property.getName()) && property.getValueClass() == net.minecraft.util.EnumFacing.Axis.class) {
                return state.withProperty((net.minecraft.block.properties.IProperty<net.minecraft.util.EnumFacing.Axis>) property, alongX ? net.minecraft.util.EnumFacing.Axis.X : net.minecraft.util.EnumFacing.Axis.Z);
            }
        }
        return state;
    }

    private static boolean roadAlongX(StructureComponent piece) {
        EnumFacing facing = piece.getCoordBaseMode();
        if (facing != null) { return facing.getAxis() == EnumFacing.Axis.X; }

        StructureBoundingBox box = piece.getBoundingBox();
        return box.maxX - box.minX >= box.maxZ - box.minZ;
    }

    private static int chainGradeAt(World world, StructureComponent road, boolean alongX, int row) {
        StructureBoundingBox box = road.getBoundingBox();
        int least = alongX ? box.minX : box.minZ;
        int most = alongX ? box.maxX : box.maxZ;
        int[] profile = noiseProfile(world, alongX, least, most, alongX ? box.minZ : box.minX, alongX ? box.maxZ : box.maxX);
        if (profile == null) { return Integer.MIN_VALUE; }

        int[] ground = profile.clone();
        flatRuns(world, alongX, least, alongX ? box.minZ : box.minX, alongX ? box.maxZ : box.maxX, profile);
        boolean[] bridged = smooth(profile);
        capEmbankment(profile, ground, bridged, new boolean[profile.length]);
        int clamped = Math.max(least, Math.min(most, row));
        return profile[clamped - least];
    }

    public static int roadGradeBeside(World world, StructureBoundingBox box) {
        StructureStart building = current();
        if (building == null) { return Integer.MIN_VALUE; }

        for (StructureComponent other : building.getComponents()) {
            if (!(other instanceof StructureVillagePieces.Path)) { continue; }

            StructureBoundingBox road = other.getBoundingBox();
            int gap = Math.max(Math.max(road.minX - box.maxX, box.minX - road.maxX), Math.max(road.minZ - box.maxZ, box.minZ - road.maxZ));
            if (gap > 2) { continue; }

            boolean alongX = roadAlongX(other);
            int start = alongX ? road.minX : road.minZ;
            int[] profile = noiseProfile(world, alongX, start, alongX ? road.maxX : road.maxZ, alongX ? road.minZ : road.minX, alongX ? road.maxZ : road.maxX);
            if (profile == null) { return Integer.MIN_VALUE; }

            int[] ground = profile.clone();
            flatRuns(world, alongX, start, alongX ? road.minZ : road.minX, alongX ? road.maxZ : road.maxX, profile);
            boolean[] bridged = smooth(profile);
            boolean[] held = new boolean[profile.length];
            boolean[] plaza = new boolean[profile.length];
            roadApron(world, other, alongX, start, profile, held);
            clampToWell(alongX, start, alongX ? road.minZ : road.minX, alongX ? road.maxZ : road.maxX, profile, plaza);
            for (int i = 0; i < held.length; i++) { if (plaza[i]) { held[i] = true; } }
            settle(profile, held);
            capEmbankment(profile, ground, bridged, plaza);
            int center = alongX ? (box.minX + box.maxX) / 2 : (box.minZ + box.maxZ) / 2;
            int row = Math.max(start, Math.min(start + profile.length - 1, center));
            if (profile[row - start] == Integer.MIN_VALUE) { continue; }

            return profile[row - start] + 1;
        }
        return Integer.MIN_VALUE;
    }

    private static int deckBridge(World world, boolean alongX, int row, int across, int acrossLeast, int acrossMost, int deckAt, IBlockState planks, BlockPos.MutableBlockPos at) {
        int deckY = deckAt;
        for (int lift = 0; lift < 8 && world.getBlockState(at.setPos(alongX ? row : across, deckY, alongX ? across : row)).getMaterial().isLiquid(); lift++) { deckY++; }
        at.setPos(alongX ? row : across, deckY, alongX ? across : row);
        if (world.getBlockState(at).getMaterial().isSolid()) { return 0; }

        int span = acrossMost - acrossLeast + 1;
        int laid = 0;
        IBlockState deck = planks;
        if (span > 3) {
            int center = (acrossLeast + acrossMost) / 2;
            int offset = Math.abs(across - center);
            if (offset > 1 + pathExtraWidth() + pathLineColumns()) {
                IBlockState walk = pathBlock("villagePathSidewalkBlock", Config.worldgen.villagePathSidewalkBlock, planks);
                deck = pathBlock("villagePathBridgeSidewalkBlock", Config.worldgen.villagePathBridgeSidewalkBlock, walk);
            }
            if (offset == (span - 1) / 2) {
                IBlockState barrier = pathBlock("villagePathBridgeBarrierBlock", Config.worldgen.villagePathBridgeBarrierBlock, planks);
                if (barrier != planks) {
                    int height = Math.max(1, ContentControl.number(ContentControl.VILLAGES, "villagePathBridgeBarrierHeight", Config.worldgen.villagePathBridgeBarrierHeight));
                    for (int y = deckY + 1; y <= deckY + height; y++) {
                        at.setPos(alongX ? row : across, y, alongX ? across : row);
                        if (world.getBlockState(at).getMaterial().isSolid()) { break; }

                        world.setBlockState(at, barrier, 2);
                        laid++;
                    }
                    at.setPos(alongX ? row : across, deckY, alongX ? across : row);
                }
            }
        }
        world.setBlockState(at, deck, 2);
        return laid + 1;
    }

    public static int plazaReach() { return 3 + (pathFullWidth() - 3) / 2; }

    public static void wellPlaza(StructureStart start, StructureComponent piece, World world, StructureBoundingBox clip) {
        StructureBoundingBox box = piece.getBoundingBox();
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        int reach = plazaReach();
        int ground = box.maxY - 3;
        int walk = pathSidewalkWidth();
        int lines = pathLineColumns();
        boolean chosen = pathChosen();
        IBlockState surface = pathBlock("villagePathBlock", Config.worldgen.villagePathBlock, Blocks.GRASS_PATH.getDefaultState());
        IBlockState line = pathBlock("villagePathLineBlock", Config.worldgen.villagePathLineBlock, surface);
        IBlockState sidewalk = pathBlock("villagePathSidewalkBlock", Config.worldgen.villagePathSidewalkBlock, surface);
        int paved = 0;
        for (int x = box.minX - reach; x <= box.maxX + reach; x++) {
            for (int z = box.minZ - reach; z <= box.maxZ + reach; z++) {
                int band = Math.max(Math.max(box.minX - x, x - box.maxX), Math.max(box.minZ - z, z - box.maxZ));
                if (band < 1) { continue; }

                at.setPos(x, ground, z);
                if (!clip.isVecInside(at) || underBuilding(start, piece, x, z)) { continue; }
                if (world.getBlockState(world.getTopSolidOrLiquidBlock(at).down()).getMaterial().isLiquid()) { continue; }

                for (int y = ground + 1; y <= ground + 4; y++) {
                    at.setPos(x, y, z);
                    IBlockState above = world.getBlockState(at);
                    if (above.getBlock() == Blocks.AIR) { continue; }
                    if (!terrainBlock(above.getBlock()) && above.getMaterial().isSolid()) { break; }

                    world.setBlockState(at, Blocks.AIR.getDefaultState(), 2);
                }
                for (int y = ground - 1; y >= ground - 8; y--) {
                    at.setPos(x, y, z);
                    if (world.getBlockState(at).getMaterial().isSolid()) { break; }

                    world.setBlockState(at, fillGround(world, x, z), 2);
                }
                at.setPos(x, ground, z);
                IBlockState natural = chosen ? surface : pathForGround(world, x, z, surface, Blocks.GRAVEL.getDefaultState(), true);
                IBlockState held = band > reach - walk ? sidewalk : lines > 0 && band == reach - walk ? line : natural;
                if (held != natural && roadCore(start, piece, x, z)) { held = natural; }
                if (!chosen) { held = natural; }
                world.setBlockState(at, held, 2);
                paved++;
            }
        }
        if (paved > 0 && ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("Paved a plaza of {} column(s) around the well at {}, {}, reaching {} out from it", paved, box.minX, box.minZ, reach); }
    }

    private static void flatRuns(World world, boolean alongX, int start, int acrossLeast, int acrossMost, int[] profile) {
        int run = Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villagePathFlatRun", Config.worldgen.villagePathFlatRun));
        if (run <= 1) { return; }

        for (int i = 0; i < profile.length; i++) {
            if (profile[i] == Integer.MIN_VALUE) { continue; }

            int grid = (start + i) - Math.floorMod(start + i, run);
            int center = (acrossLeast + acrossMost) / 2;
            int[] taken = new int[3];
            int count = 0;
            for (int across = center - 1; across <= center + 1; across++) {
                int found = surfaceAt(world, alongX ? grid : across, alongX ? across : grid);
                if (found < world.getSeaLevel() - 1) { continue; }
                taken[count++] = found;
            }
            if (count > 0) {
                Arrays.sort(taken, 0, count);
                profile[i] = taken[count / 2];
            }
        }
    }

    private static void roadApron(World world, StructureComponent piece, boolean alongX, int start, int[] profile, boolean[] held) {
        StructureStart building = current();
        if (building == null) { return; }

        for (StructureComponent other : building.getComponents()) {
            if (other == piece || !(other instanceof StructureVillagePieces.Path)) { continue; }

            StructureBoundingBox road = other.getBoundingBox();
            boolean otherAlongX = roadAlongX(other);
            if (otherAlongX == alongX) { continue; }

            StructureBoundingBox own = piece.getBoundingBox();
            if (road.minX - 1 > own.maxX || own.minX - 1 > road.maxX || road.minZ - 1 > own.maxZ || own.minZ - 1 > road.maxZ) { continue; }

            int center = alongX ? (road.minX + road.maxX) / 2 : (road.minZ + road.maxZ) / 2;
            int anchorRow = Math.max(start, Math.min(start + profile.length - 1, center));
            int grade = profile[anchorRow - start];
            if (grade == Integer.MIN_VALUE) { continue; }

            int crossRow = otherAlongX ? (own.minX + own.maxX) / 2 : (own.minZ + own.maxZ) / 2;
            int crossed = chainGradeAt(world, other, otherAlongX, crossRow);
            if (crossed > grade) { grade = crossed; }

            int reach = 1 + pathExtraWidth() + 3;
            for (int row = center - reach; row <= center + reach; row++) {
                if (row < start || row > start + profile.length - 1) { continue; }
                if (profile[row - start] != Integer.MIN_VALUE) {
                    profile[row - start] = grade;
                    held[row - start] = true;
                }
            }
        }
    }

    private static boolean underRoad(StructureStart start, StructureComponent piece, int x, int z) {
        for (StructureComponent other : start.getComponents()) {
            if (other == piece || !(other instanceof StructureVillagePieces.Path)) { continue; }

            StructureBoundingBox box = other.getBoundingBox();
            if (x >= box.minX && x <= box.maxX && z >= box.minZ && z <= box.maxZ) { return true; }
        }
        return false;
    }

    private static boolean roadCore(StructureStart start, StructureComponent piece, int x, int z) {
        for (StructureComponent other : start.getComponents()) {
            if (other == piece || !(other instanceof StructureVillagePieces.Path)) { continue; }

            StructureBoundingBox box = other.getBoundingBox();
            if (x < box.minX || x > box.maxX || z < box.minZ || z > box.maxZ) { continue; }

            boolean alongX = roadAlongX(other);
            int center = alongX ? (box.minZ + box.maxZ) / 2 : (box.minX + box.maxX) / 2;
            int offset = Math.abs((alongX ? z : x) - center);
            int span = (alongX ? box.maxZ - box.minZ : box.maxX - box.minX) + 1;
            if (offset <= Math.min(1 + pathExtraWidth(), (span - 1) / 2)) { return true; }
        }
        return false;
    }

    private static boolean underBuilding(StructureStart start, StructureComponent piece, int x, int z) {
        for (StructureComponent other : start.getComponents()) {
            if (other == piece || other instanceof StructureVillagePieces.Path) { continue; }

            StructureBoundingBox box = other.getBoundingBox();
            if (x >= box.minX && x <= box.maxX && z >= box.minZ && z <= box.maxZ) { return true; }
        }
        return false;
    }

    private static boolean underAnother(StructureStart start, StructureComponent piece, int x, int z) {
        for (StructureComponent other : start.getComponents()) {
            if (other == piece) { continue; }

            StructureBoundingBox box = other.getBoundingBox();
            if (x >= box.minX && x <= box.maxX && z >= box.minZ && z <= box.maxZ) { return true; }
        }
        return false;
    }

    private static void clampToWell(boolean alongX, int start, int acrossLeast, int acrossMost, int[] profile, boolean[] held) {
        StructureStart building = current();
        if (building == null || building.getComponents().isEmpty()) { return; }

        StructureBoundingBox well = building.getComponents().get(0).getBoundingBox();
        int reach = plazaReach();
        if (acrossMost < (alongX ? well.minZ : well.minX) - reach || acrossLeast > (alongX ? well.maxZ : well.maxX) + reach) { return; }

        int ground = well.maxY - 3;
        int rowLeast = (alongX ? well.minX : well.minZ) - reach;
        int rowMost = (alongX ? well.maxX : well.maxZ) + reach;
        int clamped = 0;
        for (int row = Math.max(start, rowLeast); row <= Math.min(start + profile.length - 1, rowMost); row++) {
            if (profile[row - start] == Integer.MIN_VALUE) { continue; }

            held[row - start] = true;
            if (profile[row - start] == ground) { continue; }

            profile[row - start] = ground;
            clamped++;
        }
        if (clamped > 0 && ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("Clamped {} road row(s) beside the well to its ground at y {}", clamped, ground); }
    }

    private static int roadAnchor(World world, boolean alongX, int row, int acrossLeast, int acrossMost, IBlockState path, IBlockState gravel) {
        for (int across = acrossLeast; across <= acrossMost; across++) {
            int x = alongX ? row : across;
            int z = alongX ? across : row;
            BlockPos spot = new BlockPos(x, 64, z);
            if (!world.isBlockLoaded(spot)) { continue; }

            IBlockState held = world.getBlockState(world.getTopSolidOrLiquidBlock(spot).down());
            if (held == path || held == gravel) { return world.getTopSolidOrLiquidBlock(spot).down().getY(); }
        }
        return Integer.MIN_VALUE;
    }

    private static boolean joined(int[] profile, int i) { return profile[i] != Integer.MIN_VALUE && profile[i - 1] != Integer.MIN_VALUE; }

    private static boolean clearable(IBlockState held) {
        Block block = held.getBlock();
        if (block == Blocks.AIR) { return false; }
        if (block == Blocks.STONE && !held.getValue(BlockStone.VARIANT).isNatural()) { return false; }

        return terrainBlock(block) || held.getMaterial() == Material.VINE || held.getMaterial() == Material.PLANTS;
    }

    private static IBlockState pathForGround(World world, int x, int z, IBlockState path, IBlockState gravel, boolean earthy) {
        Block ground = fillGround(world, x, z).getBlock();
        if (ground == Blocks.SAND) { return Blocks.SANDSTONE.getDefaultState(); }
        if (ground == Blocks.HARDENED_CLAY) { return Blocks.HARDENED_CLAY.getDefaultState(); }
        if (ground == Blocks.GRAVEL) { return Blocks.GRAVEL.getDefaultState(); }

        return earthy ? path : gravel;
    }

    private static IBlockState fillGround(World world, int x, int z) {
        Biome biome = world.getBiome(new BlockPos(x, 64, z));
        Block top = biome.topBlock.getBlock();
        Block filler = biome.fillerBlock.getBlock();
        if (top == Blocks.HARDENED_CLAY || top == Blocks.STAINED_HARDENED_CLAY || filler == Blocks.HARDENED_CLAY || filler == Blocks.STAINED_HARDENED_CLAY) { return Blocks.HARDENED_CLAY.getDefaultState(); }
        if (top == Blocks.SAND || filler == Blocks.SAND) { return Blocks.SAND.getDefaultState(); }
        if (top == Blocks.GRAVEL) { return Blocks.GRAVEL.getDefaultState(); }

        return Blocks.DIRT.getDefaultState();
    }

    private static boolean sandBiome(World world, int x, int z) {
        Biome biome = world.getBiome(new BlockPos(x, 64, z));
        return biome.topBlock.getBlock() == Blocks.SAND;
    }

    private static boolean terrainBlock(Block held) {
        return held == Blocks.STONE || held == Blocks.DIRT || held == Blocks.GRASS || held == Blocks.GRAVEL || held == Blocks.SAND
                || held == Blocks.CLAY || held == Blocks.SNOW_LAYER || held == Blocks.SNOW || held == Blocks.ICE || held == Blocks.PACKED_ICE;
    }

    private static boolean insideAnother(StructureStart start, StructureComponent piece, BlockPos at) {
        for (StructureComponent other : start.getComponents()) {
            if (other == piece || other instanceof StructureVillagePieces.Path) { continue; }

            StructureBoundingBox box = other.getBoundingBox();
            if (box.isVecInside(at)) { return true; }
            if (other instanceof StructureVillagePieces.Well && at.getY() == box.maxY + 1 && at.getX() >= box.minX && at.getX() <= box.maxX && at.getZ() >= box.minZ && at.getZ() <= box.maxZ) { return true; }
        }
        return false;
    }

    public static boolean wanted() {
        WorldTemplateDef active = ContentWorldTemplates.active();
        if (!wantedKnown || active != wantedFrom) {
            wantedHeld = ContentControl.flag(ContentControl.STRUCTURES, "terrainAdaptation", Config.worldgen.terrainAdaptation);
            wantedFrom = active;
            wantedKnown = true;
        }
        return wantedHeld;
    }

    public static void apply(World world, ChunkGeneratorOverworld generator, MapGenStructure[] generators, String[] names, double[] heightMap, int chunkX, int chunkZ) {
        if (applying) { return; }

        applying = true;
        try { seat(world, generator, generators, names, heightMap, chunkX, chunkZ); }
        finally { applying = false; }
    }

    private static void seat(World world, ChunkGeneratorOverworld generator, MapGenStructure[] generators, String[] names, double[] heightMap, int chunkX, int chunkZ) {
        loadModes();
        double[] before = heightMap.clone();
        int blockX = chunkX << 4;
        int blockZ = chunkZ << 4;
        StructureBoundingBox reach = new StructureBoundingBox(blockX - RADIUS, 0, blockZ - RADIUS, blockX + 15 + RADIUS, 255, blockZ + 15 + RADIUS);
        List<StructureBoundingBox> boxes = new ArrayList<>();
        List<Integer> bases = new ArrayList<>();
        List<Mode> modes = new ArrayList<>();
        for (int at = 0; at < generators.length; at++) {
            Mode mode = MODES.getOrDefault(names[at], Mode.NONE);
            if (mode == Mode.NONE) { continue; }

            sampler = generator;
            samplerWorld = world;
            generators[at].generate(world, chunkX, chunkZ, UNUSED);
            sampler = null;
            samplerWorld = null;
            for (StructureStart start : ((AccessorMapGenStructure) generators[at]).rdpl$getStructureMap().values()) {
                if (start == null || !start.isSizeableStructure() || !start.getBoundingBox().intersectsWith(reach)) { continue; }
                for (StructureComponent piece : start.getComponents()) {
                    if (!piece.getBoundingBox().intersectsWith(reach)) { continue; }

                    if (piece instanceof StructureVillagePieces.Start) { continue; }

                    boxes.add(piece.getBoundingBox());
                    bases.add(piece.getBoundingBox().minY + 1);
                    modes.add(mode);
                }
            }
        }
        System.arraycopy(before, 0, heightMap, 0, heightMap.length);
        if (boxes.isEmpty()) { return; }

        if (ContentLog.LOGGER.debugEnabled()) {
            int lowest = Integer.MAX_VALUE;
            int highest = Integer.MIN_VALUE;
            for (Integer base : bases) {
                lowest = Math.min(lowest, base);
                highest = Math.max(highest, base);
            }
            ContentLog.LOGGER.debug("Seating {} structure piece(s) under chunk {}, {}, on bases from y {} to y {}", boxes.size(), chunkX, chunkZ, lowest, highest);
        }

        int at = 0;
        for (int gridX = 0; gridX < 5; gridX++) {
            int x = blockX + gridX * 4;
            for (int gridZ = 0; gridZ < 5; gridZ++) {
                int z = blockZ + gridZ * 4;
                for (int gridY = 0; gridY < 33; gridY++) {
                    heightMap[at] += sink(boxes, bases, modes, x, gridY * 8, z) * SCALE;
                    at++;
                }
            }
        }
    }

    private static double sink(List<StructureBoundingBox> boxes, List<Integer> bases, List<Mode> modes, int x, int y, int z) {
        double sum = 0.0D;
        for (int i = 0; i < boxes.size(); i++) {
            StructureBoundingBox box = boxes.get(i);
            int dx = Math.max(0, Math.max(box.minX - x, x - box.maxX));
            int dz = Math.max(0, Math.max(box.minZ - z, z - box.maxZ));
            if (dx > RADIUS || dz > RADIUS) { continue; }

            int base = bases.get(i);
            int dy = y - base;
            Mode mode = modes.get(i);
            if (mode == Mode.BURY) { sum += bury(dx, dy / 2.0D, dz); }
            else if (mode == Mode.BEARD_THIN) { sum += beard(dx, dy, dz, dy) * 0.8D; }
            else if (mode == Mode.BEARD_BOX) { sum += beard(dx, Math.max(0, Math.max(base - y, y - box.maxY)), dz, dy) * 0.8D; }
            else if (mode == Mode.ENCAPSULATE) { sum += bury(dx / 2.0D, Math.max(0, Math.max(box.minY - y, y - box.maxY)) / 2.0D, dz / 2.0D) * 0.8D; }
        }
        return sum;
    }

    private static double bury(double x, double y, double z) {
        double length = Math.sqrt(x * x + y * y + z * z);
        if (length >= 6.0D) { return 0.0D; }

        return 1.0D - length / 6.0D;
    }

    private static void loadModes() {
        if (modesLoaded) { return; }

        modesLoaded = true;
        MODES.put("villages", Mode.BEARD_THIN);
        for (String entry : ContentControl.list(ContentControl.STRUCTURES, "structureAdaptation", Config.worldgen.structureAdaptation)) {
            String[] parts = entry.split("=", 2);
            if (parts.length != 2) {
                ContentLog.LOGGER.error("structureAdaptation entry '{}' is not structure=mode, ignoring it", entry);
                continue;
            }

            String name = parts[0].trim().toLowerCase(Locale.ROOT);
            String asked = parts[1].trim().toUpperCase(Locale.ROOT);
            if ("temples".equals(name)) {
                ContentLog.LOGGER.error("structureAdaptation names temples, but they place themselves only as they are built, so terrain cannot adapt to them yet and the entry is ignored");
                continue;
            }
            try { MODES.put(name, Mode.valueOf(asked)); }
            catch (IllegalArgumentException ex) { ContentLog.LOGGER.error("structureAdaptation entry '{}' asks for mode '{}', which is not none, bury, beard_thin, beard_box or encapsulate, ignoring it", entry, parts[1].trim()); }
        }
    }

    private static double beard(int x, int y, int z, int height) {
        int atX = x + RADIUS;
        int atY = y + RADIUS;
        int atZ = z + RADIUS;
        if (atX < 0 || atX >= SIZE || atY < 0 || atY >= SIZE || atZ < 0 || atZ >= SIZE) { return 0.0D; }

        double lifted = (double) height + 0.5D;
        double squared = (double) x * (double) x + lifted * lifted + (double) z * (double) z;
        double falloff = -lifted * MathHelper.fastInvSqrt(squared / 2.0D) / 2.0D;
        return falloff * (double) KERNEL[atZ * SIZE * SIZE + atX * SIZE + atY];
    }

    private static double seed(int x, double y, int z) {
        double squared = (double) x * (double) x + y * y + (double) z * (double) z;
        return Math.pow(Math.E, -squared / 16.0D);
    }
}
