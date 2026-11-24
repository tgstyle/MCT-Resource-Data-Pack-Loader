package mctmods.resourcedatapackloader.common.villages;

import mctmods.resourcedatapackloader.common.locate.ContentStructureSearch;
import mctmods.resourcedatapackloader.common.pack.loading.ContentControl;
import mctmods.resourcedatapackloader.common.util.world.Facings;
import mctmods.resourcedatapackloader.common.util.world.StructureBoxes;
import mctmods.resourcedatapackloader.common.worldgen.structure.city.BeardSite;
import mctmods.resourcedatapackloader.common.worldgen.structure.city.ContentBeard;
import mctmods.resourcedatapackloader.common.worldgen.structure.city.dress.ContentBeardJoins;
import mctmods.resourcedatapackloader.common.worldgen.structure.city.layout.BeardSurface;
import mctmods.resourcedatapackloader.common.worldgen.structure.city.plot.BeardPlots;
import mctmods.resourcedatapackloader.common.worldgen.structure.city.rail.BeardRails;
import mctmods.resourcedatapackloader.common.worldgen.structure.city.road.BeardRoads;
import mctmods.resourcedatapackloader.common.worldgen.structure.city.road.BeardRoadsGrade;
import mctmods.resourcedatapackloader.common.worldgen.structure.city.road.BeardRoadsTunnels;
import mctmods.resourcedatapackloader.core.config.Config;
import mctmods.resourcedatapackloader.common.util.worldgen.GenHeights;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.math.MathUtil;
import mctmods.resourcedatapackloader.core.util.math.Longs;

import java.util.*;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;

public final class CityDistricts {
    private static final int RELIEF = 6;
    private static final int JOIN = 4;
    private static final int TIE_REACH = 112;
    private static int settledCrowded;
    private static int settledUngrounded;
    private static int settledTaken;
    private static int settledNeighbored;

    private CityDistricts() {}

    static void drainSettled(int attempts, boolean report) {
        if (report && ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("District growth drained after {} attempt(s): {} sites too near a well, {} on unreadable ground, {} on built ground, {} beside another village", attempts, settledCrowded, settledUngrounded, settledTaken, settledNeighbored); }
        settledCrowded = settledUngrounded = settledTaken = settledNeighbored = 0;
    }

    static long site(List<StructureComponent> components, Set<Long> tried) {
        StructureBoundingBox first = components.get(0).getBoundingBox();
        int cx = StructureBoxes.centerX(first);
        int cz = StructureBoxes.centerZ(first);
        long best = Long.MIN_VALUE;
        long bestAway = Long.MAX_VALUE;
        int reach = CityGrowth.march();
        int spacing = CityGrid.spacing();
        for (StructureComponent piece : components) {
            if (!(piece instanceof StructureVillagePieces.Path)) { continue; }
            StructureBoundingBox road = piece.getBoundingBox();
            boolean alongX = BeardPlots.roadAlongX(road);
            int line = StructureBoxes.across(road, alongX);
            for (int sign = -1; sign <= 1; sign += 2) {
                int edge = alongX ? (sign < 0 ? road.minX : road.maxX) : (sign < 0 ? road.minZ : road.maxZ);
                for (int away = ContentBeard.plazaReach() + 4; away <= Math.max(48, spacing + 16); away += 8) {
                    int siteX = alongX ? edge + sign * away : line;
                    int siteZ = alongX ? line : edge + sign * away;
                    long far = MathUtil.dist2(siteX, siteZ, cx, cz);
                    if (far > (long) reach * reach || far >= bestAway) { continue; }
                    long packed = Longs.pack(siteX, siteZ);
                    if (tried.contains(packed) || crowdedAt(components, siteX, siteZ, spacing)) { continue; }
                    bestAway = far;
                    best = packed;
                }
            }
        }
        if (best != Long.MIN_VALUE) { return best; }
        int step = Math.max(8, spacing / 2);
        for (int dx = -reach; dx <= reach; dx += step) {
            for (int dz = -reach; dz <= reach; dz += step) {
                if (dx == 0 && dz == 0) { continue; }
                long away = MathUtil.dist2(dx, dz, 0, 0);
                if (away > (long) reach * reach || away >= bestAway) { continue; }
                long packed = Longs.pack(cx + dx, cz + dz);
                if (tried.contains(packed) || crowdedAt(components, cx + dx, cz + dz, spacing)) { continue; }
                bestAway = away;
                best = packed;
            }
        }
        return best;
    }

    static boolean settle(World world, Random rand, List<StructureComponent> components, int cx, int cz, int sizeFor) {
        int reach = ContentBeard.plazaReach();
        if (crowdedAt(components, cx, cz, CityGrid.spacing())) {
            settledCrowded++;
            return false;
        }
        int wellX = cx - CityLayout.WELL / 2 + 1;
        int wellZ = cz - CityLayout.WELL / 2 + 1;
        if (ungrounded(world, wellX, wellZ, reach)) {
            settledUngrounded++;
            return false;
        }
        StructureBoundingBox square = new StructureBoundingBox(wellX - reach, 0, wellZ - reach, wellX + CityLayout.WELL - 1 + reach, GenHeights.WORLD_TOP, wellZ + CityLayout.WELL - 1 + reach);
        if (BeardRails.boreUnder(components, new StructureBoundingBox(wellX, 0, wellZ, wellX + CityLayout.WELL - 1, GenHeights.WORLD_TOP, wellZ + CityLayout.WELL - 1))) {
            settledTaken++;
            return false;
        }
        for (StructureComponent other : components) {
            if (!BeardRails.buriedUnder(other, square) && other.getBoundingBox().intersectsWith(square)) {
                settledTaken++;
                return false;
            }
        }
        if (ContentStructureSearch.anyOtherOver(world, components, square)) {
            settledNeighbored++;
            return false;
        }
        List<StructureVillagePieces.PieceWeight> weights = StructureVillagePieces.getStructureVillageWeightedPieceList(rand, sizeFor);
        StructureVillagePieces.Start district = new StructureVillagePieces.Start(world.getBiomeProvider(), 0, rand, wellX, wellZ, weights, sizeFor);
        ContentVillages.sizeBlock(world, district);
        int mark = components.size();
        components.add(district);
        CityGrowth.laying(false, () -> CityGrowth.buildAndDrain(district, district, components, rand));
        int streets = 0;
        for (int i = mark; i < components.size(); i++) { if (components.get(i) instanceof StructureVillagePieces.Path) { streets++; } }
        if (streets == 0) {
            ContentLog.LOGGER.debug("The plaza at {}, {} could not grow a single street, so it is taken back down", wellX, wellZ);
            components.subList(mark, components.size()).clear();
            return false;
        }
        int rounds = 0;
        boolean tied = false;
        while (isolated(components, mark)) {
            int before = components.size();
            if (rounds < JOIN) {
                rounds++;
                CityGrowth.laying(false, () -> {
                    for (StructureComponent piece : components.subList(mark, before).toArray(new StructureComponent[0])) {
                        if (!(piece instanceof StructureVillagePieces.Path)) { continue; }
                        CityGrowth.buildAndDrain(district, piece, components, rand);
                    }
                });
            }
            if (components.size() == before && !tied && ContentControl.flag(ContentControl.VILLAGES, "villageTieStreets", Config.worldgen.villageTieStreets)) {
                tied = true;
                if (tieStreet(components, mark, district, rand, wellX, wellZ)) { continue; }
            }
            if (components.size() == before) {
                if (ContentLog.LOGGER.debugEnabled()) { apart(components, mark, wellX, wellZ, false); }
                ContentLog.LOGGER.debug("The district at {}, {} could not join its streets to the standing village, so it is taken back down", wellX, wellZ);
                components.subList(mark, components.size()).clear();
                return false;
            }
        }
        if (rounds > 0) { ContentLog.LOGGER.debug("The district at {}, {} joined the standing village after {} round(s) of street growth", wellX, wellZ, rounds); }
        if (isolated(components, mark, false)) {
            if (tieStreet(components, mark, district, rand, wellX, wellZ, true)) { ContentLog.LOGGER.debug("The district at {}, {} joined the standing village only across a gap, so that gap is paved as a street", wellX, wellZ); }
            else { ContentLog.LOGGER.debug("The district at {}, {} joined the standing village only across a gap that no tie street can pave, so the gap stands", wellX, wellZ); }
        }
        if (ContentLog.LOGGER.debugEnabled()) { apart(components, mark, wellX, wellZ, true); }
        CityGrowth.backRowsFrom(components, mark, rand);
        int shift = BeardSite.wellGround(world, district.getBoundingBox()) - BeardSite.wellNominal(district.getBoundingBox());
        if (shift != 0) {
            for (int i = mark; i < components.size(); i++) { components.get(i).getBoundingBox().offset(0, shift, 0); }
        }
        return true;
    }

    private static boolean tieStreet(List<StructureComponent> components, int mark, StructureVillagePieces.Start district, Random rand, int wellX, int wellZ) { return tieStreet(components, mark, district, rand, wellX, wellZ, false); }

    private static boolean tieStreet(List<StructureComponent> components, int mark, StructureVillagePieces.Start district, Random rand, int wellX, int wellZ, boolean closing) {
        StructureBoundingBox best = null;
        StructureComponent bestStreet = null;
        StructureBoundingBox bestMet = null;
        EnumFacing bestFacing = null;
        int bestLength = Integer.MAX_VALUE;
        int[] refused = new int[5];
        for (int i = mark; i < components.size(); i++) {
            StructureComponent street = components.get(i);
            if (!(street instanceof StructureVillagePieces.Path) || CityGrowth.bulbWide(street)) { continue; }
            StructureBoundingBox box = street.getBoundingBox();
            boolean alongX = BeardPlots.roadAlongX(street);
            if (BeardRoads.roadNarrow(street)) { continue; }
            int center = MathUtil.mid(BeardPlots.acrossLeast(box, alongX), BeardPlots.acrossMost(box, alongX));
            for (int dir = -1; dir <= 1; dir += 2) {
                int end = dir > 0 ? BeardPlots.most(box, alongX) : BeardPlots.least(box, alongX);
                for (int j = 0; j < mark; j++) {
                    StructureComponent other = components.get(j);
                    if (!(other instanceof StructureVillagePieces.Path) || CityGrowth.bulbWide(other)) { continue; }
                    StructureBoundingBox met = other.getBoundingBox();
                    boolean otherAlongX = BeardPlots.roadAlongX(other);
                    if (BeardRoads.roadNarrow(other)) { continue; }
                    int metAcrossLo = BeardPlots.acrossLeast(met, alongX);
                    int metAcrossHi = BeardPlots.acrossMost(met, alongX);
                    if (otherAlongX != alongX) {
                        if (center < metAcrossLo || center > metAcrossHi) { continue; }
                    }
                    else if (MathUtil.mid(metAcrossLo, metAcrossHi) != center) { continue; }
                    int near = dir > 0 ? BeardPlots.least(met, alongX) : BeardPlots.most(met, alongX);
                    if ((near - end) * dir <= 1) { continue; }
                    int from = dir > 0 ? end + 1 : near + 1;
                    int to = dir > 0 ? near - 1 : end - 1;
                    int length = to - from + 1;
                    if (length >= bestLength) { continue; }
                    if (length > TIE_REACH || !closing && length <= BeardRoads.pathFullWidth()) {
                        refused[0]++;
                        continue;
                    }
                    StructureBoundingBox tie = ContentBeard.strip(box, alongX, from, to);
                    if (standsOn(components, tie) || ContentBeard.taken(components, tie)) {
                        refused[1]++;
                        continue;
                    }
                    if (ContentBeard.beside(components, tie, alongX, street) != null) {
                        refused[2]++;
                        continue;
                    }
                    if (BeardRoadsTunnels.crossesHill(components, tie)) {
                        refused[3]++;
                        continue;
                    }
                    EnumFacing facing = Facings.along(alongX, dir > 0);
                    int kept = ContentBeard.layingOf(components, () -> BeardRoadsGrade.roadReach(tie, facing));
                    if (kept < length) {
                        refused[4]++;
                        continue;
                    }
                    best = tie;
                    bestStreet = street;
                    bestMet = met;
                    bestFacing = facing;
                    bestLength = length;
                }
            }
        }
        if (best == null) {
            ContentLog.LOGGER.debug("The district at {}, {} has no straight, level and free line from any of its street ends to a standing street within {} blocks, so no tie street can join it: {} too short or long, {} across a piece, {} beside a road, {} through a tunnel, {} too steep", wellX, wellZ, TIE_REACH, refused[0], refused[1], refused[2], refused[3], refused[4]);
            return false;
        }
        StructureVillagePieces.Path lane = new StructureVillagePieces.Path(district, 0, rand, best, bestFacing);
        components.add(lane);
        ContentLog.LOGGER.debug("The district at {}, {} could not grow to the standing village, so a tie street of {} row(s) is laid from the end of its street at {}, {} to the standing street at {}, {}", wellX, wellZ, bestLength, bestStreet.getBoundingBox().minX, bestStreet.getBoundingBox().minZ, bestMet.minX, bestMet.minZ);
        return true;
    }

    private static boolean standsOn(List<StructureComponent> components, StructureBoundingBox tie) {
        for (StructureComponent held : ContentBeard.everyone(components)) {
            if (BeardRails.buriedUnder(held, tie)) { continue; }
            StructureBoundingBox met = held.getBoundingBox();
            if (held instanceof RailPiece && BeardRails.crosses(met, tie)) { continue; }
            if (met.intersectsWith(tie.minX, tie.minZ, tie.maxX, tie.maxZ)) { return true; }
        }
        return false;
    }

    private static void apart(List<StructureComponent> components, int mark, int wellX, int wellZ, boolean joined) {
        int own = 0;
        for (int i = mark; i < components.size(); i++) {
            StructureComponent piece = components.get(i);
            if (!(piece instanceof StructureVillagePieces.Path)) { continue; }
            StructureBoundingBox road = piece.getBoundingBox();
            boolean narrow = BeardRoads.roadNarrow(road, BeardPlots.roadAlongX(road));
            own++;
            StructureBoundingBox nearest = null;
            int gap = Integer.MAX_VALUE;
            for (int j = 0; j < mark; j++) {
                StructureComponent other = components.get(j);
                if (!(other instanceof StructureVillagePieces.Path) && !(other instanceof StructureVillagePieces.Well)) { continue; }
                StructureBoundingBox met = other.getBoundingBox();
                int dx = Math.max(0, Math.max(met.minX - road.maxX, road.minX - met.maxX));
                int dz = Math.max(0, Math.max(met.minZ - road.maxZ, road.minZ - met.maxZ));
                int away = Math.max(dx, dz);
                if (away < gap) {
                    gap = away;
                    nearest = met;
                }
            }
            ContentLog.LOGGER.debug("The district at {}, {} {} and laid a {} road {} whose nearest standing road or well is {} block(s) off at {}", wellX, wellZ, joined ? "joined" : "failed", narrow ? "narrow" : "full", road, gap, nearest);
        }
        if (own == 0) { ContentLog.LOGGER.debug("The district at {}, {} laid no road at all", wellX, wellZ); }
    }

    private static boolean isolated(List<StructureComponent> own, int mark) { return isolated(own, mark, true); }

    private static boolean isolated(List<StructureComponent> own, int mark, boolean joins) {
        List<StructureComponent> components = ContentBeard.everyone(own);
        int count = components.size();
        int standing = own.size();
        int reach = ContentBeard.plazaReach();
        StructureBoundingBox[] boxes = new StructureBoundingBox[count];
        for (int i = 0; i < count; i++) {
            StructureComponent piece = components.get(i);
            if (piece instanceof StructureVillagePieces.Well) {
                StructureBoundingBox well = piece.getBoundingBox();
                boxes[i] = new StructureBoundingBox(well.minX - reach, well.minY, well.minZ - reach, well.maxX + reach, well.maxY, well.maxZ + reach);
            }
            else if (piece instanceof StructureVillagePieces.Path) {
                StructureBoundingBox road = piece.getBoundingBox();
                if (!BeardRoads.roadNarrow(road, BeardPlots.roadAlongX(road))) { boxes[i] = road; }
            }
            else if (piece instanceof MergePiece) { boxes[i] = piece.getBoundingBox(); }
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
                if (!touching && (!joins || !ContentBeardJoins.joins(components, components.get(at), components.get(i)))) { continue; }
                if (i < mark || i >= standing) { return false; }
                seen[i] = true;
                queue.add(i);
            }
        }
        return true;
    }

    private static boolean crowdedAt(List<StructureComponent> components, int cx, int cz, int spacing) {
        for (StructureBoundingBox well : BeardPlots.wellBoxes(components)) {
            if (MathUtil.dist2(StructureBoxes.centerX(well), StructureBoxes.centerZ(well), cx, cz) < (long) spacing * spacing) { return true; }
        }
        return false;
    }

    private static boolean ungrounded(World world, int wellX, int wellZ, int reach) {
        if (BeardSurface.unreadable(world)) { return false; }
        int sea = world.getSeaLevel();
        int lowest = Integer.MAX_VALUE;
        int highest = Integer.MIN_VALUE;
        int[] marks = {-reach, CityLayout.WELL / 2 - 1, CityLayout.WELL - 1 + reach};
        for (int dx : marks) {
            for (int dz : marks) {
                int found = BeardSurface.surfaceAt(world, wellX + dx, wellZ + dz);
                if (found < sea - 1) { return true; }
                lowest = Math.min(lowest, found);
                highest = Math.max(highest, found);
            }
        }
        return highest - lowest > RELIEF;
    }
}
