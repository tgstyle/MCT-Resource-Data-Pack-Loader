package mctmods.resourcedatapackloader.content.village;

import mctmods.resourcedatapackloader.content.worldgen.ContentBeard;
import mctmods.resourcedatapackloader.content.worldgen.ContentStructureSearch;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardGrade;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardPlots;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardRoads;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardSite;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardSurface;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IStructureStartGrow;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.world.SeededRandom;

import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraft.world.gen.structure.StructureVillagePieces;

import java.util.*;
import javax.annotation.Nullable;

public final class CityGrowth {
    public static final int MARCH = 112;
    private static final int TRIES = 32;
    private static final int RELIEF = 6;
    private static final int DIG = 4;
    private static final int VERGE = 6;
    private static final int JOIN = 4;
    private static final int PER_MARCH = 96;
    private static boolean laying;
    private static boolean bulbLaying;
    private static boolean alleyLaying;
    private static int give;

    private CityGrowth() {}

    public static boolean laying() { return laying; }

    public static boolean bulbLaying() { return bulbLaying; }

    public static void alleyLaying(boolean narrow) { alleyLaying = narrow; }

    public static boolean alleyLaying() { return alleyLaying; }

    public static int give() { return give; }

    public static int march() {
        int least = ContentVillages.plotsLeast();
        int wide = Math.max(13, ContentVillages.largestPlot());
        double filled = (double) least / PER_MARCH * ((double) (wide * wide) / (13 * 13));
        if (filled <= 1.0) { return MARCH; }
        return (int) (MARCH * Math.sqrt(filled));
    }

    public static int chunkRange() { return (march() + 112 + 16) >> 4; }

    public static void grow(StructureStart held, World world, Random rand, int size) {
        int least = ContentVillages.plotsLeast();
        if (least <= 0) { return; }
        List<StructureComponent> components = held.getComponents();
        if (components.isEmpty()) { return; }
        int built = ContentVillages.plots(components);
        if (built >= least) { return; }
        int sizeFor = size + Math.max(16, least / 32);
        Set<Long> tried = new HashSet<>();
        int districts = 0;
        int attempts = 0;
        int allowed = Math.max(8 * TRIES, least + least / 2);
        while (built < least && attempts < allowed) {
            long site = site(components, tried);
            if (site == Long.MIN_VALUE) { break; }
            attempts++;
            tried.add(site);
            if (settle(world, rand, components, (int) (site >> 32), (int) site, sizeFor)) {
                districts++;
                built = ContentVillages.plots(components);
                tried.clear();
            }
        }
        if (built < least && ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("District growth drained after {} attempt(s): {} sites too near a well, {} on unreadable ground, {} on built ground, {} beside another village", attempts, settledCrowded, settledUngrounded, settledTaken, settledNeighbored); }
        settledCrowded = settledUngrounded = settledTaken = settledNeighbored = 0;
        if (built < least) { built = infill(rand, components, least, sizeFor); }
        ((IStructureStartGrow) held).rdpl$updateBoundingBox();
        ContentLog.LOGGER.debug("The village at chunk {}, {} grew {} district(s) to {} plot(s) against the asked minimum of {}", held.getChunkPosX(), held.getChunkPosZ(), districts, built, least);
    }

    public static void culDeSacs(StructureStart held, World world, Random rand) {
        if (!ContentBeard.wanted() || BeardSurface.unreadable(world)) { return; }
        List<StructureComponent> components = held.getComponents();
        if (components.isEmpty() || !(components.get(0) instanceof StructureVillagePieces.Start)) { return; }
        StructureVillagePieces.Start startPiece = (StructureVillagePieces.Start) components.get(0);
        int wide = BeardRoads.pathFullWidth() + 8;
        int sea = world.getSeaLevel();
        int bulbs = 0;
        for (StructureComponent piece : components.toArray(new StructureComponent[0])) {
            if (!(piece instanceof StructureVillagePieces.Path) || bulbWide(piece) || CitySeams.reserved(piece)) { continue; }
            StructureBoundingBox box = piece.getBoundingBox();
            boolean alongX = BeardPlots.roadAlongX(piece);
            if (BeardRoads.roadNarrow(box, alongX)) { continue; }
            int center = alongX ? (box.minZ + box.maxZ) / 2 : (box.minX + box.maxX) / 2;
            for (int side = 0; side < 2; side++) {
                boolean outward = side == 1;
                int dir = outward ? 1 : -1;
                int end = alongX ? (outward ? box.maxX : box.minX) : (outward ? box.maxZ : box.minZ);
                int endX = alongX ? end : center;
                int endZ = alongX ? center : end;
                if (metAtEnd(world, held, piece, alongX, end, alongX ? box.minZ : box.minX, alongX ? box.maxZ : box.maxX)) { continue; }
                if (CitySeams.facesNeighbor(world, components, alongX, end, dir, center)) {
                    ContentLog.LOGGER.debug("The dead end at {}, {} faces a neighboring village's site, so it rolls no cul-de-sac and stays open for that village's streets", endX, endZ);
                    continue;
                }
                if (SeededRandom.at(world, endX + dir, endZ + dir).nextInt(4) == 3) { continue; }
                int least = (BeardRoads.pathFullWidth() + 1) / 2 + 1;
                int found = 0;
                int stem = 0;
                boolean steep = false;
                List<StructureComponent> yielding = null;
                for (int r = (wide - 1) / 2; r >= least && found == 0; r--) {
                    for (int out = 1; out <= 7; out += 3) {
                        int spot = end + dir * (out + r + 1);
                        int discX = alongX ? spot : center;
                        int discZ = alongX ? center : spot;
                        if (!clearFor(world, held, piece, discX, discZ, r)) { continue; }
                        yielding = yielding(world, held, piece, court(box, alongX, end, dir, outward, center, r, out));
                        if (yielding == null) { continue; }
                        if (!seated(world, piece, alongX, end, discX, discZ, r)) {
                            steep = true;
                            continue;
                        }
                        found = r;
                        stem = out;
                        break;
                    }
                }
                if (found == 0) {
                    if (steep && ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("The dead end at {}, {} rolled a cul-de-sac, but every court it could seat would cut the ground beside it, so it stays a plain end", endX, endZ); }
                    continue;
                }
                int half = found;
                StructureBoundingBox bulb = court(box, alongX, end, dir, outward, center, half, stem);
                int heart = end + dir * (stem + half + 1);
                int discX = alongX ? heart : center;
                int discZ = alongX ? center : heart;
                boolean dry = true;
                for (int z = bulb.minZ; z <= bulb.maxZ && dry; z += 2) {
                    for (int x = bulb.minX; x <= bulb.maxX; x += 2) {
                        int off = (x - discX) * (x - discX) + (z - discZ) * (z - discZ);
                        if (off > half * half + half) { continue; }
                        if (BeardSurface.surfaceAt(world, x, z) < sea - 1) {
                            dry = false;
                            break;
                        }
                    }
                }
                if (!dry) { continue; }
                for (StructureComponent plot : yielding) { makeWay(world, held, plot, bulb); }
                EnumFacing facing = alongX ? (outward ? EnumFacing.EAST : EnumFacing.WEST) : (outward ? EnumFacing.SOUTH : EnumFacing.NORTH);
                StructureVillagePieces.Path bulbPiece = new StructureVillagePieces.Path(startPiece, 0, rand, bulb, facing);
                components.add(bulbPiece);
                laying = true;
                bulbLaying = true;
                try {
                    if (!CityLayout.drawn()) {
                        bulbPiece.buildComponent(startPiece, components, rand);
                        drain(startPiece, components, rand);
                    }
                }
                finally {
                    laying = false;
                    bulbLaying = false;
                }
                bulbs++;
                ContentLog.LOGGER.debug("The dead end at {}, {} rolled a cul-de-sac, {} across at {}, {}", endX, endZ, wide, bulb.minX, bulb.minZ);
            }
        }
        if (bulbs > 0) { ((IStructureStartGrow) held).rdpl$updateBoundingBox(); }
    }

    public static void alleyFill(StructureStart held, Random rand) {
        if (!ContentBeard.wanted() || BeardRoads.alleyChance() <= 0 || CityLayout.drawn()) { return; }
        List<StructureComponent> components = held.getComponents();
        if (components.isEmpty() || !(components.get(0) instanceof StructureVillagePieces.Start)) { return; }
        StructureVillagePieces.Start startPiece = (StructureVillagePieces.Start) components.get(0);
        int shortest = 14;
        int longest = 42;
        int laid = 0;
        for (StructureComponent piece : components.toArray(new StructureComponent[0])) {
            if (!(piece instanceof StructureVillagePieces.Path) || bulbWide(piece)) { continue; }
            StructureBoundingBox box = piece.getBoundingBox();
            boolean alongX = BeardPlots.roadAlongX(piece);
            if (BeardRoads.roadNarrow(box, alongX)) { continue; }
            int spacing = 2 * ContentVillages.blockOf(piece);
            int lo = (alongX ? box.minX : box.minZ) + 2;
            int hi = (alongX ? box.maxX : box.maxZ) - 2;
            for (int side = 0; side < 2; side++) {
                boolean outward = side == 1;
                int edge = alongX ? (outward ? box.maxZ : box.minZ) : (outward ? box.maxX : box.minX);
                int dir = outward ? 1 : -1;
                for (int row = lo; row <= hi; ) {
                    int length = alleyRun(components, alongX, row, edge, dir, longest);
                    if (length >= shortest && crowdedLane(components, alongX, row, edge, dir, length, spacing)) { length = 0; }
                    if (length < shortest) {
                        row += 7;
                        continue;
                    }
                    int from = edge + dir;
                    int to = edge + dir * length;
                    for (StructureComponent other : components) {
                        if (other instanceof StructureVillagePieces.Path) { continue; }
                        StructureBoundingBox plot = other.getBoundingBox();
                        if (!plot.intersectsWith(alongX ? row - 1 : Math.min(from, to), alongX ? Math.min(from, to) : row - 1, alongX ? row + 1 : Math.max(from, to), alongX ? Math.max(from, to) : row + 1)) { continue; }
                        int near = dir > 0 ? (alongX ? plot.minZ : plot.minX) - 1 : (alongX ? plot.maxZ : plot.maxX) + 1;
                        if (dir > 0 ? near < to : near > to) { to = near; }
                    }
                    if ((dir > 0 ? to < from : to > from) || Math.abs(to - from) + 1 < shortest) {
                        row += 7;
                        continue;
                    }
                    StructureBoundingBox alley = new StructureBoundingBox(
                            alongX ? row - 1 : Math.min(from, to), box.minY, alongX ? Math.min(from, to) : row - 1,
                            alongX ? row + 1 : Math.max(from, to), box.maxY, alongX ? Math.max(from, to) : row + 1);
                    boolean onPlaza = false;
                    for (StructureBoundingBox square : BeardPlots.plazaSquares(components)) {
                        if (square.intersectsWith(alley.minX, alley.minZ, alley.maxX, alley.maxZ)) {
                            onPlaza = true;
                            break;
                        }
                    }
                    if (onPlaza) {
                        row += 7;
                        continue;
                    }
                    EnumFacing facing = alongX ? (outward ? EnumFacing.SOUTH : EnumFacing.NORTH) : (outward ? EnumFacing.EAST : EnumFacing.WEST);
                    StructureVillagePieces.Path lane = new StructureVillagePieces.Path(startPiece, 0, rand, alley, facing);
                    components.add(lane);
                    laying = true;
                    try {
                        lane.buildComponent(startPiece, components, rand);
                        drain(startPiece, components, rand);
                    }
                    finally { laying = false; }
                    laid++;
                    ContentLog.LOGGER.debug("An alley {} long fills the block beside the road at {}, {}, running from {}, {}", length, box.minX, box.minZ, alley.minX, alley.minZ);
                    row += spacing;
                }
            }
        }
        if (laid > 0) { ((IStructureStartGrow) held).rdpl$updateBoundingBox(); }
    }

    private static int alleyRun(List<StructureComponent> components, boolean alongX, int row, int edge, int dir, int longest) {
        int nearAny = Integer.MAX_VALUE;
        int nearPath = Integer.MAX_VALUE;
        for (StructureComponent other : components) {
            StructureBoundingBox met = other.getBoundingBox();
            int acrossLo = alongX ? met.minX : met.minZ;
            int acrossHi = alongX ? met.maxX : met.maxZ;
            if (acrossHi < row - 2 || acrossLo > row + 2) { continue; }
            int alongLo = alongX ? met.minZ : met.minX;
            int alongHi = alongX ? met.maxZ : met.maxX;
            int away = dir > 0 ? alongLo - edge : edge - alongHi;
            if (away < 1) {
                if (dir > 0 ? alongHi >= edge + 1 : alongLo <= edge - 1) { return 0; }
                continue;
            }
            nearAny = Math.min(nearAny, away);
            if (other instanceof StructureVillagePieces.Path) { nearPath = Math.min(nearPath, away); }
        }
        if (nearAny > longest + 14) { return 0; }
        int length = Math.min(longest, nearAny - 2);
        if (nearPath != Integer.MAX_VALUE) { length = Math.min(length, nearPath - ContentBeard.attachGap() - 1); }
        return length;
    }

    private static boolean crowdedLane(List<StructureComponent> components, boolean alongX, int row, int edge, int dir, int length, int spacing) {
        int from = Math.min(edge + dir, edge + dir * length);
        int to = Math.max(edge + dir, edge + dir * length);
        for (StructureComponent other : components) {
            if (!(other instanceof StructureVillagePieces.Path)) { continue; }
            StructureBoundingBox met = other.getBoundingBox();
            if (!BeardRoads.roadNarrow(met, BeardPlots.roadAlongX(met)) || bulbWide(other)) { continue; }
            if (BeardPlots.roadAlongX(met) == alongX) { continue; }
            int center = alongX ? (met.minX + met.maxX) / 2 : (met.minZ + met.maxZ) / 2;
            if (Math.abs(center - row) >= spacing) { continue; }
            int alongLo = alongX ? met.minZ : met.minX;
            int alongHi = alongX ? met.maxZ : met.maxX;
            if (alongHi >= from && alongLo <= to) { return true; }
        }
        return false;
    }

    public static void roadsFirst(StructureStart held) {
        List<StructureComponent> components = held.getComponents();
        if (components.size() < 3 || !(components.get(0) instanceof StructureVillagePieces.Start)) { return; }
        List<StructureComponent> sorted = new ArrayList<>(components.size());
        sorted.add(components.get(0));
        for (StructureComponent piece : components) {
            if (piece != components.get(0) && piece instanceof StructureVillagePieces.Path) { sorted.add(piece); }
        }
        for (StructureComponent piece : components) {
            if (piece != components.get(0) && !(piece instanceof StructureVillagePieces.Path)) { sorted.add(piece); }
        }
        components.clear();
        components.addAll(sorted);
        ContentLog.LOGGER.debug("The village at chunk {}, {} builds its {} road(s) before its plots, so a plot settles onto ground the roads have already laid", held.getChunkPosX(), held.getChunkPosZ(), sorted.size());
    }

    private static boolean metAtEnd(World world, StructureStart held, StructureComponent piece, boolean alongX, int end, int acrossLeast, int acrossMost) {
        if (metIn(held, piece, alongX, end, acrossLeast, acrossMost)) { return true; }
        for (StructureStart village : ContentStructureSearch.villageStarts(world)) {
            if (village == held) { continue; }
            if (metIn(village, piece, alongX, end, acrossLeast, acrossMost)) { return true; }
        }
        return false;
    }

    private static boolean metIn(StructureStart village, StructureComponent piece, boolean alongX, int end, int acrossLeast, int acrossMost) {
        for (StructureComponent other : village.getComponents()) {
            if (other == piece || !(other instanceof StructureVillagePieces.Path)) { continue; }
            StructureBoundingBox road = other.getBoundingBox();
            if (BeardPlots.roadAlongX(road) == alongX) { continue; }
            int least = alongX ? road.minX : road.minZ;
            int most = alongX ? road.maxX : road.maxZ;
            if (least - 1 > end || most + 1 < end) { continue; }
            if ((alongX ? road.maxZ : road.maxX) + 1 < acrossLeast || (alongX ? road.minZ : road.minX) - 1 > acrossMost) { continue; }
            ContentLog.LOGGER.debug("The end of the road at {}, {} is a junction with the road at {}, {}, so it is no dead end and grows no cul-de-sac", piece.getBoundingBox().minX, piece.getBoundingBox().minZ, road.minX, road.minZ);
            return true;
        }
        return false;
    }

    private static StructureBoundingBox court(StructureBoundingBox box, boolean alongX, int end, int dir, boolean outward, int center, int half, int stem) {
        int span = stem + 2 * half + 1;
        int lo = end + dir * (outward ? 1 : span);
        int hi = end + dir * (outward ? span : 1);
        return new StructureBoundingBox(
                alongX ? Math.min(lo, hi) : center - half, box.minY, alongX ? center - half : Math.min(lo, hi),
                alongX ? Math.max(lo, hi) : center + half, box.maxY, alongX ? center + half : Math.max(lo, hi));
    }

    @Nullable private static List<StructureComponent> yielding(World world, StructureStart held, StructureComponent piece, StructureBoundingBox bulb) {
        for (StructureStart village : ContentStructureSearch.villageStarts(world)) {
            if (village == held) { continue; }
            for (StructureComponent other : village.getComponents()) {
                if (other.getBoundingBox().intersectsWith(bulb.minX, bulb.minZ, bulb.maxX, bulb.maxZ)) { return null; }
            }
        }
        List<StructureComponent> plots = new ArrayList<>();
        for (StructureComponent other : held.getComponents()) {
            if (other == piece || !other.getBoundingBox().intersectsWith(bulb.minX, bulb.minZ, bulb.maxX, bulb.maxZ)) { continue; }
            if (other instanceof StructureVillagePieces.Well) { return null; }
            if (other instanceof StructureVillagePieces.Path) {
                if (BeardRoads.roadNarrow(other.getBoundingBox(), BeardPlots.roadAlongX(other))) { continue; }
                return null;
            }
            plots.add(other);
        }
        return plots;
    }

    private static void makeWay(World world, StructureStart held, StructureComponent plot, StructureBoundingBox bulb) {
        StructureBoundingBox box = plot.getBoundingBox();
        int[][] pushes = {
                { bulb.maxX + 1 - box.minX, 0 }, { bulb.minX - 1 - box.maxX, 0 },
                { 0, bulb.maxZ + 1 - box.minZ }, { 0, bulb.minZ - 1 - box.maxZ } };
        Arrays.sort(pushes, Comparator.comparingInt(a -> Math.abs(a[0]) + Math.abs(a[1])));
        for (int[] push : pushes) {
            StructureBoundingBox tried = new StructureBoundingBox(box);
            tried.offset(push[0], 0, push[1]);
            if (!standsFree(world, held, plot, tried)) { continue; }
            box.offset(push[0], 0, push[1]);
            ContentLog.LOGGER.debug("{} at {}, {} slides {}, {} out of the cul-de-sac at {}, {}", plot.getClass().getSimpleName(), box.minX, box.minZ, push[0], push[1], bulb.minX, bulb.minZ);
            return;
        }
        held.getComponents().remove(plot);
        ContentLog.LOGGER.debug("{} at {}, {} makes way for the cul-de-sac at {}, {}, having no room to slide out of it", plot.getClass().getSimpleName(), box.minX, box.minZ, bulb.minX, bulb.minZ);
    }

    private static boolean standsFree(World world, StructureStart held, StructureComponent plot, StructureBoundingBox tried) {
        for (StructureComponent other : held.getComponents()) {
            if (other != plot && other.getBoundingBox().intersectsWith(tried.minX, tried.minZ, tried.maxX, tried.maxZ)) { return false; }
        }
        for (StructureStart village : ContentStructureSearch.villageStarts(world)) {
            if (village == held) { continue; }
            for (StructureComponent other : village.getComponents()) {
                if (other.getBoundingBox().intersectsWith(tried.minX, tried.minZ, tried.maxX, tried.maxZ)) { return false; }
            }
        }
        return true;
    }

    public static boolean bulbWide(StructureComponent piece) {
        StructureBoundingBox box = piece.getBoundingBox();
        return Math.min(box.maxX - box.minX, box.maxZ - box.minZ) + 1 > BeardRoads.pathFullWidth();
    }

    private static boolean seated(World world, StructureComponent road, boolean alongX, int end, int discX, int discZ, int radius) {
        BeardRoads.Grade grade = BeardRoads.chainGrade(world, road, alongX);
        int level = grade == null ? Integer.MIN_VALUE : grade.at(end);
        if (level == Integer.MIN_VALUE) { level = BeardSurface.surfaceAt(world, discX, discZ); }
        int reach = radius + VERGE;
        for (int z = discZ - reach; z <= discZ + reach; z += 2) {
            for (int x = discX - reach; x <= discX + reach; x += 2) {
                if ((x - discX) * (x - discX) + (z - discZ) * (z - discZ) > reach * reach + reach) { continue; }
                int ground = BeardSurface.surfaceAt(world, x, z);
                if (level - ground > BeardGrade.CAP || ground - level > DIG) { return false; }
            }
        }
        return true;
    }

    private static boolean clearFor(World world, StructureStart held, StructureComponent piece, int discX, int discZ, int radius) {
        if (nearDisc(held, piece, discX, discZ, radius)) { return false; }
        for (StructureStart village : ContentStructureSearch.villageStarts(world)) {
            if (village == held) { continue; }
            if (nearDisc(village, piece, discX, discZ, radius)) { return false; }
        }
        return true;
    }

    private static boolean nearDisc(StructureStart village, StructureComponent piece, int discX, int discZ, int radius) {
        for (StructureComponent other : village.getComponents()) {
            if (other == piece) { continue; }
            StructureBoundingBox held = other.getBoundingBox();
            int nearX = Math.max(held.minX, Math.min(discX, held.maxX));
            int nearZ = Math.max(held.minZ, Math.min(discZ, held.maxZ));
            int away = (nearX - discX) * (nearX - discX) + (nearZ - discZ) * (nearZ - discZ);
            if (away <= (radius + 1) * (radius + 1)) { return true; }
        }
        return false;
    }

    private static int spacing() { return 2 * ContentVillages.largestPlot() + 2 * ContentBeard.plazaReach() + 2 * BeardRoads.pathFullWidth(); }

    private static long site(List<StructureComponent> components, Set<Long> tried) {
        StructureBoundingBox first = components.get(0).getBoundingBox();
        int cx = (first.minX + first.maxX) / 2;
        int cz = (first.minZ + first.maxZ) / 2;
        long best = Long.MIN_VALUE;
        long bestAway = Long.MAX_VALUE;
        int reach = march();
        for (StructureComponent piece : components) {
            if (!(piece instanceof StructureVillagePieces.Path)) { continue; }
            StructureBoundingBox road = piece.getBoundingBox();
            boolean alongX = BeardPlots.roadAlongX(road);
            int line = alongX ? (road.minZ + road.maxZ) / 2 : (road.minX + road.maxX) / 2;
            for (int sign = -1; sign <= 1; sign += 2) {
                int edge = alongX ? (sign < 0 ? road.minX : road.maxX) : (sign < 0 ? road.minZ : road.maxZ);
                for (int away = ContentBeard.plazaReach() + 4; away <= Math.max(48, spacing() + 16); away += 8) {
                    int siteX = alongX ? edge + sign * away : line;
                    int siteZ = alongX ? line : edge + sign * away;
                    long dx = siteX - cx;
                    long dz = siteZ - cz;
                    long far = dx * dx + dz * dz;
                    if (far > (long) reach * reach || far >= bestAway) { continue; }
                    long packed = ((long) siteX << 32) ^ (siteZ & 0xFFFFFFFFL);
                    if (tried.contains(packed) || crowdedAt(components, siteX, siteZ, spacing())) { continue; }
                    bestAway = far;
                    best = packed;
                }
            }
        }
        if (best != Long.MIN_VALUE) { return best; }
        int step = Math.max(8, spacing() / 2);
        for (int dx = -reach; dx <= reach; dx += step) {
            for (int dz = -reach; dz <= reach; dz += step) {
                if (dx == 0 && dz == 0) { continue; }
                long away = (long) dx * dx + (long) dz * dz;
                if (away > (long) reach * reach || away >= bestAway) { continue; }
                long packed = ((long) (cx + dx) << 32) ^ ((cz + dz) & 0xFFFFFFFFL);
                if (tried.contains(packed) || crowdedAt(components, cx + dx, cz + dz, spacing())) { continue; }
                bestAway = away;
                best = packed;
            }
        }
        return best;
    }

    private static int settledCrowded;
    private static int settledUngrounded;
    private static int settledTaken;
    private static int settledNeighbored;

    private static boolean settle(World world, Random rand, List<StructureComponent> components, int cx, int cz, int sizeFor) {
        int reach = ContentBeard.plazaReach();
        if (crowdedAt(components, cx, cz, spacing())) {
            settledCrowded++;
            return false;
        }
        int wellX = cx - 2;
        int wellZ = cz - 2;
        if (!grounded(world, wellX, wellZ, reach)) {
            settledUngrounded++;
            return false;
        }
        StructureBoundingBox square = new StructureBoundingBox(wellX - reach, 0, wellZ - reach, wellX + 5 + reach, 255, wellZ + 5 + reach);
        for (StructureComponent other : components) {
            if (other.getBoundingBox().intersectsWith(square)) {
                settledTaken++;
                return false;
            }
        }
        for (StructureStart neighbor : ContentStructureSearch.villageStarts(world)) {
            if (neighbor.getComponents() == components || !neighbor.getBoundingBox().intersectsWith(square)) { continue; }
            for (StructureComponent other : neighbor.getComponents()) {
                if (other.getBoundingBox().intersectsWith(square.minX, square.minZ, square.maxX, square.maxZ)) {
                    settledNeighbored++;
                    return false;
                }
            }
        }
        List<StructureVillagePieces.PieceWeight> weights = StructureVillagePieces.getStructureVillageWeightedPieceList(rand, sizeFor);
        StructureVillagePieces.Start district = new StructureVillagePieces.Start(world.getBiomeProvider(), 0, rand, wellX, wellZ, weights, sizeFor);
        ContentVillages.sizeBlock(world, district);
        int mark = components.size();
        components.add(district);
        laying = true;
        try {
            district.buildComponent(district, components, rand);
            drain(district, components, rand);
        }
        finally { laying = false; }
        int streets = 0;
        for (int i = mark; i < components.size(); i++) { if (components.get(i) instanceof StructureVillagePieces.Path) { streets++; } }
        if (streets == 0) {
            ContentLog.LOGGER.debug("The plaza at {}, {} could not grow a single street, so it is taken back down", wellX, wellZ);
            components.subList(mark, components.size()).clear();
            return false;
        }
        int rounds = 0;
        while (!connected(components, mark)) {
            int before = components.size();
            if (rounds < JOIN) {
                rounds++;
                laying = true;
                try {
                    for (StructureComponent piece : components.subList(mark, before).toArray(new StructureComponent[0])) {
                        if (!(piece instanceof StructureVillagePieces.Path)) { continue; }
                        piece.buildComponent(district, components, rand);
                        drain(district, components, rand);
                    }
                }
                finally { laying = false; }
            }
            if (components.size() == before) {
                ContentLog.LOGGER.debug("The district at {}, {} could not join its streets to the standing village, so it is taken back down", wellX, wellZ);
                components.subList(mark, components.size()).clear();
                return false;
            }
        }
        if (rounds > 0) { ContentLog.LOGGER.debug("The district at {}, {} joined the standing village after {} round(s) of street growth", wellX, wellZ, rounds); }
        int shift = BeardSite.wellGround(world, district.getBoundingBox()) - BeardSite.wellNominal(district.getBoundingBox());
        if (shift != 0) {
            for (int i = mark; i < components.size(); i++) { components.get(i).getBoundingBox().offset(0, shift, 0); }
        }
        return true;
    }

    private static void drain(StructureVillagePieces.Start start, List<StructureComponent> components, Random rand) {
        while (!start.pendingRoads.isEmpty() || !start.pendingHouses.isEmpty()) {
            if (start.pendingRoads.isEmpty()) { start.pendingHouses.remove(rand.nextInt(start.pendingHouses.size())).buildComponent(start, components, rand); }
            else { start.pendingRoads.remove(rand.nextInt(start.pendingRoads.size())).buildComponent(start, components, rand); }
        }
    }

    private static int infill(Random rand, List<StructureComponent> components, int least, int sizeFor) {
        int built = ContentVillages.plots(components);
        laying = true;
        try {
            for (int round = 0; round < 16 && built < least; round++) {
                for (StructureComponent piece : components.toArray(new StructureComponent[0])) {
                    if (piece instanceof StructureVillagePieces.Start) { ((StructureVillagePieces.Start) piece).structureVillageWeightedPieceList = StructureVillagePieces.getStructureVillageWeightedPieceList(rand, sizeFor); }
                }
                for (StructureComponent piece : components.toArray(new StructureComponent[0])) {
                    if (!(piece instanceof StructureVillagePieces.Path)) { continue; }
                    StructureVillagePieces.Start near = nearestStart(components, piece.getBoundingBox());
                    if (near == null) { continue; }
                    piece.buildComponent(near, components, rand);
                    drain(near, components, rand);
                }
                int now = ContentVillages.plots(components);
                if (now == built) {
                    if (give >= 6) { break; }
                    give += 2;
                    ContentLog.LOGGER.debug("The village stands short at {} plot(s), so infill may now move {} block(s) more earth to terrace its lots", built, give);
                }
                else { ContentLog.LOGGER.debug("An infill round along the standing streets raised the village to {} plot(s) against the asked minimum of {}", now, least); }
                built = now;
            }
        }
        finally {
            laying = false;
            give = 0;
        }
        return built;
    }

    private static boolean connected(List<StructureComponent> own, int mark) {
        List<StructureComponent> components = ContentBeard.everyone(own);
        int count = components.size();
        int standing = own.size();
        int reach = ContentBeard.plazaReach();
        StructureBoundingBox[] boxes = new StructureBoundingBox[count];
        for (int i = 0; i < count; i++) {
            StructureComponent piece = components.get(i);
            if (piece instanceof StructureVillagePieces.Well) {
                StructureBoundingBox well = piece.getBoundingBox();
                boxes[i] = new StructureBoundingBox(well.minX - reach, well.minY, well.minZ - reach, well.maxX + reach, well.maxY, well.maxZ + reach);
            }
            else if (piece instanceof StructureVillagePieces.Path) {
                StructureBoundingBox road = piece.getBoundingBox();
                if (!BeardRoads.roadNarrow(road, BeardPlots.roadAlongX(road))) { boxes[i] = road; }
            }
        }
        boolean[] seen = new boolean[count];
        ArrayDeque<Integer> queue = new ArrayDeque<>();
        seen[mark] = true;
        queue.add(mark);
        while (!queue.isEmpty()) {
            int at = queue.poll();
            for (int i = 0; i < count; i++) {
                if (seen[i] || boxes[i] == null) { continue; }
                boolean touching = boxes[at].intersectsWith(boxes[i].minX - 1, boxes[i].minZ - 1, boxes[i].maxX + 1, boxes[i].maxZ + 1);
                if (!touching && !ContentBeard.joins(components, components.get(at), components.get(i))) { continue; }
                if (i < mark || i >= standing) { return true; }
                seen[i] = true;
                queue.add(i);
            }
        }
        return false;
    }

    @Nullable private static StructureVillagePieces.Start nearestStart(List<StructureComponent> components, StructureBoundingBox box) {
        StructureVillagePieces.Start best = null;
        long bestAway = Long.MAX_VALUE;
        int x = (box.minX + box.maxX) / 2;
        int z = (box.minZ + box.maxZ) / 2;
        for (StructureComponent piece : components) {
            if (!(piece instanceof StructureVillagePieces.Start)) { continue; }
            StructureBoundingBox well = piece.getBoundingBox();
            int wx = (well.minX + well.maxX) / 2;
            int wz = (well.minZ + well.maxZ) / 2;
            long away = (long) (x - wx) * (x - wx) + (long) (z - wz) * (z - wz);
            if (away < bestAway) {
                bestAway = away;
                best = (StructureVillagePieces.Start) piece;
            }
        }
        return best;
    }

    private static boolean crowdedAt(List<StructureComponent> components, int cx, int cz, int spacing) {
        for (StructureBoundingBox well : BeardPlots.wellBoxes(components)) {
            long dx = (well.minX + well.maxX) / 2 - cx;
            long dz = (well.minZ + well.maxZ) / 2 - cz;
            if (dx * dx + dz * dz < (long) spacing * spacing) { return true; }
        }
        return false;
    }

    private static boolean grounded(World world, int wellX, int wellZ, int reach) {
        if (BeardSurface.unreadable(world)) { return true; }
        int sea = world.getSeaLevel();
        int lowest = Integer.MAX_VALUE;
        int highest = Integer.MIN_VALUE;
        int[] marks = {-reach, 2, 5 + reach};
        for (int dx : marks) {
            for (int dz : marks) {
                int found = BeardSurface.surfaceAt(world, wellX + dx, wellZ + dz);
                if (found < sea - 1) { return false; }
                lowest = Math.min(lowest, found);
                highest = Math.max(highest, found);
            }
        }
        return highest - lowest <= RELIEF;
    }
}
