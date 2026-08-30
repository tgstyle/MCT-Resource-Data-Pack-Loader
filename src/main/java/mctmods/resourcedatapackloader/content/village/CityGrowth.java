package mctmods.resourcedatapackloader.content.village;

import mctmods.resourcedatapackloader.content.worldgen.ContentBeard;
import mctmods.resourcedatapackloader.content.worldgen.ContentStructureSearch;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardPlots;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardRoads;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardSite;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardSurface;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IStructureStartGrow;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;

public final class CityGrowth {
    public static final int MARCH = 112;
    private static final int TRIES = 32;
    private static final int RELIEF = 6;
    private static boolean laying;
    private static int give;

    private CityGrowth() {}

    public static boolean laying() { return laying; }

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

    private static int spacing() { return 2 * ContentVillages.largestPlot() + 2 * ContentBeard.plazaReach() + 2 * BeardRoads.pathFullWidth(); }

    private static long site(List<StructureComponent> components, Set<Long> tried) {
        StructureBoundingBox first = components.get(0).getBoundingBox();
        int cx = (first.minX + first.maxX) / 2;
        int cz = (first.minZ + first.maxZ) / 2;
        int step = Math.max(8, spacing() / 2);
        long best = Long.MIN_VALUE;
        long bestAway = Long.MAX_VALUE;
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
