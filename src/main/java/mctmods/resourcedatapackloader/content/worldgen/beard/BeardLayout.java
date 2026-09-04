package mctmods.resourcedatapackloader.content.worldgen.beard;

import mctmods.resourcedatapackloader.content.village.CityGrowth;
import mctmods.resourcedatapackloader.content.village.ContentVillages;
import mctmods.resourcedatapackloader.content.worldgen.ContentBeard;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IVillagePieces;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import java.util.List;
import java.util.Random;

public final class BeardLayout {
    private BeardLayout() {}

    public static void trim(StructureBoundingBox box, boolean alongX, EnumFacing facing, int rows) {
        int step = (alongX ? facing.getXOffset() : facing.getZOffset()) >= 0 ? 1 : -1;
        if (alongX && step > 0) { box.maxX = box.minX + rows - 1; }
        else if (alongX) { box.minX = box.maxX - rows + 1; }
        else if (step > 0) { box.maxZ = box.minZ + rows - 1; }
        else { box.minZ = box.maxZ - rows + 1; }
    }

    public static boolean fromWell(List<StructureComponent> own, int backX, int backZ) {
        for (StructureComponent other : own) {
            if (!(other instanceof StructureVillagePieces.Well)) { continue; }
            StructureBoundingBox well = other.getBoundingBox();
            if (backX >= well.minX && backX <= well.maxX && backZ >= well.minZ && backZ <= well.maxZ) { return true; }
        }
        return false;
    }

    public static boolean fromWell(List<StructureComponent> own, StructureBoundingBox box, EnumFacing facing) {
        boolean alongX = facing.getAxis() == EnumFacing.Axis.X;
        int back = alongX ? (facing.getXOffset() > 0 ? box.minX - 1 : box.maxX + 1) : (facing.getZOffset() > 0 ? box.minZ - 1 : box.maxZ + 1);
        for (StructureComponent other : own) {
            if (!(other instanceof StructureVillagePieces.Well)) { continue; }
            StructureBoundingBox well = other.getBoundingBox();
            if (alongX ? (back >= well.minX && back <= well.maxX && box.maxZ >= well.minZ && box.minZ <= well.maxZ) : (back >= well.minZ && back <= well.maxZ && box.maxX >= well.minX && box.minX <= well.maxX)) { return true; }
        }
        return false;
    }

    public static boolean lineUp(List<StructureComponent> own, StructureBoundingBox found, boolean alongX, int half) {
        List<StructureComponent> pieces = ContentBeard.everyone(own);
        int center = alongX ? (found.minZ + found.maxZ) / 2 : (found.minX + found.maxX) / 2;
        StructureBoundingBox held = null;
        int nearest = Integer.MAX_VALUE;
        for (StructureComponent other : pieces) {
            if (!(other instanceof StructureVillagePieces.Path) || CityGrowth.bulbWide(other)) { continue; }
            StructureBoundingBox met = other.getBoundingBox();
            if (BeardPlots.roadAlongX(met) != alongX) { continue; }
            if (BeardRoads.roadNarrow(met, alongX)) { continue; }
            boolean acrossed = alongX ? met.maxZ >= found.minZ - half && met.minZ <= found.maxZ + half : met.maxX >= found.minX - half && met.minX <= found.maxX + half;
            if (!acrossed) { continue; }
            int gap = alongX ? Math.max(met.minX - found.maxX, found.minX - met.maxX) : Math.max(met.minZ - found.maxZ, found.minZ - met.maxZ);
            if (gap < 2 || gap > CityGrowth.march() || gap >= nearest) { continue; }
            nearest = gap;
            held = met;
        }
        if (held == null) { return true; }
        int delta = (alongX ? (held.minZ + held.maxZ) / 2 : (held.minX + held.maxX) / 2) - center;
        if (delta == 0) { return true; }
        StructureBoundingBox slid = new StructureBoundingBox(found);
        if (alongX) {
            slid.minZ += delta;
            slid.maxZ += delta;
        }
        else {
            slid.minX += delta;
            slid.maxX += delta;
        }
        for (StructureComponent taken : pieces) {
            if (taken instanceof StructureVillagePieces.Path && BeardRoads.roadNarrow(taken.getBoundingBox(), BeardPlots.roadAlongX(taken))) { continue; }
            if (taken.getBoundingBox().intersectsWith(slid.minX, slid.minZ, slid.maxX, slid.maxZ)) {
                ContentLog.LOGGER.debug("A road attempt {} cannot slide {} to line up with the road at {}, {} along its corridor, so it is refused", found, delta, held.minX, held.minZ);
                return false;
            }
        }
        ContentLog.LOGGER.debug("A road attempt {} slides {} to line up with the road at {}, {} along its corridor", found, delta, held.minX, held.minZ);
        if (alongX) {
            found.minZ = slid.minZ;
            found.maxZ = slid.maxZ;
        }
        else {
            found.minX = slid.minX;
            found.maxX = slid.maxX;
        }
        return true;
    }

    public static boolean tooNear(StructureVillagePieces.Start start, List<StructureComponent> own, StructureBoundingBox wide, EnumFacing facing) {
        boolean alongX = facing.getAxis() == EnumFacing.Axis.X;
        StructureBoundingBox ahead = new StructureBoundingBox(wide);
        int reach = fromWell(own, wide, facing) ? ContentBeard.attachGap() * 2 : 0;
        if (alongX) { if (facing.getXOffset() > 0) { ahead.maxX += reach; } else { ahead.minX -= reach; } }
        else { if (facing.getZOffset() > 0) { ahead.maxZ += reach; } else { ahead.minZ -= reach; } }
        StructureBoundingBox held = ContentBeard.beside(own, ahead, alongX, start);
        if (held == null) { return false; }
        ContentLog.LOGGER.debug("A road attempt {} facing {} would run beside the road at {}, {}, under the {} block spacing the plots on both sides need, so it may only be an alley", wide, facing, held.minX, held.minZ, ContentVillages.blockOf(start) + ContentVillages.blockOf(ContentBeard.roadAt(own, held)));
        return true;
    }

    public static boolean widensPast(List<StructureComponent> pieces, StructureBoundingBox wide, EnumFacing facing) {
        List<StructureBoundingBox> plazas = BeardPlots.plazaSquares(pieces);
        boolean alongX = facing.getAxis() == EnumFacing.Axis.X;
        for (StructureComponent other : pieces) {
            StructureBoundingBox held = other.getBoundingBox();
            if (!held.intersectsWith(wide.minX, wide.minZ, wide.maxX, wide.maxZ)) { continue; }
            if (other instanceof StructureVillagePieces.Path && BeardRoads.roadNarrow(held, BeardPlots.roadAlongX(held))) { continue; }
            if (plazas.isEmpty() || !(other instanceof StructureVillagePieces.Path)) { return false; }
            if (BeardPlots.roadAlongX(held) == alongX) { return false; }
            boolean covered = false;
            for (StructureBoundingBox plaza : plazas) {
                if (Math.max(wide.minX, held.minX) < plaza.minX || Math.min(wide.maxX, held.maxX) > plaza.maxX) { continue; }
                if (Math.max(wide.minZ, held.minZ) < plaza.minZ || Math.min(wide.maxZ, held.maxZ) > plaza.maxZ) { continue; }
                covered = true;
                break;
            }
            if (!covered) { return false; }
        }
        return true;
    }

    public static boolean roadWithinReach(List<StructureComponent> pieces, StructureBoundingBox alley, EnumFacing facing) {
        boolean alongX = facing.getAxis() == EnumFacing.Axis.X;
        boolean onward = facing == EnumFacing.EAST || facing == EnumFacing.SOUTH;
        int end = alongX ? (onward ? alley.maxX : alley.minX) : (onward ? alley.maxZ : alley.minZ);
        int reach = ContentBeard.attachGap();
        for (StructureComponent other : pieces) {
            if (!(other instanceof StructureVillagePieces.Path)) { continue; }
            StructureBoundingBox met = other.getBoundingBox();
            boolean lined = alongX ? met.maxZ >= alley.minZ && met.minZ <= alley.maxZ : met.maxX >= alley.minX && met.minX <= alley.maxX;
            if (!lined) { continue; }
            int away = onward ? (alongX ? met.minX : met.minZ) - end : end - (alongX ? met.maxX : met.maxZ);
            if (away > 0 && away <= reach) { return true; }
        }
        return false;
    }

    public static boolean joinsRoads(List<StructureComponent> pieces, StructureBoundingBox alley, EnumFacing facing) {
        int minX = alley.minX;
        int maxX = alley.maxX;
        int minZ = alley.minZ;
        int maxZ = alley.maxZ;
        switch (facing) {
            case NORTH: minZ = maxZ = alley.minZ - 1; break;
            case SOUTH: minZ = maxZ = alley.maxZ + 1; break;
            case WEST: minX = maxX = alley.minX - 1; break;
            default: minX = maxX = alley.maxX + 1;
        }
        for (StructureComponent other : pieces) {
            if (!(other instanceof StructureVillagePieces.Path)) { continue; }
            if (other.getBoundingBox().intersectsWith(minX, minZ, maxX, maxZ)) { return true; }
        }
        return false;
    }

    public static boolean acrossPlaza(List<StructureComponent> components, StructureBoundingBox road) {
        if (!BeardRoads.pathChosen()) { return false; }
        int reach = BeardRoads.pathFullWidth();
        for (StructureBoundingBox well : BeardPlots.wellBoxes(components)) {
            if (!BeardPlots.nearWell(road, well, reach)) { continue; }
            boolean alongX = road.maxX - road.minX >= road.maxZ - road.minZ;
            boolean radial = alongX ? road.maxZ >= well.minZ && road.minZ <= well.maxZ : road.maxX >= well.minX && road.minX <= well.maxX;
            if (!radial) { return true; }
        }
        return false;
    }

    @SuppressWarnings({"ConstantConditions", "ConstantValue"}) public static void branchAtBlocks(StructureComponent road, StructureVillagePieces.Start start, List<StructureComponent> listIn, Random rand, int block) {
        StructureBoundingBox box = road.getBoundingBox();
        boolean alongX = BeardPlots.roadAlongX(road);
        if (BeardRoads.roadNarrow(box, alongX) || CityGrowth.bulbWide(road)) { return; }
        int full = BeardRoads.pathFullWidth();
        int interval = 2 * block + full;
        int least = alongX ? box.minX : box.minZ;
        int most = alongX ? box.maxX : box.maxZ;
        int branched = 0;
        for (int at = least + interval; at + 2 + full <= most; at += interval) {
            for (int side = 0; side < 2; side++) {
                if (rand.nextInt(3) == 0) { continue; }
                boolean outward = side == 1;
                int x = alongX ? at : (outward ? box.maxX + 1 : box.minX - 1);
                int z = alongX ? (outward ? box.maxZ + 1 : box.minZ - 1) : at;
                EnumFacing facing = alongX ? (outward ? EnumFacing.SOUTH : EnumFacing.NORTH) : (outward ? EnumFacing.EAST : EnumFacing.WEST);
                if (IVillagePieces.rdpl$roadPiece(start, listIn, rand, x, box.minY, z, facing, road.getComponentType()) != null) { branched++; }
            }
        }
        if (branched > 0 && ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("The road at {}, {} branches {} street(s) along its length at every {} blocks, the depth of two of its {} deep blocks and a road", box.minX, box.minZ, branched, interval, block); }
    }

    public static int leanLow(StructureComponent piece, World worldIn, StructureBoundingBox structurebb, int found) {
        StructureBoundingBox box = piece.getBoundingBox();
        int average = ContentBeard.noiseAverage(worldIn, box);
        boolean wet = average != Integer.MIN_VALUE && average < worldIn.getSeaLevel();
        if (wet) { average = worldIn.getSeaLevel(); }
        if (average != Integer.MIN_VALUE) {
            if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("{} at {}, {} measures its ground at y {} from the noise surface{}", piece.getClass().getSimpleName(), box.minX, box.minZ, average, average == found ? ", agreeing with the world" : " instead of y " + found); }
            found = average;
        }
        if (!(piece instanceof StructureVillagePieces.Well)) {
            int grade = BeardRoads.roadGradeBeside(worldIn, box);
            if (grade != Integer.MIN_VALUE) {
                int seat = BeardPlots.waystone(piece) ? grade : grade - 1;
                if (wet && seat < worldIn.getSeaLevel()) {
                    seat = worldIn.getSeaLevel();
                    ContentLog.LOGGER.debug("{} at {}, {} stands on water, so it is held up to the surface at y {} instead of sinking a course below the road", piece.getClass().getSimpleName(), box.minX, box.minZ, seat);
                }
                if (seat != found) {
                    ContentLog.LOGGER.debug("{} at {}, {} stands at the grade of the road beside it, y {}, instead of y {}", piece.getClass().getSimpleName(), box.minX, box.minZ, seat, found);
                    return seat;
                }
                return found;
            }
        }
        if (piece instanceof StructureVillagePieces.Well || piece instanceof StructureVillagePieces.Field1 || piece instanceof StructureVillagePieces.Field2) { return found; }
        int lowest = ContentBeard.lowestIn(worldIn, box.minX, box.minZ, box.maxX, box.maxZ, structurebb);
        if (lowest == Integer.MAX_VALUE || found <= lowest + 3) { return found; }
        int leaned = roadClamped(piece, lowest + 3, box);
        if (found <= leaned) { return found; }
        ContentLog.LOGGER.debug("{} at {}, {} leaned from y {} down to y {} over its low side", piece.getClass().getSimpleName(), box.minX, box.minZ, found, leaned);
        return leaned;
    }

    public static int roadClamped(StructureComponent self, int leaned, StructureBoundingBox box) {
        StructureStart start = ContentBeard.current();
        if (start == null) { return leaned; }
        for (StructureComponent other : start.getComponents()) {
            if (other == self || !(other instanceof StructureVillagePieces.Path)) { continue; }
            StructureBoundingBox road = other.getBoundingBox();
            if (road.minX - 4 > box.maxX || box.minX - 4 > road.maxX || road.minZ - 4 > box.maxZ || box.minZ - 4 > road.maxZ) { continue; }
            if (road.minY > leaned) { leaned = road.minY; }
        }
        return leaned;
    }

    @SuppressWarnings({"ConstantConditions", "ConstantValue"}) public static boolean flatterFooting(StructureComponent placed, StructureVillagePieces.Start start, List<StructureComponent> structureComponents, EnumFacing facing) {
        StructureBoundingBox box = placed.getBoundingBox();
        structureComponents.remove(placed);
        int sink = ContentBeard.footingSink(placed);
        int misfit = ContentBeard.taken(structureComponents, box) ? Integer.MAX_VALUE : ContentBeard.footingMisfit(box, structureComponents, sink);
        if (misfit == 0) {
            structureComponents.add(placed);
            return true;
        }
        int alongX = facing.getAxis() == EnumFacing.Axis.X ? 0 : 1;
        StructureBoundingBox well = start.getBoundingBox();
        int wellward = (alongX == 1 ? (well.minX + well.maxX) / 2 - (box.minX + box.maxX) / 2 : (well.minZ + well.maxZ) / 2 - (box.minZ + box.maxZ) / 2) >= 0 ? 1 : -1;
        int reach = ContentBeard.plazaReach() + 1;
        int bestMisfit = misfit;
        int bestSlide = 0;
        for (int step : new int[] { 2, 4, 6, 8, 10, 12, -2, -4, -6, -8, -10, -12 }) {
            int slide = step * wellward;
            StructureBoundingBox tried = new StructureBoundingBox(box);
            tried.offset(alongX * slide, 0, (1 - alongX) * slide);
            if (BeardPlots.nearWell(tried, well, reach)) { continue; }
            if (StructureComponent.findIntersecting(structureComponents, tried) != null || ContentBeard.taken(structureComponents, tried)) { continue; }
            int triedMisfit = ContentBeard.footingMisfit(tried, structureComponents, sink);
            if (triedMisfit < bestMisfit) {
                bestMisfit = triedMisfit;
                bestSlide = slide;
                if (bestMisfit == 0) { break; }
            }
        }
        if (bestMisfit == Integer.MAX_VALUE) {
            ContentLog.LOGGER.debug("{} at {}, {} would stand on an apron deeper than {} block(s) or on another village's piece, and found no better fit within 12 along its road, so it is not built", placed.getClass().getSimpleName(), box.minX, box.minZ, 2 + sink);
            return false;
        }
        structureComponents.add(placed);
        if (bestSlide != 0) {
            box.offset(alongX * bestSlide, 0, (1 - alongX) * bestSlide);
            ContentLog.LOGGER.debug("{} at {}, {} slid {} along its road to a better fit, {} block(s) of apron in total instead of {}", placed.getClass().getSimpleName(), box.minX, box.minZ, bestSlide, bestMisfit, misfit == Integer.MAX_VALUE ? "too deep" : String.valueOf(misfit));
        }
        return true;
    }
}
