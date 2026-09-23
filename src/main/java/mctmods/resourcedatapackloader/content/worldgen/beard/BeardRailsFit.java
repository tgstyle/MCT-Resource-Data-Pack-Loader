package mctmods.resourcedatapackloader.content.worldgen.beard;

import mctmods.resourcedatapackloader.content.village.CityGrowth;
import mctmods.resourcedatapackloader.content.village.RailPiece;
import mctmods.resourcedatapackloader.content.worldgen.ContentBeard;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.world.GenHeights;

import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class BeardRailsFit {
    private static final int STRETCH = 32;
    private static final int COURSES = 2;

    private BeardRailsFit() {}

    public static void found(World world, StructureStart start, StructureVillagePieces.Start well, Random rand) {
        if (!ContentBeard.wanted() || BeardSurface.unreadable(world)) { return; }
        found(world, start, well, rand, false);
        found(world, start, well, rand, true);
    }

    private static boolean wellAcross(List<StructureComponent> pieces, StructureBoundingBox box, boolean alongX, int center) {
        for (StructureComponent piece : pieces) {
            if (!(piece instanceof StructureVillagePieces.Start)) { continue; }
            StructureBoundingBox well = piece.getBoundingBox();
            if ((alongX ? well.maxX : well.maxZ) < (alongX ? box.minX : box.minZ) || (alongX ? well.minX : well.minZ) > (alongX ? box.maxX : box.maxZ)) { continue; }
            if (!BeardRails.clearsWell(well, alongX, center, true)) { return true; }
        }
        return false;
    }

    private static void found(World world, StructureStart start, StructureVillagePieces.Start well, Random rand, boolean sub) {
        int lines = BeardRails.lines(sub);
        if (lines <= 0) { return; }
        List<StructureComponent> components = start.getComponents();
        StructureBoundingBox wellBox = well.getBoundingBox();
        Random roll = BeardLinks.on() ? BeardLinks.roll(world, wellBox, sub) : rand;
        boolean alongX = BeardRails.direction(roll, sub);
        int wellX = (wellBox.minX + wellBox.maxX) / 2;
        int wellZ = (wellBox.minZ + wellBox.maxZ) / 2;
        int nominal = BeardSite.wellNominal(wellBox);
        int apart = BeardRails.width(sub) + BeardRails.spacing(sub);
        int half = BeardRails.half(sub);
        int clear = BeardRails.clear(sub);
        int reach = CityGrowth.march() + BeardRails.tail(sub) + STRETCH;
        List<Integer> placed = new ArrayList<>();
        for (int line = 0; line < lines; line++) {
            int side = line % 2 == 0 ? 1 : -1;
            int base = alongX ? wellZ : wellX;
            int rolled = side * (clear + (line / 2) * apart + roll.nextInt(Math.max(1, BeardRails.spacing(sub) / 2)));
            int candidate = BeardRails.offWell(wellBox, alongX, base + rolled, side, sub) - base;
            if (candidate != rolled) { ContentLog.LOGGER.debug("Subway line {} of the village at {}, {} rolled onto the well at {} {}, so it is laid {} block(s) further out, at {}, and its corridor leaves the well alone", line, wellX, wellZ, alongX ? "z" : "x", base + rolled, Math.abs(candidate - rolled), base + candidate); }
            boolean moved = true;
            while (moved) {
                moved = false;
                for (int other : placed) {
                    if (Math.abs(candidate - other) >= apart) { continue; }
                    candidate += side * apart;
                    moved = true;
                }
            }
            placed.add(candidate);
            int center = line == 0 && BeardLinks.on() ? BeardLinks.lineCenter(world, wellBox, sub, base + candidate) : base + candidate;
            StructureBoundingBox box = alongX
                    ? new StructureBoundingBox(wellX - reach, nominal - BeardRails.BELOW, center - half, wellX + reach, nominal + BeardRails.ABOVE, center + half)
                    : new StructureBoundingBox(center - half, nominal - BeardRails.BELOW, wellZ - reach, center + half, nominal + BeardRails.ABOVE, wellZ + reach);
            components.add(new RailPiece(well, box, alongX, line, sub));
            ContentLog.LOGGER.debug("{} line {} of the village at {}, {} is laid {} at {} {}, {} wide, at least {} block(s) of ground from any other line of this village, before any street of the village", sub ? "Subway" : "Railway", line, wellX, wellZ, alongX ? "east to west" : "north to south", alongX ? "z" : "x", center, BeardRails.width(sub), BeardRails.spacing(sub));
        }
        BeardLinks.extend(world, start, well, sub);
    }

    private static void seekRoad(List<StructureComponent> components, List<StructureComponent> everyone, RailPiece rail, boolean alongX, int reach) {
        StructureBoundingBox box = rail.getBoundingBox();
        int mine = alongX ? (box.minZ + box.maxZ) / 2 : (box.minX + box.maxX) / 2;
        int best = Integer.MAX_VALUE;
        int wanted = mine;
        for (StructureComponent other : components) {
            if (!(other instanceof StructureVillagePieces.Path)) { continue; }
            StructureBoundingBox road = other.getBoundingBox();
            boolean roadAlongX = road.maxX - road.minX >= road.maxZ - road.minZ;
            if (roadAlongX != alongX) { continue; }
            if (BeardRoads.roadNarrow(road, roadAlongX)) { continue; }
            int center = roadAlongX ? (road.minZ + road.maxZ) / 2 : (road.minX + road.maxX) / 2;
            int off = Math.abs(center - mine);
            if (off > reach || off >= best) { continue; }
            if (wellAcross(everyone, box, alongX, center)) {
                ContentLog.LOGGER.debug("Subway line {} leaves the street at {} {} alone: its corridor there would bore through a village well", rail.line(), alongX ? "z" : "x", center);
                continue;
            }
            best = off;
            wanted = center;
        }
        if (wanted == mine) { return; }
        int shift = wanted - mine;
        if (alongX) {
            box.minZ += shift;
            box.maxZ += shift;
        }
        else {
            box.minX += shift;
            box.maxX += shift;
        }
        rail.regrade();
        ContentLog.LOGGER.debug("Subway line {} is slid {} block(s) onto the street at {} {}, so it runs under a road and its stations can surface at the road side", rail.line(), shift, alongX ? "z" : "x", wanted);
    }

    private static int shallowRow(World world, RailPiece rail) {
        int least = GenHeights.floor(world, BeardRails.FLOOR_LEAST) + BeardRails.subwayDepth();
        boolean alongX = rail.alongX();
        StructureBoundingBox box = rail.getBoundingBox();
        int center = alongX ? (box.minZ + box.maxZ) / 2 : (box.minX + box.maxX) / 2;
        for (int row = rail.rowLeast(); row <= rail.rowMost(); row++) {
            int found = ContentBeard.surfaceAt(world, alongX ? row : center, alongX ? center : row);
            if (found >= 0 && found < least) { return row; }
        }
        return Integer.MIN_VALUE;
    }

    public static void fit(World world, StructureStart start) {
        List<StructureComponent> components = start.getComponents();
        if (components.isEmpty()) { return; }
        StructureBoundingBox wellBox = components.get(0).getBoundingBox();
        List<StructureComponent> everyone = ContentBeard.everyone(world, components);
        for (StructureComponent piece : components.toArray(new StructureComponent[0])) {
            if (!(piece instanceof RailPiece)) { continue; }
            RailPiece rail = (RailPiece) piece;
            if (rail.trunk() != null) { continue; }
            boolean sub = rail.subway();
            int tail = BeardRails.tail(sub);
            boolean alongX = rail.alongX();
            boolean linkedLow = BeardLinks.linked(rail, -1);
            boolean linkedHigh = BeardLinks.linked(rail, 1);
            if (sub && !linkedLow && !linkedHigh) { seekRoad(components, everyone, rail, alongX, BeardRails.spacing(true)); }
            int least = Integer.MAX_VALUE;
            int most = Integer.MIN_VALUE;
            for (StructureComponent other : components) {
                if (other instanceof RailPiece) { continue; }
                StructureBoundingBox box = other.getBoundingBox();
                least = Math.min(least, alongX ? box.minX : box.minZ);
                most = Math.max(most, alongX ? box.maxX : box.maxZ);
            }
            if (least > most) { continue; }
            StructureBoundingBox box = rail.getBoundingBox();
            StructureBoundingBox plan = sub ? BeardRails.dressed(rail) : box;
            int from = linkedLow ? rail.rowLeast() : Math.max(rail.rowLeast(), least - tail);
            int to = linkedHigh ? rail.rowMost() : Math.min(rail.rowMost(), most + tail);
            int wellAt = alongX ? (wellBox.minX + wellBox.maxX) / 2 : (wellBox.minZ + wellBox.maxZ) / 2;
            for (StructureComponent other : everyone) {
                if (components.contains(other)) { continue; }
                StructureBoundingBox met = other.getBoundingBox();
                if (!met.intersectsWith(plan.minX, plan.minZ, plan.maxX, plan.maxZ)) { continue; }
                int metLeast = alongX ? met.minX : met.minZ;
                int metMost = alongX ? met.maxX : met.maxZ;
                if (metLeast > wellAt ? linkedHigh : metMost < wellAt && linkedLow) {
                    ContentLog.LOGGER.warn("Railway line {} of the village at {}, {} is linked past {} of another village at {}, {}, which should have kept clear of the link", rail.line(), start.getBoundingBox().minX, start.getBoundingBox().minZ, other.getClass().getSimpleName(), met.minX, met.minZ);
                    continue;
                }
                if (metLeast > wellAt) { to = Math.min(to, metLeast - 1 - BeardRails.MARGIN); }
                else if (metMost < wellAt) { from = Math.max(from, metMost + 1 + BeardRails.MARGIN); }
                else { to = from - 1; }
                ContentLog.LOGGER.debug("Railway line {} of the village at {}, {} stops short of {} of another village at {}, {}", rail.line(), start.getBoundingBox().minX, start.getBoundingBox().minZ, other.getClass().getSimpleName(), met.minX, met.minZ);
            }
            if (from > to) {
                components.remove(piece);
                ContentLog.LOGGER.debug("Railway line {} of the village at {}, {} has no room left beside the villages around it, so it is not laid", rail.line(), start.getBoundingBox().minX, start.getBoundingBox().minZ);
                continue;
            }
            if (linkedLow) { BeardLinks.fitted(rail, -1, Math.max(rail.rowLeast(), least - tail)); }
            if (linkedHigh) { BeardLinks.fitted(rail, 1, Math.min(rail.rowMost(), most + tail)); }
            if (alongX) {
                box.minX = from;
                box.maxX = to;
            }
            else {
                box.minZ = from;
                box.maxZ = to;
            }
            rail.regrade();
            if (sub) {
                List<StructureComponent> held = ContentBeard.laid();
                ContentBeard.laying(components);
                int shallow;
                try {
                    shallow = shallowRow(world, rail);
                    if (shallow == Integer.MIN_VALUE) { rail.rising(world); }
                }
                finally { ContentBeard.laying(held); }
                if (shallow != Integer.MIN_VALUE) {
                    components.remove(piece);
                    ContentLog.LOGGER.debug("Subway line {} of the village at {}, {} has no room under the ground at row {}: {} block(s) of depth would reach below y {}, the world floor plus its lining, so it is not laid", rail.line(), start.getBoundingBox().minX, start.getBoundingBox().minZ, shallow, BeardRails.subwayDepth(), GenHeights.floor(world, BeardRails.FLOOR_LEAST));
                    continue;
                }
                for (StructureComponent plot : components.toArray(new StructureComponent[0])) {
                    if (!(plot instanceof mctmods.resourcedatapackloader.content.village.ContentVillagePiece)) { continue; }
                    StructureBoundingBox met = plot.getBoundingBox();
                    if (!met.intersectsWith(box.minX, box.minZ, box.maxX, box.maxZ) || BeardRails.buriedUnder(rail, met)) { continue; }
                    components.remove(plot);
                    ContentLog.LOGGER.debug("A plot at {}, {} makes way for subway line {} climbing out of the ground", met.minX, met.minZ, rail.line());
                }
            }
            ContentLog.LOGGER.debug("Railway line {} of the village at {}, {} is fitted to rows {} to {} now the village is grown, {} beyond its last piece either way", rail.line(), start.getBoundingBox().minX, start.getBoundingBox().minZ, from, to, tail);
            if (sub) {
                int wellAlong = alongX ? (wellBox.minX + wellBox.maxX) / 2 : (wellBox.minZ + wellBox.maxZ) / 2;
                int acrossMid = alongX ? (box.minZ + box.maxZ) / 2 : (box.minX + box.maxX) / 2;
                List<StructureComponent> held = ContentBeard.laid();
                ContentBeard.laying(components);
                try { BeardStations.claim(start, rail, alongX, acrossMid, wellAlong); }
                finally { ContentBeard.laying(held); }
            }
        }
    }

    public static void standOff(World world, StructureStart start) {
        List<StructureComponent> components = start.getComponents();
        for (StructureComponent piece : components.toArray(new StructureComponent[0])) {
            if (!BeardRails.buried(piece) || ((RailPiece) piece).trunk() != null) { continue; }
            List<StructureComponent> held = ContentBeard.laid();
            ContentBeard.laying(components);
            try { standOff(world, (RailPiece) piece, components); }
            finally { ContentBeard.laying(held); }
        }
    }

    private static void standOff(World world, RailPiece rail, List<StructureComponent> components) {
        StructureBoundingBox corridor = BeardRails.dressed(rail);
        for (StructureComponent plot : components.toArray(new StructureComponent[0])) {
            if (BeardRails.isRail(plot) || plot instanceof StructureVillagePieces.Start || plot instanceof StructureVillagePieces.Road) { continue; }
            StructureBoundingBox met = plot.getBoundingBox();
            if (!met.intersectsWith(corridor.minX, corridor.minZ, corridor.maxX, corridor.maxZ)) { continue; }
            int roof = roofUnder(world, rail, met);
            int floor = roof == Integer.MIN_VALUE ? Integer.MIN_VALUE : BeardLayout.predictedFloor(plot, world);
            if (roof != Integer.MIN_VALUE && floor != Integer.MIN_VALUE && floor - COURSES <= roof) {
                components.remove(plot);
                ContentLog.LOGGER.debug("{} at {}, {}, {} to {}, {}, {} makes way for subway line {}: it would rest at y {}, and its {} foundation course(s) reach the bore roof at y {} under it", plot.getClass().getSimpleName(), met.minX, met.minY, met.minZ, met.maxX, met.maxY, met.maxZ, rail.line(), floor, COURSES, roof);
                continue;
            }
            if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("{} at {}, {}, {} to {}, {}, {} stands over subway line {} and is kept: it would rest at y {} and the bore roof under it lies at y {}, {}", plot.getClass().getSimpleName(), met.minX, met.minY, met.minZ, met.maxX, met.maxY, met.maxZ, rail.line(), floor == Integer.MIN_VALUE ? "unknown" : floor, roof == Integer.MIN_VALUE ? "unknown" : roof, roof == Integer.MIN_VALUE ? "the corridor under it carries no bore on the rows it covers" : floor == Integer.MIN_VALUE ? "and its ground cannot be measured" : "so the bore clears its " + COURSES + " foundation course(s)"); }
        }
    }

    private static int roofUnder(World world, RailPiece rail, StructureBoundingBox met) {
        boolean alongX = rail.alongX();
        int center = (rail.acrossLeast() + rail.acrossMost()) / 2;
        int[] rising = rail.risingKnown();
        List<RailPiece> only = java.util.Collections.singletonList(rail);
        int from = Math.max(alongX ? met.minX : met.minZ, rail.rowLeast());
        int to = Math.min(alongX ? met.maxX : met.maxZ, rail.rowMost());
        int roof = Integer.MIN_VALUE;
        for (int row = from; row <= to; row++) {
            if (BeardRails.surfaced(rising, row)) { continue; }
            roof = Math.max(roof, BeardRails.boreRoof(world, only, alongX ? row : center, alongX ? center : row));
        }
        return roof;
    }
}
