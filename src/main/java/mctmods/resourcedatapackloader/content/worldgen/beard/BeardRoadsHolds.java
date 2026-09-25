package mctmods.resourcedatapackloader.content.worldgen.beard;

import mctmods.resourcedatapackloader.content.village.CityGrowth;
import mctmods.resourcedatapackloader.content.worldgen.ContentBeard;
import mctmods.resourcedatapackloader.content.worldgen.beard.interfaces.IRoadLayout;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

public final class BeardRoadsHolds {
    private BeardRoadsHolds() {}

    private static boolean wetSquare(World world, StructureBoundingBox ew, StructureBoundingBox ns, StructureBoundingBox mine, List<StructureBoundingBox> roads) {
        if (BeardSurface.unreadable(world) || BeardRoads.roadNarrow(ew, true) || BeardRoads.roadNarrow(ns, false)) { return false; }
        for (int x = ns.minX; x <= ns.maxX; x++) {
            if (!wetStrip(world, x, x, ew.minZ, ew.maxZ)) { return false; }
        }
        for (int z = ew.minZ; z <= ew.maxZ; z++) {
            if (!wetStrip(world, ns.minX, ns.maxX, z, z)) { return false; }
        }
        int reach = BeardRoadsSurface.arms(ew, ns, mine, roads);
        if ((reach & 1) != 0 && !wetStrip(world, ns.minX - 1, ns.minX - 1, ew.minZ, ew.maxZ)) { return false; }
        if ((reach & 2) != 0 && !wetStrip(world, ns.maxX + 1, ns.maxX + 1, ew.minZ, ew.maxZ)) { return false; }
        if ((reach & 4) != 0 && !wetStrip(world, ns.minX, ns.maxX, ew.minZ - 1, ew.minZ - 1)) { return false; }
        return (reach & 8) == 0 || wetStrip(world, ns.minX, ns.maxX, ew.maxZ + 1, ew.maxZ + 1);
    }

    private static boolean wetStrip(World world, int minX, int maxX, int minZ, int maxZ) {
        int wet = 0;
        int cells = 0;
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                cells++;
                if (BeardSurface.surfaceAt(world, x, z) < world.getSeaLevel() - 2) { wet++; }
            }
        }
        return wet * 2 > cells;
    }

    private static int reachable(int[] profile, boolean[] held, int start, int rowMost, int least, int most, int grade) {
        int low = Integer.MIN_VALUE;
        int high = Integer.MAX_VALUE;
        for (int row = least - 1; row >= start; row--) {
            int i = row - start;
            if (!held[i] || profile[i] == Integer.MIN_VALUE) { continue; }
            int steps = least - row;
            low = Math.max(low, profile[i] - steps);
            high = Math.min(high, profile[i] + steps);
            break;
        }
        for (int row = most + 1; row <= rowMost; row++) {
            int i = row - start;
            if (!held[i] || profile[i] == Integer.MIN_VALUE) { continue; }
            int steps = row - most;
            low = Math.max(low, profile[i] - steps);
            high = Math.min(high, profile[i] + steps);
            break;
        }
        if (low > high) { return grade < low ? low : high; }
        return MathHelper.clamp(grade, low, high);
    }

    public static void roadApron(World world, @Nullable StructureComponent piece, boolean alongX, int start, int rowMost, int acrossLeast, int acrossMost, int[] profile, int[] ground, boolean[] held, boolean[] footed, boolean[] fixed, boolean[] bridged, boolean[] square, boolean[] decks) {
        List<StructureComponent> pieces = BeardRoads.villagePieces(world);
        if (pieces.isEmpty()) { return; }
        List<int[]> flats = new ArrayList<>();
        StructureBoundingBox own = piece != null ? piece.getBoundingBox()
                : new StructureBoundingBox(alongX ? start : acrossLeast, 0, alongX ? acrossLeast : start, alongX ? rowMost : acrossMost, 0, alongX ? acrossMost : rowMost);
        List<StructureBoundingBox> roads = BeardRoads.crossings(pieces, piece, own);
        for (StructureComponent other : pieces) {
            if (other == piece || !(other instanceof StructureVillagePieces.Path)) { continue; }
            StructureBoundingBox road = other.getBoundingBox();
            boolean otherAlongX = BeardPlots.roadAlongX(other);
            if (otherAlongX == alongX) {
                int ownCenter = alongX ? (own.minZ + own.maxZ) / 2 : (own.minX + own.maxX) / 2;
                int metCenter = alongX ? (road.minZ + road.maxZ) / 2 : (road.minX + road.maxX) / 2;
                if (ownCenter != metCenter) { continue; }
                int otherLeast = alongX ? road.minX : road.minZ;
                int otherMost = alongX ? road.maxX : road.maxZ;
                boolean onward = otherLeast > rowMost && otherLeast - rowMost <= ContentBeard.attachGap();
                boolean backward = otherMost < start && start - otherMost <= ContentBeard.attachGap();
                if (!onward && !backward) { continue; }
                if (occupied(pieces, piece, other, alongX, onward ? rowMost + 1 : otherMost + 1, onward ? otherLeast - 1 : start - 1, acrossLeast, acrossMost)) { continue; }
                BeardRoads.Grade next = BeardRoadsGrade.chainGrade(world, other, otherAlongX);
                int met = next == null ? Integer.MIN_VALUE : next.at(onward ? otherLeast : otherMost);
                if (met == Integer.MIN_VALUE) { continue; }
                int seam = 1 + BeardRoads.pathExtraWidth() + 3;
                int seamLeast = Math.max(start, onward ? rowMost - seam + 1 : start);
                int seamMost = Math.min(rowMost, onward ? rowMost : start + seam - 1);
                boolean decked = next.bridgedAt(onward ? otherLeast : otherMost);
                int plaza = plazaLevel(world, alongX, seamLeast, seamMost, acrossLeast, acrossMost);
                if (plaza != Integer.MIN_VALUE) {
                    if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("The road at {}, {} continues the road at {}, {} inside a well's plaza, so its seam rows take the plaza's level, y {}, not that road's y {}", own.minX, own.minZ, road.minX, road.minZ, plaza, met); }
                    met = plaza;
                    decked = false;
                }
                else if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("The road at {}, {} continues the road at {}, {}, so its seam rows hold that road's level, y {}", own.minX, own.minZ, road.minX, road.minZ, met); }
                for (int row = seamLeast; row <= seamMost; row++) {
                    profile[row - start] = met;
                    held[row - start] = true;
                    fixed[row - start] = true;
                    bridged[row - start] = decked && ground[row - start] == Integer.MIN_VALUE;
                }
                continue;
            }
            boolean overX = road.maxX >= own.minX && road.minX <= own.maxX;
            boolean overZ = road.maxZ >= own.minZ && road.minZ <= own.maxZ;
            boolean nearX = road.maxX >= own.minX - 1 && road.minX <= own.maxX + 1;
            boolean nearZ = road.maxZ >= own.minZ - 1 && road.minZ <= own.maxZ + 1;
            if (!(overX && nearZ) && !(overZ && nearX)) { continue; }
            int otherLeast = alongX ? road.minX : road.minZ;
            int otherMost = alongX ? road.maxX : road.maxZ;
            int center = MathHelper.clamp((otherLeast + otherMost) / 2, start, rowMost);
            boolean ending = otherLeast == rowMost + 1 || otherMost == start - 1;
            boolean wet = !CityGrowth.bulbWide(other) && wetSquare(world, alongX ? own : road, alongX ? road : own, own, roads);
            if (ending) {
                int crossRow = otherAlongX ? (own.minX + own.maxX) / 2 : (own.minZ + own.maxZ) / 2;
                BeardRoads.Grade crossing = CityGrowth.bulbWide(other) ? null : BeardRoadsGrade.chainGrade(world, other, otherAlongX);
                int crossed = crossing == null ? Integer.MIN_VALUE : crossing.at(crossRow);
                if (CityGrowth.bulbWide(other)) {
                    BeardRoadsEnds.Bulb pad = BeardRoadsEnds.bulbAt(world, other);
                    if (pad != null) { crossed = pad.level; }
                }
                int ownGrade = profile[center - start];
                int reach = 1 + BeardRoads.pathExtraWidth() + 3;
                int grade = plazaLevel(world, alongX, Math.max(start, center - reach), Math.min(rowMost, center + reach), acrossLeast, acrossMost);
                if (grade != Integer.MIN_VALUE) {
                    wet = false;
                    if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("The road at {}, {} ends on the road at {}, {} inside a well's plaza, so its mouth rows take the plaza's level, y {}", own.minX, own.minZ, road.minX, road.minZ, grade); }
                }
                else {
                    if (crossed != Integer.MIN_VALUE) { grade = crossed; }
                    else if (ownGrade != Integer.MIN_VALUE) { grade = ownGrade; }
                    else { grade = Math.max(BeardRoads.Grade.carried(profile, center - start), crossing == null ? Integer.MIN_VALUE : crossing.deckAt(crossRow)); }
                    if (grade == Integer.MIN_VALUE) { continue; }
                    grade = afloat(world, ground, start, center - reach, center + reach, grade);
                }
                if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("The road at {}, {} ends on the road at {}, {}, so its mouth rows around {} hold the junction's level, y {}{}", own.minX, own.minZ, road.minX, road.minZ, center, grade, wet ? ", decked over the water with the square it meets" : ""); }
                for (int row = center - reach; row <= center + reach; row++) {
                    if (row < start || row > rowMost) { continue; }
                    profile[row - start] = grade;
                    held[row - start] = true;
                    fixed[row - start] = true;
                    square[row - start] = true;
                    decks[row - start] = wet && ground[row - start] == Integer.MIN_VALUE;
                    bridged[row - start] = decks[row - start];
                }
            }
            else {
                int grade = profile[center - start];
                if (grade == Integer.MIN_VALUE) { grade = BeardRoads.Grade.carried(profile, center - start); }
                boolean overlapped = road.maxX >= own.minX && road.minX <= own.maxX && road.maxZ >= own.minZ && road.minZ <= own.maxZ;
                int crossRow = otherAlongX ? (own.minX + own.maxX) / 2 : (own.minZ + own.maxZ) / 2;
                boolean owned = false;
                if (overlapped && !owns(own, road)) {
                    BeardRoads.Grade crossing = BeardRoadsGrade.chainGrade(world, other, otherAlongX);
                    int crossed = crossing == null ? Integer.MIN_VALUE : crossing.at(crossRow);
                    if (crossed != Integer.MIN_VALUE) {
                        grade = crossed;
                        owned = true;
                    }
                }
                else if (other instanceof IRoadLayout) {
                    BeardRoads.Grade laid = ((IRoadLayout) other).rdpl$layout();
                    int crossed = laid == null ? Integer.MIN_VALUE : laid.at(crossRow);
                    if (crossed > grade) {
                        if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("The road at {}, {} raises its junction with the road at {}, {} from y {} to y {}, the level that road was lifted to out of a dip", own.minX, own.minZ, road.minX, road.minZ, grade, crossed); }
                        grade = crossed;
                    }
                }
                int plaza = plazaLevel(world, alongX, Math.max(start, otherLeast), Math.min(rowMost, otherMost), acrossLeast, acrossMost);
                if (plaza != Integer.MIN_VALUE) {
                    wet = false;
                    flats.add(new int[] { otherLeast, otherMost, plaza, road.minX, road.minZ, 1, 0, 0 });
                }
                else if (grade != Integer.MIN_VALUE) { flats.add(new int[] { otherLeast, otherMost, afloat(world, ground, start, otherLeast, otherMost, grade), road.minX, road.minZ, 0, wet ? 1 : 0, owned ? 1 : 0 }); }
            }
            int squareLeast = alongX ? road.minX : road.minZ;
            int squareMost = alongX ? road.maxX : road.maxZ;
            for (int row = squareLeast; row <= squareMost; row++) {
                if (row < start || row > start + profile.length - 1) { continue; }
                footed[row - start] = !wet;
                square[row - start] = true;
                decks[row - start] = wet;
                bridged[row - start] = wet;
            }
        }
        mergeFlats(own, flats);
        for (int[] flat : flats) {
            int otherLeast = flat[0];
            int otherMost = flat[1];
            int grade = flat[5] == 1 || flat[6] == 1 || flat[7] == 1 ? flat[2] : reachable(profile, held, start, rowMost, otherLeast, otherMost, flat[2]);
            if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("The road at {}, {} flattens its junction with the road at {}, {} to one level, y {}{}, over rows {} to {}", own.minX, own.minZ, flat[3], flat[4], grade, grade == flat[2] ? "" : " (brought from y " + flat[2] + " within reach of the rows already held)", Math.max(start, otherLeast), Math.min(rowMost, otherMost)); }
            for (int row = otherLeast; row <= otherMost; row++) {
                if (row < start || row > rowMost) { continue; }
                profile[row - start] = grade;
                held[row - start] = true;
                fixed[row - start] = true;
                square[row - start] = true;
                decks[row - start] = flat[6] == 1;
                bridged[row - start] = decks[row - start];
                footed[row - start] = !decks[row - start];
            }
        }
    }

    private static void mergeFlats(StructureBoundingBox own, List<int[]> flats) {
        for (int[] flat : flats) {
            for (int[] other : flats) {
                if (other[0] > flat[1] || other[1] < flat[0] || other[6] != 0) { continue; }
                flat[6] = 0;
            }
        }
        for (boolean merged = true; merged; ) {
            merged = false;
            for (int[] flat : flats) {
                if (flat[5] == 1) { continue; }
                for (int[] other : flats) {
                    if (other == flat || other[0] > flat[1] || other[1] < flat[0]) { continue; }
                    if (other[5] == 1) {
                        if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("The road at {}, {} holds its junction with the road at {}, {} over rows it shares with a junction inside a well's plaza, so it stands at the plaza's level, y {}", own.minX, own.minZ, flat[3], flat[4], other[2]); }
                        flat[2] = other[2];
                        flat[5] = 1;
                        merged = true;
                        break;
                    }
                    if (other[7] == 1 && flat[7] == 0) {
                        if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("The road at {}, {} holds its junction with the road at {}, {} over rows it shares with the square the road at {}, {} owns, so it stands at that square's level, y {}", own.minX, own.minZ, flat[3], flat[4], other[3], other[4], other[2]); }
                        flat[2] = other[2];
                        flat[7] = 1;
                        merged = true;
                        break;
                    }
                    if (other[7] != flat[7] || other[2] <= flat[2]) { continue; }
                    if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("The road at {}, {} holds its junctions with the roads at {}, {} and {}, {} over shared rows, so both stand at the higher level, y {}", own.minX, own.minZ, flat[3], flat[4], other[3], other[4], other[2]); }
                    flat[2] = other[2];
                    merged = true;
                }
            }
        }
    }

    private static int plazaLevel(World world, boolean alongX, int rowLeast, int rowMost, int acrossLeast, int acrossMost) {
        if (rowMost < rowLeast) { return Integer.MIN_VALUE; }
        int reach = ContentBeard.plazaReach();
        int minX = alongX ? rowLeast : acrossLeast;
        int maxX = alongX ? rowMost : acrossMost;
        int minZ = alongX ? acrossLeast : rowLeast;
        int maxZ = alongX ? acrossMost : rowMost;
        for (StructureBoundingBox well : BeardPlots.wellBoxes(ContentBeard.components())) {
            if (well.maxX + reach >= minX && well.minX - reach <= maxX && well.maxZ + reach >= minZ && well.minZ - reach <= maxZ) { return BeardSite.wellGround(world, well); }
        }
        return Integer.MIN_VALUE;
    }

    private static int afloat(World world, int[] ground, int start, int least, int most, int grade) {
        for (int row = Math.max(start, least); row <= Math.min(start + ground.length - 1, most); row++) {
            if (ground[row - start] == Integer.MIN_VALUE) { return Math.max(grade, world.getSeaLevel()); }
        }
        return grade;
    }

    private static boolean occupied(List<StructureComponent> pieces, @Nullable StructureComponent piece, StructureComponent other, boolean alongX, int lo, int hi, int acrossLeast, int acrossMost) {
        if (hi < lo) { return false; }
        int minX = alongX ? lo : acrossLeast;
        int maxX = alongX ? hi : acrossMost;
        int minZ = alongX ? acrossLeast : lo;
        int maxZ = alongX ? acrossMost : hi;
        for (StructureComponent met : pieces) {
            if (met == piece || met == other) { continue; }
            StructureBoundingBox held = met.getBoundingBox();
            if (held.maxX >= minX && held.minX <= maxX && held.maxZ >= minZ && held.minZ <= maxZ) { return true; }
        }
        return false;
    }

    private static boolean owns(StructureBoundingBox own, StructureBoundingBox met) {
        if (own.minX != met.minX) { return own.minX < met.minX; }
        if (own.minZ != met.minZ) { return own.minZ < met.minZ; }
        if (own.maxX != met.maxX) { return own.maxX < met.maxX; }
        return own.maxZ <= met.maxZ;
    }

    public static void frontHold(@Nullable StructureComponent piece, boolean alongX, int start, int acrossLeast, int acrossMost, int[] profile, boolean[] held) {
        List<StructureComponent> pieces = ContentBeard.components();
        if (pieces == null) { return; }
        int rowMost = start + profile.length - 1;
        for (StructureComponent other : pieces) {
            if (other == piece || other instanceof StructureVillagePieces.Path || BeardRails.isRail(other)) { continue; }
            StructureBoundingBox front = other.getBoundingBox();
            if ((alongX ? front.maxZ : front.maxX) < acrossLeast - 3 || (alongX ? front.minZ : front.minX) > acrossMost + 3) { continue; }
            int otherLeast = alongX ? front.minX : front.minZ;
            int otherMost = alongX ? front.maxX : front.maxZ;
            if (otherMost < start || otherLeast > rowMost) { continue; }
            int center = MathHelper.clamp((otherLeast + otherMost) / 2, start, rowMost);
            int grade = profile[center - start];
            if (grade == Integer.MIN_VALUE) { continue; }
            int spanLo = Math.max(start, otherLeast);
            int spanHi = Math.min(rowMost, otherMost);
            boolean feasible = true;
            for (int j = 0; j < profile.length; j++) {
                if (!held[j] || profile[j] == Integer.MIN_VALUE) { continue; }
                int at = start + j;
                int away = at < spanLo ? spanLo - at : at > spanHi ? at - spanHi : 0;
                if (Math.abs(grade - profile[j]) > away) {
                    feasible = false;
                    break;
                }
            }
            if (!feasible) {
                if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("The frontage of {} at {}, {} stands at y {}, which the road cannot reach at a walkable slope from its pinned rows, so it is not held to it", other.getClass().getSimpleName(), front.minX, front.minZ, grade); }
                continue;
            }
            int pinnedRows = 0;
            for (int row = spanLo; row <= spanHi; row++) {
                if (held[row - start] || profile[row - start] == Integer.MIN_VALUE) { continue; }
                profile[row - start] = grade;
                held[row - start] = true;
                pinnedRows++;
            }
            if (pinnedRows > 0 && ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("The frontage of {} at {}, {} holds {} row(s) of the road beside it to y {}, the grade it stands at", other.getClass().getSimpleName(), front.minX, front.minZ, pinnedRows, grade); }
        }
    }

    public static void clampToWell(World world, boolean alongX, int start, int acrossLeast, int acrossMost, int[] profile, boolean[] held) {
        int reach = ContentBeard.plazaReach();
        for (StructureBoundingBox well : BeardPlots.wellBoxes(ContentBeard.components())) {
            if (acrossMost < (alongX ? well.minZ : well.minX) - reach || acrossLeast > (alongX ? well.maxZ : well.maxX) + reach) { continue; }
            int ground = BeardSite.wellGround(world, well);
            int rowLeast = (alongX ? well.minX : well.minZ) - reach;
            int rowMost = (alongX ? well.maxX : well.maxZ) + reach;
            int clamped = 0;
            for (int row = Math.max(start, rowLeast); row <= Math.min(start + profile.length - 1, rowMost); row++) {
                if (profile[row - start] == Integer.MIN_VALUE || held[row - start]) { continue; }
                held[row - start] = true;
                if (profile[row - start] == ground) { continue; }
                profile[row - start] = ground;
                clamped++;
            }
            if (clamped > 0 && ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("Clamped {} road row(s) beside the well at {}, {} to its ground at y {}", clamped, well.minX, well.minZ, ground); }
        }
    }
}
