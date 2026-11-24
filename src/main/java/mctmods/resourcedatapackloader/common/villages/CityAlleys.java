package mctmods.resourcedatapackloader.common.villages;

import mctmods.resourcedatapackloader.common.util.world.Facings;
import mctmods.resourcedatapackloader.common.util.world.StructureBoxes;
import mctmods.resourcedatapackloader.common.worldgen.structure.city.ContentBeard;
import mctmods.resourcedatapackloader.common.worldgen.structure.city.plot.BeardPlots;
import mctmods.resourcedatapackloader.common.worldgen.structure.city.road.BeardRoads;
import mctmods.resourcedatapackloader.common.worldgen.structure.city.road.BeardRoadsTunnels;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.util.*;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraft.world.gen.structure.StructureVillagePieces;

public final class CityAlleys {
    private CityAlleys() {}

    public static void alleyFill(StructureStart held, Random rand) {
        if (!ContentBeard.wanted() || BeardRoads.alleyChance() <= 0 || CityLayout.drawn()) { return; }
        List<StructureComponent> components = held.getComponents();
        StructureVillagePieces.Start startPiece = CityGrowth.startOf(components);
        if (startPiece == null) { return; }
        int shortest = 14;
        int longest = 42;
        int laid = 0;
        for (StructureComponent piece : components.toArray(new StructureComponent[0])) {
            if (!(piece instanceof StructureVillagePieces.Path) || CityGrowth.bulbWide(piece)) { continue; }
            StructureBoundingBox box = piece.getBoundingBox();
            boolean alongX = BeardPlots.roadAlongX(piece);
            if (BeardRoads.roadNarrow(piece)) { continue; }
            int spacing = 2 * ContentVillages.blockOf(piece);
            int lo = BeardPlots.least(box, alongX) + 2;
            int hi = BeardPlots.most(box, alongX) - 2;
            for (int side = 0; side < 2; side++) {
                boolean outward = side == 1;
                int edge = outward ? BeardPlots.acrossMost(box, alongX) : BeardPlots.acrossLeast(box, alongX);
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
                        int near = dir > 0 ? BeardPlots.acrossLeast(plot, alongX) - 1 : BeardPlots.acrossMost(plot, alongX) + 1;
                        if (dir > 0 ? near < to : near > to) { to = near; }
                    }
                    if ((dir > 0 ? to < from : to > from) || Math.abs(to - from) + 1 < shortest) {
                        row += 7;
                        continue;
                    }
                    StructureBoundingBox alley = ContentBeard.span(!alongX, Math.min(from, to), Math.max(from, to), row - 1, row + 1, box.minY, box.maxY);
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
                    EnumFacing facing = Facings.across(alongX, outward);
                    StructureVillagePieces.Path lane = new StructureVillagePieces.Path(startPiece, 0, rand, alley, facing);
                    components.add(lane);
                    CityGrowth.laying(false, () -> CityGrowth.buildAndDrain(startPiece, lane, components, rand));
                    laid++;
                    ContentLog.LOGGER.debug("An alley {} long fills the block beside the road at {}, {}, running from {}, {}", length, box.minX, box.minZ, alley.minX, alley.minZ);
                    row += spacing;
                }
            }
        }
        if (laid > 0) { held.updateBoundingBox(); }
    }

    private static int alleyRun(List<StructureComponent> components, boolean alongX, int row, int edge, int dir, int longest) {
        int nearAny = Integer.MAX_VALUE;
        int nearPath = Integer.MAX_VALUE;
        for (StructureComponent other : components) {
            StructureBoundingBox met = other.getBoundingBox();
            if (BeardPlots.most(met, alongX) < row - 2 || BeardPlots.least(met, alongX) > row + 2) { continue; }
            int alongLo = BeardPlots.acrossLeast(met, alongX);
            int alongHi = BeardPlots.acrossMost(met, alongX);
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
            int center = StructureBoxes.across(met, !alongX);
            if (Math.abs(center - row) >= spacing) { continue; }
            int alongLo = BeardPlots.acrossLeast(met, alongX);
            int alongHi = BeardPlots.acrossMost(met, alongX);
            if (alongHi >= from && alongLo <= to) { return true; }
        }
        return false;
    }
}
