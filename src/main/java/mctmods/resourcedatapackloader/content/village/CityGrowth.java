package mctmods.resourcedatapackloader.content.village;

import mctmods.resourcedatapackloader.content.worldgen.ContentBeard;
import mctmods.resourcedatapackloader.content.worldgen.ContentStructureSearch;
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
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;

public final class CityGrowth {
    public static final int MARCH = 112;
    private static final int TRIES = 32;
    private static final int RELIEF = 6;
    private static final int JOIN = 4;
    private static boolean laying;
    private static boolean bulbLaying;
    private static int give;

    private CityGrowth() {}

    public static boolean laying() { return laying; }

    public static boolean bulbLaying() { return bulbLaying; }

    public static int give() { return give; }

    public static int chunkRange() { return (MARCH + 112 + 16) >> 4; }

    public static void grow(StructureStart held, World world, Random rand, int size) {
        int least = ContentVillages.plotsLeast();
        if (least <= 0) { return; }
        List<StructureComponent> components = held.getComponents();
        if (components.isEmpty()) { return; }
        int built = ContentVillages.plots(components);
        if (built >= least) { return; }
        int sizeFor = size + Math.max(1, least / 32);
        Set<Long> tried = new HashSet<>();
        int districts = 0;
        int attempts = 0;
        while (built < least && attempts < 8 * TRIES) {
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
        if (built < least) { built = infill(rand, components, least, sizeFor); }
        ((IStructureStartGrow) held).rdpl$updateBoundingBox();
        ContentLog.LOGGER.debug("The village at chunk {}, {} grew {} district(s) to {} plot(s) against the asked minimum of {}", held.getChunkPosX(), held.getChunkPosZ(), districts, built, least);
    }

    public static void culDeSacs(StructureStart held, World world, Random rand) {
        if (!ContentBeard.wanted() || BeardSurface.samplerFor(world) == null) { return; }
        List<StructureComponent> components = held.getComponents();
        if (components.isEmpty() || !(components.get(0) instanceof StructureVillagePieces.Start)) { return; }
        StructureVillagePieces.Start startPiece = (StructureVillagePieces.Start) components.get(0);
        int wide = BeardRoads.pathFullWidth() + 8;
        int sea = world.getSeaLevel();
        int bulbs = 0;
        for (StructureComponent piece : components.toArray(new StructureComponent[0])) {
            if (!(piece instanceof StructureVillagePieces.Path) || bulbWide(piece)) { continue; }
            StructureBoundingBox box = piece.getBoundingBox();
            boolean alongX = BeardPlots.roadAlongX(piece);
            int center = alongX ? (box.minZ + box.maxZ) / 2 : (box.minX + box.maxX) / 2;
            for (int side = 0; side < 2; side++) {
                boolean outward = side == 1;
                int dir = outward ? 1 : -1;
                int end = alongX ? (outward ? box.maxX : box.minX) : (outward ? box.maxZ : box.minZ);
                int endX = alongX ? end : center;
                int endZ = alongX ? center : end;
                if (metAtEnd(world, held, piece, alongX, end, alongX ? box.minZ : box.minX, alongX ? box.maxZ : box.maxX)) { continue; }
                if (SeededRandom.at(world, endX + dir, endZ + dir).nextInt(4) >= 3) { continue; }
                int least = (BeardRoads.pathFullWidth() + 1) / 2 + 1;
                int found = 0;
                int stem = 0;
                for (int r = (wide - 1) / 2; r >= least && found == 0; r--) {
                    for (int out = 1; out <= 7; out += 3) {
                        int discX = alongX ? end + dir * (out + r + 1) : center;
                        int discZ = alongX ? center : end + dir * (out + r + 1);
                        if (clearFor(world, held, piece, discX, discZ, r)) {
                            found = r;
                            stem = out;
                            break;
                        }
                    }
                }
                if (found == 0) { continue; }
                int half = found;
                int span = stem + 2 * half + 1;
                int lo = end + dir * (outward ? 1 : span);
                int hi = end + dir * (outward ? span : 1);
                StructureBoundingBox bulb = new StructureBoundingBox(
                        alongX ? Math.min(lo, hi) : center - half, box.minY, alongX ? center - half : Math.min(lo, hi),
                        alongX ? Math.max(lo, hi) : center + half, box.maxY, alongX ? center + half : Math.max(lo, hi));
                int discX = alongX ? end + dir * (stem + half + 1) : center;
                int discZ = alongX ? center : end + dir * (stem + half + 1);
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
                EnumFacing facing = alongX ? (outward ? EnumFacing.EAST : EnumFacing.WEST) : (outward ? EnumFacing.SOUTH : EnumFacing.NORTH);
                StructureVillagePieces.Path bulbPiece = new StructureVillagePieces.Path(startPiece, 0, rand, bulb, facing);
                components.add(bulbPiece);
                laying = true;
                bulbLaying = true;
                try {
                    bulbPiece.buildComponent(startPiece, components, rand);
                    drain(startPiece, components, rand);
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

    public static boolean bulbWide(StructureComponent piece) {
        StructureBoundingBox box = piece.getBoundingBox();
        return Math.min(box.maxX - box.minX, box.maxZ - box.minZ) + 1 > BeardRoads.pathFullWidth();
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
        for (StructureComponent piece : components) {
            if (!(piece instanceof StructureVillagePieces.Path)) { continue; }
            StructureBoundingBox road = piece.getBoundingBox();
            boolean alongX = BeardPlots.roadAlongX(road);
            int line = alongX ? (road.minZ + road.maxZ) / 2 : (road.minX + road.maxX) / 2;
            for (int sign = -1; sign <= 1; sign += 2) {
                int edge = alongX ? (sign < 0 ? road.minX : road.maxX) : (sign < 0 ? road.minZ : road.maxZ);
                for (int away = ContentBeard.plazaReach() + 4; away <= 48; away += 8) {
                    int siteX = alongX ? edge + sign * away : line;
                    int siteZ = alongX ? line : edge + sign * away;
                    long dx = siteX - cx;
                    long dz = siteZ - cz;
                    long far = dx * dx + dz * dz;
                    if (far > (long) MARCH * MARCH || far >= bestAway) { continue; }
                    long packed = ((long) siteX << 32) ^ (siteZ & 0xFFFFFFFFL);
                    if (tried.contains(packed)) { continue; }
                    bestAway = far;
                    best = packed;
                }
            }
        }
        if (best != Long.MIN_VALUE) { return best; }
        int step = Math.max(8, spacing() / 2);
        for (int dx = -MARCH; dx <= MARCH; dx += step) {
            for (int dz = -MARCH; dz <= MARCH; dz += step) {
                if (dx == 0 && dz == 0) { continue; }
                long away = (long) dx * dx + (long) dz * dz;
                if (away > (long) MARCH * MARCH || away >= bestAway) { continue; }
                long packed = ((long) (cx + dx) << 32) ^ ((cz + dz) & 0xFFFFFFFFL);
                if (tried.contains(packed)) { continue; }
                bestAway = away;
                best = packed;
            }
        }
        return best;
    }

    private static boolean settle(World world, Random rand, List<StructureComponent> components, int cx, int cz, int sizeFor) {
        int reach = ContentBeard.plazaReach();
        if (crowdedAt(components, cx, cz, spacing())) { return false; }
        int wellX = cx - 2;
        int wellZ = cz - 2;
        if (!grounded(world, wellX, wellZ, reach)) { return false; }
        StructureBoundingBox square = new StructureBoundingBox(wellX - reach, 0, wellZ - reach, wellX + 5 + reach, 255, wellZ + 5 + reach);
        for (StructureComponent other : components) {
            if (other.getBoundingBox().intersectsWith(square)) { return false; }
        }
        int margin = 2 * ContentVillages.largestPlot();
        StructureBoundingBox held = new StructureBoundingBox(square.minX - margin, 0, square.minZ - margin, square.maxX + margin, 255, square.maxZ + margin);
        for (StructureStart neighbor : ContentStructureSearch.villageStarts(world)) {
            if (neighbor.getBoundingBox().intersectsWith(held)) { return false; }
        }
        List<StructureVillagePieces.PieceWeight> weights = StructureVillagePieces.getStructureVillageWeightedPieceList(rand, sizeFor);
        StructureVillagePieces.Start district = new StructureVillagePieces.Start(world.getBiomeProvider(), 0, rand, wellX, wellZ, weights, sizeFor);
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

    private static boolean connected(List<StructureComponent> components, int mark) {
        int count = components.size();
        int reach = ContentBeard.plazaReach();
        StructureBoundingBox[] boxes = new StructureBoundingBox[count];
        for (int i = 0; i < count; i++) {
            StructureComponent piece = components.get(i);
            if (piece instanceof StructureVillagePieces.Well) {
                StructureBoundingBox well = piece.getBoundingBox();
                boxes[i] = new StructureBoundingBox(well.minX - reach, well.minY, well.minZ - reach, well.maxX + reach, well.maxY, well.maxZ + reach);
            }
            else if (piece instanceof StructureVillagePieces.Path) { boxes[i] = piece.getBoundingBox(); }
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
                if (i < mark) { return true; }
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
        if (BeardSurface.samplerFor(world) == null) { return true; }
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
