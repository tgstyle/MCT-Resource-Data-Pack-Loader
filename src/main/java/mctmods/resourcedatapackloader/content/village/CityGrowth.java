package mctmods.resourcedatapackloader.content.village;

import mctmods.resourcedatapackloader.content.worldgen.ContentBeard;
import mctmods.resourcedatapackloader.content.worldgen.ContentStructureSearch;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardPlots;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardRoads;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardSite;
import mctmods.resourcedatapackloader.content.worldgen.beard.interfaces.RoadLayout;
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
    public static final int MARCH = 224;
    private static final int TRIES = 32;
    private static final int RELIEF = 6;
    private static boolean laying;

    private CityGrowth() {}

    public static boolean laying() { return laying; }

    public static int chunkRange() { return (MARCH + 112 + 16) >> 4; }

    private static final class Anchor {
        final StructureComponent road;
        final int x;
        final int z;
        final boolean alongX;
        final int dir;

        Anchor(StructureComponent road, int x, int z, boolean alongX, int dir) {
            this.road = road;
            this.x = x;
            this.z = z;
            this.alongX = alongX;
            this.dir = dir;
        }
    }

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
        for (int attempt = 0; attempt < TRIES && built < least; attempt++) {
            Anchor next = anchor(components, tried);
            if (next == null) { break; }
            tried.add(((long) next.x << 32) ^ (next.z & 0xFFFFFFFFL));
            if (settle(world, rand, components, next, sizeFor)) {
                districts++;
                built = ContentVillages.plots(components);
            }
        }
        ((IStructureStartGrow) held).rdpl$updateBoundingBox();
        ContentLog.LOGGER.debug("The village at chunk {}, {} grew {} district(s) to {} plot(s) against the asked minimum of {}", held.getChunkPosX(), held.getChunkPosZ(), districts, built, least);
    }

    @Nullable private static Anchor anchor(List<StructureComponent> components, Set<Long> tried) {
        StructureBoundingBox first = components.get(0).getBoundingBox();
        int cx = (first.minX + first.maxX) / 2;
        int cz = (first.minZ + first.maxZ) / 2;
        Anchor best = null;
        long bestAway = Long.MAX_VALUE;
        for (StructureComponent piece : components) {
            if (!(piece instanceof StructureVillagePieces.Path)) { continue; }
            StructureBoundingBox box = piece.getBoundingBox();
            boolean alongX = BeardPlots.roadAlongX(box);
            int acrossLeast = alongX ? box.minZ : box.minX;
            int acrossMost = alongX ? box.maxZ : box.maxX;
            for (int side = 0; side < 2; side++) {
                int dir = side == 0 ? -1 : 1;
                int row = (side == 0 ? (alongX ? box.minX : box.minZ) : (alongX ? box.maxX : box.maxZ)) + dir;
                if (met(components, piece, alongX, row, acrossLeast, acrossMost)) { continue; }
                int x = alongX ? row : (box.minX + box.maxX) / 2;
                int z = alongX ? (box.minZ + box.maxZ) / 2 : row;
                if (tried.contains(((long) x << 32) ^ (z & 0xFFFFFFFFL))) { continue; }
                long away = (long) (x - cx) * (x - cx) + (long) (z - cz) * (z - cz);
                if (away < bestAway) {
                    bestAway = away;
                    best = new Anchor(piece, x, z, alongX, dir);
                }
            }
        }
        return best;
    }

    private static boolean met(List<StructureComponent> components, StructureComponent piece, boolean alongX, int row, int acrossLeast, int acrossMost) {
        for (StructureComponent other : components) {
            if (other == piece) { continue; }
            StructureBoundingBox box = other.getBoundingBox();
            if (row < (alongX ? box.minX : box.minZ) || row > (alongX ? box.maxX : box.maxZ)) { continue; }
            if ((alongX ? box.maxZ : box.maxX) >= acrossLeast && (alongX ? box.minZ : box.minX) <= acrossMost) { return true; }
        }
        return false;
    }

    private static boolean settle(World world, Random rand, List<StructureComponent> components, Anchor anchor, int sizeFor) {
        int reach = ContentBeard.plazaReach();
        int spacing = 2 * ContentVillages.largestPlot() + 2 * ContentBeard.plazaReach() + 2 * BeardRoads.pathFullWidth();
        int acrossCenter = anchor.alongX ? anchor.z : anchor.x;
        int centerAlong = (anchor.alongX ? anchor.x : anchor.z) + anchor.dir * (reach + 2);
        int pushed = 0;
        while (pushed <= spacing && crowdedAt(components, anchor.alongX ? centerAlong : acrossCenter, anchor.alongX ? acrossCenter : centerAlong, spacing)) {
            centerAlong += anchor.dir;
            pushed++;
        }
        if (pushed > spacing) { return false; }
        int wellX = (anchor.alongX ? centerAlong : acrossCenter) - 2;
        int wellZ = (anchor.alongX ? acrossCenter : centerAlong) - 2;
        StructureBoundingBox first = components.get(0).getBoundingBox();
        int fromWell = Math.max(Math.abs(wellX + 2 - (first.minX + first.maxX) / 2), Math.abs(wellZ + 2 - (first.minZ + first.maxZ) / 2));
        if (fromWell > MARCH) {
            ContentLog.LOGGER.debug("A district well at {}, {} would stand {} block(s) out from the primary well, past the {} block march the structure range covers, so it is refused", wellX, wellZ, fromWell, MARCH);
            return false;
        }
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
        StructureBoundingBox road = anchor.road.getBoundingBox();
        StructureBoundingBox street = anchor.alongX
                ? new StructureBoundingBox(anchor.dir > 0 ? anchor.x : wellX + 6, road.minY, road.minZ, anchor.dir > 0 ? wellX - 1 : anchor.x, road.maxY, road.maxZ)
                : new StructureBoundingBox(road.minX, road.minY, anchor.dir > 0 ? anchor.z : wellZ + 6, road.maxX, road.maxY, anchor.dir > 0 ? wellZ - 1 : anchor.z);
        for (StructureComponent other : components) {
            if (other == anchor.road || other instanceof StructureVillagePieces.Path) { continue; }
            if (other.getBoundingBox().intersectsWith(street)) { return false; }
        }
        int grownLeast = anchor.alongX ? Math.min(road.minX, street.minX) : Math.min(road.minZ, street.minZ);
        int grownMost = anchor.alongX ? Math.max(road.maxX, street.maxX) : Math.max(road.maxZ, street.maxZ);
        int ownCenter = anchor.alongX ? (road.minZ + road.maxZ) / 2 : (road.minX + road.maxX) / 2;
        for (StructureComponent other : components) {
            if (other == anchor.road || !(other instanceof StructureVillagePieces.Path)) { continue; }
            StructureBoundingBox kept = other.getBoundingBox();
            if (BeardPlots.roadAlongX(kept) != anchor.alongX) { continue; }
            boolean acrossed = anchor.alongX ? kept.maxZ >= road.minZ && kept.minZ <= road.maxZ : kept.maxX >= road.minX && kept.minX <= road.maxX;
            if (!acrossed) { continue; }
            int gap = anchor.alongX ? Math.max(kept.minX - grownMost, grownLeast - kept.maxX) : Math.max(kept.minZ - grownMost, grownLeast - kept.maxZ);
            if (gap < 2 || gap > BeardRoads.pathFullWidth() + 2) { continue; }
            int keptCenter = anchor.alongX ? (kept.minZ + kept.maxZ) / 2 : (kept.minX + kept.maxX) / 2;
            if (keptCenter != ownCenter) {
                ContentLog.LOGGER.debug("A district off the road at {}, {} is refused: its street would meet the road at {}, {} across a junction with centers {} and {} out of line", road.minX, road.minZ, kept.minX, kept.minZ, ownCenter, keptCenter);
                return false;
            }
        }
        if (anchor.alongX) {
            if (anchor.dir > 0) { road.maxX = wellX - 1; }
            else { road.minX = wellX + 6; }
        }
        else {
            if (anchor.dir > 0) { road.maxZ = wellZ - 1; }
            else { road.minZ = wellZ + 6; }
        }
        if (anchor.road instanceof RoadLayout) { ((RoadLayout) anchor.road).rdpl$layout(null); }
        List<StructureVillagePieces.PieceWeight> weights = StructureVillagePieces.getStructureVillageWeightedPieceList(rand, sizeFor);
        StructureVillagePieces.Start district = new StructureVillagePieces.Start(world.getBiomeProvider(), 0, rand, wellX, wellZ, weights, sizeFor);
        int mark = components.size();
        components.add(district);
        laying = true;
        try {
            district.buildComponent(district, components, rand);
            while (!district.pendingRoads.isEmpty() || !district.pendingHouses.isEmpty()) {
                if (district.pendingRoads.isEmpty()) { district.pendingHouses.remove(rand.nextInt(district.pendingHouses.size())).buildComponent(district, components, rand); }
                else { district.pendingRoads.remove(rand.nextInt(district.pendingRoads.size())).buildComponent(district, components, rand); }
            }
        }
        finally { laying = false; }
        int shift = BeardSite.wellGround(world, district.getBoundingBox()) - BeardSite.wellNominal(district.getBoundingBox());
        if (shift != 0) {
            for (int i = mark; i < components.size(); i++) { components.get(i).getBoundingBox().offset(0, shift, 0); }
        }
        return true;
    }

    private static boolean crowdedAt(List<StructureComponent> components, int cx, int cz, int spacing) {
        for (StructureBoundingBox well : BeardPlots.wellBoxes(components)) {
            if (Math.abs((well.minX + well.maxX) / 2 - cx) < spacing && Math.abs((well.minZ + well.maxZ) / 2 - cz) < spacing) { return true; }
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
