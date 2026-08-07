package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.def.WorldTemplateDef;
import mctmods.resourcedatapackloader.mixin.*;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockFence;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
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
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
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
    private static boolean layingRoad;
    private static int peeks;
    private static final Map<World, ChunkGeneratorOverworld> SAMPLERS = new WeakHashMap<>();
    private static final Map<World, Map<Long, Integer>> TOPS = new WeakHashMap<>();
    private static WorldTemplateDef wantedFrom;
    private static boolean wantedHeld;
    private static boolean wantedKnown;
    private static final int SITE_REACH = 2;
    private static final int SITE_TOLERANCE = 16;
    private static final int SITE_SEPARATION = 8;
    private static final int FOOTING_STEP = 4;
    private static final int FOOTING_TOLERANCE = 3;
    private static final long NO_SITE = Long.MIN_VALUE;
    private static final ChunkPrimer UNUSED = new ChunkPrimer();
    private static ChunkGeneratorOverworld sampler;
    private static World samplerWorld;

    private ContentBeard() {}

    public static void layingBuilding(boolean now) { layingBuilding = now; }

    public static void layingRoad(boolean now) { layingRoad = now; }

    public static boolean seatingVillage() { return (layingBuilding || layingRoad) && wanted() && samplerWorld != null; }

    public static boolean roughFooting(StructureBoundingBox box) {
        World world = samplerWorld;
        if (world == null) { return false; }

        int[] alongX = samplesAcross(box.minX, box.maxX);
        int[] alongZ = samplesAcross(box.minZ, box.maxZ);
        int lowest = Integer.MAX_VALUE;
        int highest = Integer.MIN_VALUE;
        for (int x : alongX) {
            for (int z : alongZ) {
                int sampled = surfaceAt(world, x, z);
                if (sampled < 0) { return false; }
                if (sampled < lowest) { lowest = sampled; }
                if (sampled > highest) { highest = sampled; }
            }
        }
        return highest - lowest > FOOTING_TOLERANCE;
    }

    private static int[] samplesAcross(int least, int most) {
        int span = most - least;
        int count = span / FOOTING_STEP + (span % FOOTING_STEP == 0 ? 1 : 2);
        int[] out = new int[count];
        for (int i = 0; i < count; i++) { out[i] = Math.min(least + i * FOOTING_STEP, most); }
        return out;
    }

    public static int lowestIn(World worldIn, int minX, int minZ, int maxX, int maxZ, StructureBoundingBox clip) {
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        int floor = worldIn.provider.getAverageGroundLevel() - 1;
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
        int[] heights = new int[span * span];
        Arrays.fill(heights, Integer.MIN_VALUE);
        long chosen = NO_SITE;
        int bestSpread = Integer.MAX_VALUE;
        int bestPull = Integer.MAX_VALUE;
        for (int x = margin; x < spacing - margin; x++) {
            for (int z = margin; z < spacing - margin; z++) {
                if (!MapGenVillage.VILLAGE_SPAWN_BIOMES.contains(region[(x + SITE_REACH) * 4 + 2 + ((z + SITE_REACH) * 4 + 2) * size])) { continue; }

                int lowest = Integer.MAX_VALUE;
                int highest = Integer.MIN_VALUE;
                for (int dx = 0; dx <= SITE_REACH * 2; dx++) {
                    for (int dz = 0; dz <= SITE_REACH * 2; dz++) {
                        int at = (x + dx) * span + z + dz;
                        int sampledHeight = heights[at];
                        if (sampledHeight == Integer.MIN_VALUE) {
                            sampledHeight = surfaceAt(world, sampled, region, originX, originZ, size, (baseX + x + dx - SITE_REACH) * 16 + 8, (baseZ + z + dz - SITE_REACH) * 16 + 8);
                            heights[at] = sampledHeight;
                        }
                        if (sampledHeight < 0) {
                            lowest = Integer.MAX_VALUE;
                            highest = Integer.MIN_VALUE;
                            dx = SITE_REACH * 2;
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

                int felled = fellAround(event.getWorld(), start, piece, box, clip, at);
                if (felled > 0) { ContentLog.LOGGER.debug("Felled {} tree block(s) crowding {} at {}, {}", felled, piece.getClass().getSimpleName(), box.minX, box.minZ); }
            }
        }
    }

    private static int fellAround(World world, StructureStart start, StructureComponent piece, StructureBoundingBox box, StructureBoundingBox clip, BlockPos.MutableBlockPos at) {
        List<BlockPos> trunk = new ArrayList<>();
        ArrayDeque<BlockPos> spreading = new ArrayDeque<>();
        int felled = 0;
        for (int x = box.minX - 2; x <= box.maxX + 2; x++) {
            for (int z = box.minZ - 2; z <= box.maxZ + 2; z++) {
                for (int y = box.minY + 1; y <= box.maxY + 8; y++) {
                    if (x >= box.minX && x <= box.maxX && z >= box.minZ && z <= box.maxZ && y <= box.maxY) { continue; }

                    at.setPos(x, y, z);
                    if (!clip.isVecInside(at) || insideAnother(start, piece, at)) { continue; }

                    Material material = world.getBlockState(at).getMaterial();
                    if (material == Material.WOOD) { spreading.add(at.toImmutable()); }
                    else if (material == Material.VINE) { felled += clearAt(world, at); }
                }
            }
        }
        while (!spreading.isEmpty() && trunk.size() < 256) {
            BlockPos log = spreading.poll();
            if (!clip.isVecInside(log) || trunk.contains(log) || insideAnother(start, piece, log)) { continue; }
            if (log.getX() >= box.minX && log.getX() <= box.maxX && log.getZ() >= box.minZ && log.getZ() <= box.maxZ && log.getY() <= box.maxY) { continue; }
            if (world.getBlockState(log).getMaterial() != Material.WOOD) { continue; }

            at.setPos(log.getX(), log.getY(), log.getZ());
            felled += clearAt(world, at);
            trunk.add(log);
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        if (dx != 0 || dy != 0 || dz != 0) { spreading.add(log.add(dx, dy, dz)); }
                    }
                }
            }
        }
        for (BlockPos log : trunk) {
            for (int x = log.getX() - 5; x <= log.getX() + 5; x++) {
                for (int z = log.getZ() - 5; z <= log.getZ() + 5; z++) {
                    for (int y = log.getY() - 5; y <= log.getY() + 5; y++) {
                        if (!world.isChunkGeneratedAt(x >> 4, z >> 4)) { continue; }

                        at.setPos(x, y, z);
                        if (insideAnother(start, piece, at)) { continue; }
                        if (world.getBlockState(at).getMaterial() != Material.LEAVES) { continue; }
                        if (woodKeeps(world, x, y, z)) { continue; }

                        felled += clearAt(world, at);
                    }
                }
            }
        }
        return felled;
    }

    private static boolean woodKeeps(World world, int leafX, int leafY, int leafZ) {
        BlockPos.MutableBlockPos near = new BlockPos.MutableBlockPos();
        for (int dx = -4; dx <= 4; dx++) {
            for (int dy = -4; dy <= 4; dy++) {
                for (int dz = -4; dz <= 4; dz++) {
                    if (Math.abs(dx) + Math.abs(dy) + Math.abs(dz) > 4) { continue; }
                    if (!world.isChunkGeneratedAt((leafX + dx) >> 4, (leafZ + dz) >> 4)) { continue; }

                    near.setPos(leafX + dx, leafY + dy, leafZ + dz);
                    if (world.getBlockState(near).getMaterial() == Material.WOOD) { return true; }
                }
            }
        }
        return false;
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
        int level = surface(generator, world, (well.minX + well.maxX) / 2, (well.minZ + well.maxZ) / 2);
        if (level < 0) { return; }

        int shift = level - nominal;
        int roads = 0;
        for (StructureComponent piece : start.getComponents()) {
            piece.getBoundingBox().offset(0, shift, 0);
            if (piece instanceof StructureVillagePieces.Path) { roads++; }
        }
        start.getBoundingBox().offset(0, shift, 0);
        ContentLog.LOGGER.debug("A village born at {}, {} is founded at y {}, shifted {} from its nominal ground at y {}, laid with {} piece(s), {} of them roads", (well.minX + well.maxX) / 2, (well.minZ + well.maxZ) / 2, level, shift, nominal, start.getComponents().size(), roads);
    }

    public static void openAround(StructureStart start, StructureComponent piece, World world, StructureBoundingBox clip) {
        if (!(piece instanceof StructureVillagePieces.Village) || piece instanceof StructureVillagePieces.Path || piece instanceof StructureVillagePieces.Torch) { return; }

        StructureBoundingBox box = piece.getBoundingBox();
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        if (piece instanceof StructureVillagePieces.Start) {
            bankWell(start, piece, world, clip, box, at);
            return;
        }
        int grounded = 0;
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
        int opened = 0;
        int spared = 0;
        int notGround = 0;
        int hangingOver = 0;
        for (int x = box.minX - 2; x <= box.maxX + 2; x++) {
            for (int z = box.minZ - 2; z <= box.maxZ + 2; z++) {
                if (x >= box.minX && x <= box.maxX && z >= box.minZ && z <= box.maxZ) { continue; }

                int bed = roadTop(world, start, at, x, z, box.minY + 1, box.minY + 12);
                for (int y = bed == Integer.MIN_VALUE ? box.minY + 1 : bed + 1; y <= box.minY + 12; y++) {
                    at.setPos(x, y, z);
                    if (!clip.isVecInside(at)) { continue; }
                    if (insideAnother(start, piece, at)) {
                        spared++;
                        continue;
                    }

                    Material material = world.getBlockState(at).getMaterial();
                    if (opening(material)) { opened += clearAt(world, at); }
                    else if (material != Material.AIR) { notGround++; }
                }
                at.setPos(x, box.minY + 13, z);
                if (clip.isVecInside(at) && world.getBlockState(at).getMaterial().isSolid() && !insideAnother(start, piece, at)) { hangingOver++; }
            }
        }
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
                    for (int step = 1; step <= 5; step++) {
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
        int bridged = 0;
        for (StructureComponent other : start.getComponents()) {
            if (other == piece || other instanceof StructureVillagePieces.Path || !(other instanceof StructureVillagePieces.Village)) { continue; }

            bridged += bridge(world, start, piece, box, ((AccessorStructureComponentBox) other).rdpl$box(), clip, at);
        }
        if (opened + spared + notGround + hangingOver + grounded + overhead + bridged + doorways > 0) { ContentLog.LOGGER.debug("Opened {} block(s) around {} at {}, {}, spared {} inside neighbouring pieces, left {} that were not ground, stood {} block(s) of ground under it, lifted {} off its roof, bridged {} between it and a neighbour, freed {} in front of its doors, and the hillside still hangs over {} column(s)", opened, piece.getClass().getSimpleName(), box.minX, box.minZ, spared, notGround, grounded, overhead, bridged, doorways, hangingOver); }
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

    private static void bankWell(StructureStart start, StructureComponent piece, World world, StructureBoundingBox clip, StructureBoundingBox box, BlockPos.MutableBlockPos at) {
        int rim = box.maxY - 3;
        int banked = 0;
        int opened = 0;
        for (int x = box.minX - 2; x <= box.maxX + 2; x++) {
            for (int z = box.minZ - 2; z <= box.maxZ + 2; z++) {
                if (x >= box.minX && x <= box.maxX && z >= box.minZ && z <= box.maxZ) { continue; }
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
        world.setBlockState(at, Blocks.AIR.getDefaultState(), 2);
        int cleared = 1;
        at.move(EnumFacing.UP);
        if (world.getBlockState(at).getBlock() == Blocks.SNOW_LAYER) {
            world.setBlockState(at, Blocks.AIR.getDefaultState(), 2);
            cleared++;
        }
        at.move(EnumFacing.DOWN);
        return cleared;
    }

    private static StructureStart CURRENT;

    public static void building(StructureStart start) { CURRENT = start; }

    public static StructureStart current() { return CURRENT; }

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
                int toBox = Math.max(0, Math.max(box.minX - x, x - box.maxX)) + Math.max(0, Math.max(box.minZ - z, z - box.maxZ));
                int toNear = Math.max(0, Math.max(near.minX - x, x - near.maxX)) + Math.max(0, Math.max(near.minZ - z, z - near.maxZ));
                int base = toBox <= toNear ? box.minY : near.minY;
                int bed = roadTop(world, start, at, x, z, base + 1, base + 12);
                for (int y = bed == Integer.MIN_VALUE ? base + 1 : bed + 1; y <= base + 12; y++) {
                    at.setPos(x, y, z);
                    if (!clip.isVecInside(at) || insideAnother(start, piece, at)) { continue; }
                    if (opening(world.getBlockState(at).getMaterial())) { cleared += clearAt(world, at); }
                }
            }
        }
        return cleared;
    }

    public static void pave(StructureComponent piece, World world, StructureBoundingBox clip, IBlockState path, IBlockState gravel, IBlockState planks) {
        StructureBoundingBox box = piece.getBoundingBox();
        boolean alongX = box.maxX - box.minX >= box.maxZ - box.minZ;
        int least = Math.max(alongX ? box.minX : box.minZ, alongX ? clip.minX : clip.minZ);
        int most = Math.min(alongX ? box.maxX : box.maxZ, alongX ? clip.maxX : clip.maxZ);
        if (most < least) { return; }

        int acrossLeast = alongX ? box.minZ : box.minX;
        int acrossMost = alongX ? box.maxZ : box.maxX;
        int rows = most - least + 1;
        int[] profile = new int[rows];
        for (int i = 0; i < rows; i++) {
            int found = Integer.MAX_VALUE;
            for (int across = acrossLeast; across <= acrossMost; across++) {
                int x = alongX ? least + i : across;
                int z = alongX ? across : least + i;
                BlockPos spot = new BlockPos(x, 64, z);
                if (!clip.isVecInside(spot)) { continue; }

                BlockPos top = world.getTopSolidOrLiquidBlock(spot).down();
                if (top.getY() < world.getSeaLevel() - 1 || world.getBlockState(top).getMaterial().isLiquid()) { continue; }
                if (top.getY() < found) { found = top.getY(); }
            }
            profile[i] = found == Integer.MAX_VALUE ? Integer.MIN_VALUE : found;
        }
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
        int cut = 0;
        int filled = 0;
        int paved = 0;
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        for (int i = 0; i < rows; i++) {
            for (int across = acrossLeast; across <= acrossMost; across++) {
                int x = alongX ? least + i : across;
                int z = alongX ? across : least + i;
                BlockPos spot = new BlockPos(x, 64, z);
                if (!clip.isVecInside(spot)) { continue; }

                BlockPos top = world.getTopSolidOrLiquidBlock(spot).down();
                if (top.getY() < world.getSeaLevel()) { top = new BlockPos(x, world.getSeaLevel() - 1, z); }
                if (profile[i] == Integer.MIN_VALUE) {
                    if (world.getBlockState(top).getMaterial().isLiquid()) {
                        world.setBlockState(top, planks, 2);
                        paved++;
                    }
                    continue;
                }
                if (bridged[i]) {
                    at.setPos(x, profile[i], z);
                    if (!world.getBlockState(at).getMaterial().isSolid()) {
                        world.setBlockState(at, planks, 2);
                        paved++;
                    }
                    continue;
                }
                if (world.getBlockState(top).getMaterial().isLiquid()) {
                    world.setBlockState(top, planks, 2);
                    paved++;
                    continue;
                }

                int target = profile[i];
                at.setPos(x, target, z);
                IBlockState held = world.getBlockState(at);
                Block base = held.getBlock();
                if (held.getMaterial().isSolid() && !terrainBlock(base) && base != Blocks.GRASS_PATH && base != Blocks.PLANKS && base != Blocks.SANDSTONE && base != Blocks.RED_SANDSTONE && base != Blocks.HARDENED_CLAY && base != Blocks.STAINED_HARDENED_CLAY && base != Blocks.MYCELIUM) { continue; }

                for (int y = target + 1; y <= target + 4; y++) {
                    at.setPos(x, y, z);
                    IBlockState above = world.getBlockState(at);
                    Block up = above.getBlock();
                    if (up == Blocks.AIR) { continue; }
                    if (above.getMaterial().isLiquid()) { break; }
                    if (terrainBlock(up) || up == Blocks.GRASS_PATH || up == Blocks.SANDSTONE || up == Blocks.MYCELIUM || !above.getMaterial().isSolid()) {
                        world.setBlockState(at, Blocks.AIR.getDefaultState(), 2);
                        cut++;
                        continue;
                    }
                    break;
                }
                for (int y = target - 1; y >= target - 8; y--) {
                    at.setPos(x, y, z);
                    if (world.getBlockState(at).getMaterial().isSolid()) { break; }

                    world.setBlockState(at, Blocks.DIRT.getDefaultState(), 2);
                    filled++;
                }
                at.setPos(x, target, z);
                boolean earthy = base == Blocks.GRASS || base == Blocks.DIRT || base == Blocks.MYCELIUM || base == Blocks.GRASS_PATH || base == Blocks.AIR || !world.getBlockState(at).getMaterial().isSolid();
                world.setBlockState(at, earthy ? path : gravel, 2);
                paved++;
            }
        }
        if ((cut + filled + paved > 0) && ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("Graded the road at {}, {} within its chunk: paved {} column(s), cut {} block(s) off bumps, filled {} into dips", box.minX, box.minZ, paved, cut, filled); }
    }

    private static boolean joined(int[] profile, int i) { return profile[i] != Integer.MIN_VALUE && profile[i - 1] != Integer.MIN_VALUE; }

    private static boolean terrainBlock(Block held) {
        return held == Blocks.STONE || held == Blocks.DIRT || held == Blocks.GRASS || held == Blocks.GRAVEL || held == Blocks.SAND
                || held == Blocks.CLAY || held == Blocks.SNOW_LAYER || held == Blocks.SNOW || held == Blocks.ICE || held == Blocks.PACKED_ICE;
    }

    private static boolean insideAnother(StructureStart start, StructureComponent piece, BlockPos at) {
        for (StructureComponent other : start.getComponents()) {
            if (other == piece || other instanceof StructureVillagePieces.Path) { continue; }
            if (other.getBoundingBox().isVecInside(at)) { return true; }
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
