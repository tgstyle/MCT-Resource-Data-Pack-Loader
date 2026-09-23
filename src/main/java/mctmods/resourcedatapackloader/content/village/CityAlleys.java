package mctmods.resourcedatapackloader.content.village;

import mctmods.resourcedatapackloader.content.worldgen.ContentBeard;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardPlots;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardRoads;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardRoadsTunnels;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IStructureStartGrow;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.util.EnumFacing;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import java.util.*;

public final class CityAlleys {
    private CityAlleys() {}

    public static void alleyFill(StructureStart held, Random rand) {
        if (!ContentBeard.wanted() || BeardRoads.alleyChance() <= 0 || CityLayout.drawn()) { return; }
        List<StructureComponent> components = held.getComponents();
        if (components.isEmpty() || !(components.get(0) instanceof StructureVillagePieces.Start)) { return; }
        StructureVillagePieces.Start startPiece = (StructureVillagePieces.Start) components.get(0);
        int shortest = 14;
        int longest = 42;
        int laid = 0;
        for (StructureComponent piece : components.toArray(new StructureComponent[0])) {
            if (!(piece instanceof StructureVillagePieces.Path) || CityGrowth.bulbWide(piece)) { continue; }
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
                    if (onPlaza || ContentBeard.hugsStreet(components, alley, !alongX) || BeardRoadsTunnels.crossesHill(components, alley)) {
                        row += 7;
                        continue;
                    }
                    EnumFacing facing = alongX ? (outward ? EnumFacing.SOUTH : EnumFacing.NORTH) : (outward ? EnumFacing.EAST : EnumFacing.WEST);
                    StructureVillagePieces.Path lane = new StructureVillagePieces.Path(startPiece, 0, rand, alley, facing);
                    components.add(lane);
                    CityGrowth.laying = true;
                    try {
                        lane.buildComponent(startPiece, components, rand);
                        CityGrowth.drain(startPiece, components, rand);
                    }
                    finally { CityGrowth.laying = false; }
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
            if (!BeardRoads.roadNarrow(met, BeardPlots.roadAlongX(met)) || CityGrowth.bulbWide(other)) { continue; }
            if (BeardPlots.roadAlongX(met) == alongX) { continue; }
            int center = alongX ? (met.minX + met.maxX) / 2 : (met.minZ + met.maxZ) / 2;
            if (Math.abs(center - row) >= spacing) { continue; }
            int alongLo = alongX ? met.minZ : met.minX;
            int alongHi = alongX ? met.maxZ : met.maxX;
            if (alongHi >= from && alongLo <= to) { return true; }
        }
        return false;
    }
}
