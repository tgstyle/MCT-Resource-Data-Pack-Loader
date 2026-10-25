package mctmods.resourcedatapackloader.content.worldgen.beard;

import mctmods.resourcedatapackloader.content.worldgen.ContentBeard;
import mctmods.resourcedatapackloader.content.worldgen.ContentBeardJoins;
import mctmods.resourcedatapackloader.content.worldgen.ContentSites;
import mctmods.resourcedatapackloader.content.worldgen.ContentStructurePlacement;
import mctmods.resourcedatapackloader.content.worldgen.beard.interfaces.IRoadLayout;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.world.Facings;
import mctmods.resourcedatapackloader.util.world.GroundLevel;
import mctmods.resourcedatapackloader.util.Longs;
import mctmods.resourcedatapackloader.util.Runs;

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
import javax.annotation.Nullable;

public final class BeardSite {
    private static final int GRADE_PASSES = 4;
    private static final int RING = 4;

    private BeardSite() {}

    public static long siteFor(World world, ContentSites known, int cellX, int cellZ, int spacing) {
        long cell = Longs.pack(cellX, cellZ);
        Long held = known.get(cell);
        if (held != null) { return held; }
        if (world.getMinecraftServer() != null) { world.getMinecraftServer().setUserMessage("menu.generatingTerrain"); }
        long chosen = chooseSite(world, cellX, cellZ, spacing);
        known.put(cell, chosen);
        if (chosen == ContentBeard.NO_SITE) { ContentLog.LOGGER.debug("Village cell {}, {} has no chunk both flat within {} block(s) and {} chunk(s) clear of its neighbors, so nothing is founded there", cellX, cellZ, ContentBeard.SITE_TOLERANCE, ContentBeard.SITE_SEPARATION); }
        else { ContentLog.LOGGER.debug("Village cell {}, {} founds on chunk {}, {}, the flattest ground it has", cellX, cellZ, Longs.high(chosen), Longs.low(chosen)); }
        return chosen;
    }
    private static long chooseSite(World world, int cellX, int cellZ, int spacing) {
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
                chosen = Longs.pack(baseX + x, baseZ + z);
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
                best = new BlockPos((int) pin[0] + ContentBeard.BESIDE_WELL, 64, (int) pin[1] + ContentBeard.BESIDE_WELL);
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
                    if (known.get(Longs.pack(cellX + dx, cellZ + dz)) == null && System.nanoTime() >= ending) { return best; }
                    long chosen = siteFor(world, known, cellX + dx, cellZ + dz, grid);
                    if (chosen == ContentBeard.NO_SITE) { continue; }
                    int chunkX = Longs.high(chosen);
                    int chunkZ = Longs.low(chosen);
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
    public static boolean flatSite(World world, int chunkX, int chunkZ, int spacing) {
        if (BeardSurface.unreadable(world)) { return false; }
        ContentSites known = ContentSites.of(world, spacing);
        int grid = known.spacing();
        long chosen = siteFor(world, known, Math.floorDiv(chunkX, grid), Math.floorDiv(chunkZ, grid), grid);
        return chosen != ContentBeard.NO_SITE && chosen == Longs.pack(chunkX, chunkZ);
    }
    public static boolean mansionCandidateNear(World world, int chunkX, int chunkZ) {
        ChunkGeneratorOverworld sampled = BeardSurface.samplerFor(world);
        if (sampled == null) { return false; }
        MapGenStructure mansions = sampled.woodlandMansionGenerator;
        mansions.world = world;
        for (int x = chunkX - 6; x <= chunkX + 2; x++) {
            for (int z = chunkZ - 6; z <= chunkZ + 2; z++) {
                if (mansions.canSpawnStructureAtCoords(x, z)) { return true; }
            }
        }
        return false;
    }
    public static int lowestIn(World worldIn, int minX, int minZ, int maxX, int maxZ, StructureBoundingBox clip) {
        int floor = worldIn.provider.getAverageGroundLevel() - 1;
        Integer flat = BeardSurface.flatTop(worldIn);
        if (flat != null) { return Math.max(flat + 1, floor); }
        if (BeardSurface.samplerFor(worldIn) != null) {
            int[] found = footprint(worldIn, new StructureBoundingBox(minX, 0, minZ, maxX, 0, maxZ));
            if (found[2] > 0) { return Math.max(found[0], floor); }
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
    private static int footingSpread(StructureBoundingBox box) {
        World world = ContentBeard.samplerWorld;
        if (world == null) { return 0; }
        int[] heights = sampleGrid(world, box);
        if (heights == null) { return Integer.MAX_VALUE; }
        int lowest = Integer.MAX_VALUE;
        int highest = Integer.MIN_VALUE;
        for (int sampled : heights) {
            lowest = Math.min(lowest, sampled);
            highest = Math.max(highest, sampled);
        }
        return highest - lowest;
    }
    @Nullable private static int[] sampleGrid(World world, StructureBoundingBox box) {
        int[] heights = new int[((box.maxX + 3 - box.minX) / 4 + 1) * ((box.maxZ + 3 - box.minZ) / 4 + 1)];
        int n = 0;
        for (int x = box.minX; x <= box.maxX + 3; x += 4) {
            for (int z = box.minZ; z <= box.maxZ + 3; z += 4) {
                int sampled = BeardSurface.surfaceAt(world, Math.min(x, box.maxX), Math.min(z, box.maxZ));
                if (sampled < 0) { return null; }
                heights[n++] = sampled;
            }
        }
        return heights;
    }
    private static int[] footprint(World world, StructureBoundingBox box) {
        int lowest = Integer.MAX_VALUE;
        int highest = Integer.MIN_VALUE;
        int count = 0;
        for (int z = box.minZ; z <= box.maxZ; z++) {
            for (int x = box.minX; x <= box.maxX; x++) {
                int found = BeardSurface.surfaceAt(world, x, z);
                if (found < 0) { continue; }
                count++;
                lowest = Math.min(lowest, found);
                highest = Math.max(highest, found);
            }
        }
        return new int[] { lowest, highest, count };
    }
    public static int footingMisfit(StructureBoundingBox box, List<StructureComponent> pieces, int sink, int give, int allow) {
        int spread = footingSpread(box);
        if (spread == Integer.MAX_VALUE) { return spread; }
        World world = ContentBeard.samplerWorld;
        if (world == null) { return 0; }
        for (StructureComponent piece : pieces) {
            if (!(piece instanceof StructureVillagePieces.Path)) { continue; }
            StructureBoundingBox road = piece.getBoundingBox();
            int gapX = Math.max(road.minX - box.maxX, box.minX - road.maxX);
            int gapZ = Math.max(road.minZ - box.maxZ, box.minZ - road.maxZ);
            if (Math.max(gapX, gapZ) > 2 || Math.min(gapX, gapZ) > 0) { continue; }
            int stand = BeardRoadsGrade.roadGradeBeside(world, box);
            if (stand == Integer.MIN_VALUE) { return Integer.MAX_VALUE; }
            int[] grounds = sampleGrid(world, box);
            if (grounds == null) { return Integer.MAX_VALUE; }
            int total = 0;
            for (int ground : grounds) {
                int gap = stand - ground;
                if (gap > allow + sink + give || -gap > allow || spread > allow + give) { return Integer.MAX_VALUE; }
                total += Math.abs(gap);
            }
            return total;
        }
        return Integer.MAX_VALUE;
    }
    public static void standOff(World world, StructureStart start) {
        if (!ContentBeard.wanted() || BeardSurface.unreadable(world)) { return; }
        List<StructureComponent> pieces = start.getComponents();
        List<StructureComponent> held = ContentBeard.laid();
        ContentBeard.laying(pieces);
        try {
            List<StructureComponent> around = ContentBeard.everyone(world, pieces);
            for (StructureComponent plot : pieces.toArray(new StructureComponent[0])) {
                if (plot instanceof StructureVillagePieces.Road || plot instanceof StructureVillagePieces.Well || BeardRails.isRail(plot)) { continue; }
                StructureBoundingBox box = plot.getBoundingBox();
                int floor = BeardLayout.predictedFloor(plot, world);
                if (floor == Integer.MIN_VALUE) { continue; }
                int hill = groundAround(world, around, box);
                int allow = ContentBeard.footingAllow(plot);
                if (hill == Integer.MIN_VALUE || hill - floor <= allow) { continue; }
                pieces.remove(plot);
                ContentLog.LOGGER.debug("{} at {}, {}, {} to {}, {}, {} makes way for the hill around it: it would stand at y {}, {} block(s) under the ground of y {} around its clearing, deeper than the {} block(s) of apron it may dig", plot.getClass().getSimpleName(), box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, floor, hill - floor, hill, allow);
            }
        }
        finally { ContentBeard.laying(held); }
    }

    private static int groundAround(World world, List<StructureComponent> around, StructureBoundingBox box) {
        int[] ring = new int[2 * (box.maxX - box.minX + box.maxZ - box.minZ) + 8 * RING + 4];
        int count = 0;
        for (int x = box.minX - RING; x <= box.maxX + RING; x++) {
            count = ringGround(world, around, ring, count, x, box.minZ - RING);
            count = ringGround(world, around, ring, count, x, box.maxZ + RING);
        }
        for (int z = box.minZ - RING + 1; z <= box.maxZ + RING - 1; z++) {
            count = ringGround(world, around, ring, count, box.minX - RING, z);
            count = ringGround(world, around, ring, count, box.maxX + RING, z);
        }
        if (count == 0) { return Integer.MIN_VALUE; }
        Arrays.sort(ring, 0, count);
        return ring[count / 2];
    }

    private static int ringGround(World world, List<StructureComponent> around, int[] ring, int count, int x, int z) {
        for (StructureComponent piece : around) {
            StructureBoundingBox taken = piece.getBoundingBox();
            if (x >= taken.minX && x <= taken.maxX && z >= taken.minZ && z <= taken.maxZ) { return count; }
        }
        int sampled = BeardSurface.surfaceAt(world, x, z);
        if (sampled < 0) { return count; }
        ring[count] = sampled;
        return count + 1;
    }

    private static void trim(StructureBoundingBox box, boolean alongX, boolean growsUp, int rows) {
        if (growsUp && alongX) { box.maxX = box.minX + rows - 1; }
        else if (growsUp) { box.maxZ = box.minZ + rows - 1; }
        else if (alongX) { box.minX = box.maxX - rows + 1; }
        else { box.minZ = box.maxZ - rows + 1; }
    }

    private static void settleRoads(StructureStart start) {
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
                EnumFacing facing = Facings.along(alongX, growsUp);
                int kept = BeardRoadsGrade.roadReach(box, facing);
                if (kept >= rows) { continue; }
                int attached = attachedRows(pieces, piece, box, alongX, growsUp);
                int trimmed = Math.max(kept, attached);
                if (trimmed >= rows) {
                    ContentLog.LOGGER.debug("The road at {}, {} no longer grades to a walkable slope now its junctions are known, but pieces attach along all {} row(s) of it, so it stands", box.minX, box.minZ, rows);
                    continue;
                }
                trim(box, alongX, growsUp, trimmed);
                pulled++;
                ContentLog.LOGGER.debug("The road at {}, {} is pulled back from {} to {} row(s) now its junctions are known, keeping {} row(s) that pieces attach to", box.minX, box.minZ, rows, trimmed, attached);
            }
        }
        finally { ContentBeard.laying(laid); }
        if (pulled > 0 && ContentBeard.samplerWorld != null) { BeardRoadsTunnels.forget(ContentBeard.samplerWorld); }
        if (tested > 0 && ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("Re-tested {} road(s) at {}, {} now the layout is done, pulling back {}", tested, well.minX, well.minZ, pulled); }
    }

    private static void layoutRoads(World world, StructureStart start) {
        List<StructureComponent> pieces = start.getComponents();
        List<StructureComponent> held = ContentBeard.laid();
        ContentBeard.laying(pieces);
        try {
            for (StructureComponent piece : pieces) { frontRoad(pieces, piece); }
            for (StructureComponent piece : pieces) { ContentBeardJoins.attach(start, piece); }
        }
        finally { ContentBeard.laying(held); }
        gradeRoads(world, start, "at layout time");
        BeardRoadsTunnels.forget(world);
    }

    public static void gradeRoads(World world, StructureStart start, String when) {
        if (!ContentBeard.wanted() || BeardSurface.unreadable(world)) { return; }
        List<StructureComponent> pieces = start.getComponents();
        List<StructureComponent> held = ContentBeard.laid();
        ContentBeard.laying(pieces);
        int stored = 0;
        int passes = 0;
        try {
            for (StructureComponent piece : pieces) { BeardRoads.clearLayout(piece); }
            boolean settled = false;
            while (!settled && passes < GRADE_PASSES) {
                passes++;
                settled = true;
                stored = 0;
                for (StructureComponent piece : pieces) {
                    if (!(piece instanceof StructureVillagePieces.Path) || !(piece instanceof IRoadLayout)) { continue; }
                    StructureBoundingBox box = piece.getBoundingBox();
                    boolean alongX = BeardPlots.roadAlongX(piece);
                    BeardRoads.Grade grade = BeardRoadsGrade.roadProfile(world, piece, alongX, alongX ? box.minX : box.minZ, alongX ? box.maxX : box.maxZ, alongX ? box.minZ : box.minX, alongX ? box.maxZ : box.maxX, true);
                    if (grade == null) { continue; }
                    BeardRoads.Grade before = ((IRoadLayout) piece).rdpl$layout();
                    if (before == null || !before.sameAs(grade)) { settled = false; }
                    ((IRoadLayout) piece).rdpl$layout(grade);
                    stored++;
                }
            }
        }
        finally { ContentBeard.laying(held); }
        if (stored > 0 && ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("Stored the graded profile of {} road(s) of the village at {}, {} {}, settled after {} pass(es)", stored, start.getBoundingBox().minX, start.getBoundingBox().minZ, when, passes); }
    }

    public static void pullBackDecks(World world, StructureStart start) {
        if (!ContentBeard.wanted() || BeardSurface.unreadable(world)) { return; }
        List<StructureComponent> pieces = start.getComponents();
        if (pieces.isEmpty()) { return; }
        StructureBoundingBox well = pieces.get(0).getBoundingBox();
        int pulled = 0;
        for (StructureComponent piece : pieces) {
            if (!(piece instanceof StructureVillagePieces.Path) || !(piece instanceof IRoadLayout)) { continue; }
            BeardRoads.Grade grade = ((IRoadLayout) piece).rdpl$layout();
            if (grade == null) { continue; }
            StructureBoundingBox box = piece.getBoundingBox();
            boolean alongX = BeardPlots.roadAlongX(piece);
            int least = alongX ? box.minX : box.minZ;
            int most = alongX ? box.maxX : box.maxZ;
            int rows = most - least + 1;
            if (grade.start() != least || grade.rows() != rows) { continue; }
            int middle = alongX ? (well.minX + well.maxX) / 2 : (well.minZ + well.maxZ) / 2;
            boolean growsUp = Math.abs(least - middle) <= Math.abs(most - middle);
            int kept = shoreBeforeSlopedDeck(grade, growsUp);
            if (kept < 0) { continue; }
            int trimmed = Math.max(kept, attachedRows(pieces, piece, box, alongX, growsUp));
            if (trimmed >= rows || trimmed < 7) {
                ContentLog.LOGGER.debug("The road at {}, {} crosses water on a deck that cannot lie at one height, but pieces attach along it or too little of it stands on the shore, so it stands", box.minX, box.minZ);
                continue;
            }
            trim(box, alongX, growsUp, trimmed);
            pulled++;
            ContentLog.LOGGER.debug("The road at {}, {} is pulled back from {} to {} row(s) to the shore, since its deck cannot lie at one height between the junction it leaves and the land it reaches", box.minX, box.minZ, rows, trimmed);
        }
        if (pulled > 0) {
            BeardRoadsTunnels.forget(world);
            gradeRoads(world, start, "once the roads whose decks cannot lie level are pulled back");
        }
    }

    private static int shoreBeforeSlopedDeck(BeardRoads.Grade grade, boolean growsUp) {
        int rows = grade.rows();
        for (int n = 0; n < rows; n++) {
            if (!grade.bridged[growsUp ? n : rows - 1 - n]) { continue; }
            int end = Runs.end(k -> grade.bridged[growsUp ? k : rows - 1 - k], n, rows);
            int low = Integer.MAX_VALUE;
            int high = Integer.MIN_VALUE;
            for (int k = n; k <= end; k++) {
                int level = grade.profile[growsUp ? k : rows - 1 - k];
                if (level == Integer.MIN_VALUE) { continue; }
                low = Math.min(low, level);
                high = Math.max(high, level);
            }
            if (high > low) {
                int cut = n;
                while (cut > 0 && grade.ground[growsUp ? cut - 1 : rows - cut] == Integer.MIN_VALUE) { cut--; }
                return cut;
            }
            n = end;
        }
        return -1;
    }

    private static void frontRoad(List<StructureComponent> pieces, StructureComponent piece) {
        if (!(piece instanceof StructureVillagePieces.Path)) { return; }
        StructureBoundingBox box = piece.getBoundingBox();
        boolean alongX = BeardPlots.roadAlongX(piece);
        int acrossLeast = alongX ? box.minZ : box.minX;
        int acrossMost = alongX ? box.maxZ : box.maxX;
        for (StructureComponent other : pieces) {
            if (other == piece || other instanceof StructureVillagePieces.Path || BeardRails.isRail(other)) { continue; }
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
        int[] found = footprint(world, well);
        if (found[2] == 0) { return wellNominal(well); }
        int lowest = found[0];
        int floor = world.provider.getAverageGroundLevel() - 1;
        if (lowest < floor) {
            ContentLog.LOGGER.debug("The well would found under water at y {}, so it is held up to the water line at y {}", lowest, floor);
            return floor;
        }
        return lowest;
    }

    public static void foundAtBirth(World world, StructureStart start) {
        if (BeardSurface.unreadable(world) || start.getComponents().isEmpty()) { return; }
        StructureBoundingBox well = start.getComponents().get(0).getBoundingBox();
        int nominal = wellNominal(well);
        int level = wellGround(world, well);
        if (ContentLog.LOGGER.debugEnabled()) {
            int[] found = footprint(world, well);
            ContentLog.LOGGER.debug("The well footprint at {}, {} samples y {}..{} across {} column(s), founding on y {}", well.minX, well.minZ, found[0], found[1], found[2], level);
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
