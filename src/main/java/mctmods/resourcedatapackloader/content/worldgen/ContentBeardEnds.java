package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.village.CityGrowth;
import mctmods.resourcedatapackloader.content.village.CitySeams;
import mctmods.resourcedatapackloader.content.village.MergePiece;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardPlots;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardRails;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardRoads;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardRoadsGrade;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardRoadsTunnels;
import mctmods.resourcedatapackloader.content.worldgen.beard.interfaces.IRoadLayout;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IStructureComponentBox;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IStructureStartGrow;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

public final class ContentBeardEnds {
    private static Predicate<StructureComponent> vetoed;

    private ContentBeardEnds() {}

    public static void closeEnds(StructureStart start, World world, Random rand, Predicate<StructureComponent> veto, List<StructureComponent> also) {
        if (!ContentBeard.wanted()) { return; }
        List<StructureComponent> pieces = start.getComponents();
        List<StructureComponent> everyone = ContentBeard.everyone(world, pieces);
        for (StructureComponent piece : also) { if (!everyone.contains(piece)) { everyone.add(piece); } }
        vetoed = veto;
        try {
            for (StructureComponent piece : pieces.toArray(new StructureComponent[0])) {
                if (!(piece instanceof StructureVillagePieces.Path)) { continue; }
                StructureBoundingBox box = ((IStructureComponentBox) piece).rdpl$box();
                if (box == null) { continue; }
                boolean alongX = BeardPlots.roadAlongX(piece);
                if (BeardRoads.roadNarrow(box, alongX) || CityGrowth.bulbWide(piece)) { continue; }
                for (int side = 0; side < 2; side++) {
                    boolean outward = side == 1;
                    int end = alongX ? (outward ? box.maxX : box.minX) : (outward ? box.maxZ : box.minZ);
                    StructureBoundingBox strip = alongX ? new StructureBoundingBox(end, box.minY, box.minZ, end, box.maxY, box.maxZ) : new StructureBoundingBox(box.minX, box.minY, end, box.maxX, box.maxY, end);
                    if (CitySeams.built(world, strip)) { continue; }
                    closeEnd(start, piece, box, alongX, outward, everyone, rand);
                }
            }
        }
        finally { vetoed = null; }
        ((IStructureStartGrow) start).rdpl$updateBoundingBox();
    }

    public static void attachAll(StructureStart start, World world, Random rand) {
        if (!ContentBeard.wanted()) { return; }
        List<StructureComponent> pieces = start.getComponents();
        List<StructureComponent> everyone = ContentBeard.everyone(world, pieces);
        for (StructureComponent piece : pieces.toArray(new StructureComponent[0])) { elbows(start, piece, everyone, rand); }
        for (StructureComponent piece : pieces.toArray(new StructureComponent[0])) { ContentBeardJoins.attach(start, piece); }
        if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("The village at {}, {} settled every road box before any chunk was built, so no road is extended into ground that is already made", start.getBoundingBox().minX, start.getBoundingBox().minZ); }
    }

    private static void elbows(StructureStart start, StructureComponent piece, List<StructureComponent> everyone, Random rand) {
        if (!(piece instanceof StructureVillagePieces.Path)) { return; }
        StructureBoundingBox box = ((IStructureComponentBox) piece).rdpl$box();
        if (box == null) { return; }
        boolean alongX = BeardPlots.roadAlongX(piece);
        if (BeardRoads.roadNarrow(box, alongX) || CityGrowth.bulbWide(piece)) { return; }
        for (int side = 0; side < 2; side++) { closeEnd(start, piece, box, alongX, side == 1, everyone, rand); }
    }

    private static void closeEnd(StructureStart start, StructureComponent piece, StructureBoundingBox box, boolean alongX, boolean outward, List<StructureComponent> everyone, Random rand) {
        int end = alongX ? (outward ? box.maxX : box.minX) : (outward ? box.maxZ : box.minZ);
        int acrossLo = alongX ? box.minZ : box.minX;
        int acrossHi = alongX ? box.maxZ : box.maxX;
        int endX = alongX ? end : (acrossLo + acrossHi) / 2;
        int endZ = alongX ? (acrossLo + acrossHi) / 2 : end;
        int attachGap = ContentBeard.attachGap();
        List<StructureComponent> pieces = start.getComponents();
        if (metBeyond(everyone, piece, alongX, end + (outward ? 1 : -1), acrossLo, acrossHi)) {
            if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("The end at {}, {} of the road at {}, {} already meets something", endX, endZ, box.minX, box.minZ); }
            return;
        }
        StructureComponent bestOther = null;
        StructureBoundingBox bestMet = null;
        boolean bestCollinear = false;
        int bestScore = Integer.MAX_VALUE;
        for (StructureComponent other : everyone) {
            if (other == piece || !(other instanceof StructureVillagePieces.Path)) { continue; }
            StructureBoundingBox met = ((IStructureComponentBox) other).rdpl$box();
            if (met == null) { continue; }
            boolean otherAlongX = BeardPlots.roadAlongX(other);
            if (BeardRoads.roadNarrow(met, otherAlongX) || CityGrowth.bulbWide(other)) { continue; }
            if (otherAlongX == alongX) {
                int metAcrossLo = alongX ? met.minZ : met.minX;
                int metAcrossHi = alongX ? met.maxZ : met.maxX;
                if (metAcrossHi < acrossLo || metAcrossLo > acrossHi) { continue; }
                int gap = outward ? (alongX ? met.minX : met.minZ) - end : end - (alongX ? met.maxX : met.maxZ);
                if (gap < 2 || gap > attachGap * 2 || gap >= bestScore) { continue; }
                bestScore = gap;
                bestOther = other;
                bestMet = met;
                bestCollinear = true;
                continue;
            }
            int metAcrossLo = alongX ? met.minX : met.minZ;
            int metAcrossHi = alongX ? met.maxX : met.maxZ;
            int ahead = outward ? metAcrossLo - end : end - metAcrossHi;
            if (ahead < 2 || ahead > attachGap * 2) { continue; }
            int metAlongLo = alongX ? met.minZ : met.minX;
            int metAlongHi = alongX ? met.maxZ : met.maxX;
            boolean overlaps = metAlongHi >= acrossLo && metAlongLo <= acrossHi;
            int sideGap = overlaps ? 0 : metAlongLo > acrossHi ? metAlongLo - acrossHi - 1 : acrossLo - metAlongHi - 1;
            if (sideGap > attachGap) { continue; }
            if (ahead + sideGap >= bestScore) { continue; }
            bestScore = ahead + sideGap;
            bestOther = other;
            bestMet = met;
        }
        if (bestMet == null) { return; }
        if (bestCollinear) {
            int metNear = outward ? (alongX ? bestMet.minX : bestMet.minZ) : (alongX ? bestMet.maxX : bestMet.maxZ);
            int cFrom = outward ? end + 1 : metNear + 1;
            int cTo = outward ? metNear - 1 : end - 1;
            int metSideLo = alongX ? bestMet.minZ : bestMet.minX;
            int metSideHi = alongX ? bestMet.maxZ : bestMet.maxX;
            int offset = (metSideLo + metSideHi) / 2 - (acrossLo + acrossHi) / 2;
            if (offset != 0) {
                if (!mergeInto(start, piece, box, alongX, outward, everyone, bestOther, acrossLo, acrossHi, metSideLo, metSideHi, cFrom, cTo, offset)) {
                    crossStreet(start, piece, box, alongX, outward, everyone, rand, bestOther, end, acrossLo, acrossHi, metSideLo, metSideHi, cFrom, cTo);
                }
                return;
            }
            List<StructureComponent> joining = standing(everyone, pieces, piece, bestOther, alongX ? cFrom : acrossLo, alongX ? acrossLo : cFrom, alongX ? cTo : acrossHi, alongX ? acrossHi : cTo);
            if (joining == null) {
                ContentLog.LOGGER.debug("The dead end at {}, {} cannot reach the facing street at {}, {}: the strip is held", endX, endZ, bestMet.minX, bestMet.minZ);
                return;
            }
            StructureBoundingBox beside = ContentBeard.beside(pieces, ContentBeard.strip(box, alongX, cFrom, cTo), alongX, piece);
            if (beside != null) {
                ContentLog.LOGGER.debug("The dead end at {}, {} cannot reach the facing street at {}, {}: it would run beside the road at {}, {}", endX, endZ, bestMet.minX, bestMet.minZ, beside.minX, beside.minZ);
                return;
            }
            for (StructureComponent plot : joining) {
                ContentLog.LOGGER.debug("{} at {}, {} makes way for the road closing its dead end", plot.getClass().getSimpleName(), plot.getBoundingBox().minX, plot.getBoundingBox().minZ);
                pieces.remove(plot);
            }
            if (alongX) {
                if (outward) { box.maxX = metNear - 1; }
                else { box.minX = metNear + 1; }
            }
            else {
                if (outward) { box.maxZ = metNear - 1; }
                else { box.minZ = metNear + 1; }
            }
            if (piece instanceof IRoadLayout) { ((IRoadLayout) piece).rdpl$layout(null); }
            ContentLog.LOGGER.debug("The dead end at {}, {} reaches the facing street at {}, {} and joins it, {} plot(s) making way", endX, endZ, bestMet.minX, bestMet.minZ, joining.size());
            return;
        }
        int metAcrossLo = alongX ? bestMet.minX : bestMet.minZ;
        int metAcrossHi = alongX ? bestMet.maxX : bestMet.maxZ;
        int metAlongLo = alongX ? bestMet.minZ : bestMet.minX;
        int metAlongHi = alongX ? bestMet.maxZ : bestMet.maxX;
        boolean overlaps = metAlongHi >= acrossLo && metAlongLo <= acrossHi;
        int from = outward ? end + 1 : overlaps ? metAcrossHi + 1 : metAcrossLo;
        int to = outward ? (overlaps ? metAcrossLo - 1 : metAcrossHi) : end - 1;
        List<StructureComponent> making = standing(everyone, pieces, piece, bestOther, alongX ? from : acrossLo, alongX ? acrossLo : from, alongX ? to : acrossHi, alongX ? acrossHi : to);
        if (making == null) {
            ContentLog.LOGGER.debug("The dead end at {}, {} cannot reach the road at {}, {}: a well or road holds the strip", endX, endZ, bestMet.minX, bestMet.minZ);
            return;
        }
        StructureBoundingBox beside = ContentBeard.beside(pieces, ContentBeard.strip(box, alongX, from, to), alongX, piece);
        if (beside != null) {
            ContentLog.LOGGER.debug("The dead end at {}, {} cannot reach the road at {}, {}: it would run beside the road at {}, {}", endX, endZ, bestMet.minX, bestMet.minZ, beside.minX, beside.minZ);
            return;
        }
        if (BeardRoadsTunnels.frontsHill(bestOther, ContentBeard.strip(box, alongX, from, to))) {
            ContentLog.LOGGER.debug("The dead end at {}, {} cannot close into the road at {}, {}: it would meet it inside its tunnel through a hill", endX, endZ, bestMet.minX, bestMet.minZ);
            return;
        }
        if (stepsTo(pieces, box, alongX, outward, outward ? to : from)) {
            ContentLog.LOGGER.debug("The dead end at {}, {} cannot close into the road at {}, {}: the longer road would not walk end to end", endX, endZ, bestMet.minX, bestMet.minZ);
            return;
        }
        if (!overlaps) {
            int metFrom = metAlongLo > acrossHi ? acrossHi + 1 : metAlongHi + 1;
            int metTo = metAlongLo > acrossHi ? metAlongLo - 1 : acrossLo - 1;
            if (metFrom <= metTo) {
                List<StructureComponent> metMaking = standing(everyone, pieces, piece, bestOther, alongX ? metAcrossLo : metFrom, alongX ? metFrom : metAcrossLo, alongX ? metAcrossHi : metTo, alongX ? metTo : metAcrossHi);
                if (metMaking == null) { return; }
                StructureBoundingBox metBeside = ContentBeard.beside(pieces, ContentBeard.strip(bestMet, !alongX, metFrom, metTo), !alongX, bestOther);
                if (metBeside != null) {
                    ContentLog.LOGGER.debug("The dead end at {}, {} cannot close into a corner with the road at {}, {}: that road would run beside the road at {}, {}", endX, endZ, bestMet.minX, bestMet.minZ, metBeside.minX, metBeside.minZ);
                    return;
                }
                for (StructureComponent plot : metMaking) { if (!making.contains(plot)) { making.add(plot); } }
            }
        }
        for (StructureComponent plot : making) {
            ContentLog.LOGGER.debug("{} at {}, {} makes way for the road closing its dead end", plot.getClass().getSimpleName(), plot.getBoundingBox().minX, plot.getBoundingBox().minZ);
            pieces.remove(plot);
        }
        if (alongX) {
            if (outward) { box.maxX = overlaps ? metAcrossLo - 1 : metAcrossHi; }
            else { box.minX = overlaps ? metAcrossHi + 1 : metAcrossLo; }
            if (!overlaps) {
                if (metAlongLo > acrossHi) { bestMet.minZ = box.maxZ + 1; }
                else { bestMet.maxZ = box.minZ - 1; }
            }
        }
        else {
            if (outward) { box.maxZ = overlaps ? metAcrossLo - 1 : metAcrossHi; }
            else { box.minZ = overlaps ? metAcrossHi + 1 : metAcrossLo; }
            if (!overlaps) {
                if (metAlongLo > acrossHi) { bestMet.minX = box.maxX + 1; }
                else { bestMet.maxX = box.minX - 1; }
            }
        }
        if (piece instanceof IRoadLayout) { ((IRoadLayout) piece).rdpl$layout(null); }
        if (bestOther instanceof IRoadLayout) { ((IRoadLayout) bestOther).rdpl$layout(null); }
        ContentLog.LOGGER.debug("The dead end at {}, {} reaches the road at {}, {} and closes into a {}, {} plot(s) making way", endX, endZ, bestMet.minX, bestMet.minZ, overlaps ? "junction" : "corner", making.size());
    }

    private static boolean stepsTo(List<StructureComponent> pieces, StructureBoundingBox box, boolean alongX, boolean outward, int reach) {
        StructureBoundingBox longer = new StructureBoundingBox(box);
        if (alongX) {
            if (outward) { longer.maxX = reach; }
            else { longer.minX = reach; }
        }
        else {
            if (outward) { longer.maxZ = reach; }
            else { longer.minZ = reach; }
        }
        EnumFacing facing = alongX ? (outward ? EnumFacing.EAST : EnumFacing.WEST) : (outward ? EnumFacing.SOUTH : EnumFacing.NORTH);
        List<StructureComponent> held = ContentBeard.laid();
        int kept;
        ContentBeard.laying(pieces);
        try { kept = BeardRoadsGrade.roadReach(longer, facing); }
        finally { ContentBeard.laying(held); }
        return kept < (alongX ? longer.maxX - longer.minX : longer.maxZ - longer.minZ) + 1;
    }

    private static final int MERGE_ROWS = 3;

    private static boolean mergeInto(StructureStart start, StructureComponent piece, StructureBoundingBox box, boolean alongX, boolean outward, List<StructureComponent> everyone, StructureComponent other, int acrossLo, int acrossHi, int metSideLo, int metSideHi, int cFrom, int cTo, int offset) {
        List<StructureComponent> pieces = start.getComponents();
        if (pieces.isEmpty() || !(pieces.get(0) instanceof StructureVillagePieces.Start)) { return false; }
        int rows = cTo - cFrom + 1;
        int shift = Math.abs(offset);
        int endX = alongX ? (outward ? box.maxX : box.minX) : (acrossLo + acrossHi) / 2;
        int endZ = alongX ? (acrossLo + acrossHi) / 2 : (outward ? box.maxZ : box.minZ);
        StructureBoundingBox met = other.getBoundingBox();
        if (shift > (BeardRoads.pathFullWidth() - 1) / 2 || rows < MERGE_ROWS * shift) {
            ContentLog.LOGGER.debug("The dead end at {}, {} faces the street at {}, {} {} block(s) off its line with {} row(s) between them, too far off or too close to merge", endX, endZ, met.minX, met.minZ, shift, rows);
            return false;
        }
        int unionLo = Math.min(acrossLo, metSideLo);
        int unionHi = Math.max(acrossHi, metSideHi);
        StructureBoundingBox merge = alongX
                ? new StructureBoundingBox(cFrom, box.minY, unionLo, cTo, box.maxY, unionHi)
                : new StructureBoundingBox(unionLo, box.minY, cFrom, unionHi, box.maxY, cTo);
        if (BeardRoadsTunnels.crossesHill(pieces, merge)) {
            ContentLog.LOGGER.debug("The dead end at {}, {} cannot merge into the street at {}, {}: the merge would meet a tunnel through a hill", endX, endZ, met.minX, met.minZ);
            return false;
        }
        List<StructureComponent> making = standing(everyone, pieces, piece, other, merge.minX, merge.minZ, merge.maxX, merge.maxZ);
        if (making == null) {
            ContentLog.LOGGER.debug("The dead end at {}, {} cannot merge into the street at {}, {}: the ground between them is held", endX, endZ, met.minX, met.minZ);
            return false;
        }
        StructureBoundingBox beside = ContentBeard.beside(pieces, merge, alongX, piece);
        if (beside != null) {
            ContentLog.LOGGER.debug("The dead end at {}, {} cannot merge into the street at {}, {}: the merge would run beside the road at {}, {}", endX, endZ, met.minX, met.minZ, beside.minX, beside.minZ);
            return false;
        }
        for (StructureComponent plot : making) {
            ContentLog.LOGGER.debug("{} at {}, {} makes way for the street merging into its neighbor", plot.getClass().getSimpleName(), plot.getBoundingBox().minX, plot.getBoundingBox().minZ);
            pieces.remove(plot);
        }
        int ownCenter = (acrossLo + acrossHi) / 2;
        int metCenter = (metSideLo + metSideHi) / 2;
        MergePiece lane = new MergePiece((StructureVillagePieces.Start) pieces.get(0), merge, alongX, outward ? ownCenter : metCenter, outward ? metCenter : ownCenter);
        pieces.add(lane);
        everyone.add(lane);
        ContentLog.LOGGER.debug("The dead end at {}, {} meets the street at {}, {} {} block(s) off its line and merges into it over {} row(s) at {}, {}, {} plot(s) making way", endX, endZ, met.minX, met.minZ, shift, rows, merge.minX, merge.minZ, making.size());
        return true;
    }

    private static void crossStreet(StructureStart start, StructureComponent piece, StructureBoundingBox box, boolean alongX, boolean outward, List<StructureComponent> everyone, Random rand, StructureComponent other, int end, int acrossLo, int acrossHi, int metSideLo, int metSideHi, int cFrom, int cTo) {
        List<StructureComponent> pieces = start.getComponents();
        if (pieces.isEmpty() || !(pieces.get(0) instanceof StructureVillagePieces.Start)) { return; }
        int endX = alongX ? end : (acrossLo + acrossHi) / 2;
        int endZ = alongX ? (acrossLo + acrossHi) / 2 : end;
        int full = BeardRoads.pathFullWidth();
        int open = cTo - cFrom + 1;
        if (open < full) {
            ContentLog.LOGGER.debug("The dead end at {}, {} faces the offset street at {}, {} too closely for a cross street to tie them, so both stay closed", endX, endZ, other.getBoundingBox().minX, other.getBoundingBox().minZ);
            return;
        }
        int conLo = outward ? cTo - full + 1 : cFrom;
        int conHi = outward ? cTo : cFrom + full - 1;
        int unionLo = Math.min(acrossLo, metSideLo);
        int unionHi = Math.max(acrossHi, metSideHi);
        StructureBoundingBox avenue = alongX
                ? new StructureBoundingBox(conLo, box.minY, unionLo, conHi, box.maxY, unionHi)
                : new StructureBoundingBox(unionLo, box.minY, conLo, unionHi, box.maxY, conHi);
        if (BeardRoadsTunnels.crossesHill(pieces, avenue)) {
            ContentLog.LOGGER.debug("The dead end at {}, {} cannot tie to the offset street at {}, {}: the cross street would meet a tunnel through a hill", endX, endZ, other.getBoundingBox().minX, other.getBoundingBox().minZ);
            return;
        }
        List<StructureComponent> making = standing(everyone, pieces, piece, other, avenue.minX, avenue.minZ, avenue.maxX, avenue.maxZ);
        if (making == null) {
            ContentLog.LOGGER.debug("The dead end at {}, {} cannot tie to the offset street at {}, {}: the cross street's ground is held", endX, endZ, other.getBoundingBox().minX, other.getBoundingBox().minZ);
            return;
        }
        StructureBoundingBox beside = ContentBeard.beside(pieces, avenue, !alongX, piece);
        if (beside != null) {
            ContentLog.LOGGER.debug("The dead end at {}, {} cannot tie to the offset street at {}, {}: the cross street would run beside the road at {}, {}", endX, endZ, other.getBoundingBox().minX, other.getBoundingBox().minZ, beside.minX, beside.minZ);
            return;
        }
        int exFrom = outward ? end + 1 : conHi + 1;
        int exTo = outward ? conLo - 1 : end - 1;
        List<StructureComponent> more = standing(everyone, pieces, piece, other, alongX ? exFrom : acrossLo, alongX ? acrossLo : exFrom, alongX ? exTo : acrossHi, alongX ? acrossHi : exTo);
        if (more == null) {
            ContentLog.LOGGER.debug("The dead end at {}, {} cannot reach the cross street that would tie it: its own strip is held", endX, endZ);
            return;
        }
        if (ContentBeard.beside(pieces, ContentBeard.strip(box, alongX, exFrom, exTo), alongX, piece) != null) {
            ContentLog.LOGGER.debug("The dead end at {}, {} cannot reach the cross street that would tie it: its own strip would run beside another road", endX, endZ);
            return;
        }
        for (StructureComponent plot : more) { if (!making.contains(plot)) { making.add(plot); } }
        for (StructureComponent plot : making) {
            ContentLog.LOGGER.debug("{} at {}, {} makes way for the road closing its dead end", plot.getClass().getSimpleName(), plot.getBoundingBox().minX, plot.getBoundingBox().minZ);
            pieces.remove(plot);
        }
        StructureVillagePieces.Path lane = new StructureVillagePieces.Path((StructureVillagePieces.Start) pieces.get(0), 0, rand, avenue, alongX ? EnumFacing.SOUTH : EnumFacing.EAST);
        pieces.add(lane);
        everyone.add(lane);
        if (alongX) {
            if (outward) { box.maxX = conLo - 1; }
            else { box.minX = conHi + 1; }
        }
        else {
            if (outward) { box.maxZ = conLo - 1; }
            else { box.minZ = conHi + 1; }
        }
        if (piece instanceof IRoadLayout) { ((IRoadLayout) piece).rdpl$layout(null); }
        ContentLog.LOGGER.debug("The dead end at {}, {} and the offset street at {}, {} tie through a cross street at {}, {}, {} plot(s) making way", endX, endZ, other.getBoundingBox().minX, other.getBoundingBox().minZ, avenue.minX, avenue.minZ, making.size());
    }

    public static boolean metBeyond(List<StructureComponent> pieces, StructureComponent piece, boolean alongX, int beyond, int acrossLo, int acrossHi) {
        for (StructureComponent other : pieces) {
            if (other == piece || !(other instanceof StructureVillagePieces.Path || other instanceof MergePiece)) { continue; }
            StructureBoundingBox met = other.getBoundingBox();
            if (BeardRoads.roadNarrow(met, BeardPlots.roadAlongX(other))) { continue; }
            int lo = alongX ? met.minX : met.minZ;
            int hi = alongX ? met.maxX : met.maxZ;
            if (beyond < lo - 1 || beyond > hi + 1) { continue; }
            int oLo = alongX ? met.minZ : met.minX;
            int oHi = alongX ? met.maxZ : met.maxX;
            if (oHi < acrossLo || oLo > acrossHi) { continue; }
            return true;
        }
        return false;
    }

    @Nullable private static List<StructureComponent> standing(List<StructureComponent> everyone, List<StructureComponent> own, StructureComponent piece, StructureComponent other, int minX, int minZ, int maxX, int maxZ) {
        if (maxX < minX || maxZ < minZ) { return new ArrayList<>(); }
        List<StructureComponent> plots = new ArrayList<>();
        for (StructureComponent held : everyone) {
            if (held == piece || held == other) { continue; }
            StructureBoundingBox met = held.getBoundingBox();
            if (!met.intersectsWith(minX, minZ, maxX, maxZ)) { continue; }
            if (held instanceof StructureVillagePieces.Well || held instanceof MergePiece) { return null; }
            if (BeardRails.isRail(held)) {
                StructureBoundingBox road = piece.getBoundingBox();
                StructureBoundingBox whole = new StructureBoundingBox(Math.min(road.minX, minX), 0, Math.min(road.minZ, minZ), Math.max(road.maxX, maxX), 0, Math.max(road.maxZ, maxZ));
                if (own.contains(held) && BeardRails.crosses(met, whole)) { continue; }
                return null;
            }
            if (held instanceof StructureVillagePieces.Path) {
                if (BeardRoads.roadNarrow(met, BeardPlots.roadAlongX(held))) { continue; }
                return null;
            }
            if (!own.contains(held) || (vetoed != null && vetoed.test(held))) { return null; }
            plots.add(held);
        }
        return plots;
    }
}
