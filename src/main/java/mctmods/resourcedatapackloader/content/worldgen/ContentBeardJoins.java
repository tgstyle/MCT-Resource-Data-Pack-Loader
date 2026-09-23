package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.village.MergePiece;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardPlots;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardRails;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardRoads;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardRoadsTunnels;
import mctmods.resourcedatapackloader.content.worldgen.beard.interfaces.IRoadLayout;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IStructureComponentBox;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import javax.annotation.Nullable;
import java.util.List;

public final class ContentBeardJoins {

    private ContentBeardJoins() {}

    public static void attach(StructureStart start, StructureComponent piece) {
        if (!(piece instanceof StructureVillagePieces.Path)) { return; }
        StructureBoundingBox box = ((IStructureComponentBox) piece).rdpl$box();
        if (box == null) { return; }
        boolean alongX = BeardPlots.roadAlongX(piece);
        if (BeardRoads.roadNarrow(box, alongX)) { return; }
        List<StructureComponent> everyone = ContentBeard.everyone(start.getComponents());
        for (StructureComponent other : everyone) {
            if (other == piece || !(other instanceof StructureVillagePieces.Path)) { continue; }
            StructureBoundingBox met = ((IStructureComponentBox) other).rdpl$box();
            if (met == null) { continue; }
            if (BeardRoads.roadNarrow(met, BeardPlots.roadAlongX(other))) { continue; }
            boolean lined = alongX ? met.maxZ >= box.minZ && met.minZ <= box.maxZ : met.maxX >= box.minX && met.minX <= box.maxX;
            if (!lined) { continue; }
            if (BeardPlots.roadAlongX(other) == alongX) {
                int ownCenter = alongX ? (box.minZ + box.maxZ) / 2 : (box.minX + box.maxX) / 2;
                int metCenter = alongX ? (met.minZ + met.maxZ) / 2 : (met.minX + met.maxX) / 2;
                if (ownCenter != metCenter) { continue; }
            }
            int ahead = alongX ? met.minX - box.maxX : met.minZ - box.maxZ;
            int behind = alongX ? box.minX - met.maxX : box.minZ - met.maxZ;
            int from = (alongX ? Math.min(box.maxX, met.maxX) : Math.min(box.maxZ, met.maxZ)) + 1;
            int to = (alongX ? Math.max(box.minX, met.minX) : Math.max(box.minZ, met.minZ)) - 1;
            if (ahead > 1 && ahead <= ContentBeard.attachGap() && free(everyone, piece, box, alongX, from, to) && uncrossed(everyone, piece, other, alongX, from, to, box) && ContentBeard.beside(everyone, ContentBeard.strip(box, alongX, from, to), alongX, piece) == null && !BeardRoadsTunnels.frontsHill(other, ContentBeard.strip(box, alongX, from, to))) {
                if (alongX) { box.maxX = met.minX - 1; }
                else { box.maxZ = met.minZ - 1; }
            }
            else if (behind > 1 && behind <= ContentBeard.attachGap() && free(everyone, piece, box, alongX, from, to) && uncrossed(everyone, piece, other, alongX, from, to, box) && ContentBeard.beside(everyone, ContentBeard.strip(box, alongX, from, to), alongX, piece) == null && !BeardRoadsTunnels.frontsHill(other, ContentBeard.strip(box, alongX, from, to))) {
                if (alongX) { box.minX = met.maxX + 1; }
                else { box.minZ = met.maxZ + 1; }
            }
        }
        for (StructureComponent other : everyone) {
            if (other == piece || !(other instanceof StructureVillagePieces.Path)) { continue; }
            StructureBoundingBox met = ((IStructureComponentBox) other).rdpl$box();
            if (met == null || BeardPlots.roadAlongX(other) == alongX) { continue; }
            square(everyone, piece, box, alongX, met, true);
        }
    }

    public static boolean joins(List<StructureComponent> pieces, StructureComponent piece, StructureComponent other) {
        if (!(piece instanceof StructureVillagePieces.Path) || !(other instanceof StructureVillagePieces.Path)) { return false; }
        StructureBoundingBox box = piece.getBoundingBox();
        StructureBoundingBox met = other.getBoundingBox();
        boolean alongX = BeardPlots.roadAlongX(piece);
        if (BeardRoads.roadNarrow(box, alongX) || BeardRoads.roadNarrow(met, BeardPlots.roadAlongX(other))) { return false; }
        boolean lined = alongX ? met.maxZ >= box.minZ && met.minZ <= box.maxZ : met.maxX >= box.minX && met.minX <= box.maxX;
        if (!lined) { return false; }
        int gap = alongX ? Math.max(met.minX - box.maxX, box.minX - met.maxX) : Math.max(met.minZ - box.maxZ, box.minZ - met.maxZ);
        int from = (alongX ? Math.min(box.maxX, met.maxX) : Math.min(box.maxZ, met.maxZ)) + 1;
        int to = (alongX ? Math.max(box.minX, met.minX) : Math.max(box.minZ, met.minZ)) - 1;
        if (BeardPlots.roadAlongX(other) == alongX) {
            int ownCenter = alongX ? (box.minZ + box.maxZ) / 2 : (box.minX + box.maxX) / 2;
            int metCenter = alongX ? (met.minZ + met.maxZ) / 2 : (met.minX + met.maxX) / 2;
            if (gap <= 1) { return ownCenter == metCenter; }
            if (ownCenter == metCenter) { return gap <= ContentBeard.attachGap() * 2 && closable(pieces, piece, other, box, alongX, from, to); }
            return gap >= BeardRoads.pathFullWidth() && gap <= ContentBeard.attachGap() * 2 && closable(pieces, piece, other, box, alongX, from, to);
        }
        if (gap <= 1) { return true; }
        if (gap > ContentBeard.attachGap()) { return false; }
        return free(pieces, piece, box, alongX, from, to) && uncrossed(pieces, piece, other, alongX, from, to, box) && !BeardRoadsTunnels.frontsHill(other, ContentBeard.strip(box, alongX, from, to));
    }

    private static boolean closable(List<StructureComponent> pieces, StructureComponent piece, StructureComponent other, StructureBoundingBox box, boolean alongX, int from, int to) {
        int minX = alongX ? from : box.minX;
        int maxX = alongX ? to : box.maxX;
        int minZ = alongX ? box.minZ : from;
        int maxZ = alongX ? box.maxZ : to;
        for (StructureComponent held : pieces) {
            if (held == piece || held == other) { continue; }
            StructureBoundingBox spot = held.getBoundingBox();
            if (!spot.intersectsWith(minX, minZ, maxX, maxZ)) { continue; }
            if (held instanceof StructureVillagePieces.Well || held instanceof MergePiece) { return false; }
            if (held instanceof StructureVillagePieces.Path && !BeardRoads.roadNarrow(spot, BeardPlots.roadAlongX(held))) { return false; }
        }
        return true;
    }

    private static boolean uncrossed(List<StructureComponent> pieces, StructureComponent piece, StructureComponent met, boolean alongX, int from, int to, StructureBoundingBox box) {
        int minX = alongX ? from : box.minX;
        int maxX = alongX ? to : box.maxX;
        int minZ = alongX ? box.minZ : from;
        int maxZ = alongX ? box.maxZ : to;
        for (StructureComponent other : pieces) {
            if (other == piece || other == met || !(other instanceof StructureVillagePieces.Path)) { continue; }
            if (BeardPlots.roadAlongX(other) == alongX) { continue; }
            if (BeardRoads.roadNarrow(other.getBoundingBox(), BeardPlots.roadAlongX(other))) { continue; }
            if (other.getBoundingBox().intersectsWith(minX, minZ, maxX, maxZ)) { return false; }
        }
        return true;
    }

    @Nullable private static int[] cornerRows(StructureBoundingBox box, boolean alongX, StructureBoundingBox met, boolean back) { return cornerRows(box, alongX, met, back, false); }

    @Nullable private static int[] cornerRows(StructureBoundingBox box, boolean alongX, StructureBoundingBox met, boolean back, boolean abutting) {
        int acrossLeast = alongX ? box.minZ : box.minX;
        int acrossMost = alongX ? box.maxZ : box.maxX;
        int metAlongLeast = alongX ? met.minZ : met.minX;
        int metAlongMost = alongX ? met.maxZ : met.maxX;
        if (metAlongMost < acrossLeast - 1 || metAlongLeast > acrossMost + 1) { return null; }
        int metAcrossLeast = alongX ? met.minX : met.minZ;
        int metAcrossMost = alongX ? met.maxX : met.maxZ;
        int slack = abutting && (metAlongLeast < acrossLeast || metAlongMost > acrossMost) ? 1 : 0;
        if (back) {
            int least = alongX ? box.minX : box.minZ;
            return least > metAcrossLeast && least <= metAcrossMost + slack ? new int[] { metAcrossLeast, least - 1 } : null;
        }
        int most = alongX ? box.maxX : box.maxZ;
        return most < metAcrossMost && most >= metAcrossLeast - slack ? new int[] { most + 1, metAcrossMost } : null;
    }

    private static boolean plazaHeld(List<StructureComponent> pieces, StructureBoundingBox box, boolean alongX, int least, int most) {
        int acrossLeast = alongX ? box.minZ : box.minX;
        int acrossMost = alongX ? box.maxZ : box.maxX;
        for (StructureBoundingBox square : BeardPlots.plazaSquares(pieces)) {
            int alongLo = alongX ? square.minX : square.minZ;
            int alongHi = alongX ? square.maxX : square.maxZ;
            int acrossLo = alongX ? square.minZ : square.minX;
            int acrossHi = alongX ? square.maxZ : square.maxX;
            if (least >= alongLo && most <= alongHi && acrossLeast >= acrossLo && acrossMost <= acrossHi) { return true; }
        }
        return false;
    }

    private static boolean square(List<StructureComponent> pieces, @Nullable StructureComponent piece, StructureBoundingBox box, boolean alongX, StructureBoundingBox met) { return square(pieces, piece, box, alongX, met, false); }

    private static boolean square(List<StructureComponent> pieces, @Nullable StructureComponent piece, StructureBoundingBox box, boolean alongX, StructureBoundingBox met, boolean abutting) {
        boolean grew = false;
        int[] backRows = cornerRows(box, alongX, met, true, abutting);
        if (backRows != null && plazaHeld(pieces, box, alongX, backRows[0], backRows[1])) { backRows = null; }
        if (backRows != null) {
            if (free(pieces, piece, box, alongX, backRows[0], backRows[1]) && ContentBeard.beside(pieces, ContentBeard.strip(box, alongX, backRows[0], backRows[1]), alongX, piece) == null && !ContentBeard.taken(pieces, ContentBeard.strip(box, alongX, backRows[0], backRows[1]))) {
                if (alongX) { box.minX = backRows[0]; }
                else { box.minZ = backRows[0]; }
                grew = true;
                ContentLog.LOGGER.debug("The road at {}, {} squares its corner back from {} to {} to line up with the road at {}, {}", box.minX, box.minZ, backRows[1] + 1, backRows[0], met.minX, met.minZ);
            }
            else { ContentLog.LOGGER.debug("The road at {}, {} keeps its corner at {} rather than squaring to {}, those rows being taken", box.minX, box.minZ, backRows[1] + 1, backRows[0]); }
        }
        int[] outRows = cornerRows(box, alongX, met, false, abutting);
        if (outRows != null && plazaHeld(pieces, box, alongX, outRows[0], outRows[1])) { outRows = null; }
        if (outRows != null) {
            if (free(pieces, piece, box, alongX, outRows[0], outRows[1]) && ContentBeard.beside(pieces, ContentBeard.strip(box, alongX, outRows[0], outRows[1]), alongX, piece) == null && !ContentBeard.taken(pieces, ContentBeard.strip(box, alongX, outRows[0], outRows[1]))) {
                if (alongX) { box.maxX = outRows[1]; }
                else { box.maxZ = outRows[1]; }
                grew = true;
                ContentLog.LOGGER.debug("The road at {}, {} squares its corner out from {} to {} to line up with the road at {}, {}", box.minX, box.minZ, outRows[0] - 1, outRows[1], met.minX, met.minZ);
            }
            else { ContentLog.LOGGER.debug("The road at {}, {} keeps its corner at {} rather than squaring to {}, those rows being taken", box.minX, box.minZ, outRows[0] - 1, outRows[1]); }
        }
        return grew;
    }

    public static boolean claimCorners(List<StructureComponent> pieces, StructureBoundingBox box, boolean alongX) {
        for (StructureComponent other : pieces) {
            if (!(other instanceof StructureVillagePieces.Path)) { continue; }
            StructureBoundingBox met = ((IStructureComponentBox) other).rdpl$box();
            if (met == null || BeardPlots.roadAlongX(other) == alongX) { continue; }
            int[] back = cornerRows(box, alongX, met, true);
            if (back != null && !plazaHeld(pieces, box, alongX, back[0], back[1]) && !free(pieces, null, box, alongX, back[0], back[1])) { return false; }
            int[] out = cornerRows(box, alongX, met, false);
            if (out != null && !plazaHeld(pieces, box, alongX, out[0], out[1]) && !free(pieces, null, box, alongX, out[0], out[1])) { return false; }
            int[] metBack = cornerRows(met, !alongX, box, true);
            if (metBack != null && !plazaHeld(pieces, met, !alongX, metBack[0], metBack[1]) && !free(pieces, other, met, !alongX, metBack[0], metBack[1])) { return false; }
            int[] metOut = cornerRows(met, !alongX, box, false);
            if (metOut != null && !plazaHeld(pieces, met, !alongX, metOut[0], metOut[1]) && !free(pieces, other, met, !alongX, metOut[0], metOut[1])) { return false; }
        }
        for (StructureComponent other : pieces) {
            if (!(other instanceof StructureVillagePieces.Path)) { continue; }
            StructureBoundingBox met = ((IStructureComponentBox) other).rdpl$box();
            if (met == null || BeardPlots.roadAlongX(other) == alongX) { continue; }
            square(pieces, null, box, alongX, met);
            if (square(pieces, other, met, !alongX, box) && other instanceof IRoadLayout) { ((IRoadLayout) other).rdpl$layout(null); }
        }
        return true;
    }

    private static boolean free(List<StructureComponent> pieces, @Nullable StructureComponent piece, StructureBoundingBox box, boolean alongX, int least, int most) {
        StructureBoundingBox strip = alongX ? new StructureBoundingBox(least, box.minY, box.minZ, most, box.maxY, box.maxZ) : new StructureBoundingBox(box.minX, box.minY, least, box.maxX, box.maxY, most);
        for (StructureComponent other : pieces) {
            if (other == piece || BeardRails.buriedUnder(other, strip)) { continue; }
            StructureBoundingBox held = other.getBoundingBox();
            boolean acrossed = alongX ? held.maxZ >= box.minZ && held.minZ <= box.maxZ : held.maxX >= box.minX && held.minX <= box.maxX;
            boolean along = alongX ? held.maxX >= least && held.minX <= most : held.maxZ >= least && held.minZ <= most;
            if (!acrossed || !along) { continue; }
            if (!(other instanceof StructureVillagePieces.Path)) { return false; }
            if (BeardRoads.roadNarrow(held, BeardPlots.roadAlongX(other))) { continue; }
            if (BeardPlots.roadAlongX(other) != alongX) { continue; }
            return false;
        }
        return true;
    }
}
