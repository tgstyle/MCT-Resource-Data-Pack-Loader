package mctmods.resourcedatapackloader.content.worldgen.beard;

import mctmods.resourcedatapackloader.content.worldgen.ContentBeard;
import mctmods.resourcedatapackloader.content.worldgen.ContentSites;
import mctmods.resourcedatapackloader.content.worldgen.ContentStructurePlacement;
import mctmods.resourcedatapackloader.content.worldgen.beard.interfaces.RoadLayout;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IChunkGeneratorBeardFields;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IMapGenBase;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IMapGenStructureSpawn;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IMinecraftServerMessage;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.world.GroundLevel;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.World;
import net.minecraft.world.gen.ChunkGeneratorOverworld;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import java.util.Arrays;
import java.util.List;

public final class BeardSite {
    private BeardSite() {}

    public static long packedChunk(int chunkX, int chunkZ) { return ((long) chunkX << 32) | (chunkZ & 0xFFFFFFFFL); }

    public static long siteFor(World world, ContentSites known, int cellX, int cellZ, int spacing) {
        long cell = packedChunk(cellX, cellZ);
        Long held = known.get(cell);
        if (held != null) { return held; }
        if (world.getMinecraftServer() != null) { ((IMinecraftServerMessage) world.getMinecraftServer()).rdpl$setUserMessage("menu.generatingTerrain"); }
        long chosen = chooseSite(world, cellX, cellZ, spacing);
        known.put(cell, chosen);
        if (chosen == ContentBeard.NO_SITE) { ContentLog.LOGGER.debug("Village cell {}, {} has no chunk both flat within {} block(s) and {} chunk(s) clear of its neighbours, so nothing is founded there", cellX, cellZ, ContentBeard.SITE_TOLERANCE, ContentBeard.SITE_SEPARATION); }
        else { ContentLog.LOGGER.debug("Village cell {}, {} founds on chunk {}, {}, the flattest ground it has", cellX, cellZ, (int) (chosen >> 32), (int) chosen); }
        return chosen;
    }
    public static long chooseSite(World world, int cellX, int cellZ, int spacing) {
        ChunkGeneratorOverworld sampled = BeardSurface.samplerFor(world);
        if (sampled == null) { return ContentBeard.NO_SITE; }
        int margin = ContentBeard.SITE_SEPARATION / 2;
        int baseX = cellX * spacing;
        int baseZ = cellZ * spacing;
        int span = spacing + ContentBeard.SITE_REACH * 2;
        int size = span * 4 + 1;
        int originX = (baseX - ContentBeard.SITE_REACH) * 4;
        int originZ = (baseZ - ContentBeard.SITE_REACH) * 4;
        Biome[] region = world.getBiomeProvider().getBiomesForGeneration(new Biome[size * size], originX, originZ, size, size);
        int lattice = span * 2;
        int[] heights = new int[lattice * lattice];
        Arrays.fill(heights, Integer.MIN_VALUE);
        long chosen = ContentBeard.NO_SITE;
        int bestSpread = Integer.MAX_VALUE;
        int bestPull = Integer.MAX_VALUE;
        for (int x = margin; x < spacing - margin; x++) {
            for (int z = margin; z < spacing - margin; z++) {
                if (!MapGenVillage.VILLAGE_SPAWN_BIOMES.contains(region[(x + ContentBeard.SITE_REACH) * 4 + 2 + ((z + ContentBeard.SITE_REACH) * 4 + 2) * size])) { continue; }
                int lowest = Integer.MAX_VALUE;
                int highest = Integer.MIN_VALUE;
                int limit = Math.min(ContentBeard.SITE_TOLERANCE, bestSpread);
                for (int dx = 0; dx <= ContentBeard.SITE_REACH * 4 + 1; dx++) {
                    for (int dz = 0; dz <= ContentBeard.SITE_REACH * 4 + 1; dz++) {
                        int at = (x * 2 + dx) * lattice + z * 2 + dz;
                        int sampledHeight = heights[at];
                        if (sampledHeight == Integer.MIN_VALUE) {
                            sampledHeight = BeardSurface.surfaceAt(world, sampled, region, originX, originZ, size, (baseX + x - ContentBeard.SITE_REACH) * 16 + 4 + dx * 8, (baseZ + z - ContentBeard.SITE_REACH) * 16 + 4 + dz * 8);
                            heights[at] = sampledHeight;
                        }
                        if (sampledHeight < 0) {
                            lowest = Integer.MAX_VALUE;
                            highest = Integer.MIN_VALUE;
                            dx = ContentBeard.SITE_REACH * 4 + 2;
                            break;
                        }
                        if (sampledHeight < lowest) { lowest = sampledHeight; }
                        if (sampledHeight > highest) { highest = sampledHeight; }
                        if (highest - lowest > limit) {
                            dx = ContentBeard.SITE_REACH * 4 + 2;
                            break;
                        }
                    }
                }
                if (lowest == Integer.MAX_VALUE) { continue; }
                int spread = highest - lowest;
                if (spread > ContentBeard.SITE_TOLERANCE || spread > bestSpread) { continue; }
                int pull = Math.abs(x * 2 - spacing) + Math.abs(z * 2 - spacing);
                if (spread == bestSpread && pull >= bestPull) { continue; }
                bestSpread = spread;
                bestPull = pull;
                chosen = packedChunk(baseX + x, baseZ + z);
            }
        }
        return chosen;
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
        int grid = known.spacing();
        int cellX = Math.floorDiv(from.getX() >> 4, grid);
        int cellZ = Math.floorDiv(from.getZ() >> 4, grid);
        long ending = System.nanoTime() + budgetNanos;
        BlockPos best = null;
        long bestAway = Long.MAX_VALUE;
        int stopAt = 100;
        for (int ring = 0; ring <= stopAt; ring++) {
            for (int dx = -ring; dx <= ring; dx++) {
                for (int dz = -ring; dz <= ring; dz++) {
                    if (Math.max(Math.abs(dx), Math.abs(dz)) != ring) { continue; }
                    if (known.get(packedChunk(cellX + dx, cellZ + dz)) == null && System.nanoTime() >= ending) { return best; }
                    long chosen = siteFor(world, known, cellX + dx, cellZ + dz, grid);
                    if (chosen == ContentBeard.NO_SITE) { continue; }
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
    public static Boolean flatSite(World world, int chunkX, int chunkZ, int spacing) {
        if (BeardSurface.samplerFor(world) == null) { return null; }
        ContentSites known = ContentSites.of(world, spacing);
        int grid = known.spacing();
        long chosen = siteFor(world, known, Math.floorDiv(chunkX, grid), Math.floorDiv(chunkZ, grid), grid);
        return chosen != ContentBeard.NO_SITE && chosen == packedChunk(chunkX, chunkZ);
    }
    public static boolean mansionCandidateNear(World world, int chunkX, int chunkZ) {
        ChunkGeneratorOverworld sampled = BeardSurface.samplerFor(world);
        if (sampled == null) { return false; }
        MapGenStructure mansions = ((IChunkGeneratorBeardFields) sampled).rdpl$mansions();
        ((IMapGenBase) mansions).rdpl$setWorld(world);
        IMapGenStructureSpawn asker = (IMapGenStructureSpawn) mansions;
        for (int x = chunkX - 6; x <= chunkX + 2; x++) {
            for (int z = chunkZ - 6; z <= chunkZ + 2; z++) {
                if (asker.rdpl$canSpawnStructureAtCoords(x, z)) { return true; }
            }
        }
        return false;
    }
    public static int lowestIn(World worldIn, int minX, int minZ, int maxX, int maxZ, StructureBoundingBox clip) {
        int floor = worldIn.provider.getAverageGroundLevel() - 1;
        if (BeardSurface.samplerFor(worldIn) != null) {
            int lowest = Integer.MAX_VALUE;
            for (int z = minZ; z <= maxZ; z++) {
                for (int x = minX; x <= maxX; x++) {
                    int sampled = BeardSurface.surfaceAt(worldIn, x, z);
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
                lowest = Math.min(lowest, Math.max(GroundLevel.inWindow(worldIn, at).getY(), floor));
            }
        }
        return lowest;
    }
    public static int footingSpread(StructureBoundingBox box) {
        World world = ContentBeard.samplerWorld;
        if (world == null) { return 0; }
        int lowest = Integer.MAX_VALUE;
        int highest = Integer.MIN_VALUE;
        for (int x = box.minX; x <= box.maxX + 3; x += 4) {
            for (int z = box.minZ; z <= box.maxZ + 3; z += 4) {
                int sampled = BeardSurface.surfaceAt(world, Math.min(x, box.maxX), Math.min(z, box.maxZ));
                if (sampled < 0) { return Integer.MAX_VALUE; }
                if (sampled < lowest) { lowest = sampled; }
                if (sampled > highest) { highest = sampled; }
            }
        }
        return highest - lowest;
    }
    public static int footingMisfit(StructureBoundingBox box, List<StructureComponent> pieces, int sink, int give) {
        int spread = footingSpread(box);
        if (spread == Integer.MAX_VALUE) { return spread; }
        World world = ContentBeard.samplerWorld;
        for (StructureComponent piece : pieces) {
            if (!(piece instanceof StructureVillagePieces.Path)) { continue; }
            StructureBoundingBox road = piece.getBoundingBox();
            int gapX = Math.max(road.minX - box.maxX, box.minX - road.maxX);
            int gapZ = Math.max(road.minZ - box.maxZ, box.minZ - road.maxZ);
            if (Math.max(gapX, gapZ) > 2 || Math.min(gapX, gapZ) > 0) { continue; }
            int stand = BeardRoads.roadGradeBeside(world, box);
            if (stand == Integer.MIN_VALUE) { return Integer.MAX_VALUE; }
            int total = 0;
            for (int x = box.minX; x <= box.maxX + 3; x += 4) {
                for (int z = box.minZ; z <= box.maxZ + 3; z += 4) {
                    int ground = BeardSurface.surfaceAt(world, Math.min(x, box.maxX), Math.min(z, box.maxZ));
                    if (ground < 0) { return Integer.MAX_VALUE; }
                    int gap = stand - ground;
                    if (gap > 2 + sink + give || -gap > 2 + give || spread > 2 + give) { return Integer.MAX_VALUE; }
                    total += Math.abs(gap);
                }
            }
            return total;
        }
        return Integer.MAX_VALUE;
    }
    public static void settleRoads(StructureStart start) {
        List<StructureComponent> pieces = start.getComponents();
        if (ContentBeard.samplerWorld == null || pieces.isEmpty()) { return; }
        StructureBoundingBox well = pieces.get(0).getBoundingBox();
        List<StructureComponent> laid = ContentBeard.laid();
        ContentBeard.laying(pieces);
        int tested = 0;
        int pulled = 0;
        try {
            for (StructureComponent piece : pieces) {
                if (!(piece instanceof StructureVillagePieces.Path)) { continue; }
                StructureBoundingBox box = piece.getBoundingBox();
                boolean alongX = BeardPlots.roadAlongX(piece);
                int least = alongX ? box.minX : box.minZ;
                int most = alongX ? box.maxX : box.maxZ;
                int rows = most - least + 1;
                if (rows < 14) { continue; }
                tested++;
                int middle = alongX ? (well.minX + well.maxX) / 2 : (well.minZ + well.maxZ) / 2;
                boolean growsUp = Math.abs(least - middle) <= Math.abs(most - middle);
                EnumFacing facing = alongX ? (growsUp ? EnumFacing.EAST : EnumFacing.WEST) : (growsUp ? EnumFacing.SOUTH : EnumFacing.NORTH);
                int kept = BeardRoads.roadReach(box, facing);
                if (kept >= rows) { continue; }
                int attached = attachedRows(pieces, piece, box, alongX, growsUp);
                int trimmed = Math.max(kept, attached);
                if (trimmed >= rows) {
                    ContentLog.LOGGER.debug("The road at {}, {} no longer grades to a walkable slope now its junctions are known, but pieces attach along all {} row(s) of it, so it stands", box.minX, box.minZ, rows);
                    continue;
                }
                if (growsUp && alongX) { box.maxX = box.minX + trimmed - 1; }
                else if (growsUp) { box.maxZ = box.minZ + trimmed - 1; }
                else if (alongX) { box.minX = box.maxX - trimmed + 1; }
                else { box.minZ = box.maxZ - trimmed + 1; }
                pulled++;
                ContentLog.LOGGER.debug("The road at {}, {} is pulled back from {} to {} row(s) now its junctions are known, keeping {} row(s) that pieces attach to", box.minX, box.minZ, rows, trimmed, attached);
            }
        }
        finally { ContentBeard.laying(laid); }
        if (tested > 0 && ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("Re-tested {} road(s) at {}, {} now the layout is done, pulling back {}", tested, well.minX, well.minZ, pulled); }
    }

    private static void layoutRoads(World world, StructureStart start) {
        List<StructureComponent> pieces = start.getComponents();
        List<StructureComponent> held = ContentBeard.laid();
        ContentBeard.laying(pieces);
        int stored = 0;
        try {
            for (StructureComponent piece : pieces) { frontRoad(pieces, piece); }
            for (StructureComponent piece : pieces) { ContentBeard.attach(start, piece); }
            for (StructureComponent piece : pieces) {
                if (!(piece instanceof StructureVillagePieces.Path) || !(piece instanceof RoadLayout)) { continue; }
                StructureBoundingBox box = piece.getBoundingBox();
                boolean alongX = BeardPlots.roadAlongX(piece);
                BeardRoads.Grade grade = BeardRoads.roadProfile(world, piece, alongX, alongX ? box.minX : box.minZ, alongX ? box.maxX : box.maxZ, alongX ? box.minZ : box.minX, alongX ? box.maxZ : box.maxX, true);
                if (grade == null) { continue; }
                ((RoadLayout) piece).rdpl$layout(grade);
                stored++;
            }
        }
        finally { ContentBeard.laying(held); }
        if (stored > 0 && ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("Attached the junctions and stored the graded profile of {} road(s) of the village at {}, {} at layout time", stored, start.getBoundingBox().minX, start.getBoundingBox().minZ); }
    }

    private static void frontRoad(List<StructureComponent> pieces, StructureComponent piece) {
        if (!(piece instanceof StructureVillagePieces.Path)) { return; }
        StructureBoundingBox box = piece.getBoundingBox();
        boolean alongX = BeardPlots.roadAlongX(piece);
        int acrossLeast = alongX ? box.minZ : box.minX;
        int acrossMost = alongX ? box.maxZ : box.maxX;
        for (StructureComponent other : pieces) {
            if (other == piece || other instanceof StructureVillagePieces.Path) { continue; }
            StructureBoundingBox front = other.getBoundingBox();
            if ((alongX ? front.maxZ : front.maxX) < acrossLeast - 3 || (alongX ? front.minZ : front.minX) > acrossMost + 3) { continue; }
            int least = alongX ? box.minX : box.minZ;
            int most = alongX ? box.maxX : box.maxZ;
            int otherLeast = alongX ? front.minX : front.minZ;
            int otherMost = alongX ? front.maxX : front.maxZ;
            if (otherMost < least || otherLeast > most) { continue; }
            int high = otherMost > most ? Math.max(most, clearTo(pieces, piece, other, alongX, most + 1, otherMost, true, acrossLeast, acrossMost)) : most;
            int low = otherLeast < least ? Math.min(least, clearTo(pieces, piece, other, alongX, otherLeast, least - 1, false, acrossLeast, acrossMost)) : least;
            if (high == most && low == least) { continue; }
            if (alongX) {
                box.minX = low;
                box.maxX = high;
            }
            else {
                box.minZ = low;
                box.maxZ = high;
            }
            ContentLog.LOGGER.debug("The road at {}, {} is stretched from rows {}..{} to {}..{} to cover the frontage of {} at {}, {}", box.minX, box.minZ, least, most, low, high, other.getClass().getSimpleName(), front.minX, front.minZ);
        }
    }

    private static int clearTo(List<StructureComponent> pieces, StructureComponent road, StructureComponent fronting, boolean alongX, int lowEnd, int highEnd, boolean growUp, int acrossLeast, int acrossMost) {
        for (StructureComponent other : pieces) {
            if (other == road || other == fronting) { continue; }
            StructureBoundingBox held = other.getBoundingBox();
            if ((alongX ? held.maxZ : held.maxX) < acrossLeast || (alongX ? held.minZ : held.minX) > acrossMost) { continue; }
            int otherLeast = alongX ? held.minX : held.minZ;
            int otherMost = alongX ? held.maxX : held.maxZ;
            if (otherMost < lowEnd || otherLeast > highEnd) { continue; }
            if (growUp) { highEnd = Math.min(highEnd, otherLeast - 1); }
            else { lowEnd = Math.max(lowEnd, otherMost + 1); }
        }
        return growUp ? highEnd : lowEnd;
    }

    private static int attachedRows(List<StructureComponent> pieces, StructureComponent road, StructureBoundingBox box, boolean alongX, boolean growsUp) {
        int least = alongX ? box.minX : box.minZ;
        int most = alongX ? box.maxX : box.maxZ;
        int acrossLeast = alongX ? box.minZ : box.minX;
        int acrossMost = alongX ? box.maxZ : box.maxX;
        int rows = 0;
        for (StructureComponent other : pieces) {
            if (other == road) { continue; }
            StructureBoundingBox at = other.getBoundingBox();
            if ((alongX ? at.maxZ : at.maxX) < acrossLeast - 3 || (alongX ? at.minZ : at.minX) > acrossMost + 3) { continue; }
            int otherLeast = alongX ? at.minX : at.minZ;
            int otherMost = alongX ? at.maxX : at.maxZ;
            if (otherMost < least || otherLeast > most) { continue; }
            int reach = growsUp ? otherMost - least + 1 : most - otherLeast + 1;
            if (reach > rows) { rows = reach; }
        }
        return rows;
    }

    public static int wellNominal(StructureBoundingBox well) { return well.maxY - 3; }

    public static int wellGround(World world, StructureBoundingBox well) {
        int lowest = Integer.MAX_VALUE;
        for (int z = well.minZ; z <= well.maxZ; z++) {
            for (int x = well.minX; x <= well.maxX; x++) {
                int found = BeardSurface.surfaceAt(world, x, z);
                if (found >= 0 && found < lowest) { lowest = found; }
            }
        }
        if (lowest == Integer.MAX_VALUE) { return wellNominal(well); }
        int floor = world.provider.getAverageGroundLevel() - 1;
        if (lowest < floor) {
            ContentLog.LOGGER.debug("The well would found under water at y {}, so it is held up to the water line at y {}", lowest, floor);
            return floor;
        }
        return lowest;
    }

    public static void foundAtBirth(StructureStart start) {
        ChunkGeneratorOverworld generator = ContentBeard.sampler;
        World world = ContentBeard.samplerWorld;
        if (generator == null || world == null || start.getComponents().isEmpty()) { return; }
        StructureBoundingBox well = start.getComponents().get(0).getBoundingBox();
        int nominal = wellNominal(well);
        int level = wellGround(world, well);
        if (ContentLog.LOGGER.debugEnabled()) {
            int highest = Integer.MIN_VALUE;
            int count = 0;
            for (int z = well.minZ; z <= well.maxZ; z++) {
                for (int x = well.minX; x <= well.maxX; x++) {
                    int found = BeardSurface.surfaceAt(world, x, z);
                    if (found < 0) { continue; }
                    count++;
                    if (found > highest) { highest = found; }
                }
            }
            int rawLowest = Integer.MAX_VALUE;
            for (int z = well.minZ; z <= well.maxZ; z++) {
                for (int x = well.minX; x <= well.maxX; x++) {
                    int found = BeardSurface.surfaceAt(world, x, z);
                    if (found >= 0 && found < rawLowest) { rawLowest = found; }
                }
            }
            ContentLog.LOGGER.debug("The well footprint at {}, {} samples y {}..{} across {} column(s), founding on y {}", well.minX, well.minZ, rawLowest, highest, count, level);
        }
        int shift = level - nominal;
        int roads = 0;
        for (StructureComponent piece : start.getComponents()) {
            piece.getBoundingBox().offset(0, shift, 0);
            if (piece instanceof StructureVillagePieces.Path) { roads++; }
        }
        start.getBoundingBox().offset(0, shift, 0);
        settleRoads(start);
        layoutRoads(world, start);
        ContentLog.LOGGER.debug("A village born at {}, {} is founded at y {}, shifted {} from its nominal ground at y {}, laid with {} piece(s), {} of them roads", (well.minX + well.maxX) / 2, (well.minZ + well.maxZ) / 2, level, shift, nominal, start.getComponents().size(), roads);
    }
}
